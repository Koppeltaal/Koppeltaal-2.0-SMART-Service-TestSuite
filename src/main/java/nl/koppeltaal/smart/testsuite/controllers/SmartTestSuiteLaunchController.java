/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright Headease B.V. (c) 2020.
 */

package nl.koppeltaal.smart.testsuite.controllers;

import java.security.GeneralSecurityException;
import java.util.Collections;
import nl.koppeltaal.smart.testsuite.service.SmartTestSuiteService;
import nl.koppeltaal.smart.testsuite.valueobject.LaunchData;
import nl.koppeltaal.smart.testsuite.valueobject.SmartTestSuiteLaunchRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 *
 */

@RestController
@RequestMapping("launch")
public class SmartTestSuiteLaunchController {

	private final RestTemplate restTemplate;

	public SmartTestSuiteLaunchController(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Autowired
	public void setSmartTestSuiteService(SmartTestSuiteService smartTestSuiteService) {
		this.smartTestSuiteService = smartTestSuiteService;
	}

	private SmartTestSuiteService smartTestSuiteService;

	@RequestMapping(value = "debug", method = RequestMethod.POST)
	public LaunchData debug(@RequestBody SmartTestSuiteLaunchRequest request) throws GeneralSecurityException {
		LaunchData rv = new LaunchData(request, false);
		rv.setClientAssertion(smartTestSuiteService.getLaunchToken(request));
		return rv;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String getAccessToken(@RequestBody SmartTestSuiteLaunchRequest launchRequest) throws GeneralSecurityException {
		LaunchData rv = new LaunchData(launchRequest, false);
		rv.setClientAssertion(smartTestSuiteService.getLaunchToken(launchRequest));

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("client_assertion_type", rv.getClientAssertionType());
		body.add("client_assertion", rv.getClientAssertion());
		body.add("grant_type", rv.getGrantType());

		if(StringUtils.isNotBlank(rv.getScope())) {
			body.add("scope", rv.getScope());
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

		return restTemplate.postForObject(rv.getUrl(), request, String.class);
	}

}
