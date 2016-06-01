package de.bund.bfr.knime.Xsd2Xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.xml.XMLBlobCell;
import org.knime.core.data.xml.XMLCell;
import org.knime.core.data.xml.XMLCellFactory;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.FileUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This is the model implementation of Xsd2Xml.
 * 
 *
 * @author 
 */
public class Xsd2XmlNodeModel extends NodeModel {
    
	static final String XSD_FILE = "xsdfile";
	static final String XML_FILE = "xmlfile";
	static final String SAVE_WORKFLOW = "save workflow?";
	
    private final SettingsModelString xsdFile = new SettingsModelString(XSD_FILE, "");
    private final SettingsModelString xmlFile = new SettingsModelString(XML_FILE, "");
    private final SettingsModelBoolean saveWF = new SettingsModelBoolean(SAVE_WORKFLOW, true); 

    /**
     * Constructor for the node model.
     */
    protected Xsd2XmlNodeModel() {
        super(1, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {

    	// root element
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    	Document doc = docBuilder.newDocument();
    	doc.setXmlStandalone(true);

    	String xsd = xsdFile.getStringValue();//"C:\\Users\\Armin\\Desktop\\LIMS_43\\Tauschordner_AK_AW\\XML-Projekt_Reporting2013\\XMLschemas\\AMR Isolate Based Data Model.xsd";
    	String xml = xmlFile.getStringValue();//"C:\\Users\\Armin\\Desktop\\LIMS_43\\Tauschordner_AK_AW\\XML-Projekt_Reporting2013\\XMLschemas\\AMR_Iso.xml";

    	File xsdDatei = new File(xsd); // default, wegen knime.workflow u.ä.
        URL url = FileUtil.toURL(xsd);
        Path localPath = FileUtil.resolveToPath(url);
        if (localPath != null) {
        	xsdDatei = localPath.toFile();
            xsd = xsdDatei.getAbsolutePath();
        }
    	File xmlDatei = new File(xml); // default, wegen knime.workflow u.ä.
        url = FileUtil.toURL(xml);
        localPath = FileUtil.resolveToPath(url);
        if (localPath != null) {
        	xmlDatei = localPath.toFile();
            xml = xmlDatei.getAbsolutePath();
        }
        
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilderXsd = docBuilderFactory.newDocumentBuilder();
        Document document = docBuilderXsd.parse(xsdDatei);
        BufferedDataTable in = inData[0];
                
        NodeList list = document.getElementsByTagName("xs:element"); 
        NodeList other = null;
        for (int i=0;i<list.getLength();i++) {
        	Element e = (Element)list.item(i);
        	if (e.getAttribute("name").equalsIgnoreCase("result")) {
    	    	other = e.getElementsByTagName("xs:element");
    	    	break;
        	}
        }
        HashMap<String, Integer> hm = new HashMap<>();
        String[] cn = in.getDataTableSpec().getColumnNames();
        for (int i=0;i<cn.length;i++) {
        	hm.put(cn[i], i);
        }
		Element rootElement = doc.createElement("dataset");
    	doc.appendChild(rootElement);        		
		for (DataRow row : in) {
    	    Element result = doc.createElement("result");
    	    rootElement.appendChild(result);
            for (int i = 0 ; i < other.getLength(); i++) {
            	Element el = (Element)other.item(i);
            	if( el.hasAttributes()) {
            		String ela = el.getAttribute("name");
            		if (hm.containsKey(ela) && !row.getCell(hm.get(ela)).isMissing()) {
                	    Element el2 = doc.createElement(ela);
                		el2.appendChild(doc.createTextNode(row.getCell(hm.get(ela)).toString()));
                	    result.appendChild(el2);
            		}
            	}
            }
		}    	 
        	
    	// write xml to file
    	TransformerFactory transformerFactory = TransformerFactory.newInstance();
    	 
    	Transformer transformer = transformerFactory.newTransformer();
    	transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
    	transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    	transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount","2");
    	DOMSource source = new DOMSource(doc);
    	 
    	StreamResult result = new StreamResult(xml); // xmlFile.getStringValue()
    	 
    	// Show output on console during development
    	//StreamResult result = new StreamResult(System.out);
    	 
    	transformer.transform(source, result);
    	System.out.println("Xml File saved!");
    	
    	if (saveWF.getBooleanValue()) {
        	saveWF(exec, xml);
        	System.out.println("Workflow saved!");    		
    	}
    	 

    	// Validation method 1
    	    
    	final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    	final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    	final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	factory.setNamespaceAware(true);
    	factory.setValidating(true);
    	         
    	factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);

    	// Set the schema file
    	factory.setAttribute(JAXP_SCHEMA_SOURCE, xsdDatei);

    	try {
    		DocumentBuilder parser = factory.newDocumentBuilder();

    	    // Parse the file. If errors found, they will be printed.
    		parser.setErrorHandler(new MyErrorHandler(this));
    		parser.parse(xml); // xmlFile.getStringValue()
    	    }
    	catch (SAXException e) {
    	    e.printStackTrace();
    	}

    	
    	// Validation method 2

    	Source schemaFile = new StreamSource(new File(xsd)); // xsdFile.getStringValue()
        Source xmlDateiSource = new StreamSource(xmlDatei);
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(schemaFile);
        Validator validator = schema.newValidator();
        
        try{
            validator.validate(xmlDateiSource);
            System.out.println(xmlDateiSource.getSystemId() + " is valid");
        }
        catch (SAXException e) {
            System.out.println(xmlDateiSource.getSystemId() + " is NOT valid");
            System.out.println("Reason: " + e.getLocalizedMessage());
        }
    	         
        
		BufferedDataContainer buf = exec.createDataContainer(getSpec());
		DataCell[] cells = new DataCell[1];
		cells[0] = XMLCellFactory.create(doc);
		String sxml = "";
		if (cells[0] instanceof XMLBlobCell) sxml = ( (XMLBlobCell) cells[0]).getStringValue();
		else sxml = ( (XMLCell) cells[0]).getStringValue();
		cells[0] = new StringCell(sxml);
		
		DataRow outputRow = new DefaultRow(RowKey.createRowKey(buf.size()), cells);
		buf.addRowToTable(outputRow);
    	buf.close();
        return new BufferedDataTable[]{buf.getTable()};
    }
	private DataTableSpec getSpec() {
		DataColumnSpec[] outSpec = new DataColumnSpec[1];		
		outSpec[0] = new DataColumnSpecCreator("xml", StringCell.TYPE).createSpec();

		return new DataTableSpec(outSpec);
	}
	private Integer saveWF(final ExecutionContext exec, String xmlFile) throws Exception {
		Integer result = null;
		for (NodeContainer nc : WorkflowManager.ROOT.getNodeContainers()) {
			if (nc instanceof WorkflowManager) {
				WorkflowManager wfm = (WorkflowManager) nc;
				for (Xsd2XmlNodeModel m : wfm.findNodes(Xsd2XmlNodeModel.class, true).values()) {
					if (m == this) {
						File wfdir = wfm.getWorkingDir().getFile();
						wfm.save(wfdir, exec, true);
						String zipfile = xmlFile + ".wf_" + System.currentTimeMillis() + ".zip";
						zipDirectory(wfdir, zipfile);
					}
				}
			}
		}
		return result;
	}
	private void zipDirectory(File dir, String zipDirName) {
		try {
			List<String> filesListInDir = populateFilesList(null, dir);
			//now zip files one by one
			//create ZipOutputStream to write to the zip file
			FileOutputStream fos = new FileOutputStream(zipDirName);
			ZipOutputStream zos = new ZipOutputStream(fos);
			for (String filePath : filesListInDir) {
				//for ZipEntry we need to keep only relative file path, so we used substring on absolute path
				ZipEntry ze = new ZipEntry(filePath.substring(dir.getParentFile().getAbsolutePath().length() + 1, filePath.length()));
				zos.putNextEntry(ze);
				//read the file and write to ZipOutputStream
				FileInputStream fis = new FileInputStream(filePath);
				byte[] buffer = new byte[1024];
				int len;
				while ((len = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}
				zos.closeEntry();
				fis.close();
			}
			zos.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private List<String> populateFilesList(List<String> filesListInDir, File dir) throws IOException {
		if (filesListInDir == null) filesListInDir = new ArrayList<String>();
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				if (!file.getName().equals(".knimeLock")) filesListInDir.add(file.getAbsolutePath());
			} else {
				filesListInDir = populateFilesList(filesListInDir, file);
			}
		}
		return filesListInDir;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // TODO: generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {
		return new DataTableSpec[] {getSpec()};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
    	xsdFile.saveSettingsTo(settings);
    	xmlFile.saveSettingsTo(settings);
    	saveWF.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	xsdFile.loadSettingsFrom(settings);
    	xmlFile.loadSettingsFrom(settings);
    	if (settings.containsKey(SAVE_WORKFLOW)) saveWF.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	xsdFile.validateSettings(settings);
    	xmlFile.validateSettings(settings);
    	if (settings.containsKey(SAVE_WORKFLOW)) saveWF.validateSettings(settings);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // TODO: generated method stub
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // TODO: generated method stub
    }

    class MyErrorHandler implements ErrorHandler {
    	private Xsd2XmlNodeModel xsdNM;
    	MyErrorHandler(Xsd2XmlNodeModel xsdNM) {
    		this.xsdNM = xsdNM;
    	}
        public void warning(SAXParseException exception) throws SAXException {
            // Bring things to a crashing halt
        	String message = "**Parsing Warning**" +
                    "  Line:    " + 
                    exception.getLineNumber() + "" +
                 "  URI:     " + 
                    exception.getSystemId() + "" +
                 "  Message: " + 
                    exception.getMessage();
            System.out.println(message);        
    		xsdNM.setWarningMessage(message);
            throw new SAXException("Warning encountered");
        }
        public void error(SAXParseException exception) throws SAXException {
            // Bring things to a crashing halt
        	String message = "**Parsing Error**" +
                    "  Line:    " + 
                    exception.getLineNumber() + "" +
                 "  URI:     " + 
                    exception.getSystemId() + "" +
                 "  Message: " + 
                    exception.getMessage();
            System.out.println(message);        
    		xsdNM.setWarningMessage(message);
            throw new SAXException("Error encountered");
        }
        public void fatalError(SAXParseException exception) throws SAXException {
            // Bring things to a crashing halt
        	String message = "**Parsing Fatal Error**" +
                    "  Line:    " + 
                    exception.getLineNumber() + "" +
                 "  URI:     " + 
                    exception.getSystemId() + "" +
                 "  Message: " + 
                    exception.getMessage();
            System.out.println(message);        
    		xsdNM.setWarningMessage(message);
            throw new SAXException("Fatal Error encountered");
        }
    }
}

