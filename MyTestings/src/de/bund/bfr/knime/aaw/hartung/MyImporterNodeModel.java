package de.bund.bfr.knime.aaw.hartung;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.BooleanCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.IntCell;
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
 * This is the model implementation of MyImporter.
 * 
 *
 * @author Armin Weiser
 */
public class MyImporterNodeModel extends NodeModel {
    
	static final String XLS_FILE = "xlsfile";
	
    private final SettingsModelString xlsFile = new SettingsModelString(XLS_FILE, "");

    /**
     * Constructor for the node model.
     */
    protected MyImporterNodeModel() {
        super(0, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
    	String filename = xlsFile.getStringValue();
		InputStream is = null;
		System.out.println(filename);
		if (filename.startsWith("http://")) {
			URL url = new URL(filename);
			URLConnection uc = url.openConnection();
			is = uc.getInputStream();
		} else {
			is = new FileInputStream(filename);
		}

		POIFSFileSystem fs = new POIFSFileSystem(is);
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFSheet sheet;
		HSSFRow row;

		BufferedDataContainer buf = exec.createDataContainer(getSpec());
		int rowNumber = 0;
		sheet = wb.getSheet("import");
		if (sheet != null) {
			for (int i=0; i<wb.getNumberOfNames(); i++) {
	            Name name = wb.getNameAt(i);
	            if (!"import".equals(name.getSheetName())) continue;
	            if (name.getNameName().equals("Print_Area")) continue;

	            try {
	            	if (!name.getRefersToFormula().endsWith("!#REF!") && !name.getRefersToFormula().equals("\"Dummy\"")) {
		                AreaReference area = new AreaReference(name.getRefersToFormula());
		                if (area.isSingleCell()) {
		                    CellReference crList[] = area.getAllReferencedCells();
		                    if (crList[0].getCol() == 1) {
		        				int rowIndex = crList[0].getRow();
		                    	row = sheet.getRow(rowIndex);
		                    	//Jahr
		                    	Integer jahr = null;
		                    	HSSFCell cell = row.getCell(11); // Spalte L
		                    	String str = getStrVal(cell);
		                    	if (str != null && str.trim().length() > 2) jahr = Integer.parseInt(str.substring(2).trim());
		                    	//Bundesland
		                    	String bl = null;
		                    	cell = row.getCell(14); // Spalte O
		                    	str = getStrVal(cell);
		                    	if (str != null && str.trim().length() > 2) bl = str.substring(2).trim();
		                    	if (bl == null) continue;
	                    		// Ansprechpartner, Labname, AnsprechpartnerMail, Akkreditiert, Staat, Saison, Agents
		                    	String ansprechpartner = null;
		                    	String laborname = null;
		                    	String ansprechpartnerMail = null;
		                    	Boolean akkreditiert = null;
		                    	String staat = null;
		                    	String saison = null;
		                    	String agent = null;
		                    	String agent1 = null;
		                    	String agent2 = null;
		                    	int firstDataRow = 0;
		                    	for (int plusIndex = 1;plusIndex<20;plusIndex++) {
			                    	row = sheet.getRow(rowIndex + plusIndex);
			                    	cell = row.getCell(12); // Spalte M
			                    	str = getStrVal(cell);
			                    	if (str != null && str.trim().length() > 2 && str.startsWith("**")) ansprechpartner = str.substring(2).trim();
			                    	if (str != null && str.trim().length() > 2 && str.startsWith("##")) laborname = str.substring(2).trim();
			                    	if (str != null && str.trim().length() > 0 && str.indexOf("@") > 0) ansprechpartnerMail = str.trim();
			                    	cell = row.getCell(15); // Spalte P
			                    	if (cell != null && cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
			                    		akkreditiert = cell.getBooleanCellValue();
			                    	}
			                    	cell = row.getCell(1); // Spalte B
			                    	str = getStrVal(cell);
			                    	if (str != null && str.trim().length() > 2 && str.startsWith("**")) staat = str.substring(2).trim();
			                    	cell = row.getCell(2); // Spalte C
			                    	str = getStrVal(cell);
			                    	if (str != null && str.trim().length() > 2 && str.startsWith("**")) saison = str.substring(2).trim();
			                    	cell = row.getCell(7); // Spalte H
			                    	str = getStrVal(cell);
			                    	if (str != null && str.trim().length() > 3 && str.startsWith("**")) agent = str.substring(2).trim();
			                    	cell = row.getCell(8); // Spalte I
			                    	str = getStrVal(cell);
			                    	if (str != null && str.trim().length() > 3 && str.startsWith("**")) agent1 = str.substring(2).trim();
			                    	cell = row.getCell(9); // Spalte J
			                    	str = getStrVal(cell);
			                    	if (str != null && str.trim().length() > 3 && str.startsWith("**")) agent2 = str.substring(2).trim();
			                    	if (plusIndex > 5) {
				                    	cell = row.getCell(0); // Spalte A
				                    	str = getStrVal(cell);
				                    	if (str != null && str.trim().length() > 1) {
				                    		firstDataRow = plusIndex;
				                    		break;
				                    	}
			                    	}			                    	
		                    	}
		                    	
		                    	if (firstDataRow == 0) {
		                    		System.err.println("firstDataRow = 0...");
		                    		break;
		                    	}
		        				int plusIndex = firstDataRow;
		                    	for (;;plusIndex++) {
			                    	// Source, Methode, Grund, Ebene
			                    	row = sheet.getRow(rowIndex + plusIndex);
			                    	if (row == null) break;
			                    	cell = row.getCell(8); // Spalte I
			                    	String bitte = getStrVal(cell);
			                    	cell = row.getCell(11); // Spalte L
			                    	String astJahr = getStrVal(cell);
			                    	cell = row.getCell(13); // Spalte N
			                    	String bland = getStrVal(cell);
			                    	if (bitte != null && bitte.trim().equals("bitte ggf. Zeilen einfügen")
			                    			|| astJahr != null && astJahr.trim().equals("**" + jahr)
			                    			|| bland != null && bland.trim().equals("Bundesland:")) break;
			                    	cell = row.getCell(0); // Spalte A
			                    	if (cell == null || getStrVal(cell) == null) cell = row.getCell(1); // Spalte B
			                    	if (cell == null) {
			                    	}
			                    	else {
				                    	String Source = null;
				                    	str = getStrVal(cell);
				                    	if (str != null && str.trim().length() > 0) Source = str.trim();
				                    	String Methode = null;
				                    	cell = row.getCell(3); // Spalte D
				                    	str = getStrVal(cell);
				                    	if (str != null && str.trim().length() > 0) Methode = str.trim();
				                    	String Grund = null;
				                    	cell = row.getCell(4); // Spalte E
				                    	str = getStrVal(cell);
				                    	if (str != null && str.trim().length() > 0) Grund = str.trim();
				                    	String Ebene = null;
				                    	cell = row.getCell(5); // Spalte F
				                    	str = getStrVal(cell);
				                    	if (str != null && str.trim().length() > 0) Ebene = str.trim();
				                    	String Anzahl = null;
				                    	cell = row.getCell(6); // Spalte G
				                    	str = getStrVal(cell);
				                    	if (str != null && str.trim().length() > 0) Anzahl = str.trim();
				                    	if (Anzahl == null) continue;
				                    	String pos = null;
				                    	cell = row.getCell(7); // Spalte H
				                    	str = getStrVal(cell);
				                    	if (str != null && str.trim().length() > 0) pos = str.trim();
				                    	String pos1 = null;
				                    	cell = row.getCell(8); // Spalte I
				                    	str = getStrVal(cell);
				                    	if (str != null && str.trim().length() > 0) pos1 = str.trim();
				                    	String pos2 = null;
				                    	cell = row.getCell(9); // Spalte J
				                    	str = getStrVal(cell);
				                    	if (str != null && str.trim().length() > 0) pos2 = str.trim();
				                    	String agent3 = null;
				                    	cell = row.getCell(10); // Spalte K
				                    	str = getStrVal(cell);
				                    	if (str != null && str.trim().length() > 0) agent3 = str.trim();
				                    	String pos3 = null;
				                    	cell = row.getCell(11); // Spalte L
				                    	str = getStrVal(cell);
				                    	if (str != null && str.trim().length() > 0) pos3 = str.trim();
				                    	String agent4 = null;
				                    	cell = row.getCell(12); // Spalte M
				                    	str = getStrVal(cell);
				                    	if (str != null && str.trim().length() > 0) agent4 = str.trim();
				                    	String pos4 = null;
				                    	cell = row.getCell(13); // Spalte N
				                    	str = getStrVal(cell);
				                    	if (str != null && str.trim().length() > 0) pos4 = str.trim();
				                    	String agent5 = null;
				                    	cell = row.getCell(14); // Spalte O
				                    	str = getStrVal(cell);
				                    	if (str != null && str.trim().length() > 0) agent5 = str.trim();
				                    	String pos5 = null;
				                    	cell = row.getCell(15); // Spalte P
				                    	str = getStrVal(cell);
				                    	if (str != null && str.trim().length() > 0) pos5 = str.trim();
				                    	if (agent5 != null || pos5 != null) {
				                    		System.err.println("agent5 / pos5\t" + agent5 + "\t" + pos5);
				                    	}
				                    	String remark = null;
				                    	cell = row.getCell(19); // Spalte T
				                    	str = getStrVal(cell);
				                    	if (str != null && str.trim().length() > 0) remark = str.trim();
				                    	
				        				DataCell[] cells = new DataCell[57];
				        				cells[0] = DataType.getMissingCell();
				        				cells[1] = DataType.getMissingCell();
				        				cells[2] = new StringCell(name.getNameName());
				        				cells[3] = (staat == null ? DataType.getMissingCell() : new StringCell(staat));
				        				cells[4] = DataType.getMissingCell();
				        				cells[5] = (jahr == null ? DataType.getMissingCell() : new IntCell(jahr));
				        				cells[6] = (saison == null ? DataType.getMissingCell() : new StringCell(saison));
				        				cells[7] = (bl == null ? DataType.getMissingCell() : new StringCell(bl));
				        				cells[8] = DataType.getMissingCell();
				        				cells[9] = DataType.getMissingCell();
				        				cells[10] = (laborname == null ? DataType.getMissingCell() : new StringCell(laborname));
				        				cells[11] = (akkreditiert == null ? DataType.getMissingCell() : akkreditiert ? BooleanCell.TRUE : BooleanCell.FALSE);
				        				cells[12] = (Source == null ? DataType.getMissingCell() : new StringCell(Source));
				        				cells[13] = DataType.getMissingCell();
				        				cells[14] = DataType.getMissingCell();
				        				cells[15] = DataType.getMissingCell();
				        				cells[16] = DataType.getMissingCell();
				        				cells[17] = DataType.getMissingCell();
				        				cells[18] = DataType.getMissingCell();
				        				cells[19] = DataType.getMissingCell();
				        				cells[20] = DataType.getMissingCell();
				        				cells[21] = DataType.getMissingCell();
				        				cells[22] = DataType.getMissingCell();
				        				cells[23] = DataType.getMissingCell();
				        				cells[24] = DataType.getMissingCell();
				        				cells[25] = DataType.getMissingCell();
				        				cells[26] = DataType.getMissingCell();
				        				cells[27] = DataType.getMissingCell();
				        				cells[28] = DataType.getMissingCell();
				        				cells[29] = (Methode == null ? DataType.getMissingCell() : new StringCell(Methode));
				        				cells[30] = (Grund == null ? DataType.getMissingCell() : new StringCell(Grund));
				        				cells[31] = (Ebene == null ? DataType.getMissingCell() : new StringCell(Ebene));
				        				cells[32] = DataType.getMissingCell();
				        				cells[33] = DataType.getMissingCell();
				        				cells[34] = (agent == null ? DataType.getMissingCell() : new StringCell(agent));
				        				cells[35] = DataType.getMissingCell();
				        				cells[36] = DataType.getMissingCell();
				        				cells[37] = DataType.getMissingCell();
				        				cells[38] = DataType.getMissingCell();
				        				cells[39] = DataType.getMissingCell();
				        				cells[40] = DataType.getMissingCell();
				        				cells[41] = (Anzahl == null ? DataType.getMissingCell() : new StringCell(Anzahl));
				        				cells[42] = (pos == null ? DataType.getMissingCell() : new StringCell(pos));
				        				cells[43] = (pos1 == null ? DataType.getMissingCell() : new StringCell(pos1));
				        				cells[44] = (pos2 == null ? DataType.getMissingCell() : new StringCell(pos2));
				        				cells[45] = (pos3 == null ? DataType.getMissingCell() : new StringCell(pos3));
				        				cells[46] = (pos4 == null ? DataType.getMissingCell() : new StringCell(pos4));
				        				cells[47] = (agent1 == null || pos1 == null ? DataType.getMissingCell() : new StringCell(agent1));
				        				cells[48] = (agent2 == null || pos2 == null ? DataType.getMissingCell() : new StringCell(agent2));
				        				cells[49] = (agent3 == null ? DataType.getMissingCell() : new StringCell(agent3));
				        				cells[50] = (agent4 == null ? DataType.getMissingCell() : new StringCell(agent4));
				        				cells[51] = (remark == null ? DataType.getMissingCell() : new StringCell(remark));
				        				String asp = null;
				        				if (ansprechpartner != null) asp = ansprechpartner;
				        				if (ansprechpartnerMail != null) asp += (asp == null ? "" : ",") + ansprechpartnerMail;
				        				cells[52] = (asp == null ? DataType.getMissingCell() : new StringCell(asp));
				        				cells[53] = DataType.getMissingCell();
				        				cells[54] = DataType.getMissingCell();
				        				cells[55] = DataType.getMissingCell();
				        				cells[56] = DataType.getMissingCell();
				        				RowKey key = RowKey.createRowKey(rowNumber);
				        				rowNumber++;
				        				DataRow outputRow = new DefaultRow(key, cells);

				        				buf.addRowToTable(outputRow);
			                    	}
		                    	}
		                    	
		                    			
		        				//}
		        				exec.checkCanceled();
		                    	System.err.println(rowIndex + " (" + plusIndex + ")\t" + name.getNameName());
		                    }
		                }
	            	}
	            }
	            catch (Exception e) {
            		System.err.println(e.getMessage());
            		e.printStackTrace();
	            }
	        }
		}

		// buf.addRowToTable(new DefaultRow(i+"", IO.createCell(delivery.getId()), IO.createCell(next)));
		buf.close();
        return new BufferedDataTable[]{buf.getTable()};
    }
	private DataTableSpec getSpec() {
		DataColumnSpec[] spec = new DataColumnSpec[57];
		spec[0] = new DataColumnSpecCreator("DEL", StringCell.TYPE).createSpec();
		spec[1] = new DataColumnSpecCreator("regr", StringCell.TYPE).createSpec();
		spec[2] = new DataColumnSpecCreator("TABR", StringCell.TYPE).createSpec();
		spec[3] = new DataColumnSpecCreator("COUNTRY", StringCell.TYPE).createSpec();
		spec[4] = new DataColumnSpecCreator("COUCOD", StringCell.TYPE).createSpec();
		spec[5] = new DataColumnSpecCreator("YEAR", IntCell.TYPE).createSpec();
		spec[6] = new DataColumnSpecCreator("SAISON", StringCell.TYPE).createSpec();
		spec[7] = new DataColumnSpecCreator("REGION", StringCell.TYPE).createSpec();
		spec[8] = new DataColumnSpecCreator("REGCOD", StringCell.TYPE).createSpec();
		spec[9] = new DataColumnSpecCreator("LABOR", StringCell.TYPE).createSpec();
		spec[10] = new DataColumnSpecCreator("LABNAM", StringCell.TYPE).createSpec();
		spec[11] = new DataColumnSpecCreator("Akkreditiert", BooleanCell.TYPE).createSpec();
		spec[12] = new DataColumnSpecCreator("SOURCE", StringCell.TYPE).createSpec();
		spec[13] = new DataColumnSpecCreator("SOUCOD", StringCell.TYPE).createSpec();
		spec[14] = new DataColumnSpecCreator("Souefsa", StringCell.TYPE).createSpec();
		spec[15] = new DataColumnSpecCreator("Souadv", StringCell.TYPE).createSpec();
	    spec[16] = new DataColumnSpecCreator("Souadvcod", StringCell.TYPE).createSpec();
	    spec[17] = new DataColumnSpecCreator("SOURCEA", StringCell.TYPE).createSpec();
	    spec[18] = new DataColumnSpecCreator("SOURCEB", StringCell.TYPE).createSpec();	    
	    spec[19] = new DataColumnSpecCreator("SOURCEC", StringCell.TYPE).createSpec();	    
	    spec[20] = new DataColumnSpecCreator("SOUCODA", StringCell.TYPE).createSpec();	    
	    spec[21] = new DataColumnSpecCreator("SOUCODB", StringCell.TYPE).createSpec();	    
	    spec[22] = new DataColumnSpecCreator("SOUCODC", StringCell.TYPE).createSpec();	    
	    spec[23] = new DataColumnSpecCreator("SOUDETB", StringCell.TYPE).createSpec();	    
	    spec[24] = new DataColumnSpecCreator("SYSTEM", StringCell.TYPE).createSpec();	    
	    spec[25] = new DataColumnSpecCreator("SYSCOD", StringCell.TYPE).createSpec();	    
	    spec[26] = new DataColumnSpecCreator("SYSTM", StringCell.TYPE).createSpec();	    
	    spec[27] = new DataColumnSpecCreator("SYSTG", StringCell.TYPE).createSpec();	    
	    spec[28] = new DataColumnSpecCreator("SYSTP", StringCell.TYPE).createSpec();	    
	    spec[29] = new DataColumnSpecCreator("SYSTMC", StringCell.TYPE).createSpec();	    
	    spec[30] = new DataColumnSpecCreator("SYSTGC", StringCell.TYPE).createSpec();	    
	    spec[31] = new DataColumnSpecCreator("SYSTPC", StringCell.TYPE).createSpec();	    
	    spec[32] = new DataColumnSpecCreator("SYSTEMAD", StringCell.TYPE).createSpec();	    
	    spec[33] = new DataColumnSpecCreator("SYSTPAB", StringCell.TYPE).createSpec();	    
	    spec[34] = new DataColumnSpecCreator("CAUSAGENT", StringCell.TYPE).createSpec();	    
	    spec[35] = new DataColumnSpecCreator("CAUCOD", StringCell.TYPE).createSpec();	    
	    spec[36] = new DataColumnSpecCreator("HCATEG", StringCell.TYPE).createSpec();	    
	    spec[37] = new DataColumnSpecCreator("DATCONT", StringCell.TYPE).createSpec();	    
	    spec[38] = new DataColumnSpecCreator("HERDS", StringCell.TYPE).createSpec();	    
	    spec[39] = new DataColumnSpecCreator("HERDAGENT", StringCell.TYPE).createSpec();	    
	    spec[40] = new DataColumnSpecCreator("ICATEG", StringCell.TYPE).createSpec();	    
	    spec[41] = new DataColumnSpecCreator("INDIVIDUAL", StringCell.TYPE).createSpec();	    
	    spec[42] = new DataColumnSpecCreator("INDIAGENT", StringCell.TYPE).createSpec();	    
	    spec[43] = new DataColumnSpecCreator("QUANT0", StringCell.TYPE).createSpec();	    
	    spec[44] = new DataColumnSpecCreator("QUANT1", StringCell.TYPE).createSpec();	    
	    spec[45] = new DataColumnSpecCreator("QUANT2", StringCell.TYPE).createSpec();	    
	    spec[46] = new DataColumnSpecCreator("QUANT3", StringCell.TYPE).createSpec();	    
	    spec[47] = new DataColumnSpecCreator("QUNAM0", StringCell.TYPE).createSpec();	    
	    spec[48] = new DataColumnSpecCreator("QUNAM1", StringCell.TYPE).createSpec();	    
	    spec[49] = new DataColumnSpecCreator("QUNAM2", StringCell.TYPE).createSpec();	    
	    spec[50] = new DataColumnSpecCreator("QUNAM3", StringCell.TYPE).createSpec();	    
	    spec[51] = new DataColumnSpecCreator("REMARK", StringCell.TYPE).createSpec();	    
	    spec[52] = new DataColumnSpecCreator("NOTE", StringCell.TYPE).createSpec();	    
	    spec[53] = new DataColumnSpecCreator("LABORKENNUNG", StringCell.TYPE).createSpec();	    
	    spec[54] = new DataColumnSpecCreator("ADVDATEI", StringCell.TYPE).createSpec();	    
	    spec[55] = new DataColumnSpecCreator("DATUM", StringCell.TYPE).createSpec();	    
	    spec[56] = new DataColumnSpecCreator("_DBASELOCK", StringCell.TYPE).createSpec();	    
		return new DataTableSpec(spec);
	}
	private String getStrVal(HSSFCell cell) {
		int maxChars = 100000;
		String result = null;
		try {
			if (cell == null || cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
			} else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
				result = cell.getStringCellValue();
				if (result.equals(".")) result = null;
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
		return result;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {
		return new DataTableSpec[] {getSpec()};
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

