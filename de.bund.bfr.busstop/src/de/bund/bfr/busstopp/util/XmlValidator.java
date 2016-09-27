package de.bund.bfr.busstopp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;

public class XmlValidator {

	public static void main(String[] args) throws SOAPException, IOException {
		System.err.println(new XmlValidator().validate("/Users/arminweiser/Desktop/xml_test/Anleitung_pmmlab.txt"));
		System.err.println(new XmlValidator().validate("/Users/arminweiser/Desktop/xml_test/bbk/bbk1.xml"));
		System.err.println(new XmlValidator().validate("/Users/arminweiser/Desktop/xml_test/out.xml"));
		System.err.println(new XmlValidator().createRequest("/Users/arminweiser/Downloads/null.txt"));
	}

	public boolean validate(String filename) {
		return validate(new StreamSource(new File(filename)));
	}
	private boolean validate(Source ds) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
       	  	URL xsd = getClass().getResource("/de/bund/bfr/busstopp/util/xsd/main/de.nrw.verbraucherschutz.idv.dienste.2016.2.warenrueckverfolgung.transport.schema.xsd");
       	  	//URL xsd = getClass().getResource("/de/bund/bfr/busstopp/util/xsd/main/de.nrw.verbraucherschutz.idv.dienste.2016.2.warenrueckverfolgung.transport.wsdl");
       	    Schema schema = factory.newSchema(xsd);
            Validator validator = schema.newValidator();
            validator.validate(ds);
        } catch (IOException | SAXException e) {
            System.out.println("Exception: "+e.getMessage());
            return false;
        }
        return true;
	}
	/**
	   * Creates a request from an XML template
	   */
	  private boolean createRequest(String filename) throws SOAPException, IOException {
		boolean result = true;
	    InputStream template = new FileInputStream(new File(filename));
	    try {
	      MessageFactory mf = MessageFactory.newInstance(); // SOAPConstants.SOAP_1_1_PROTOCOL
	      SOAPMessage message = mf.createMessage(new MimeHeaders(), template);
	      SOAPPart sp = message.getSOAPPart();
	      SOAPEnvelope se = sp.getEnvelope();
	      SOAPBody body = se.getBody();
          NodeList nl = body.getChildNodes();
          for(int i=0;i<nl.getLength();i++) {
        	  if (result) {
                  Node nln = nl.item(i);
                  if (nln.getNodeName().endsWith("kontrollpunktmeldung")) {
                      DOMSource ds = new DOMSource(nln);
                      //System.out.println(nln.getNodeName());
                      result = validate(ds);            	  
                  }
        	  }
        	  else break;
          }

	      return result;
	    } catch (Exception e) {
		    return false;
	    } finally {
	      template.close();
	    }
	  }	
}
