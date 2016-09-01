package de.bund.bfr.knime.aaw.hartung;

public class RowProps {

	private String SourceA;
	private String SourceB;
	private String SourceC;
	private String Methode;
	private String Grund;
	private String Ebene;
	private Integer Amount;
	private String PAB = null;
	private String MAD = null;
	
	public RowProps() {
		
	}
	public RowProps(String SourceA, String SourceB, String SourceC, String Methode, String Grund, String Ebene, Integer Amount) {
		this.SourceA = SourceA;
		this.SourceB = SourceB;
		this.SourceC = SourceC;
		this.Methode = Methode;
		this.Grund = Grund;
		this.Ebene = Ebene;
		this.Amount = Amount;
	}

	public String getMAD() {
		return MAD;
	}
	public void setMAD(String mAD) {
		MAD = mAD;
	}
	public String getPAB() {
		return PAB;
	}
	public void setPAB(String pAB) {
		PAB = pAB;
	}

	public String getSourceA() {
		return SourceA;
	}

	public String getSourceB() {
		return SourceB;
	}

	public String getSourceC() {
		return SourceC;
	}

	public String getMethode() {
		return Methode;
	}

	public String getGrund() {
		return Grund;
	}

	public String getEbene() {
		return Ebene;
	}

	public Integer getAmount() {
		return Amount;
	}
	
	
	public void setSourceA(String sourceA) {
		SourceA = sourceA;
	}

	public void setSourceB(String sourceB) {
		SourceB = sourceB;
	}

	public void setSourceC(String sourceC) {
		SourceC = sourceC;
	}

	public void setMethode(String methode) {
		Methode = methode;
	}

	public void setGrund(String grund) {
		Grund = grund;
	}

	public void setEbene(String ebene) {
		Ebene = ebene;
	}

	public void setAmount(Integer amount) {
		Amount = amount;
	}
	public String getCOD() {
		return getGrund() + "," + getMethode() + "," + getEbene();
	}

	public RowProps clone() {
		return new RowProps(SourceA, SourceB, SourceC, Methode, Grund, Ebene, Amount);
	}
	public boolean equals(RowProps nextRP) {
		if (nextRP == null) return false;
		boolean eq = (nextRP.getSourceA() == null && SourceA == null || nextRP.getSourceA() != null && nextRP.getSourceA().equals(SourceA)) &&
					(nextRP.getSourceB() == null && SourceB == null || nextRP.getSourceB() != null && nextRP.getSourceB().equals(SourceB)) && 
					(nextRP.getSourceC() == null && SourceC == null || nextRP.getSourceC() != null && nextRP.getSourceC().equals(SourceC)) && 
					(nextRP.getMethode() == null && Methode == null || nextRP.getMethode() != null && nextRP.getMethode().equals(Methode)) && 
					(nextRP.getGrund() == null && Grund == null || nextRP.getGrund() != null && nextRP.getGrund().equals(Grund)) && 
					(nextRP.getEbene() == null && Ebene == null || nextRP.getEbene() != null && nextRP.getEbene().equals(Ebene)) && 
					(nextRP.getAmount() == null && Amount == null || nextRP.getAmount() != null && Amount != null && nextRP.getAmount().intValue() == Amount.intValue());
		if (eq)
			return true;
		return false;
	}
}
