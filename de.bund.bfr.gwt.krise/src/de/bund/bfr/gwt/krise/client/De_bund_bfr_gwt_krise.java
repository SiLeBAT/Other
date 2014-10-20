package de.bund.bfr.gwt.krise.client;

import com.github.gwtd3.api.D3;
import com.google.gwt.core.client.EntryPoint;  
import com.google.gwt.core.client.JsArray;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

import de.bund.bfr.gwt.krise.shared.MyTracingData;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.Selection;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.events.SelectHandler;
import com.chap.links.client.Network;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class De_bund_bfr_gwt_krise implements EntryPoint {

	public void onModuleLoad() {
		RootLayoutPanel.get().add(new MyTracingMap());
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
	
    public void onModuleLoad4() {
    	RootPanel.get().add(new ForceDirectedLayout());
    	final Label versionLabel = new Label("d3.js current version: " + D3.version());
  	  	RootPanel.get().add(versionLabel);
    }

    Network network = null;
    
    /**
     * This is the entry point method.
     */
    public void onModuleLoad3() {
        // Create a callback to be called when the visualization API
        // has been loaded.
        Runnable onLoadCallback = new Runnable() {
          public void run() {

            // Create nodes table with some data
            DataTable nodes = DataTable.create();
            nodes.addColumn(DataTable.ColumnType.NUMBER, "id");
            nodes.addColumn(DataTable.ColumnType.STRING, "text");
            nodes.addColumn(DataTable.ColumnType.STRING, "style");
            nodes.addRow(); 
            int i = 0;
            nodes.setValue(i, 0, 1);
            nodes.setValue(i, 1, "Node 1");
            nodes.setValue(i, 2, "circle");
            nodes.addRow(); 
            i++;
            nodes.setValue(i, 0, 2);
            nodes.setValue(i, 1, "Node 2");
            nodes.setValue(i, 2, "circle");
            nodes.addRow(); 
            i++;
            nodes.setValue(i, 0, 3);
            nodes.setValue(i, 1, "Node 3");
            nodes.setValue(i, 2, "circle");

            // Create links table with some data
            DataTable links = DataTable.create();
            links.addColumn(DataTable.ColumnType.NUMBER, "from");
            links.addColumn(DataTable.ColumnType.NUMBER, "to");
            links.addColumn(DataTable.ColumnType.STRING, "title");
            links.addColumn(DataTable.ColumnType.STRING, "style");
            links.addColumn(DataTable.ColumnType.STRING, "text");
            links.addRow(); 
            i = 0;
            links.setValue(i, 0, 1);
            links.setValue(i, 1, 2);
            links.setValue(i, 2, "WEE");
            links.setValue(i, 3, "moving-arrows");
            links.addRow(); 
            i++;
            links.setValue(i, 0, 1);
            links.setValue(i, 1, 3);
            links.setValue(i, 3, "moving-arrows");
            links.addRow(); 
            i++;
            links.setValue(i, 0, 2);
            links.setValue(i, 1, 3);
            links.setValue(i, 3, "moving-arrows");
            links.setValue(i, 4, "0.4");
            
            // Create options
            Network.Options options = Network.Options.create();
            options.setWidth("100%");
            options.setHeight("800px");
            
            // create the visualization, with data and options
            network = new Network(nodes, links, options);
            network.addSelectHandler(createSelectHandler(network));
            
            RootPanel.get("mynetwork").add(network);
          }
        };
        
        // Load the visualization api, passing the onLoadCallback to be called
        // when loading is done.
        VisualizationUtils.loadVisualizationApi(onLoadCallback);  
    }
	private SelectHandler createSelectHandler(final Network network) {
		return new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				JsArray<Selection> sel = network.getSelections();

				String info = "Selected nodes: ";
				for (int i = 0; i < sel.length(); i++) {
					int row = sel.get(i).getRow();
					info += row + " ";
				}
				if (sel.length() == 0) {
					info += "none";
				}

				// RootPanel.get("lblInfo").add(new Label(info));
				System.out.println(info);
			}
		};
	}
}
