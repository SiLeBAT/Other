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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;
import com.google.gwt.i18n.client.Messages;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragTrackerMode;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.RecordDropAppearance;
import com.smartgwt.client.types.RowEndEditAction;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.VisibilityChangedEvent;
import com.smartgwt.client.widgets.events.VisibilityChangedHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.FormItemValueFormatter;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.GroupValueFunction;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordDropEvent;
import com.smartgwt.client.widgets.grid.events.RecordDropHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

abstract class EditableGrid extends ListGrid {
	Logger logger = Logger.getLogger(this.getClass().getSimpleName());

	/**
	 * Initializes EditableGrid.
	 */
	public EditableGrid(DataSource dataSource) {
		setDataSource(dataSource);
		List<ListGridField> listGridFields = new ArrayList<ListGridField>();
		for (DataSourceField field : dataSource.getFields()) {
			if (!field.getHidden()) listGridFields.add(new ListGridField(field.getName()));
		}
		setFields(listGridFields.toArray(new ListGridField[0]));

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

		// setCanDrag(true);
		setCanReorderRecords(true);
		setCanDragRecordsOut(true);
		setCanAcceptDroppedRecords(true);
		setRecordDropAppearance(RecordDropAppearance.OVER);
		setDragTrackerMode(DragTrackerMode.RECORD);
		// setDragDataAction(DragDataAction.NONE);

		this.addRecordDropHandler(new RecordDropHandler() {
			@Override
			public void onRecordDrop(RecordDropEvent event) {
				if (event.getSourceWidget() != EditableGrid.this) {
					SC.say("Merging is currently only allowed in the same widget");
					return;
				}

				final ListGridRecord sourceRecord = event.getDropRecords()[0];
				final ListGridRecord targetRecord = event.getTargetRecord();
				logger.severe(sourceRecord.toString());
				if (targetRecord != null && sourceRecord != targetRecord) {
					String source = sourceRecord.toMap().toString(), target = targetRecord.toMap().toString();
					SC.confirm("Are you sure to merge the given record\n" + source + "\ninto the record\n" + target
							+ "?", new BooleanCallback() {
						@Override
						public void execute(Boolean value) {
							if (value != null) mergeRecords(sourceRecord, targetRecord);
						}
					});
				}
			}
		});
	}

