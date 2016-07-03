package adobeserver.handlers;

import adobeserver.http.HttpRequest;
import adobeserver.http.HttpResponse;
import adobeserver.http.HttpResponseBuilder;
import adobeserver.server.ServerConfig;

/**
 *  Sample handler - returns static test. Used for testing purposes
 * 
 * 	@author Razvan Manolescu
 */
public class SampleHandler implements Handler{
	
	public SampleHandler(final ServerConfig config) {
		
	}

    public HttpResponse handle(final HttpRequest request) {
        return HttpResponseBuilder.newInstance().payload("Sample text".getBytes()).build();
    }
}
