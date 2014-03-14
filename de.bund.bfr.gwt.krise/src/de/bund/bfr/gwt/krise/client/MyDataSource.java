package de.bund.bfr.gwt.krise.client;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.SchemaSet;
import com.smartgwt.client.data.XMLTools;
import com.smartgwt.client.data.XSDLoadCallback;

public class MyDataSource extends DataSource {
    // Override all four CRUD operations - create, retrieve, update and delete 
    // (add, fetch, update and remove in SmartClient terminology).
    
    // Note that the parameters sent by the client arrive here already converted to Java Maps
    // by the SmartClient Server - with SmartClient Pro, Power and Enterprise Editions, there's 
    // no need to worry about conversion to and from XML or JSON even in a custom DS 
    // implementation
   
	DataSource myDataSource = null;

	public MyDataSource(String myDataSourceName) {
		getDS(myDataSourceName);
	}
	public DataSource getDS() {
		return myDataSource;
	}
	private void getDS(final String myDataSourceName) {
		final String schemaFile = "/shared/ds/" + myDataSourceName + ".xsd";
	    final XSDLoadCallback callback = new XSDLoadCallback() {
	    	@Override
	        public void execute(SchemaSet schemaSet) {
	    		myDataSource = schemaSet.getSchema(myDataSourceName);
	    		myDataSource.setClientOnly(true);
	        }
	    };
	    XMLTools.loadXMLSchema(schemaFile, callback);	
	}
	@Override
    public void addData(Record newRecord) {
    	super.addData(newRecord);
    }

	@Override
    public void fetchData() {
		super.fetchData();
    }

	@Override
    public void removeData(Record data) {
		super.removeData(data);
    }

	@Override
    public void updateData(Record updatedRecord) {
		super.updateData(updatedRecord);
    }
}