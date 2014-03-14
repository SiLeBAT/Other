package de.bund.bfr.gwt.krise.shared;

import java.io.Serializable;
import java.util.LinkedHashMap;

import com.smartgwt.client.types.ListGridFieldType;

public class MyField implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7101827632047652045L;
	
	public static final int DATE = 10;
	public static final int FLOAT = 11;
	public static final int TEXT = 12;
	public static final int INTEGER = 13;
	public static final int BOOLEAN = 14;
	public static final int ITEM = 15;

	private boolean isPrimary = false;
	private boolean isReadOnly = false;
	private String title;
	private String name;
	private int type;
	private String foreignKey;
	private int maxLength;
	private String[] enumeration = null;
	private LinkedHashMap<String, String> valueMap = null;

	public MyField() {
		this("test", TEXT);
	}
	public MyField(String name, int type) {
		setName(name);
		if (name.equals("ID")) {
			isPrimary = true;
			isReadOnly = true;
		}
		else if (name.equals("Einheit") || name.equals("UnitEinheit")) {
			enumeration = new String[3];
			enumeration[0] = "kg";
			enumeration[1] = "g";
			enumeration[2] = "t";
		}
		setTitle(translate(name));
		setType(type);
		setMaxLength(-1);
	}
	
	private String translate(String str) {
		if (str.equals("Strasse")) return "Street";
		else if (str.equals("Hausnummer")) return "House Number";
		else if (str.equals("PLZ")) return "Zip";
		else if (str.equals("Ort")) return "City";
		else if (str.equals("Bundesland")) return "Region";
		else if (str.equals("Land")) return "Country";
		else if (str.equals("Ansprechpartner")) return "Contact Person";
		else if (str.equals("Betriebsart")) return "Type of Business";
		else if (str.equals("VATnumber")) return "VAT";
		else if (str.equals("FallErfuellt")) return "Is Case";
		else if (str.equals("AnzahlFaelle")) return "Number of Cases";
		else if (str.equals("Kommentar")) return "Comment";
		
		else if (str.equals("Artikelnummer")) return "Article Number";
		else if (str.equals("Bezeichnung")) return "Denomination";
		else if (str.equals("Prozessierung")) return "Processing";

		else if (str.equals("Zutaten")) return "Ingredients";
		else if (str.equals("ChargenNr")) return "Lot No";
		else if (str.equals("MHD")) return "Expiration Date";
		else if (str.equals("Herstellungsdatum")) return "Production Date";
		else if (str.equals("Menge")) return "Amount";
		else if (str.equals("Einheit")) return "Unit";
		
		else if (str.equals("Lieferdatum")) return "Delivery Date";
		else if (str.equals("Unitmenge")) return "Amount";
		else if (str.equals("UnitEinheit")) return "Unit";
		else if (str.equals("Empfänger")) return "Recipient";
		else return str;
	}
	
	public boolean isPrimary() {
		return isPrimary;
	}
	public boolean isReadOnly() {
		return isReadOnly;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getForeignKey() {
		return foreignKey;
	}
	public void setForeignKey(String foreignKey) {
		this.foreignKey = foreignKey;
	}
	public int getMaxLength() {
		return maxLength;
	}
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}
	public String[] getEnumeration() {
		return enumeration;
	}
	public void setEnumeration(String[] enumeration) {
		this.enumeration = enumeration;
	}
	public LinkedHashMap<String, String> getValueMap() {
		return valueMap;
	}
	public void setValueMap(LinkedHashMap<String, String> valueMap) {
		this.valueMap = valueMap;
	}
	
	public static ListGridFieldType getType(int type) {
		if (type == DATE) return ListGridFieldType.DATE;
		else if (type == FLOAT) return ListGridFieldType.FLOAT;
		else if (type == TEXT) return ListGridFieldType.TEXT;
		else if (type == INTEGER) return ListGridFieldType.INTEGER;
		else if (type == BOOLEAN) return ListGridFieldType.BOOLEAN;
		else if (type == ITEM) return ListGridFieldType.TEXT;
		else return ListGridFieldType.TEXT;
	}
}
