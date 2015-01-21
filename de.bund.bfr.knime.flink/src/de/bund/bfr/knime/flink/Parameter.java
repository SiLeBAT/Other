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
import java.text.Format;
import java.text.NumberFormat;

/**
 * 
 */
public class Parameter implements Serializable {
	public static enum Type {
		STRING(org.knime.core.node.workflow.FlowVariable.Type.STRING, null), 
		INTEGER(org.knime.core.node.workflow.FlowVariable.Type.INTEGER, NumberFormat.getIntegerInstance()) {
			@Override
			public Object fromString(String value) {
				return Integer.valueOf(value);
			}
		},
		DOUBLE(org.knime.core.node.workflow.FlowVariable.Type.DOUBLE, NumberFormat.getNumberInstance()) {
			@Override
			public Object fromString(String value) {
				return Double.valueOf(value);
			}
		};
		
		/**
		 * Initializes Type.
		 *
		 * @param format
		 */
		private Type(org.knime.core.node.workflow.FlowVariable.Type flowType, Format format) {
			this.flowType = flowType;
			this.format = format;
		}

		private final Format format;
		
		private final org.knime.core.node.workflow.FlowVariable.Type flowType;
		
		/**
		 * Returns the flowType.
		 * 
		 * @return the flowType
		 */
		public org.knime.core.node.workflow.FlowVariable.Type getFlowType() {
			return this.flowType;
		}
		
		public String toString(Object value) {
			return value.toString();
		}

		public Object fromString(String value) {
			return value;
		}

		/**
		 * @return
		 */
		public Format getFormat() {
			return this.format;
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7010758786608017809L;

	private final String defaultValue;

	private final String name;

	private final Type type;

	/**
	 * Initializes Field.
	 * 
	 * @param name
	 * @param type
	 */
	public Parameter(String name, Type type, String defaultValue) {
		super();
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
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
		if (this.getClass() != obj.getClass())
			return false;
		Parameter other = (Parameter) obj;
		if (this.defaultValue == null) {
			if (other.defaultValue != null)
				return false;
		} else if (!this.defaultValue.equals(other.defaultValue))
			return false;
		return this.name.equals(other.name) && this.type.equals(other.type);
	}

	/**
	 * Returns the defaultValue.
	 * 
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return this.defaultValue;
	}

	/**
	 * Returns the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the type.
	 * 
	 * @return the type
	 */
	public Type getType() {
		return this.type;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.defaultValue == null ? 0 : this.defaultValue.hashCode());
		result = prime * result + this.name.hashCode();
		result = prime * result + this.type.hashCode();
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.name + ": " + this.type + " = " + this.defaultValue;
	}

}
