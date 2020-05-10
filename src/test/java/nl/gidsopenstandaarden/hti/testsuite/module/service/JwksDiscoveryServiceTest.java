package nl.gidsopenstandaarden.hti.testsuite.module.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.gidsopenstandaarden.hti.testsuite.module.valueobject.ValidationRequest;
import nl.gidsopenstandaarden.hti.testsuite.portal.configuration.HtiPortalConfiguration;
import nl.gidsopenstandaarden.hti.testsuite.portal.service.HtiPortalService;
import nl.gidsopenstandaarden.hti.testsuite.portal.utils.KeyUtils;
import nl.gidsopenstandaarden.hti.testsuite.portal.valueobject.HtiPortalLaunchRequest;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.lang.JoseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class JwksDiscoveryServiceTest {
	@Spy
	private ObjectMapper objectMapper = new ObjectMapper();
	@InjectMocks
	private JwksDiscoveryService jwksDiscoveryService;

	HtiPortalService htiPortalService;

	HtiPortalConfiguration htiPortalConfiguration;

	static KeyPair SIGNING_KEYPAIR;

	@Before
	public void initTests() throws NoSuchAlgorithmException {
		if (SIGNING_KEYPAIR == null) {
			SIGNING_KEYPAIR = KeyUtils.generateKeyPair();
		}
		htiPortalService = new HtiPortalService();
		htiPortalConfiguration = new HtiPortalConfiguration();
		htiPortalConfiguration.setJwtTimeoutInSeconds(10);
		htiPortalConfiguration.setAud("https://audience.example.com");
		htiPortalConfiguration.setIss("https://issuer.example.com");
		htiPortalConfiguration.setModuleLaunchUrl("/launch");
		htiPortalConfiguration.setSigningAlgorithm("RSA512");
		htiPortalConfiguration.setSigningPublicKey(KeyUtils.encodeKey(SIGNING_KEYPAIR.getPublic()));
		htiPortalConfiguration.setSigningPrivateKey(KeyUtils.encodeKey(SIGNING_KEYPAIR.getPrivate()));
		htiPortalService.setHtiPortalConfiguration(htiPortalConfiguration);
		htiPortalService.setObjectMapper(new ObjectMapper());
	}

	@Test
	public void testGetJwksUrl1() throws GeneralSecurityException {
		HtiPortalLaunchRequest prototype = htiPortalService.getPrototype(mock(HttpServletRequest.class));
		String token = htiPortalService.getLaunchToken(prototype);

		String jwksUrl = jwksDiscoveryService.getJwksUrl(token);
		assertEquals("https://issuer.example.com/.well-known/jwks.json", jwksUrl);
	}
	@Test
	public void testGetJwksUrl2() throws GeneralSecurityException {
		htiPortalConfiguration.setIss("issuer.example.com");
		HtiPortalLaunchRequest prototype = htiPortalService.getPrototype(mock(HttpServletRequest.class));
		String token = htiPortalService.getLaunchToken(prototype);

		jwksDiscoveryService = Mockito.spy(jwksDiscoveryService);

		String jwksUrl = jwksDiscoveryService.getJwksUrl(token);
		assertEquals("https://issuer.example.com/.well-known/jwks.json", jwksUrl);
	}
	@Test
	public void testDiscoverPublicKeyWithJwks() throws GeneralSecurityException, JoseException, IOException {
		htiPortalConfiguration.setIss("issuer.example.com");
		HtiPortalLaunchRequest prototype = htiPortalService.getPrototype(mock(HttpServletRequest.class));
		String token = htiPortalService.getLaunchToken(prototype);

		jwksDiscoveryService = Mockito.spy(jwksDiscoveryService);
		JsonWebKey jsonWebKey = mock(JsonWebKey.class);
		when(jsonWebKey.getKey()).thenReturn(SIGNING_KEYPAIR.getPublic());
		Mockito.doReturn(jsonWebKey).when(jwksDiscoveryService).getJsonWebKeyByKid(Mockito.anyString(), Mockito.anyString());

		Key key = jwksDiscoveryService.discoverPublicKeyWithJwks(token);
		assertEquals(SIGNING_KEYPAIR.getPublic(), key);
	}

	@Test
	public void testDiscoverPublicKeyWithJwksException() throws GeneralSecurityException, JoseException, IOException {
		htiPortalConfiguration.setIss("issuer.example.com");
		HtiPortalLaunchRequest prototype = htiPortalService.getPrototype(mock(HttpServletRequest.class));
		String token = htiPortalService.getLaunchToken(prototype);

		jwksDiscoveryService = Mockito.spy(jwksDiscoveryService);
		Mockito.doThrow(JoseException.class).when(jwksDiscoveryService).getJsonWebKeyByKid(Mockito.anyString(), Mockito.anyString());

		Key key = jwksDiscoveryService.discoverPublicKeyWithJwks(token);
		assertNull(key);
	}
	@Test
	public void testDiscoverPublicKeyWithJwksEmpty() throws GeneralSecurityException {
		htiPortalConfiguration.setIss("issuer.example.com");
		HtiPortalLaunchRequest prototype = htiPortalService.getPrototype(mock(HttpServletRequest.class));
		String token = htiPortalService.getLaunchToken(prototype);

		jwksDiscoveryService = Mockito.spy(jwksDiscoveryService);
		Mockito.doReturn("").when(jwksDiscoveryService).getJwksUrl(Mockito.anyString());

		Key key = jwksDiscoveryService.discoverPublicKeyWithJwks(token);
		assertNull(key);
	}
}
