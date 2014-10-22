package de.bund.bfr.gwt.krise.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.bund.bfr.gwt.krise.shared.MyTracingGISData;

public class MyCallbackGIS implements AsyncCallback<MyTracingGISData> {

	private MyTracingMap tracingMap;

	public MyCallbackGIS(MyTracingMap tracingMap) {
		this.tracingMap = tracingMap;
	}

	public void onFailure(Throwable caught) {
		com.google.gwt.user.client.Window.alert(caught.getMessage());
	}

	@Override
	public void onSuccess(MyTracingGISData result) {
		tracingMap.fillMap(result);
	}
}
