package adobeserver.http;

import junit.framework.TestCase;

import org.apache.commons.io.FilenameUtils;


/**
 * HTTP response class test
 * 
 * 	@author Razvan Manolescu
 */
public class HttpResponseTest extends TestCase {

	public void testNormalResponse() {
		
		final String payload = "test";
		final HttpResponse response =  HttpResponseBuilder.newInstance().payload(payload)
				.payloadFileExtension(FilenameUtils.getExtension("test.xml")).build();
		
		assertEquals("200 should be default status code", HttpStatus.OK, response.getStatus());
		assertTrue(response.getHeaders().get("Content-Type").contains("xml"));
		assertEquals("Content length should be correctly injected", 
				Integer.parseInt(response.getHeaders().get("Content-Length")), payload.length());
	}
	
	public void testErrorResponse() {
		
		final HttpResponse response =  HttpResponseBuilder.newInstance()
				.payloadFileExtension(FilenameUtils.getExtension("test.json")).setverError().build();
		
		assertEquals("Status code should be 500", HttpStatus.INTERNAL_SERVER_ERROR, response.getStatus());
		assertTrue(response.getHeaders().get("Content-Type").contains("json"));
		assertEquals("Content length should be correctly injected", 0,
				Integer.parseInt(response.getHeaders().get("Content-Length")));
	}
	
	public void testNonKeepAlive() {
		
		final HttpResponse response =  HttpResponseBuilder.newInstance().keepAlive(false).build();
		
		assertNotNull(response.getHeaders().get("Connection"));
		assertEquals("Content length should be correctly injected", 
				"close", response.getHeaders().get("Connection"));
	}
}
