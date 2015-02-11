/*******************************************************************************
 * Copyright (c) 2014 Federal Institute for Risk Assessment (BfR), Germany
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package de.bund.bfr.crisis.client;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.RowEndEditAction;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * @author heisea
 */
public class ProductGrid extends ListGrid {
	/**
	 * Initializes ProductGrid.
	 */
	public ProductGrid() {
		setHeight100();
		setWidth(800);
		setDrawAheadRatio(4);
		setCanExpandRecords(true);

		setAutoFetchData(true);
		setDataSource(ProductDS.getInstance());
		
        setCanEdit(true);    
        setModalEditing(true);    
        setEditEvent(ListGridEditEvent.CLICK);    
        setListEndEditAction(RowEndEditAction.NEXT);    
        setAutoSaveEdits(true);          
        setCanRemoveRecords(true);  
	}
	
	public void updateStation(Record stationRecord) {
		fetchRelatedData(stationRecord, StationDS.getInstance());    
	}
	
	/* (non-Javadoc)
	 * @see com.smartgwt.client.widgets.grid.ListGrid#getExpansionComponent(com.smartgwt.client.widgets.grid.ListGridRecord)
	 */
	@Override
	protected Canvas getExpansionComponent(ListGridRecord record) {
		return new LotGrid(record);
	}
}

class LotGrid extends ListGrid {
	/**
	 * Initializes ProductGrid.
	 */
	public LotGrid(Record productRecord) {
		setHeight(1000);
		setWidth100();
		setDrawAheadRatio(4);
		setCanExpandRecords(true);

		setAutoFetchData(true);
		setDataSource(LotDS.getInstance());
		fetchRelatedData(productRecord, StationDS.getInstance());  
		
        setCanEdit(true);    
        setModalEditing(true);    
        setEditEvent(ListGridEditEvent.CLICK);    
        setListEndEditAction(RowEndEditAction.NEXT);    
        setAutoSaveEdits(true);  
        setCanRemoveRecords(true);  
	}
	
	/* (non-Javadoc)
	 * @see com.smartgwt.client.widgets.grid.ListGrid#getExpansionComponent(com.smartgwt.client.widgets.grid.ListGridRecord)
	 */
	@Override
	protected Canvas getExpansionComponent(ListGridRecord record) {
		return new DeliveryGrid(record);
	}
}

class DeliveryGrid extends ListGrid {
	/**
	 * Initializes ProductGrid.
	 */
	public DeliveryGrid(Record lotRecord) {
		setHeight(1000);
		setWidth100();
		setDrawAheadRatio(4);

		setAutoFetchData(true);
		setDataSource(DeliveryDS.getInstance());
		fetchRelatedData(lotRecord, LotDS.getInstance());   
		
        setCanEdit(true);    
        setModalEditing(true);    
        setEditEvent(ListGridEditEvent.CLICK);    
        setListEndEditAction(RowEndEditAction.NEXT);    
        setAutoSaveEdits(true);  
        setCanRemoveRecords(true);  
	}
}