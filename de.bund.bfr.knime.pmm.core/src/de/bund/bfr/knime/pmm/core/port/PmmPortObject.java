/*******************************************************************************
 * Copyright (c) 2013 Federal Institute for Risk Assessment (BfR), Germany 
 * 
 * Developers and contributors are 
 * Christian Thoens (BfR)
 * Armin A. Weiser (BfR)
 * Matthias Filter (BfR)
 * Annemarie Kaesbohrer (BfR)
 * Bernd Appel (BfR)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package de.bund.bfr.knime.pmm.core.port;

import java.io.Serializable;
import java.util.List;

import javax.swing.JComponent;

import org.eclipse.emf.ecore.EObject;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;

import de.bund.bfr.knime.pmm.core.EmfUtilities;

public class PmmPortObject implements PortObject, Serializable {

	public static final PortType TYPE = new PortType(PmmPortObject.class);

	private static final long serialVersionUID = 1L;

	private String xml;

	private PmmPortObjectSpec spec;

	public PmmPortObject(EObject obj, String type) {
		this(EmfUtilities.toXml(obj), new PmmPortObjectSpec(type));
	}

	public PmmPortObject(List<? extends EObject> data, String type) {
		this(EmfUtilities.listToXml(data), new PmmPortObjectSpec(type));
	}

	public PmmPortObject(String xml, PmmPortObjectSpec spec) {
		this.xml = xml;
		this.spec = spec;
	}

	@Override
	public String getSummary() {
		return "Shapefile Port";
	}

	@Override
	public PortObjectSpec getSpec() {
		return spec;
	}

	@Override
	public JComponent[] getViews() {
		return new JComponent[] { new PmmPortObjectView(this) };
	}

	public String getXml() {
		return xml;
	}

	public <T> List<T> getData(List<T> defaultList) {
		return EmfUtilities.listFromXml(xml, defaultList);
	}

	public String getType() {
		return spec.getType();
	}

	public static PortObjectSerializer<PmmPortObject> getPortObjectSerializer() {
		return new PmmPortObjectSerializer();
	}

}
