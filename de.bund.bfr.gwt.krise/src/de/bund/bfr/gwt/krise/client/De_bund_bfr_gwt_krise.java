package de.bund.bfr.gwt.krise.client;

import com.google.gwt.core.client.EntryPoint;  
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

import de.bund.bfr.gwt.krise.shared.MyTracingData;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class De_bund_bfr_gwt_krise implements EntryPoint {

	public void onModuleLoad() {
		RootLayoutPanel.get().add(new MyTracingMap());
		/*
	      //create textboxes
	      TextBox textBox1 = new TextBox(); 
	      TextBox textBox2 = new TextBox();

	      //add text to text box
	      textBox2.setText("Hello World!");

	      //set textbox as readonly
	      textBox2.setReadOnly(true);

	      // Add text boxes to the root panel.
	      VerticalPanel panel = new VerticalPanel();
	      panel.setSpacing(10);
	      panel.add(textBox1);
	      panel.add(textBox2);

	      RootPanel.get().add(panel);
	      */
	}
	public Canvas getViewPanel() {
		HLayout hLayout = new HLayout(5);
		//hLayout.addMember(new Label("Dummy Label in a Horizontal Layout"));
		// Add the map to the HTML host page
		hLayout.addMember(new MyTracingMap());		
		return hLayout;
	}
	
	public void onModuleLoad2() {
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
