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

import java.util.HashSet;
import java.util.Set;

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
public class HDFSFilesObject implements PortObject {
	/** Type representing this port object. */
	public static final PortType TYPE = new PortType(HDFSFilesObject.class);

	private static final NodeLogger LOGGER = NodeLogger.getLogger(HDFSFilesObject.class);

	private Set<HDFSFile> files = new HashSet<>();

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
		HDFSFilesObject other = (HDFSFilesObject) obj;
		return this.files.equals(other.files);
	}

	/**
	 * Returns the files.
	 * 
	 * @return the files
	 */
	public Set<HDFSFile> getFiles() {
		return this.files;
	}
	
	/**
	 * Sets the files to the specified value.
	 *
	 * @param files the files to set
	 */
	public void setFiles(Set<HDFSFile> files) {
		if (files == null)
			throw new NullPointerException("files must not be null");

		this.files = files;
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.port.PortObject#getSpec()
	 */
	@Override
	public PortObjectSpec getSpec() {
		HDFSFilesObjectSpec spec = new HDFSFilesObjectSpec();
		spec.setFiles(this.files);
		return spec;
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.port.PortObject#getSummary()
	 */
	@Override
	public String getSummary() {
		return String.format("Flink file @ %s", this.files);
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
		result = prime * result + this.files.hashCode();
		return result;
	}

	public static PortObjectSpecSerializer<HDFSFilesObjectSpec> getPortObjectSpecSerializer() {
		return new HDFSFileObjectSpecSerializer();
	}

}
