package adobeserver.handlers;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import adobeserver.http.HttpRequest;
import adobeserver.http.HttpResponse;
import adobeserver.http.HttpResponseBuilder;
import adobeserver.server.ServerConfig;

/**
 *  Server configuration handler - returns {@link ServerConfig} serialization
 * 
 * 	@author Razvan Manolescu
 */
public class ServerConfigHandler implements Handler {

    final static Logger LOG = Logger.getLogger(ServerConfigHandler.class.getSimpleName());
	final ServerConfig config;
	
	public ServerConfigHandler(final ServerConfig config) {
		this.config = config;
	}
	
	@Override
	public HttpResponse handle(final HttpRequest request) {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			return HttpResponseBuilder.newInstance().payload(mapper.writeValueAsString(config)).payloadFileExtension("json").build();
		} catch (JsonProcessingException e) {
			LOG.error("Could not serialize config");
			return HttpResponseBuilder.newInstance().setverError().build();
		}
	}

}
