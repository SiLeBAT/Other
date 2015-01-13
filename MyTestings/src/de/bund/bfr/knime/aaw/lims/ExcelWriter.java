package de.bund.bfr.knime.aaw.lims;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelWriter {

	XSSFWorkbook workbook;
	XSSFSheet sheet;
	XSSFCellStyle defaultStyle;

	public ExcelWriter(LinkedHashSet<List<Object>> data, Object css) {
		// Blank workbook
		workbook = new XSSFWorkbook();
		// Create a blank sheet
		sheet = workbook.createSheet("default");
		defaultStyle = workbook.createCellStyle();

		// Iterate over data and write to sheet
		int rownum = 0;
		for (List<Object> rowData : data) {
			XSSFRow row = sheet.createRow(rownum++);
			int cellnum = 0;
			for (Object obj : rowData) {
				XSSFCell cell = row.createCell(cellnum++);
				if (obj instanceof String)
					cell.setCellValue((String) obj);
				else if (obj instanceof Integer)
					cell.setCellValue((Integer) obj);
				else if (obj instanceof Double)
					cell.setCellValue((Double) obj);
				else
					System.err.println("Unsupported type: " + obj);
			}
		}
	}

	public void save(String filename) {
		try {
			// Write the workbook in file system
			FileOutputStream out = new FileOutputStream(new File(filename));
			workbook.write(out);
			out.close();
			System.out.println(filename + " written successfully on disk.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setStyle(boolean isRow, int num, boolean isBold, boolean isCenter, boolean hasRightBorder, boolean hasBottomBorder, String dataFormat) {
		XSSFFont font = workbook.createFont();
		if (isBold) font.setBold(isBold);

		XSSFCellStyle style = (XSSFCellStyle) defaultStyle.clone();
		//style.setFillForegroundColor(new XSSFColor(Color.ORANGE));
		//style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
		if (isCenter) style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		style.setFont(font);
		
		if (hasRightBorder) style.setBorderRight(XSSFCellStyle.BORDER_MEDIUM);
		if (hasBottomBorder) style.setBorderBottom(XSSFCellStyle.BORDER_MEDIUM);
		
		if (dataFormat != null) {
			XSSFCreationHelper createHelper = workbook.getCreationHelper();
			style.setDataFormat(createHelper.createDataFormat().getFormat(dataFormat));
		}

		if (isRow) {
			XSSFRow row = sheet.getRow(num);
			if (row != null) {
				for (int i=row.getFirstCellNum();i<=row.getLastCellNum();i++) {
					XSSFCell cell = row.getCell(i);
					setS(cell, style);
				}				
			}
		}
		else {
			for (int i=0;i<=sheet.getLastRowNum();i++) {
				XSSFRow row = sheet.getRow(i);
				XSSFCell cell = row.getCell(num);
				setS(cell, style);
			}
		}
	}
	private void setS(XSSFCell cell, XSSFCellStyle style) {
		if (cell != null) {
			XSSFCellStyle cstyle = (XSSFCellStyle) cell.getCellStyle().clone();
			if (cstyle.equals(defaultStyle)) {
				cell.setCellStyle(style);
			}
			else {
				if (style.getFont().getBold() != defaultStyle.getFont().getBold()) cstyle.setFont(style.getFont());
				if (style.getAlignment() != defaultStyle.getAlignment()) cstyle.setAlignment(style.getAlignment());
				if (style.getBorderRight() != defaultStyle.getBorderRight()) cstyle.setBorderRight(style.getBorderRight());
				if (style.getBorderBottom() != defaultStyle.getBorderBottom()) cstyle.setBorderBottom(style.getBorderBottom());
				if (style.getDataFormat() != defaultStyle.getDataFormat()) cstyle.setDataFormat(style.getDataFormat());
				cell.setCellStyle(cstyle);
			}
			
		}
	}
	public void autoSizeColumns(int numCols) {
		for (int i=0;i<numCols;i++) sheet.autoSizeColumn(i);		
	}
}