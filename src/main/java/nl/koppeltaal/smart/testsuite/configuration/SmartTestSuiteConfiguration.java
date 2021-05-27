/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright Headease B.V. (c) 2020.
 */

package nl.koppeltaal.smart.testsuite.configuration;

import nl.koppeltaal.smart.testsuite.utils.KeyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

/**
 *
 */
@Configuration
@ConfigurationProperties(prefix = "smart.testsuite")
public class SmartTestSuiteConfiguration {
	private final static Log LOG = LogFactory.getLog(SmartTestSuiteConfiguration.class);
	/**
	 * Used to publish to the validating parties.
	 */
	String signingPublicKey;
	/**
	 * Used to sign the JWT message.
	 */
	String signingPrivateKey;
	/**
	 * Algorithm used to sign the JWT message.
	 */
	String signingAlgorithm;
	private String aud;
	private String iss;
	/**
	 * JWT token timeout.
	 */
	private int jwtTimeoutInSeconds = 5 * 60; // 5 minutes

	public String getAud() {
		return aud;
	}

	public void setAud(String aud) {
		this.aud = aud;
	}

	public String getIss() {
		return iss;
	}

	public void setIss(String iss) {
		this.iss = iss;
	}

	public int getJwtTimeoutInSeconds() {
		return jwtTimeoutInSeconds;
	}

	public void setJwtTimeoutInSeconds(int jwtTimeoutInSeconds) {
		this.jwtTimeoutInSeconds = jwtTimeoutInSeconds;
	}

	public String getSigningAlgorithm() {
		return signingAlgorithm;
	}

	public void setSigningAlgorithm(String signingAlgorithm) {
		this.signingAlgorithm = signingAlgorithm;
	}

	public String getSigningPrivateKey() {
		return signingPrivateKey;
	}

	public void setSigningPrivateKey(String signingPrivateKey) {
		this.signingPrivateKey = signingPrivateKey;
	}

	public String getSigningPublicKey() {
		return signingPublicKey;
	}

	public void setSigningPublicKey(String signingPublicKey) {
		this.signingPublicKey = signingPublicKey;
	}

	@PostConstruct
	public void init() throws NoSuchAlgorithmException {
		if (StringUtils.isEmpty(signingPublicKey) || StringUtils.isEmpty(signingPrivateKey)) {
			LOG.info("Signing public and/or private key not configured, generating a temporary key pair");
			final KeyPair keyPair = KeyUtils.generateKeyPair();
			signingPublicKey = KeyUtils.encodeKey(keyPair.getPublic());
			signingPrivateKey = KeyUtils.encodeKey(keyPair.getPrivate());
		}
	}


}
