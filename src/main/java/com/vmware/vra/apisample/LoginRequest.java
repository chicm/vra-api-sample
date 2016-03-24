package com.vmware.vra.apisample;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class LoginRequest {
	private String username;
	private String password;
	private String tenant;
	
	public LoginRequest(String u, String pwd, String t) {
		username = u;
		password = pwd;
		tenant = t;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getTenant() {
		return tenant;
	}
	public void setTenant(String tenant) {
		this.tenant = tenant;
	}
	
}
