package org.web.app;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * This class helps in reading the configurations of the application
 * 
 * @author mskonline
 */

@Component
public class AppConfig {

	final static Logger logger = Logger.getLogger(AppConfig.class);

	private XMLConfiguration config;

	public AppConfig() {
		try {
			config = new XMLConfiguration("appconfig.xml");
		} catch (ConfigurationException cex) {
			logger.error("Error loading the configurations" + cex.getMessage());
		}
	}

	/**
	 * Get the value for a key
	 * 
	 * @param key The key
	 * @return The value of the key
	 */
	public String get(String key) {
		if (config == null) {
			return null;
		}

		return config.getString(key);
	}
}
