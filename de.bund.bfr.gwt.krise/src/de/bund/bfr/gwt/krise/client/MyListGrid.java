package de.bund.bfr.gwt.krise.client;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.RowEndEditAction;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellSavedEvent;
import com.smartgwt.client.widgets.grid.events.CellSavedHandler;
import com.smartgwt.client.widgets.grid.events.EditCompleteEvent;
import com.smartgwt.client.widgets.grid.events.EditCompleteHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

import de.bund.bfr.gwt.krise.shared.MyTracingData;

public class MyListGrid extends ListGrid {

	private final HsqldbServiceAsync hsqldbService;
	private final MyWaitWindow pleaseWaitWindow;
	private int tableType;
	
	public MyListGrid(int tableType) {
		this(new MyWaitWindow(), (HsqldbServiceAsync) GWT.create(HsqldbService.class), tableType);
	}
	public MyListGrid(MyWaitWindow pleaseWaitWindow, HsqldbServiceAsync hsqldbService, int tableType) {
		this.pleaseWaitWindow = pleaseWaitWindow;
		this.hsqldbService = hsqldbService;
		this.tableType = tableType;
        this.setCanEdit(true);  
        this.setModalEditing(true);  
        this.setEditEvent(ListGridEditEvent.CLICK);  
        this.setListEndEditAction(RowEndEditAction.NEXT);  
        this.setAutoSaveEdits(false);  
        
        this.setBaseStyle("ft-Base");
        this.setBodyStyleName("ft-Base");
        this.setShowHover(false);
        this.setShowRollOver(false);
        this.setShowRowNumbers(true);
        
		this.setHeaderBaseStyle("ft-Header" + tableType);
		this.setHeaderBarStyle("ft-Header" + tableType);
		this.setHeaderTitleStyle("ft-Header" + tableType);
        this.setWidth(tableType == 0 ? "100%" : "95%");  
        this.setHeight(tableType == 0 ? "100%" : (475 - tableType * 90)+"");  
        //this.setCellHeight(25);  
        //this.setDrawAheadRatio(4);  
        this.setCanExpandRecords(tableType < MyTracingData.DELIVERY);    
  
        this.setShowFilterEditor(true);  
        this.setFilterOnKeypress(true);  
        this.setFastCellUpdates(true);
        this.setAutoFetchData(true);     
        
        this.addCellSavedHandler(new CellSavedHandler() {
            public void onCellSaved(CellSavedEvent event) {
            	//updateDetails(event);
            }
          });
        this.addEditCompleteHandler(new EditCompleteHandler() {
			@Override
			public void onEditComplete(EditCompleteEvent event) {
            	//updateDetails(event);
			}
          });
    }
	public void updateDetails(EditCompleteEvent event) {
    	String message = this.getAllEditRows().length + " / ";
    	for (Object o : event.getNewValues().keySet()) {
    		message += o + "->" + event.getNewValues().get(o) + " / ";
    	}
    	message += "\n" + getChanges(this);
    	SC.say(message);
	}
	public void updateDetails(CellSavedEvent event) {
    	String message = event.getRecord().getAttributeAsInt("ID") + " - " + this.getFieldName(event.getColNum()) + " - " + event.getOldValue() + " - " + event.getNewValue();    	
    	message += "\n" + getChanges(this);
    	SC.say(message);
	}
	private String getChanges(MyListGrid grid) {
		String message = "old length: " + grid.getDataSource().getCacheData().length + " vs. newLength" + grid.getTotalRows() + "\n<br>";
    	for (int i=0;i<grid.getAllEditRows().length;i++) {
    		message += grid.getAllEditRows()[i] + " / ";
    	}
    	return message;
	}

	public void fetchMyData(final ListGridRecord record) {
		pleaseWaitWindow.centerInPage(); pleaseWaitWindow.show();  	
		MyCallback myCallback = new MyCallback(this, pleaseWaitWindow);
        //hsqldbService.getStations(myCallback);
        hsqldbService.getData(tableType, record == null ? "" : record.getAttribute("ID"), myCallback);		
	}
	@Override  
    protected Canvas getExpansionComponent(final ListGridRecord record) {    
        VLayout layout = new VLayout(5);  
        layout.setPadding(5);
        final MyListGrid subGrid = new MyListGrid(pleaseWaitWindow, hsqldbService, tableType + 1);
        //productGrid.setDataSource(getRelatedDataSource(record));
        //productGrid.fetchRelatedData(record, SupplyCategoryXmlDS.getInstance());  
        subGrid.fetchMyData(record);
        layout.addMember(subGrid);         
        layout.addMember(addButtons(subGrid, record, tableType + 1));  
        
        return layout;  
    }
	public HLayout addButtons(final MyListGrid subGrid, final ListGridRecord record, int tt) {
		//setStyle(subGrid.getFilterEditor(), tt);
        HLayout hLayout = new HLayout(10);  
        hLayout.setAlign(Alignment.CENTER);  

        IButton newButton = new IButton("New");  
        newButton.setBaseStyle("ft-Header" + tt);
        newButton.setTop(250);  
        newButton.addClickHandler(new ClickHandler() {  
            public void onClick(ClickEvent event) {  
            	subGrid.startEditingNew();  
            }  
        });  
        hLayout.addMember(newButton);  
/*
        IButton saveButton = new IButton("Save");  
        saveButton.setBaseStyle("ft-Header" + tt);
        saveButton.addClickHandler(new ClickHandler() {  
            public void onClick(ClickEvent event) {  
            	SC.say(getChanges(subGrid));
            	subGrid.saveAllEdits(); 
            }  
        });  
        hLayout.addMember(saveButton);  
*/
        /*
        IButton discardButton = new IButton("Discard");  
        discardButton.setBaseStyle("ft-Header" + tt);
        discardButton.addClickHandler(new ClickHandler() {  
            public void onClick(ClickEvent event) {  
            	subGrid.discardAllEdits();  
            }  
        });  
        hLayout.addMember(discardButton);  
*/
        final IButton removeButton = new IButton("Remove");  
        removeButton.setBaseStyle("ft-Header" + tt);
        removeButton.addClickHandler(new ClickHandler() {  
            public void onClick(ClickEvent event) {  
                subGrid.removeSelectedData();  
                //removeButton.disable();  
            }  
        });  
        hLayout.addMember(removeButton);  

        if (record != null) {
        	final MyListGrid tg = this;;
            IButton closeButton = new IButton("Close");  
            closeButton.setBaseStyle("ft-Header" + tt);
            closeButton.addClickHandler(new ClickHandler() {  
                public void onClick(ClickEvent event) {  
                    tg.collapseRecord(record);  
                }  
            });  
            hLayout.addMember(closeButton);  
        }
                                         
        return hLayout;
	}
}
