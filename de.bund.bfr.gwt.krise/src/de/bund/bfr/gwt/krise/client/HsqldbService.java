package de.bund.bfr.gwt.krise.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.bund.bfr.gwt.krise.shared.MyTracingData;
import de.bund.bfr.gwt.krise.shared.MyTracingGISData;
import de.bund.bfr.gwt.krise.shared.Station;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("tracingDB")
public interface HsqldbService extends RemoteService {
	MyTracingData getData(int table, String id) throws IllegalArgumentException;
	MyTracingGISData getGISData(String searchString) throws IllegalArgumentException;
	Station getStationInfo(int stationId) throws IllegalArgumentException;
}
