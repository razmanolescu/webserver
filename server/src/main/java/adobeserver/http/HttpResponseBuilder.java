package adobeserver.http;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import adobeserver.util.HttpUtils;


/**
 *  Builder class for {@link HttpRequest}
 * 
 * 	@author Razvan Manolescu
 */
public class HttpResponseBuilder {

	private static final HttpStatus DEFAULT_STATUS = HttpStatus.OK;
	private boolean keepAlive = true;
	private HttpStatus httpStatus = DEFAULT_STATUS;
	private byte[] payload;
	private String version = null;
	private String contentType = null;
	private Map<String, String> headers;
	
	public static HttpResponseBuilder newInstance() {
		return new HttpResponseBuilder();
	}
	
	public HttpResponseBuilder keepAlive(final boolean keepAlive) {
		this.keepAlive = keepAlive;
		return this;
	}
	
	public HttpResponseBuilder payload(final String payload) {
		return payload(payload.getBytes());
	}
	
	public HttpResponseBuilder payload(final byte[] payload) {
		this.payload = payload;
		return this;
	}
	
	public HttpResponseBuilder version(final String version) {
		this.version = version;
		return this;
	}
	
	public HttpResponseBuilder ok() {
		return status(HttpStatus.OK);
	}
	
	public HttpResponseBuilder notFound() {
		return status(HttpStatus.NOT_FOUND);
	}
	
	public HttpResponseBuilder forbidden() {
		return status(HttpStatus.FORBIDDEN);
	}
	
	public HttpResponseBuilder setverError() {
		return status(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	public HttpResponseBuilder status(final HttpStatus status) {
		this.httpStatus = status;
		return this;
	}
	
	public HttpResponseBuilder contentType(final String contentType) {
		this.contentType = contentType;
		return this;
	}
	
	public HttpResponseBuilder payloadFileExtension(final String extension) {
		return contentType(HttpUtils.getContentTypeByExtension(extension));
	}
	
	private Map<String, String> getCreateHeaders() {
		if(headers == null) {
			headers = new LinkedHashMap<>();
		}
		
		return headers;
	}
	
	public HttpResponseBuilder headers(final Map<String, String> headerMap) {
		final Map<String, String> headers = getCreateHeaders();
		headers.putAll(headerMap);
		
		return this;
	}
	
	public HttpResponseBuilder header(final String name, final String value) {
		final Map<String, String> headers = getCreateHeaders();
		headers.put(name, value);
		return this;
	}
	
	public HttpResponse build() {
		return new HttpResponse(httpStatus, Optional.ofNullable(contentType), Optional.ofNullable(payload), 
				Optional.ofNullable(headers), Optional.ofNullable(version), keepAlive);
	}
}
