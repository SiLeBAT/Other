package de.bund.bfr.busstopp.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;

public class XmlValidator {

	public static void main(String[] args) {
		System.err.println(new XmlValidator().validate("/Users/arminweiser/Desktop/xml_test/Anleitung_pmmlab.txt"));
		System.err.println(new XmlValidator().validate("/Users/arminweiser/Desktop/xml_test/bbk/bbk1.xml"));
	}

	public boolean validate(String filename) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      	  	URL xsd = getClass().getResource("/de/bund/bfr/busstopp/util/xsd/main/de.nrw.verbraucherschutz.idv.dienste.2016.2.warenrueckverfolgung.transport.schema.xsd");
            Schema schema = factory.newSchema(xsd);
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(filename)));
        } catch (IOException | SAXException e) {
            //System.out.println("Exception: "+e.getMessage());
            return false;
        }
        return true;
	}
}
