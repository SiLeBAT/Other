package de.bund.bfr.knime.aaw.lims;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.knime.core.data.BooleanValue;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.IntValue;
import org.knime.core.data.RowKey;
import org.knime.core.data.StringValue;
import org.knime.core.data.date.DateAndTimeValue;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
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
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * This is the model implementation of MyTab21.
 * 
 *
 * @author aaw
 */
public class MyTab21NodeModel extends NodeModel {
    
	static final String BASE_FOLDER = "basefolder";
	static final String ERREGER = "erreger";
	static final String BFR_PROGRAMM = "bfrprogramm";
	static final String SEROVAR = "serovar";
	static final String BLSUBFOLDER = "blsubfolder";
	static final String JAHR = "jahr";
	static final String KRITERIEN_JAHR = "kriterienjahr";
	static final String PPID = "pruefplanid";
	static final String ESBL = "esbl";
	static final String CARBA = "carba";
	static final String ENTERO = "entero";
	
    private final SettingsModelString baseFolder = new SettingsModelString(BASE_FOLDER, "C:/Dokumente und Einstellungen/Weiser/Desktop/tawak/");
    private final SettingsModelString erreger = new SettingsModelString(ERREGER, "SA");
    private final SettingsModelString bfrProgramm = new SettingsModelString(BFR_PROGRAMM, "Dia");
    private final SettingsModelString serovar = new SettingsModelString(SEROVAR, "");
    private final SettingsModelString blSubFolder = new SettingsModelString(BLSUBFOLDER, "");
    private final SettingsModelInteger jahr = new SettingsModelInteger(JAHR, 2013);
    private final SettingsModelInteger kriterienJahr = new SettingsModelInteger(KRITERIEN_JAHR, 2013);
    private final SettingsModelString pruefPlanId = new SettingsModelString(PPID, "");
    private final SettingsModelInteger esbl = new SettingsModelInteger(ESBL, 0);
    private final SettingsModelInteger carba = new SettingsModelInteger(CARBA, 0);
    private final SettingsModelInteger entero = new SettingsModelInteger(ENTERO, 0);

    private boolean doAutosize = false;
	
