package adobeserver.server.workers;

import java.util.concurrent.ThreadFactory;

/**
 *  Custom thread factory (mainly for custom naming)
 * 
 * 	@author Razvan Manolescu
 */
public class DispachWorkerFactory implements ThreadFactory {
	   private int tId = 0;
	   private String prefix;
	   private static final String DEFAULT_PREFIX = "dispachThreadPool";

	   public DispachWorkerFactory() {
		     this.prefix = DEFAULT_PREFIX;
	   }
	   
	   public DispachWorkerFactory(final String prefix) {
		   this.prefix = prefix;
	   }

	   public Thread newThread(final Runnable r) {
		   return new Thread(r, prefix + "-tId-" + tId++);
	   }
	}