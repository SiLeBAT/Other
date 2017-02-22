package de.bund.bfr.knime.aaw.lims;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;

public class MyBvlDs {

	private final SimpleDateFormat bvlFormatter = new SimpleDateFormat("dd-MMM-yy"); //"17-Feb-15";
	private final SimpleDateFormat bvlFormatter2 = new SimpleDateFormat("yyyy-MM-dd"); //"2016-12-15"

	private String PROBEN_NR = null;
	private Integer TEILPROBEN_NR = null;
	private String VORBEFUND = null;
	private String ZERL_MATRIX = null;
	private String PROBENAHME_DAT = null;
	private Long PROBENAHME_MILLIS = null;
	private String ProbenahmeOrt = null;
	private String Betriebsart = null;
	private DataRow dr;
	private int col_BvlProbenNr;
	private int col_BvlTeilProbenNr;
	private int col_BvlVorbefund;
	private int col_BvlAdvCode;
	private int col_BvlSamplingDate;
	private int col_BvlSamplingOrt;
	private int col_BvlBetriebsart;
	
	public MyBvlDs(int col_BvlProbenNr, int col_BvlTeilProbenNr, int col_BvlVorbefund, int col_BvlAdvCode, int col_BvlSamplingDate, int col_BvlSamplingOrt, int col_BvlBetriebsart) {
		this.col_BvlProbenNr = col_BvlProbenNr;		
		this.col_BvlTeilProbenNr = col_BvlTeilProbenNr;		
		this.col_BvlVorbefund = col_BvlVorbefund;		
		this.col_BvlAdvCode = col_BvlAdvCode;		
		this.col_BvlSamplingDate = col_BvlSamplingDate;		
		this.col_BvlSamplingOrt = col_BvlSamplingOrt;		
		this.col_BvlBetriebsart = col_BvlBetriebsart;		
	}
	
	private void fillData() {
		if (col_BvlProbenNr >= 0) {
			DataCell dc = dr.getCell(col_BvlProbenNr);			
			if (!dc.isMissing()) PROBEN_NR = ((StringCell) dc).getStringValue();
		}
		if (col_BvlTeilProbenNr >= 0) {
			DataCell dc = dr.getCell(col_BvlTeilProbenNr);			
			if (!dc.isMissing()) TEILPROBEN_NR = ((IntCell) dc).getIntValue();
		}
		if (col_BvlVorbefund >= 0) {
			DataCell dc = dr.getCell(col_BvlVorbefund);
			if (!dc.isMissing()) VORBEFUND = ((StringCell) dc).getStringValue();
		}
		if (col_BvlAdvCode >= 0) {
			DataCell dc = dr.getCell(col_BvlAdvCode);
			if (!dc.isMissing()) ZERL_MATRIX = ((StringCell) dc).getStringValue();
		}
		if (col_BvlSamplingDate >= 0) {
			DataCell dc = dr.getCell(col_BvlSamplingDate);
			if (!dc.isMissing()) {
				PROBENAHME_DAT = ((StringCell) dc).getStringValue();
				PROBENAHME_MILLIS = getProbenahmeDate();
			}
		}
		if (col_BvlSamplingOrt >= 0) {
			DataCell dc = dr.getCell(col_BvlSamplingOrt);
			if (dc != null && !dc.isMissing()) ProbenahmeOrt = ((StringCell) dc).getStringValue();
		}
		if (col_BvlBetriebsart >= 0) {
			DataCell dc = dr.getCell(col_BvlBetriebsart);
			if (dc != null && !dc.isMissing()) Betriebsart = ((StringCell) dc).getStringValue();
		}
	}
	public String getKey() {
		return PROBEN_NR + ";:_" + TEILPROBEN_NR + ";:_" + VORBEFUND + ";:_" + ZERL_MATRIX + ";:_" + PROBENAHME_DAT + ";:_" + ProbenahmeOrt + ";:_" + Betriebsart;
	}
	
	public DataRow getDr() {
		return dr;
	}
	public void setDr(DataRow dr) {
		this.dr = dr;
		fillData();
	}
	public String getPROBEN_NR() {
		return PROBEN_NR;
	}
	public Integer getTEILPROBEN_NR() {
		return TEILPROBEN_NR;
	}
	public String getVORBEFUND() {
		return VORBEFUND;
	}
	public String getZERL_MATRIX() {
		return ZERL_MATRIX;
	}
	public String getPROBENAHME_DAT() {
		return PROBENAHME_DAT;
	}
	public Long getProbenahmeDate() {
		if (PROBENAHME_MILLIS != null) return PROBENAHME_MILLIS;
		Long result = getDate(bvlFormatter, PROBENAHME_DAT, false);
		if (result == null) result = getDate(bvlFormatter2, PROBENAHME_DAT, true);
		return result;
	}
	private Long getDate(SimpleDateFormat formatter, String datum, boolean throwError) {
		try {
			return formatter.parse(datum).getTime();
		} catch (ParseException e) {
			if (throwError) e.printStackTrace();
			return null;
		}
	}
	public String getProbenahmeOrt() {
		return ProbenahmeOrt;
	}

	public String getBetriebsart() {
		return Betriebsart;
	}
}
