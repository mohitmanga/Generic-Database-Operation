package com.generic.util;

import java.util.Properties;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public final class CustomPropertyPlaceholder extends PropertyPlaceholderConfigurer implements InitializingBean {

	/** Used to store the properties as name value pairs */
	private static Properties properties = new Properties();

	public void afterPropertiesSet() throws Exception {
		this.loadProperties(properties);
	}

	public CustomPropertyPlaceholder() {}
	
	/**
	 * This method returns the value of the given property
	 * @param name The name of the property
	 * @return Returns the configured property.
	 */
	public String getProperty(String name){
		return properties.getProperty(name);
	}
}

