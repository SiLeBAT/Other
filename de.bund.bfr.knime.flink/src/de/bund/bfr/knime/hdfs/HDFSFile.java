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

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;

/**
 * 
 */
public class HDFSFile implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8509681999308041826L;

	private URI location;

	private HDFSSettings hdfsSettings = new HDFSSettings();

	/**
	 * Initializes HDFSFile.
	 *
	 */
	public HDFSFile() {
		try {
			this.location = new File(".").toURI();
		} catch (Exception e) {
			throw new IllegalStateException("Java cannot transform local file to URL", e);
		} 
	}
	
	/**
	 * Returns the location.
	 * 
	 * @return the location
	 */
	public URI getLocation() {
		return this.location;
	}

	/**
	 * Sets the location to the specified value.
	 * 
	 * @param location
	 *        the location to set
	 */
	public void setLocation(URI location) {
		if (location == null)
			throw new NullPointerException("location must not be null");

		this.location = location;
	}

	/**
	 * Returns the hdfsSettings.
	 * 
	 * @return the hdfsSettings
	 */
	public HDFSSettings getHdfsSettings() {
		return this.hdfsSettings;
	}

	/**
	 * Sets the hdfsSettings to the specified value.
	 * 
	 * @param hdfsSettings
	 *        the hdfsSettings to set
	 */
	public void setHdfsSettings(HDFSSettings hdfsSettings) {
		if (hdfsSettings == null)
			throw new NullPointerException("hdfsSettings must not be null");

		this.hdfsSettings = hdfsSettings;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HDFSFile [location=" + this.location + ", hdfsSettings=" + this.hdfsSettings + "]";
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.hdfsSettings.hashCode();
		result = prime * result + this.location.hashCode();
		return result;
	}

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
		if (getClass() != obj.getClass())
			return false;
		HDFSFile other = (HDFSFile) obj;
		return this.hdfsSettings.equals(other.hdfsSettings) && this.location.equals(other.location);
	}

}
