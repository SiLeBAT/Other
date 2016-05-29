package de.bund.bfr.knime.Xsd2Xml.xsd2xml;

import java.io.File;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamResult;
 
import org.apache.xerces.xs.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import jlibs.xml.sax.XMLDocument;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
 
import jlibs.xml.xsd.XSInstance;
import jlibs.xml.xsd.XSParser;
 
public class XsInstancer {
    public static void main(String[] pArgs) throws SAXException {
        try {
            String filename = "C:/Users/Armin/Downloads/GDE2_message_prevalence.xsd";
            // instance.
 
            final Document doc = loadXsdDocument(filename);
 
                        //Find the docs root element and use it to find the targetNamespace
            final Element rootElem = doc.getDocumentElement();
            String targetNamespace = null;
            if (rootElem != null && rootElem.getNodeName().equals("xs:schema")) 
                       {
                targetNamespace = rootElem.getAttribute("targetNamespace");
            }
             
 
                        //Parse the file into an XSModel object
            XSModel xsModel = new XSParser().parse(filename);
 
                        //Define defaults for the XML generation
            XSInstance instance = new XSInstance();
            instance.minimumElementsGenerated = 0;
            instance.maximumElementsGenerated = 0;
            instance.minimumListItemsGenerated = 0;
            instance.maximumListItemsGenerated = 0;
            instance.generateDefaultAttributes = false;
            instance.generateOptionalAttributes = false;
            instance.maximumRecursionDepth = 0;
            instance.generateAllChoices = false;
            instance.showContentModel = false;
            instance.generateOptionalElements = false;
 
                        //Build the sample xml doc
                        //Replace first param to XMLDoc with a file input stream to write to file
            QName rootElement = new QName(targetNamespace, "message");
            XMLDocument sampleXml = new XMLDocument(new StreamResult(System.out), false, 4, "UTF-8");
            /*
            sampleXml.startDocument();{
            	sampleXml.startElement("dataset");{
                    //for(Employee emp: company.employees){
                    	sampleXml.startElement("result");{
                            sampleXml.addElement("sampWeight", "");
                            sampleXml.addElement("sampArea", "sa2");
                            sampleXml.addElement("resComm", "sa2");
                            sampleXml.addElement("sampUnit", "sa2");
                            sampleXml.addElement("affectHerds", "sa2");
                            sampleXml.addElement("unitsPositive", "sa2");
                            sampleXml.addElement("sampContext", "sa2");
                            sampleXml.addElement("totUnitsPositive", "sa2");
                            sampleXml.addElement("contrFlocks", "sa2");
                            sampleXml.addElement("target", "sa2");
                            sampleXml.addElement("totUnitsTested", "sa2");
                            sampleXml.addElement("unitsTested", "sa2");
                            sampleXml.addElement("zoonosis", "sa2");
                            sampleXml.addElement("amType", "sa2");
                            sampleXml.addElement("recId", "sa2");
                            sampleXml.addElement("sampDetails", "sa2");
                            sampleXml.addElement("vaccination", "sa2");
                            sampleXml.addElement("sampWeightUnit", "sa2");
                            sampleXml.addElement("sampOrig", "sa2");
                            sampleXml.addElement("repCountry", "sa2");
                            sampleXml.addElement("matrix", "sa2");
                            sampleXml.addElement("sourceInfo", "sa2");
                            sampleXml.addElement("repYear", "sa2");
                            sampleXml.addElement("sampStage", "sa2");
                            sampleXml.addElement("lang", "sa2");
                            sampleXml.addElement("anMethCode", "sa2");
                            sampleXml.addElement("progSampStrategy", "sa2");
                            sampleXml.addElement("quantity", "sa2");
                       }
                        sampleXml.endElement("result");
                    //}
                }
            	sampleXml.endElement("dataset");
            }
            sampleXml.endDocument();
            */
            instance.generate(xsModel, rootElement, sampleXml);
        } catch (TransformerConfigurationException e) 
                {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
 
    public static Document loadXsdDocument(String inputName) {
        final String filename = inputName;
 
        final DocumentBuilderFactory factory = DocumentBuilderFactory
                .newInstance();
        factory.setValidating(false);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setIgnoringComments(true);
        Document doc = null;
 
        try {
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final File inputFile = new File(filename);
            doc = builder.parse(inputFile);
        } catch (final Exception e) {
            e.printStackTrace();
            // throw new ContentLoadException(msg);
        }
 
        return doc;
    }
}