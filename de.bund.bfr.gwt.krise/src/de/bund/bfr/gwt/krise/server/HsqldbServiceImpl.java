package de.bund.bfr.gwt.krise.server;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import de.bund.bfr.gwt.krise.client.HsqldbService;
import de.bund.bfr.gwt.krise.shared.Delivery;
import de.bund.bfr.gwt.krise.shared.MyField;
import de.bund.bfr.gwt.krise.shared.MyTracingData;
import de.bund.bfr.gwt.krise.shared.MyTracingGISData;
import de.bund.bfr.gwt.krise.shared.Station;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class HsqldbServiceImpl extends RemoteServiceServlet implements HsqldbService {

	public Station getStationInfo(int stationId) throws IllegalArgumentException {
		Station result = null;
		try {
			ResultSet rs = DBKernel.getResultSet("SELECT \"ID\",\"Name\",\"Longitude\",\"Latitude\" FROM \"Station\" WHERE \"ID\"="+stationId);
			if (rs != null && rs.first()) {
				result = new Station(rs.getInt("ID"), rs.getString("Name"), rs.getDouble("Longitude"), rs.getDouble("Latitude"));
			}
		}
		catch (Exception e) {e.printStackTrace();}
		return result;
	}
	public MyTracingGISData getGISData(int stationId) throws IllegalArgumentException {
		MyTracingGISData mtd = new MyTracingGISData();
		try {
			LinkedHashMap<Integer, Station> stations = new LinkedHashMap<Integer, Station>(); 
			ResultSet rs = DBKernel.getResultSet("SELECT \"ID\",\"Name\",\"Longitude\",\"Latitude\" FROM \"Station\" WHERE \"ID\"="+stationId);
			if (rs != null && rs.first()) {
				do {
					stations.put(rs.getInt("ID"), new Station(rs.getInt("ID"), rs.getString("Name"), rs.getDouble("Longitude"), rs.getDouble("Latitude")));
				} while (rs.next());
				mtd.setStations(stations);
			}
			
			rs = DBKernel.getResultSet("SELECT " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("ID") + "," + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("Station") + "," + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("Empfänger") +
					" FROM " + DBKernel.delimitL("Lieferungen") + " LEFT JOIN " + DBKernel.delimitL("Station") + " AS " + DBKernel.delimitL("S1") + " ON " + DBKernel.delimitL("Lieferungen")
					+ "." + DBKernel.delimitL("Empfänger") + "=" + DBKernel.delimitL("S1") + "." + DBKernel.delimitL("ID") + " LEFT JOIN " + DBKernel.delimitL("Chargen") + " ON " + DBKernel.delimitL("Lieferungen")
					+ "." + DBKernel.delimitL("Charge") + "=" + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("ID") + " LEFT JOIN " + DBKernel.delimitL("Produktkatalog")
					+ " ON " + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("Artikel") + "=" + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("ID") +
					" LEFT JOIN " + DBKernel.delimitL("Station") + " AS " + DBKernel.delimitL("S2") + " ON " + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("Station") + "=" + DBKernel.delimitL("S2") + "." + DBKernel.delimitL("ID")
					+ " WHERE " + DBKernel.delimitL("S1") + "." + DBKernel.delimitL("ID") + "="+stationId + " OR " + DBKernel.delimitL("S2") + "." + DBKernel.delimitL("ID") + "="+stationId
					//+ (searchString.trim().isEmpty() ? "" : " WHERE LCASE(" + DBKernel.delimitL("S1") + "." + DBKernel.delimitL("Name") + ") LIKE '%" + searchString.toLowerCase() + "%'" + " OR LCASE(" + DBKernel.delimitL("S2") + "." + DBKernel.delimitL("Name") + ") LIKE '%" + searchString.toLowerCase() + "%'")
					+ " ORDER BY " + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("ID"));
			if (rs != null && rs.first()) {
				LinkedHashMap<Integer, Delivery> deliveries = new LinkedHashMap<Integer, Delivery>(); 
				do {
					int lieferID = rs.getInt("Lieferungen.ID");
					int from = rs.getInt("Produktkatalog.Station");
					int to = rs.getInt("Lieferungen.Empfänger");
						deliveries.put(lieferID, new Delivery(lieferID, from, to));
						if (!stations.containsKey(from)) {
							ResultSet rs2 = DBKernel.getResultSet("SELECT \"Name\",\"Longitude\",\"Latitude\" FROM \"Station\" WHERE \"ID\" = " + from);
							if (rs2 != null && rs2.first()) stations.put(from, new Station(from, rs2.getString("Name"), rs2.getDouble("Longitude"), rs2.getDouble("Latitude")));
						}
						if (!stations.containsKey(to)) {
							ResultSet rs2 = DBKernel.getResultSet("SELECT \"Name\",\"Longitude\",\"Latitude\" FROM \"Station\" WHERE \"ID\" = " + to);
							if (rs2 != null && rs2.first()) stations.put(to, new Station(to, rs2.getString("Name"), rs2.getDouble("Longitude"), rs2.getDouble("Latitude")));
						}
				} while (rs.next());
				mtd.setDeliveries(deliveries);
			}
			
		}	
		catch (Exception e) {e.printStackTrace();}
		return mtd;
	}
	public MyTracingGISData getGISData(String searchString) throws IllegalArgumentException {
		try {
			int id = Integer.parseInt(searchString);
			return getGISData(id);
		}
		catch (Exception e) {}
		MyTracingGISData mtd = new MyTracingGISData();
		try {
			LinkedHashMap<Integer, Station> stations = new LinkedHashMap<Integer, Station>(); 
			ResultSet rs = DBKernel.getResultSet("SELECT \"ID\",\"Name\",\"Longitude\",\"Latitude\" FROM \"Station\"" + (searchString.trim().isEmpty() ? "" : " WHERE LCASE(\"Name\") LIKE '%" + searchString.toLowerCase() + "%'"));
			if (rs != null && rs.first()) {
				do {
					stations.put(rs.getInt("ID"), new Station(rs.getInt("ID"), rs.getString("Name"), rs.getDouble("Longitude"), rs.getDouble("Latitude")));
				} while (rs.next());
				mtd.setStations(stations);
			}
			rs = DBKernel.getResultSet("SELECT " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("ID") + "," + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("Station") + "," + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("Empfänger") +
					" FROM " + DBKernel.delimitL("Lieferungen") + " LEFT JOIN " + DBKernel.delimitL("Station") + " AS " + DBKernel.delimitL("S1") + " ON " + DBKernel.delimitL("Lieferungen")
					+ "." + DBKernel.delimitL("Empfänger") + "=" + DBKernel.delimitL("S1") + "." + DBKernel.delimitL("ID") + " LEFT JOIN " + DBKernel.delimitL("Chargen") + " ON " + DBKernel.delimitL("Lieferungen")
					+ "." + DBKernel.delimitL("Charge") + "=" + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("ID") + " LEFT JOIN " + DBKernel.delimitL("Produktkatalog")
					+ " ON " + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("Artikel") + "=" + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("ID") +
					" LEFT JOIN " + DBKernel.delimitL("Station") + " AS " + DBKernel.delimitL("S2") + " ON " + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("Station") + "=" + DBKernel.delimitL("S2") + "." + DBKernel.delimitL("ID")
					+ (searchString.trim().isEmpty() ? "" : " WHERE LCASE(" + DBKernel.delimitL("S1") + "." + DBKernel.delimitL("Name") + ") LIKE '%" + searchString.toLowerCase() + "%'" + " OR LCASE(" + DBKernel.delimitL("S2") + "." + DBKernel.delimitL("Name") + ") LIKE '%" + searchString.toLowerCase() + "%'")
					+ " ORDER BY " + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("ID"));
			if (rs != null && rs.first()) {
				LinkedHashMap<Integer, Delivery> deliveries = new LinkedHashMap<Integer, Delivery>(); 
				do {
					int lieferID = rs.getInt("Lieferungen.ID");
					int from = rs.getInt("Produktkatalog.Station");
					int to = rs.getInt("Lieferungen.Empfänger");
						deliveries.put(lieferID, new Delivery(lieferID, from, to));
						if (!stations.containsKey(from)) {
							ResultSet rs2 = DBKernel.getResultSet("SELECT \"Name\",\"Longitude\",\"Latitude\" FROM \"Station\" WHERE \"ID\" = " + from);
							if (rs2 != null && rs2.first()) stations.put(from, new Station(from, rs2.getString("Name"), rs2.getDouble("Longitude"), rs2.getDouble("Latitude")));
						}
						if (!stations.containsKey(to)) {
							ResultSet rs2 = DBKernel.getResultSet("SELECT \"Name\",\"Longitude\",\"Latitude\" FROM \"Station\" WHERE \"ID\" = " + to);
							if (rs2 != null && rs2.first()) stations.put(to, new Station(to, rs2.getString("Name"), rs2.getDouble("Longitude"), rs2.getDouble("Latitude")));
						}
				} while (rs.next());
				mtd.setDeliveries(deliveries);
			}
		}
		catch (Exception e) {e.printStackTrace();}
		return mtd;
	}
	public MyTracingData getData(int table, String id) throws IllegalArgumentException {
		MyTracingData mtd = new MyTracingData();
		ResultSet rs = null;
		HashSet<String> excludingCols = new HashSet<String>();
		if (table == MyTracingData.STATION) {
			rs = DBKernel.getResultSet("SELECT * FROM \"Station\"");
			excludingCols.add("Produktkatalog");
			excludingCols.add("Postfach");
			excludingCols.add("Telefon");
			excludingCols.add("Fax");
			excludingCols.add("EMail");
			excludingCols.add("Webseite");
			excludingCols.add("Betriebsnummer");
			excludingCols.add("Fax");
			excludingCols.add("AlterMin");
			excludingCols.add("AlterMax");
			excludingCols.add("DatumBeginn");
			excludingCols.add("DatumHoehepunkt");
			excludingCols.add("DatumEnde");
			excludingCols.add("Erregernachweis");
		}
		else if (table == MyTracingData.PRODUCT) {
			rs = DBKernel.getResultSet("SELECT * FROM \"Produktkatalog\" WHERE \"Station\" = " + id);
			excludingCols.add("Station");
			excludingCols.add("Matrices");
			excludingCols.add("Chargen");
		}
		else if (table == MyTracingData.LOT) {
			rs = DBKernel.getResultSet("SELECT * FROM \"Chargen\" WHERE \"Artikel\" = " + id);
			excludingCols.add("Artikel");
			//excludingCols.add("Zutaten");
			excludingCols.add("Lieferungen");
		}
		else if (table == MyTracingData.DELIVERY) {
			rs = DBKernel.getResultSet("SELECT * FROM \"Lieferungen\" WHERE \"Charge\" = " + id);
			excludingCols.add("Charge");
		}
		if (rs != null) {
			try {
				LinkedHashSet<MyField> cols = new LinkedHashSet<MyField>();
				int numCols = rs.getMetaData().getColumnCount();
				for (int i=0;i<numCols;i++) {
					String colname = rs.getMetaData().getColumnName(i+1);
					if (!excludingCols.contains(colname)) {
						int ct = rs.getMetaData().getColumnType(i+1);
						MyField mf = null;
						if (colname.equals("Zutaten")) mf = new MyField(colname, MyField.ITEM);
						else if (ct == Types.DATE) mf = new MyField(colname, MyField.DATE);
						else if (ct == Types.DOUBLE) mf = new MyField(colname, MyField.FLOAT);
						else if (ct == Types.VARCHAR) mf = new MyField(colname, MyField.TEXT);
						else if (ct == Types.INTEGER) mf = new MyField(colname, MyField.INTEGER);
						else if (ct == Types.BOOLEAN) mf = new MyField(colname, MyField.BOOLEAN);
						else System.err.println("type missing: " + rs.getMetaData().getColumnTypeName(i+1));
						if (mf != null && colname.equals("Empfänger")) {
							LinkedHashMap<String, String> stationMap = new LinkedHashMap<String, String>();
							ResultSet rss = DBKernel.getResultSet("SELECT * FROM \"Station\"");
							if (rss != null && rss.first()) {
								do {
									stationMap.put(rss.getString("ID"), rss.getString("Name"));
								} while(rss.next());
							}
							mf.setValueMap(stationMap);
						}
						if (ct == Types.VARCHAR) mf.setMaxLength(rs.getMetaData().getColumnDisplaySize(i+1));
						cols.add(mf);
					}
				}
				if (rs.first()) {
					List<List<String>> rows = new ArrayList<List<String>>();
					LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Boolean>>> items = new LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Boolean>>>();
					do {
						List<String> record = new ArrayList<String>();
						for (MyField column : cols) {
							if (column.getName().equals("Zutaten")) {
								LinkedHashMap<String, LinkedHashMap<String, Boolean>> lhmsb0 = new LinkedHashMap<String, LinkedHashMap<String, Boolean>>();
								ResultSet rs2 = DBKernel.getResultSet("SELECT \"Station\" FROM \"Produktkatalog\" WHERE \"ID\" = " + id);
								if (rs2 != null && rs2.first()) {
									int stationid = rs2.getInt(1);
									ResultSet rs3 = DBKernel.getResultSet("SELECT * FROM \"Lieferungen\" LEFT JOIN \"Chargen\" ON \"Lieferungen\".\"Charge\" = \"Chargen\".\"ID\" LEFT JOIN \"Produktkatalog\" ON \"Chargen\".\"Artikel\" = \"Produktkatalog\".\"ID\" WHERE \"Empfänger\" = " + stationid);
									ResultSet rs4 = DBKernel.getResultSet("SELECT \"Zutat\" FROM \"ChargenVerbindungen\" WHERE \"Produkt\" = " + record.get(0));
									LinkedHashMap<String, Boolean> lhmsb = new LinkedHashMap<String, Boolean>();
									if (rs3 != null && rs3.first()) {
										do {
											boolean isZutat = false;
											if (rs4 != null && rs4.first()) {
												do {
													if (rs4.getInt(1) == rs3.getInt("Lieferungen.ID")) {
														isZutat = true;
														break; 
													}
												} while (rs4.next());
											}
											lhmsb.put(rs3.getString("Produktkatalog.Bezeichnung") + ";" + rs3.getString("Chargen.ChargenNr") + ";" + rs3.getString("Lieferungen.dd_day")+"."+rs3.getString("Lieferungen.dd_month")+"."+rs3.getString("Lieferungen.dd_year"), isZutat);
										} while (rs3.next());
										lhmsb0.put(record.get(0), lhmsb);
									}
								}
								items.put("Zutaten", lhmsb0);
							}
							record.add(rs.getObject(column.getName()) == null ? null : rs.getString(column.getName()));
						}
				  		rows.add(record);
					} while (rs.next());
					mtd.setCols(cols);
					mtd.setRows(rows);
					mtd.setItems(items);
				}
			}
			catch (Exception e) {e.printStackTrace();}
		}
		return mtd;
	}	
}
