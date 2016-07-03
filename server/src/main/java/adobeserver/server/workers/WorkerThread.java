package adobeserver.server.workers;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Optional;
import java.util.Queue;

import org.apache.log4j.Logger;

import adobeserver.handlers.HandlerFactory;
import adobeserver.http.HttpRequest;
import adobeserver.util.HttpIOProcessor;

/**
 *  Main server worker thread - processes requests from the wire and dispatches to handlers
 * 
 * 	@author Razvan Manolescu
 */
public class WorkerThread implements Runnable {
	final Logger LOG = Logger.getLogger(WorkerThread.class.getSimpleName());
	
    private SelectionKey key;
    private Optional<Queue<SocketChannel>> keyQueue = Optional.empty();
    private HandlerFactory handlerFactory;
    private HttpIOProcessor httpProcessor;
    
    public WorkerThread(final SelectionKey key, final HandlerFactory handlerFactory, final HttpIOProcessor httpProcessor) {
    	this(key, null, handlerFactory, httpProcessor);
    }
    
    public WorkerThread(final SelectionKey key, final Queue<SocketChannel> keyQueue, 
    		final HandlerFactory handlerFactory, final HttpIOProcessor httpProcessor) {
        this.key = key;
        this.keyQueue = Optional.ofNullable(keyQueue);
        this.handlerFactory = handlerFactory;
        this.httpProcessor = httpProcessor;
    }

    public void run() {
        LOG.info("Thread " + Thread.currentThread().getName() + " running");
        
        try {
            processWork();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Gets request(s) from channel and processes responses for each. 
     * 
     * @throws Exception
     */
    private void processWork() throws Exception {
        
        final SocketChannel channel = (SocketChannel) key.channel();

        for(String requestString : httpProcessor.readRequest(channel)) {
	        final HttpRequest request = new HttpRequest(requestString);
	        httpProcessor.sendResponse(channel, handlerFactory.getHandler(request.getPath()).handle(request), request.isKeepAlive());
	       
	        if(request.isKeepAlive() && keyQueue.isPresent()) {
	        	LOG.debug("Request is keep-alive");
	        	channel.socket().setKeepAlive(true);
	        	keyQueue.get().add((SocketChannel) key.channel());
	        }
	        else 
	        	channel.close();
        }
            
    }
    

}