package de.bund.bfr.knime.krise;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.hsh.bfr.db.DBKernel;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.port.PortObjectSpec;

import com.toedter.calendar.JDateChooser;

/**
 * <code>NodeDialog</code> for the "MyKrisenInterfaces" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author draaw
 */
public class MyKrisenInterfacesNodeDialog extends NodeDialogPane {

	private DbConfigurationUi dbui;
	private JCheckBox doCrossContaminateAll, doClustering, enforceTemporalOrder;
	private JCheckBox filterCaseSensitive, doAnonymize, antiArticle, showCasesFilter, antiCompany, goBackFilterIfMixed, goForwardFilter;
	private JComboBox<String> OrAnd;
	//private JComboBox<String> tracingType;
	private JTextField company, charge, artikel;
	private JDateChooser dateFrom, dateTo;
	//private JSpinner tracingOmitMax;
	//private JCheckBox showAll;

	protected MyKrisenInterfacesNodeDialog() {
		JPanel panel = new JPanel();
    	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));   	
    	
    	dbui = new DbConfigurationUi();

    	JPanel panelTracing = new JPanel();
    	panelTracing.setBorder(new TitledBorder("Tracing"));
    	//tracingBack = new JCheckBox(); tracingBack.setText("Do BackTracing?"); panelTracing.add(tracingBack);
    	boolean isEN = DBKernel.getLanguage().equalsIgnoreCase("en");
    	//tracingType = new JComboBox<String>(); tracingType.addItem("STATION"); tracingType.addItem(isEN ? "ARTICLE" : "ARTIKEL"); 
    	//tracingType.addItem("CHARGE"); tracingType.addItem(isEN ? "DELIVERY" : "LIEFERUNG"); panelTracing.add(tracingType);
    	//tracingOmitMax = new JSpinner(); tracingOmitMax.setModel(new SpinnerNumberModel(3, 0, 10, 1)); panelTracing.add(tracingOmitMax);
    	doCrossContaminateAll = new JCheckBox(); doCrossContaminateAll.setText("cross-contamination?"); panelTracing.add(doCrossContaminateAll);
    	enforceTemporalOrder = new JCheckBox(); enforceTemporalOrder.setText("enforce temporal order?"); enforceTemporalOrder.setSelected(true); panelTracing.add(enforceTemporalOrder);
    	doClustering = new JCheckBox(); doClustering.setText("clustering?"); panelTracing.add(doClustering);
    	panel.add(panelTracing);
    	
    	JPanel panelPeriod = new JPanel();
    	panelPeriod.setBorder(new TitledBorder("Period"));
    	panelPeriod.add(new JLabel("dateFrom:")); dateFrom = new JDateChooser(); panelPeriod.add(dateFrom);
    	panelPeriod.add(new JLabel("dateTo:")); dateTo = new JDateChooser(); panelPeriod.add(dateTo);
    	//panel.add(panelPeriod);
    	
    	doAnonymize = new JCheckBox(); doAnonymize.setText("Anonymize?"); panel.add(doAnonymize);
    	
    	JPanel panelFilter = new JPanel();
    	panelFilter.setLayout(new BoxLayout(panelFilter, BoxLayout.Y_AXIS));   	
    	panelFilter.setBorder(new TitledBorder("Filter options"));
    	JPanel otherFilterPanel = new JPanel();
    	filterCaseSensitive = new JCheckBox(); filterCaseSensitive.setText("Case sensitive?"); otherFilterPanel.add(filterCaseSensitive);
    	OrAnd = new JComboBox<String>(); OrAnd.addItem("OR"); OrAnd.addItem("AND"); OrAnd.setSelectedItem("AND"); otherFilterPanel.add(OrAnd);
    	panelFilter.add(otherFilterPanel);
    	JPanel companyFilterPanel = new JPanel();
    	companyFilterPanel.setBorder(new TitledBorder("Company"));
    	company = new JTextField(); company.setColumns(15); companyFilterPanel.add(new JLabel("Company:")); companyFilterPanel.add(company);
    	showCasesFilter = new JCheckBox(); showCasesFilter.setText("Case Priority > 0?"); companyFilterPanel.add(showCasesFilter);
    	antiCompany = new JCheckBox(); antiCompany.setText("Anti Company?"); companyFilterPanel.add(antiCompany); antiCompany.setVisible(false);
    	panelFilter.add(companyFilterPanel);
    	JPanel chargeFilterPanel = new JPanel();
    	chargeFilterPanel.setBorder(new TitledBorder("Lot"));
    	charge = new JTextField(); charge.setColumns(15); chargeFilterPanel.add(new JLabel("Charge:")); chargeFilterPanel.add(charge);
    	panelFilter.add(chargeFilterPanel);
    	JPanel articleFilterPanel = new JPanel();
    	articleFilterPanel.setBorder(new TitledBorder("Article"));
    	artikel = new JTextField(); artikel.setColumns(15); articleFilterPanel.add(new JLabel(isEN ? "Article:" : "Artikel:")); articleFilterPanel.add(artikel);
    	antiArticle = new JCheckBox(); antiArticle.setText(isEN ? "Anti Article?" : "Anti Artikel?"); articleFilterPanel.add(antiArticle); antiArticle.setVisible(false);
    	panelFilter.add(articleFilterPanel);
    	JPanel backforwFilterPanel = new JPanel();
    	goBackFilterIfMixed = new JCheckBox(); goBackFilterIfMixed.setText("Backward"); goBackFilterIfMixed.setToolTipText("Search chains in backward direction (from found criteria to senders)."); backforwFilterPanel.add(goBackFilterIfMixed);
    	goForwardFilter = new JCheckBox(); goForwardFilter.setText("Forward"); goForwardFilter.setToolTipText("Search chains in forward direction (from found criteria to recipients)."); backforwFilterPanel.add(goForwardFilter);
    	panelFilter.add(backforwFilterPanel);
    	//showAll = new JCheckBox(); showAll.setText("Show all?"); showAll.setToolTipText("also if filtered out? Then the filtered nodes/edges may be visualized in the Visualizer node"); panelFilter.add(showAll);
    	panel.add(panelFilter);
    	
    	addTab("Tracing/Filtering", panel);
    	addTab("Database connection", dbui);
    }
	
	@Override
	protected void saveSettingsTo( final NodeSettingsWO settings )
			throws InvalidSettingsException {
		
		settings.addString( MyKrisenInterfacesNodeModel.PARAM_FILENAME, dbui.getFilename() );
		settings.addString( MyKrisenInterfacesNodeModel.PARAM_LOGIN, dbui.getLogin() );
		settings.addString( MyKrisenInterfacesNodeModel.PARAM_PASSWD, dbui.getPasswd() );
		settings.addBoolean( MyKrisenInterfacesNodeModel.PARAM_OVERRIDE, dbui.isOverride() );
		//settings.addBoolean(MyKrisenInterfacesNodeModel.PARAM_TRACINGBACK, tracingBack.isSelected());
		//settings.addInt(MyKrisenInterfacesNodeModel.PARAM_TRACINGTYPE, tracingType.getSelectedIndex());
		settings.addBoolean(MyKrisenInterfacesNodeModel.PARAM_CC, doCrossContaminateAll.isSelected());
		settings.addBoolean(MyKrisenInterfacesNodeModel.PARAM_ETO, enforceTemporalOrder.isSelected());
		settings.addBoolean( MyKrisenInterfacesNodeModel.PARAM_CLUSTERING, doClustering.isSelected() );
		//int omitMax = 0;
		//if (tracingOmitMax.getValue() != null && tracingOmitMax.getValue() instanceof Integer) omitMax = (int) tracingOmitMax.getValue();
		//settings.addInt(MyKrisenInterfacesNodeModel.PARAM_TRACINGOMITMAX, omitMax);
		settings.addString(MyKrisenInterfacesNodeModel.PARAM_FILTERORAND, OrAnd.getSelectedItem().toString());
		settings.addBoolean( MyKrisenInterfacesNodeModel.PARAM_FILTERCASESENSITIVITY, filterCaseSensitive.isSelected() );
		settings.addBoolean( MyKrisenInterfacesNodeModel.PARAM_ANONYMIZE, doAnonymize.isSelected() );
		settings.addString( MyKrisenInterfacesNodeModel.PARAM_FILTER_COMPANY, company.getText() );
		settings.addString( MyKrisenInterfacesNodeModel.PARAM_FILTER_CHARGE, charge.getText() );
		settings.addString( MyKrisenInterfacesNodeModel.PARAM_FILTER_ARTIKEL, artikel.getText() );
		settings.addBoolean( MyKrisenInterfacesNodeModel.PARAM_ANTIARTICLE, antiArticle.isSelected() );
		settings.addBoolean( MyKrisenInterfacesNodeModel.PARAM_FILTERBACKIFMIXED, goBackFilterIfMixed.isSelected());
		settings.addBoolean(MyKrisenInterfacesNodeModel.PARAM_FILTERFORWARD, goForwardFilter.isSelected());
		//settings.addBoolean(MyKrisenInterfacesNodeModel.PARAM_SHOWALL, showAll.isSelected());
		settings.addBoolean( MyKrisenInterfacesNodeModel.PARAM_SHOWCASES, showCasesFilter.isSelected() );
		settings.addBoolean( MyKrisenInterfacesNodeModel.PARAM_ANTICOMPANY, antiCompany.isSelected() );
		
	    SimpleDateFormat sdfToDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 2012-07-10 00:00:00
	    String strDate = dateFrom.getDate() == null ? "" : sdfToDate.format(dateFrom.getDate());
	    if (!strDate.isEmpty()) strDate = strDate.substring(0, strDate.indexOf(" ")) + " 00:00:00";
		settings.addString(MyKrisenInterfacesNodeModel.PARAM_FILTER_DATEFROM, strDate);
		strDate = dateTo.getDate() == null ? "" : sdfToDate.format(dateTo.getDate());
		if (!strDate.isEmpty()) strDate = strDate.substring(0, strDate.indexOf(" ")) + " 23:59:59";
		settings.addString(MyKrisenInterfacesNodeModel.PARAM_FILTER_DATETO, strDate);
	}

	@Override
	protected void loadSettingsFrom( final NodeSettingsRO settings, final PortObjectSpec[] specs )  {		
		try {
			
			dbui.setFilename( settings.getString( MyKrisenInterfacesNodeModel.PARAM_FILENAME ) );
			dbui.setLogin( settings.getString( MyKrisenInterfacesNodeModel.PARAM_LOGIN ) );
			dbui.setPasswd( settings.getString( MyKrisenInterfacesNodeModel.PARAM_PASSWD ) );
			dbui.setOverride( settings.getBoolean( MyKrisenInterfacesNodeModel.PARAM_OVERRIDE ) );
			//tracingBack.setSelected(settings.getBoolean(MyKrisenInterfacesNodeModel.PARAM_TRACINGBACK));
			//tracingType.setSelectedIndex(settings.getInt(MyKrisenInterfacesNodeModel.PARAM_TRACINGTYPE));
			if (settings.containsKey(MyKrisenInterfacesNodeModel.PARAM_CC)) doCrossContaminateAll.setSelected(settings.getBoolean(MyKrisenInterfacesNodeModel.PARAM_CC));
			if (settings.containsKey(MyKrisenInterfacesNodeModel.PARAM_ETO)) enforceTemporalOrder.setSelected(settings.getBoolean(MyKrisenInterfacesNodeModel.PARAM_ETO));
			if (settings.containsKey(MyKrisenInterfacesNodeModel.PARAM_CLUSTERING)) doClustering.setSelected(settings.getBoolean(MyKrisenInterfacesNodeModel.PARAM_CLUSTERING));
			//tracingOmitMax.setValue(settings.getInt(MyKrisenInterfacesNodeModel.PARAM_TRACINGOMITMAX));
			if (settings.containsKey(MyKrisenInterfacesNodeModel.PARAM_FILTERCASESENSITIVITY)) filterCaseSensitive.setSelected(settings.getBoolean(MyKrisenInterfacesNodeModel.PARAM_FILTERCASESENSITIVITY));
			if (settings.containsKey(MyKrisenInterfacesNodeModel.PARAM_FILTERORAND) && settings.getString(MyKrisenInterfacesNodeModel.PARAM_FILTERORAND) != null) OrAnd.setSelectedItem(settings.getString(MyKrisenInterfacesNodeModel.PARAM_FILTERORAND));
			doAnonymize.setSelected(settings.getBoolean(MyKrisenInterfacesNodeModel.PARAM_ANONYMIZE));
			company.setText(settings.getString( MyKrisenInterfacesNodeModel.PARAM_FILTER_COMPANY));
			charge.setText(settings.getString( MyKrisenInterfacesNodeModel.PARAM_FILTER_CHARGE ));
			artikel.setText(settings.getString( MyKrisenInterfacesNodeModel.PARAM_FILTER_ARTIKEL ));
			antiArticle.setSelected(settings.getBoolean(MyKrisenInterfacesNodeModel.PARAM_ANTIARTICLE));
			goBackFilterIfMixed.setSelected(settings.getBoolean(MyKrisenInterfacesNodeModel.PARAM_FILTERBACKIFMIXED));
			goForwardFilter.setSelected(settings.getBoolean(MyKrisenInterfacesNodeModel.PARAM_FILTERFORWARD));
			//showAll.setSelected(settings.getBoolean(MyKrisenInterfacesNodeModel.PARAM_SHOWALL));
			if (settings.containsKey(MyKrisenInterfacesNodeModel.PARAM_SHOWCASES)) showCasesFilter.setSelected(settings.getBoolean(MyKrisenInterfacesNodeModel.PARAM_SHOWCASES));
			antiCompany.setSelected(settings.getBoolean(MyKrisenInterfacesNodeModel.PARAM_ANTICOMPANY));

		    SimpleDateFormat sdfToDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 2012-07-10 00:00:00
	    	try {
	    		String df = settings.getString(MyKrisenInterfacesNodeModel.PARAM_FILTER_DATEFROM);
				if (df != null && !df.isEmpty()) dateFrom.setDate(sdfToDate.parse(df));
	    		df = settings.getString(MyKrisenInterfacesNodeModel.PARAM_FILTER_DATETO);
				if (df != null && !df.isEmpty()) dateTo.setDate(sdfToDate.parse(df));
			}
	    	catch (ParseException e) {
				e.printStackTrace();
			}
		}
		catch( InvalidSettingsException ex ) {
			
			ex.printStackTrace( System.err );
		}
		
	}
}