	/**
     * Constructor for the node model.
     */
    protected MyTab21NodeModel() {
        super(3, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
    	long ttt = System.currentTimeMillis();
    	
    	// Expected data Wirkstoffe: "Gruppe","Name","Kurz","cutoff","Sort"
    	// Expected data Datensatz: "Programm_kurz", "Serovar_Schreibweise", Wirkstoffe
    	// dataset einlesen
    	// Bezeichnungen
    	HashMap<String, Programm> ps = new HashMap<String, Programm>();
    	DataTableSpec dts = inData[2].getSpec();
    	String[] cns = dts.getColumnNames();
    	for (DataRow row : inData[2]) {
			DataCell dc = row.getCell(0);
			Programm p = null;
			if (!dc.isMissing()) {
				p = new Programm(false);
				p.setName(((StringCell) dc).getStringValue());
				//ps.put(p.getName(), p);
				dc = row.getCell(1); // Tierart
				if (!dc.isMissing()) p.setTierart(((StringValue) dc).getStringValue());
				dc = row.getCell(2); // Matrix
				if (!dc.isMissing()) p.addMatrix(((StringValue) dc).getStringValue());
				dc = row.getCell(3); // Probenahmeort
				if (!dc.isMissing()) p.setProbenahmeort(((StringValue) dc).getStringValue());
			}
			if (p != null && p.getName() != null) {
				if (!ps.containsKey(p.getName())) ps.put(p.getName(), p);
				else ps.get(p.getName()).merge(p);
				System.out.println(ps.get(p.getName()).getMatrices());
			}
    	}
    	
    	// Wirkstoffe cutoffs einlesen
    	HashMap<String, Wirkstoff> wss = new HashMap<String, Wirkstoff>();
    	HashMap<Integer, Wirkstoff> ws = new HashMap<Integer, Wirkstoff>();
    	dts = inData[1].getSpec();
    	cns = dts.getColumnNames();
    	for (DataRow row : inData[1]) {
    		Wirkstoff w = new Wirkstoff();
    		for (int i=0;i<dts.getNumColumns();i++) {
    			DataCell dc = row.getCell(i);
        	    if (!dc.isMissing()) {
        			if (cns[i].equalsIgnoreCase("Gruppe")) w.setGruppe(((StringCell) dc).getStringValue());
        			else if (cns[i].equalsIgnoreCase("Name")) w.setName(((StringCell) dc).getStringValue());
        			else if (cns[i].equalsIgnoreCase("Kurz")) w.setKurz(((StringCell) dc).getStringValue());
        			else if (cns[i].equalsIgnoreCase("cutoff")) w.setCutoff(((DoubleCell) dc).getDoubleValue());
        			else if (cns[i].equalsIgnoreCase("cutoffcj")) w.setCutoff2(((DoubleCell) dc).getDoubleValue());
        			else if (cns[i].equalsIgnoreCase("Sort")) w.setIndexSort(((IntCell) dc).getIntValue());
        	    }
    		}
    		if (w.getIndexSort() != null) {
    			ws.put(w.getIndexSort(), w);
    			wss.put(w.getKurz(), w);
    		}
    	}
    	System.err.println("Wirkstoffe:\t" + (System.currentTimeMillis()-ttt));

    	// dataset einlesen
    	// preTab
    	dts = inData[0].getSpec();
    	cns = dts.getColumnNames();
    	ExcelWriter ew = new ExcelWriter();
    	int rowIndex = 0;
    	XSSFRow xrow = ew.createRow(rowIndex);
    	int colIndex = 0;
    	for (String cn : cns) {
    		XSSFCell cell = xrow.createCell(colIndex); cell.setCellValue(cn); colIndex++;
       		if (wss.containsKey(cn)) {
       			cell = xrow.createCell(colIndex); cell.setCellValue(cn + "_Res"); colIndex++;
       		}
    	}
    	//System.err.println("Wirkstoffe34:\t" + (System.currentTimeMillis()-ttt));
    	boolean containsStrings = false;
try {
    	for (DataRow row : inData[0]) {
    		exec.checkCanceled();
        	//System.err.println("Wirkstofferow:\t" + row);
    		Programm p = new Programm(true);
    		rowIndex++; xrow = ew.createRow(rowIndex);
    		colIndex = 0;
    		int dtsci = dts.findColumnIndex("Programm_kurz");
    		if (dtsci >= 0) {
				DataCell dc = row.getCell(dtsci);
				if (!dc.isMissing()) p.setName(((StringValue) dc).getStringValue());
    		}
    		dtsci = dts.findColumnIndex("Serovar_Schreibweise");
    		if (dtsci >= 0) {
				DataCell dc = row.getCell(dtsci);
				if (!dc.isMissing()) p.setSerovarName(((StringValue) dc).getStringValue());
    		}
    		/*
    		dtsci = dts.findColumnIndex("Tierart");
    		if (dtsci >= 0) {
				DataCell dc = row.getCell(dtsci);
				if (!dc.isMissing()) p.setTierart(((StringValue) dc).getStringValue());
    		}
    		dtsci = dts.findColumnIndex("Matrix");
    		if (dtsci >= 0) {
				DataCell dc = row.getCell(dtsci);
				if (!dc.isMissing()) p.addMatrix(((StringValue) dc).getStringValue());
    		}
    		dtsci = dts.findColumnIndex("Probenahmeort");
    		if (dtsci >= 0) {
				DataCell dc = row.getCell(dtsci);
				if (!dc.isMissing()) p.setProbenahmeort(((StringValue) dc).getStringValue());
    		}
			*/
    		for (int i=0;i<dts.getNumColumns();i++) {
    			DataCell dc = row.getCell(i);
        	    if (!dc.isMissing()) {
        	    	if (dc.getType().isCompatible(IntValue.class)) {
        	    		XSSFCell cell = xrow.createCell(colIndex); cell.setCellValue(((IntValue) dc).getIntValue()); colIndex++;
        	    	}
        	    	else if (dc.getType().isCompatible(DoubleValue.class)) {
        	    		XSSFCell cell = xrow.createCell(colIndex); cell.setCellValue(((DoubleValue) dc).getDoubleValue()); colIndex++;
        	    	}
        	    	else if (dc.getType().isCompatible(BooleanValue.class)) {
        	    		XSSFCell cell = xrow.createCell(colIndex); cell.setCellValue(((BooleanValue)dc).getBooleanValue()); colIndex++;
        	    	}
        	    	else if (dc.getType().isCompatible(DateAndTimeValue.class)) {
        	    		DateAndTimeValue dateAndTime = (DateAndTimeValue)dc;
        	    		Calendar val = dateAndTime.getUTCCalendarClone();
        	    		XSSFCell cell = xrow.createCell(colIndex); cell.setCellValue(val); colIndex++;
        	    		XSSFCellStyle dateStyle = ew.getWBStyle();
        	    		dateStyle.setDataFormat(ew.getHelper().createDataFormat().getFormat("dd.mm.yyyy"));
        	    		cell.setCellStyle(dateStyle);
        	    	}
        	    	else if (dc.getType().isCompatible(StringValue.class)) {
        	    		XSSFCell cell = xrow.createCell(colIndex); cell.setCellValue(((StringValue) dc).getStringValue()); colIndex++;
        	    	}
        	    	else {
        	    		XSSFCell cell = xrow.createCell(colIndex); cell.setCellValue(dc.toString()); colIndex++;
        	    	}

        			if (wss.containsKey(cns[i])) {
        				if (!dc.getType().isCompatible(DoubleValue.class)) {
        					p.addParameter(wss.get(cns[i]), ((StringValue) dc).getStringValue());
        					containsStrings = true;
            	    		XSSFCell cell = xrow.createCell(colIndex); cell.setCellValue(""); colIndex++;
        				}
        				else {
            				boolean isPositiv = p.addWirkstoff(wss.get(cns[i]), ((DoubleValue) dc).getDoubleValue(), erreger.getStringValue());
            	    		XSSFCell cell = xrow.createCell(colIndex); cell.setCellValue(isPositiv); colIndex++;
        				}
        			}
        	    }
        	    else {
    	    		XSSFCell cell = xrow.createCell(colIndex); cell.setCellValue(""); colIndex++;
        	    }
        	}
    		if (p.getName() != null) {
        		p.sampleFin();
    			if (!ps.containsKey(p.getName())) ps.put(p.getName(), p);
    			else ps.get(p.getName()).merge(p);
    		}
    	}
}
catch (Exception ee) {System.err.println(ee.getMessage());ee.printStackTrace();throw ee;}

    	System.err.println("preTab0:\t" + (System.currentTimeMillis()-ttt));
    	String pfn = getFilename(baseFolder.getStringValue(), "preTab" + (containsStrings ? (carba.getIntValue() == 1 ? "_CARBA" : "_ESBL") : "21"));

    	ew.setStyle(0, 0, null, null, true, true, false, false, true, null, null); // RowHeader
    	if (doAutosize) ew.autoSizeColumn(5);
    	ew.save(pfn);
    	System.err.println("preTab1:\t" + (System.currentTimeMillis()-ttt));
    	
    	// Ergebnisse berechnen und ausgeben
    	SortedSet<String> pkeys = new TreeSet<String>(ps.keySet());
		int maxResi = 0;
		for (String pkey : pkeys) {
			Programm p = ps.get(pkey);
			if (p.getMaxResi() > maxResi) maxResi = p.getMaxResi();
		}
   		SortedSet<Integer> wkeys = new TreeSet<Integer>(ws.keySet());
    	if (containsStrings) {
    		// Tab1
   	    	LinkedHashSet<List<Object>> tab1 = new LinkedHashSet<List<Object>>();
        	List<String> doubleList = new ArrayList<String>();
        	int dblListStart = 0;int dblListEnd = 10;
        	for (int i=dblListStart;i<=dblListEnd;i++) doubleList.add("<2^" + i);
        	doubleList.add(">=2^" + dblListEnd);
        	List<String> stringList = new ArrayList<String>();
        	stringList.add("+");stringList.add("-");
        	for (String pkey : pkeys) {
        		Programm p = ps.get(pkey);
       	   		for (Integer wkey : wkeys) {
       	   			Wirkstoff w = ws.get(wkey);
       	   			String kurz = w.getKurz();
    	   	   		HashMap<String, Integer> pFrequencymap = p.getParamFrequencyMap(kurz);
    	   	   		if (pFrequencymap != null) {
    	   	   			for (String str : pFrequencymap.keySet()) if (!stringList.contains(str)) stringList.add(str);
    	   	   		}
       	   		}
        	}
   	   		List<Object> tab1Row = new ArrayList<Object>();
   	   		tab1Row.add(""); tab1Row.add("Total"); tab1Row.add("#Positiv"); tab1Row.add("%Positiv");
   	   		for (String str : stringList) tab1Row.add(str);
   	   		for (String str : doubleList) tab1Row.add(str);
   	   		tab1.add(tab1Row);
   	   		LinkedHashMap<String, LinkedHashMap<String, Integer>> hmGesamt = new LinkedHashMap<>();
        	HashSet<Integer> dlrl = new HashSet<>();
        	int rowIndexx = 0;
        	// Tab3
    		for (String pkey : pkeys) {
    			Programm p = ps.get(pkey);
       	    	LinkedHashSet<List<Object>> tab3 = new LinkedHashSet<List<Object>>();
       	   		List<Object> tab3Row = new ArrayList<Object>();
       	   		tab3Row.add(""); tab3Row.add("Total"); tab3Row.add("#Positiv"); tab3Row.add("%Positiv");
       	   		for (String str : stringList) tab3Row.add(str);
       	   		for (String str : doubleList) tab3Row.add(str);
       			tab3.add(tab3Row);
       	   		for (Integer wkey : wkeys) {
       	   			Wirkstoff w = ws.get(wkey);
       	   			String kurz = w.getKurz();
       	   			if (!hmGesamt.containsKey(kurz)) hmGesamt.put(kurz, new LinkedHashMap<String, Integer>());
       	   			LinkedHashMap<String, Integer> hmKurz = hmGesamt.get(kurz);
       	   			rowIndexx++;
       	   			tab3Row = new ArrayList<Object>();
       	   			tab3Row.add(kurz);
       				HashMap<String, Integer> pw = p.getNumPositive();
       				int num = pw.containsKey(kurz) ? pw.get(kurz) : 0;
       				tab3Row.add(p.getNumSamples()); tab3Row.add(num); tab3Row.add(100.0 * num / p.getNumSamples()); 
       				if (!hmKurz.containsKey("Total")) hmKurz.put("Total", 0);
       				hmKurz.put("Total", hmKurz.get("Total") + p.getNumSamples());
       				if (!hmKurz.containsKey("#Positiv")) hmKurz.put("#Positiv", 0);
       				hmKurz.put("#Positiv", hmKurz.get("#Positiv") + num);
    	   	   		HashMap<Double, Integer> frequencymap = p.getFrequencyMap(kurz);
    	   	   		HashMap<String, Integer> pFrequencymap = p.getParamFrequencyMap(kurz);
    	   	   		for (String str : stringList)  {
           				if (!hmKurz.containsKey(str)) hmKurz.put(str, 0);
	    	   	   		if (pFrequencymap != null && pFrequencymap.containsKey(str)) {
	    	   	   			tab3Row.add(pFrequencymap.get(str));
	           				hmKurz.put(str, hmKurz.get(str) + pFrequencymap.get(str));
	    	   	   		}
	    	   	   		else tab3Row.add("");
    	   	   		}
    	   	   		HashMap<String, Integer> hm = new HashMap<>();
	    	   	   	if (frequencymap != null) {
	    	   	   		for (int i = 1;i<doubleList.size() - 1;i++)  {
	    	   	   			int count = 0;
	    	   	   			for (Double dbl : frequencymap.keySet()) {
	    	   	   				if (dbl.doubleValue() < Math.pow(2, dblListStart+i) && dbl.doubleValue() >= Math.pow(2, dblListStart+i-1)) count += frequencymap.get(dbl);
	    	   	   			}
		    	   	   		hm.put(doubleList.get(i), count);
	    	   	   		}
		   	   			int count = 0;
		   	   			for (Double dbl : frequencymap.keySet()) {
		   	   				if (dbl.doubleValue() < Math.pow(2, dblListStart)) count += frequencymap.get(dbl);
		   	   			}
	    	   	   		hm.put(doubleList.get(0), count);
		   	   			count = 0;
		   	   			for (Double dbl : frequencymap.keySet()) {
		   	   				if (dbl.doubleValue() >= Math.pow(2, dblListStart + doubleList.size() - 2)) count += frequencymap.get(dbl);
		   	   			}
	    	   	   		hm.put(doubleList.get(doubleList.size()-1), count);
	    	   	   	}
    	   	   		for (String str : doubleList) {
           				if (!hmKurz.containsKey(str)) hmKurz.put(str, 0);
        	   	   		if (hm.containsKey(str) && hm.get(str) > 0) {
        	   	   			tab3Row.add(hm.get(str));
        	   				dlrl.add(rowIndexx);
	           				hmKurz.put(str, hmKurz.get(str) + hm.get(str));
        	   	   		}
        	   	   		else tab3Row.add("");
    	   	   		}
    	   			tab3.add(tab3Row);
       			}
       	   		
       	    	String fn = getFilename(baseFolder.getStringValue(), (carba.getIntValue() == 1 ? "CARBA_" : "ESBL_") + p.getName());
       	    	ew = new ExcelWriter(tab3);
       	    	ew.setStyle(0, 0, null, null, true, true, false, false, true, null, null); // RowHeader
       	    	ew.setStyle(null, null, 0, 0, true, false, false, true, false, null, null); // ColumnHeader
       	    	ew.setStyle(null, null, 3, 3, false, false, false, true, false, null, null); // TrennBorder
       	    	ew.setStyle(null, null, 3 + stringList.size(), 3 + stringList.size(), false, false, false, true, false, null, null); // LastColumnBorder1
       	    	ew.setStyle(null, null, 3 + stringList.size() + (4-dblListStart), 3 + stringList.size() + (4-dblListStart), false, false, false, true, false, null, null); // LimitBorder (>=8)
       	    	for (Integer dlr : dlrl)
       	    		ew.setStyle(dlr, dlr, 3 + stringList.size() + (5-dblListStart), null, false, false, false, false, false, null, Color.RED); // LimitBorder (>=8)
       	    	ew.setStyle(null, null, 3 + stringList.size() + doubleList.size(), 3 + stringList.size() + doubleList.size(), false, false, false, true, false, null, null); // LastColumnBorder2
       	    	ew.setStyle(tab3.size() - 1, tab3.size() - 1, null, null, false, false, false, false, true, null, null); // LastRowBorder
       	    	ew.autoSizeColumns(tab3Row.size());
       	    	ew.save(fn);
       		}
        	System.err.println("tab3:\t" + (System.currentTimeMillis()-ttt));
        	for (String kurz : hmGesamt.keySet()) {
        		tab1Row = new ArrayList<Object>();
        		tab1Row.add(kurz);
        		LinkedHashMap<String, Integer> hmKurz = hmGesamt.get(kurz);
        		tab1Row.add(hmKurz.get("Total")); tab1Row.add(hmKurz.get("#Positiv")); tab1Row.add(100.0 * hmKurz.get("#Positiv") / hmKurz.get("Total")); 
   	   			for (String str : hmKurz.keySet()) {
   	   				if (!str.equals("Total") && !str.equals("#Positiv")) {
   	   					if (hmKurz.get(str) > 0) {
   	   						tab1Row.add(hmKurz.get(str));
   	   					}
   	   					else tab1Row.add("");
   	   				}
   	   			}

	   	   		tab1.add(tab1Row);
        	}
   	    	String fn = getFilename(baseFolder.getStringValue(), (carba.getIntValue() == 1 ? "CARBA_gesamt" : "ESBL_gesamt"));
   	    	ew = new ExcelWriter(tab1);
   	    	ew.setStyle(0, 0, null, null, true, true, false, false, true, null, null); // RowHeader
   	    	ew.setStyle(null, null, 0, 0, true, false, false, true, false, null, null); // ColumnHeader
   	    	ew.setStyle(null, null, 3, 3, false, false, false, true, false, null, null); // TrennBorder
   	    	ew.setStyle(null, null, 3 + stringList.size(), 3 + stringList.size(), false, false, false, true, false, null, null); // LastColumnBorder1
   	    	ew.setStyle(null, null, 3 + stringList.size() + (4-dblListStart), 3 + stringList.size() + (4-dblListStart), false, false, false, true, false, null, null); // LimitBorder (>=8)
   	    	for (Integer dlr : dlrl)
   	    		ew.setStyle(dlr, dlr, 3 + stringList.size() + (5-dblListStart), null, false, false, false, false, false, null, Color.RED); // LimitBorder (>=8)
   	    	ew.setStyle(null, null, 3 + stringList.size() + doubleList.size(), 3 + stringList.size() + doubleList.size(), false, false, false, true, false, null, null); // LastColumnBorder2
   	    	ew.setStyle(tab1.size() - 1, tab1.size() - 1, null, null, false, false, false, false, true, null, null); // LastRowBorder
   	    	ew.autoSizeColumns(tab1Row.size());
   	    	ew.save(fn);
        	System.err.println("tab1:\t" + (System.currentTimeMillis()-ttt));
        }
    	else {
       		for (Integer wkey : wkeys) {
       			Wirkstoff w = ws.get(wkey);
       			boolean hasWKey = false;
       			for (String pkey : pkeys) {
       				Programm p = ps.get(pkey);
       				HashMap<String, Integer> pw = p.getNumPositive();
       				if (pw.containsKey(w.getKurz())) {
       					hasWKey = true;
       					break;
       				}
       			}
       			if (!hasWKey) ws.remove(wkey);
       		}
       		wkeys = new TreeSet<Integer>(ws.keySet());
        	// Tab1
        	LinkedHashSet<List<Object>> tab1 = new LinkedHashSet<List<Object>>();
       		List<Integer> tab1Borders = new ArrayList<Integer>();
       		List<Integer> tab1BordersV = new ArrayList<Integer>();

       		List<Object> tab1Row = new ArrayList<Object>();
    		tab1Row.add("");
    		tab1BordersV.add(0);
        	for (String pkey : pkeys) {
        		tab1Row.add(pkey); tab1Row.add(pkey + " (#Positiv)"); tab1Row.add(pkey + " (%Positiv)");
        		tab1BordersV.add(tab1Row.size()-1);
        	}
    		tab1.add(tab1Row);
    		
       		for (Integer wkey : wkeys) {
       			Wirkstoff w = ws.get(wkey);
       			tab1Row = new ArrayList<Object>();
       			String kurz = w.getKurz();
       			tab1Row.add(kurz);
       			for (String pkey : pkeys) {
       				Programm p = ps.get(pkey);
       				HashMap<String, Integer> pw = p.getNumPositive();
       				int num = pw.containsKey(kurz) ? pw.get(kurz) : 0;
    	   	   		tab1Row.add(p.getNumSamples()); tab1Row.add(num); 				
    	   	   		if (p.getNumSamples() > 0) tab1Row.add(100.0 * num / p.getNumSamples()); else tab1Row.add("");
       			}
       			tab1.add(tab1Row);
       		}
       		
    		tab1Borders.add(tab1.size() - 1);
       		for (int i=0;i<=maxResi;i++) {
       	   		tab1Row = new ArrayList<Object>();
       	   		tab1Row.add(i == 0 ? "Sensibel" : i + "x resistent");
       			for (String pkey : pkeys) {
       				Programm p = ps.get(pkey);
       				int num = p.getNumResistent(i);
       	   	   		tab1Row.add(p.getNumSamples()); tab1Row.add(num);  				
    	   	   		if (p.getNumSamples() > 0) tab1Row.add(100.0 * num / p.getNumSamples()); else tab1Row.add("");
       			}
       	   		tab1.add(tab1Row);
       		}
        	System.err.println("tab1:\t" + (System.currentTimeMillis()-ttt));

        	// Tab1_22
        	LinkedHashSet<List<Object>> tab122 = new LinkedHashSet<List<Object>>();
       		List<Integer> tab122Borders = new ArrayList<Integer>();
       		List<Integer> tab122BordersV = new ArrayList<Integer>();

       		List<Object> tab122Row = new ArrayList<Object>();
    		tab122Row.add("Programm");
        	for (String pkey : pkeys) {
        		tab122Row.add(pkey); tab122Row.add("");
        		tab122BordersV.add(tab122Row.size()-1);
        	}
    		tab122.add(tab122Row);
    		
    		tab122Row = new ArrayList<Object>();
    		tab122Row.add("Tierart");
        	for (String pkey : pkeys) {
        		Programm p = ps.get(pkey);
        		tab122Row.add(p.getTierart()); tab122Row.add("");
        		tab122BordersV.add(tab122Row.size()-1);
        	}
    		tab122.add(tab122Row);
    		
    		tab122Row = new ArrayList<Object>();
    		tab122Row.add("Probenahmeort");
        	for (String pkey : pkeys) {
        		Programm p = ps.get(pkey);
        		tab122Row.add(p.getProbenahmeort()); tab122Row.add("");
        		tab122BordersV.add(tab122Row.size()-1);
        	}
    		tab122.add(tab122Row);
    		
    		tab122Row = new ArrayList<Object>();
    		tab122Row.add("Matrix");
        	for (String pkey : pkeys) {
        		Programm p = ps.get(pkey);
        		tab122Row.add(String.join("\n", p.getMatrices())); tab122Row.add("");
        		tab122BordersV.add(tab122Row.size()-1);
        	}
    		tab122.add(tab122Row);
    		
    		tab122Row = new ArrayList<Object>();
    		tab122Row.add("");
        	for (String pkey : pkeys) {
        		tab122Row.add("N"); tab122Row.add("%");
        		tab122BordersV.add(tab122Row.size()-1);
        	}
    		tab122.add(tab122Row);
    		
    		tab122Borders.add(tab122.size() - 1);
    		tab122Row = new ArrayList<Object>();
    		tab122Row.add("Anzahl untersucht");
        	for (String pkey : pkeys) {
        		Programm p = ps.get(pkey);
        		tab122Row.add(p.getNumSamples()); tab122Row.add("");
        		tab122BordersV.add(tab122Row.size()-1);
        	}
    		tab122.add(tab122Row);
    		tab122Borders.add(tab122.size() - 1);
   		
       		for (Integer wkey : wkeys) {
       			Wirkstoff w = ws.get(wkey);
       			tab122Row = new ArrayList<Object>();
       			String lang = w.getName();
       			tab122Row.add(lang);
       			for (String pkey : pkeys) {
       				if (ps.containsKey(pkey)) {
           				Programm p = ps.get(pkey);
           				HashMap<String, Integer> pw = p.getNumPositive();
           				int num = pw.containsKey(w.getKurz()) ? pw.get(w.getKurz()) : 0;
        	   	   		tab122Row.add(num);
        	   	   		if (p.getNumSamples() > 0) tab122Row.add(100.0 * num / p.getNumSamples()); else tab122Row.add("");
       				}
       			}
       			tab122.add(tab122Row);
       		}
       		
    		tab122Borders.add(tab122.size() - 1);
       		for (int i=0;i<=maxResi;i++) {
       	   		tab122Row = new ArrayList<Object>();
       	   		tab122Row.add(i == 0 ? "Sensibel" : i + "x resistent");
       			for (String pkey : pkeys) {
       				Programm p = ps.get(pkey);
       				int num = p.getNumResistent(i);
       	   	   		tab122Row.add(num);
       	   	   		if (p.getNumSamples() > 0) tab122Row.add(100.0 * num / p.getNumSamples()); else tab122Row.add("");
       			}
       	   		tab122.add(tab122Row);
       		}
        	System.err.println("tab122:\t" + (System.currentTimeMillis()-ttt));

        	// Tab2
        	LinkedHashSet<List<Object>> tab2 = new LinkedHashSet<List<Object>>();
       		List<Integer> tab2Borders = new ArrayList<Integer>();
       		List<Object> tab2Row = new ArrayList<Object>();
       		tab2Row.add("Gruppe"); tab2Row.add("Sum"); tab2Row.add("percent"); tab2Row.add("totalCount"); tab2Row.add("Programm");
    		tab2.add(tab2Row);
        	for (String pkey : pkeys) {
        		Programm p = ps.get(pkey);
           		HashMap<String, Integer> pgrc = p.getGroupResistanceCount();
        		if (pgrc != null) {
        			SortedSet<String> grkeys = new TreeSet<String>(pgrc.keySet());
        			int sum = 0;
        			for (String group : grkeys) {
        				tab2Row = new ArrayList<Object>();
        				tab2Row.add(group); tab2Row.add(pgrc.get(group)); tab2Row.add(100.0 * pgrc.get(group) / p.getNumSamples());
        				tab2Row.add(p.getNumSamples()); tab2Row.add(p.getName());
        				tab2.add(tab2Row);
        				sum += pgrc.get(group);
        			}
        			tab2Row = new ArrayList<Object>();
        			tab2Row.add("Sum"); tab2Row.add(sum); tab2Row.add(100.0 * sum / p.getNumSamples());
    				tab2Row.add(p.getNumSamples()); tab2Row.add(p.getName());
        			tab2Borders.add(tab2.size());
        			tab2.add(tab2Row);
        		}
        	}
        	System.err.println("tab2:\t" + (System.currentTimeMillis()-ttt));

        	// Tab3
        	List<Double> doubleList = new ArrayList<Double>();
        	doubleList.add(0.008);doubleList.add(0.015);doubleList.add(0.03125);doubleList.add(0.0625);doubleList.add(0.125);doubleList.add(0.25);
        	doubleList.add(0.5);doubleList.add(1.0);doubleList.add(2.0);doubleList.add(4.0);doubleList.add(8.0);doubleList.add(16.0);
        	doubleList.add(32.0);doubleList.add(64.0);doubleList.add(128.0);doubleList.add(256.0);doubleList.add(512.0);
        	doubleList.add(1024.0);doubleList.add(2048.0);
    		for (String pkey : pkeys) {
    			Programm p = ps.get(pkey);
       	    	LinkedHashSet<List<Object>> tab3 = new LinkedHashSet<List<Object>>();
       	   		List<Object> tab3Row = new ArrayList<Object>();
       	   		tab3Row.add(""); tab3Row.add("Total"); tab3Row.add("#Positiv"); tab3Row.add("%Positiv");
       	   		for (Double dbl : doubleList) tab3Row.add(dbl);
       			tab3.add(tab3Row);
       	   		for (Integer wkey : wkeys) {
       	   			Wirkstoff w = ws.get(wkey);
       	   			String kurz = w.getKurz();
       	   			tab3Row = new ArrayList<Object>();
       	   			tab3Row.add(kurz);
       				HashMap<String, Integer> pw = p.getNumPositive();
       				int num = pw.containsKey(kurz) ? pw.get(kurz) : 0;
       				tab3Row.add(p.getNumSamples()); tab3Row.add(num); tab3Row.add(100.0 * num / p.getNumSamples()); 
    	   	   		HashMap<Double, Integer> frequencymap = p.getFrequencyMap(kurz);
    	   	   		if (frequencymap != null) {
    			   	   	for (Double dbl : doubleList) {
    			   	   		if (frequencymap.containsKey(dbl)) tab3Row.add(frequencymap.get(dbl));
    			   	   		else tab3Row.add("");
    			   	   	}	   	   			
    	   	   		}
    	   			tab3.add(tab3Row);
       			}
       	   		
       	   		for (int i=0;i<=maxResi;i++) {
       	   			tab3Row = new ArrayList<Object>();
       	   			tab3Row.add(i == 0 ? "Sensibel" : i + "x resistent");
       				int num = p.getNumResistent(i);
       				tab3Row.add(p.getNumSamples()); tab3Row.add(num); tab3Row.add(100.0 * num / p.getNumSamples());   				
       	   	   		tab3.add(tab3Row);
       	   		}
       	    	String fn = getFilename(baseFolder.getStringValue(), "MHKs_" + p.getName());
       	    	ew = new ExcelWriter(tab3);
       	    	ew.setStyle(0, 0, null, null, true, true, false, false, true, null, null); // RowHeader
       	    	ew.setStyle(null, null, 0, 0, true, false, false, true, false, null, null); // ColumnHeader
       	    	ew.setStyle(null, null, 3, 3, false, false, false, true, false, null, null); // TrennBorder
       	    	ew.setStyle(null, null, 3 + doubleList.size(), 3 + doubleList.size(), false, false, false, true, false, null, null); // LastColumnBorder
       	    	ew.setStyle(tab3.size() - maxResi - 2, tab3.size() - maxResi - 2, null, null, false, false, false, false, true, null, null); // LastRowBorder
       	    	ew.setStyle(tab3.size() - 1, tab3.size() - 1, null, null, false, false, false, false, true, null, null); // LastRowBorder
       	    	if (doAutosize) ew.autoSizeColumns(tab3Row.size());
       	    	ew.save(fn);
       		}
        	System.err.println("tab3:\t" + (System.currentTimeMillis()-ttt));

        	String fn = getFilename(baseFolder.getStringValue(), "Tab21");
        	ew = new ExcelWriter(tab1);
        	ew.setStyle(0, 0, null, null, true, true, false, false, true, null, null); // RowHeader
        	ew.setStyle(null, null, 0, 0, true, false, false, true, false, null, null); // ColumnHeader
        	for (int bl : tab1Borders) ew.setStyle(bl, bl, null, null, false, false, false, false, true, null, null); // TrennBorder
        	for (int bl : tab1BordersV) ew.setStyle(null, null, bl, bl, false, false, false, true, false, null, null); // TrennBorder
        	ew.setStyle(tab1.size() - 1, tab1.size() - 1, null, null, false, false, false, false, true, null, null); // LastRowBorder
        	if (doAutosize) ew.autoSizeColumns(tab1Row.size());
        	ew.save(fn);
        	fn = getFilename(baseFolder.getStringValue(), "Tab22");
        	ew = new ExcelWriter(tab122);
        	ew.setStyle(0, 3, null, null, true, true, false, false, true, null, null, 1, 2); // RowHeader
        	ew.setStyle(4, 4, null, null, true, true, true, false, true, null, null); // N %
        	ew.setStyle(null, null, 0, 0, true, false, false, true, false, null, null); // ColumnHeader
        	for (int bl : tab122Borders) ew.setStyle(bl, bl, null, null, false, false, false, false, true, null, null); // TrennBorder
        	for (int bl : tab122BordersV) ew.setStyle(null, null, bl, bl, false, false, false, true, false, null, null); // TrennBorder
        	ew.setStyle(tab122.size() - 1, tab122.size() - 1, null, null, false, false, false, false, true, null, null); // LastRowBorder
        	ew.setFormat4Cols(6, null, 2, 2, "0.0"); // cols with dataFormat
        	
        	if (doAutosize) ew.autoSizeColumns(tab122Row.size());
        	ew.autoSizeAllColumns(); 
        	ew.save(fn);
        	fn = getFilename(baseFolder.getStringValue(), "WGruppen");
        	ew = new ExcelWriter(tab2);
        	for (int bl : tab2Borders) ew.setStyle(bl, bl, null, null, false, false, false, false, true, null, null); // TrennBorder
        	ew.setStyle(0, 0, null, null, true, true, false, false, true, null, null); // RowHeader
        	ew.setStyle(null, null, 0, 0, true, false, false, true, false, null, null); // ColumnHeader
        	ew.setStyle(null, null, 4, 4, false, false, false, true, false, null, null); // LastColumnBorder
        	ew.setStyle(null, null, 2, 2, false, false, false, false, false, "#.###", null); // DoubleColumn
        	if (doAutosize) ew.autoSizeColumns(tab2Row.size());
        	ew.save(fn);
        	System.err.println("excelwriting:\t" + (System.currentTimeMillis()-ttt));    		
    	}

    	    	    	
    	BufferedDataContainer buf2 = exec.createDataContainer(getSpec2());

		RowKey key = RowKey.createRowKey(0L);
		DataCell[] cells = new DataCell[5];
		cells[0] = DataType.getMissingCell();
		cells[1] = DataType.getMissingCell();
		cells[2] = DataType.getMissingCell();
		cells[3] = DataType.getMissingCell();
		cells[4] = DataType.getMissingCell();
		DataRow outputRow = new DefaultRow(key, cells);
		buf2.addRowToTable(outputRow);

    	buf2.close();
        return new BufferedDataTable[]{buf2.getTable()};
    }
    private String getFilename(String baseFolder, String fbase) {
    	//baseFolder = "G:/Abteilung-4/43/Forschung/EFSA CFP_EFSA_BIOMO_2011_01/Tauschordner_AK_AW/";
    	String ser = serovar.getStringValue().replace(":", "_");
    	String agent = erreger.getStringValue();
    	if (esbl.getIntValue() == 1) {
    		if (carba.getIntValue() == 1) agent = "CARBA";
    		else agent = "ESBL";
    	}
    	else if (entero.getIntValue() == 1) agent = "Entero";
    	String filename = baseFolder + bfrProgramm.getStringValue() + "_" + agent + "_" + jahr.getIntValue();
        if (pruefPlanId.getStringValue() != null && !pruefPlanId.getStringValue().isEmpty()) {
        	filename += "_" + pruefPlanId.getStringValue();
        }
    	if (blSubFolder.getStringValue() != null && !blSubFolder.getStringValue().isEmpty()) {
    		filename += "/" + blSubFolder.getStringValue();
    	}
    	filename += "/Mass" + kriterienJahr.getIntValue() + "/" + fbase + "_" + ser + "_";
    	try {
    		String DATE_FORMAT = "yyMMdd";
    		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
    		Calendar c1 = Calendar.getInstance(); // today
    		filename += sdf.format(c1.getTime()) + ".xlsx";
    	}
    	catch (Exception e) {
    		filename += System.currentTimeMillis() + ".xlsx";
    	}
    	java.io.File f = new java.io.File(filename);
    	f.getParentFile().mkdirs();  
    	return filename;
    }
	private DataTableSpec getSpec2() {
		DataColumnSpec[] spec = new DataColumnSpec[5];
		spec[0] = new DataColumnSpecCreator("Gruppe", StringCell.TYPE).createSpec();
		spec[1] = new DataColumnSpecCreator("Sum", IntCell.TYPE).createSpec();
		spec[2] = new DataColumnSpecCreator("percent", DoubleCell.TYPE).createSpec();
		spec[3] = new DataColumnSpecCreator("totalCount", StringCell.TYPE).createSpec();
		spec[4] = new DataColumnSpecCreator("Programm", StringCell.TYPE).createSpec();
		return new DataTableSpec(spec);
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
		return new DataTableSpec[] {getSpec2()};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
    	baseFolder.saveSettingsTo(settings);
    	erreger.saveSettingsTo(settings);
    	bfrProgramm.saveSettingsTo(settings);
    	serovar.saveSettingsTo(settings);
    	blSubFolder.saveSettingsTo(settings);
    	jahr.saveSettingsTo(settings);
    	kriterienJahr.saveSettingsTo(settings);
    	pruefPlanId.saveSettingsTo(settings);
    	esbl.saveSettingsTo(settings);
    	carba.saveSettingsTo(settings);
    	entero.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	baseFolder.loadSettingsFrom(settings);
    	erreger.loadSettingsFrom(settings);
    	bfrProgramm.loadSettingsFrom(settings);
    	serovar.loadSettingsFrom(settings);
    	blSubFolder.loadSettingsFrom(settings);
    	jahr.loadSettingsFrom(settings);
    	kriterienJahr.loadSettingsFrom(settings);
    	if (settings.containsKey(PPID)) pruefPlanId.loadSettingsFrom(settings);
    	if (settings.containsKey(ESBL)) esbl.loadSettingsFrom(settings);
    	if (settings.containsKey(CARBA)) carba.loadSettingsFrom(settings);
    	if (settings.containsKey(ENTERO)) entero.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	baseFolder.validateSettings(settings);
    	erreger.validateSettings(settings);
    	bfrProgramm.validateSettings(settings);
    	serovar.validateSettings(settings);
    	blSubFolder.validateSettings(settings);
    	jahr.validateSettings(settings);
    	kriterienJahr.validateSettings(settings);
    	if (settings.containsKey(PPID)) pruefPlanId.validateSettings(settings);
    	if (settings.containsKey(ESBL)) esbl.validateSettings(settings);
    	if (settings.containsKey(CARBA)) carba.validateSettings(settings);
    	if (settings.containsKey(ENTERO)) entero.validateSettings(settings);
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

