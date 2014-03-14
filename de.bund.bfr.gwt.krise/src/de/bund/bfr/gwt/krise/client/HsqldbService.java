package de.bund.bfr.gwt.krise.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.bund.bfr.gwt.krise.shared.MyTracingData;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("tracingDB")
public interface HsqldbService extends RemoteService {
	MyTracingData getData(int table, String id) throws IllegalArgumentException;
}
