/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright Headease B.V. (c) 2020.
 */

package nl.gidsopenstandaarden.hti.testsuite.module.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.jose4j.jwk.HttpsJwks;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Service
public class JwksDiscoveryService {

	private ObjectMapper objectMapper;

	public Key discoverPublicKeyWithJwks(String token) {
		try {
			String jwksUrl = getJwksUrl(token);
			String kid = getKid(token);
			if (StringUtils.isNotEmpty(kid) && StringUtils.isNotEmpty(jwksUrl)) {
				JsonWebKey found = getJsonWebKeyByKid(jwksUrl, kid);

				if (found != null) {
					return found.getKey();
				}

			}
		} catch (JoseException | IOException e) {
			return null;
		}
		return null;
	}

	public JsonWebKey getJsonWebKeyByKid(String jwksUrl, String kid) throws JoseException, IOException {
		HttpsJwks httpsJkws = new HttpsJwks(jwksUrl);
		final List<JsonWebKey> jsonWebKeys = httpsJkws.getJsonWebKeys();
		JsonWebKey found = null;
		for (JsonWebKey jsonWebKey : jsonWebKeys) {
			if (StringUtils.equals(jsonWebKey.getKeyId(), kid)) {
				found = jsonWebKey;
			}
		}
		return found;
	}

	public String getJwksUrl(String token) {
		String issuer = getIssuer(token);
		String jwksUrl = "";
		if (StringUtils.isNotEmpty(issuer)) {
			String domain = issuer;
			if (!StringUtils.startsWith(domain, "http")) {
				domain = "https://" + domain;
			}
			if (!StringUtils.endsWith(domain, "/")) {
				domain = domain + "/";
			}
			jwksUrl = domain + ".well-known/jwks.json";
		}
		return jwksUrl;
	}

	@Autowired
	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@SuppressWarnings("unchecked")
	private String getIssuer(String token) {
		String rv = "";
		if (StringUtils.countMatches(token, '.') == 2) {
			final String[] strings = StringUtils.split(token, '.');
			String payload = strings[1];
			payload = new String(Base64.getDecoder().decode(payload), StandardCharsets.US_ASCII);
			final Map<String, Object> map;
			try {
				map = objectMapper.readValue(payload, Map.class);
				if (map.get("iss") instanceof String) {
					rv = (String) map.get("iss");
				}
			} catch (JsonProcessingException e) {
				// Ignore
			}
		}
		return rv;
	}

	@SuppressWarnings("unchecked")
	public String getKid(String token) throws com.fasterxml.jackson.core.JsonProcessingException {
		String rv = "";
		if (StringUtils.countMatches(token, '.') == 2) {
			final String[] strings = StringUtils.split(token, '.');
			String header = strings[0];
			header = new String(Base64.getDecoder().decode(header), StandardCharsets.US_ASCII);
			final Map<String, Object> map = objectMapper.readValue(header, Map.class);
			if (map.get("kid") instanceof String) {
				rv = (String) map.get("kid");
			}
		}
		return rv;
	}
}
