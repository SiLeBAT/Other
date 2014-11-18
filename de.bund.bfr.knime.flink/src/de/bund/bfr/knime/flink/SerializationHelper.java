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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

/**
 * 
 */
public class SerializationHelper {
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T readObject(InputStream inputStream) throws IOException {
		ObjectInputStream dais = new ObjectInputStream(inputStream);
		try {
			return (T) dais.readObject();
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Cannot find setting class(es), although we are in the same package", e);
		}
	}

	public static <T extends Serializable> T readObject(NodeSettingsRO settings, String key)
			throws InvalidSettingsException {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(settings.getByteArray(key));
			return readObject(bis);
		} catch (IOException e) {
			throw new InvalidSettingsException("Cannot read object", e);
		}
	}

	public static void writeObject(NodeSettingsWO settings, String key, Serializable serializable) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			writeObject(bos, serializable);
			bos.close();
			settings.addByteArray(key, bos.toByteArray());
		} catch (IOException e) {
			throw new IllegalStateException("Cannot serialize the object locally.", e);
		}
	}

	public static void writeObject(OutputStream out, Serializable serializable) throws IOException {
		ObjectOutputStream daos = new ObjectOutputStream(out);
		daos.writeObject(serializable);
		daos.flush();
	}
}
