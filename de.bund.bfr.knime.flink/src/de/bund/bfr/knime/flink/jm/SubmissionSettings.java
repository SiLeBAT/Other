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
package de.bund.bfr.knime.flink.jm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.config.Config;

import de.bund.bfr.knime.flink.FlinkProgramWithUsage;
import de.bund.bfr.knime.flink.Parameter;
import de.bund.bfr.knime.flink.Parameter.Type;

/**
 * 
 */
public class SubmissionSettings {
	private Map<Parameter, String> parameterValues = new HashMap<>();

	final static String CFGKEY_PARAMETER = "Parameters";

	private FlinkProgramWithUsage flinkProgramWithUsage;

	/**
	 * @param flinkProgramObjectSpec
	 */
	public void setProgram(FlinkProgramWithUsage flinkProgramWithUsage) {
		if (flinkProgramWithUsage == null)
			return;

		// something changed, verify parameters
		if (!flinkProgramWithUsage.equals(this.flinkProgramWithUsage)) {
			Map<Parameter, String> newParameterValues = new HashMap<>();
			// check if all parameter of the program are still valid
			for (Parameter newParameter : flinkProgramWithUsage.getParameters()) {
				Parameter oldParameter = null;
				if (this.flinkProgramWithUsage != null)
					for (Parameter parameter : this.flinkProgramWithUsage.getParameters())
						if (newParameter.getName().equals(parameter.getName())) {
							oldParameter = parameter;
							break;
						}

				String value = this.parameterValues.get(oldParameter);
				newParameterValues.put(newParameter, value);
			}
			this.parameterValues = newParameterValues;
			this.flinkProgramWithUsage = flinkProgramWithUsage;
		}
	}

	/**
	 * @return
	 */
	public String[] getParameterValues() {
		List<Parameter> parameters = this.flinkProgramWithUsage.getParameters();
		String[] values = new String[parameters.size()];
		for (int index = 0; index < parameters.size(); index++)
			values[index] = this.parameterValues.get(parameters.get(index));
		return values;
	}

	/**
	 * Returns the flinkProgramWithUsage.
	 * 
	 * @return the flinkProgramWithUsage
	 */
	public FlinkProgramWithUsage getFlinkProgramWithUsage() {
		return this.flinkProgramWithUsage;
	}

	/**
	 * @param settings
	 */
	public void loadSettingsFrom(NodeSettingsRO settings) throws InvalidSettingsException {
		Map<Parameter, String> parameterValues = new LinkedHashMap<>();
		if (settings.containsKey(CFGKEY_PARAMETER)) {
			Config subSettings = settings.getConfig(CFGKEY_PARAMETER);
			if (this.flinkProgramWithUsage == null) {
				// create ad-hoc program for loading the settings
				List<Parameter> parameters = new ArrayList<>();
				for (String parameterName : subSettings)
					parameters.add(new Parameter(parameterName, Type.STRING, false));
				this.flinkProgramWithUsage = new FlinkProgramWithUsage();
				this.flinkProgramWithUsage.setParameters(parameters);
			}
			for (Parameter parameter : this.flinkProgramWithUsage.getParameters()) {
				String value = subSettings.getString(parameter.getName(), "");
				parameterValues.put(parameter, value);
			}
		}
		this.parameterValues = parameterValues;
	}

	/**
	 * @param settings
	 */
	public void saveSettingsTo(NodeSettingsWO settings) throws InvalidSettingsException {
		if (this.flinkProgramWithUsage == null)
			throw new InvalidSettingsException("Cannot save settings, when program is not connected.");
		Config subSettings = settings.addConfig(CFGKEY_PARAMETER);
		for (Parameter parameter : this.flinkProgramWithUsage.getParameters()) {
			String value = this.parameterValues.get(parameter);
			subSettings.addString(parameter.getName(), value);
		}
	}

	/**
	 * @param settings
	 */
	public void validateSettings() throws InvalidSettingsException {
		for (Parameter parameter : this.flinkProgramWithUsage.getParameters()) {
			String value = this.parameterValues.get(parameter);
			if (value == null)
				throw new InvalidSettingsException(String.format("Parameter %s must be set", parameter.getName()));
			if (value != "<variable>")
				try {
					parameter.getType().fromString(value);
				} catch (Exception e) {
					throw new InvalidSettingsException(e.getMessage(), e);
				}
		}
	}

	/**
	 * @param name
	 * @return
	 */
	public String getParameterValue(Parameter parameter) {
		String parameterValue = this.parameterValues.get(parameter);
		return parameterValue == null ? "" : parameterValue;
	}

	public void setParameterValue(Parameter parameter, String value) {
		this.parameterValues.put(parameter, value);
	}
}
