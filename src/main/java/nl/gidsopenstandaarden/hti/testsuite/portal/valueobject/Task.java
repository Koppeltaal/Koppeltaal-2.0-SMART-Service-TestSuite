/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright Headease B.V. (c) 2020.
 */

package nl.gidsopenstandaarden.hti.testsuite.portal.valueobject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Task {
	String resourceType = "Task";
	String id;
	Meta meta;
	Text text;
	final Reference definitionReference = new Reference();
	String status = "requested";
	String intent = "plan";
	@JsonProperty("for")
	User forUser;
	User requester;
	User owner;

	public Reference getDefinitionReference() {
		return definitionReference;
	}

	public User getForUser() {
		return forUser;
	}

	public void setForUser(User forUser) {
		this.forUser = forUser;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIntent() {
		return intent;
	}

	public void setIntent(String intent) {
		this.intent = intent;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public User getRequester() {
		return requester;
	}

	public void setRequester(User requester) {
		this.requester = requester;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Text getText() {
		return text;
	}

	public void setText(Text text) {
		this.text = text;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static final class Meta {
		private String versionId;
		private Date lastUpdated;
		private String source = "https://issuer.edia.nl/fhir";

		public Date getLastUpdated() {
			return new Date(lastUpdated.getTime());
		}

		public void setLastUpdated(Date lastUpdated) {
			this.lastUpdated = new Date(lastUpdated.getTime());
		}

		public String getSource() {
			return source;
		}

		public void setSource(String source) {
			this.source = source;
		}

		public String getVersionId() {
			return versionId;
		}

		public void setVersionId(String versionId) {
			this.versionId = versionId;
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static final class Text {
		private String status;
		private String div;

		public String getDiv() {
			return div;
		}

		public void setDiv(String div) {
			this.div = div;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Identifier {
		private String value;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Reference {
		private String reference;

		public String getReference() {
			return reference;
		}

		public void setReference(String reference) {
			this.reference = reference;
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class User extends Reference {
	}
}
