package de.bund.bfr.gwt.krise.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.poi.hssf.usermodel.HSSFCell;

public class DBKernel {

	public static Connection theConn = null;
	
	public static void getConnection() {
		if (theConn == null) {
		    try {
			    Class.forName("org.hsqldb.jdbc.JDBCDriver").newInstance();
			    String serverPath = "localhost/tracing_ehec";//"192.168.212.54/silebat"; tracing_hepa tracing_nrw tracing_ehec
			    String connStr = "jdbc:hsqldb:hsql://" + serverPath;
			    theConn = DriverManager.getConnection(connStr, "SA", "");  
		    }
		    catch(Exception e) {e.printStackTrace();}
		}
	}
	public static ResultSet getResultSet(final String sql) {
		ResultSet ergebnis = null;
		try {
			getConnection();
		    Statement anfrage = theConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		    ergebnis = anfrage.executeQuery(sql);
		    ergebnis.first();
		}
		catch (Exception e) {System.err.println(sql);e.printStackTrace();}
		return ergebnis;
	}	
	public static String getValue4Id(String tablename, String fieldname, String id) {
		String result = null;
		String sql = "SELECT " + DBKernel.delimitL(fieldname) + " FROM " + DBKernel.delimitL(tablename) + " WHERE " + DBKernel.delimitL("ID") + " = " + id;
		try {
			ResultSet rs = DBKernel.getResultSet(sql);
			if (rs != null && rs.first()) {
				result = rs.getString(fieldname);
			}
		}
		catch (Exception e) {e.printStackTrace();}	
		return result;
	}
	public static Integer getNewId(String sql) {
		Integer result = null;
		try {
			getConnection();
			PreparedStatement ps = theConn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			if (ps.executeUpdate() > 0) {
				ResultSet res = ps.getGeneratedKeys();
				res.next();
				result = res.getInt(1);
				res.close();
			}
			ps.close();
		} catch (SQLException ex) {
			System.err.println(sql);
			ex.printStackTrace();
		}	
		return result;
	}
	public static String delimitL(final String name) {
		String newName = name.replace("\"", "\"\"");
		return "\"" + newName + "\"";
	}
	public static String getStrVal(HSSFCell cell, int maxChars) {
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
}
