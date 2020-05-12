/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright Headease B.V. (c) 2020.
 */

package nl.gidsopenstandaarden.hti.testsuite.portal.valueobject;

/**
 *
 */
public class LaunchData {
	String url;
	String token;
	boolean redirect = false;

	public LaunchData(String url, String token, boolean redirect) {
		this.url = url;
		this.token = token;
		this.redirect = redirect;
	}

	public LaunchData(String url, String token) {
		this.url = url;
		this.token = token;
	}

	public LaunchData(){

	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isRedirect() {
		return redirect;
	}

	public void setRedirect(boolean redirect) {
		this.redirect = redirect;
	}
}
