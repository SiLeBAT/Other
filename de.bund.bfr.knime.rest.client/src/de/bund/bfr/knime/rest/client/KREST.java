package de.bund.bfr.knime.rest.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.SslConfigurator;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class KREST {

	//private static final String restResource = "http://maslxlabhpc01.bfr.bund.de/knime/rest/v4/";
	private static final String restResource = "https://knime.bfrlab.de/com.knime.enterprise.server/rest/v4/";
	//private static final String restResource = "http://vm-knime:8095/vm-knime/rest/v4/";
	
	private Client client = null;
	private CacheControl cc = null;

	public KREST(String username, String password) {
		System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
		cc = getCC();
		/*
        final InputStream trustStore = KREST.class.getResourceAsStream("/de/bund/bfr/knime/rest/client/res/truststore.jks");
        byte[] ba = null;
		try {
			ba = getByteArray(trustStore);
		} catch (IOException e) {
			e.printStackTrace();
		}
       SslConfigurator sslConfig = SslConfigurator.newInstance()
                .trustStoreBytes(ba)
                .trustStorePassword("pmmlab")
                .keyStoreBytes(ba)
                .keyPassword("pmmlab");

		SSLContext sslContext = sslConfig.createSSLContext();
			

		client = ClientBuilder.newBuilder().sslContext(sslContext).build();
		*/
	    client = ClientBuilder.newClient();

		client.register(HttpAuthenticationFeature.basic(username, password));
		client.register(MultiPartFeature.class);
	}
	private byte[] getByteArray(InputStream is) throws IOException {
		      ByteArrayOutputStream bos = new ByteArrayOutputStream();

		        byte[] buffer = new byte[4096];
		        int bytesRead = 0;
		        while ((bytesRead = is.read(buffer)) != -1) {
		            bos.write(buffer, 0, bytesRead);
		        }
		        return bos.toByteArray();
	}

	public Map<String, String> getJobPoolResult(String wfPath, Map<String, Object> inputs, Map<String, Boolean> outputs)
			throws IOException, ParseException {
		FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
		MultiPart multipartEntity = formDataMultiPart;
		for (String param : inputs.keySet()) {
			Object o = inputs.get(param);
			if (o instanceof File) {
				File f = (File) o;
				FileDataBodyPart filePart = new FileDataBodyPart("file", f);
				filePart.setContentDisposition(FormDataContentDisposition.name(param).fileName(f.getName()).build()); // "file-upload-1"
				multipartEntity = formDataMultiPart.bodyPart(filePart);
			} else {
				multipartEntity = formDataMultiPart.field(param, inputs.get(param), MediaType.APPLICATION_JSON_TYPE); // "line-count-3"																														// "{\"integer\":2}"
			}
		}

		System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
		Builder builder = client.target(restResource).path("repository").path(wfPath + ":job-pool").request().accept(MediaType.APPLICATION_JSON);
		Response res = builder.cacheControl(cc).post(Entity.entity(multipartEntity, MediaType.MULTIPART_FORM_DATA));
		
		Map<String, String> result = getResult(res, outputs);
		
		formDataMultiPart.close();
		multipartEntity.close();

		return result;
	}
	private CacheControl getCC() {
        CacheControl cc = new CacheControl();
        cc.setNoCache( true );
        cc.setMaxAge( -1 );
        cc.setMustRevalidate( true );
        return cc;
	}

	public String discardJob(String jobid) {
		System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
		Builder builder = client.target(restResource).path("jobs").path(jobid).request()
		// .accept(MediaType.APPLICATION_JSON)
		;
		Response res = builder.cacheControl(cc).delete();
		String result = res.getStatus() + "\t" + res.readEntity(String.class);

		res.close();

		return result;
	}

	private Map<String, String> getResult(Response res, Map<String, Boolean> outputs) throws IOException, ParseException {
		Map<String, String> result = new HashMap<>();
		String json = res.readEntity(String.class);

		JSONParser parser = new JSONParser();
		Object obj = parser.parse(json);
		JSONObject jsonObject = (JSONObject) obj;
		//JSONObject state = (JSONObject) jsonObject.get("state");
		if (!jsonObject.get("state").toString().equalsIgnoreCase("executed")) return null;
		JSONObject ov = (JSONObject) jsonObject.get("outputValues");
		if (ov != null) {
			for (String param : outputs.keySet()) {
				Object jo = ov.get(param);
				if (jo != null) {
					if (jo instanceof JSONArray) {
						JSONArray pv = (JSONArray) ov.get(param);
						if (pv != null) result.put(param, pv.toJSONString());
					}
					else if (jo instanceof JSONObject) {
						result.put(param, ((JSONObject) jo).toJSONString());
					}
				}
			}
		}
		res.close();
		return result;
	}
	public Map<String, String> getResult(String jobid, Map<String, Boolean> outputs)
			throws IOException, ParseException {
		System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
		Builder builder = client.target(restResource).path("jobs").path(jobid).request().accept(MediaType.APPLICATION_JSON);
		Response res = builder.cacheControl(cc).get();
		return getResult(res, outputs);
	}

	public boolean executeJob(String jobid, Map<String, Object> inputs) throws IOException {
		FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
		MultiPart multipartEntity = formDataMultiPart;
		if (inputs != null) {
			for (String param : inputs.keySet()) {
				Object o = inputs.get(param);
				if (o instanceof File) {
					File f = (File) o;
					FileDataBodyPart filePart = new FileDataBodyPart("file", f);
					filePart.setContentDisposition(FormDataContentDisposition.name(param).fileName(f.getName()).build()); // "file-upload-1"
					multipartEntity = formDataMultiPart.bodyPart(filePart);
				} else {
					multipartEntity = formDataMultiPart.field(param, inputs.get(param), MediaType.APPLICATION_JSON_TYPE); // "line-count-3"
																															// "{\"integer\":2}"
				}
			}
		}

		System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
		Builder builder = client.target(restResource).path("jobs").path(jobid).request().accept(MediaType.APPLICATION_JSON);
		Response res = builder.cacheControl(cc).post(Entity.entity(multipartEntity, MediaType.MULTIPART_FORM_DATA));

		boolean result = res.getStatus() == 200;
		// System.err.println(res.readEntity(String.class));
		//System.out.println(jobid + " - " + res);

		res.close();
		formDataMultiPart.close();
		multipartEntity.close();

		return result;
	}

	public String getNewJobID(String path) {
		String jobid = null;
		System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
		Builder builder = client.target(restResource).path(path).request().accept(MediaType.APPLICATION_JSON);
		Response res = builder.cacheControl(cc).post(null);

		// System.err.println(res.getStatus());
		if (res.getStatus() == 201) { // succesfully created
			res.close();

			String location = res.getHeaders().get("Location").get(0).toString();
			jobid = location.substring(location.indexOf("/jobs/") + 6);
		}
		res.close();

		return jobid;
	}
	/*
	public List<String> getAllJobIDs(String path) throws ParseException {
		List<String> result = new ArrayList<>();
		System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
		Builder builder = client.target(restResource).path(path).request().accept(MediaType.APPLICATION_JSON);
		Response res = builder.cacheControl(cc).get();

		String json = res.readEntity(String.class);

		JSONParser parser = new JSONParser();
		Object obj = parser.parse(json);
		JSONObject jsonObject = (JSONObject) obj;
		JSONArray jbs = (JSONArray) jsonObject.get("jobs");
		if (jbs != null) {
			for (int i=0; i < jbs.size(); i++) {
				JSONObject jo = (JSONObject) jbs.get(i);
				String id = (String) jo.get("id");
				String state = (String) jo.get("state");
				result.add(id);
			}
		}
		res.close();
		return result;
	}
*/
	private void is2File(InputStream is, String filename) {
		OutputStream outputStream = null;
		try {
			// write the inputStream to a FileOutputStream
			outputStream = new FileOutputStream(new File(filename));

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = is.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
			System.out.println("Done!");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					// outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}
}