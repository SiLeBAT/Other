package de.bund.bfr.gwt.krise.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.bund.bfr.gwt.krise.shared.MyTracingData;
import de.bund.bfr.gwt.krise.shared.MyTracingGISData;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface HsqldbServiceAsync {
	void getData(int table, String id, AsyncCallback<MyTracingData> callback) throws IllegalArgumentException;
	void getGISData(String searchString, AsyncCallback<MyTracingGISData> callback) throws IllegalArgumentException;
}
