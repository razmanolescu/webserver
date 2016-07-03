package adobeserver.server;

import java.util.Optional;

import adobeserver.util.PropertyReader;

/**
 *  Server-specific configurations read from property files
 * 
 * 	@author Razvan Manolescu
 */
public class ServerConfig {

	private Optional<Integer> numServiceThreads;
	private int port;
	private String fileBasePath;
	
	public ServerConfig(final PropertyReader reader) throws Exception {
		port = Integer.parseInt(reader.getRequiredProperty("server.port"));
		numServiceThreads = Optional.ofNullable(Integer.parseInt(reader.getRequiredProperty("server.numServiceThreads")));
		fileBasePath = reader.getRequiredProperty("server.basePath");
	}
	
	public Optional<Integer> getNumServiceThreads() {
		return numServiceThreads;
	}
	
	public int getPort() {
		return port;
	}
	
	public String getFileBasePath() {
		return fileBasePath;
	}
}
