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
import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class FlinkProgramWithUsage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6579443517360560694L;

	private String jarPath;

	private List<Parameter> parameters = new ArrayList<>();

	/**
	 * Returns the jarPath.
	 * 
	 * @return the jarPath
	 */
	public String getJarPath() {
		return this.jarPath;
	}

	/**
	 * Returns the arguments.
	 * 
	 * @return the arguments
	 */
	public List<Parameter> getParameters() {
		return this.parameters;
	}

	/**
	 * Sets the jarPath to the specified value.
	 * 
	 * @param jarPath
	 *        the jarPath to set
	 */
	public void setJarPath(String jarPath) {
		if (jarPath == null)
			throw new NullPointerException("jarPath must not be null");

		this.jarPath = jarPath;
	}

	/**
	 * Sets the arguments to the specified value.
	 * 
	 * @param arguments
	 *        the arguments to set
	 */
	public void setParameters(List<Parameter> arguments) {
		if (arguments == null)
			throw new NullPointerException("arguments must not be null");

		this.parameters = arguments;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.jarPath + " " + this.parameters;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.jarPath == null) ? 0 : this.jarPath.hashCode());
		result = prime * result + this.parameters.hashCode();
		return result;
	}

	/* (non-Javadoc)
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
		FlinkProgramWithUsage other = (FlinkProgramWithUsage) obj;
		if (this.jarPath == null) {
			if (other.jarPath != null)
				return false;
		} else if (!this.jarPath.equals(other.jarPath))
			return false;
		return this.parameters.equals(other.parameters);
	}

}
