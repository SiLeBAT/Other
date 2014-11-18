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

import java.io.File;
import java.io.IOException;

import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.client.program.Client;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.ProgramInvocationException;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.GlobalConfiguration;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelOptionalString;
import org.knime.core.node.defaultnodesettings.SettingsModelStringArray;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.port.flowvariable.FlowVariablePortObjectSpec;

import de.bund.bfr.knime.flink.FlinkJobManagerSettings;
import de.bund.bfr.knime.flink.port.FlinkJobmanagerConnectionObject;

/**
 * This is the model implementation of FlinkJobSubmission.
 * Submits a job to Flink.
 * 
 * @author Arvid Heise
 */
public class FlinkJobSubmissionNodeModel extends NodeModel {

	/**
	 * the settings key which is used to retrieve and store the settings (from
	 * the dialog or from a settings file) (package visibility to be usable from
	 * the dialog).
	 */
	static final String CFGKEY_DOP = "Degree of parallelism",
			CFGKEY_PARAMETER = "Parameters",
			CFGKEY_JAR = "Jar",
			CFGKEY_JOB_SUCCESS = "Job success",
			CFGKEY_JOB_STATUS = "Job status";

	/** initial default count value. */
	static final int DEFAULT_DOP = -1;

	// the logger instance
	private static final NodeLogger logger = NodeLogger
		.getLogger(FlinkJobSubmissionNodeModel.class);

	// example value: the models count variable filled from the dialog
	// and used in the models execution method. The default components of the
	// dialog work with "SettingsModels".
	private final SettingsModelIntegerBounded dop = createDOPModel();

	private final SettingsModelOptionalString jarPath = createPathModel();

	private final SettingsModelStringArray parameters = new SettingsModelStringArray(
		CFGKEY_PARAMETER, new String[0]);

	/**
	 * Constructor for the node model.
	 */
	protected FlinkJobSubmissionNodeModel() {
		super(new PortType[] { FlinkJobmanagerConnectionObject.TYPE, FlowVariablePortObject.TYPE_OPTIONAL },
			new PortType[] { FlowVariablePortObject.TYPE });
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.NodeModel#configure(org.knime.core.node.port.PortObjectSpec[])
	 */
	@Override
	protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs) throws InvalidSettingsException {
		return new PortObjectSpec[] { FlowVariablePortObjectSpec.INSTANCE };
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.NodeModel#execute(org.knime.core.node.port.PortObject[],
	 * org.knime.core.node.ExecutionContext)
	 */
	@Override
	protected PortObject[] execute(PortObject[] inObjects, ExecutionContext exec) throws Exception {
		FlinkJobmanagerConnectionObject connection = (FlinkJobmanagerConnectionObject) inObjects[0];
		FlinkJobManagerSettings settings = connection.getSettings();

		try {
			PackagedProgram packagedProgram =
				new PackagedProgram(new File(this.jarPath.getStringValue()), this.parameters.getStringArrayValue());

			Configuration configuration = GlobalConfiguration.getConfiguration();
			if (this.dop.getIntValue() != -1)
				configuration.setInteger(ConfigConstants.DEFAULT_PARALLELIZATION_DEGREE_KEY, this.dop.getIntValue());
			Client client = new Client(settings.getAddress(), configuration, packagedProgram.getUserCodeClassLoader());
			JobExecutionResult result = client.run(packagedProgram, this.dop.getIntValue(), true);
			this.pushFlowVariableInt(CFGKEY_JOB_SUCCESS, 1);
			this.pushFlowVariableString(CFGKEY_JOB_STATUS, String.format("Executed in %s ms", result.getNetRuntime()));
		} catch (ProgramInvocationException e) {
			this.pushFlowVariableInt(CFGKEY_JOB_SUCCESS, 0);
		}

		return new PortObject[] { FlowVariablePortObject.INSTANCE };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir, final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
		this.dop.loadSettingsFrom(settings);
		this.jarPath.loadSettingsFrom(settings);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir, final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		this.dop.saveSettingsTo(settings);
		this.jarPath.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
		this.dop.validateSettings(settings);
		this.jarPath.validateSettings(settings);
	}

	static SettingsModelIntegerBounded createDOPModel() {
		return new SettingsModelIntegerBounded(CFGKEY_DOP, DEFAULT_DOP, -1, Integer.MAX_VALUE);
	}

	static SettingsModelOptionalString createPathModel() {
		return new SettingsModelOptionalString(CFGKEY_JAR, "", false);
	}

}
