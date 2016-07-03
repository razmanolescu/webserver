package adobeserver.http;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;


/**
 *  HTTP resonse class
 * 
 * 	@author Razvan Manolescu
 */
public class HttpResponse {

	private String DEFAULT_VERSION = "HTTP/1.1";
	private String DEFAULT_CONTENT_TYPE = "text/plain";
    private String version = DEFAULT_VERSION;
    private String contentType = DEFAULT_CONTENT_TYPE;
    private Map<String, String> headers = new LinkedHashMap<String, String>();
    private byte[] payload;
    private final HttpStatus status;
    
    public HttpResponse(final HttpStatus status, final Optional<String> contentType, final Optional<byte[]> payload, 
    		final Optional<Map<String, String>> headers , final Optional<String> version, final boolean isKeepAlive) {
    	this.status = status;
    	
    	if(payload.isPresent()) {
    		this.payload = payload.get();
    	}
    	
    	if(headers.isPresent()) {
    		headers.get().putAll(this.headers);
    	}
    	
    	if(version.isPresent()) {
    		this.version = version.get();
    	}
    	
    	if(contentType.isPresent()) {
    		this.contentType = contentType.get();
    	}
    	
    	addDefaultHeaders(isKeepAlive);
    }
    
    private void addDefaultHeaders(final boolean isKeepAlive) {
        headers.put("Date", new Date().toString());
        headers.put("Server", "Adobe server");
        headers.put("Host", "www.adobeserver.com");
        headers.put("Content-Type", "text");
        headers.put("Access-Control-Allow-Origin", null);
        headers.put("Content-Type", contentType);
        headers.put("Content-Length", payload == null ? "0" : Integer.toString(payload.length));
        if(!isKeepAlive && version.contains("1.1")) {
            headers.put("Connection", "close");
        }
    }

    public HttpStatus getStatus() {
    	return status;
    }

    public String getHeader(String header) {
        return headers.get(header);
    }

    public byte[] getPayload() {
        return payload;
    }

	public String getVersion() {
		return version;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}
}