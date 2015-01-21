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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;

import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortObjectSpecZipInputStream;
import org.knime.core.node.port.PortObjectSpecZipOutputStream;

import de.bund.bfr.knime.flink.SerializationHelper;
import de.bund.bfr.knime.hdfs.HDFSFile;

/**
 * Represents a connection to the Flink job manager. Currently, the connection is only virtual: With every job
 * submission, we send a completely independent request. However, this special port allows users to keep their Flink
 * configuration at one place through the {@link de.bund.bfr.knime.flink.jm.FlinkJobManagerConnectionNodeModel}.
 */
public class HDFSFilesObjectSpec implements PortObjectSpec {
	private Set<HDFSFile> files = new HashSet<>();

	/**
	 * Returns the files.
	 * 
	 * @return the files
	 */
	public Set<HDFSFile> getFiles() {
		return this.files;
	}

	/*
	 * (non-Javadoc)
	 * @see org.knime.core.node.port.PortObjectSpec#getViews()
	 */
	@Override
	public JComponent[] getViews() {
		return new JComponent[] { new HDFSFilesObjectView(this.files) };
	}

	/**
	 * Sets the files to the specified value.
	 * 
	 * @param files
	 *        the files to set
	 */
	public void setFiles(Set<HDFSFile> files) {
		if (files == null)
			throw new NullPointerException("files must not be null");

		this.files = files;
	}

	public static PortObjectSpecSerializer<HDFSFilesObjectSpec> getPortObjectSpecSerializer() {
		return new HDFSFileObjectSpecSerializer();
	}
	
	public static class HDFSFileObjectSpecSerializer extends PortObjectSpecSerializer<HDFSFilesObjectSpec> {
		@Override
		public HDFSFilesObjectSpec loadPortObjectSpec(PortObjectSpecZipInputStream in) throws IOException {
			HDFSFilesObjectSpec spec = new HDFSFilesObjectSpec();
			spec.setFiles(SerializationHelper.<HashSet<HDFSFile>> readObject(in));
			return spec;
		}
		
		@Override
		public void savePortObjectSpec(HDFSFilesObjectSpec portObjectSpec, PortObjectSpecZipOutputStream out)
				throws IOException {
			SerializationHelper.writeObject(out, new HashSet<>(portObjectSpec.getFiles()));
		}
	}
}
