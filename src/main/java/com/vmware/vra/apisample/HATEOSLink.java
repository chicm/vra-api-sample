package com.vmware.vra.apisample;

import com.google.gson.annotations.SerializedName;

public class HATEOSLink {
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getRel() {
		return rel;
	}
	public void setRel(String rel) {
		this.rel = rel;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	@SerializedName("@type")
	private String type;
	private String rel;
	private String href;
}
