/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright Headease B.V. (c) 2020.
 */

package nl.koppeltaal.smart.testsuite.service;


import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;
import nl.koppeltaal.smart.testsuite.valueobject.SmartTestSuiteLaunchRequest;
import nl.koppeltaal.springbootstarterjwks.config.JwksConfiguration;
import nl.koppeltaal.springbootstarterjwks.util.KeyUtils;
import org.apache.commons.lang3.StringUtils;
import org.jose4j.jwk.PublicJsonWebKey;
import org.jose4j.jwk.Use;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.jose4j.lang.JoseException;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class SmartTestSuiteService {

  private final JwksConfiguration jwksConfiguration;

  public SmartTestSuiteService(JwksConfiguration jwksConfiguration) {
    this.jwksConfiguration = jwksConfiguration;
  }

  public String getLaunchToken(SmartTestSuiteLaunchRequest request) throws GeneralSecurityException {
    try {
      JwtClaims claims = new JwtClaims();
      claims.setAudience(request.getAud());
      claims.setIssuer(request.getIss());
      claims.setSubject(request.getSub());
      claims.setIssuedAt(NumericDate.now());
      claims.setExpirationTime(NumericDate.fromMilliseconds(System.currentTimeMillis()
          + jwksConfiguration.getJwtTimeoutInSeconds() * 1000L));
      claims.setJwtId(UUID.randomUUID().toString());

//      return signWithJwks(claims);
      JsonWebSignature jws = new JsonWebSignature();

      // The payload of the JWS is JSON content of the JWT Claims
      jws.setPayload(claims.toJson());

      // Set the signature algorithm on the JWT/JWS that will integrity protect the claims
      jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA512);

      if(StringUtils.isNotBlank(request.getPrivateKey())) {
        return signWithCustomPrivateKey(jws, request.getPrivateKey());
      }

      return signWithJwks(jws);
    } catch (JoseException e) {
      throw new GeneralSecurityException(e);
    }
  }

  private String signWithJwks(JsonWebSignature jws)
      throws NoSuchAlgorithmException, InvalidKeySpecException, JoseException {
    KeyPair rsaKeyPair = KeyUtils
        .getRsaKeyPair(jwksConfiguration.getSigningPublicKey(), jwksConfiguration.getSigningPrivateKey());
    PublicJsonWebKey jwk = PublicJsonWebKey.Factory.newPublicJwk(rsaKeyPair.getPublic());
    jwk.setPrivateKey(rsaKeyPair.getPrivate());
    jwk.setUse(Use.SIGNATURE);
    jwk.setAlgorithm(jwksConfiguration.getSigningAlgorithm());

    // The JWT is signed using the private key
    jws.setKey(jwk.getPrivateKey());

    // Set the Key ID (kid) header because it's just the polite thing to do.
    // We only have one key in this example but a using a Key ID helps
    // facilitate a smooth key rollover process
    jws.setKeyIdHeaderValue(KeyUtils.getFingerPrint(rsaKeyPair.getPublic()));

    return jws.getCompactSerialization();
  }

  private String signWithCustomPrivateKey(JsonWebSignature jws, String privateKeyContent) throws GeneralSecurityException {

    try {

      // The JWT is signed using the private key
      jws.setKey(getPrivateKey(privateKeyContent));

      // Set the signature algorithm on the JWT/JWS that will integrity protect the claims
      jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA512);

      return jws.getCompactSerialization();
    } catch (JoseException e) {
      throw new GeneralSecurityException(e);
    }
  }

  private PrivateKey getPrivateKey(String privateKeyContent)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    KeyFactory kf = KeyFactory.getInstance("RSA");

    PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(
        Base64.getDecoder().decode(privateKeyContent),
        AlgorithmIdentifiers.RSA_USING_SHA512
    );

    return kf.generatePrivate(keySpecPKCS8);
  }
}
