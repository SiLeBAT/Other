package de.bund.bfr.busstopp.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.SchemaFactory;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.bund.bfr.busstopp.Constants;
import de.bund.bfr.busstopp.dao.Dao;
import de.bund.bfr.busstopp.dao.ItemLoader;
import de.bund.bfr.busstopp.model.Item;
import de.bund.bfr.busstopp.model.ResponseX;
import de.bund.bfr.busstopp.util.SendEmail;
import de.bund.bfr.busstopp.util.XmlValidator;
import de.bund.bfr.busstopp.util.ZipArchive;
import de.nrw.verbraucherschutz.idv.daten.Kontrollpunktmeldung;
import de.nrw.verbraucherschutz.idv.daten.Meldung;

// Will map the resource to the URL items
@Path("/items")
public class ItemsResource {

	// Allows to insert contextual objects into the class,
	// e.g. ServletContext, Request, ResponseX, UriInfo
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
    @Context
    SecurityContext securityContext;
    
	// Return the list of items to the user in the browser
	@GET
	@Produces(MediaType.TEXT_XML)
	public List<Item> getItems4Browser() {
		return getOutputs(false);
	}

	// Return the list of items for applications
	@GET
	@Produces({ MediaType.APPLICATION_XML}) // , MediaType.APPLICATION_JSON 
	public List<Item> getItems() {
		return getOutputs(false);
	}
	private List<Item> getOutputs(boolean inclDeleted) {
		List<Item> items = new ArrayList<Item>();
		for (ItemLoader u : Dao.instance.getModel().values()) {
			items.add(u.getXml());
		}
		if (inclDeleted) {
			for (ItemLoader u : Dao.instance.getModelDel().values()) {
				items.add(u.getXml());
			}			
		}
		return items;
	}

	// deletes all Items
	@DELETE
	@Produces({ MediaType.APPLICATION_XML})
	public Response deleteAll() {
		ResponseX response = new ResponseX();
		Status status = Response.Status.OK;
		response.setAction("DELETEALL");
		if (true || securityContext.isUserInRole("bfr")) {
			int numDeleted = Dao.instance.deleteAll();
			response.setCount(numDeleted);
			response.setSuccess(true);
		}
		else {
			response.setSuccess(false);	
			status = Response.Status.FORBIDDEN;
			response.setError("No permission to access this feature!");
		}
		return Response.status(status).entity(response).type(MediaType.APPLICATION_XML).build();
	}

	// retuns the number of items
	@GET
	@Path("count")
	@Produces(MediaType.TEXT_PLAIN)
	public String getCount() {
		int count = Dao.instance.getModel().size();
		return String.valueOf(count);
	}

	// Defines that the next path parameter after items is
	// treated as a parameter and passed to the ItemResources
	// Allows to type http://localhost:8080/de.bund.bfr.busstopp/rest/app/1
	@Path("{id}")
	public ItemResource getItem(@PathParam("id") Long id) {
		return new ItemResource(uriInfo, request, securityContext, id);
	}

	// jersey.config.server.wadl.disableWadl=true

