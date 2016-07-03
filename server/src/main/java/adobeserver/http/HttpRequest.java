package adobeserver.http;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;


/**
 *  HTTP request class
 * 
 * 	@author Razvan Manolescu
 */
public class HttpRequest {
    
    private String method;
    private String path;
    private String version;
    private Map<String, String> headers = new HashMap<String, String>();

    public HttpRequest(final String requestString) {
        parse(requestString);
    }

    private void parse(final String requestString) {
        final StringTokenizer tokenizer = new StringTokenizer(requestString);
        method = tokenizer.nextToken().toUpperCase();
        path = tokenizer.nextToken();
        version = tokenizer.nextToken();
        version = version == null || version.isEmpty() || !version.contains("/") ? version : version.split("/")[1];
        
        final String[] lines = requestString.split("\r\n");
        for (int i = 1; i < lines.length; i++) {
            final String[] keyVal = lines[i].split(":", 2);
            headers.put(keyVal[0], keyVal[1]);
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHeader(final String key) {
        return headers.get(key);
    }
    
    /**
     * Whether connection should be kept alive, based on request information
     * 
     * @return
     */
    public boolean isKeepAlive() {
    	boolean shouldPersist = false;
    	boolean isClose = false, isKeepAlive = false;
    	final String conHeaderValue = headers.get("connection");
    	
    	if(conHeaderValue != null && !conHeaderValue.isEmpty()) {
    		isClose = conHeaderValue.contains("close");
    		isKeepAlive = conHeaderValue.contains("keep-alive");
    	}
    	
    	if("1.0".equals(version) && isKeepAlive) {
    		shouldPersist = true;
    	} else if("1.1".equals(version) && !isClose) {
    		shouldPersist = true;
    	}
    	
    	return shouldPersist;
    }
    
}
