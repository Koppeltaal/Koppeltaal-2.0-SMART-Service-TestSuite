package nl.gidsopenstandaarden.hti.testsuite.module.service;

import nl.gidsopenstandaarden.hti.testsuite.module.configuration.HtiModuleConfiguration;
import nl.gidsopenstandaarden.hti.testsuite.module.valueobject.ValidationRequest;
import nl.gidsopenstandaarden.hti.testsuite.module.valueobject.ValidationResponse;
import nl.gidsopenstandaarden.hti.testsuite.portal.utils.KeyUtils;
import nl.gidsopenstandaarden.hti.testsuite.shared.HostIdentityService;
import org.apache.commons.lang3.StringUtils;
import org.jose4j.jwt.consumer.*;
import org.jose4j.jwx.JsonWebStructure;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

/**
 *
 */
@Service
public class HtiModuleService {

	private JwksDiscoveryService jwksDiscoveryService;

	private HostIdentityService hostIdentityService;
	private HtiModuleConfiguration htiModuleConfiguration;

	@Autowired
	public void setHostIdentityService(HostIdentityService hostIdentityService) {
		this.hostIdentityService = hostIdentityService;
	}

	@Autowired
	public void setHtiModuleConfiguration(HtiModuleConfiguration htiModuleConfiguration) {
		this.htiModuleConfiguration = htiModuleConfiguration;
	}

	@Autowired
	public void setJwksDiscoveryService(JwksDiscoveryService jwksDiscoveryService) {
		this.jwksDiscoveryService = jwksDiscoveryService;
	}

	public ValidationResponse validate(ValidationRequest request, HttpServletRequest httpServletRequest) {
		final ValidationResponse response = new ValidationResponse();
		if (StringUtils.isEmpty(request.getPublicKey())) {
			Key publicKey = jwksDiscoveryService.discoverPublicKeyWithJwks(request.getToken());
			if (publicKey != null) {
				request.setPublicKey(KeyUtils.encodeKey(publicKey));
			}
		}
		try {
			JwtConsumer consumer = new JwtConsumerBuilder()
					.setRequireExpirationTime()
					.setExpectedAudience(true, htiModuleConfiguration.getAud(), hostIdentityService.getOwnHostIdentity(httpServletRequest)) // This should be done
					.setVerificationKey(KeyUtils.getRsaPublicKey(request.getPublicKey()))
					.build();
			JwtContext ctx = consumer.process(request.getToken());

			List<JsonWebStructure> joseObjects = ctx.getJoseObjects();
			JsonWebStructure jsonWebStructure = joseObjects.get(0);
			response.setPayload(jsonWebStructure.getPayload());
			response.setHeader(jsonWebStructure.getHeaders().getFullHeaderAsJsonString());
			response.setAlgorithm(jsonWebStructure.getAlgorithm().getAlgorithmIdentifier());

		} catch (JoseException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidJwtException e) {
			e.printStackTrace();
			response.setError(e.getMessage());
		}

		response.setPublicKey(request.getPublicKey());
		response.setJwksUrl(jwksDiscoveryService.getJwksUrl(request.getToken()));
		return response;
	}

}
