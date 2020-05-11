package nl.gidsopenstandaarden.hti.testsuite.module.valueobject;

/**
 *
 */
public class ValidationRequest {
	private String token;
	private String publicKey;

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
