package adobeserver.handlers;

import adobeserver.server.ServerConfig;
import adobeserver.util.PropertyReader;
import junit.framework.TestCase;
import static org.easymock.EasyMock.*;

/**
 * Handler factory test
 * 
 * 	@author Razvan Manolescu
 */
public class HandlerFactoryTest extends TestCase {
	
	public void testNormalResponse() throws Exception {
		
		final PropertyReader reader = createMock(PropertyReader.class);
		expect(reader.getRequiredProperty("server.port")).andReturn("10093");
		expect(reader.getRequiredProperty("server.basePath")).andReturn("test");
		expect(reader.getRequiredProperty("server.numServiceThreads")).andReturn("10");
		replay(reader);
		
		final ServerConfig config = new ServerConfig(reader);
		final HandlerFactory handlerFactory = new HandlerFactory(config);
		
		assertNotNull(handlerFactory.getHandler("/config"));
		assertTrue(handlerFactory.getHandler("/config") instanceof ServerConfigHandler);
		assertTrue(handlerFactory.getHandler("/sample") instanceof SampleHandler);
		assertTrue(handlerFactory.getHandler("/normal/path/to/file.xml") instanceof StaticFileHandler);	
		verify(reader);
	}
	
}
