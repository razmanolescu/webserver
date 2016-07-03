package adobeserver.security;

import junit.framework.TestCase;
import adobeserver.security.SecurityManager;


/**
 * Test security for static file handler
 * 
 * 	@author Razvan Manolescu
 */
public class SecurityManagerTest extends TestCase {

	public void testDotSlash() {
		final SecurityManager manager = SecurityManager.getInstance();
		assertFalse(manager.isValidPath("./"));
		assertFalse(manager.isValidPath("../path"));
		assertFalse(manager.isValidPath("%2e%2F"));
		assertFalse(manager.isValidPath("%2E%2F"));
		assertFalse(manager.isValidPath("%u002e/test"));
		assertFalse(manager.isValidPath("%u002e%u2215test"));
		assertTrue(manager.isValidPath("/normal/path/to/file/"));
		assertTrue(manager.isValidPath("/normal/path/to/specific/file.xml"));
	}
	
}
