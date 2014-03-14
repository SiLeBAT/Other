package de.bund.bfr.gwt.krise.client;

import com.google.gwt.core.client.EntryPoint;  
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

import de.bund.bfr.gwt.krise.shared.MyTracingData;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class De_bund_bfr_gwt_krise implements EntryPoint {

	public void onModuleLoad() {
		VLayout vLayout = new VLayout();
		vLayout.setShowEdges(true);  
	    vLayout.setWidth100();
	    vLayout.setHeight100();
	    vLayout.setMembersMargin(5);  
	    vLayout.setLayoutMargin(10);  
	    MyListGrid stationGrid = new MyListGrid(MyTracingData.STATION);   
	    stationGrid.setHeight("90%");
	    stationGrid.fetchMyData(null);
	    vLayout.addMember(stationGrid);  

	    HLayout hl = stationGrid.addButtons(stationGrid, null, 0);
	    hl.setHeight("*");
	    vLayout.addMember(hl);  

	                /*
	        Canvas canvas = new Canvas();  
		canvas.setMargin(10);
		canvas.setPadding(5);
        canvas.setWidth("100%");  
        canvas.setHeight("80%");  
        canvas.setAlign(Alignment.CENTER);  
        Canvas canvas2 = new Canvas();
        canvas2.setHeight("20%");  
        canvas2.setAlign(Alignment.CENTER);
        canvas2.addChild();  
        */
        vLayout.draw();  
    }  
}
