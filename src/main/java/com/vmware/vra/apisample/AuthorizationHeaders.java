package com.vmware.vra.apisample;

import org.springframework.http.HttpHeaders;

public class AuthorizationHeaders extends HttpHeaders {
	private static final long serialVersionUID = -4253977753194877385L;

	public AuthorizationHeaders(AuthorizationToken token) {
		this.set("Authorization","Bearer " + token.getId());
	}
}
