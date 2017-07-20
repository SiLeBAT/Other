package de.bund.bfr.knime.aaw.lims;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.DocumentFactoryHelper;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.format.CellDateFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * This is the model implementation of MyLIMSZooImporter.
 * 
 *
 * @author BfR
 */
public class MyLIMSZooImporterNodeModel extends NodeModel {
    
	static final String XLS_FILE = "xlsfile";

    private final SettingsModelString xlsFile = new SettingsModelString(XLS_FILE, "");

    /**
     * Constructor for the node model.
     */
    protected MyLIMSZooImporterNodeModel() {
        super(0, 1);
    }

    //simple way to check for both types of excel files
    public boolean isXls(InputStream i) throws IOException{
        return POIFSFileSystem.hasPOIFSHeader(i);
    }
    public boolean isXlsx(InputStream i) throws IOException{
        return DocumentFactoryHelper.hasOOXMLHeader(i);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
    	String filename = xlsFile.getStringValue();
		InputStream is = null;
		if (filename.startsWith("http://") || filename.startsWith("knime://") || filename.startsWith("file:/")) {
			URL url = new URL(filename);
			URLConnection uc = url.openConnection();
			is = uc.getInputStream();
		} else {
			is = new FileInputStream(filename);
		}

		Workbook wb = null;
		BufferedInputStream bis = new BufferedInputStream(is);
		if (isXls(bis)) {
			wb = new HSSFWorkbook(bis);
		}
		else if (isXlsx(bis)) {
			wb = new XSSFWorkbook(bis);
		}
		
		Sheet sheet;
		Row row;

		List<DataColumnSpec> columns = new ArrayList<>();
		boolean specNotDefined = true;
		BufferedDataContainer buf = null;
				
		long rowNumber = 0;
		sheet = wb.getSheet("Einsendeformular");
		if (sheet == null) sheet = wb.getSheetAt(0);
		
		if (sheet != null) {
			
			Footer footer = sheet.getFooter();
			String version = footer.getLeft();
			if (version.toLowerCase().startsWith("validiert")) {
				version = getStrVal(sheet.getRow(2).getCell(1));
			}
			this.pushFlowVariableString("Version", version);
			
			int i=38;
	       	for (i=0;i<sheet.getPhysicalNumberOfRows();i++) {
        		row = sheet.getRow(i);
        		if (row != null) {
        			Cell cell = row.getCell(0); // Spalte A
        			String str = getStrVal(cell);
        			if (str != null && str.toLowerCase().indexOf("ihre") >= 0 && str.toLowerCase().indexOf("probe") >= 0 && str.toLowerCase().indexOf("nummer") >= 0) break;
        		}
	       	}
			this.pushFlowVariableInt("TableStart", i+1);
			
			String email = null;
			row = sheet.getRow(20);
			if (row != null) {
    			Cell cell = row.getCell(2); // Spalte C
    			if (cell != null) email = getStrVal(cell);
			}
			this.pushFlowVariableString("EMail", email);

        	for (;i<sheet.getPhysicalNumberOfRows();i++) {
        		row = sheet.getRow(i);
        		if (row != null) {
    				DataCell[] cells = specNotDefined?null:new DataCell[columns.size()];
    				boolean rowNull = true;
            		for (int col = 0;col<(specNotDefined?row.getPhysicalNumberOfCells():columns.size());col++) {
                		Cell cell = row.getCell(col);
                		String str = getStrVal(cell);
                		if (specNotDefined) {
                			if (str == null) str = "Col_" + (col + 1);
                			columns.add(new DataColumnSpecCreator(str, StringCell.TYPE).createSpec());
                		}
                		else {
                			if (str == null) cells[col] = DataType.getMissingCell();
                			else {
                				rowNull = false;
                				cells[col] = new StringCell(str);
                			}
                		}
            		}
            		if (specNotDefined) {
            			DataTableSpec spec = new DataTableSpec(columns.toArray(new DataColumnSpec[0]));
            			buf = exec.createDataContainer(spec);
                		specNotDefined = false;
            		}
            		else if (!rowNull) {
        				RowKey key = RowKey.createRowKey(rowNumber);
        				rowNumber++;
        				DataRow outputRow = new DefaultRow(key, cells);

        				buf.addRowToTable(outputRow);
            		}
        		}
        	}
		}
		wb.close();
		if (buf == null) {
			columns.add(new DataColumnSpecCreator("empty", StringCell.TYPE).createSpec());
			DataTableSpec spec = new DataTableSpec(columns.toArray(new DataColumnSpec[0]));
			buf = exec.createDataContainer(spec);
		}
		buf.close();
		return new BufferedDataTable[]{buf.getTable()};
    }
	private String getStrVal(Cell cell) {
		int maxChars = 100000;
		String result = null;
		try {
			if (cell == null || cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
			} else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
				result = cell.getStringCellValue();
				if (result.equals(".")) result = null;
			} else if (DateUtil.isCellDateFormatted(cell)) {
				String pattern = "dd.mm.yyyy";
				result = new CellDateFormatter(pattern).format(cell.getDateCellValue()); 
			} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC || cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
				try {
					double dbl = cell.getNumericCellValue();
					if (Math.round(dbl) == dbl) result = "" + ((int) dbl);
					else result = "" + cell.getNumericCellValue();
				} catch (Exception e) {
					result = cell.getStringCellValue();
				}
			} else {
				result = cell.toString();
			}
			if (result != null) {
				if (result.equals("#N/A")) {
					result = null;
				} else if (result.length() > maxChars) {
					System.err.println("string too long (" + result.length() + ") - shortened to " + maxChars + " chars... '" + result + "' -> '" + result.substring(0, maxChars)
							+ "'");
					result = result.substring(0, maxChars);
				}
			}
		} catch (Exception e) {
		}
		return result == null ? null : result.trim();
	}

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // TODO: generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {
        return new DataTableSpec[]{null};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
    	xlsFile.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	xlsFile.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	xlsFile.validateSettings(settings);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
    }

}

