package de.bund.bfr.knime.aaw.lims;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.date.DateAndTimeCell;

public class MyLimsDs {

	private String KundenProbenr = null;
	private String AVV = null;
	private String Vorbefund = null;
	private String Ergebnis = null;
	private String Status = null;
	private String ProjectName = null;
	private String MatrixACode = null;
	private Long Probenahme = null;
	private String ProbenahmeOrt = null;
	private String Betriebsart = null;
	private DataRow dr;
	private int col_limsKundenNr;
	private int col_limsAVV;
	private int col_LimsVorbefund;
	private int col_limsResult;
	private int col_limsStatus;
	private int col_LimsAdvCode;
	private int col_LimsSamplingDate;
	private int col_LimsProjectName;
	private int col_LimsSamplingOrt;
	private int col_LimsBetriebsart;
	
	public MyLimsDs(int col_limsKundenNr, int col_limsAVV, int col_LimsVorbefund, int col_limsResult, int col_limsStatus, int col_LimsAdvCode, int col_LimsSamplingDate, int col_LimsProjectName, int col_LimsSamplingOrt, int col_LimsBetriebsart) {
		this.col_limsKundenNr = col_limsKundenNr;		
		this.col_limsAVV = col_limsAVV;		
		this.col_LimsVorbefund = col_LimsVorbefund;		
		this.col_limsResult = col_limsResult;		
		this.col_limsStatus = col_limsStatus;		
		this.col_LimsAdvCode = col_LimsAdvCode;		
		this.col_LimsSamplingDate = col_LimsSamplingDate;		
		this.col_LimsProjectName = col_LimsProjectName;		
		this.col_LimsSamplingOrt = col_LimsSamplingOrt;		
		this.col_LimsBetriebsart = col_LimsBetriebsart;		
	}
	
	private void fillData() {
		if (col_limsKundenNr >= 0) {
			DataCell dc = dr.getCell(col_limsKundenNr);
			if (dc != null && !dc.isMissing()) KundenProbenr = ((StringCell) dc).getStringValue();
		}
		if (col_limsAVV >= 0) {
			DataCell dc = dr.getCell(col_limsAVV);
			if (dc != null && !dc.isMissing()) AVV = ((StringCell) dc).getStringValue();
		}
		if (col_LimsVorbefund >= 0) {
			DataCell dc = dr.getCell(col_LimsVorbefund);
			if (dc != null && !dc.isMissing()) Vorbefund = ((StringCell) dc).getStringValue();
		}
		if (col_limsResult >= 0) {
			DataCell dc = dr.getCell(col_limsResult);
			if (dc != null && !dc.isMissing()) Ergebnis = ((StringCell) dc).getStringValue();
		}
		if (col_LimsAdvCode >= 0) {
			DataCell dc = dr.getCell(col_LimsAdvCode);
			if (dc != null && !dc.isMissing()) MatrixACode = ((StringCell) dc).getStringValue();
		}
		if (col_LimsSamplingDate >= 0) {
			DataCell dc = dr.getCell(col_LimsSamplingDate);
			if (dc != null && !dc.isMissing()) Probenahme = ((DateAndTimeCell) dc).getUTCTimeInMillis();
		}
		if (col_limsStatus >= 0) {
			DataCell dc = dr.getCell(col_limsStatus);
			if (dc != null && !dc.isMissing()) Status = ((StringCell) dc).getStringValue();
		}
		if (col_LimsProjectName >= 0) {
			DataCell dc = dr.getCell(col_LimsProjectName);
			if (dc != null && !dc.isMissing()) ProjectName = ((StringCell) dc).getStringValue();
		}
		if (col_LimsSamplingOrt >= 0) {
			DataCell dc = dr.getCell(col_LimsSamplingOrt);
			if (dc != null && !dc.isMissing()) ProbenahmeOrt = ((StringCell) dc).getStringValue();
		}
		if (col_LimsBetriebsart >= 0) {
			DataCell dc = dr.getCell(col_LimsBetriebsart);
			if (dc != null && !dc.isMissing()) Betriebsart = ((StringCell) dc).getStringValue();
		}
	}
	public String getKey() {
		return KundenProbenr + ";:_" + AVV + ";:_" + Vorbefund + ";:_" + Ergebnis + ";:_" + MatrixACode + ";:_" + Probenahme + ";:_" + ProbenahmeOrt + ";:_" + Betriebsart + ";:_" + Status;
	}
	
	public DataRow getDr() {
		return dr;
	}
	public void setDr(DataRow dr) {
		this.dr = dr;
		fillData();
	}
	public String getKundenProbenr() {
		return KundenProbenr;
	}

	public String getAVV() {
		return AVV;
	}

	public String getVorbefund() {
		return Vorbefund;
	}

	public String getErgebnis() {
		return Ergebnis;
	}

	public String getStatus() {
		return Status;
	}

	public String getMatrixACode() {
		return MatrixACode;
	}

	public Long getProbenahme() {
		return Probenahme;
	}
	
	public String getProjectName() {
		return ProjectName;
	}

	public void setProjectName(String projectName) {
		ProjectName = projectName;
	}
	public String getProbenahmeOrt() {
		return ProbenahmeOrt;
	}

	public String getBetriebsart() {
		return Betriebsart;
	}

	
	private MyBLTResults mblt = null;
	
	public MyBLTResults getMblt(boolean refresh) {
		if (refresh || mblt == null) mblt = new MyBLTResults();
		return mblt;
	}
	public MyBLTResults setMblt(MyLimsDs mld, MyBvlDs mbd) {
		mblt = new MyBLTResults();
		mblt.setV_status(mld.getStatus() == null ? null : mld.getStatus().toLowerCase().indexOf("v") >= 0);
		Boolean b_date = checkDates(mbd.getProbenahmeDate(), mld.getProbenahme());
		mblt.setV_date(b_date);
		Boolean b_adv = checkAdv(mbd.getZERL_MATRIX(), mld.getMatrixACode());		
		mblt.setV_adv(b_adv);
		Double d_befund = StringSimilarity.diceCoefficientOptimized(mbd.getVORBEFUND(), mld.getVorbefund());
		if (mld.getProjectName() != null && mbd.getVORBEFUND() != null && mld.getProjectName().startsWith("Moni-ESBL-") && mbd.getVORBEFUND().indexOf("ESBL") >= 0) {
			d_befund = 1.0;
		}
		mblt.setVorbefundScore(d_befund);
		double betriebsartMatch = StringSimilarity.diceCoefficientOptimized(mbd.getBetriebsart(), mld.getBetriebsart());
		double probenahmeortMatch = StringSimilarity.diceCoefficientOptimized(mbd.getProbenahmeOrt(), mld.getProbenahmeOrt());
		mblt.setBetriebsartMatch(betriebsartMatch);
		mblt.setProbenahmeortMatch(probenahmeortMatch);
		return mblt;
	}
	
	private final long ONE_DAY = 24*60*60*1000;
    private Boolean checkDates(Long date1, Long date2) {
    	if (date1 == null || date2 == null) return null;
		boolean criterium = date1 >= date2 - ONE_DAY && date1 <= date2 + ONE_DAY;
		return criterium;
    }
    private Boolean checkAdv(String adv1, String adv2) {
    	if (adv1 == null || adv2 == null) return null;
    	return adv1.equals(adv2);
    }
}
