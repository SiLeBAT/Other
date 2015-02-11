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
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceFloatField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.widgets.form.fields.FormItem;

public class GrailsDataSourceBuilder {
	private String className;

	private List<String> fieldNames = new ArrayList<String>();

	private Map<String, DataSourceField> dataSourceFields = new HashMap<String, DataSourceField>();

	private Map<String, FormItem> formItems = new HashMap<String, FormItem>();

	public GrailsDataSourceBuilder(String className) {
		this.className = className;
	}

	public GrailsDataSourceBuilder addFields(List<String> fieldNames) {
		this.fieldNames.addAll(fieldNames);
		return this;
	}

	public GrailsDataSourceBuilder addFields(String... fieldNames) {
		return addFields(Arrays.asList(fieldNames));
	}

	public GrailsDataSourceBuilder addType(String fieldName, Class<?> type) {
		return addField(fieldName, getFieldForType(fieldName, type));
	}

	private DataSourceField getFieldForType(String fieldName, Class<?> type) {
		try {
			DataSourceField field = newDataSourceForType(type);
			field.setName(fieldName);
			field.setLength(50);
			return field;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private DataSourceField newDataSourceForType(Class<?> type) {
		if (type == String.class)
			return new DataSourceTextField();
		if (type == Integer.class)
			return new DataSourceIntegerField();
		if (type == Double.class)
			return new DataSourceFloatField();
		if (type == Boolean.class)
			return new DataSourceBooleanField();
		throw new IllegalArgumentException("Unknown type " + type);
	}

	public GrailsDataSourceBuilder addField(String fieldName, DataSourceField field) {
		this.dataSourceFields.put(fieldName, field);
		return this;
	}

	public GrailsDataSourceBuilder addFormItem(String fieldName, FormItem formItem) {
		this.formItems.put(fieldName, formItem);
		return this;
	}

	public RestDataSource build() {
		RestDataSource dataSource = new RestDataSource();
		dataSource.setID(className);
		dataSource.setClientOnly(false);
		dataSource.setDataFormat(DSDataFormat.JSON);

		DataSourceField idField = new DataSourceIntegerField("id");
		idField.setHidden(true);
		idField.setPrimaryKey(true);

		dataSource.addField(idField);
		for (String fieldName : fieldNames) {
			DataSourceField field = this.dataSourceFields.get(fieldName);
			if (field == null)
				field = getFieldForType(fieldName, String.class);
			FormItem editor = formItems.get(fieldName);
			if (editor != null)
				field.setEditorType(editor);
			dataSource.addField(field);
		}

		// setup operations
		// 1. fetch
		OperationBinding fetch = createBinding(DSOperationType.FETCH, "fetch", DSProtocol.POSTPARAMS);
		// 2. update
		OperationBinding update = createBinding(DSOperationType.UPDATE, "save", DSProtocol.POSTPARAMS);
		// 3. add
		OperationBinding add = createBinding(DSOperationType.ADD, "save", DSProtocol.POSTPARAMS);
		// 4. remove
		OperationBinding remove = createBinding(DSOperationType.REMOVE, "delete", DSProtocol.POSTPARAMS);
		dataSource.setOperationBindings(fetch, update, add, remove);
		
		return dataSource;
	}

	private OperationBinding createBinding(DSOperationType operationType, String actionName, DSProtocol dataProtocol) {
		String base = "de.bund.bfr.crisis";
		OperationBinding binding = new OperationBinding(operationType, "/" + base + "/" + className + "/" + actionName + ".json");
		binding.setDataProtocol(dataProtocol);
		return binding;
	}
}
