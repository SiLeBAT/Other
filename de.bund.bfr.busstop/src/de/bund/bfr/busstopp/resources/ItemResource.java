package de.bund.bfr.busstopp.resources;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import de.bund.bfr.busstopp.Constants;
import de.bund.bfr.busstopp.dao.Dao;
import de.bund.bfr.busstopp.dao.ItemLoader;
import de.bund.bfr.busstopp.model.Item;
import de.bund.bfr.busstopp.model.ResponseX;

import javax.ws.rs.core.UriInfo;

public class ItemResource {
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
    @Context
    SecurityContext securityContext;
    
    String environment;
	Long id;

	public ItemResource(UriInfo uriInfo, Request request, SecurityContext securityContext, String environment, Long id) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.securityContext = securityContext;
		this.environment = environment;
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
		Map<Long, ItemLoader> map = Dao.instance.getModel(environment);
		if (map == null) {
			throw new RuntimeException("Get: Environment '" + environment + "' not found");
		}
		ItemLoader item = map.get(id);
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
			Map<Long, ItemLoader> map = Dao.instance.getModel(environment);
			Map<Long, ItemLoader> mapBfR = Dao.instance.getModel("bfr");
			if (map != null || mapBfR != null) {
				String env = environment;
				ItemLoader c = null;
				if (map != null) c = map.get(id);
				if (c == null && mapBfR != null) {
					c = mapBfR.get(id);
					env = "bfr";
				}
				if (c != null) {
					try {
						c.delete();
					} catch (IOException e) {
						e.printStackTrace();
						response.setSuccess(false);
						status = Response.Status.INTERNAL_SERVER_ERROR;
						response.setError(e.getMessage());
					}
					Dao.instance.delete(env, id, c);
					response.setSuccess(true);
				}
				else  {
					response.setSuccess(false);
					status = Response.Status.PRECONDITION_FAILED;
					response.setError("ID not found");
				}
			}
			else  {
				response.setSuccess(false);
				status = Response.Status.PRECONDITION_FAILED;
				response.setError("Environment not found");
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
			Map<Long, ItemLoader> map = Dao.instance.getModel(environment);
			if (map != null) {
	 			ItemLoader c = map.get(id);
	 			if (c == null) return Response.noContent().build();
				String filename = Constants.SERVER_UPLOAD_LOCATION_FOLDER + c.getXml().getId() + "/" + c.getXml().getIn().getFilename();
	 			ResponseBuilder response = getDownloadResponse(filename);
	 		    return response.build();
			}
			else {
				return Response.noContent().build();
			}
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
			Map<Long, ItemLoader> map = Dao.instance.getModel(environment);
			if (map == null) return "";
			ItemLoader c = map.get(id);
			if (c != null) {
			    return c.getXml().getIn().getComment();
			}
			else {
				return "";
			}
		}
		else {
			return "";
		}
	}	
	
	@DELETE
	@Path("bin")
	@Produces({ MediaType.APPLICATION_XML})
	public Response clearBin(@QueryParam("environment") String environment) {
		ResponseX response = new ResponseX();
		Status status = Response.Status.OK;
		response.setAction("CLEARITEM");
		if (securityContext.isUserInRole("bfr")) {
			int numDeleted = Dao.instance.clearBin(environment, id);
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