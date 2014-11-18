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
package de.bund.bfr.knime.flink;

import java.io.Serializable;
import java.net.InetSocketAddress;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.GlobalConfiguration;

/**
 * 
 */
public class FlinkJobManagerSettings implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1508497214849942766L;

	private InetSocketAddress address;

	private Configuration configuration = GlobalConfiguration.getConfiguration();

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
		FlinkJobManagerSettings other = (FlinkJobManagerSettings) obj;
		return this.address.equals(other.address) && this.configuration.equals(other.configuration);
	}

	/**
	 * Returns the address.
	 * 
	 * @return the address
	 */
	public InetSocketAddress getAddress() {
		return this.address;
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
		result = prime * result + this.address.hashCode();
		result = prime * result + this.configuration.hashCode();
		return result;
	}

	/**
	 * Sets the address to the specified value.
	 * 
	 * @param address
	 *        the address to set
	 */
	public void setAddress(InetSocketAddress address) {
		if (address == null)
			throw new NullPointerException("address must not be null");

		this.address = address;
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
