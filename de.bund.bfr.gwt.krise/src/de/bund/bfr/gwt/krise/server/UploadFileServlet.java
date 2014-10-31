package de.bund.bfr.gwt.krise.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * servlet to handle file upload requests
 * 
 * @author hturksoy
 * 
 */
public class UploadFileServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6288041120341021693L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			ServletFileUpload upload = new ServletFileUpload();
			response.setContentType("text/plain");

			FileItemIterator iterator = upload.getItemIterator(request);

			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				InputStream stream = item.openStream();

				if (item.isFormField()) {
					System.err.println("Got a form field: " + item.getFieldName() + " " + Streams.asString(stream));

				} else {
					System.err.println("Got an uploaded file: " + item.getFieldName() + ", name = " + item.getName());
					importXls(stream);
					response.getOutputStream().write("subbi".getBytes());
				}

			}
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
	}
	private void importXls(InputStream is) throws IOException {
		POIFSFileSystem fs = new POIFSFileSystem(is);
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFSheet sheet;
		HSSFRow row;

		for (int i = 0; i < wb.getNumberOfSheets(); i++) {
			sheet = wb.getSheetAt(i);
			String sheetName = sheet.getSheetName();
			
			int numRows = sheet.getLastRowNum();
			row = sheet.getRow(0);
			int numCols = row.getLastCellNum();
			String[] colnames = new String[numCols];
			for (int j = 0; j < numCols; j++) {
				String fieldName = row.getCell(j).getStringCellValue();
				colnames[j] = fieldName;
				System.err.println(colnames[j]);
			}
		}
	}
}