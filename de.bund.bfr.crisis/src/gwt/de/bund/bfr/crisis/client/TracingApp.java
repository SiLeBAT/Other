package de.bund.bfr.crisis.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TracingApp implements EntryPoint {
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		RootPanel container = RootPanel.get("mapContainer");
		AbsolutePanel panel = new AbsolutePanel();
		panel.setSize("100%", "100%");
		TracingMap tracingMap = new TracingMap();
		panel.add(tracingMap, 0, 0);
		tracingMap.addChildrenToParent(panel);
//		panel.add(new StationView(StationDS), 50, 50);
		container.add(panel);
	}

}
