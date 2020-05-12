/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright Headease B.V. (c) 2020.
 */

package nl.gidsopenstandaarden.hti.testsuite.portal.valueobject;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 */
public class HtiPortalLaunchRequest {
	@JsonProperty("task_id")
	String taskId;
	@JsonProperty("activity_id")
	String activityId;
	@JsonProperty("user_type")
	String userType;
	@JsonProperty("user_id")
	String userId;
	@JsonProperty("intent")
	String intent;
	@JsonProperty("status")
	String status;
	@JsonProperty("aud")
	private String aud;
	@JsonProperty("iss")
	private String iss;
	@JsonProperty("launch_url")
	private String launchUrl;
	@JsonProperty("use_jwe")
	private boolean isUseJwe = false;
	@JsonProperty("jwe_public_key")
	private String jwePublicKey;
	/**
	 * Read only usage of this property
	 */
	@JsonProperty("public_key")
	private String publicKey;

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getAud() {
		return aud;
	}

	public void setAud(String aud) {
		this.aud = aud;
	}

	public String getIntent() {
		return intent;
	}

	public void setIntent(String intent) {
		this.intent = intent;
	}

	public String getIss() {
		return iss;
	}

	public void setIss(String iss) {
		this.iss = iss;
	}

	public String getJwePublicKey() {
		return jwePublicKey;
	}

	public void setJwePublicKey(String jwePublicKey) {
		this.jwePublicKey = jwePublicKey;
	}

	public String getLaunchUrl() {
		return launchUrl;
	}

	public void setLaunchUrl(String launchUrl) {
		this.launchUrl = launchUrl;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public boolean isUseJwe() {
		return isUseJwe;
	}

	public void setUseJwe(boolean useJwe) {
		isUseJwe = useJwe;
	}
}
