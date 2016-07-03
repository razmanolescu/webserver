package adobeserver.util;

/**
 *  HTTP utilites class
 * 
 * 	@author Razvan Manolescu
 */
public class HttpUtils {
	
    public static String getContentTypeByExtension(final String extension) {
    	switch(extension) {
    		case "txt" : return "text/plain";
    		case "json" : return "application/json";
    		case "xml" : return "application/xml";
    		case "html" : return "text/html";
    	}
    	
    	return null;
    }
    
}

