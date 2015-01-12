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
package de.bund.bfr.knime.hdfs.port;

import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;

import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortObjectZipInputStream;
import org.knime.core.node.port.PortObjectZipOutputStream;
import org.knime.core.node.port.PortType;

import de.bund.bfr.knime.hdfs.HDFSSettings;

/**
 * Represents a connection to the HDFS. Currently, the connection is only virtual: With every file operator, we send a
 * completely independent request. However, this special port allows users to keep their HDFS
 * configuration at one place through the {@link HDFSSettings}.
 */
public class HDFSConnectionObject implements PortObject {
	/** Type representing this port object. */
	public static final PortType TYPE = new PortType(HDFSConnectionObject.class);

	private static final NodeLogger LOGGER = NodeLogger.getLogger(HDFSConnectionObject.class);

	private HDFSSettings settings = new HDFSSettings();

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
		HDFSConnectionObject other = (HDFSConnectionObject) obj;
		return this.settings.equals(other.settings);
	}

	/**
	 * Returns the settings.
	 * 
	 * @return the settings
	 */
	public HDFSSettings getSettings() {
		return this.settings;
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.port.PortObject#getSpec()
	 */
	@Override
	public PortObjectSpec getSpec() {
		HDFSConnectionObjectSpec spec = new HDFSConnectionObjectSpec();
		spec.setSettings(this.settings);
		return spec;
	}
	
	/**
	 * Sets the settings to the specified value.
	 *
	 * @param settings the settings to set
	 */
	public void setSettings(HDFSSettings settings) {
		if (settings == null)
			throw new NullPointerException("settings must not be null");

		this.settings = settings;
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.port.PortObject#getSummary()
	 */
	@Override
	public String getSummary() {
		return String.format("Job manager connection @ %s", this.settings.getConfiguration().get("fs.default.name"));
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

	public static PortObjectSerializer<HDFSConnectionObject> getPortObjectSerializer() {
		return new HDFSConnectionObjectSerializer();
	}

	public static class HDFSConnectionObjectSerializer extends
			PortObjectSerializer<HDFSConnectionObject> {
		@Override
		public HDFSConnectionObject loadPortObject(PortObjectZipInputStream in, PortObjectSpec spec,
				ExecutionMonitor exec) throws IOException, CanceledExecutionException {
			HDFSConnectionObject object = new HDFSConnectionObject();
			object.setSettings(((HDFSConnectionObjectSpec) spec).getSettings());
			return object;
		}

		@Override
		public void savePortObject(HDFSConnectionObject portObject, PortObjectZipOutputStream out, ExecutionMonitor exec)
				throws IOException, CanceledExecutionException {
		}
	}
}
