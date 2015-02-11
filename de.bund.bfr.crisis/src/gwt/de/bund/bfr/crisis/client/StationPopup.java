package de.bund.bfr.crisis.client;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class StationPopup extends Window {
	private final DynamicForm form = new DynamicForm();

	private final IButton saveButton = new IButton();

	private final IButton resetButton = new IButton();
	
	private final ProductGrid productGrid = new ProductGrid();

	public StationPopup() {
		// init myself
		initMySelf();
		// form for editing
		initEditForm();
		hide();
	}

	private void initMySelf() {
		setWidth(850);
		setHeight(500);
		setMembersMargin(5);
		setMargin(5);
		setPadding(10);
	}

	private void initEditForm() {
		// the form
		form.setIsGroup(false);
		form.setDataSource(StationDS.getInstance());
		form.setCellPadding(5);
		form.setWidth("100%");
		form.setColWidths(100, 300);
		saveButton.setTitle("SAVE");
		saveButton.setTooltip("Save this station");
		saveButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent clickEvent) {
				form.saveData();
				StationPopup.this.hide();
			}
		});
		resetButton.setTitle("RESET");
		resetButton.setTooltip("Reset");
		resetButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				form.reset();
			}
		});
		HLayout buttons = new HLayout(10);
		buttons.setAlign(Alignment.CENTER);
		buttons.addMember(resetButton);
		buttons.addMember(saveButton);
		VLayout dialog = new VLayout(10);
		dialog.setPadding(10);
		dialog.addMember(form);
		dialog.addMember(buttons);
		ScrollPanel scrollPanel = new ScrollPanel(productGrid);
		scrollPanel.setWidth("800");
		scrollPanel.setHeight("600");
		dialog.addMember(scrollPanel);
		dialog.setWidth100();
		// form dialog
		this.setShowShadow(true);
		this.setShowTitle(false);
//		this.setIsModal(true);
		this.setPadding(20);
		this.setWidth(900);
		this.setHeight(600);
		this.setShowMinimizeButton(false);
		this.setShowCloseButton(true);
		this.setShowModalMask(true);
		this.centerInPage();
		HeaderControl closeControl = new HeaderControl(HeaderControl.CLOSE, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				StationPopup.this.hide();
			}
		});
		this.setHeaderControls(HeaderControls.HEADER_LABEL, closeControl);
		this.addItem(dialog);
	}

	public void show(String featureId, final int x, final int y) {
		StationDS.getInstance().fetchData(new Criteria("id", featureId), new DSCallback() {
			@Override
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				Record stationRecord = response.getData()[0];
				form.editRecord(stationRecord);
				productGrid.updateStation(stationRecord);
				centerInPage();
				show();
			}
		});

	}
}