/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright Headease B.V. (c) 2020.
 */

package nl.gidsopenstandaarden.hti.testsuite.shared;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
@Service
public class HostIdentityService {
	public String getOwnHostIdentity(HttpServletRequest request) {
		final String scheme = request.getScheme();
		final int port = request.getServerPort();
		if (isDefault(scheme, port)) {
			return scheme + "://" + request.getServerName();
		} else {
			return scheme + "://" + request.getServerName() + ":" + port;
		}
	}

	private boolean isDefault(String scheme, int port) {
		return port == 80 && StringUtils.equals(scheme, "http") || port == 443 && StringUtils.equals(scheme, "https");
	}


}
