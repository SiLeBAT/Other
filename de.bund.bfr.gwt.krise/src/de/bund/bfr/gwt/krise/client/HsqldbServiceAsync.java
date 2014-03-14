package de.bund.bfr.gwt.krise.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.bund.bfr.gwt.krise.shared.MyTracingData;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface HsqldbServiceAsync {
	void getData(int table, String id, AsyncCallback<MyTracingData> callback) throws IllegalArgumentException;
}
