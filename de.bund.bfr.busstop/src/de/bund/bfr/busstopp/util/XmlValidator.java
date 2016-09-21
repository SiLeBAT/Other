package de.bund.bfr.busstopp.util;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import de.nrw.verbraucherschutz.idv.daten.Kontrollpunktmeldung;

public class XmlValidator {

	public static void main(String[] args) throws JAXBException {
		System.err.println(new XmlValidator().validate("/Users/arminweiser/Desktop/xml_test/Anleitung_pmmlab.txt"));
	}

	@SuppressWarnings("unchecked")
	public boolean validate(String filename) {
		boolean result = false;
		Unmarshaller reader;
		try {
			reader = JAXBContext.newInstance(Kontrollpunktmeldung.class.getPackage().getName())
					.createUnmarshaller();

			reader.setSchema(SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
					.newSchema(Kontrollpunktmeldung.class.getResource(
							"/de/nrw/verbraucherschutz/idv/daten/main/de.nrw.verbraucherschutz.idv.dienste.2016.2.warenrueckverfolgung.transport.schema.xsd")));
	
			File file = new File(filename);
			if (file.exists() && file.isFile()) {
				if (file.getName().endsWith(".xml")) {
					System.out.println("----- " + file.getName() + " -----");

					@SuppressWarnings("unused")
					Kontrollpunktmeldung meldung = ((JAXBElement<Kontrollpunktmeldung>) reader.unmarshal(file)).getValue();
					
					result = true;
				}
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return result;		
	}
}
