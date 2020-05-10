package nl.gidsopenstandaarden.hti.testsuite.portal.utils;

import java.security.KeyPair;

public class RsaKeyPairGenerator {

    public static void main(String[] args) throws Exception {
        KeyPair keyPair = KeyUtils.generateKeyPair();
        // Output the public key as base64
        String publicK = KeyUtils.encodeKey(keyPair.getPublic());
        // Output the private key as base64
        String privateK = KeyUtils.encodeKey(keyPair.getPrivate());

        System.out.println(publicK);
        System.out.println(privateK);
    }


}
