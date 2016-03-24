package com.vmware.vra.apisample;

import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Application.class);
    
    private final static String  host = "https://vra-01a.corp.local";
    private AuthorizationToken token = null;
    private RestTemplate restTemplate = null;

    public static void main(String args[]) {
        SpringApplication.run(Application.class);
    }

    @Override
    public void run(String... args) throws Exception {
    	init();
    	login();
    	findUser();
    	getCatalogItems();
    	getCatalogItemViews();
    	requestMachine("CentOS 6.3");
    	
    	//logout();
    	//testJson();
    }
    
    public void init() {
    	NullHostnameVerifier verifier = new NullHostnameVerifier(); 
        TrustAllCertificatesHttpRequestFactory requestFactory = new TrustAllCertificatesHttpRequestFactory(verifier);
    	
    	restTemplate = new RestTemplate(requestFactory);
    }
    
    public void login() {
    	String loginURL = host + "/identity/api/tokens";
    	HttpHeaders headers = new HttpHeaders();
    	headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	headers.setAccessControlAllowCredentials(true);
    	
    	HttpEntity<LoginRequest> request = new HttpEntity<>(
    			new LoginRequest("tony@corp.local", "VMware1!", "vsphere.local"), headers);
    	token = restTemplate.postForObject(loginURL, request, AuthorizationToken.class);
    	System.out.println(">>>>token:" + token.getId());
    }
    
    public void logout() {
    	String url = host + "/" + token.getId();
    	restTemplate.delete(url);
    	/*
    	HttpHeaders headers = new HttpHeaders();
    	headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    	headers.set("Authorization","Bearer " + token.getId());
    	HttpEntity<String> entity = new HttpEntity<String>(headers);
    	restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);*/
    }
    
    public void listTenants() {
    	String url = host + "/identity/api/tenants";
    	HttpHeaders headers = new HttpHeaders();
    	headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
    	headers.set("Authorization","Bearer " + token.getId());
    	
    	HttpEntity<String> entity = new HttpEntity<String>(headers);
    	ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    	//assertTrue(response.getStatusCode() == HttpStatus.OK);
    	log.info("tenants:" + response.getBody()); 
    }
    
    public void findUser() {
    	String url = host + "/identity/api/tenants/vsphere.local/principals/tony@corp.local";
    	HttpHeaders headers = new HttpHeaders();
    	headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
    	headers.set("Authorization","Bearer " + token.getId());
    	
    	HttpEntity<String> entity = new HttpEntity<String>(headers);
    	ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    	//assertTrue(response.getStatusCode() == HttpStatus.OK);
    	log.info("tenants:" + response.getBody()); 
    }
    
    public List<String> getCatalogItems() {
    	String url = host + "/catalog-service/api/consumer/entitledCatalogItems";
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	headers.set("Authorization","Bearer " + token.getId());
    	
    	HttpEntity<String> entity = new HttpEntity<String>(headers);
    	ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    	//assertTrue(response.getStatusCode() == HttpStatus.OK);
    	log.info(">>>CatalogItems:" + response.getBody()); 
    	return getCatalogIds(response.getBody());
    }
    
    public void requestMachine(String catalogItemName) {
    	String catalogItemViews = getCatalogItemViews();
    	Set<HATEOSLink> links = getLinksfromCatalogView(catalogItemViews, catalogItemName);
    	HATEOSLink templateLink = null;
    	HATEOSLink requestLink = null;
    	for(HATEOSLink l: links) {
    		if(l.getRel().contains("GET")) {
    			templateLink = l;
    		} else if(l.getRel().contains("POST")) {
    			requestLink = l;
    		}
    	}

    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	headers.set("Authorization","Bearer " + token.getId());
    	HttpEntity<HATEOSLink> entity = new HttpEntity<>(headers);
    	log.info(">>>templateLink URL: " + templateLink.getHref());
    	ResponseEntity<String> response = restTemplate.exchange(templateLink.getHref(), HttpMethod.GET, entity, String.class);
    	log.info(">>>RequestTemplate response: " + response.getBody());
    	
    	log.info(">>>requestLink URL: " + requestLink.getHref());
    	HttpEntity<String> requestEntity = new HttpEntity<>(response.getBody(), headers);
    	ResponseEntity<String> requestMachineResponse = restTemplate.exchange(requestLink.getHref(), HttpMethod.POST, requestEntity, String.class);
    	log.info(">>>requestMachineResponse: " + requestMachineResponse.getBody());
    }
    
    private String getCatalogItemViews() {
    	String url = host + "/catalog-service/api/consumer/entitledCatalogItemViews";
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	headers.set("Authorization","Bearer " + token.getId());
    	
    	HttpEntity<String> entity = new HttpEntity<String>(headers);
    	ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    	//assertTrue(response.getStatusCode() == HttpStatus.OK);
    	log.info(">>>CatalogItemViews:" + response.getBody()); 
    	return response.getBody();
    }
    
    public Set<HATEOSLink> getLinksfromCatalogView(String jsonStr, String catalogName) {
    	Set<HATEOSLink> res = new HashSet<>();
    	Gson gson = new GsonBuilder().create();
    	JsonObject obj = gson.fromJson(jsonStr, JsonObject.class);
    	JsonArray items = obj.getAsJsonArray("content");
    	Iterator<JsonElement> it = items.iterator();
    	while(it.hasNext()) {
    		JsonObject item = it.next().getAsJsonObject();
    		log.info("catalogName: " + item.get("name").getAsString());
    		log.info("links: " + item.get("links").getAsJsonArray());
    		if(item.get("name").getAsString().equalsIgnoreCase(catalogName)) {
    			JsonArray links = item.get("links").getAsJsonArray();
    			Iterator<JsonElement> itLinks = links.iterator();
    			while(itLinks.hasNext()) {
    				JsonObject link = itLinks.next().getAsJsonObject();
    				res.add(gson.fromJson(link.toString(), HATEOSLink.class));
    			}
    			break;
    		}    		
    	}
    	return res;
    }
    
    private List<String> getCatalogIds(String jsonString) {
    	List<String> res = new ArrayList<>();
    	Gson gson = new GsonBuilder().create();
    	JsonObject obj = gson.fromJson(jsonString, JsonObject.class);
    	JsonArray items = obj.getAsJsonArray("content");
    	Iterator<JsonElement> it = items.iterator();
    	while(it.hasNext()) {
    		JsonObject item = it.next().getAsJsonObject();
    		JsonObject clg = item.getAsJsonObject("catalogItem");
    		res.add(clg.get("id").getAsString());
    		log.info("id:" + clg.get("id").getAsString());
    		log.info("name:" + clg.get("name").getAsString());
    	}
    	return res;
    }
    
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
    
    
    public void test() {
        RestTemplate restTemplate = new RestTemplate();
        Quote quote = restTemplate.getForObject("http://gturnquist-quoters.cfapps.io/api/random", Quote.class);
        log.info(quote.toString());    	
    }
}

class TrustAllCertificatesHttpRequestFactory extends SimpleClientHttpRequestFactory {
    private final HostnameVerifier verifier;
    public TrustAllCertificatesHttpRequestFactory(HostnameVerifier verifier) {
        this.verifier = verifier;
    }

    @Override
    protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
        if (connection instanceof HttpsURLConnection) {
            ((HttpsURLConnection) connection).setHostnameVerifier(verifier);
            ((HttpsURLConnection) connection).setSSLSocketFactory(trustSelfSignedSSL().getSocketFactory());
            ((HttpsURLConnection) connection).setAllowUserInteraction(true);
        }
        super.prepareConnection(connection, httpMethod);
    }

    public SSLContext trustSelfSignedSSL() {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }
                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            ctx.init(null, new TrustManager[] { tm }, null);
            SSLContext.setDefault(ctx);
            return ctx;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}

class NullHostnameVerifier implements HostnameVerifier {
    public boolean verify(String hostname, SSLSession session) {
        return true;
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
	
	//BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();