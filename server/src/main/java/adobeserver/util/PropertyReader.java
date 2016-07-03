package adobeserver.util;

import java.io.FileInputStream;
import java.util.Optional;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 *  Wrapper over .property files reader
 * 
 * 	@author Razvan Manolescu
 */
public class PropertyReader {
	
    final Logger LOG = Logger.getLogger(PropertyReader.class.getSimpleName());
	
	private Properties properties;
	
	public PropertyReader(final String filePath) {
		this.properties = new Properties();
		try {
			properties.load(new FileInputStream(filePath));
		} catch (Exception e) {
			LOG.fatal("Could not load app properties", e);
		}
	}
	
	
	public String getProperty(final String propName) {
		return getProperty(propName, Optional.<String>empty());
	}
	
	public String getProperty(final String propName, final Optional<String> defaultValue) {
		String propValue = properties.getProperty(propName);
		if(propValue == null && defaultValue.isPresent()) {
			propValue = defaultValue.get();
		}
		
		return propValue;
	}

	/**
	 * Gets required property from configuration file
	 * 
	 * @param propName
	 * @return property value
	 * @throws Exception
	 */
	public String getRequiredProperty(final String propName) throws Exception {
		final String propValue = properties.getProperty(propName);
		if(propValue == null) {
			throw new Exception("Property not found");
		}
		
		return propValue;
	}
	
}
