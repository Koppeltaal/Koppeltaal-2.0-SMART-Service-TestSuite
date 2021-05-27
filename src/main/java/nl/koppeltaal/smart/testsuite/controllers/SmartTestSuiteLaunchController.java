/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright Headease B.V. (c) 2020.
 */

package nl.koppeltaal.smart.testsuite.controllers;

import nl.koppeltaal.smart.testsuite.service.SmartTestSuiteService;
import nl.koppeltaal.smart.testsuite.valueobject.SmartTestSuiteLaunchRequest;
import nl.koppeltaal.smart.testsuite.valueobject.LaunchData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.GeneralSecurityException;

/**
 *
 */

@RestController
@RequestMapping("launch")
public class SmartTestSuiteLaunchController {
	@Autowired
	public void setSmartTestSuiteService(SmartTestSuiteService smartTestSuiteService) {
		this.smartTestSuiteService = smartTestSuiteService;
	}

	private SmartTestSuiteService smartTestSuiteService;

	@RequestMapping(method = RequestMethod.POST)
	public LaunchData launch(@RequestBody SmartTestSuiteLaunchRequest request) throws GeneralSecurityException {
		LaunchData rv = new LaunchData(request, false);
		rv.setClientAssertion(smartTestSuiteService.getLaunchToken(request));
		return rv;
	}

}
