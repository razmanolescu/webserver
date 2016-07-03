package adobeserver.handlers;

import adobeserver.http.HttpRequest;
import adobeserver.http.HttpResponse;

/**
 *  Handler inteface
 * 
 * 	@author Razvan Manolescu
 */
public interface Handler {
	
    public HttpResponse handle(final HttpRequest request);
}
