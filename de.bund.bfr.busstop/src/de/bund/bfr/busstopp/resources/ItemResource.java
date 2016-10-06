package de.bund.bfr.busstopp.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.xml.soap.SOAPException;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import de.bund.bfr.busstopp.Constants;
import de.bund.bfr.busstopp.dao.Dao;
import de.bund.bfr.busstopp.dao.ItemLoader;
import de.bund.bfr.busstopp.model.Item;
import de.bund.bfr.busstopp.model.ResponseX;
import de.bund.bfr.busstopp.util.SendEmail;
import de.bund.bfr.busstopp.util.XmlValidator;

import javax.ws.rs.core.UriInfo;

public class ItemResource {
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
    @Context
    SecurityContext securityContext;
	Long id;

	public ItemResource(UriInfo uriInfo, Request request, SecurityContext securityContext, Long id) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.securityContext = securityContext;
		this.id = id;
	}

	// Application integration
	@GET
	@Produces({ MediaType.APPLICATION_XML}) // , MediaType.APPLICATION_JSON 
	public Item getItem() {
		return getOutput();
	}

	// for the browser
	@GET
	@Produces(MediaType.TEXT_XML)
	public Item getItem4Browser() {
		return getOutput();
	}

	private Item getOutput() {
		ItemLoader item = Dao.instance.getModel().get(id);
		if (item == null)
			throw new RuntimeException("Get: ItemLoader with ID '" + id + "' not found");
		return item.getXml();
	}

	@DELETE
	@Produces({ MediaType.APPLICATION_XML})
	public Response deleteItem() {
		ResponseX response = new ResponseX();
		Status status = Response.Status.OK;
		response.setId(id);
		response.setAction("DELETE");
		if (true || securityContext.isUserInRole("x2bfr")) {
			ItemLoader c = Dao.instance.getModel().get(id);
			if (c != null) {
				try {
					c.delete();
				} catch (IOException e) {
					e.printStackTrace();
					response.setSuccess(false);
					status = Response.Status.INTERNAL_SERVER_ERROR;
					response.setError(e.getMessage());
				}
				c = Dao.instance.getModel().remove(id);
				response.setSuccess(true);
			}
			else  {
				response.setSuccess(false);
				status = Response.Status.PRECONDITION_FAILED;
				response.setError("ID not found");
			}
		} 
		else {
			response.setSuccess(false);
			status = Response.Status.FORBIDDEN;
			response.setError("No permission to access this feature!");
		}
		return Response.status(status).entity(response).type(MediaType.APPLICATION_XML).build();
	}
	
	private ResponseBuilder getDownloadResponse(String filename) {
	    ResponseBuilder response = Response.noContent();
	    File file = new File(filename);
	    if (file.exists() && file.isFile()) {
		    response = Response.ok((Object) file);
		    response.header("Content-Disposition", "attachment; filename=" + file.getName());
	    }
		return response;
	}
	@GET
	@Path("file")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getFile() {
		if (!securityContext.getUserPrincipal().getName().equals("prod_lanuv2bfr")) {
			ItemLoader c = Dao.instance.getModel().get(id);
			if (c == null) return Response.noContent().build();
			String filename = Constants.SERVER_UPLOAD_LOCATION_FOLDER + c.getXml().getId() + "/" + c.getXml().getOut().getReport();
			ResponseBuilder response = getDownloadResponse(filename);
		    return response.build();
		}
		else {
			return Response.noContent().build();
		}
	}
	@GET
	@Path("workflow")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getWorkflow() {
		if (securityContext.isUserInRole("bfr2x")) {
			ItemLoader c = Dao.instance.getModel().get(id);
			if (c == null) return Response.noContent().build();
			String filename = Constants.SERVER_UPLOAD_LOCATION_FOLDER + c.getXml().getId() + "/" + c.getXml().getOut().getWorkflow();
			ResponseBuilder response = getDownloadResponse(filename);
			return response.build();
		}
		else {
			return Response.noContent().build();
		}
	}
	@GET
	@Path("report")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getReport() {
		if (securityContext.isUserInRole("bfr2x")) {
			ItemLoader c = Dao.instance.getModel().get(id);
			if (c == null) return Response.noContent().build();
			String filename = Constants.SERVER_UPLOAD_LOCATION_FOLDER + c.getXml().getId() + "/" + c.getXml().getOut().getReport();
			ResponseBuilder response = getDownloadResponse(filename);
		    return response.build();
		}
		else {
			return Response.noContent().build();
		}
	}
	@GET
	@Path("comment")
	@Produces(MediaType.TEXT_PLAIN)
	public String getComment() {
		if (securityContext.isUserInRole("bfr2x")) {
			ItemLoader c = Dao.instance.getModel().get(id);
			if (c != null) {
			    return c.getXml().getOut().getComment();
			}
			else {
				return "";
			}
		}
		else {
			return "";
		}
	}
	
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_XML})
	public Response resultFile(@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition contentDispositionHeader) {

		ResponseX response = new ResponseX();
		Status status = Response.Status.OK;
		response.setAction("UPLOADRESULTS");
		if (securityContext.getUserPrincipal().getName().equals("bfr_admin")) {
			try {
				if (contentDispositionHeader != null) {
					ItemLoader c = Dao.instance.getModel().get(id);
					if (c == null) return Response.noContent().build();
					c.getXml().getOut().setReport("report.xml");
					String filename = Constants.SERVER_UPLOAD_LOCATION_FOLDER + c.getXml().getId() + "/" + c.getXml().getOut().getReport();

					String filePath = c.saveReport(fileInputStream);

					boolean isValid = new XmlValidator().validateViaRequest(filePath);
					response.setSuccess(isValid);
					response.setId(id);
														
					if (!isValid) {
						status = Response.Status.PRECONDITION_FAILED;
						response.setError("'" + filename + "' couldn't be validated!");
					}

					new SendEmail().doSend("'" + filename + "' mit id '" + id + "' wurde validiert: " + isValid, filePath);
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
}