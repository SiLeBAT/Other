package de.bund.bfr.crisis.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Overlay type for a non-materialized grails reference.
 */
class Reference extends JavaScriptObject {

	// Overlay types always have protected, zero-arg ctors
	protected Reference() {
	}

	public final native int getId() /*-{ return this.id; }-*/;
	
	public final native String getType() /*-{ return this["class"]; }-*/;
}