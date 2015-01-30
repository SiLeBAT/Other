package org.codehaus.groovy.grails.plugins.gwt.client;

import com.google.gwt.user.client.rpc.RemoteService;

public interface GwtActionService extends RemoteService {
    grails.plugins.gwt.shared.Response execute(grails.plugins.gwt.shared.Action arg0);
}
