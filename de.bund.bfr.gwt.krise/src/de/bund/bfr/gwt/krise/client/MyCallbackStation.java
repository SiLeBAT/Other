package de.bund.bfr.gwt.krise.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.bund.bfr.gwt.krise.shared.MyTracingGISData;
import de.bund.bfr.gwt.krise.shared.Station;

public class MyCallbackStation implements AsyncCallback<Station> {

	private MyTracingMap tracingMap;

	public MyCallbackStation(MyTracingMap tracingMap) {
		this.tracingMap = tracingMap;
	}

	public void onFailure(Throwable caught) {
		com.google.gwt.user.client.Window.alert(caught.getMessage());
	}

	@Override
	public void onSuccess(Station result) {
		//tracingMap.fillMap(result);
	}
}
