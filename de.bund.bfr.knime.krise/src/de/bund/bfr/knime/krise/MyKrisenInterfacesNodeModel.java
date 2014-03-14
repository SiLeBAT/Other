package de.bund.bfr.knime.krise;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hsh.bfr.db.DBKernel;
import org.hsh.bfr.db.Hsqldbiface;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.BooleanCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.xml.XMLCell;
import org.knime.core.data.xml.XMLCellFactory;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import com.thoughtworks.xstream.XStream;

import de.bund.bfr.knime.openkrise.MyNewTracing;

/**
 * This is the model implementation of MyKrisenInterfaces.
 * 
 *
 * @author draaw
 */
public class MyKrisenInterfacesNodeModel extends NodeModel {
    
	static final String PARAM_FILENAME = "filename";
	static final String PARAM_LOGIN = "login";
	static final String PARAM_PASSWD = "passwd";
	static final String PARAM_OVERRIDE = "override";
	static final String PARAM_CC = "crosscontamination";
	static final String PARAM_ETO = "enforcetemporalorder";
	static final String PARAM_CLUSTERING = "clustering";
	static final String PARAM_ANONYMIZE = "anonymize";
	static final String PARAM_FILTER_COMPANY = "filter_Company";
	static final String PARAM_FILTER_CHARGE = "filter_Charge";
	static final String PARAM_FILTER_ARTIKEL = "filter_Artikel";
	static final String PARAM_ANTIARTICLE = "antiArtikel";
	static final String PARAM_FILTERBACKIFMIXED = "goBackFilterIfMixed";
	static final String PARAM_FILTERFORWARD = "goForwardFilter";
	static final String PARAM_ANTICOMPANY = "antiCompany";
	static final String PARAM_SHOWCASES = "showCases";
	static final String PARAM_FILTERCASESENSITIVITY = "filterCaseSensitivity";
	static final String PARAM_FILTERORAND = "OrAnd";
	
	static final String PARAM_FILTER_DATEFROM = "dateFrom";
	static final String PARAM_FILTER_DATETO = "dateTo";

	private String filename;
	private String login;
	private String passwd;
	private boolean filterCaseSensitive;
	private String OrAnd;
	private boolean override;
	private boolean doAnonymize;
	private boolean doCC, doETO = true, doClustering;
	private String companyFilter = "", chargeFilter = "", artikelFilter = "";
	private boolean antiArticle = false, antiCompany = false, goBackFilterIfMixed = false, goForwardFilter = false, showCases = false;
	private String dateFrom, dateTo;
		
	private MyRelations mr = null;
	private MyRealChain mrc = null;

	private boolean isDE = false;

