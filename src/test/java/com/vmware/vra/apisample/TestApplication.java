package com.vmware.vra.apisample;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestApplication {
	
	@Test
	public void testGetRequestTemplatefromCatalogViewResponse() throws IOException {
		String jsonStr = FileUtils.readFileToString(new File("d:\\vra\\catagoryview.json"));
		System.out.println(jsonStr);
		Application app = new Application();
		Set<HATEOSLink> links = app.getLinksfromCatalogView(jsonStr, "CentOS 6.3");
		
		for(HATEOSLink link: links) {
			System.out.println("url:" + link.getHref());
			
			Gson gson = new GsonBuilder().create();
			System.out.println("json: " + gson.toJson(link));
		}
		
	}
}
