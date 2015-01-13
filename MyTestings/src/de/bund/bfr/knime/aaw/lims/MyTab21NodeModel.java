package de.bund.bfr.knime.aaw.lims;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.RowKey;
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

/**
 * This is the model implementation of MyTab21.
 * 
 *
 * @author aaw
 */
public class MyTab21NodeModel extends NodeModel {
    
    /**
     * Constructor for the node model.
     */
    protected MyTab21NodeModel() {
        super(2, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
    	// Wirkstoffe cutoffs einlesen
    	HashMap<String, Wirkstoff> wss = new HashMap<String, Wirkstoff>();
    	HashMap<Integer, Wirkstoff> ws = new HashMap<Integer, Wirkstoff>();
    	DataTableSpec dts = inData[1].getSpec();
    	String[] cn = dts.getColumnNames();
    	for (DataRow row : inData[1]) {
    		Wirkstoff w = new Wirkstoff();
    		for (int i=0;i<dts.getNumColumns();i++) {
    			DataCell dc = row.getCell(i);
        	    if (!dc.isMissing()) {
        			if (cn[i].equalsIgnoreCase("Gruppe")) w.setGruppe(((StringCell) dc).getStringValue());
        			else if (cn[i].equalsIgnoreCase("Name")) w.setName(((StringCell) dc).getStringValue());
        			else if (cn[i].equalsIgnoreCase("Kurz")) w.setKurz(((StringCell) dc).getStringValue());
        			else if (cn[i].equalsIgnoreCase("cutoff")) w.setCutoff(((DoubleCell) dc).getDoubleValue());
        			else if (cn[i].equalsIgnoreCase("Sort")) w.setIndexSort(((IntCell) dc).getIntValue());
        	    }
    		}
    		if (w.getIndexSort() != null) {
    			ws.put(w.getIndexSort(), w);
    			wss.put(w.getKurz(), w);
    		}
    	}

    	// dataset einlesen
    	HashMap<String, Programm> ps = new HashMap<String, Programm>();
    	dts = inData[0].getSpec();
    	cn = dts.getColumnNames();
    	for (DataRow row : inData[0]) {
    		Programm p = new Programm();
    		for (int i=0;i<dts.getNumColumns();i++) {
    			DataCell dc = row.getCell(i);
        	    if (!dc.isMissing()) {
        			if (cn[i].equalsIgnoreCase("Programm_kurz")) p.setName(((StringCell) dc).getStringValue());
        			else if (wss.containsKey(cn[i])) p.addWirkstoff(wss.get(cn[i]), ((DoubleCell) dc).getDoubleValue());
        	    }
    		}
    		if (p.getName() != null) {
        		p.sampleFin();
    			if (!ps.containsKey(p.getName())) ps.put(p.getName(), p);
    			else ps.get(p.getName()).merge(p);
    		}
    	}
    	
    	// Ergebnisse berechnen und ausgeben
    	// Tab1
    	LinkedHashSet<List<Object>> tab1 = new LinkedHashSet<List<Object>>();
    	SortedSet<String> pkeys = new TreeSet<String>(ps.keySet());
		int maxResi = 0;
		for (String pkey : pkeys) {
			Programm p = ps.get(pkey);
			if (p.getMaxResi() > maxResi) maxResi = p.getMaxResi();
		}
   		SortedSet<Integer> wkeys = new TreeSet<Integer>(ws.keySet());
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

   		List<Object> tab1Row = new ArrayList<Object>();
		tab1Row.add("");
    	for (String pkey : pkeys) {
    		tab1Row.add(pkey); tab1Row.add(pkey + " (#Positiv)"); tab1Row.add(pkey + " (%Positiv)");
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
	   	   		tab1Row.add(p.getNumSamples()); tab1Row.add(num); tab1Row.add(100.0 * num / p.getNumSamples());   				
   			}
   			tab1.add(tab1Row);
   		}
   		
   		for (int i=0;i<=maxResi;i++) {
   	   		tab1Row = new ArrayList<Object>();
   	   		tab1Row.add(i == 0 ? "Sensibel" : i + "x resistent");
   			for (String pkey : pkeys) {
   				Programm p = ps.get(pkey);
   				int num = p.getNumResistent(i);
   	   	   		tab1Row.add(p.getNumSamples()); tab1Row.add(num); tab1Row.add(100.0 * num / p.getNumSamples());   				
   			}
   	   		tab1.add(tab1Row);
   		}

    	// Tab2
    	LinkedHashSet<List<Object>> tab2 = new LinkedHashSet<List<Object>>();
   		List<Integer> tab2Borders = new ArrayList<Integer>();
   		List<Object> tab2Row = new ArrayList<Object>();
   		tab2Row.add("Gruppe"); tab2Row.add("Sum"); tab2Row.add("percent"); tab2Row.add("totalCount"); tab2Row.add("Programm");
		tab2Borders.add(tab2.size());
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


    	String fn = getFilename("C:/Dokumente und Einstellungen/Weiser/Desktop/tawak/", "Tab21");
    	ExcelWriter ew = new ExcelWriter(tab1, null);
    	ew.setStyle(true, 0, true, true, false, false, false, null);
    	ew.autoSizeColumns(tab1Row.size());
    	ew.save(fn);
    	fn = getFilename("C:/Dokumente und Einstellungen/Weiser/Desktop/tawak/", "Tab212");
    	ew = new ExcelWriter(tab2, null);
    	for (int bl : tab2Borders) ew.setStyle(true, bl, false, false, false, false, true, null);
    	ew.setStyle(false, 1, false, false, true, false, false, null);
    	ew.setStyle(false, 4, false, false, false, true, false, null);
    	ew.setStyle(false, 2, false, false, false, false, false, "#.###");
    	ew.setStyle(true, 0, true, true, false, false, false, null);
    	ew.setStyle(false, 0, true, false, false, false, false, null);
    	ew.autoSizeColumns(tab2Row.size());
    	ew.save(fn);
    	    	    	
    	BufferedDataContainer buf2 = exec.createDataContainer(getSpec2());

		RowKey key = RowKey.createRowKey(0);
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
    	String filename = baseFolder + "BfRProg_Erreger_Jahr/MassKrit/" + fbase + "_ser_";
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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
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

