package org.web.app;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

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

	public String get(String key) {
		if (config == null)
			return null;

		return config.getString(key);
	}
}
