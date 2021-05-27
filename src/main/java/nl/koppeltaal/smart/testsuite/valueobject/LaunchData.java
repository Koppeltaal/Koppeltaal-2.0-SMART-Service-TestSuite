/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright Headease B.V. (c) 2020.
 */

package nl.koppeltaal.smart.testsuite.valueobject;

/**
 *
 */
public class LaunchData {
	private final String url;
	private final String clientAssertionType = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer";
	private final String grantType = "client_credentials";
	private final String scope;
	private final boolean redirect;

	private String clientAssertion;

	public LaunchData(SmartTestSuiteLaunchRequest request, boolean redirect) {
		this.url = request.getAud();
		this.scope = request.getScope();
		this.redirect = redirect;
	}

	public String getClientAssertion() {
		return clientAssertion;
	}

	public void setClientAssertion(String clientAssertion) {
		this.clientAssertion = clientAssertion;
	}

	public String getClientAssertionType() {
		return clientAssertionType;
	}

	public String getUrl() {
		return url;
	}

	public String getGrantType() {
		return grantType;
	}

	public String getScope() {
		return scope;
	}

	public boolean isRedirect() {
		return redirect;
	}
}
