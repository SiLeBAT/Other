//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2016.01.28 um 03:29:13 PM CET 
//


package de.bund.bfr.epcis;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse f�r VocabularyListType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="VocabularyListType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Vocabulary" type="{urn:epcglobal:epcis-masterdata:xsd:1}VocabularyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VocabularyListType", namespace = "urn:epcglobal:epcis-masterdata:xsd:1", propOrder = {
    "vocabulary"
})
public class VocabularyListType {

    @XmlElement(name = "Vocabulary")
    protected List<VocabularyType> vocabulary;

    /**
     * Gets the value of the vocabulary property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vocabulary property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVocabulary().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VocabularyType }
     * 
     * 
     */
    public List<VocabularyType> getVocabulary() {
        if (vocabulary == null) {
            vocabulary = new ArrayList<VocabularyType>();
        }
        return this.vocabulary;
    }

}
