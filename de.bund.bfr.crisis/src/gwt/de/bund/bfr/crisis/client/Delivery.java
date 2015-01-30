package de.bund.bfr.crisis.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Overlay type for {@link de.bund.bfr.crisis.Delivery}.
 */
class Delivery extends JavaScriptObject {

	// Overlay types always have protected, zero-arg ctors
	protected Delivery() {
	}

	public final native int getId() /*-{ return this.id; }-*/;

	public final native int getStationId() /*-{ return this.station; }-*/;

	public final native int getRecipientId() /*-{ return this.recipient; }-*/;
}