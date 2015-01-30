package org.codehaus.groovy.grails.plugins.gwt.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GwtActionServiceAsync {
    void execute(grails.plugins.gwt.shared.Action arg0, AsyncCallback callback);
}
