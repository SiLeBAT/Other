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
package de.bund.bfr.knime.flink.program;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "FlinkProgramLoader" Node.
 * Loads an existing Flink program.
 * 
 * @author Arvid Heise
 */
public class FlinkProgramLoaderNodeFactory
		extends NodeFactory<FlinkProgramLoaderNodeModel> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeDialogPane createNodeDialogPane() {
		return new FlinkProgramLoaderNodeDialog();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FlinkProgramLoaderNodeModel createNodeModel() {
		return new FlinkProgramLoaderNodeModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeView<FlinkProgramLoaderNodeModel> createNodeView(final int viewIndex,
			final FlinkProgramLoaderNodeModel nodeModel) {
		return new FlinkProgramLoaderNodeView(nodeModel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNrNodeViews() {
		return 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasDialog() {
		return true;
	}

}
