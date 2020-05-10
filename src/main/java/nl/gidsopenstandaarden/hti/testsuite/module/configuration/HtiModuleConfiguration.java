package nl.gidsopenstandaarden.hti.testsuite.module.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@Configuration
@ConfigurationProperties(prefix = "hti.module")
public class HtiModuleConfiguration {
	public String aud;

	public String getAud() {
		return aud;
	}

	public void setAud(String aud) {
		this.aud = aud;
	}
}
