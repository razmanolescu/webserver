package adobeserver.server;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.Capture;
import org.easymock.EasyMock;

import adobeserver.handlers.HandlerFactory;
import adobeserver.http.HttpResponse;
import adobeserver.http.HttpStatus;
import adobeserver.server.workers.WorkerThread;
import adobeserver.util.HttpIOProcessor;
import adobeserver.util.PropertyReader;


/**
 * Test class for Server worker. Tests responses and processing of requests
 * 
 * 	@author Razvan Manolescu
 */
public class WorkerTheadTest extends TestCase {

	private HttpResponse makeMockRequest(final String path) throws Exception {
		
		// Mock server configuration
		final PropertyReader reader = createMock(PropertyReader.class);
		expect(reader.getRequiredProperty("server.port")).andReturn("10093");
		expect(reader.getRequiredProperty("server.basePath")).andReturn("test");
		expect(reader.getRequiredProperty("server.numServiceThreads")).andReturn("10");
		replay(reader);
		
		final ServerConfig config = new ServerConfig(reader);
		final HandlerFactory handlerFactory = new HandlerFactory(config);
		final SelectionKey key = createMock(SelectionKey.class);
        final SocketChannel channel = EasyMock.createNiceMock(SocketChannel.class); 
		expect(key.channel()).andReturn(channel).anyTimes();
		
		final Capture<HttpResponse> capture = new Capture<HttpResponse>();
		
		// Fake IO 
		final HttpIOProcessor processor = createMock(HttpIOProcessor.class);
		final List<String> procStr = new ArrayList<String>(1);
		procStr.add("GET\t/" + path + "\nHTTP/1.1\nHost: localhost:10093\nUser-Agent: curl/7.49.1\nAccept: */*");
		expect(processor.readRequest(EasyMock.anyObject(SocketChannel.class))).andReturn(procStr);
		processor.sendResponse(EasyMock.anyObject(SocketChannel.class), EasyMock.capture(capture), EasyMock.anyBoolean());
		EasyMock.expectLastCall();
		replay(key, channel, processor);
	
		final WorkerThread thread = new WorkerThread(key, handlerFactory, processor);
		try {
			thread.run();
		} catch(Throwable t) {
			
		}
		
		final HttpResponse response = capture.getValue();
		
		return response;
	}
	
	public void testWorkerSampleURL() throws Exception {

		final HttpResponse response = makeMockRequest("sample");
		assertNotNull(response);
		assertEquals("HTTP/1.1", response.getVersion());
		assertEquals(HttpStatus.OK, response.getStatus());
		assertEquals("Sample text should be as expected", "Sample text", new String(response.getPayload()));
	}
	
	public void testWorkerInvalidURL() throws Exception {

		final HttpResponse response = makeMockRequest("url/that/does/not/exist");
		assertNotNull(response);
		assertEquals("HTTP/1.1", response.getVersion());
		assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
	}
	
	public void testWorkerSecurityViolationURL() throws Exception {

		final HttpResponse response = makeMockRequest("../sneaky");
		assertNotNull(response);
		assertEquals("HTTP/1.1", response.getVersion());
		assertEquals(HttpStatus.FORBIDDEN, response.getStatus());
	}

	public void testWorkerConfig() throws Exception {

		final HttpResponse response = makeMockRequest("config");
		assertNotNull(response);
		assertEquals("HTTP/1.1", response.getVersion());
		assertEquals(HttpStatus.OK, response.getStatus());
		assertTrue("Port information should be contained in payload", new String(response.getPayload()).contains("port"));
	}
	
}
