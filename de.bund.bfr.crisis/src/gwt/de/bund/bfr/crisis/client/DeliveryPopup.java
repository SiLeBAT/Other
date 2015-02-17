package de.bund.bfr.crisis.client;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.RowEndEditAction;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.VLayout;

public class DeliveryPopup extends Window {
	private final ListGrid deliveryGrid = new ListGrid();

	public DeliveryPopup() {
		// init myself
		initMySelf();
		initGrid();
	}

	public void updateStations(int senderId, int recipientId) {
		Criteria criteria = new Criteria();
		criteria.addCriteria("recipient", recipientId);
		criteria.addCriteria("lot.product.station", senderId);
		deliveryGrid.fetchData(criteria);
	}

	private void initMySelf() {
		setWidth(500);
		setHeight(500);
		setMembersMargin(5);
		setMargin(5);
		setPadding(10);
		setOpacity(95);
		setCanDragResize(true);
		centerInPage();

		// form dialog
		this.setShowShadow(true);
		// this.setIsModal(true);
		this.setPadding(20);
		this.setWidth(700);
		this.setHeight(400);
		this.setShowMinimizeButton(false);
		this.setShowMaximizeButton(true);
		this.setShowCloseButton(true);
		this.setShowModalMask(true);
		this.centerInPage();

		this.addItem(deliveryGrid);
	}

	private void initGrid() {
		deliveryGrid.setHeight("*");
		deliveryGrid.setWidth100();
		deliveryGrid.setDrawAheadRatio(4);

		deliveryGrid.setCanEdit(true);
		deliveryGrid.setEditByCell(true);
		deliveryGrid.setModalEditing(true);
		deliveryGrid.setEditEvent(ListGridEditEvent.CLICK);
		deliveryGrid.setListEndEditAction(RowEndEditAction.NEXT);
		deliveryGrid.setAutoSaveEdits(true);
		deliveryGrid.setCanRemoveRecords(true);
		deliveryGrid.setWarnOnRemoval(true);
		deliveryGrid.setAlternateRecordStyles(true);
		deliveryGrid.setShowAllRecords(true);
		deliveryGrid.setBodyOverflow(Overflow.VISIBLE);
		deliveryGrid.setOverflow(Overflow.VISIBLE);
		deliveryGrid.setDataSource(DeliveryDS.getInstance());
		
		ListGridField[] fields = deliveryGrid.getFields();
		System.out.println(fields);
	}
}