	/**
     * Constructor for the node model.
     */
    protected MyKrisenInterfacesNodeModel() {
        super(0, 3);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
    	Hsqldbiface db = null;
    	if (override) {
			db = new Hsqldbiface(filename, login, passwd);
		} else {
			db = new Hsqldbiface(DBKernel.getLocalConn(true));
		}
    	//if (doAnonymize) doAnonymizeHard(db.getConnection());

    	mrc = new MyRealChain();

    	System.err.println("Starting MyRelations...");
    	mr = new MyRelations(db);
    	
    	System.err.println("Starting Filters...");
    	MyChain companyFilterList = applyCompanyFilter(db);
    	MyChain chargeFilterList = applyChargeFilter(db);
    	MyChain articleFilterList = applyArticleFilter(db);
    	
    	System.err.println("Starting Plausibility Checks...");
		// Date_In <= Date_Out???
		String sql = "SELECT \"ChargenVerbindungen\".\"ID\" AS \"ID\", \"L1\".\"ID\" AS \"ID_In\", \"L2\".\"ID\" AS \"ID_Out\", \"L1\".\"dd_day\" AS \"Day_In\",\"L2\".\"dd_day\" AS \"Day_Out\", \"L1\".\"dd_month\" AS \"Month_In\",\"L2\".\"dd_month\" AS \"Month_Out\", \"L1\".\"dd_year\" AS \"Year_In\",\"L2\".\"dd_year\" AS \"Year_Out\" FROM \"Lieferungen\" AS \"L1\" LEFT JOIN \"ChargenVerbindungen\" ON \"L1\".\"ID\"=\"ChargenVerbindungen\".\"Zutat\" LEFT JOIN \"Lieferungen\" AS \"L2\" ON \"L2\".\"Charge\"=\"ChargenVerbindungen\".\"Produkt\" WHERE \"ChargenVerbindungen\".\"ID\" IS NOT NULL AND (\"L2\".\"dd_year\" < \"L1\".\"dd_year\" OR \"L2\".\"dd_year\" = \"L1\".\"dd_year\" AND \"L2\".\"dd_month\" < \"L1\".\"dd_month\" OR \"L2\".\"dd_year\" = \"L1\".\"dd_year\" AND \"L2\".\"dd_month\" = \"L1\".\"dd_month\" AND \"L2\".\"dd_day\" < \"L1\".\"dd_day\")";
    	ResultSet rsp = db.pushQuery(sql);
    	while (rsp.next()) {
    		System.err.println("Dates correct?? In: " + rsp.getInt("ID_In") + " (" + rsp.getInt("Day_In") + "." + rsp.getInt("Month_In") + "." + rsp.getInt("Year_In") + ") vs. Out: " + rsp.getInt("ID_Out") + " (" + rsp.getInt("Day_Out") + "." + rsp.getInt("Month_Out") + "." + rsp.getInt("Year_Out") + ")");
    	}
		// Sum(In) <=> Sum(Out)???
    	sql = "select GROUP_CONCAT(\"id1\") AS \"ids_in\",sum(\"Amount_In\") AS \"Amount_In\",min(\"Amount_Out\") AS \"Amount_Out\",min(\"id2\") as \"ids_out\" from (SELECT min(\"L1\".\"ID\") AS \"id1\",GROUP_CONCAT(\"L2\".\"ID\") AS \"id2\",min(\"L1\".\"Unitmenge\") AS \"Amount_In\",sum(\"L2\".\"Unitmenge\") AS \"Amount_Out\" FROM \"Lieferungen\" AS \"L1\" LEFT JOIN \"ChargenVerbindungen\" ON \"L1\".\"ID\"=\"ChargenVerbindungen\".\"Zutat\" LEFT JOIN \"Lieferungen\" AS \"L2\" ON \"L2\".\"Charge\"=\"ChargenVerbindungen\".\"Produkt\" WHERE \"ChargenVerbindungen\".\"ID\" IS NOT NULL GROUP BY \"L1\".\"ID\") GROUP BY \"id2\"";
    	rsp = db.pushQuery(sql);
    	while (rsp.next()) {
    		if (rsp.getObject("Amount_In") != null && rsp.getObject("Amount_Out") != null) {
    			double in = rsp.getDouble("Amount_In");
    			double out = rsp.getDouble("Amount_Out");
    			if (in > out * 1.1 || out > in * 1.1) {
            		System.err.println("Amounts correct?? In: " + rsp.getString("ids_in") + " (" + in + " kg) vs. Out: " + rsp.getString("ids_out") + " (" + out + ")");    			
    			}
    		}
    	}

    	System.err.println("Starting Tracing...");
    	MyNewTracing mnt = MyNewTracingLoader.getNewTracingModel(db, doCC, doETO, false);
/*
    	System.err.println("Starting Wordle...");
    	BufferedDataContainer outputWordle = exec.createDataContainer(getSpecWordle());
    	if (mnt != null) {
    		int rowNumber = 0;
    		LinkedHashMap<Integer, HashSet<Integer>> scoreDeliveries = mnt.getScores(false);
    		HashMap<Integer, String> caseKeywords = new HashMap<Integer, String>(); 
        	for (Integer lieferID : scoreDeliveries.keySet()) {
    			sql = "SELECT " + DBKernel.delimitL("Station") + "." + DBKernel.delimitL("Name") +
    					"," + DBKernel.delimitL("Station") + "." + DBKernel.delimitL("ID") +
    					"," + DBKernel.delimitL("Station") + "." + DBKernel.delimitL("Bundesland") +
    					"," + DBKernel.delimitL("Station") + "." + DBKernel.delimitL("Land") +
    					"," + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("Bezeichnung") +
    					"," + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("ChargenNr") +
    					" FROM " + DBKernel.delimitL("Lieferungen") +
    					" LEFT JOIN " + DBKernel.delimitL("Chargen") +
    					" ON " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("Charge") + "=" + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("ID") +
    					" LEFT JOIN " + DBKernel.delimitL("Produktkatalog") +
    					" ON " + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("Artikel") + "=" + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("ID") +
	        			" LEFT JOIN " + DBKernel.delimitL("Station") +
	        			" ON " + DBKernel.delimitL("Station") + "." + DBKernel.delimitL("ID") + "=" + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("Station") +
    					" WHERE " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("ID") + "=" + lieferID;
            	ResultSet rs = db.pushQuery(sql);
    	    	while (rs.next()) {
            		String bl = getBL(rs.getString("Bundesland"));
            		String country = getBL(rs.getString("Land"), 3);
            		int stationID = rs.getInt("Station.ID");
            		String company  = (rs.getObject("Name") == null || doAnonymize) ? bl + stationID + "(" + country + ")" : rs.getString("Name");
            		
            		HashSet<Integer> hi = scoreDeliveries.get(lieferID);
            		for (Integer caseID : hi) {
            			if (!caseKeywords.containsKey(caseID)) caseKeywords.put(caseID, "");
            			String chainKeywords = caseKeywords.get(caseID);
        	    		if (chainKeywords.indexOf(" " + company + " ") < 0) chainKeywords += " " + company + " ";
        	    		if (rs.getString("Bezeichnung") != null && chainKeywords.indexOf(" " + rs.getString("Bezeichnung") + " ") < 0) chainKeywords += " " + rs.getString("Bezeichnung") + " ";
        	    		if (rs.getString("ChargenNr") != null && chainKeywords.indexOf(" " + rs.getString("ChargenNr") + " ") < 0) chainKeywords += " " + rs.getString("ChargenNr") + " ";
        	    		caseKeywords.put(caseID, chainKeywords);
            		}
    	    	}
        	}
        	for (Integer caseID : caseKeywords.keySet()) {
        	    DataCell[] cells = new DataCell[2];
        	    cells[0] = new StringCell(caseKeywords.get(caseID));
        	    cells[1] = new IntCell(1);
        	    RowKey key = RowKey.createRowKey(++rowNumber);
        	    DataRow outputRow = new DefaultRow(key, cells);
        	    outputWordle.addRowToTable(outputRow);        		
        	}
    	}
    	outputWordle.close();
*/
    	System.err.println("Starting Nodes33...");
    	//HashSet<Integer> toBeMerged = new HashSet<Integer>();
    	//LinkedHashMap<Integer, String> id2Code = new LinkedHashMap<Integer, String>(); 
    	// Alle Stationen -> Nodes33
    	BufferedDataContainer output33Nodes = exec.createDataContainer(getSpec33Nodes());
    	ResultSet rs = db.pushQuery("SELECT * FROM " + DBKernel.delimitL("Station"));
    	int rowNumber = 0;
    	while (rs.next()) {
    		int stationID = rs.getInt("ID");
    		//if (!antiArticle || !checkCompanyReceivedArticle(stationID, articleFilterList) || !checkCase(stationID)) {
        		String bl = getBL(rs.getString("Bundesland"));
        		String country = getBL(rs.getString("Land"), 3);
        		String company  = (rs.getObject("Name") == null || doAnonymize) ? bl + stationID + "(" + country + ")" : rs.getString("Name");
        		//if (rs.getObject("Land") != null && rs.getString("Land").equals("Serbia")) toBeMerged.add(stationID);
        		//id2Code.put(stationID, company);
        	    RowKey key = RowKey.createRowKey(rowNumber);
        	    DataCell[] cells = new DataCell[17];
        	    cells[0] = new IntCell(stationID);
        	    cells[1] = new StringCell(company);
        	    //cells[2] = new StringCell("square"); // circle, square, triangle
        	    //cells[3] = new DoubleCell(1.5);
        	    //cells[4] = new StringCell("yellow"); // red, yellow
        	    cells[2] = (rs.getObject("PLZ") == null) ? DataType.getMissingCell() : new StringCell(rs.getString("PLZ"));
        	    cells[3] = (doAnonymize || rs.getObject("Ort") == null) ? DataType.getMissingCell() : new StringCell(rs.getString("Ort"));
        	    cells[4] = (doAnonymize || rs.getObject("Bundesland") == null || rs.getString("Bundesland").equals("NULL")) ? DataType.getMissingCell() : new StringCell(rs.getString("Bundesland"));
        	    cells[5] = (doAnonymize || rs.getObject("Land") == null) ? DataType.getMissingCell() : new StringCell(rs.getString("Land"));
        	    cells[6] = (rs.getObject("Betriebsart") == null) ? DataType.getMissingCell() : new StringCell(rs.getString("Betriebsart"));
        	    double casePriority = rs.getDouble("CasePriority");
        	    if (casePriority < 0) casePriority = 0;
        	    if (casePriority  > 1) casePriority = 1;        	    
        	    cells[7] = (rs.getObject("CasePriority") == null) ? DataType.getMissingCell() : new DoubleCell(casePriority);
        	    cells[8] = (rs.getObject("AnzahlFaelle") == null) ? DataType.getMissingCell() : new IntCell(rs.getInt("AnzahlFaelle")); // DataType.getMissingCell()
        	    cells[9] = (rs.getObject("DatumBeginn") == null) ? DataType.getMissingCell() : new StringCell(rs.getString("DatumBeginn"));
        	    cells[10] = (rs.getObject("DatumHoehepunkt") == null) ? DataType.getMissingCell() : new StringCell(rs.getString("DatumHoehepunkt"));
        	    cells[11] = (rs.getObject("DatumEnde") == null) ? DataType.getMissingCell() : new StringCell(rs.getString("DatumEnde"));
        	    if (company.toLowerCase().indexOf("d.o.o") >= 0) {
        	    	System.out.print("");
        	    }
        		boolean filterTrue = (
        				!showCases && companyFilter.trim().isEmpty() || companyFilterList.containsValue(stationID)) &&
        				(chargeFilter.trim().isEmpty() || chargeFilterList.containsValue(stationID)) &&
        				(artikelFilter.trim().isEmpty() || !antiArticle && articleFilterList.containsValue(stationID) || antiArticle && !articleFilterList.containsValue(stationID));
            	cells[12] = filterTrue ? BooleanCell.TRUE : BooleanCell.FALSE; // OnFilter
             	if (mnt != null) {
            		cells[13] = new DoubleCell(mnt.getStationScore(stationID));
            		cells[14] = mnt.isStationStart(stationID) ? BooleanCell.TRUE : BooleanCell.FALSE; 
            		cells[15] = mnt.isStationEnd(stationID) ? BooleanCell.TRUE : BooleanCell.FALSE; 
            	}
            	else {
            		cells[13] = DataType.getMissingCell();
            		cells[14] = DataType.getMissingCell();
            		cells[15] = DataType.getMissingCell();
            	}
        	    cells[16] = (rs.getObject("Serial") == null) ? DataType.getMissingCell() : new StringCell(rs.getString("Serial"));

        	    
        	    DataRow outputRow = new DefaultRow(key, cells);

        	    output33Nodes.addRowToTable(outputRow);
    		//}
    	    exec.checkCanceled();
    	    //exec.setProgress(rowNumber / 10000, "Adding row " + rowNumber);

    	    rowNumber++;
    	}
    	output33Nodes.close();
    	rs.close();

    	//mnt.mergeStations(toBeMerged);
    	//System.err.println(mnt.getStationScore(-1));
    	
    	System.err.println("Starting Links33...");
    	// Alle Lieferungen -> Links33
    	BufferedDataContainer output33Links = exec.createDataContainer(getSpec33Links());
    	rs = db.pushQuery("SELECT * FROM " + DBKernel.delimitL("Lieferungen") +
    			" LEFT JOIN " + DBKernel.delimitL("Chargen") +
    			" ON " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("Charge") + "=" + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("ID") +
    			" LEFT JOIN " + DBKernel.delimitL("Produktkatalog") +
    			" ON " + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("Artikel") + "=" + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("ID") +
    			" ORDER BY " + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("ID"));
    	rowNumber = 0;
    	while (rs.next()) {
    		int lieferID = rs.getInt("Lieferungen.ID");
    		boolean filterTrue = (companyFilter.trim().isEmpty() || companyFilterList.containsKey(lieferID)) &&
    				(chargeFilter.trim().isEmpty() || chargeFilterList.containsKey(lieferID)) &&
    				(artikelFilter.trim().isEmpty() || !antiArticle && articleFilterList.containsKey(lieferID) || antiArticle && !articleFilterList.containsKey(lieferID));
    		//if (filterTrue) {
        		int id1 = rs.getInt("Produktkatalog.Station");
        		int id2 = rs.getInt("Lieferungen.Empfänger");
        		//if (id2Code.containsKey(id1) && id2Code.containsKey(id2)) {
            		int from = id1;//id2Code.get(id1);
            		int to = id2;//id2Code.get(id2);
            	    RowKey key = RowKey.createRowKey(rowNumber);
            	    DataCell[] cells = new DataCell[22];
            	    cells[0] = new IntCell(from);
            	    cells[1] = new IntCell(to);
            	    //cells[2] = new StringCell("black"); // black
            	    cells[2] = (doAnonymize || rs.getObject("Artikelnummer") == null) ? DataType.getMissingCell() : new StringCell(rs.getString("Artikelnummer"));
            	    cells[3] = (rs.getObject("Bezeichnung") == null) ? DataType.getMissingCell() : new StringCell(rs.getString("Bezeichnung"));
            	    cells[4] = (rs.getObject("Prozessierung") == null) ? DataType.getMissingCell() : new StringCell(rs.getString("Prozessierung"));
            	    cells[5] = (rs.getObject("IntendedUse") == null) ? DataType.getMissingCell() : new StringCell(rs.getString("IntendedUse"));
            	    cells[6] = (doAnonymize || rs.getObject("ChargenNr") == null) ? DataType.getMissingCell() : new StringCell(rs.getString("ChargenNr"));
            	    String mhd = sdfFormat(rs.getString("MHD_day"), rs.getString("MHD_month"), rs.getString("MHD_year"));
            	    cells[7] = (mhd == null) ? DataType.getMissingCell() : new StringCell(mhd);
            	    String pd = sdfFormat(rs.getString("pd_day"), rs.getString("pd_month"), rs.getString("pd_year"));
            	    cells[8] = (pd == null) ? DataType.getMissingCell() : new StringCell(pd);
            	    String dd = sdfFormat(rs.getString("Lieferungen.dd_day"), rs.getString("Lieferungen.dd_month"), rs.getString("Lieferungen.dd_year"));
            	    cells[9] = (dd == null) ? DataType.getMissingCell() : new StringCell(dd);
            	    Double menge = calcMenge(rs.getObject("Unitmenge"), rs.getObject("UnitEinheit"));
            	    cells[10] = menge == null ? DataType.getMissingCell() : new DoubleCell(menge / 1000.0); // Menge [kg]
            	    cells[11] = new StringCell("Row" + rowNumber);
                	cells[12] = filterTrue ? BooleanCell.TRUE : BooleanCell.FALSE; // OnFilter
                	if (mnt != null) cells[13] = new DoubleCell(mnt.getDeliveryScore(lieferID));
                	else cells[13] = DataType.getMissingCell();
                	cells[14] = new IntCell(lieferID);
            	    cells[15] = (rs.getObject("Lieferungen.Serial") == null) ? DataType.getMissingCell() : new StringCell(rs.getString("Lieferungen.Serial"));
            	    cells[16] = (rs.getObject("Chargen.OriginCountry") == null) ? DataType.getMissingCell() : new StringCell(rs.getString("Chargen.OriginCountry"));

            		cells[17] = (rs.getObject("Lieferungen.EndChain") == null) ? DataType.getMissingCell() : new StringCell(rs.getString("Lieferungen.EndChain"));
            		cells[18] = (rs.getObject("Lieferungen.Explanation_EndChain") == null) ? DataType.getMissingCell() : new StringCell(rs.getString("Lieferungen.Explanation_EndChain"));
            		cells[19] = (rs.getObject("Lieferungen.Contact_Questions_Remarks") == null) ? DataType.getMissingCell() : new StringCell(rs.getString("Lieferungen.Contact_Questions_Remarks"));
            		cells[20] = (rs.getObject("Lieferungen.Further_Traceback") == null) ? DataType.getMissingCell() : new StringCell(rs.getString("Lieferungen.Further_Traceback"));
            		cells[21] = (rs.getObject("Chargen.MicrobioSample") == null) ? DataType.getMissingCell() : new StringCell(rs.getString("Chargen.MicrobioSample"));
            		
                	DataRow outputRow = new DefaultRow(key, cells);

            	    output33Links.addRowToTable(outputRow);
            	    rowNumber++;
        		//}
        		//else {
        		//	exec.setMessage(id1 + " or " + id2 + " not found in Stationen...");
        		//}
    		//}
    	    exec.checkCanceled();
    	    //exec.setProgress(rowNumber / (double)inData[0].getRowCount(), "Adding row " + rowNumber);   	    
    	}
    	output33Links.close();
    	rs.close();

    	BufferedDataContainer buf = exec.createDataContainer(getDataModelSpec());
    	if (!doCC) {
        	buf.addRowToTable(new DefaultRow("0", XMLCellFactory.create(getDataModel(mnt))));
    	}
    	buf.close();
    	//getDataModel(buf.getTable());
    	
    	System.err.println("Fin!");
    	return new BufferedDataTable[]{output33Nodes.getTable(), output33Links.getTable(), buf.getTable()}; // outputWordle.getTable(), outputBurow.getTable(), outputBurowNew.getTable(), 
    }
	private String sdfFormat(String day, String month, String year) {
		if (day == null || day.trim().isEmpty() && month == null || month.trim().isEmpty() && year == null || year.trim().isEmpty()) return null;
		return day + "." + month + "." + year;
	}
    /*
    private MyNewTracing getDataModel(BufferedDataTable table) {
    	MyNewTracing newMnt = null;
    	for (DataRow row : table) {
    		DataCell cell = row.getCell(0);
    		String xml = ((StringValue) cell).getStringValue();
    		XStream xstream = MyNewTracing.getXStream();
    		newMnt = (MyNewTracing) xstream.fromXML(xml);
    		
    		// Attention: it is essential to call fillDeliveries after importing from xml!!!!!!
    		newMnt.fillDeliveries();
    		
    		// Examples for settings
    		newMnt.setCase(1, 0.5);
    		newMnt.setCrossContamination(1, true);
    		
    		// Examples for score fetching
    		newMnt.getStationScore(1);
    		newMnt.getDeliveryScore(1);
    		break;
        }
    	return newMnt;
    }
    */
    private String getDataModel(MyNewTracing mnt) {
		XStream xstream = MyNewTracing.getXStream();
		String xml = xstream.toXML(mnt);
		//System.err.println(xml);
		System.err.println(xml.length());
		return xml;    		
    }
    private Double calcMenge(Object u3, Object bu3) {
    	Double result = null;
    	if (u3 != null && bu3 != null) {
    		Double u3d = (Double) u3;
    		String bu3s = bu3.toString();
    		if (bu3s.equalsIgnoreCase("t")) result = u3d * 1000000;
    		else if (bu3s.equalsIgnoreCase("kg")) result = u3d * 1000;
    		else result = u3d; // if (bu3s.equalsIgnoreCase("g")) 
    	}
    	return result;
    }
    /*
    private DataTableSpec getSpecTracing() {
    	DataColumnSpec[] spec = new DataColumnSpec[10];
    	spec[0] = new DataColumnSpecCreator("Lfd", IntCell.TYPE).createSpec();
    	spec[1] = new DataColumnSpecCreator("NumCases", IntCell.TYPE).createSpec();
    	spec[2] = new DataColumnSpecCreator("CommonObjectID", IntCell.TYPE).createSpec();
    	spec[3] = new DataColumnSpecCreator("CommonObject", StringCell.TYPE).createSpec();
    	spec[4] = new DataColumnSpecCreator("ClusterCenterIDs", StringCell.TYPE).createSpec();
    	spec[5] = new DataColumnSpecCreator("ClusterCenters", StringCell.TYPE).createSpec();
    	spec[6] = new DataColumnSpecCreator("NumClusters", IntCell.TYPE).createSpec();
    	spec[7] = new DataColumnSpecCreator("OmittedNodes", StringCell.TYPE).createSpec();
    	spec[8] = new DataColumnSpecCreator("HitRate", DoubleCell.TYPE).createSpec();
    	spec[9] = new DataColumnSpecCreator("FkLink", BooleanCell.TYPE).createSpec();
    	return new DataTableSpec(spec);
    }
    */
    private DataTableSpec getDataModelSpec() {
    	DataColumnSpec[] spec = new DataColumnSpec[1];
    	spec[0] = new DataColumnSpecCreator("DataModel", XMLCell.TYPE).createSpec();
    	return new DataTableSpec(spec);
    }
    /*
    private DataTableSpec getSpecWordle() {
    	DataColumnSpec[] spec = new DataColumnSpec[2];
    	spec[0] = new DataColumnSpecCreator("Words", StringCell.TYPE).createSpec();
    	spec[1] = new DataColumnSpecCreator("Weight", IntCell.TYPE).createSpec();
    	return new DataTableSpec(spec);
    }
    */
    private DataTableSpec getSpec33Nodes() {
    	DataColumnSpec[] spec = new DataColumnSpec[17];
    	spec[0] = new DataColumnSpecCreator("ID", IntCell.TYPE).createSpec();
    	spec[1] = new DataColumnSpecCreator("node", StringCell.TYPE).createSpec();
    	spec[2] = new DataColumnSpecCreator(isDE ? "PLZ" : "ZIP", StringCell.TYPE).createSpec();    
    	spec[3] = new DataColumnSpecCreator(isDE ? "Ort" : "City", StringCell.TYPE).createSpec();    
    	spec[4] = new DataColumnSpecCreator(isDE ? "Bundesland" : "County", StringCell.TYPE).createSpec();    
    	spec[5] = new DataColumnSpecCreator(isDE ? "Land" : "Country", StringCell.TYPE).createSpec();    
    	spec[6] = new DataColumnSpecCreator(isDE ? "Betriebsart" : "type of business", StringCell.TYPE).createSpec();    
    	spec[7] = new DataColumnSpecCreator("CasePriority", DoubleCell.TYPE).createSpec();    
    	spec[8] = new DataColumnSpecCreator(isDE ? "NumFaelle" : "Number Cases", IntCell.TYPE).createSpec();    
    	spec[9] = new DataColumnSpecCreator(isDE ? "DatumBeginn" : "Date start", StringCell.TYPE).createSpec();    
    	spec[10] = new DataColumnSpecCreator(isDE ? "DatumHoehepunkt" : "Date peak", StringCell.TYPE).createSpec();    
    	spec[11] = new DataColumnSpecCreator(isDE ? "DatumEnde" : "Date end", StringCell.TYPE).createSpec();    
    	spec[12] = new DataColumnSpecCreator("OnFilter", BooleanCell.TYPE).createSpec();    
    	spec[13] = new DataColumnSpecCreator("TracingScore", DoubleCell.TYPE).createSpec();    
    	spec[14] = new DataColumnSpecCreator("DeadStart", BooleanCell.TYPE).createSpec();    
    	spec[15] = new DataColumnSpecCreator("DeadEnd", BooleanCell.TYPE).createSpec();    
    	spec[16] = new DataColumnSpecCreator("Serial", StringCell.TYPE).createSpec(); 
    	return new DataTableSpec(spec);
    }
    private DataTableSpec getSpec33Links() {
    	DataColumnSpec[] spec = new DataColumnSpec[22];
    	spec[0] = new DataColumnSpecCreator("from", IntCell.TYPE).createSpec();
    	spec[1] = new DataColumnSpecCreator("to", IntCell.TYPE).createSpec();
    	spec[2] = new DataColumnSpecCreator(isDE ? "Artikelnummer" : "Item Number", StringCell.TYPE).createSpec();
    	spec[3] = new DataColumnSpecCreator(isDE ? "Bezeichnung" : "Name", StringCell.TYPE).createSpec();
    	spec[4] = new DataColumnSpecCreator(isDE ? "Prozessierung" : "Processing", StringCell.TYPE).createSpec();
    	spec[5] = new DataColumnSpecCreator("IntendedUse", StringCell.TYPE).createSpec();
    	spec[6] = new DataColumnSpecCreator(isDE ? "ChargenNr" : "Charge Number", StringCell.TYPE).createSpec();
    	spec[7] = new DataColumnSpecCreator(isDE ? "MHD" : "Date Expiration", StringCell.TYPE).createSpec();
    	spec[8] = new DataColumnSpecCreator(isDE ? "Herstellungsdatum" : "Date Manufactoring", StringCell.TYPE).createSpec();
    	spec[9] = new DataColumnSpecCreator(isDE ? "Lieferdatum" : "Date Delivery", StringCell.TYPE).createSpec();
    	spec[10] = new DataColumnSpecCreator(isDE ? "Menge [kg]" : "Amount [kg]", DoubleCell.TYPE).createSpec();
    	spec[11] = new DataColumnSpecCreator("EdgeID", StringCell.TYPE).createSpec();
    	spec[12] = new DataColumnSpecCreator("OnFilter", BooleanCell.TYPE).createSpec();    
    	spec[13] = new DataColumnSpecCreator("TracingScore", DoubleCell.TYPE).createSpec();    
    	spec[14] = new DataColumnSpecCreator("ID", IntCell.TYPE).createSpec();    
    	spec[15] = new DataColumnSpecCreator("Serial", StringCell.TYPE).createSpec(); 
    	spec[16] = new DataColumnSpecCreator("OriginCountry", StringCell.TYPE).createSpec(); 
    	spec[17] = new DataColumnSpecCreator("EndChain", StringCell.TYPE).createSpec();    
    	spec[18] = new DataColumnSpecCreator("ExplanationEndChain", StringCell.TYPE).createSpec();    
    	spec[19] = new DataColumnSpecCreator("Contact_Questions_Remarks", StringCell.TYPE).createSpec();    
    	spec[20] = new DataColumnSpecCreator("FurtherTB", StringCell.TYPE).createSpec();    
    	spec[21] = new DataColumnSpecCreator("MicroSample", StringCell.TYPE).createSpec();    
    	return new DataTableSpec(spec);
    }
    
