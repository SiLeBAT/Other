package de.bund.bfr.gwt.krise.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

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

			FileItemIterator iterator = upload.getItemIterator(request);

			HSSFWorkbook uploadXls = null; 
			boolean forward = true;
			String deliveryId="", fromId="", toId="";
			
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				InputStream stream = item.openStream();

				if (item.isFormField()) {
					String name = item.getFieldName();
					String value = Streams.asString(stream);
					System.err.println("Got a form field: " + name + ": " + value);
					if (name.equals("forward") || name.equals("backward")) {
						forward = name.equals("forward");
					}
					else if (name.equals("deliveryId")) {
						deliveryId = value;
					}
					else if (name.equals("fromId")) {
						fromId = value;
					}
					else if (name.equals("toId")) {
						toId = value;
					}
				} else if (!item.getName().isEmpty()) {
					POIFSFileSystem fs = new POIFSFileSystem(stream);
					uploadXls = new HSSFWorkbook(fs);

					System.err.println("Got an uploaded file: " + item.getFieldName() + ", name = " + item.getName());
				}
			}
			
			if (uploadXls == null) {
				response.setContentType("application/octet-stream");
				response.setHeader("Content-Disposition", "attachment;filename=Lieferliste_" + (forward ? "forward_" : "backward_") + deliveryId + ".xls");
				OutputStream out = response.getOutputStream();

				HSSFWorkbook workbook = createXls(forward, deliveryId, fromId, toId);
				
				workbook.write(out);
				out.flush();
				out.close();
			}
			else {
				importXls(uploadXls, forward, deliveryId, fromId, toId);

				response.setContentType("text/plain");
				response.getOutputStream().write("subbi".getBytes());
			}

		} catch (FileUploadException e) {
			e.printStackTrace();
		}
	}

	private void importXls(HSSFWorkbook wb, boolean forward, String deliveryId, String fromId, String toId) throws IOException {
		for (int i = 0; i < wb.getNumberOfSheets(); i++) {
			HSSFSheet sheet = wb.getSheetAt(i);
			String sheetName = sheet.getSheetName();
			if (sheetName.equals("Lieferliste")) {
				int numRows = sheet.getLastRowNum() + 1;
				if (numRows > 3) {
					HSSFRow row1 = sheet.getRow(1);
					int numCols = row1.getLastCellNum() + 1;
					HashMap<String, String> station = new HashMap<String, String>(); 
					HashMap<String, String> product = new HashMap<String, String>(); 
					HashMap<String, String> lot = new HashMap<String, String>(); 
					HashMap<String, String> delivery = new HashMap<String, String>(); 
					for (int j=3;j<numRows;j++) {
						HSSFRow row = sheet.getRow(j);
						if (row != null) {
							for (int k = 0; k < numCols; k++) {
								String fieldName = DBKernel.getStrVal(row1.getCell(k), 1000);
								if (fieldName != null) {
									fieldName = fieldName.trim();
									if (row.getCell(k) != null) {
										String value = DBKernel.getStrVal(row.getCell(k), 1000);
										if (fieldName.equals("Produktname")) {
											product.put("Bezeichnung", value);
										}
										else if (fieldName.equals("Produkt_Nr")) {
											product.put("Artikelnummer", value);
										}
										else if (fieldName.equals("Tag_Empfang")) {
											delivery.put("dd_day", value);
										}
										else if (fieldName.equals("Monat_Empfang")) {
											delivery.put("dd_month", value);
										}
										else if (fieldName.equals("Jahr_Empfang")) {
											delivery.put("dd_year", value);
										}
										else if (fieldName.equals("Gewicht")) {
											delivery.put("Unitmenge", value);
											delivery.put("UnitEinheit", "kg");
										}
										else if (fieldName.equals("Los-Nr.")) {
											lot.put("ChargenNr", value);
										}
										else if (fieldName.equals("Name Unternehmen")) {
											station.put("Name", value);
										}
										else if (fieldName.equals("Straße_Adresse")) {
											station.put("Strasse", value);
										}
										else if (fieldName.equals("HausNr.")) {
											station.put("Hausnummer", value);
										}
										else if (fieldName.equals("PLZ")) {
											station.put("PLZ", value);
										}
										else if (fieldName.equals("Stadt")) {
											station.put("Ort", value);
										}
										else if (fieldName.equals("Landkreis")) {
											station.put("District", value);
										}
										else if (fieldName.equals("Bundesland")) {
											station.put("Bundesland", value);
										}
										else if (fieldName.equals("Tätigkeit")) {
											station.put("Betriebsart", value);
										}
									}
								}
							}
							
							Integer sid = insertData("Station", station);
							product.put("Station", forward ? toId : sid + "");
							Integer pid = insertData("Produktkatalog", product);
							lot.put("Artikel", ""+pid);
							Integer lid = insertData("Chargen", lot);
							delivery.put("Charge", ""+lid);
							delivery.put("Empfänger", forward ? sid + "" : fromId);
							Integer did = insertData("Lieferungen", delivery);
							
							HashMap<String, String> data = new HashMap<String, String>();
							data.put("Zutat", forward ? deliveryId : ""+did); data.put("Produkt", forward ? ""+lid : getCharge(deliveryId));
							insertData("ChargenVerbindungen", data);
						}
					}
				}
			}
		}
	}
	private Integer insertData(String tablename, HashMap<String, String> data) {
		Integer result = null;
		String fields = "";
		String vals = "";
		for (String field : data.keySet()) {
			fields += "," + DBKernel.delimitL(field);
			vals += ",'" + data.get(field) + "'";
		}
		if (fields.length() > 0) {
			String sql = "INSERT INTO " + DBKernel.delimitL(tablename) + " (" + fields.substring(1) + ") VALUES (" + vals.substring(1) + ")";
			result = DBKernel.getNewId(sql);
		}
		return result;
	}
	private String getCharge(String deliveryId) {
		return DBKernel.getValue4Id("Lieferungen", "Charge", deliveryId);
	}
	private HSSFWorkbook createXls(boolean forward, String deliveryId, String fromId, String toId) throws IOException {
		InputStream is = getServletContext().getResourceAsStream("/db/Lieferliste.xls");
		POIFSFileSystem fs = new POIFSFileSystem(is);
		HSSFWorkbook workbook = new HSSFWorkbook(fs);
		HSSFSheet worksheet = workbook.getSheetAt(0);
		HSSFRow row = worksheet.getRow(0);
		String name = DBKernel.getValue4Id("Station", "Name", forward ? toId : fromId);
		row.getCell(2).setCellValue(name + " bzgl. der Lieferung mit der Id " + deliveryId);
		row.getCell(7).setCellValue(forward ? "Empfänger" : "Lieferant:");
		row.getCell(8).setCellValue("je Produkt eine neue Zeile in Liste samt Angaben des " + (forward ? "Empfängers" : "Lieferanten") + " einfügen");
		return workbook;
/*
		HSSFRow row = worksheet.createRow(0);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue("Lieferliste von Unternehmen:");
		cell.setCellStyle(getStyle(workbook, HSSFColor.GOLD.index, true, 12));
		cell = row.createCell(1);
		cell.setCellStyle(getStyle(workbook, HSSFColor.GOLD.index, true, 12));
		cell = row.createCell(2);
		cell.setCellValue("Heinz Tummel GmbH & Co KG");
		cell.setCellStyle(getStyle(workbook, -1, true, 12));
		row.createCell(3);
		row.createCell(4);
		row.createCell(5);
		row.createCell(6);
		row.createCell(7);
		cell = row.createCell(8);
		cell.setCellValue("Lieferant:"); // Empfänger je nachdem, ob forward oder backward
		cell.setCellStyle(getStyle(workbook, HSSFColor.TURQUOISE.index, true, 12));
		cell = row.createCell(9);
		cell.setCellValue("je Produkt eine neue Zeile in Liste samt Angaben des Lieferanten einfügen"); // Empfänger je nachdem, ob forward oder backward
		cell.setCellStyle(getStyle(workbook, HSSFColor.TURQUOISE.index, true, 12));

		row = worksheet.createRow(1);
		cell = row.createCell(0);
		cell.setCellValue("Produktname"); 
		cell.setCellStyle(getStyle(workbook, HSSFColor.PINK.index, true, 10));
		cell = row.createCell(1);
		cell.setCellValue("Produkt_Nr"); 
		cell.setCellStyle(getStyle(workbook, HSSFColor.PINK.index, true, 10));
		cell = row.createCell(2);
		cell.setCellValue("Empfang_Tag"); 
		cell.setCellStyle(getStyle(workbook, HSSFColor.PINK.index, true, 10));
		cell = row.createCell(3);
		cell.setCellValue("Empfang_Monat"); 
		cell.setCellStyle(getStyle(workbook, HSSFColor.PINK.index, true, 10));
		cell = row.createCell(4);
		cell.setCellValue("Empfang_Jahr"); 
		cell.setCellStyle(getStyle(workbook, HSSFColor.PINK.index, true, 10));
		cell = row.createCell(5);
		cell.setCellValue("Gewicht"); 
		cell.setCellStyle(getStyle(workbook, HSSFColor.PINK.index, true, 10));
		cell = row.createCell(6);
		cell.setCellValue("Los-Nr."); 
		cell.setCellStyle(getStyle(workbook, HSSFColor.PINK.index, true, 10));
		cell = row.createCell(7);
		cell.setCellValue("Name Lieferant"); // Empfänger
		cell.setCellStyle(getStyle(workbook, HSSFColor.TURQUOISE.index, true, 10));
		cell = row.createCell(8);
		cell.setCellValue("Straße"); 
		cell.setCellStyle(getStyle(workbook, HSSFColor.TURQUOISE.index, true, 10));
		cell = row.createCell(9);
		cell.setCellValue("HausNr."); 
		cell.setCellStyle(getStyle(workbook, HSSFColor.TURQUOISE.index, true, 10));
		cell = row.createCell(10);
		cell.setCellValue("PLZ"); 
		cell.setCellStyle(getStyle(workbook, HSSFColor.TURQUOISE.index, true, 10));
		cell = row.createCell(11);
		cell.setCellValue("Stadt"); 
		cell.setCellStyle(getStyle(workbook, HSSFColor.TURQUOISE.index, true, 10));
		cell = row.createCell(12);
		cell.setCellValue("Landkreis"); 
		cell.setCellStyle(getStyle(workbook, HSSFColor.TURQUOISE.index, true, 10));
		cell = row.createCell(13);
		cell.setCellValue("Bundesland"); 
		cell.setCellStyle(getStyle(workbook, HSSFColor.TURQUOISE.index, true, 10));
		cell = row.createCell(14);
		cell.setCellValue("Tätigkeit"); 
		cell.setCellStyle(getStyle(workbook, HSSFColor.TURQUOISE.index, true, 10));

		row = worksheet.createRow(2);
		cell = row.createCell(0);
		cell.setCellValue("z. B.\nMastschwein,\nLäufer,\nSauen,\nSchweinehälften"); 
		cell.setCellStyle(getStyle(workbook, HSSFColor.PINK.index, false, 10));
		cell = row.createCell(1);
		cell.setCellValue("bzw.\nlfd.-Nr. Schlachtung"); 
		cell.setCellStyle(getStyle(workbook, HSSFColor.PINK.index, false, 10));
		cell = row.createCell(2);
		cell.setCellValue("Tag der Schlachtung oder Anlieferung an Schlachthof"); 
		cell.setCellStyle(getStyle(workbook, HSSFColor.PINK.index, false, 10));
		cell = row.createCell(3);
		cell.setCellValue("Monat der Schlachtung oder Anlieferung an Schlachthof"); 
		cell.setCellStyle(getStyle(workbook, HSSFColor.PINK.index, false, 10));
		cell = row.createCell(4);
		cell.setCellValue("Jahr der Schlachtung oder Anlieferung an Schlachthof"); 
		cell.setCellStyle(getStyle(workbook, HSSFColor.PINK.index, false, 10));
		cell = row.createCell(5);
		cell.setCellValue("in kg"); 
		cell.setCellStyle(getStyle(workbook, HSSFColor.PINK.index, false, 10));
		cell = row.createCell(6);
		cell.setCellValue("bzw.\nVVVO-Nr."); 
		cell.setCellStyle(getStyle(workbook, HSSFColor.PINK.index, false, 10));
		cell = row.createCell(7);
		cell.setCellValue("z. B.\nFleisch GmbH"); // Empfänger
		cell.setCellStyle(getStyle(workbook, HSSFColor.TURQUOISE.index, false, 10));
		cell = row.createCell(8);
		cell.setCellValue("z. B.\nMusterstraße"); 
		cell.setCellStyle(getStyle(workbook, HSSFColor.TURQUOISE.index, false, 10));
		cell = row.createCell(9);
		cell.setCellValue("z. B.\n13"); 
		cell.setCellStyle(getStyle(workbook, HSSFColor.TURQUOISE.index, false, 10));
		cell = row.createCell(10);
		cell.setCellValue("z. B.\n10117"); 
		cell.setCellStyle(getStyle(workbook, HSSFColor.TURQUOISE.index, false, 10));
		cell = row.createCell(11);
		cell.setCellValue("z. B. Berlin"); 
		cell.setCellStyle(getStyle(workbook, HSSFColor.TURQUOISE.index, false, 10));
		cell = row.createCell(12);
		cell.setCellValue("z. B.\nMusterlandkreis"); 
		cell.setCellStyle(getStyle(workbook, HSSFColor.TURQUOISE.index, false, 10));
		cell = row.createCell(13);
		cell.setCellValue("Kurz z. B.\nBB, TH, SN"); 
		cell.setCellStyle(getStyle(workbook, HSSFColor.TURQUOISE.index, false, 10));
		cell = row.createCell(14);
		cell.setCellValue("z. B. Primärproduktion,\nZerlegebetrieb, Schlachthof, Zerlegebetrieb, Händler,Transporteur"); 	
		cell.setCellStyle(getStyle(workbook, HSSFColor.TURQUOISE.index, false, 10));
		
		for (int i=0;i<row.getLastCellNum();i++) worksheet.setColumnWidth(i, 3000);//worksheet.autoSizeColumn(i);
		*/
		/*
		cellB1.setCellValue("Goodbye");
		cellStyle = workbook.createCellStyle();
		cellStyle.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
		cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		cellB1.setCellStyle(cellStyle);
		HSSFCell cellD1 = row.createCell(3);
		cellD1.setCellValue(new Date());
		cellStyle = workbook.createCellStyle();
		cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));
		cellD1.setCellStyle(cellStyle);
		*/
	}
	/*
	private HSSFCellStyle getStyle(HSSFWorkbook workbook, int bgColor, boolean bold, int size) {
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		if (bgColor >= 0) {
			cellStyle.setFillForegroundColor((short) bgColor);
			cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		}
		HSSFFont font = workbook.createFont();
		if (bold) font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		if (size > 0) font.setFontHeightInPoints((short) size);
		cellStyle.setFont(font);			
		return cellStyle;
	}
	*/
}