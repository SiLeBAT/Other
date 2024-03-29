package de.bund.bfr.knime.aaw.hartung;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.DocumentFactoryHelper;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
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
        super(0, 3);
    }

    private void genError(BufferedDataContainer buf2, int row, int col, String error) {
		System.err.println(error);
		DataCell[] cells = new DataCell[5];
		cells[0] = new IntCell(2); // Status
		cells[1] = new IntCell(row); // row
		cells[2] = new IntCell(col); // col
		cells[3] = new IntCell(231); // errnum
		cells[4] = new StringCell(error);// Error
		RowKey key = RowKey.createRowKey(buf2.size());
		DataRow outputRow = new DefaultRow(key, cells);
		buf2.addRowToTable(outputRow);        		    	
    }
    private void writeRow(BufferedDataContainer buf2, BufferedDataContainer buf3, Row row) {
		DataCell[] cells = new DataCell[20];
		for (int i=0;i<20;i++) {
			String str = row == null ? null : getStrVal(row.getCell(i), buf2);
			cells[i] = str == null ? DataType.getMissingCell() : new StringCell(str);
		}
		RowKey key = RowKey.createRowKey(buf3.size()+1);
		DataRow outputRow = new DefaultRow(key, cells);
		buf3.addRowToTable(outputRow);        		    	
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

		BufferedDataContainer buf = exec.createDataContainer(getSpec());
		BufferedDataContainer buf2 = exec.createDataContainer(getSpec2());
		BufferedDataContainer buf3 = exec.createDataContainer(getSpec3());
		long rowNumber = 0;
		sheet = wb.getSheet("import");
		if (sheet != null) {
        	Integer jahr = null;
        	String bl = null;
        	String ansprechpartner = null;
        	String laborname = null;
        	String ansprechpartnerMail = null;
        	Boolean akkreditiert = null;
        	String defSourceC = null;
        	String labor = null;
        	LinkedHashMap<Integer, Testings> tests = new LinkedHashMap<Integer, Testings>();
        	for (int i=0;i<sheet.getPhysicalNumberOfRows();i++) {
        		row = sheet.getRow(i);
        		writeRow(buf2, buf3, row);
        	}
        	for (int i=0;i<sheet.getPhysicalNumberOfRows();i++) {
        		row = sheet.getRow(i);
        		if (row == null) break;
        		if (i == 0) { 
        			labor = getStrVal(row.getCell(3), buf2); // D1
        			if (labor != null && labor.length() >= 2) labor = labor.substring(2);
        		}
        		Cell cell = row.getCell(0); // Spalte A
        		String str = getStrVal(cell, buf2);
        		if (str == null || (!str.trim().startsWith("A. ") && !str.trim().startsWith("B. ") && !str.trim().startsWith("C. "))) continue;
        		int inedx = str.indexOf(". ") + 2;
        		int index2 = str.indexOf(" ", inedx);
        		System.err.println(str + "\t" + inedx + "\t" + index2);
        		String namename = index2 < 0 ? str.substring(inedx) : str.substring(inedx, index2);
        		int rowIndex = i;
        	//}
			//for (int i=0; i<wb.getNumberOfNames(); i++) {
				/*
	            Name name = wb.getNameAt(i);
	            if (!"import".equals(name.getSheetName())) continue;
	            if (name.getNameName().equals("Print_Area")) continue;

	            try {
	            	if (!name.getRefersToFormula().endsWith("!#REF!") && !name.getRefersToFormula().equals("\"Dummy\"")) {
		                AreaReference area = new AreaReference(name.getRefersToFormula());
		                if (area.isSingleCell()) {
		                    CellReference crList[] = area.getAllReferencedCells();
		                    if (crList[0].getCol() == 1) {
		                    */
		        				//int rowIndex = crList[0].getRow();
		                    	//row = sheet.getRow(rowIndex);
		                    	//Jahr
		                    	cell = row.getCell(11); // Spalte L
		                    	str = getStrVal(cell, buf2);
		                    	if (str != null && str.trim().length() > 2) jahr = Integer.parseInt(str.substring(2).trim());
		                    	//Bundesland
		                    	cell = row.getCell(14); // Spalte O
		                    	str = getStrVal(cell, buf2);
		                    	if (str != null) {
		                    		if (str.trim().length() > 2 && str.startsWith("**")) bl = str.substring(2).trim();
		                    		else if (str.trim().length() > 1 && str.startsWith("*")) bl = str.substring(1).trim();
		                    		else if (str.trim().length() > 0) bl = str.trim();
		                    	}

		                    	// Ansprechpartner, Labname, AnsprechpartnerMail, Akkreditiert, Staat, Saison, Agents
		                    	String staat = null;
		                    	String saison = null;
		                    	int firstDataRow = 0;
		                    	boolean anzahlFin = false;
		                    	for (int plusIndex = 1;plusIndex<20;plusIndex++) {
			                    	row = sheet.getRow(rowIndex + plusIndex);

			                    	if (plusIndex > 5) { // anzahlFin
				                    	cell = row.getCell(0); // Spalte A
				                    	str = getStrVal(cell, buf2);
				                    	if (str != null && str.trim().length() > 1 &&
				                    			str.indexOf("*") < 0 && str.indexOf("Probenart") < 0 && str.indexOf("Herkunftsstaat unbedingt angeben") < 0
				                    			) { // "'Probenart*,**", "Lebensmittel*", "'Probenart*" ?????
				                    		firstDataRow = plusIndex;
				                    		break;
				                    	}
			                    	}			                    	

			                    	cell = row.getCell(2); // Spalte C
			                    	str = getStrVal(cell, buf2);
			                    	if (str != null && str.trim().equalsIgnoreCase("alles bezogen auf")) {	
			                    		for (int pp=1;pp<10;pp++) {
				                    		Row nextRow = sheet.getRow(rowIndex + plusIndex + pp);		
				                    		str = getStrVal(nextRow.getCell(2), buf2);
					                    	if (str != null && str.trim().length() > 0 && !str.trim().startsWith("Beispiel")) {
					                    		defSourceC = str.trim();
					                    		break;
					                    	}
					                    	if (str != null && str.trim().startsWith("Beispiel")) break;
			                    		}
			                    	}
			                    		
			                    	cell = row.getCell(12); // Spalte M
			                    	str = getStrVal(cell, buf2);
			                    	if (str != null && str.trim().length() > 2 && str.startsWith("**")) ansprechpartner = str.substring(2).trim();
			                    	if (str != null && str.trim().length() > 2 && str.startsWith("##")) laborname = str.substring(2).trim();
			                    	if (str != null && str.trim().length() > 0 && str.indexOf("@") > 0) ansprechpartnerMail = str.trim();
			                    	cell = row.getCell(15); // Spalte P
			                    	if (cell != null && cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
			                    		akkreditiert = cell.getBooleanCellValue();
			                    	}
			                    	cell = row.getCell(1); // Spalte B
			                    	str = getStrVal(cell, buf2);
			                    	if (str != null && str.trim().length() > 2 && str.startsWith("**")) staat = str.substring(2).trim();
			                    	cell = row.getCell(2); // Spalte C
			                    	str = getStrVal(cell, buf2);
			                    	if (str != null && str.trim().length() > 2 && str.startsWith("**")) saison = str.substring(2).trim();
			                    	Testings tst = null;
			                    	boolean anzahlFound = false;
		                    		//boolean endStarFound = false;
		                    		boolean kbeThere = false;
			                    	for (int j=7;j<19;j++) {
				                    	cell = row.getCell(j); // Spalte H-S
				                    	str = getStrVal(cell, buf2);
				                    	boolean strZahl = (str != null && (str.trim().equals("Anzahl") || str.trim().equals("Zahl")));
				                    	if (anzahlFound || strZahl) {
				                    		if (!anzahlFin) tests = new LinkedHashMap<Integer, Testings>();
				                    		anzahlFound = true; anzahlFin = true;
				                    		boolean starFound = false;
				                    		boolean kbeFound = false;
				                    		for (int minusIndex = 5; minusIndex > 0;minusIndex--) {
					                    		row = sheet.getRow(rowIndex + plusIndex - minusIndex);
						                    	cell = row.getCell(j);
						                    	str = getStrVal(cell, buf2);
						                    	if (str != null && str.trim().length() > 3) {
						                    		if (str.startsWith("**") || str.startsWith("* ")) {
						                    			starFound = true;
						                    			tst = new Testings();
						                    			tst.setAgent(str.substring(2).trim());	
						                    			// default ist "positiv", kann nachher noch überschrieben werden von KBE/g
						                    			Quant q = new Quant("positiv", null);
						                    			tst.getQuants().put(j, q);
						                    		}
						                    		else if (str.trim().endsWith("*")) {
						                    			if (kbeThere) {
							                    			//endStarFound = true;
						                    				tst = new Testings();
						                    				tst.setAgentCol(j);
						                    			}
						                    		}
						                    		else if (str.indexOf("KBE/g") > 0) {
						                    			kbeFound = true;
						                    			kbeThere = true;
						                    			Quant q = new Quant(str, null);
						                    			tst.getQuants().put(j, q);
						                    		}
						                    	}
				                    		}
				                    		if (starFound || kbeFound) tests.put(j, tst);
				                    		/*
				                    		else if (endStarFound && !kbeFound) {
				                    			tests.put(j, null);
				                    		}
				                    		*/
				                    		else if (!starFound && strZahl) { //(j+1) % 2 == 0 Spalten H, J, L, N, P
				                    			tests.put(j, null);
				                    		}
				                    		row = sheet.getRow(rowIndex + plusIndex);
				                    	}
			                    	}			                    	
		                    	}
		                    	
		                    	if (bl == null) continue; // kann evtl. auch weg

		                    	if (firstDataRow == 0) {
		                    		genError(buf2, row.getRowNum() + 1, 1, "firstDataRow = 0...");
		                    		continue;
		                    	}
		        				int plusIndex = firstDataRow;
		                    	System.err.println("Start: " + namename + "\t" + (rowIndex + plusIndex));
		                    	RowProps rowProps = null;
		                    	for (;;plusIndex++) {
			                    	// Source, Methode, Grund, Ebene
			                    	row = sheet.getRow(rowIndex + plusIndex);
			                    	if (row == null) break;
			                    	// Checke, ob neuer Block
			                    	cell = row.getCell(8); // Spalte I
			                    	String bitte = getStrVal(cell, buf2);
			                    	cell = row.getCell(11); // Spalte L
			                    	String astJahr = getStrVal(cell, buf2);
			                    	cell = row.getCell(13); // Spalte N
			                    	String bland = getStrVal(cell, buf2);
			                    	if (bitte != null && bitte.trim().equals("bitte ggf. Zeilen einfügen")
			                    			|| astJahr != null && astJahr.trim().equals("**" + jahr)
			                    			|| bland != null && bland.trim().equals("Bundesland:")) {
			                    		i = rowIndex + plusIndex - 1;
			                    		break;
			                    	}
			                    	
			                    	// Nein, ok, dann weiter
			                    	rowProps = getA2G(row, rowProps, defSourceC, buf2);
			                		if (rowProps == null || rowProps.getAmount() == 0) continue;
			                		String sa = rowProps.getSourceA();
			                    	if (sa != null && (sa.startsWith("A. ") || sa.startsWith("B. ") || sa.startsWith("C. "))) {
				                		inedx = str.indexOf(". ") + 2;
				                		index2 = str.indexOf(" ", inedx);
				                		System.err.println(str + "\t" + inedx + "\t" + index2);
				                		namename = index2 < 0 ? str.substring(inedx) : str.substring(inedx, index2);
				                		continue;
			                    	}

			                    	int furtherAgentsIndex = 0;
			                    	Integer allPositive = null;
			                    	Integer sumPositive = null;
			                    	
			                    	String remark = null;
			                    	boolean repeat = false;
			                    	while (true) {
				                    	for (int j=7;j<19;j++) {
				                    		if (tests.containsKey(j)) { // sonst empty oder Name
						                    	cell = row.getCell(j); // Spalten H-S
						                    	str = getStrVal(cell, buf2);
					                    		Testings tst = tests.get(j);
						                    	if (str != null && str.trim().length() > 0) {
						                    		if (sumPositive == null) sumPositive = 0;
						                    		String pos = str.trim();
						                    		if (tst == null) { // self-defined agent
								                    	cell = row.getCell(j - 1);
								                    	str = getStrVal(cell, buf2);
								                    	if (str != null && str.trim().length() > 0) {
								                    		String[] parts = str.split("\n"); // sometimes more than only one agent in cell
								                    		String[] poss = pos.split("\n");
								                    		int posssi = 0;
								                    		for (String p : parts) {
								                    			if (!p.trim().isEmpty()) {
								                    				for (;posssi<poss.length;posssi++) {
								                    					if (!poss[posssi].isEmpty()) break;
								                    				}
										                    		int posi = posssi >= poss.length ? 0 : getInt(poss[posssi], buf2, row);
										                    		posssi++;
											                    	tst = new Testings();
									                    			tst.setAgent(p.trim());
									                    			if (j == 7 && !repeat) allPositive = posi;
									                    			else if (j > 7)  sumPositive += posi;
									                    			Quant q = new Quant("positiv", posi);
									                    			tst.getQuants().put(j, q);
									                    			tests.put(furtherAgentsIndex, tst);
									                    			furtherAgentsIndex--;
								                    			}
								                    		}
								                    	}
						                    		}
						                    		else if (!repeat) {
						                    			if (tst.getAgent() == null) {
						                    				if (tst.getAgentCol() == null) {
						        	                    		genError(buf2, row.getRowNum() + 1, 1, "hmmm..getAgentCol sollte definiert sein...");
						                    				}
						                    				else {
						                    					cell = row.getCell(tst.getAgentCol());
						                    					str = getStrVal(cell, buf2);
						                    					if (str != null && str.trim().length() > 0) {
						                    						tst.setAgent(str.trim());
						                    					}
						                    				}
						                    			}
							                    		int posi = getInt(pos, buf2, row);
						                    			tst.getQuants().get(j).setAmount(posi);
						                    			if (j == 7) allPositive = posi;
						                    			else sumPositive += posi;
						                    		}
						                    	}
						                    	else if (!repeat) {
						                    		if (tst != null) {
						                    			tst.getQuants().get(j).setAmount(null);					                    				
						                    		}
						                    	}
				                    		}
				                    	}
				                    	cell = row.getCell(19); // Spalte T
				                    	str = getStrVal(cell, buf2);
				                    	if (str != null && str.trim().length() > 0) {
				                    		if (remark == null) remark = str.trim();
				                    		else remark += str.trim();
				                    	}
				                    	plusIndex++;
				                    	row = sheet.getRow(rowIndex + plusIndex);
				                    	RowProps tmpRowProps = getA2G(row, rowProps, defSourceC, buf2);
				                    	if (tmpRowProps == null || tmpRowProps.getAmount() == 0 || !rowProps.equals(tmpRowProps)) {
				                    		plusIndex--;
				                    		break;
				                    	}
				                    	repeat = true;
			                    	}
			                    	
			                    	if (allPositive == null) allPositive = sumPositive;
			                    	
			                    	Testings lastTesting = null;
			                    	for (Integer j : tests.keySet()) {
			                    		Testings tst = tests.get(j);
			                    		if (tst != null && (lastTesting == null || !lastTesting.equals(tst))) {
				                    		lastTesting = tst;
					        				Quant[] a = tst.getQuants().values().toArray(new Quant[]{});
					        				int k=0;
					        				boolean hasTests = false;
					        				for (;k<a.length;k++) {
					        					if (a[k] != null && a[k].getAmount() != null) {
					        						hasTests = true;
					        						break;
					        					}
					        				}
					        				if (hasTests || j == 7) {
						        				DataCell[] cells = new DataCell[57];
						        				cells[0] = DataType.getMissingCell(); // DEL
						        				cells[1] = new IntCell((int) (rowNumber+1)); // regr
						        				cells[2] = new StringCell(namename);//name.getNameName().substring(name.getNameName().indexOf("_") + 1));
						        				cells[3] = (staat == null ? DataType.getMissingCell() : new StringCell(staat));
						        				cells[4] = DataType.getMissingCell();
						        				cells[5] = (jahr == null ? DataType.getMissingCell() : new IntCell(jahr));
						        				cells[6] = (saison == null ? DataType.getMissingCell() : new StringCell(saison));
						        				cells[7] = (bl == null ? DataType.getMissingCell() : new StringCell(bl));
						        				cells[8] = DataType.getMissingCell();
						        				cells[9] = labor == null ? DataType.getMissingCell() : new StringCell(labor); // Labor
						        				cells[10] = (laborname == null ? DataType.getMissingCell() : new StringCell(laborname));
						        				cells[11] = (akkreditiert == null ? DataType.getMissingCell() : akkreditiert ? BooleanCell.TRUE : BooleanCell.FALSE);
						        				cells[12] = (rowProps.getSourceA() == null && rowProps.getSourceB() == null ? DataType.getMissingCell() : rowProps.getSourceB() == null ? new StringCell(rowProps.getSourceA()) : new StringCell(rowProps.getSourceB()));
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
						        				cells[25] = (rowProps.getCOD() == null ? DataType.getMissingCell() : new StringCell(rowProps.getCOD()));
						        				cells[26] = DataType.getMissingCell();
						        				cells[27] = DataType.getMissingCell();
						        				cells[28] = DataType.getMissingCell();
						        				cells[29] = (rowProps.getMethode() == null ? DataType.getMissingCell() : new StringCell(rowProps.getMethode()));
						        				cells[30] = (rowProps.getGrund() == null ? DataType.getMissingCell() : new StringCell(rowProps.getGrund()));
						        				cells[31] = (rowProps.getEbene() == null ? DataType.getMissingCell() : new StringCell(rowProps.getEbene()));
						        				cells[32] = (rowProps.getMAD() == null ? DataType.getMissingCell() : new StringCell(rowProps.getMAD()));
						        				cells[33] = (rowProps.getPAB() == null ? DataType.getMissingCell() : new StringCell(rowProps.getPAB()));
						        				cells[34] = (tst.getAgent() == null ? DataType.getMissingCell() : new StringCell(tst.getAgent()));
						        				cells[35] = DataType.getMissingCell();
						        				boolean isGruppe = rowProps.getSourceC() != null && (rowProps.getSourceC().equals("Herden/Gehöfte") || rowProps.getSourceC().equals("Herden") || rowProps.getSourceC().equals("Gewicht (in T)"));
						        				boolean isIndividual = rowProps.getSourceC() != null && (rowProps.getSourceC().equals("Einzeltiere") || rowProps.getSourceC().equals("Gehöft") || rowProps.getSourceC().equals("Sendungen") || rowProps.getSourceC().equals("Probenzahl"));
						        				cells[36] = (!isGruppe ? DataType.getMissingCell() : new StringCell(rowProps.getSourceC()));
						        				cells[37] = DataType.getMissingCell();
						        				cells[38] = (!isGruppe || rowProps.getAmount() == null ? DataType.getMissingCell() : new StringCell(rowProps.getAmount()+""));
						        				
						                    	if (tst.hasKBE()) allPositive = tst.getQuantSum();
						        				else if (rowProps.getAmount() != null && allPositive != null && allPositive > rowProps.getAmount()) allPositive = rowProps.getAmount();
						        				
						        				if (j == 7 && a.length == 1 || tst.hasKBE()) {
						        					cells[39] = !isGruppe ? DataType.getMissingCell() :
						        						(allPositive == null ? (rowProps.getSourceA() == null ? new StringCell("-") : new StringCell("-")) : new StringCell(allPositive+""));
						        				}
						        				else {
						        					cells[39] = (!isGruppe || a[0] == null || a[0].getAmount() == null ? DataType.getMissingCell() : new StringCell(a[0].getAmount()+""));
						        				}
						        				
						        				cells[40] = (!isIndividual ? DataType.getMissingCell() : new StringCell(rowProps.getSourceC()));
						        				cells[41] = (!isIndividual || rowProps.getAmount() == null ? DataType.getMissingCell() : new StringCell(rowProps.getAmount()+""));
						        				
						        				if (j == 7 && a.length == 1 || tst.hasKBE()) {
						        					cells[42] = !isIndividual ? DataType.getMissingCell() :
						        						(allPositive == null ? (rowProps.getSourceA() == null ? new StringCell("-") : new StringCell("-")) : new StringCell(allPositive+""));
						        				}
						        				else {
						        					cells[42] = (!isIndividual || a[0] == null || a[0].getAmount() == null ? DataType.getMissingCell() : new StringCell(a[0].getAmount()+""));
						        				}
						        				
						        				cells[43] = (a[0] == null || a[0].getQuantum().equals("positiv") || a[0].getAmount() == null ? DataType.getMissingCell() : new StringCell(a[0].getAmount()+""));
						        				cells[44] = (a.length < 2 || a[1] == null || a[1].getAmount() == null ? DataType.getMissingCell() : new StringCell(a[1].getAmount()+""));
						        				cells[45] = (a.length < 3 || a[2] == null || a[2].getAmount() == null ? DataType.getMissingCell() : new StringCell(a[2].getAmount()+""));
						        				cells[46] = (a.length < 4 || a[3] == null || a[3].getAmount() == null ? DataType.getMissingCell() : new StringCell(a[3].getAmount()+""));
						        				cells[47] = (a[0] == null || a[0].getQuantum().equals("positiv") || a[0].getQuantum() == null ? DataType.getMissingCell() : new StringCell(a[0].getQuantum()));
						        				cells[48] = (a.length < 2 || a[1] == null || a[1].getQuantum() == null ? DataType.getMissingCell() : new StringCell(a[1].getQuantum()));
						        				cells[49] = (a.length < 3 || a[2] == null || a[2].getQuantum() == null ? DataType.getMissingCell() : new StringCell(a[2].getQuantum()));
						        				cells[50] = (a.length < 4 || a[3] == null || a[3].getQuantum() == null ? DataType.getMissingCell() : new StringCell(a[3].getQuantum()));
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
			                    	}
			                    	furtherAgentsIndex++;
			                    	for (;furtherAgentsIndex<=0;furtherAgentsIndex++) {
				                    	tests.remove(furtherAgentsIndex);			                    		
			                    	}
		                    	}
		                    	
		                    			
		        				//}
		        				exec.checkCanceled();
		                    	System.err.println("End: " + namename + "\t" + (rowIndex + plusIndex));
		                    	/*
		                    }
		                }
	            	}
	            }
	            catch (Exception e) {
            		System.err.println(e.getMessage());
            		e.printStackTrace();
	            }
	            */
	        }
		}

		// buf.addRowToTable(new DefaultRow(i+"", IO.createCell(delivery.getId()), IO.createCell(next)));
		buf.close();
		buf2.close();
		buf3.close();
		wb.close();
        return new BufferedDataTable[]{buf.getTable(), buf2.getTable(), buf3.getTable()};
    }
    private int getInt(String number, BufferedDataContainer buf2, Row row) {
		int posi = 0;
		if (number != null) {
			try {
	    		posi = Integer.parseInt(number);						                    			
			}
			catch (Exception e) {
        		genError(buf2, row.getRowNum() + 1, 1, e + "");
			} 
		}
		return posi;
    }
    private RowProps getA2G(Row row, RowProps oldRowProps, String defSourceC, BufferedDataContainer buf2) {
    	if (row == null) return null;
    	RowProps result = new RowProps();
    	if (oldRowProps != null) result = oldRowProps.clone();
    	
    	Cell cell = row.getCell(0); // Spalte A
    	String str = getStrVal(cell, buf2);
    	//System.err.println(row.getRowNum() + " -> " + str);
    	if (str != null && str.trim().length() > 0) {
    		if (str.startsWith("!")) return null;
    		result.setSourceA(str.trim()); result.setSourceB(null);
    		if (result.getSourceC() == null) result.setSourceC(defSourceC);
    	}
    	cell = row.getCell(1); // Spalte B
    	str = getStrVal(cell, buf2);
    	if (str != null && str.trim().length() > 0 && !str.trim().equals("davon:     \\______________________")) {
    		result.setSourceB(str.trim());
    		if (result.getSourceC() == null) result.setSourceC(defSourceC);
    	}
    	cell = row.getCell(2); // Spalte C
    	str = getStrVal(cell, buf2);
    	if (str != null && str.trim().length() > 0) result.setSourceC(str.trim());
    	// Herden/Gehöfte vs. Einzeltiere .... Gewicht (in T) vs. Sendungen

    	String Methode = null;
    	cell = row.getCell(3); // Spalte D
    	str = getStrVal(cell, buf2);
    	if (str != null && str.trim().length() > 0) Methode = str.trim();
    	result.setMethode(Methode);
    	String Grund = null;
    	String MAD = null;
    	cell = row.getCell(4); // Spalte E
    	str = getStrVal(cell, buf2);
    	if (str != null && str.trim().length() > 0) {
    		Grund = str.trim();
    		int index = Grund.indexOf(",");
    		if (index > 0) {
    			MAD = Grund.substring(index+1);
    			Grund = Grund.substring(0, index);
    		}
    	}
    	result.setMAD(MAD);
    	result.setGrund(Grund);
    	String Ebene = null;
    	String PAB = null;
    	cell = row.getCell(5); // Spalte F
    	str = getStrVal(cell, buf2);
    	if (str != null && str.trim().length() > 0) {
    		Ebene = str.trim();
    		if (Ebene.equalsIgnoreCase("k")) {
    			Ebene = "01";
    			PAB = "K";
    		}
			if (!checkInteger(Ebene)) {
        		genError(buf2, row.getRowNum() + 1, 1, "Row " + row.getRowNum() + ": Ebene ist kein Integerwert (" + Ebene + ")!!!");
			}
    	}
    	result.setEbene(Ebene);
    	result.setPAB(PAB);
    	String Anzahl = null;
    	cell = row.getCell(6); // Spalte G
    	str = getStrVal(cell, buf2);
    	if (str != null && str.trim().length() > 0) Anzahl = str.trim();
		int posi = getInt(Anzahl, buf2, row);
		if (posi == 0) result.setAmount(0);//return null;
		//if (Anzahl == null || Integer.parseInt(Anzahl) == 0) result.setAmount(null);
		else result.setAmount(posi);
		
		return result;
    }
    private boolean checkInteger(String s) {
    	try {
    		Integer.parseInt(s);
    	}
    	catch (Exception e) {return false;}
    	return true;
    }
	private DataTableSpec getSpec3() {
		DataColumnSpec[] spec = new DataColumnSpec[20];
		spec[0] = new DataColumnSpecCreator("A", StringCell.TYPE).createSpec();
		spec[1] = new DataColumnSpecCreator("B", StringCell.TYPE).createSpec();
		spec[2] = new DataColumnSpecCreator("C", StringCell.TYPE).createSpec();
		spec[3] = new DataColumnSpecCreator("D", StringCell.TYPE).createSpec();
		spec[4] = new DataColumnSpecCreator("E", StringCell.TYPE).createSpec();
		spec[5] = new DataColumnSpecCreator("F", StringCell.TYPE).createSpec();
		spec[6] = new DataColumnSpecCreator("G", StringCell.TYPE).createSpec();
		spec[7] = new DataColumnSpecCreator("H", StringCell.TYPE).createSpec();
		spec[8] = new DataColumnSpecCreator("I", StringCell.TYPE).createSpec();
		spec[9] = new DataColumnSpecCreator("J", StringCell.TYPE).createSpec();
		spec[10] = new DataColumnSpecCreator("K", StringCell.TYPE).createSpec();
		spec[11] = new DataColumnSpecCreator("L", StringCell.TYPE).createSpec();
		spec[12] = new DataColumnSpecCreator("M", StringCell.TYPE).createSpec();
		spec[13] = new DataColumnSpecCreator("N", StringCell.TYPE).createSpec();
		spec[14] = new DataColumnSpecCreator("O", StringCell.TYPE).createSpec();
		spec[15] = new DataColumnSpecCreator("P", StringCell.TYPE).createSpec();
		spec[16] = new DataColumnSpecCreator("Q", StringCell.TYPE).createSpec();
		spec[17] = new DataColumnSpecCreator("R", StringCell.TYPE).createSpec();
		spec[18] = new DataColumnSpecCreator("S", StringCell.TYPE).createSpec();
		spec[19] = new DataColumnSpecCreator("T", StringCell.TYPE).createSpec();
		return new DataTableSpec(spec);
	}
	private DataTableSpec getSpec2() {
		DataColumnSpec[] spec = new DataColumnSpec[5];
		spec[0] = new DataColumnSpecCreator("Status", IntCell.TYPE).createSpec();
		spec[1] = new DataColumnSpecCreator("Zeile", IntCell.TYPE).createSpec();
		spec[2] = new DataColumnSpecCreator("Spalte", IntCell.TYPE).createSpec();
		spec[3] = new DataColumnSpecCreator("Fehler-Nr", IntCell.TYPE).createSpec();
		spec[4] = new DataColumnSpecCreator("Kommentar", StringCell.TYPE).createSpec();
		return new DataTableSpec(spec);
	}
	private DataTableSpec getSpec() {
		DataColumnSpec[] spec = new DataColumnSpec[57];
		spec[0] = new DataColumnSpecCreator("DEL", StringCell.TYPE).createSpec();
		spec[1] = new DataColumnSpecCreator("regr", IntCell.TYPE).createSpec();
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
	private String getStrVal(Cell cell, BufferedDataContainer buf2) {
		int maxChars = 100000;
		String result = null;
		try {
			if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
			} else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
				result = cell.getStringCellValue();
				if (result.equals(".")) result = null;
			} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC || cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
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
	        		genError(buf2, cell.getRowIndex() + 1, 1, "string too long (" + result.length() + ") - shortened to " + maxChars + " chars... '" + result + "' -> '" + result.substring(0, maxChars)
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

    //simple way to check for both types of excel files
    public boolean isXls(InputStream i) throws IOException{
        return POIFSFileSystem.hasPOIFSHeader(i);
    }
    public boolean isXlsx(InputStream i) throws IOException{
        return DocumentFactoryHelper.hasOOXMLHeader(i);
    }
}

