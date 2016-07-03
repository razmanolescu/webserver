package adobeserver.server;

import org.apache.log4j.PropertyConfigurator;

import adobeserver.util.PropertyReader;
import adobeserver.util.ServerConstants;
 
 
/**
 *  Main class
 * 
 * 	@author Razvan Manolescu
 */
public class App {
 
    public static void main(String[] args) throws Exception {
    	
        PropertyConfigurator.configure(ServerConstants.LOGGING_CONFIG_FILE_PATH);
        final Server server = new Server(new ServerConfig(new PropertyReader(ServerConstants.APP_CONFIG_FILE_PATH)));
        server.start();
    }  

}
