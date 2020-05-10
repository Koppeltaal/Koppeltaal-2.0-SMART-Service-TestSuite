package nl.gidsopenstandaarden.hti.testsuite.portal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.gidsopenstandaarden.hti.testsuite.portal.configuration.HtiPortalConfiguration;
import nl.gidsopenstandaarden.hti.testsuite.portal.utils.KeyUtils;
import nl.gidsopenstandaarden.hti.testsuite.portal.valueobject.HtiPortalLaunchRequest;
import nl.gidsopenstandaarden.hti.testsuite.portal.valueobject.Task;
import nl.gidsopenstandaarden.hti.testsuite.shared.HostIdentityService;
import org.apache.commons.lang3.StringUtils;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwk.PublicJsonWebKey;
import org.jose4j.jwk.Use;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.UUID;

/**
 *
 */
@Service
public class HtiPortalService {

	private HtiPortalConfiguration htiPortalConfiguration;

	private ObjectMapper objectMapper;
	private HostIdentityService hostIdentityService;

	public String getLaunchToken(HtiPortalLaunchRequest request) throws GeneralSecurityException {
		Task task = buildTask(request);
		return getLaunchToken(task, request);
	}

	public HtiPortalLaunchRequest getPrototype(HttpServletRequest httpServletRequest) {
		final HtiPortalLaunchRequest request = new HtiPortalLaunchRequest();
		request.setTaskId(getSortOfRandomIdentifier());
		request.setActivityId(getSortOfRandomIdentifier());

		request.setUserType("Person");
		request.setUserId(getSortOfRandomIdentifier());

		request.setIntent("plan");
		request.setStatus("requested");

		request.setAud(htiPortalConfiguration.getAud());
		final String iss = htiPortalConfiguration.getIss();
		if (StringUtils.isNotEmpty(iss)) {
			request.setIss(iss);
		} else {
			request.setIss(hostIdentityService.getOwnHostIdentity(httpServletRequest));
		}

		request.setLaunchUrl(htiPortalConfiguration.getModuleLaunchUrl());
		request.setPublicKey(htiPortalConfiguration.getSigningPublicKey());

//		request.setSigningPublicKey(htiRequestConfiguration.getDefaultSigningPublicKey());
//		request.setSigningPrivateKey(htiRequestConfiguration.getDefaultSigningPrivateKey());

		return request;
	}

	@Autowired
	public void setHostIdentityService(HostIdentityService hostIdentityService) {
		this.hostIdentityService = hostIdentityService;
	}

	@Autowired
	public void setHtiPortalConfiguration(HtiPortalConfiguration htiPortalConfiguration) {
		this.htiPortalConfiguration = htiPortalConfiguration;
	}

	@Autowired
	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	private String buildJweWrapping(String payload, String jwePublicKey) throws NoSuchAlgorithmException, InvalidKeySpecException, JoseException {
		JsonWebEncryption jwe = new JsonWebEncryption();
		jwe.setAlgorithmHeaderValue("RSA-OAEP");
		jwe.setEncryptionMethodHeaderParameter("A128CBC-HS256");
		jwe.setKey(KeyUtils.getRsaPublicKey(jwePublicKey));
		jwe.setContentTypeHeaderValue("JWT");
		jwe.setPayload(payload);
		return jwe.getCompactSerialization();
	}

	private Task buildTask(HtiPortalLaunchRequest request) {
		Task task = new Task();
		// Id
		task.setId(request.getTaskId());

		// For
		final Task.User user = new Task.User();
		user.setReference(request.getUserType() + "/" + request.getUserId());
		task.setForUser(user);

		task.setIntent(request.getIntent());
		task.setStatus(request.getStatus());

		task.getDefinitionReference().setReference("ActivityDefinition/" + request.getActivityId());

		return task;
	}

	protected String getLaunchToken(Task task, HtiPortalLaunchRequest request) throws GeneralSecurityException {
		try {
			JwtClaims claims = new JwtClaims();
			claims.setClaim("task", toMap(task));
			claims.setIssuedAt(NumericDate.now());
			claims.setAudience(request.getAud());
			claims.setIssuer(request.getIss());
			claims.setExpirationTime(NumericDate.fromMilliseconds(System.currentTimeMillis() + htiPortalConfiguration.getJwtTimeoutInSeconds() * 1000));
			claims.setJwtId(UUID.randomUUID().toString());

			JsonWebSignature jws = new JsonWebSignature();

			// The payload of the JWS is JSON content of the JWT Claims
			jws.setPayload(claims.toJson());

			KeyPair rsaKeyPair = KeyUtils.getRsaKeyPair(htiPortalConfiguration.getSigningPublicKey(), htiPortalConfiguration.getSigningPrivateKey());
			PublicJsonWebKey jwk = PublicJsonWebKey.Factory.newPublicJwk(rsaKeyPair.getPublic());
			jwk.setPrivateKey(rsaKeyPair.getPrivate());
			jwk.setUse(Use.SIGNATURE);
			jwk.setAlgorithm(htiPortalConfiguration.getSigningAlgorithm());

			// The JWT is signed using the private key
			jws.setKey(jwk.getPrivateKey());

			// Set the Key ID (kid) header because it's just the polite thing to do.
			// We only have one key in this example but a using a Key ID helps
			// facilitate a smooth key rollover process
			jws.setKeyIdHeaderValue(KeyUtils.getFingerPrint(rsaKeyPair.getPublic()));

			// Set the signature algorithm on the JWT/JWS that will integrity protect the claims
			jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA512);

			final String payload = jws.getCompactSerialization();
			return (request.isUseJwe() ? buildJweWrapping(payload, request.getJwePublicKey()) : payload);
		} catch (JoseException e) {
			throw new GeneralSecurityException(e);
		}

	}

	private String getSortOfRandomIdentifier() {
		return UUID.randomUUID().toString().split("-")[0];
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> toMap(Task task) {
		return objectMapper.convertValue(task, Map.class);
	}
}
