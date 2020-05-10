package nl.gidsopenstandaarden.hti.testsuite.portal.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSADecrypter;
import nl.gidsopenstandaarden.hti.testsuite.portal.configuration.HtiPortalConfiguration;
import nl.gidsopenstandaarden.hti.testsuite.portal.utils.KeyUtils;
import nl.gidsopenstandaarden.hti.testsuite.portal.valueobject.HtiPortalLaunchRequest;
import nl.gidsopenstandaarden.hti.testsuite.shared.HostIdentityService;
import org.junit.Before;
import org.junit.Test;
import org.junit.platform.commons.util.StringUtils;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class HtiPortalServiceTest {

	@Mock
	HtiPortalConfiguration htiPortalConfiguration;

	@Spy
	ObjectMapper objectMapper = new ObjectMapper();

	@Spy
	HostIdentityService hostIdentityService;

	@InjectMocks
	HtiPortalService htiPortalService;

	static KeyPair SIGNING_KEYPAIR;

	@Before
	public void initTests() throws NoSuchAlgorithmException {
		if (SIGNING_KEYPAIR == null) {
			SIGNING_KEYPAIR = KeyUtils.generateKeyPair();
		}
		when(htiPortalConfiguration.getIss()).thenReturn("https://issuer.example.com");
		when(htiPortalConfiguration.getAud()).thenReturn("https://audiemce.example.com");
		when(htiPortalConfiguration.getModuleLaunchUrl()).thenReturn("https://module.example.com/launch");

		when(htiPortalConfiguration.getSigningPublicKey()).thenReturn(KeyUtils.encodeKey(SIGNING_KEYPAIR.getPublic()));
		when(htiPortalConfiguration.getSigningPrivateKey()).thenReturn(KeyUtils.encodeKey(SIGNING_KEYPAIR.getPrivate()));
		when(htiPortalConfiguration.getSigningAlgorithm()).thenReturn("RSA512");
		when(htiPortalConfiguration.getJwtTimeoutInSeconds()).thenReturn(15 * 60);


		hostIdentityService = new HostIdentityService();
	}

	@Test
	public void testGetLaunchTokenNormal() throws GeneralSecurityException {
		final HtiPortalLaunchRequest prototype = htiPortalService.getPrototype(mock(HttpServletRequest.class));
		final String launchToken = htiPortalService.getLaunchToken(prototype);

		Algorithm algorithm = Algorithm.RSA512((RSAPublicKey) SIGNING_KEYPAIR.getPublic(), null);
		JWTVerifier verifier = JWT.require(algorithm)
				.withIssuer(htiPortalConfiguration.getIss())
				.build();

		verifier.verify(launchToken);
	}

	@Test
	public void testGetLaunchTokenJwe() throws GeneralSecurityException, ParseException, JOSEException {
		final HtiPortalLaunchRequest prototype = htiPortalService.getPrototype(mock(HttpServletRequest.class));
		prototype.setUseJwe(true);
		final KeyPair jweKeyPair = KeyUtils.generateKeyPair();
		prototype.setJwePublicKey(KeyUtils.encodeKey(jweKeyPair.getPublic()));
		final String launchToken = htiPortalService.getLaunchToken(prototype);

		JWEObject jweObject = JWEObject.parse(launchToken);

		jweObject.decrypt(new RSADecrypter(jweKeyPair.getPrivate()));

		Payload payload = jweObject.getPayload();
		String jwt = payload.toSignedJWT().getParsedString();

		Algorithm algorithm = Algorithm.RSA512((RSAPublicKey) SIGNING_KEYPAIR.getPublic(), null);
		JWTVerifier verifier = JWT.require(algorithm)
				.withIssuer(htiPortalConfiguration.getIss())
				.build();

		verifier.verify(jwt);

	}

	@Test(expected = GeneralSecurityException.class)
	public void testGetLaunchTokenException() throws GeneralSecurityException {
		final KeyPair keyPair = KeyUtils.generateKeyPair(512);
		when(htiPortalConfiguration.getSigningPublicKey()).thenReturn(KeyUtils.encodeKey(keyPair.getPublic()));
		when(htiPortalConfiguration.getSigningPrivateKey()).thenReturn(KeyUtils.encodeKey(keyPair.getPrivate()));

		final HtiPortalLaunchRequest prototype = htiPortalService.getPrototype(mock(HttpServletRequest.class));
		final String jwt = htiPortalService.getLaunchToken(prototype);

		Algorithm algorithm = Algorithm.RSA512((RSAPublicKey) SIGNING_KEYPAIR.getPublic(), null);
		JWTVerifier verifier = JWT.require(algorithm)
				.withIssuer(htiPortalConfiguration.getIss())
				.build();

		verifier.verify(jwt);
	}

	@Test
	public void testGetPrototypeIssuerEmpty() {
		when(htiPortalConfiguration.getIss()).thenReturn("");
		final HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getScheme()).thenReturn("https");
		when(request.getServerName()).thenReturn("issuer.example.com");
		when(request.getServerPort()).thenReturn(443);
		final HtiPortalLaunchRequest prototype = htiPortalService.getPrototype(request);
		assertEquals("https://issuer.example.com", prototype.getIss());
		assertEquals(htiPortalConfiguration.getAud(), prototype.getAud());
		assertTrue(StringUtils.isNotBlank(prototype.getActivityId()));
		assertTrue(StringUtils.isNotBlank(prototype.getTaskId()));
		assertTrue(StringUtils.isNotBlank(prototype.getUserId()));
		assertEquals("https://module.example.com/launch", prototype.getLaunchUrl());
	}

	@Test
	public void testGetPrototypeIssuerEmptyOddPort() {
		when(htiPortalConfiguration.getIss()).thenReturn("");
		final HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getScheme()).thenReturn("https");
		when(request.getServerName()).thenReturn("issuer.example.com");
		when(request.getServerPort()).thenReturn(8443);
		final HtiPortalLaunchRequest prototype = htiPortalService.getPrototype(request);
		assertEquals("https://issuer.example.com:8443", prototype.getIss());
		assertEquals(htiPortalConfiguration.getAud(), prototype.getAud());
		assertTrue(StringUtils.isNotBlank(prototype.getActivityId()));
		assertTrue(StringUtils.isNotBlank(prototype.getTaskId()));
		assertTrue(StringUtils.isNotBlank(prototype.getUserId()));
		assertEquals("https://module.example.com/launch", prototype.getLaunchUrl());
	}

	@Test
	public void testGetPrototypeNormal() {
		final HtiPortalLaunchRequest prototype = htiPortalService.getPrototype(mock(HttpServletRequest.class));
		assertEquals(htiPortalConfiguration.getIss(), prototype.getIss());
		assertEquals(htiPortalConfiguration.getAud(), prototype.getAud());
		assertTrue(StringUtils.isNotBlank(prototype.getActivityId()));
		assertTrue(StringUtils.isNotBlank(prototype.getTaskId()));
		assertTrue(StringUtils.isNotBlank(prototype.getUserId()));
		assertEquals("https://module.example.com/launch", prototype.getLaunchUrl());
	}
}
