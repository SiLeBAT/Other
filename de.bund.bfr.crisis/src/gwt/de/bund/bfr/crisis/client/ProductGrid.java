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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.RowEndEditAction;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.GroupValueFunction;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
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
		setCanGroupBy(true);
		setCanDrag(true);
		setCanDragReposition(true);
		setCanDragSelect(true);

		getField("id").setGroupValueFunction(new GroupValueFunction() {
			public Object getGroupValue(Object value, ListGridRecord record, ListGridField field, String fieldName,
					ListGrid grid) {
				return record.getAttributeAsInt("_simgroup");
			}
		});
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
		IButton collapseButton = new IButton("Collapse all");
		collapseButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				getGroupTree().closeAll();
			}
		});
		final IButton groupButton = new IButton("Group similar");
		groupButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (groupButton.isSelected()) {
					simGroupRecords();
					setGroupByField("id");
				}
				else
					ungroup();
			}
		});
		groupButton.setActionType(SelectionType.CHECKBOX);

		hLayout.addMember(addButton);
		hLayout.addMember(collapseButton);
		hLayout.addMember(groupButton);

		layout.addMember(this);
		layout.addMember(hLayout);

		layout.setHeight("*");
		return layout;
	}

	private void simGroupRecords() {
		ListGridRecord[] records = getRecords();
		Map<Integer, Set<Integer>> clusters = new HashMap<Integer, Set<Integer>>();
		for (int index1 = 0; index1 < records.length; index1++) {
			for (int index2 = index1 + 1; index2 < records.length; index2++) {
				if (isSimilar(records[index1], records[index2])) {
					Set<Integer> cluster = clusters.get(index1);
					if (cluster == null) {
						cluster = new HashSet<Integer>();
						cluster.add(index1);
					}
					// merge clusters
					Set<Integer> cluster2 = clusters.get(index2);
					if (cluster2 == null) {
						cluster2 = new HashSet<Integer>();
						cluster2.add(index2);
					}
					cluster.addAll(cluster2);
					for (Integer id : cluster2)
						clusters.put(id, cluster);
				}
			}
		}
		
		for (Entry<Integer, Set<Integer>> cluster : clusters.entrySet()) 
			records[cluster.getKey()].setAttribute("_simgroup", cluster.getValue().iterator().next());
	}

	private boolean isSimilar(ListGridRecord listGridRecord, ListGridRecord listGridRecord2) {
		String[] attr1 = listGridRecord.getAttributes();
		String[] attr2 = listGridRecord2.getAttributes();
		double sim = 0, weight = 0;
		for (int fieldIndex = 0; fieldIndex < attr1.length; fieldIndex++)
			if (attr1[fieldIndex] != null && !attr1[fieldIndex].isEmpty() &&
				attr2[fieldIndex] != null && !attr2[fieldIndex].isEmpty()) {
				sim += Similarities.getEditSim(attr1[fieldIndex], attr2[fieldIndex]);
				weight += 1;
			}
		return weight == 0 || (sim / weight > .8);
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

		for (ListGridField field : getFields()) {
			field.setOptionDataSource(ProductDS.getInstance());
		}
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
	 * Initializes LotGrid.
	 */
	public LotGrid(Record productRecord) {
		setCanExpandRecords(true);
		setDataSource(LotDS.getInstance());
		fetchRelatedData(productRecord, ProductDS.getInstance());
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
	 * Initializes DeliveryGrid.
	 */
	public DeliveryGrid(Record lotRecord) {
		setDataSource(DeliveryDS.getInstance());
		fetchRelatedData(lotRecord, LotDS.getInstance());
	}
}