	private void mergeRecords(final ListGridRecord sourceRecord, final ListGridRecord targetRecord) {
		final String[] sourceAttributes = sourceRecord.getAttributes();
		for (int index = 0; index < sourceAttributes.length; index++) {
			String sourceValue = sourceRecord.getAttribute(sourceAttributes[index]);
			String targetValue = targetRecord.getAttribute(sourceAttributes[index]);
			if (targetValue == null || targetValue.isEmpty())
				targetRecord.setAttribute(sourceAttributes[index], sourceValue);
		}
		moveChildren(sourceRecord, targetRecord, new DSCallback() {
			@Override
			public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest) {
				updateData(targetRecord);
				removeData(sourceRecord);
			}
		});
	}

	protected abstract void moveChildren(ListGridRecord sourceRecord, ListGridRecord targetRecord, DSCallback callback);

	public Canvas wrapWithActionButtons() {
		return wrapWithActionButtons(getDataSource().getID());
	}

	public Canvas wrapWithActionButtons(String newButtonName) {
		VLayout layout = new VLayout(5);
		layout.setPadding(5);

		HLayout hLayout = new HLayout(10);
		hLayout.setAlign(Alignment.CENTER);

		IButton addButton = new IButton("Add new " + newButtonName);
		addButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				ListGridRecord newRecord = new ListGridRecord();
				addData(newRecord);
				selectRecord(newRecord);
			}
		});
		hLayout.addMember(addButton);

		if (getCanExpandRecords()) {
			IButton collapseButton = new IButton("Collapse all");
			collapseButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					for (ListGridRecord record : getRecords())
						expandRecord(record);
					// getGroupTree().closeAll();
				}
			});
			hLayout.addMember(collapseButton);
		}

		final IButton groupButton = new IButton("Group similar");
		groupButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				ListGridField[] fields = getFields();
				fields[fields.length - 1].setGroupValueFunction(new GroupValueFunction() {
					public Object getGroupValue(Object value, ListGridRecord record, ListGridField field,
							String fieldName, ListGrid grid) {
						return record.getAttributeAsInt("_simgroup");
					}
				});
				if (groupButton.isSelected()) {
					simGroupRecords();
					setGroupByField(fields[fields.length - 1].getName());
				} else
					ungroup();
			}
		});
		groupButton.setActionType(SelectionType.CHECKBOX);
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
			if (attr1[fieldIndex] != null && !attr1[fieldIndex].isEmpty() && attr2[fieldIndex] != null
					&& !attr2[fieldIndex].isEmpty()) {
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
	private Record stationRecord;

	/**
	 * Initializes ProductGrid.
	 */
	public ProductGrid() {
		super(ProductDS.getInstance());
		setCanExpandRecords(true);
		setCanDragRecordsOut(false);

		for (ListGridField field : getFields()) {
			field.setOptionDataSource(ProductDS.getInstance());
		}
	}

	public void updateStation(Record stationRecord) {
		this.stationRecord = stationRecord;
		fetchRelatedData(stationRecord, StationDS.getInstance());
	}

	@Override
	protected void moveChildren(final ListGridRecord sourceRecord, final ListGridRecord targetRecord,
			final DSCallback callback) {
		LotDS.getInstance().fetchData(new Criteria("product", sourceRecord.getAttribute("id")), new DSCallback() {
			@Override
			public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest) {
				for (Record record : dsResponse.getData()) {
					record.setAttribute("product", targetRecord.getAttribute("id"));
					LotDS.getInstance().updateData(record);
				}
				LotDS.getInstance().updateCaches(dsResponse);
				callback.execute(dsResponse, data, dsRequest);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.smartgwt.client.widgets.grid.ListGrid#getExpansionComponent(com.smartgwt
	 * .client.widgets.grid.ListGridRecord)
	 */
	@Override
	protected Canvas getExpansionComponent(ListGridRecord record) {
		return new LotGrid(stationRecord, record).wrapWithActionButtons();
	}
}

class LotGrid extends EditableGrid {
	private Record stationRecord;

	/**
	 * Initializes LotGrid.
	 */
	public LotGrid(Record stationRecord, Record productRecord) {
		super(LotDS.getInstance());
		this.stationRecord = stationRecord;
		setCanExpandRecords(true);
		fetchRelatedData(productRecord, ProductDS.getInstance());
	}

	@Override
	protected void moveChildren(final ListGridRecord sourceRecord, final ListGridRecord targetRecord,
			final DSCallback callback) {
		final String targetId = targetRecord.getAttribute("id");
		DeliveryDS.getInstance().fetchData(new Criteria("lot", sourceRecord.getAttribute("id")), new DSCallback() {
			@Override
			public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest) {
				for (Record record : dsResponse.getData()) {
					record.setAttribute("lot", targetId);
					DeliveryDS.getInstance().updateData(record);
				}
				DeliveryDS.getInstance().updateCaches(dsResponse);

				FoodRecipeDS.getInstance().fetchData(new Criteria("lot", sourceRecord.getAttribute("id")),
						new DSCallback() {
							@Override
							public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest) {
								for (Record record : dsResponse.getData()) {
									record.setAttribute("lot", targetId);
									FoodRecipeDS.getInstance().updateData(record);
								}

								FoodRecipeDS.getInstance().updateCaches(dsResponse);

								callback.execute(dsResponse, data, dsRequest);
							}
						});
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.smartgwt.client.widgets.grid.ListGrid#getExpansionComponent(com.smartgwt
	 * .client.widgets.grid.ListGridRecord)
	 */
	@Override
	protected Canvas getExpansionComponent(ListGridRecord record) {
		Layout lotCanvas = new VLayout();
		lotCanvas.setHeight("*");
		lotCanvas.setWidth100();
		Label recipeLabel = new Label("Recipe");
		recipeLabel.setHeight(30);
		lotCanvas.addMember(recipeLabel);
		lotCanvas.addMember(new RecipeGrid(stationRecord, record).wrapWithActionButtons());
		Label deliveriesLabel = new Label("Deliveries");
		deliveriesLabel.setHeight(30);
		lotCanvas.addMember(deliveriesLabel);
		lotCanvas.addMember(new DeliveryGrid(record).wrapWithActionButtons());
		return lotCanvas;
	}
}

@DefaultLocale("en_US")
interface RecipeDeliveryFormat extends Messages {
	@DefaultMessage("{1}/{2} delivered at {3}/{4}/{5} from {0}")
	String getDisplayValue(Object stationName, Object originalProduct, Object originalLot, Object deliveryDay,
			Object deliveryMonth, Object deliveryYear);
}

class RecipeGrid extends EditableGrid {
	private Record stationRecord;

	/**
	 * Initializes DeliveryGrid.
	 * 
	 * @param stationRecord
	 */
	public RecipeGrid(Record stationRecord, Record lotRecord) {
		super(FoodRecipeDS.getInstance());
		this.stationRecord = stationRecord;
		Criteria criteria = new Criteria("lot", lotRecord.getAttribute("id"));
		criteria.addCriteria(
				"_include",
				"deliveringStation=lot.product.station.name,originalProduct=lot.product.denomination,originalLot=lot.lotNumber,deliveryDateDay=ingredient.deliveryDateDay,deliveryDateMonth=ingredient.deliveryDateMonth,deliveryDateYear=ingredient.deliveryDateYear");
		fetchData(criteria);
		setCanDrag(false);

		ListGridField[] standardFields = getFields();
		List<ListGridField> allFields = new ArrayList<ListGridField>();
		for (ListGridField standardField : standardFields) {
			standardField.setWidth(50);
			allFields.add(standardField);
		}
		ListGridField deliveryField = new ListGridField("ingredient");
		allFields.add(deliveryField);
		setFields(allFields.toArray(new ListGridField[0]));

		final RecipeDeliveryFormat format = GWT.create(RecipeDeliveryFormat.class);
		final Object[] defaultValues = { "Unnamed station", "Unnamed product", "Unnamed lot", "?", "?", "?" };
		deliveryField.setCellFormatter(new CellFormatter() {
			@Override
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				Object[] values = { record.getAttribute("deliveringStation"), record.getAttribute("originalProduct"),
						record.getAttribute("originalLot"), record.getAttributeAsInt("deliveryDateDay"),
						record.getAttributeAsInt("deliveryDateMonth"), record.getAttributeAsInt("deliveryDateYear") };
				for (int index = 0; index < defaultValues.length; index++)
					if (values[index] == null) values[index] = defaultValues[index];
				return format.getDisplayValue(values[0], values[1], values[2], values[3], values[4], values[5]);
			}
		});

		final ComboBoxItem deliverySelect = new ComboBoxItem();
		Criteria suggestCriteria = new Criteria("recipient", stationRecord.getAttribute("id"));
		suggestCriteria
				.addCriteria("_include",
						"deliveringStation=lot.product.station.name,originalProduct=lot.product.denomination,originalLot=lot.lotNumber");
		// DeliveryDS.getInstance().fetchData(criteria, new DSCallback() {
		// @Override
		// public void execute(DSResponse dsResponse, Object data, DSRequest
		// dsRequest) {
		// LotDS.getInstance().fetchRecord(defaultValues, null);
		// deliverySelect.setValueMap(null);
		// }
		// });
		deliverySelect.setAddUnknownValues(false);
		deliverySelect.setOptionDataSource(DeliveryDS.getInstance());
		deliverySelect.setPickListCriteria(suggestCriteria);
		deliverySelect.setPickListWidth(500);
		deliverySelect.setValueField("id");
		deliverySelect.setDisplayField("originalProduct");
		deliverySelect.setValueFormatter(new FormItemValueFormatter() {  
	            public String formatValue(Object value, Record record, DynamicForm form, FormItem item) {  
					Object[] values = { record.getAttribute("deliveringStation"), record.getAttribute("originalProduct"),
							record.getAttribute("originalLot"), record.getAttributeAsInt("deliveryDateDay"),
							record.getAttributeAsInt("deliveryDateMonth"), record.getAttributeAsInt("deliveryDateYear") };
					for (int index = 0; index < defaultValues.length; index++)
						if (values[index] == null) values[index] = defaultValues[index];
					return format.getDisplayValue(values[0], values[1], values[2], values[3], values[4], values[5]);
	            }  
	        });  
		//
		ListGrid pickListProperties = new ListGrid();
		pickListProperties.setShowFilterEditor(true);
		deliverySelect.setPickListProperties(pickListProperties);
		//
		String[] fieldNames = { "deliveringStation", "originalProduct", "originalLot", "deliveryDateDay", "deliveryDateMonth", "deliveryDateYear" };
		ListGridField[] fields = new ListGridField[fieldNames.length];
		for (int index = 0; index < fieldNames.length; index++)
			fields[index] = new ListGridField(fieldNames[index]);
		deliverySelect.setPickListFields(fields);

		deliveryField.setEditorProperties(deliverySelect);
		// setAutoFetchDisplayMap(true);
	}

	@Override
	public Canvas wrapWithActionButtons() {
		return super.wrapWithActionButtons("ingredient");
	}

	@Override
	protected void moveChildren(ListGridRecord sourceRecord, ListGridRecord targetRecord, DSCallback callback) {
	}
}

class DeliveryGrid extends EditableGrid {
	/**
	 * Initializes DeliveryGrid.
	 */
	public DeliveryGrid(Record lotRecord) {
		super(DeliveryDS.getInstance());
		fetchRelatedData(lotRecord, LotDS.getInstance());

		final ComboBoxItem recipientSelect = new ComboBoxItem();
		recipientSelect.setAddUnknownValues(false);
		recipientSelect.setOptionDataSource(StationDS.getInstance());
		recipientSelect.setPickListWidth(500);
		recipientSelect.setValueField("id");
		recipientSelect.setDisplayField("name");

		LinkedHashMap<String, String> hashMap = new LinkedHashMap<String, String>();
		hashMap.put("", "New station");
		recipientSelect.setSpecialValues(hashMap);
		recipientSelect.setSeparateSpecialValues(true);
		recipientSelect.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				// new station clicked
				if (event.getValue().toString().isEmpty()) {
					// request (prefilled) new station record from backend
					StationDS.getInstance().addData(new Record(), new DSCallback() {
						@Override
						public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest) {
							// got them, now display the popup
							final StationPopup stationPopup = new StationPopup();
							stationPopup.updateStation(dsResponse.getData()[0]);
							stationPopup.setShowModalMask(true);
							stationPopup.setTitle("New station");
							stationPopup.show();

							stationPopup.addVisibilityChangedHandler(new VisibilityChangedHandler() {
								@Override
								public void onVisibilityChanged(VisibilityChangedEvent event) {
									Record stationRecord = stationPopup.getStationRecord();
									String id = stationRecord.getAttribute("id");
									logger.severe("new station id " + id);
									if (id != null && !id.isEmpty()) {
										ListGridRecord selectedRecord = getSelectedRecord();
										selectedRecord.setAttribute("recipient", id);
										DeliveryDS.getInstance().updateData(selectedRecord);
									}
									DeliveryDS.getInstance().invalidateCache();
								}
							});
						}
					});
					event.cancel();
				}
			}
		});

		ListGrid pickListProperties = new ListGrid();
		pickListProperties.setShowFilterEditor(true);
		recipientSelect.setPickListProperties(pickListProperties);

		String[] fieldNames = { "name", "vatNumber", "street", "houseNumber", "zipCode", "city" };
		ListGridField[] fields = new ListGridField[fieldNames.length];
		for (int index = 0; index < fieldNames.length; index++)
			fields[index] = new ListGridField(fieldNames[index]);
		fields[0].setWidth(100);
		fields[2].setWidth(100);
		recipientSelect.setPickListFields(fields);

		ListGridField recipientField = getField("recipient");
		recipientField.setDisplayField("recipientName");
		recipientField.setEditorProperties(recipientSelect);
		recipientField.setWidth(100);
		setAutoFetchDisplayMap(true);
	}

	@Override
	protected void moveChildren(ListGridRecord sourceRecord, ListGridRecord targetRecord, DSCallback callback) {
		callback.execute(null, null, null);
	}
}
