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

import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortObjectZipInputStream;
import org.knime.core.node.port.PortObjectZipOutputStream;
import org.knime.core.node.port.PortType;

import de.bund.bfr.knime.flink.FlinkProgramWithUsage;

/**
 * Represents a connection to the Flink job manager. Currently, the connection is only virtual: With every job
 * submission, we send a completely independent request. However, this special port allows users to keep their Flink
 * configuration at one place through the {@link de.bund.bfr.knime.flink.jm.FlinkJobManagerConnectionNodeModel}.
 */
public class FlinkProgramObject implements PortObject {
	/** Type representing this port object. */
	public static final PortType TYPE = new PortType(FlinkProgramObject.class);

	private FlinkProgramWithUsage program = new FlinkProgramWithUsage();

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
		FlinkProgramObject other = (FlinkProgramObject) obj;
		return this.program.equals(other.program);
	}

	/**
	 * Returns the program.
	 * 
	 * @return the program
	 */
	public FlinkProgramWithUsage getProgram() {
		return this.program;
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.port.PortObject#getSpec()
	 */
	@Override
	public PortObjectSpec getSpec() {
		FlinkProgramObjectSpec spec = new FlinkProgramObjectSpec();
		spec.setProgram(this.program);
		return spec;
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.port.PortObject#getSummary()
	 */
	@Override
	public String getSummary() {
		return String.format("Flink program @ %s", this.program);
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
		result = prime * result + this.program.hashCode();
		return result;
	}

	public static PortObjectSerializer<FlinkProgramObject> getPortObjectSerializer() {
		return new FlinkProgramObjectSerializer();
	}

	public static class FlinkProgramObjectSerializer extends PortObjectSerializer<FlinkProgramObject> {
		@Override
		public FlinkProgramObject loadPortObject(PortObjectZipInputStream in, PortObjectSpec spec, ExecutionMonitor exec)
				throws IOException, CanceledExecutionException {
			FlinkProgramObject object = new FlinkProgramObject();
			object.setProgram(((FlinkProgramObjectSpec) spec).getProgram());
			return object;
		}

		@Override
		public void savePortObject(FlinkProgramObject portObject, PortObjectZipOutputStream out, ExecutionMonitor exec)
				throws IOException, CanceledExecutionException {
		}
	}

	/**
	 * Sets the program to the specified value.
	 *
	 * @param program the program to set
	 */
	public void setProgram(FlinkProgramWithUsage program) {
		if (program == null)
			throw new NullPointerException("program must not be null");

		this.program = program;
	}
}
