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

import javax.swing.JComponent;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortObjectSpec.PortObjectSpecSerializer;
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

	private static final NodeLogger LOGGER = NodeLogger.getLogger(FlinkJobmanagerConnectionObject.class);

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

	public static PortObjectSpecSerializer<FlinkJobmanagerConnectionObjectSpec> getPortObjectSpecSerializer() {
		return new FlinkJobmanagerConnectionObjectSpecSerializer();
	}

}
