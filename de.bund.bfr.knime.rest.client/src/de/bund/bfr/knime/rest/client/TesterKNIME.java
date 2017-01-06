package de.bund.bfr.knime.rest.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

 
public class TesterKNIME {

    public static void main(String[] args) throws Exception {	  
        ClientConfig clientConfig = new ClientConfig();
        
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("", "");
        clientConfig.register( feature) ;
     
        Client client = ClientBuilder.newClient( clientConfig );
        //WebTarget webTarget = client.target("https://knime.bfrlab.de/com.knime.enterprise.server/rest/v4/repository/").path("testing");
        WebTarget webTarget = client.target("http://vm-knime:8094/com.knime.enterprise.server/rest/v4/repository/RemoteReader");
         
        Invocation.Builder invocationBuilder =  webTarget.request(MediaType.APPLICATION_JSON);
        Response response = invocationBuilder.get();
         
        System.out.println(response.getStatus());
        System.out.println(response.getStatusInfo());
         
        if(response.getStatus() == 200) {
        	System.out.println(invocationBuilder.get(String.class));
        }
  }
}