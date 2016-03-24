package com.vmware.vra.apisample;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorizationToken {
	private String expires;
	private String id;
	private String tenant;
	public String getExpires() {
		return expires;
	}
	public void setExpires(String expires) {
		this.expires = expires;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTenant() {
		return tenant;
	}
	public void setTenant(String tenant) {
		this.tenant = tenant;
	}
}
