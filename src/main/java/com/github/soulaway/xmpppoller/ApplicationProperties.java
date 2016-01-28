package com.github.soulaway.xmpppoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

@Configuration
@PropertySources({ @PropertySource(value = "properties/application.properties", ignoreResourceNotFound = true) })
public class ApplicationProperties {

	@Autowired
	private Environment env;

	public String getProperty(String propName) {
		return env.getProperty(propName);
	}
	public boolean getBProperty(String propName) {
		return Boolean.parseBoolean(env.getProperty(propName));
	}
	public int getIProperty(String propName) {
		return Integer.parseInt(env.getProperty(propName));
	}
}
