package nl.gidsopenstandaarden.hti.testsuite.module.valueobject;

/**
 *
 */
public class ValidationResponse {
	private String header;
	private String payload;
	private String publicKey;
	private String algorithm;
	private String error;
	private String jwksUrl;

	public String getAlgorithm() {
		return algorithm;
	}

	public String getError() {
		return error;
	}

	public String getJwksUrl() {
		return jwksUrl;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public String getPayload() {
		return payload;
	}

	public void setError(String error) {
		this.error = error;
	}

	public void setJwksUrl(String jwksUrl) {
		this.jwksUrl = jwksUrl;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}


}
