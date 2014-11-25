package de.bund.bfr.knime.aaw.hartung;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
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
		sheet = wb.getSheet("import");
		if (sheet != null) {
			for (int i=0; i<wb.getNumberOfNames(); i++) {
	            Name name = wb.getNameAt(i);
	            if (!"import".equals(name.getSheetName())) continue;
	            if (name.getNameName().equals("Print_Area")) continue;

	            try {
	                AreaReference area = new AreaReference(name.getRefersToFormula());
	                if (area.isSingleCell()) {
	                    CellReference crList[] = area.getAllReferencedCells();
	                    if (crList[0].getCol() == 1) {
	                    	System.err.println(crList[0].getRow() + "\t" + crList[0].getCol() + "\t" + name.getNameName());
	                    	for (int j=crList[0].getRow();;j++) {
	                    		row = sheet.getRow(i);
	                    		HSSFCell cell = row.getCell(1);
	                    	}
	                    }
	                }
	            }
	            catch (Exception e) {}
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

