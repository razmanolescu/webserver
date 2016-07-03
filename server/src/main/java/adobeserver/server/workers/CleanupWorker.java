package adobeserver.server.workers;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import org.apache.log4j.Logger;

import adobeserver.util.ServerConstants;


/**
 *  Idle socket cleanup worker thread. Closes socket connections with a lifetime grater or equal to {@code socketTimeoutMs}
 * 
 * 	@author Razvan Manolescu
 */
public class CleanupWorker implements Runnable {

	final Logger LOG = Logger.getLogger(CleanupWorker.class.getSimpleName());
	final Selector selector;
	final long socketTimeoutMs;

	public CleanupWorker(final Selector selector) {
		this(selector, ServerConstants.DEFAULT_SOCKET_TIMEOUT_MS);
	}

	public CleanupWorker(final Selector selector, final long timeoutMs) {
		this.selector = selector;
		this.socketTimeoutMs = timeoutMs;
	}

	public void run() {
		LOG.debug("Starting idle channel cleanup for " + selector.keys().size() + " keys");

		for (SelectionKey key : selector.keys()) { 
			
			if ((key.interestOps() & SelectionKey.OP_READ) != 0 && key.attachment() != null
					&& (System.currentTimeMillis() - (Long)key.attachment()) >= socketTimeoutMs) {	
				
				LOG.debug("Closing connection to " + key.channel());
				try {
					key.channel().close();
				} catch (IOException e) {
					LOG.error("Could not close channel " + key.channel());
				}
			}
		}

	}

}
