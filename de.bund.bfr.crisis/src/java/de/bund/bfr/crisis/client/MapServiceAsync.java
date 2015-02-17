package de.bund.bfr.crisis.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("rpc")
public interface MapServiceAsync {
    void search(java.lang.String arg0, AsyncCallback callback);
    void getStationId(java.lang.String arg0, AsyncCallback callback);
    void searchSuggestions(java.lang.String arg0, AsyncCallback callback);
}
