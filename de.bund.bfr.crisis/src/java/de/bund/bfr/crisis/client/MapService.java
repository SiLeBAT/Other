package de.bund.bfr.crisis.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("rpc")
public interface MapService extends RemoteService {
    java.lang.String search(java.lang.String arg0);
    java.lang.String getStationId(java.lang.String arg0);
    java.lang.String searchSuggestions(java.lang.String arg0);
}
