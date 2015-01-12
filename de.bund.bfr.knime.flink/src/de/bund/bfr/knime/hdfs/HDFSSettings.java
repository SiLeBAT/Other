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
package de.bund.bfr.knime.hdfs;

import java.io.IOException;
import java.io.Serializable;

import org.apache.hadoop.conf.Configuration;

/**
 * Represents a connection to the HDFS. Currently, the connection is only virtual: With every file operator, we send a
 * completely independent request. However, this special port allows users to keep their HDFS
 * configuration at one place.
 */
public class HDFSSettings implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1508497214849942766L;

	private transient Configuration configuration = new Configuration();

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
		HDFSSettings other = (HDFSSettings) obj;
		return this.configuration.getValByRegex(".*").equals(other.configuration.getValByRegex(".*"));
	}

	private void writeObject(java.io.ObjectOutputStream stream) throws IOException {
		stream.defaultWriteObject();
		this.configuration.write(stream);
	}

	private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		this.configuration = new Configuration();
		this.configuration.readFields(stream);
	}

	/**
	 * Returns the configuration.
	 * 
	 * @return the configuration
	 */
	public Configuration getConfiguration() {
		return this.configuration;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.configuration.getValByRegex(".*").hashCode();
		return result;
	}

	/**
	 * Sets the configuration to the specified value.
	 * 
	 * @param configuration
	 *        the configuration to set
	 */
	public void setConfiguration(Configuration configuration) {
		if (configuration == null)
			throw new NullPointerException("configuration must not be null");

		this.configuration = configuration;
	}
}
