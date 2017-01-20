package de.bund.bfr.lims.importer.validator.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import de.bund.bfr.knime.rest.client.KREST;

/**
 * Servlet implementation class EinsendeValidator
 */
public class EinsendeValidator extends HttpServlet {

	private static final long serialVersionUID = 1L;

    private static final String UPLOAD_DIRECTORY = "uploads";
    private static final int THRESHOLD_SIZE     = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB
     
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EinsendeValidator() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "";
		String message2 = null;

        // checks if the request actually contains upload file
        if (!ServletFileUpload.isMultipartContent(request)) {
        	message = "Request does not contain upload data";
        }
        else {
            // configures upload settings
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(THRESHOLD_SIZE);
            factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
             
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setFileSizeMax(MAX_FILE_SIZE);
            upload.setSizeMax(MAX_REQUEST_SIZE);

            
            // constructs the directory path to store upload file
            String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY + File.separator + System.currentTimeMillis();
           // creates the directory if it does not exist
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
           
            try {
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
                        File storeFile = new File(filePath);
                         
                        String ext = getFileExtension(fileName);
                        if (!ext.equals("xls") && !ext.equals("xlsx")) {
                        	message = "The submitted file '" + fileName + "' has no correct file extension.";
                            break;
                        }

                        // saves the file on disk
                        item.write(storeFile);

                        if (storeFile == null || !storeFile.exists() || !storeFile.isFile()) {
                        	message = "There is an unknown problem with the submitted file '" + fileName + "'<BR>Please write an email to foodrisklabs@bfr.bund.de with your submitted file.";
                            break;
                        }

                    	System.out.println(uploadPath + "\t" + storeFile);
                    	
            	  		if (storeFile != null && storeFile.exists()) {
            	  	    	Map<String, Object> inputs = new HashMap<>();
            	  		    inputs.put("file-upload-211:210", storeFile);
            	  		    Map<String, Boolean> outputs = new HashMap<>(); // doStream bedeutet bei true: file download, bei false: sichtbarkeit im browser
            	  		    outputs.put("XLS-918:917", false);
            	  		    outputs.put("XLS-918:926", false);
            	  		    //outputs.put("json-output-945:947", false);
            	  		    Map<String, String> r = new KREST().doWorkflow("testing/Alex_testing/Proben-Einsendung_Web4_aaw", inputs, outputs, false);
            	  	    	message = r.get("XLS-918:917");	
            	  	    	message2 = r.get("XLS-918:926");	
            	  		}
             
    	                //deregisterDrivers();
    	                deleteUploadDir(uploadDir);
                    }
                }
            } catch (Exception ex) {
            	message = "There was an error: " + ex.getMessage();
             }
        }
        
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        //message = readFile("/Users/arminweiser/Desktop/Desktop.csv", Charset.defaultCharset());//"dtraralla dfgd f fglkdfgl  rkgjfdkjgdlfgj dfgjfdgj dlfkkg fkgj dlkfgjd f!";
        //if (message == null || message.trim().isEmpty()) message = "success!";
        //message = message.trim().replace("\n", "<BR>");
        //System.out.println(message.length());
        //String[] spl = message.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
        Scanner scan = new Scanner(message);
        scan.useDelimiter("\n");
        message = "{\n\t\"data\": [\n";
        while(scan.hasNext()){
        	String str = scan.next();
        	int count = (str.length() - str.replace("\"", "").length()) % 2;
        	if (count == 1) str += scan.next();
        	message += "\t[" + str + "],\n";
            //System.out.println(scan.next());
        }
        scan.close();
        /*
        message = "{\n\t\"data\": [\n";
        for (String str : spl) {
        	message += "\t[" + str + "],\n";
        }
        */
        message = message.substring(0, message.length() - 2) + "\n";
        message += "\t]";
        if (message2 == null) message += "\n}";
        else {
        	message += ",\n";
            message += "\t\"orig\": [\n";
            //spl = message2.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            scan = new Scanner(message2);
            scan.useDelimiter("\n");
            while(scan.hasNext()){
            	String str = scan.next();
            	int count = (str.length() - str.replace("\"", "").length()) % 2;
            	if (count == 1) str += scan.next();
            	message += "\t[" + str + "],\n";
            }
            scan.close();
            /*
           for (String str : spl) {
        	   //if (str.endsWith(",")) str += "\"\"";
        	   //while (str.indexOf(",,") >= 0) str = str.replace(",,", ",\"\",");
        	   message += "\t[" + str + "],\n";
            }
            */
            message = message.substring(0, message.length() - 2) + "\n";
            message += "\t]\n}";
        }
        /*
        {
        	  "data": [
        	    ["", "Kia", "Nissan", "Toyota", "Honda", "Mazda", "Ford"],
        	    ["2012", 10, 11, 12, 13, 15, 16],
        	    ["2013", 10, 11, 12, 13, 15, 16],
        	    ["2014", 10, 11, 12, 13, 15, 16],
        	    ["2015", 10, 11, 12, 13, 15, 16],
        	    ["2016", 10, 11, 12, 13, 15, 16]
        	  ]
        	}
        	*/
        out.println(message);
        out.flush();
	}
	private String readFile(String path, Charset encoding) 
			  throws IOException 
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, encoding);
			}
	private void deleteUploadDir(File uploadDir){
	    try {
	        for (File f : uploadDir.listFiles()) {
	            f.delete();
	        }
	        //uploadDir.delete();
	    } catch (Exception e) {
	        e.printStackTrace(System.err);
	    }
	}
	private String getFileExtension(String fileName) {
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
        return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }
}