	/**
	 * ItemLoader a File
	 */

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_XML})
	public Response itemFile(@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition contentDispositionHeader,
			@FormDataParam("comment") String comment) {

		ResponseX response = new ResponseX();
		Status status = Response.Status.OK;
		response.setAction("UPLOAD");
		String un = securityContext.getUserPrincipal().getName();
		if (!un.equals("prod_bfr2lanuv")) {
			try {
				if (contentDispositionHeader != null) {
					String filename = contentDispositionHeader.getFileName();

					long newId = System.currentTimeMillis();
					ItemLoader item = new ItemLoader(newId, filename, comment);
					String filePath = item.save(fileInputStream);
					Dao.instance.getModel().put(newId, item);

					String[] tags = new String[]{"kontrollpunktmeldung"};
					if (securityContext.isUserInRole("bfr")) {
						tags = new String[]{"kontrollpunktmeldung","analyseergebnis"};
					}
					else if (securityContext.isUserInRole("bfr2x")) {
						tags = new String[]{"analyseergebnis"};
					}
					else if (securityContext.isUserInRole("x2bfr")) {
						tags = new String[]{"kontrollpunktmeldung"};
					}
					boolean isValid = new XmlValidator().validateViaRequest(filePath, tags);
					//isValid = true;
					response.setSuccess(isValid);
					response.setId(newId);
														
					if (!isValid) {
						status = Response.Status.PRECONDITION_FAILED;
						response.setError("'" + filename + "' couldn't be validated!");
						try {
							item.delete();
						} catch (IOException e) {
						}
						Dao.instance.getModel().remove(newId);
					}

					new SendEmail().doSend("'" + un + "' hat die Datei '" + filename + "' mit id '" + newId + "' hochgeladen: Valide -> " + isValid, filePath);
				}
				else {
					response.setSuccess(false);
					status = Response.Status.BAD_REQUEST;
				response.setError("Parameters not correct! Did you use 'file'?");
				}
			} catch (IOException | SOAPException e) {
				e.printStackTrace();
				response.setSuccess(false);
				status = Response.Status.INTERNAL_SERVER_ERROR;
				response.setError(e.getMessage());
			}
		}
		else {
			response.setSuccess(false);
			status = Response.Status.FORBIDDEN;
			response.setError("No permission to access this feature!");
		}
		return Response.status(status).entity(response).type(MediaType.APPLICATION_XML).build();
	}

	@GET
	@Path("kpms/{fallnummer}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getFiles(@PathParam("fallnummer") String fallNummer) {
		if (securityContext.isUserInRole("bfr")) {
			try {
				File zipfile = File.createTempFile("busstop_xmls", ".zip");
				ZipArchive za = new ZipArchive(zipfile.getAbsolutePath());
				List<Item> li = getOutputs(true);
				for (Item i : li) {
					Long id = i.getId();
					String filename = Constants.SERVER_UPLOAD_LOCATION_FOLDER + id + "/" + i.getIn().getFilename();
					//System.out.println(filename);
					if (fallNummer == null || fallNummer.trim().isEmpty()) za.add(new File(filename), id);
					else {
						String fn = getFallnummer(filename, "kontrollpunktmeldung");
						if (fallNummer.equals(fn)) za.add(new File(filename), id);
					}
					
				}
				za.close();
			    ResponseBuilder response = Response.noContent();
			    if (zipfile.exists() && zipfile.isFile()) {
				    response = Response.ok((Object) zipfile);
				    //response.setContentType("application/zip");
				    response.header("Content-Disposition", "attachment; filename=" + zipfile.getName());
			    }
			    return response.build();
			}
			catch (Exception e) {
				e.printStackTrace();
				return Response.noContent().build();
			}
		}
		else {
			return Response.noContent().build();
		}
	}
	@GET
	@Path("faelle")
	@Produces(MediaType.TEXT_PLAIN)
	public String getFaelle() {
		if (securityContext.isUserInRole("bfr")) {
			try {
				HashSet<String> faelle = new HashSet<>();
				List<Item> li = getOutputs(true);
				for (Item i : li) {
					Long id = i.getId();
					String filename = Constants.SERVER_UPLOAD_LOCATION_FOLDER + id + "/" + i.getIn().getFilename();
					String fn = getFallnummer(filename, "kontrollpunktmeldung");
					if (fn != null) faelle.add(fn);
				}
				String out = "";
				for (String fn : faelle) {
					out += fn + "\n";
				}
				return out;
			}
			catch (Exception e) {
				e.printStackTrace();
				return "";
			}
		}
		return "";
	}
	@SuppressWarnings("unchecked")
	private String getFallnummer(String filename, String tag) throws SOAPException, IOException {
		String fallnummer = null;
		Unmarshaller reader;
		try {
			reader = JAXBContext.newInstance(Kontrollpunktmeldung.class.getPackage().getName()).createUnmarshaller();

			reader.setSchema(SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
					.newSchema(Kontrollpunktmeldung.class.getResource(
							"/de/nrw/verbraucherschutz/idv/dienste/de.nrw.verbraucherschutz.idv.dienste.2016.2.warenrueckverfolgung.transport.schema.xsd")));

			File f = new File(filename);
						InputStream template = new FileInputStream(f);
						MessageFactory mf = MessageFactory.newInstance(); // SOAPConstants.SOAP_1_1_PROTOCOL
						SOAPMessage message = mf.createMessage(new MimeHeaders(), template);
						SOAPPart sp = message.getSOAPPart();
						SOAPEnvelope se = null;
						try {
							se = sp.getEnvelope();
						}
						catch (Exception e) {}
						if (se != null) {
							SOAPBody body = se.getBody();
							NodeList nl = body.getChildNodes();
							for (int i = 0; i < nl.getLength(); i++) {
								Node nln = nl.item(i);
								String nn = nln.getNodeName();
								if (nn.endsWith(":" + tag)) {
									DOMSource ds = new DOMSource(nln);
									try {
										Kontrollpunktmeldung kpm = ((JAXBElement<Kontrollpunktmeldung>) reader.unmarshal(ds)).getValue();
										Meldung meldung = kpm.getMeldung();
										fallnummer = meldung.getFallNummer();
										break;
									}
									catch (Exception e) {System.err.println(filename);}
								}
							}
						}
		} catch (JAXBException e) {
			System.err.println(filename);
			e.printStackTrace();
		} catch (SAXException e) {
			System.err.println(filename);
			e.printStackTrace();
		}
		return fallnummer;
	}
/*
	@GET
	@Path("rdt_json")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response getRdtJson() {
		String filename = Constants.SERVER_UPLOAD_LOCATION_FOLDER + "/bbk.json";
	    ResponseBuilder response = Response.noContent();
	    File file = new File(filename);
	    if (file.exists()) {
		    response = Response.ok((Object) file);
		    response.header("Content-Disposition", "attachment; filename=" + file.getName());
	    }
	    return response.header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, OPTIONS").header("Access-Control-Max-Age", "1000").build();
	}
*/
	@DELETE
	@Path("bin")
	@Produces({ MediaType.APPLICATION_XML})
	public Response clearBin() {
		ResponseX response = new ResponseX();
		Status status = Response.Status.OK;
		response.setAction("CLEARBIN");
		if (securityContext.isUserInRole("bfr")) {
			int numDeleted = Dao.instance.clearBin();
			response.setCount(numDeleted);
			response.setSuccess(true);
		}
		else {
			response.setSuccess(false);
			status = Response.Status.FORBIDDEN;
			response.setError("No permission to access this feature!");
		}
		return Response.status(status).entity(response).type(MediaType.APPLICATION_XML).build();
	}
	
}