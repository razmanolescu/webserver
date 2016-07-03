package adobeserver.util;

/**
 *  Static global server configurations
 * 
 * 	@author Razvan Manolescu
 */
public class ServerConstants {

    public static final String LOGGING_CONFIG_FILE_PATH = "src/main/conf/log4j.properties";
    public static final String APP_CONFIG_FILE_PATH = "src/main/conf/config.properties";
    
    public static final Long DEFAULT_SOCKET_TIMEOUT_MS = 15L * 1000L;
    public static final Long DEFAULT_CLEANUP_INTERVAL_S = 5L;
    
    public static final int READ_BUFFER_SIZE_BYTES = 8 * 1024;
    public static final int MIN_MESSAGE_MEMORY_BYTES = 512;
}
