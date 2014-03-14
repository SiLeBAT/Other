package de.bund.bfr.gwt.krise.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.AdvancedCriteria;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceDateField;
import com.smartgwt.client.data.fields.DataSourceEnumField;
import com.smartgwt.client.data.fields.DataSourceFloatField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.OperatorId;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import de.bund.bfr.gwt.krise.shared.MyField;
import de.bund.bfr.gwt.krise.shared.MyTracingData;

public class MyCallback implements AsyncCallback<MyTracingData> {

	private MyWaitWindow pleaseWaitWindow;
	private ListGrid listGrid;

	public MyCallback(ListGrid listGrid, MyWaitWindow pleaseWaitWindow) {
		this.listGrid = listGrid;
		this.pleaseWaitWindow = pleaseWaitWindow;
	}

	public void onFailure(Throwable caught) {
		pleaseWaitWindow.destroy();  
		com.google.gwt.user.client.Window.alert(caught.getMessage());
	}

	@Override
	public void onSuccess(MyTracingData result) {
		if (result != null) {
			//listGrid.setWrapCells(true);
			//listGrid.setFixedRecordHeights(false);  
			
			//listGrid.setTitle("MyTotalTitle");
			fillList(result.getCols(), result.getRows(), result.getItems());
		}
		pleaseWaitWindow.destroy();  
	}
	private void setInput4Grid(DataSource dataSource, ListGridRecord[] records) {
		if (dataSource.getFieldNames().length > 0) {
			dataSource.setCacheData(records);
			listGrid.setDataSource(dataSource);
			//listGrid.setData(records);  
			
			listGrid.setSortField(0);  
			listGrid.setSortDirection(SortDirection.ASCENDING);  
	        //listGrid.getField(1).setAutoFitWidthApproach(AutoFitWidthApproach.BOTH);
	        //listGrid.getField(1).setAutoFitWidth(true);
			
			listGrid.fetchData(new AdvancedCriteria("ID", OperatorId.GREATER_THAN, 0));
			//setStyle(listGrid.getFilterEditor(), 1);
			//listGrid.setAutoFitMaxWidth(1000);
		    //listGrid.setAutoFitMaxWidth(1000);
			//listGrid.setAutoWidth();
	        //listGrid.setCanAutoFitFields(false);

			listGrid.draw();  
			
	        //listGrid.setAutoFitFieldWidths(true);
	        //listGrid.setAutoFitWidthApproach(AutoFitWidthApproach.BOTH);
	        //listGrid.setAutoFitFieldsFillViewport(true);
            //listGrid.setAutoFitExpandField("Name");	        
		}
	}  
	private void fillList(LinkedHashSet<MyField> cols, List<List<String>> rows, LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Boolean>>> items) {
		if (cols != null && rows != null) {
			DataSource dataSource = new DataSource();	
			dataSource.setClientOnly(true);
		    DataSourceField[] dsf = new DataSourceField[cols.size()];
			int i=0;
			for (MyField column : cols) {
				String colname = column.getName();
				dsf[i] = getDSF(column);
				dsf[i].setPrimaryKey(column.isPrimary());
				dsf[i].setCanEdit(!column.isReadOnly());
				if (column.getValueMap() != null) dsf[i].setValueMap(column.getValueMap());
				else if (column.getEnumeration() != null) dsf[i].setValueMap(column.getEnumeration());
				if (column.getMaxLength() >= 0) dsf[i].setLength(column.getMaxLength());
				//dsf[i].setForeignKey(foreignKey);
				if (column.getType() == MyField.ITEM && items.containsKey(colname)) {
					for (LinkedHashMap<String, Boolean> lhb : items.get(colname).values()) {
						dsf[i].setValueMap(lhb.keySet().toArray(new String[]{}));
						dsf[i].setMultiple(true);
						break;
					}
				}
				dsf[i].setPrompt(colname);
				i++;
			}
		    dataSource.setFields(dsf);//itemID, itemName, nextShipment);		
			
			ListGridRecord[] records = new ListGridRecord[rows.size()];
			for (i=0;i<rows.size();i++) {
				List<String> lo = rows.get(i);
				ListGridRecord record = new ListGridRecord();
				int j=0;
				for (MyField column : cols) {
					String colname = column.getName();
					if (lo.get(j) != null) {
						if (column.getType() == MyField.ITEM) {
							List<String> it = new ArrayList<String>();
							if (items.containsKey(colname)) {
								for (String item : items.get(colname).get(lo.get(0)).keySet()) {
									if (items.get(colname).get(lo.get(0)).get(item)) it.add(item);
								}
							}
							if (it.size() > 0) record.setAttribute(colname, it.toArray(new String[]{}));
							else record.setAttribute(colname, new String[]{});
						}
						else record.setAttribute(colname, lo.get(j));
					}
					j++;
				}	
		  		records[i] = record;
			}
			setInput4Grid(dataSource, records);
		}
	}
	private DataSourceField getDSF(MyField column) {
		String colname = column.getName();
		int type = column.getType();
		String title = column.getTitle();
		if (column.getValueMap() != null || column.getEnumeration() != null) return new DataSourceEnumField(colname, title);
		else if (type == MyField.DATE) return new DataSourceDateField(colname, title);
		else if (type == MyField.FLOAT) return new DataSourceFloatField(colname, title);
		else if (type == MyField.TEXT) return new DataSourceTextField(colname, title);
		else if (type == MyField.INTEGER) return new DataSourceIntegerField(colname, title);
		else if (type == MyField.BOOLEAN) return new DataSourceBooleanField(colname, title);
		else if (type == MyField.ITEM) return new DataSourceEnumField(colname, title);
		else return new DataSourceTextField(colname, title);
	}
	/*
	private void setStyle(ListGrid lg, int tt) {
		if (lg != null) {
			lg.setBackgroundColor("ft-Header" + tt);
			lg.setBodyBackgroundColor("ft-Header" + tt);
			lg.setFieldHeaderBaseStyle("ID", "ft-Header" + tt);
			lg.setFieldHeaderTitleStyle("ID", "ft-Header" + tt);
		}		
	}
	*/
}
