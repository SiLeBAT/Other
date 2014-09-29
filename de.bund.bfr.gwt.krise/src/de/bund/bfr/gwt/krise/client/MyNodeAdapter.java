package de.bund.bfr.gwt.krise.client;

import org.thechiselgroup.choosel.protovis.client.PVNodeAdapter;

public class MyNodeAdapter implements PVNodeAdapter<MyNodes> {

	@Override
	public String getNodeName(MyNodes t) {
		// TODO Auto-generated method stub
		return t.getLabel();
	}

	@Override
	public Object getNodeValue(MyNodes t) {
		// TODO Auto-generated method stub
		return t.getValue();
	}
	

}
