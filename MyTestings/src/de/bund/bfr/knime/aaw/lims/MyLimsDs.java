package de.bund.bfr.knime.aaw.lims;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.date.DateAndTimeCell;

public class MyLimsDs {

	private String KundenProbenr = null;
	private String Vorbefund = null;
	private String Ergebnis = null;
	private String Status = null;
	private String MatrixACode = null;
	private Long Probenahme = null;
	private DataRow dr;
	private int col_limsKundenNr;
	private int col_LimsVorbefund;
	private int col_limsResult;
	private int col_limsStatus;
	private int col_LimsAdvCode;
	private int col_LimsSamplingDate;
	
	public MyLimsDs(int col_limsKundenNr, int col_LimsVorbefund, int col_limsResult, int col_limsStatus, int col_LimsAdvCode, int col_LimsSamplingDate) {
		this.col_limsKundenNr = col_limsKundenNr;		
		this.col_LimsVorbefund = col_LimsVorbefund;		
		this.col_limsResult = col_limsResult;		
		this.col_limsStatus = col_limsStatus;		
		this.col_LimsAdvCode = col_LimsAdvCode;		
		this.col_LimsSamplingDate = col_LimsSamplingDate;		
	}
	
	private void fillData() {
		if (col_limsKundenNr >= 0) {
			DataCell dc = dr.getCell(col_limsKundenNr);
			if (dc != null && !dc.isMissing()) KundenProbenr = ((StringCell) dc).getStringValue();
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
	}
	public String getKey() {
		return KundenProbenr + ";:_" + Vorbefund + ";:_" + Ergebnis + ";:_" + MatrixACode + ";:_" + Probenahme;
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
	
	private MyBLTResults mblt = null;
	
	public MyBLTResults getMblt(boolean refresh) {
		if (refresh || mblt == null) mblt = new MyBLTResults();
		return mblt;
	}
	
}
