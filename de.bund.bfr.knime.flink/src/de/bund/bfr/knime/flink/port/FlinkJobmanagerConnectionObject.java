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
package de.bund.bfr.knime.flink.port;

import java.io.IOException;

import javax.swing.JComponent;

import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.GlobalConfiguration;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortObjectZipInputStream;
import org.knime.core.node.port.PortObjectZipOutputStream;
import org.knime.core.node.port.PortType;

import de.bund.bfr.knime.flink.FlinkJobManagerSettings;

/**
 * Represents a connection to the Flink job manager. Currently, the connection is only virtual: With every job
 * submission, we send a completely independent request. However, this special port allows users to keep their Flink
 * configuration at one place through the {@link de.bund.bfr.knime.flink.jm.FlinkJobManagerConnectionNodeModel}.
 */
public class FlinkJobmanagerConnectionObject implements PortObject {
	/** Type representing this port object. */
	public static final PortType TYPE = new PortType(FlinkJobmanagerConnectionObject.class);

	private FlinkJobManagerSettings settings = new FlinkJobManagerSettings();

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		FlinkJobmanagerConnectionObject other = (FlinkJobmanagerConnectionObject) obj;
		return this.settings.equals(other.settings);
	}

	/**
	 * Returns the settings.
	 * 
	 * @return the settings
	 */
	public FlinkJobManagerSettings getSettings() {
		return this.settings;
	}

	/**
	 * Sets the settings to the specified value.
	 *
	 * @param settings the settings to set
	 */
	public void setSettings(FlinkJobManagerSettings settings) {
		if (settings == null)
			throw new NullPointerException("settings must not be null");

		this.settings = settings;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.port.PortObject#getSpec()
	 */
	@Override
	public PortObjectSpec getSpec() {
		FlinkJobmanagerConnectionObjectSpec spec = new FlinkJobmanagerConnectionObjectSpec();
		spec.setSettings(this.settings);
		return spec;
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.port.PortObject#getSummary()
	 */
	@Override
	public String getSummary() {
		return String.format("Job manager connection @ %s", this.settings.getAddress());
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.port.PortObject#getViews()
	 */
	@Override
	public JComponent[] getViews() {
		return this.getSpec().getViews();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.settings.hashCode();
		return result;
	}

	public static PortObjectSerializer<FlinkJobmanagerConnectionObject> getPortObjectSerializer() {
		return new FlinkJobmanagerConnectionObjectSpecSerializer();
	}

	public static class FlinkJobmanagerConnectionObjectSpecSerializer extends
			PortObjectSerializer<FlinkJobmanagerConnectionObject> {
		@Override
		public FlinkJobmanagerConnectionObject loadPortObject(PortObjectZipInputStream in, PortObjectSpec spec,
				ExecutionMonitor exec) throws IOException, CanceledExecutionException {
			FlinkJobmanagerConnectionObject object = new FlinkJobmanagerConnectionObject();
			object.setSettings(((FlinkJobmanagerConnectionObjectSpec) spec).getSettings());
			return object;
		}
		
		@Override
		public void savePortObject(FlinkJobmanagerConnectionObject portObject, PortObjectZipOutputStream out,
				ExecutionMonitor exec) throws IOException, CanceledExecutionException {
		}
	}
	
	static {
		Configuration configuration = new Configuration();
		configuration.setBoolean(ConfigConstants.FILESYSTEM_DEFAULT_OVERWRITE_KEY, true);
		configuration.setBoolean(ConfigConstants.FILESYSTEM_OUTPUT_ALWAYS_CREATE_DIRECTORY_KEY, true);
		GlobalConfiguration.includeConfiguration(configuration);
	}
}
