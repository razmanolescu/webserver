package adobeserver.security;

import java.util.regex.Pattern;

/**
 *  Server security manager
 * 
 * 	@author Razvan Manolescu
 */
public class SecurityManager {

	private static SecurityManager manager;
	
	final Pattern dotSlashPattern = Pattern.compile(".*(\\.|%2e|%u002e|%2E)+(\\/|%2F|%2f|%u2215).*");

	
	private SecurityManager() {
		
	}
	
	public static SecurityManager getInstance() {
		if(manager == null) {
			synchronized (SecurityManager.class) {
				if(manager == null) {
					manager = new SecurityManager();
				}
			}
		}
		return manager;
	}
	
	
	public boolean isValidPath(final String path) {
		return !dotSlashPattern.matcher(path).matches();
	}
}

