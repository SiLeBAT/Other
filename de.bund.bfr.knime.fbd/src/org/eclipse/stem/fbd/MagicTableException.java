package org.eclipse.stem.fbd;

/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

/**
 * General Exception class for {@link MagicTable} errors.
 */
public class MagicTableException extends Exception {

	private static final long serialVersionUID = -7173883923664211501L;

	/**
	 * Constructor with error message.
	 * 
	 * @param string
	 *            error message
	 */
	public MagicTableException(final String string) {
		super(string);
	}
}