    private String getBL(String bl) {
    	return getBL(bl, 2);
    }
    private String getBL(String bl, int numCharsMax) {
    	String result = bl;
    	if (result == null || result.trim().isEmpty() || result.trim().equalsIgnoreCase("null")) result = "NN";
    	if (result.length() > numCharsMax) {
    		result = result.substring(0, numCharsMax);
    	}
    	return result;
    }
    
    
    private MyChain applyChargeFilter(Hsqldbiface db) throws SQLException {
    	MyChain result = new MyChain(); 
    	if (!chargeFilter.trim().isEmpty()) {
	    	ResultSet rs = db.pushQuery("SELECT " + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("Station") + "," +
	    			DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("ID") + "," +
	    			DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("Empfänger") +
	    			" FROM " + DBKernel.delimitL("Produktkatalog") +
	    			" LEFT JOIN " + DBKernel.delimitL("Chargen") +
	    			" ON " + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("Artikel") + "=" + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("ID") +
	    			" LEFT JOIN " + DBKernel.delimitL("Lieferungen") +
	    			" ON " + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("ID") + "=" + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("Charge") +
	    			" WHERE " + getFilterAsSQL(DBKernel.delimitL("ChargenNr"), chargeFilter));
			while (rs.next()) {
				int lieferID = rs.getInt("ID");
				if (lieferID > 0) {
					result.put(lieferID, rs.getInt("Empfänger"));
					int senderID = rs.getInt("Station");
					result.put(-lieferID, senderID);
			    	if (goForwardFilter) goForward(db, lieferID, result);
			    	if (goBackFilterIfMixed) goBackward(db, lieferID, result);
				}
			}
    	}
		return result;
    }
    private MyChain applyArticleFilter(Hsqldbiface db) throws SQLException {
    	MyChain result = new MyChain(); 
    	if (!artikelFilter.trim().isEmpty()) {
	    	ResultSet rs = db.pushQuery("SELECT " + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("Station") + "," +
	    			DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("ID") + "," +DBKernel.delimitL("Bezeichnung") + "," +
	    			DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("Empfänger") +
	    			" FROM " + DBKernel.delimitL("Produktkatalog") +
	    			" LEFT JOIN " + DBKernel.delimitL("Chargen") +
	    			" ON " + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("Artikel") + "=" + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("ID") +
	    			" LEFT JOIN " + DBKernel.delimitL("Lieferungen") +
	    			" ON " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("Charge") + "=" + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("ID") +
	    			" WHERE " + getFilterAsSQL(DBKernel.delimitL("Bezeichnung"), artikelFilter));
			while (rs.next()) {
				int lieferID = rs.getInt("ID");
				if (lieferID > 0) {
					int receiverID = rs.getInt("Empfänger");
					result.put(lieferID, receiverID);
					int senderID = rs.getInt("Station");
					result.put(-lieferID, senderID);
					if (goForwardFilter) goForward(db, lieferID, result);
			    	if (goBackFilterIfMixed) goBackward(db, lieferID, result);
				}
			}
    	}
		return result;
    }
    private String getFilterAsSQL(String fieldname, String filter) {
    	String result = "";
    	//String[] parts = filter.split(" ", 0);
        String[] parts = filter.split(" (?=([^\"]*\"[^\"]*\")*[^\"]*$)");
    	for (int i=0;i<parts.length;i++) {
    		if (!parts[i].trim().isEmpty()) {
    			//parts[i] = parts[i].replace("%", "\\%");
    			//parts[i] = parts[i].replace("_", "\\_");
    			if (parts[i].startsWith("\"") && parts[i].endsWith("\"")) parts[i] = parts[i].substring(1, parts[i].length() - 1);
        		if (filterCaseSensitive) result += " " + OrAnd + " " + fieldname + " LIKE '%" + parts[i] + "%'";
        		else result += " " + OrAnd + " UCASE(" + fieldname + ") LIKE '%" + parts[i].toUpperCase() + "%'";
    		}
    	}
    	if (!result.isEmpty()) result = result.substring(OrAnd.length() + 2);
    	return result;
    }
    private MyChain applyCompanyFilter(Hsqldbiface db) throws SQLException {
    	MyChain result = new MyChain(); 
    	if (!companyFilter.trim().isEmpty() || showCases) {
    		String where = " WHERE ";
    		if (!companyFilter.trim().isEmpty()) where += getFilterAsSQL(DBKernel.delimitL("Name"), companyFilter);
    		if (showCases) where += (where.length() > 7 ? " OR " : "") + DBKernel.delimitL("CasePriority") + " > 0";
        	ResultSet rs = db.pushQuery("SELECT " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("ID") + "," +
        			DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("Empfänger") +
        			" FROM " + DBKernel.delimitL("Station") +
        			" LEFT JOIN " + DBKernel.delimitL("Lieferungen") +
        			" ON " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("Empfänger") + "=" + DBKernel.delimitL("Station") + "." + DBKernel.delimitL("ID") +
        			where);
    		while (rs.next()) {
    			int lieferID = rs.getInt("ID");
    			if (lieferID > 0) {
    				result.put(lieferID, rs.getInt("Empfänger"));
    				if (goForwardFilter) goForward(db, lieferID, result);
    		    	if (goBackFilterIfMixed) goBackward(db, lieferID, result);
    			}
    		}
        	rs = db.pushQuery("SELECT " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("ID") +
       			 "," + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("Empfänger") +
        			 "," + DBKernel.delimitL("Station") + "." + DBKernel.delimitL("ID") +
        			" FROM " + DBKernel.delimitL("Station") +
        			" LEFT JOIN " + DBKernel.delimitL("Produktkatalog") +
        			" ON " + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("Station") + "=" + DBKernel.delimitL("Station") + "." + DBKernel.delimitL("ID") +
        			" LEFT JOIN " + DBKernel.delimitL("Chargen") +
        			" ON " + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("Artikel") + "=" + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("ID") +
        			" LEFT JOIN " + DBKernel.delimitL("Lieferungen") +
        			" ON " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("Charge") + "=" + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("ID") +
        			where);
    		while (rs.next()) {
    			int lieferID = rs.getInt("Lieferungen.ID");
    			if (lieferID > 0) {
    				result.put(MathUtilities.getRandomNegativeInt(), rs.getInt("Station.ID"));
    				result.put(lieferID, goForwardFilter ? rs.getInt("Empfänger") : 0);
    				if (goForwardFilter) goForward(db, lieferID, result);
    		    	if (goBackFilterIfMixed) goBackward(db, lieferID, result);				
    			}
    		}
    	}
    	return result;
    }
    
