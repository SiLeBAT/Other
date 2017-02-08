package de.bund.bfr.lims.importer.validator.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.bund.bfr.knime.rest.client.KREST;

/**
 * Servlet implementation class EinsendeValidator
 */
public class EinsendeValidator extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String UPLOAD_DIRECTORY = "uploads";
	private static final int THRESHOLD_SIZE = 1024 * 1024 * 3; // 3MB
	private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
	private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB

	private static String sWorkflowPath = "";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EinsendeValidator() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String message = "";

		System.err.println("dp_start: " + (""+(System.currentTimeMillis() / 1000)).substring(6));
		Map<String, Object> inputs = new HashMap<>();
		File uploadDir = null;

		try {
			// checks if the request actually contains upload file
			if (request.getContentType().equals("application/json")) {
				String sObj = null;
				StringBuffer jb = new StringBuffer();
				String line = null;
				try {
					BufferedReader reader = request.getReader();
					while ((line = reader.readLine()) != null)
						jb.append(line);
				} catch (Exception ex) {
					message = "There was an error: " + ex.getMessage();
					StringWriter sw = new StringWriter();
					ex.printStackTrace(new PrintWriter(sw));
					message += sw.toString();
					System.err.println(message);
				}

				try {
					sObj = jb.toString();
				} catch (Exception e) {
					// crash and burn
					message = "Error parsing JSON request string";
				}

				inputs.put("json-input-1147", sObj);
			} else if (!ServletFileUpload.isMultipartContent(request)) {
				message = "Request does not contain upload data";
			} else {
				// configures upload settings
				DiskFileItemFactory factory = new DiskFileItemFactory();
				factory.setSizeThreshold(THRESHOLD_SIZE);
				factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

				ServletFileUpload upload = new ServletFileUpload(factory);
				upload.setFileSizeMax(MAX_FILE_SIZE);
				upload.setSizeMax(MAX_REQUEST_SIZE);

				// constructs the directory path to store upload file
				String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY
						+ File.separator + System.currentTimeMillis();
				// creates the directory if it does not exist
				uploadDir = new File(uploadPath);
				if (!uploadDir.exists()) {
					uploadDir.mkdirs();
				}
				File storeFile = null;
				// parses the request's content to extract file data
				List<?> formItems = upload.parseRequest(request);
				Iterator<?> iter = formItems.iterator();

				// iterates over form's fields
				while (iter.hasNext()) {
					DiskFileItem item = (DiskFileItem) iter.next();
					// processes only fields that are not form fields
					if (!item.isFormField()) {
						String fileName = new File(item.getName()).getName();
						if (fileName.trim().length() == 0) {
							message = "No file to validate!";
							break;
						}
						String filePath = uploadPath + File.separator + fileName;
						storeFile = new File(filePath);

						String ext = getFileExtension(fileName);
						if (!ext.equals("xls") && !ext.equals("xlsx")) {
							message = "The submitted file '" + fileName + "' has no correct file extension.";
							break;
						}

						// saves the file on disk
						item.write(storeFile);

						if (storeFile == null || !storeFile.exists() || !storeFile.isFile()) {
							message = "There is an unknown problem with the submitted file '" + fileName
									+ "'<BR>Please write an email to foodrisklabs@bfr.bund.de with your submitted file.";
							break;
						}

						System.out.println(uploadPath + "\t" + storeFile);

					} else {
						if (item.getFieldName().equals("workflowname")) {
							sWorkflowPath = item.getString();
						}
					}
				}

				// go on and do!
				if (storeFile != null && storeFile.exists()) {
					inputs.put("file-upload-211:210", storeFile);
				}
			}

			System.err.println("sag_start: " + (""+(System.currentTimeMillis() / 1000)).substring(6));
			message = sendAndGet(inputs);
			System.err.println("sag_end: " + (""+(System.currentTimeMillis() / 1000)).substring(6));
			
		} catch (Exception ex) {
			message = "There was an error: " + ex.getMessage();
			StringWriter sw = new StringWriter();
			ex.printStackTrace(new PrintWriter(sw));
			message += sw.toString();
			System.err.println(message);
		}
		// deregisterDrivers();
		deleteUploadDir(uploadDir);

		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.println(message);
		out.flush();
		System.err.println("dp_end: " + (""+(System.currentTimeMillis() / 1000)).substring(6));
	}

	private String sendAndGet(Map<String, Object> inputs)
			throws ParserConfigurationException, SAXException, IOException, URISyntaxException, ParseException {
		String username = "";
		String password = "";
		InputStream in = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("/de/bund/bfr/lims/importer/validator/servlet/userdata.xml");
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(in);
		username = document.getElementsByTagName("user").item(0).getTextContent();
		password = document.getElementsByTagName("password").item(0).getTextContent();

		System.err.println(sWorkflowPath);
		Map<String, Boolean> outputs = new HashMap<>(); // doStream bedeutet bei true: file download, bei false: sichtbarkeit im browser
		outputs.put("json-output-918:4", false);
		System.err.println("dw_start: " + (""+(System.currentTimeMillis() / 1000)).substring(6));
		Map<String, String> r = new KREST().doWorkflow(sWorkflowPath, username, password, inputs, outputs, true);
		System.err.println("dw_end: " + (""+(System.currentTimeMillis() / 1000)).substring(6));
		return r.get("json-output-918:4");
	}

	private String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	private void deleteUploadDir(File uploadDir) {
		if (uploadDir != null) {
			try {
				for (File f : uploadDir.listFiles()) {
					f.delete();
				}
				// uploadDir.delete();
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}
	}

	private String getFileExtension(String fileName) {
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		else
			return "";
	}
}
