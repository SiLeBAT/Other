package de.bund.bfr.crisis.client;

import org.gwtopenmaps.openlayers.client.geometry.Point;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Overlay type for {@link de.bund.bfr.crisis.Station}.
 */
class Station extends JavaScriptObject {

	protected Station() {
	}

	public final native String getName() /*-{ return this.name; }-*/;

	public final native int getId() /*-{ return this.id; }-*/;

	public final native double getLatitude() /*-{ return this.latitude; }-*/;

	public final native double getLongitude() /*-{ return this.longitude; }-*/;

	public final Point getPoint() {
		return new Point(getLongitude(), getLatitude());
	}

}