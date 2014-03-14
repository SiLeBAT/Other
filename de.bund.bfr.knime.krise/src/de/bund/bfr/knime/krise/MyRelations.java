package de.bund.bfr.knime.krise;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;

import org.hsh.bfr.db.DBKernel;
import org.hsh.bfr.db.Hsqldbiface;

public class MyRelations {

	static final int STATION = 0;
	static final int ARTIKEL = 1;
	static final int CHARGE = 2;
	static final int LIEFERUNG = 3;
	
	private HashMap<Integer, Integer> stationFromLieferID;
	private HashMap<Integer, Integer> articleFromLieferID;
	private HashMap<Integer, Integer> chargeFromLieferID;
	
	private HashMap<Integer, Integer> stationFromArticleID;
	private HashMap<Integer, Integer> stationFromChargeID;
	
	private HashMap<Integer, HashSet<Integer>> lieferungenFromArticleID;
	private HashMap<Integer, HashSet<Integer>> lieferungenFromChargeID;
	private HashMap<Integer, HashSet<Integer>> lieferungenFromStationID;
	private HashMap<Integer, HashSet<Integer>> lieferungenFromLieferID;
	
	public MyRelations(Hsqldbiface db) {
		stationFromLieferID = new HashMap<Integer, Integer>();
		articleFromLieferID = new HashMap<Integer, Integer>();
		chargeFromLieferID = new HashMap<Integer, Integer>();
		stationFromArticleID = new HashMap<Integer, Integer>();
		stationFromChargeID = new HashMap<Integer, Integer>();
		lieferungenFromArticleID = new HashMap<Integer, HashSet<Integer>>();
		lieferungenFromChargeID = new HashMap<Integer, HashSet<Integer>>();
		lieferungenFromStationID = new HashMap<Integer, HashSet<Integer>>();
		lieferungenFromLieferID = new HashMap<Integer, HashSet<Integer>>();
		fillHashes(db);
	}
	public Integer getStationFromObject(Integer objectID, int objectType) {
		if (objectType == ARTIKEL) return stationFromArticleID.get(objectID);
		else if (objectType == CHARGE) return stationFromChargeID.get(objectID);
		else if (objectType == LIEFERUNG) return stationFromLieferID.get(objectID);
		else return objectID; // STATION
	}
	public HashSet<Integer> getLieferungFromObject(Integer objectID, int objectType) {
		if (objectType == ARTIKEL) return lieferungenFromArticleID.get(objectID);
		else if (objectType == CHARGE) return lieferungenFromChargeID.get(objectID);
		else if (objectType == STATION) return lieferungenFromStationID.get(objectID);
		else return lieferungenFromLieferID.get(objectID); // LIEFERUNG
	}
	public Integer getObject(int lieferID, int objectType) {
		if (objectType == ARTIKEL) return articleFromLieferID.get(lieferID);
		else if (objectType == CHARGE) return chargeFromLieferID.get(lieferID);
		else if (objectType == STATION) return stationFromLieferID.get(lieferID);
		else return lieferID; // LIEFERUNG
	}
	private void fillHashes(Hsqldbiface db) {
		try {
			ResultSet rs = db.pushQuery("SELECT " + DBKernel.delimitL("ID") + " FROM " + DBKernel.delimitL("Lieferungen"));
			while (rs.next()) {
				int id = rs.getInt("ID");
				HashSet<Integer> hs = new HashSet<Integer>();
				hs.add(id); lieferungenFromLieferID.put(id, hs);
				setObjects4LieferID(db, STATION, id, stationFromLieferID);
				setObjects4LieferID(db, ARTIKEL, id, articleFromLieferID);
				setObjects4LieferID(db, CHARGE, id, chargeFromLieferID);
			}
			
			rs = db.pushQuery("SELECT " + DBKernel.delimitL("ID") + " FROM " + DBKernel.delimitL("Produktkatalog"));
			while (rs.next()) {
				int id = rs.getInt("ID");
				if (stationFromArticleID.containsKey(id)) continue;
				else stationFromArticleID.put(id, getStationFromSQL(db, getStationSQL(id, ARTIKEL)));
				
				HashSet<Integer> hs = getLieferungenFromSQL(db, getLieferungSQL(id, ARTIKEL));
				lieferungenFromArticleID.put(id, hs);
			}
			
			rs = db.pushQuery("SELECT " + DBKernel.delimitL("ID") + " FROM " + DBKernel.delimitL("Chargen"));
			while (rs.next()) {
				int id = rs.getInt("ID");
				if (stationFromChargeID.containsKey(id)) continue;
				else stationFromChargeID.put(id, getStationFromSQL(db, getStationSQL(id, CHARGE)));
				
				HashSet<Integer> hs = getLieferungenFromSQL(db, getLieferungSQL(id, CHARGE));
				lieferungenFromChargeID.put(id, hs);
			}

			setLieferungenFromStation(db);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
    private void setObjects4LieferID(Hsqldbiface db, int objectType, int id, HashMap<Integer, Integer> objectFromID) throws SQLException {
    	if (objectFromID.containsKey(id)) return;
		String sql = "";
		if (objectType == STATION) {
			sql = "SELECT " + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("Station") + " AS " + DBKernel.delimitL("Object") +
    				" FROM " + DBKernel.delimitL("Lieferungen") +
    				" LEFT JOIN " + DBKernel.delimitL("Chargen") +
    				" ON " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("Charge") + "=" + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("ID") +
    				" LEFT JOIN " + DBKernel.delimitL("Produktkatalog") +
    				" ON " + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("Artikel") + "=" + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("ID") +
    				" WHERE " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("ID") + "=" + id;
		}
		else if (objectType == ARTIKEL) {
			sql = "SELECT " + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("Artikel") + " AS " + DBKernel.delimitL("Object") +
    				" FROM " + DBKernel.delimitL("Lieferungen") +
    				" LEFT JOIN " + DBKernel.delimitL("Chargen") +
    				" ON " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("Charge") + "=" + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("ID") +
    				" WHERE " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("ID") + "=" + id;
		}
		else if (objectType == CHARGE) {
			sql = "SELECT " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("Charge") + " AS " + DBKernel.delimitL("Object") +
    				" FROM " + DBKernel.delimitL("Lieferungen") +
    				" WHERE " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("ID") + "=" + id;
		}
		else {//if (objectType == LIEFERUNG) {
			sql = "SELECT " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("ID") + " AS " + DBKernel.delimitL("Object") +
    				" FROM " + DBKernel.delimitL("Lieferungen") +
    				" WHERE " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("ID") + "=" + id;
		}
			
		ResultSet rs = db.pushQuery(sql);
		rs.next();
		objectFromID.put(id, rs.getInt("Object"));
		if (rs.next()) {
			System.err.println("LieferID has several objects... " + id);
		}
    }
    private String getStationSQL(int id, int objectType) {
    	String sql = "";
    	if (objectType == STATION) {
		}
		else if (objectType == ARTIKEL) {
			sql = "SELECT " + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("Station") +
					" FROM " + DBKernel.delimitL("Produktkatalog") +
					" WHERE " + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("ID") + "=" + id;
		}
		else if (objectType == CHARGE) {
			sql = "SELECT " + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("Station") +
					" FROM " + DBKernel.delimitL("Chargen") +
					" LEFT JOIN " + DBKernel.delimitL("Produktkatalog") +
					" ON " + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("Artikel") + "=" + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("ID") +
					" WHERE " + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("ID") + "=" + id;
		}
		else if (objectType == LIEFERUNG) {
			sql = "SELECT " + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("Station") +
					" FROM " + DBKernel.delimitL("Lieferungen") +
					" LEFT JOIN " + DBKernel.delimitL("Chargen") +
					" ON " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("Charge") + "=" + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("ID") +
					" LEFT JOIN " + DBKernel.delimitL("Produktkatalog") +
					" ON " + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("Artikel") + "=" + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("ID") +
					" WHERE " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("ID") + "=" + id;
		}
		return sql;
    }
    private String getLieferungSQL(int id, int objectType) {
    	String sql = "";
    	if (objectType == STATION) {
		}
		else if (objectType == ARTIKEL) {
			sql = "SELECT " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("ID") +
					" FROM " + DBKernel.delimitL("Lieferungen") +
					" LEFT JOIN " + DBKernel.delimitL("Chargen") +
					" ON " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("Charge") + "=" + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("ID") +
					" WHERE " + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("Artikel") + "=" + id;
		}
		else if (objectType == CHARGE) {
			sql = "SELECT " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("ID") +
					" FROM " + DBKernel.delimitL("Lieferungen") +
					" WHERE " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("Charge") + "=" + id;
		}
		else if (objectType == LIEFERUNG) {
		}
		return sql;
    }
    private int getStationFromSQL(Hsqldbiface db, String sql) {
    	if (sql.length() > 0) {
    		try {
    			ResultSet rs = db.pushQuery(sql);
    	    	while (rs.next()) {
    	    		return rs.getInt(1);
    	    	}
    		}
    		catch (SQLException e) {
    			e.printStackTrace();
    		}
    	}
    	return 0;
    }
    private HashSet<Integer> getLieferungenFromSQL(Hsqldbiface db, String sql) {
    	HashSet<Integer> result = new HashSet<Integer>();
    	if (sql.length() > 0) {
    		try {
    			ResultSet rs = db.pushQuery(sql);
    	    	while (rs.next()) {
    	    		int lieferID = rs.getInt(1);
    	    		result.add(lieferID);
    	    	}
    		}
    		catch (SQLException e) {
    			e.printStackTrace();
    		}
    	}
    	return result;
    }
    private void setLieferungenFromStation(Hsqldbiface db) { // was hat eine Station alles geliefert?
		String sql = "SELECT " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("ID") +
				"," + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("Station") +
				//"," + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("Empfänger") +
				" FROM " + DBKernel.delimitL("Lieferungen") +
				" LEFT JOIN " + DBKernel.delimitL("Chargen") +
				" ON " + DBKernel.delimitL("Lieferungen") + "." + DBKernel.delimitL("Charge") + "=" + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("ID") +
				" LEFT JOIN " + DBKernel.delimitL("Produktkatalog") +
				" ON " + DBKernel.delimitL("Chargen") + "." + DBKernel.delimitL("Artikel") + "=" + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("ID") +
				" ORDER BY " + DBKernel.delimitL("Produktkatalog") + "." + DBKernel.delimitL("Station") + " ASC";
		
		try {
			HashSet<Integer> hs = new HashSet<Integer>();
			ResultSet rs = db.pushQuery(sql);
			int stationID = -1;
	    	while (rs.next()) {
	    		if (stationID != rs.getInt(2)) {
	    			if (stationID > 0) lieferungenFromStationID.put(stationID, hs);
	    			hs = new HashSet<Integer>();
	    			stationID = rs.getInt(2);
	    		}
	    		hs.add(rs.getInt(1));
	    	}
			if (stationID > 0) lieferungenFromStationID.put(stationID, hs);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
    }
}