    private void goForward(Hsqldbiface db, int lieferID, MyChain results) throws SQLException {
		String sql = "SELECT " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("ID") +
			"," + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("Empfänger") +
			" FROM " + DBKernel.delimitL("ChargenVerbindungen") +
			" LEFT JOIN " + DBKernel.delimitL("Lieferungen") +
			" ON " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("Charge") + "=" + DBKernel.delimitL("ChargenVerbindungen") + "." + DBKernel.delimitL("Produkt") +
			" WHERE " + DBKernel.delimitL("Zutat") + "=" + lieferID;
		if (doCC) {
			// L2 ist zeitlich die erste Station
			sql = "SELECT " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("ID") +
					"," + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("Empfänger") +
					" FROM " + DBKernel.delimitL("Lieferungen") +
    				" LEFT JOIN " + DBKernel.delimitL("Chargen") +
    				" ON " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("Charge") + "=" + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("ID") +
    				" LEFT JOIN " + DBKernel.delimitL("Produktkatalog") +
    				" ON " + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("Artikel") + "=" + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("ID") +
	        		" LEFT JOIN " + DBKernel.delimitL("Station") +
	        		" ON " + DBKernel.delimitL("Station") + "." + DBKernel.delimitL("ID") + "=" + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("Station") +
	        		" LEFT JOIN " + DBKernel.delimitL("Lieferungen") + " AS " + DBKernel.delimitL("L2") + 
	        		" ON " + DBKernel.delimitL("Station") + "." + DBKernel.delimitL("ID") + "=" + DBKernel.delimitL("L2") + "." + DBKernel.delimitL("Empfänger") +
    				" WHERE " + DBKernel.delimitL("L2") + "." + DBKernel.delimitL("ID") + "=" + lieferID +
    				(doETO ?
						" AND " +
    					"(" + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("dd_year") + " IS NULL" +
						" OR " + DBKernel.delimitL("L2") + "." + DBKernel.delimitL("dd_year") + " IS NULL" +
						" OR " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("dd_year") + ">" + DBKernel.delimitL("L2") + "." + DBKernel.delimitL("dd_year") + ")"
    					
						+ " OR " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("dd_year") + "=" + DBKernel.delimitL("L2") + "." + DBKernel.delimitL("dd_year") +
						" AND (" + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("dd_month") + " IS NULL" +
						" OR " + DBKernel.delimitL("L2") + "." + DBKernel.delimitL("dd_month") + " IS NULL" +
						" OR " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("dd_month") + ">" + DBKernel.delimitL("L2") + "." + DBKernel.delimitL("dd_month") + ")"
    					
						+ " OR " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("dd_month") + "=" + DBKernel.delimitL("L2") + "." + DBKernel.delimitL("dd_month") +
						" AND (" + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("dd_day") + " IS NULL" +
						" OR " + DBKernel.delimitL("L2") + "." + DBKernel.delimitL("dd_day") + " IS NULL" +
						" OR " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("dd_day") + ">=" + DBKernel.delimitL("L2") + "." + DBKernel.delimitL("dd_day") + ")"
    					:
    					"");
		}
		ResultSet rs = db.pushQuery(sql);
		while (rs.next()) {
			int newLieferID = rs.getInt("ID");
			if (!results.containsKey(newLieferID)) {
				//getObjectFromLieferID(db, objectType, newLieferID);
				results.put(newLieferID, rs.getInt("Empfänger"));
				mrc.addData(lieferID, newLieferID);
				goForward(db, newLieferID, results);
			}
		}
    }
    private void goBackward(Hsqldbiface db, int lieferID, MyChain results) throws SQLException {
    	String sql = "SELECT " + DBKernel.delimitL("Zutat") +
    				"," + DBKernel.delimitL("ZutatLieferungen") + "." + DBKernel.delimitL("Empfänger") +
    				" FROM " + DBKernel.delimitL("Lieferungen") + " AS " + DBKernel.delimitL("LieferungLieferungen") +
    				" LEFT JOIN " + DBKernel.delimitL("ChargenVerbindungen") +
    				" ON " + DBKernel.delimitL("LieferungLieferungen") + "." + DBKernel.delimitL("Charge") + "=" + DBKernel.delimitL("ChargenVerbindungen") + "." + DBKernel.delimitL("Produkt") +
    				" LEFT JOIN " + DBKernel.delimitL("Lieferungen") + " AS " + DBKernel.delimitL("ZutatLieferungen") +
    				" ON " + DBKernel.delimitL("ZutatLieferungen") + "." + DBKernel.delimitL("ID") + "=" + DBKernel.delimitL("ChargenVerbindungen") + "." + DBKernel.delimitL("Zutat") +
    				" WHERE " + DBKernel.delimitL("LieferungLieferungen") + "." + DBKernel.delimitL("ID") + "=" + lieferID;
		if (doCC) {
			sql = "SELECT " + DBKernel.delimitL("L2") + "." + DBKernel.delimitL("ID") +
					"," + DBKernel.delimitL("L2") + "." + DBKernel.delimitL("Empfänger") +
					" FROM " + DBKernel.delimitL("Lieferungen") +
    				" LEFT JOIN " + DBKernel.delimitL("Chargen") +
    				" ON " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("Charge") + "=" + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("ID") +
    				" LEFT JOIN " + DBKernel.delimitL("Produktkatalog") +
    				" ON " + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("Artikel") + "=" + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("ID") +
	        		" LEFT JOIN " + DBKernel.delimitL("Station") +
	        		" ON " + DBKernel.delimitL("Station") + "." + DBKernel.delimitL("ID") + "=" + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("Station") +
	        		" LEFT JOIN " + DBKernel.delimitL("Lieferungen") + " AS " + DBKernel.delimitL("L2") + 
	        		" ON " + DBKernel.delimitL("Station") + "." + DBKernel.delimitL("ID") + "=" + DBKernel.delimitL("L2") + "." + DBKernel.delimitL("Empfänger") +
    				" WHERE " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("ID") + "=" + lieferID +
    				(doETO ?
						" AND " +
    					"(" + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("dd_year") + " IS NULL" +
						" OR " + DBKernel.delimitL("L2") + "." + DBKernel.delimitL("dd_year") + " IS NULL" +
						" OR " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("dd_year") + ">" + DBKernel.delimitL("L2") + "." + DBKernel.delimitL("dd_year") + ")"
    					
						+ " OR " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("dd_year") + "=" + DBKernel.delimitL("L2") + "." + DBKernel.delimitL("dd_year") +
						" AND (" + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("dd_month") + " IS NULL" +
						" OR " + DBKernel.delimitL("L2") + "." + DBKernel.delimitL("dd_month") + " IS NULL" +
						" OR " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("dd_month") + ">" + DBKernel.delimitL("L2") + "." + DBKernel.delimitL("dd_month") + ")"
    					
						+ " OR " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("dd_month") + "=" + DBKernel.delimitL("L2") + "." + DBKernel.delimitL("dd_month") +
						" AND (" + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("dd_day") + " IS NULL" +
						" OR " + DBKernel.delimitL("L2") + "." + DBKernel.delimitL("dd_day") + " IS NULL" +
						" OR " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("dd_day") + ">=" + DBKernel.delimitL("L2") + "." + DBKernel.delimitL("dd_day") + ")"
    					:
    					"");
		}
		ResultSet rs = db.pushQuery(sql);
		while (rs.next()) {
			int newLieferID = doCC ? rs.getInt("ID") : rs.getInt("Zutat");
			if (newLieferID > 0 && !results.containsKey(newLieferID)) {
				int eid = rs.getInt("Empfänger");
				//getObjectFromLieferID(db, objectType, newLieferID);
				results.put(newLieferID, eid);
				mrc.addData(newLieferID, lieferID);
				goBackward(db, newLieferID, results);
			}
			else { // Lieferant noch einfügen
				int sfo = mr.getStationFromObject(lieferID, MyRelations.LIEFERUNG);
				results.putDeliverer(sfo);
			}
		}
    }
    
