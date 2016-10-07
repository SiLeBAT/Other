package de.bund.bfr.busstopp.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import javax.xml.soap.SOAPException;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import de.bund.bfr.busstopp.Constants;
import de.bund.bfr.busstopp.dao.Dao;
import de.bund.bfr.busstopp.dao.ItemLoader;
import de.bund.bfr.busstopp.model.Item;
import de.bund.bfr.busstopp.model.ResponseX;
import de.bund.bfr.busstopp.util.SendEmail;
import de.bund.bfr.busstopp.util.XmlValidator;
import de.bund.bfr.busstopp.util.ZipArchive;

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
		if (!securityContext.getUserPrincipal().getName().equals("prod_bfr2lanuv")) {
			try {
				if (contentDispositionHeader != null) {
					String filename = contentDispositionHeader.getFileName();

					long newId = System.currentTimeMillis();
					ItemLoader item = new ItemLoader(newId, filename, comment);
					String filePath = item.save(fileInputStream);
					Dao.instance.getModel().put(newId, item);

					boolean isValid = new XmlValidator().validateViaRequest(filePath, "kontrollpunktmeldung");
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

					new SendEmail().doSend("'" + filename + "' mit id '" + newId + "' wurde validiert: " + isValid, filePath);
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
	@Path("files")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getFiles() {
		if (securityContext.isUserInRole("bfr")) {
			try {
				File zipfile = File.createTempFile("busstop_xmls", ".zip");
				ZipArchive za = new ZipArchive(zipfile.getAbsolutePath());
				List<Item> li = getOutputs(true);
				for (Item i : li) {
					Long id = i.getId();
					String filename = Constants.SERVER_UPLOAD_LOCATION_FOLDER + id + "/" + i.getIn().getFilename();
					za.add(new File(filename), id);
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
	@POST
	@Path("/uploadreport")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_XML})
	public Response resultFile(@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition contentDispositionHeader) {

		ResponseX response = new ResponseX();
		Status status = Response.Status.OK;
		response.setAction("UPLOADREPORT");
		if (securityContext.getUserPrincipal().getName().equals("bfr_admin")) {
			try {
				if (contentDispositionHeader != null) {
					long id = System.currentTimeMillis();
					String filename = Constants.SERVER_UPLOAD_LOCATION_FOLDER + "out_" + id + File.separator + "report.bfr";
					ItemLoader.saveReport(fileInputStream, filename);

					boolean isValid = new XmlValidator().validateViaRequest(filename, "analyseergebnis");
					response.setSuccess(isValid);
					response.setId(id);
														
					if (!isValid) {
						status = Response.Status.PRECONDITION_FAILED;
						response.setError("'" + filename + "' couldn't be validated!");
					}

					new SendEmail().doSend("'" + filename + "' mit id '" + id + "' wurde validiert: " + isValid, filename);
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