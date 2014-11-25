/*******************************************************************************
 * Copyright (c) 2014 Federal Institute for Risk Assessment (BfR), Germany
 *
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this file. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package de.bund.bfr.knime.hdfs.port;

import javax.swing.JComponent;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortObjectSpec.PortObjectSpecSerializer;
import org.knime.core.node.port.PortType;

import de.bund.bfr.knime.hdfs.HDFSFile;

/**
 * Represents an HDFS file location.
 */
public class HDFSFileObject implements PortObject {
	/** Type representing this port object. */
	public static final PortType TYPE = new PortType(HDFSFileObject.class);

	private static final NodeLogger LOGGER = NodeLogger.getLogger(HDFSFileObject.class);

	private HDFSFile file = new HDFSFile();

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
		HDFSFileObject other = (HDFSFileObject) obj;
		return this.file.equals(other.file);
	}

	/**
	 * Returns the file.
	 * 
	 * @return the file
	 */
	public HDFSFile getFile() {
		return this.file;
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.port.PortObject#getSpec()
	 */
	@Override
	public PortObjectSpec getSpec() {
		HDFSFileObjectSpec spec = new HDFSFileObjectSpec();
		spec.setFile(this.file);
		return spec;
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.port.PortObject#getSummary()
	 */
	@Override
	public String getSummary() {
		return String.format("Flink file @ %s", this.file);
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
		result = prime * result + this.file.hashCode();
		return result;
	}

	public static PortObjectSpecSerializer<HDFSFileObjectSpec> getPortObjectSpecSerializer() {
		return new HDFSFileObjectSpecSerializer();
	}

}
