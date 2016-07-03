package adobeserver.handlers;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import adobeserver.server.ServerConfig;


/**
 *  HTTP request handler factory
 * 
 * 	@author Razvan Manolescu
 */
public class HandlerFactory {

	private Map<Class<?>, Handler> handlerInstanceMap = new HashMap<>();
	private ServerConfig config;
	
    final Logger LOG = Logger.getLogger(HandlerFactory.class.getSimpleName());
	
	public HandlerFactory(final ServerConfig config) {
		this.config = config;
	}
	
	public Handler getHandler(final String path) {
		if(path.equalsIgnoreCase("/config")) {
			return getCreateHandler(ServerConfigHandler.class);
		} 
		
		if(path.equalsIgnoreCase("/sample")) {
			return getCreateHandler(SampleHandler.class);
		} 
		
		return getCreateHandler(StaticFileHandler.class);
	}
	
	private Handler getCreateHandler(final Class<?> handlerClass) {
		if(!handlerInstanceMap.containsKey(handlerClass)) {
			try {
				handlerInstanceMap.put(handlerClass, createHandler(handlerClass));
			} catch(Exception e) {
				LOG.fatal("Coud not instantiate class " + handlerClass.getSimpleName(), e);
			}
		}
		
		return handlerInstanceMap.get(handlerClass);
	}
	
	private Handler createHandler(final Class<?> handlerClass) throws Exception {
		final Constructor<?> constructor = handlerClass.getConstructor(ServerConfig.class);
		final Handler instance = (Handler) constructor.newInstance(config);
		return instance;
	}
}
