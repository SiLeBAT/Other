package de.bund.bfr.crisis.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * Overlay type for {@link de.bund.bfr.crisis.Station}.
 */
class SearchResult extends JavaScriptObject {

	protected SearchResult() {
	}

	public final native JsArray<Station> getStations() /*-{ return this.stations; }-*/;

	public final native JsArray<Delivery> getDeliveries() /*-{ return this.deliveries; }-*/;
}