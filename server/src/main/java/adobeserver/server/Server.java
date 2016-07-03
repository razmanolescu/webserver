package adobeserver.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import adobeserver.handlers.HandlerFactory;
import adobeserver.server.workers.CleanupWorker;
import adobeserver.server.workers.DispachWorkerFactory;
import adobeserver.server.workers.WorkerThread;
import adobeserver.util.HttpIOProcessor;
import adobeserver.util.ServerConstants;

/**
 *  HTTP Server class
 * 
 * 	@author Razvan Manolescu
 */
public class Server {

    final Logger LOG = Logger.getLogger(Server.class.getSimpleName());
	
    private Selector selector;
    private ServerSocketChannel serverChannel;
    private LifeCycle lifeCyclePhase = LifeCycle.STOPPED;
    
    // Set thread pool name - thread factory
    final ExecutorService executor;
    final Queue<SocketChannel> reusedChannelQueue;
    final HandlerFactory handlerFactory;
    final HttpIOProcessor processor;
    
    public Server(final ServerConfig config) {
    	changePhase(LifeCycle.STARTED);
    	reusedChannelQueue = new ConcurrentLinkedQueue<SocketChannel>();
    	handlerFactory = new HandlerFactory(config);
    	processor = new HttpIOProcessor();
    	
    	
	    if(config.getNumServiceThreads().isPresent() && config.getNumServiceThreads().get() > 0) {
	    	executor = Executors.newFixedThreadPool(config.getNumServiceThreads().get(), new DispachWorkerFactory()); 
	    } else {
	    	executor = Executors.newCachedThreadPool(new DispachWorkerFactory()); 
	    }
    	
    	try {
    		selector = Selector.open();
			serverChannel = ServerSocketChannel.open();
		    serverChannel.socket().bind(new InetSocketAddress(config.getPort()));
		    serverChannel.configureBlocking(false);
		    serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (Exception e) {
			LOG.error(e.getMessage());	
		}
    	
    	configureConnectionCleanupPool();
    }
    
    
    private synchronized void changePhase(final LifeCycle newPhase) {
    	LOG.info(String.format("Server stage changed from [%s] to [%s]", lifeCyclePhase, newPhase));
    	this.lifeCyclePhase = newPhase;
    }
    
    /**
     * Get current server lifecycle phase
     * 
     * @return lifeCyclePhase 
     */
    public LifeCycle getLifeCyclePhase() {
    	return this.lifeCyclePhase;
    }
    
    private void configureConnectionCleanupPool() {
	    final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new DispachWorkerFactory("CleanupPool"));
	    scheduler.scheduleAtFixedRate(new CleanupWorker(selector), 0, 
	    		ServerConstants.DEFAULT_CLEANUP_INTERVAL_S, TimeUnit.SECONDS);
    }
    
    /**
     * Shutdown this server, preventing it from handling any more requests.
     */
    public final void shutdown() {
    	changePhase(LifeCycle.STOPPING);
        try {
            selector.close();
            executor.shutdownNow();
            serverChannel.close();
        } catch (IOException ex) {
        	LOG.error("Could not shudown server" + ex);
        }
        
        changePhase(LifeCycle.STOPPED);
    }
    
    /**
     * Polls {@link #reusedChannelQueue} until there is an open connection. Re-registers read interest,
     * and attaches current timestamp
     * 
     * @param selector
     * @throws IOException
     */
    private void processReuseQueue(final Selector selector) throws IOException {
    	if(reusedChannelQueue.isEmpty()) {
    		return;
    	}
    	
        SocketChannel channel;
        int count = 0;
        while ((channel = reusedChannelQueue.poll()) != null) {
                if (channel != null) {
                	count++;
                    channel.configureBlocking(false);
                    channel.register(selector, SelectionKey.OP_READ, System.currentTimeMillis());
                }
        }
        
        LOG.debug("Adding " + count +" channels for reuse");
    }
    
    /**
     * Core run method. This is not a thread safe method, however it is non
     * blocking. If an exception is encountered it will be thrown wrapped in a
     * RuntimeException, and the server will automatically be {@link #shutDown}
     */
    public void start() {
    	changePhase(LifeCycle.RUNNING);
    	
        while(lifeCyclePhase == LifeCycle.RUNNING) {
            try {
                
                processReuseQueue(selector);
                selector.selectNow();
           
                final Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                if(iter.hasNext())
                    LOG.debug("Have " + selector.selectedKeys().size() + " channels");
                
                while (iter.hasNext()) {

                    final SelectionKey key = iter.next();
                    if (!key.isValid()) {
                        continue;
                    }
                    try {
                    	// First, accept connection
                        if (key.isAcceptable()) {
                            final SocketChannel client = serverChannel.accept(); 
                            LOG.debug("Accepted new connection " + client);
                            client.configureBlocking(false);
                            client.register(selector, SelectionKey.OP_READ);
                            
                        // Secondly, read after registering interest with selector
                        } else if (key.isReadable()) {
                        	if(key.attachment() != null) {
                        		LOG.info("Reusing channelObject#" + key.channel().hashCode() +" "+(Long)key.attachment());
                        	}
                        	
                        	// Cancel key, will re-register later if needed. Go into blocking mode for worker thread.
                        	key.cancel();
                        	key.channel().configureBlocking(true);
                            executor.submit(new WorkerThread(key, reusedChannelQueue, handlerFactory, processor));
                        }
                        
                        iter.remove();
                    } catch (Exception ex) {
                    	LOG.error("Error handling channel" + key.channel());
                        key.cancel();
                        key.channel().close();
                        LOG.error("Error in game loop " + ex);
                        
                    }
                }
                
                selector.selectNow();

            } catch (IOException ex) {
            	LOG.fatal("Game loop interruped. Server stopping. ", ex);
                shutdown();
                throw new RuntimeException(ex);
            }
        }
    }
}
