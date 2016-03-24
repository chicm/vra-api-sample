package com.vmware.vra.apisample;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

public class TestVraRestClient {
	@Test
	public void testGetRequestTemplatefromCatalogViewResponse() throws Exception {
		String jsonStr = FileUtils.readFileToString(new File("d:\\vra\\catagoryview.json"));
		System.out.println(jsonStr);
		VraRestClient app = new VraRestClient();
		Method m = VraRestClient.class.getDeclaredMethod("getRequestTemplateLink", String.class, String.class);
		m.setAccessible(true);
		HATEOSLink link = (HATEOSLink)m.invoke(app, jsonStr, "CentOS 6.3");
		
		System.out.println("url:" + link.getHref());
			
		Gson gson = new GsonBuilder().create();
		System.out.println("json: " + gson.toJson(link));		
	}
	
	@Test
	public void testGetRequestMachineLink() throws Exception {
		String jsonStr = FileUtils.readFileToString(new File("d:\\vra\\catagoryview.json"));
		System.out.println(jsonStr);
		VraRestClient app = new VraRestClient();
		Method m = VraRestClient.class.getDeclaredMethod("getRequestMachineLink", String.class, String.class);
		m.setAccessible(true);
		HATEOSLink link = (HATEOSLink)m.invoke(app, jsonStr, "CentOS 6.3");
		
		System.out.println("url:" + link.getHref());
			
		Gson gson = new GsonBuilder().create();
		System.out.println("json: " + gson.toJson(link));		
	}
	
	@Test
    public void testJson() {
    	try {
	    	Gson gson = new GsonBuilder().create();
	    	JsonReader reader = new JsonReader(new FileReader("d:\\vra\\catalogItemResponse.json"));
	    	JsonObject obj = gson.fromJson(reader, JsonObject.class);
	    	//JsonObject obj = gson.fromJson("", JsonObject.class);
	    	JsonArray items = obj.getAsJsonArray("content");
	    	Iterator<JsonElement> it = items.iterator();
	    	while(it.hasNext()) {
	    		JsonObject item = it.next().getAsJsonObject();
	    		JsonObject clg = item.getAsJsonObject("catalogItem");
	    		System.out.println("id:" + clg.get("id").getAsString());
	    		System.out.println("name:" + clg.get("name").getAsString());
	    	}
    	} catch(Exception e) {
    		e.printStackTrace(System.out);
    	}
    }
}

/*
 * ClientHttpRequestFactory requestFactory = getClientHttpRequestFactory();
RestTemplate restTemplate = new RestTemplate(requestFactory);

HttpEntity<Foo> request = new HttpEntity<>(new Foo("bar"));
Foo foo = restTemplate.postForObject(fooResourceUrl, request, Foo.class);
assertThat(foo, notNullValue());
assertThat(foo.getName(), is("bar"));

SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).useTLS().build();
SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, new AllowAllHostnameVerifier());
BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("username", "mypassword"));

HttpClient httpClient = HttpClientBuilder.create()
                             .setSSLSocketFactory(connectionFactory)
                             .setDefaultCredentialsProvider(credentialsProvider)
                             .build();

ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);


 */
/*   
public void test() {
    RestTemplate restTemplate = new RestTemplate();
    Quote quote = restTemplate.getForObject("http://gturnquist-quoters.cfapps.io/api/random", Quote.class);
    log.info(quote.toString());    	
}*/
//BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();