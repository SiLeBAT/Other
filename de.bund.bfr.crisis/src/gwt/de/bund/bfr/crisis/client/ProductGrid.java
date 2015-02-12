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
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.AutoComplete;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.RowEndEditAction;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

class EditableGrid extends ListGrid {
	/**
	 * Initializes EditableGrid.
	 */
	public EditableGrid() {
		setHeight("*");
		setWidth100();
		setDrawAheadRatio(4);

		setCanEdit(true);
		setEditByCell(true);  
		setModalEditing(true);
		setEditEvent(ListGridEditEvent.CLICK);
		setListEndEditAction(RowEndEditAction.NEXT);
		setAutoSaveEdits(true);
		setCanRemoveRecords(true);
		setWarnOnRemoval(true);
		setAlternateRecordStyles(true);
		setShowAllRecords(true);
		setBodyOverflow(Overflow.VISIBLE);
		setOverflow(Overflow.VISIBLE);
		
		
	}
	
	public Canvas wrapWithActionButtons() {
		VLayout layout = new VLayout(5);
		layout.setPadding(5);

		HLayout hLayout = new HLayout(10);
		hLayout.setAlign(Alignment.CENTER);

		IButton addButton = new IButton("Add new " + getDataSource().getID());
		addButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				ListGridRecord newRecord = new ListGridRecord();
				addData(newRecord);
				selectRecord(newRecord);
			}
		});     
		hLayout.addMember(addButton);
		
		layout.addMember(this);
		layout.addMember(hLayout);
		
		layout.setHeight("*");
		return layout;
	}
}

/**
 * @author heisea
 */
public class ProductGrid extends EditableGrid {
	/**
	 * Initializes ProductGrid.
	 */
	public ProductGrid() {
		setCanExpandRecords(true);
		setDataSource(ProductDS.getInstance());
		setData();
	}

	public void updateStation(Record stationRecord) {
		fetchRelatedData(stationRecord, StationDS.getInstance());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.smartgwt.client.widgets.grid.ListGrid#getExpansionComponent(com.smartgwt.client.widgets.grid.ListGridRecord)
	 */
	@Override
	protected Canvas getExpansionComponent(ListGridRecord record) {
		return new LotGrid(record).wrapWithActionButtons();
	}
}

class LotGrid extends EditableGrid {
	/**
	 * Initializes ProductGrid.
	 */
	public LotGrid(Record productRecord) {
		setCanExpandRecords(true);
		setDataSource(LotDS.getInstance());
		fetchRelatedData(productRecord, StationDS.getInstance());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.smartgwt.client.widgets.grid.ListGrid#getExpansionComponent(com.smartgwt.client.widgets.grid.ListGridRecord)
	 */
	@Override
	protected Canvas getExpansionComponent(ListGridRecord record) {
		return new DeliveryGrid(record).wrapWithActionButtons();
	}
}

class DeliveryGrid extends EditableGrid {
	/**
	 * Initializes ProductGrid.
	 */
	public DeliveryGrid(Record lotRecord) {
		setDataSource(DeliveryDS.getInstance());
		fetchRelatedData(lotRecord, LotDS.getInstance());
	}
}