    @SuppressWarnings("unused")
	private void doAnonymizeHard(Connection conn) {
		String sql = "SELECT * FROM " + DBKernel.delimitL("Station");
		ResultSet rs = DBKernel.getResultSet(conn, sql, false);
		try {
			if (rs != null && rs.first()) {
				do {
            		String bl = getBL(rs.getString("Bundesland"));
            		String country = getBL(rs.getString("Land"), 3);
            		int stationID = rs.getInt("ID");
            		String anonStr = bl + stationID + "(" + country + ")";
					sql = "UPDATE " + DBKernel.delimitL("Station") + " SET " + DBKernel.delimitL("Name") + "='" + anonStr +
							"', " + DBKernel.delimitL("Strasse") + "=NULL, " + DBKernel.delimitL("Hausnummer") + "=NULL, " +
							DBKernel.delimitL("Ort") + "=NULL WHERE " + DBKernel.delimitL("ID") + "=" + rs.getInt("ID");
					DBKernel.sendRequest(conn, sql, false, false);
					sql = "UPDATE " + DBKernel.delimitL("Station") + " SET " + DBKernel.delimitL("Betriebsnummer") + "=NULL WHERE " + DBKernel.delimitL("ID") + "=" + rs.getInt("ID");
					DBKernel.sendRequest(conn, sql, false, false);
				} while (rs.next());
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
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
    	//DBKernel.convertEHEC2NewDB("Samen");
    	// evtl. auch: else if (DBKernel.isKrise) { ... nochmal auskommentieren
    	//DBKernel.convertEHEC2NewDB("Cluster");
        return new DataTableSpec[]{getSpec33Nodes(), getSpec33Links(), getDataModelSpec()}; // getSpecBurow(), null, getSpecWordle(),  
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
    	settings.addString(PARAM_FILENAME, filename);
    	settings.addString(PARAM_LOGIN, login);
    	settings.addString(PARAM_PASSWD, passwd);
    	settings.addBoolean(PARAM_OVERRIDE, override);
    	//settings.addBoolean( PARAM_TRACINGBACK, tracingBack );
    	//settings.addInt( PARAM_TRACINGTYPE, objectType );
    	//settings.addInt( PARAM_TRACINGOMITMAX, tracingOmitMax );
    	settings.addBoolean( PARAM_CC, doCC );
    	settings.addBoolean(PARAM_ETO, doETO);
    	settings.addBoolean( PARAM_CLUSTERING, doClustering );
    	
    	settings.addBoolean(PARAM_FILTERCASESENSITIVITY, filterCaseSensitive);
    	settings.addBoolean( PARAM_ANONYMIZE, doAnonymize );
    	settings.addString(PARAM_FILTERORAND, OrAnd);
    	settings.addString( PARAM_FILTER_COMPANY, companyFilter );
    	settings.addString( PARAM_FILTER_CHARGE, chargeFilter );
    	settings.addString( PARAM_FILTER_ARTIKEL, artikelFilter );
    	settings.addBoolean( PARAM_ANTIARTICLE, antiArticle );
    	settings.addBoolean( PARAM_FILTERBACKIFMIXED, goBackFilterIfMixed );
    	settings.addBoolean(PARAM_FILTERFORWARD, goForwardFilter);
    	settings.addBoolean( PARAM_ANTICOMPANY, antiCompany );
    	settings.addBoolean( PARAM_SHOWCASES, showCases );
    	
    	settings.addString(PARAM_FILTER_DATEFROM, dateFrom);
    	settings.addString(PARAM_FILTER_DATETO, dateTo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	filename = settings.getString(PARAM_FILENAME);
    	login = settings.getString(PARAM_LOGIN);
    	passwd = settings.getString(PARAM_PASSWD);
    	override = settings.getBoolean(PARAM_OVERRIDE);
    	//tracingBack = settings.getBoolean( PARAM_TRACINGBACK );
    	//objectType = settings.getInt( PARAM_TRACINGTYPE );
    	//tracingOmitMax = settings.containsKey(PARAM_TRACINGOMITMAX) ? settings.getInt(PARAM_TRACINGOMITMAX) : 3;
    	if (settings.containsKey(PARAM_CC)) doCC = settings.getBoolean( PARAM_CC );
    	if (settings.containsKey(PARAM_ETO)) doETO = settings.getBoolean(PARAM_ETO);
    	if (settings.containsKey(PARAM_CLUSTERING)) doClustering = settings.getBoolean( PARAM_CLUSTERING );
    	if (settings.containsKey(PARAM_FILTERCASESENSITIVITY)) filterCaseSensitive = settings.getBoolean(PARAM_FILTERCASESENSITIVITY);
    	doAnonymize = settings.getBoolean(PARAM_ANONYMIZE, false);
    	companyFilter = settings.getString(PARAM_FILTER_COMPANY, "");
    	if (settings.containsKey(PARAM_FILTERORAND)) OrAnd = settings.getString(PARAM_FILTERORAND);
    	chargeFilter = settings.getString(PARAM_FILTER_CHARGE, "");
    	artikelFilter = settings.getString(PARAM_FILTER_ARTIKEL, "");
    	antiArticle = settings.getBoolean(PARAM_ANTIARTICLE, false);
    	goBackFilterIfMixed = settings.getBoolean(PARAM_FILTERBACKIFMIXED, false);
    	goForwardFilter = settings.getBoolean(PARAM_FILTERFORWARD, false);
    	antiCompany = settings.getBoolean(PARAM_ANTICOMPANY, false);
    	if (settings.containsKey(PARAM_SHOWCASES)) showCases = settings.getBoolean(PARAM_SHOWCASES);
    	
    	dateFrom = settings.getString(PARAM_FILTER_DATEFROM);
    	dateTo = settings.getString(PARAM_FILTER_DATETO);
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

