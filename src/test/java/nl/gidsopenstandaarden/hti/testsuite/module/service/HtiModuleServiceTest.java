package nl.gidsopenstandaarden.hti.testsuite.module.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.gidsopenstandaarden.hti.testsuite.module.configuration.HtiModuleConfiguration;
import nl.gidsopenstandaarden.hti.testsuite.module.valueobject.ValidationRequest;
import nl.gidsopenstandaarden.hti.testsuite.module.valueobject.ValidationResponse;
import nl.gidsopenstandaarden.hti.testsuite.portal.configuration.HtiPortalConfiguration;
import nl.gidsopenstandaarden.hti.testsuite.portal.service.HtiPortalService;
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

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class HtiModuleServiceTest {
	@Mock
	HtiModuleConfiguration htiModuleConfiguration;

	@Spy
	ObjectMapper objectMapper = new ObjectMapper();

	@Spy
	HostIdentityService hostIdentityService = new HostIdentityService();

	@Mock
	JwksDiscoveryService jwksDiscoveryService;

	@InjectMocks
	HtiModuleService htiModuleService;

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

		when(htiModuleConfiguration.getAud()).thenReturn("https://audience.example.com");

	}

	@Test
	public void testValidate() throws GeneralSecurityException {
		ValidationRequest request = new ValidationRequest();
		HtiPortalLaunchRequest prototype = htiPortalService.getPrototype(mock(HttpServletRequest.class));
		request.setToken(htiPortalService.getLaunchToken(prototype));
		request.setPublicKey(KeyUtils.encodeKey(SIGNING_KEYPAIR.getPublic()));
		ValidationResponse validate = htiModuleService.validate(request, mock(HttpServletRequest.class));
		assertTrue(StringUtils.isBlank(validate.getError()));
	}
	@Test
	public void testValidateTimeout() throws GeneralSecurityException {
		htiPortalConfiguration.setJwtTimeoutInSeconds(-1);
		ValidationRequest request = new ValidationRequest();
		HtiPortalLaunchRequest prototype = htiPortalService.getPrototype(mock(HttpServletRequest.class));
		request.setToken(htiPortalService.getLaunchToken(prototype));
		request.setPublicKey(KeyUtils.encodeKey(SIGNING_KEYPAIR.getPublic()));
		ValidationResponse validate = htiModuleService.validate(request, mock(HttpServletRequest.class));
		assertTrue(StringUtils.isNotBlank(validate.getError()));
	}

}
