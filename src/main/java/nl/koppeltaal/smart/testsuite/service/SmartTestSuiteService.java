/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright Headease B.V. (c) 2020.
 */

package nl.koppeltaal.smart.testsuite.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.koppeltaal.smart.testsuite.configuration.SmartTestSuiteConfiguration;
import nl.koppeltaal.smart.testsuite.utils.KeyUtils;
import nl.koppeltaal.smart.testsuite.valueobject.SmartTestSuiteLaunchRequest;
import nl.koppeltaal.smart.testsuite.valueobject.Task;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwk.PublicJsonWebKey;
import org.jose4j.jwk.Use;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.jose4j.lang.JoseException;
import org.springframework.stereotype.Service;

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
public class SmartTestSuiteService {

  private final SmartTestSuiteConfiguration smartTestSuiteConfiguration;

  private final ObjectMapper objectMapper;
  private final HostIdentityService hostIdentityService;

  public SmartTestSuiteService(
      SmartTestSuiteConfiguration smartTestSuiteConfiguration,
      ObjectMapper objectMapper,
      HostIdentityService hostIdentityService) {
    this.smartTestSuiteConfiguration = smartTestSuiteConfiguration;
    this.objectMapper = objectMapper;
    this.hostIdentityService = hostIdentityService;
  }

  public String getLaunchToken(SmartTestSuiteLaunchRequest request) throws GeneralSecurityException {
    try {
      JwtClaims claims = new JwtClaims();
      claims.setAudience(request.getAud());
      claims.setIssuer(request.getIss());
      claims.setSubject(request.getSub());
      claims.setIssuedAt(NumericDate.now());
      claims.setExpirationTime(NumericDate.fromMilliseconds(System.currentTimeMillis()
          + smartTestSuiteConfiguration.getJwtTimeoutInSeconds() * 1000L));
      claims.setJwtId(UUID.randomUUID().toString());

      JsonWebSignature jws = new JsonWebSignature();

      // The payload of the JWS is JSON content of the JWT Claims
      jws.setPayload(claims.toJson());

      KeyPair rsaKeyPair = KeyUtils.getRsaKeyPair(smartTestSuiteConfiguration.getSigningPublicKey(), smartTestSuiteConfiguration.getSigningPrivateKey());
      PublicJsonWebKey jwk = PublicJsonWebKey.Factory.newPublicJwk(rsaKeyPair.getPublic());
      jwk.setPrivateKey(rsaKeyPair.getPrivate());
      jwk.setUse(Use.SIGNATURE);
      jwk.setAlgorithm(smartTestSuiteConfiguration.getSigningAlgorithm());

      // The JWT is signed using the private key
      jws.setKey(jwk.getPrivateKey());

      // Set the Key ID (kid) header because it's just the polite thing to do.
      // We only have one key in this example but a using a Key ID helps
      // facilitate a smooth key rollover process
      jws.setKeyIdHeaderValue(KeyUtils.getFingerPrint(rsaKeyPair.getPublic()));

      // Set the signature algorithm on the JWT/JWS that will integrity protect the claims
      jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA512);

      return jws.getCompactSerialization();
    } catch (JoseException e) {
      throw new GeneralSecurityException(e);
    }
  }

}
