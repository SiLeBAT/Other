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
package de.bund.bfr.knime.hdfs.file;

import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "HDFSDownload" Node.
 * Downloads an HDFS file from a remote HDFS namenode to the local filesystem.
 *
 * @author Arvid Heise
 */
public class HDFSDownloadNodeView extends NodeView<HDFSDownloadNodeModel> {

    /**
     * Creates a new view.
     * 
     * @param nodeModel The model (class: {@link HDFSDownloadNodeModel})
     */
    protected HDFSDownloadNodeView(final HDFSDownloadNodeModel nodeModel) {
        super(nodeModel);
        // TODO: generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void modelChanged() {
        // TODO: generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClose() {
        // TODO: generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onOpen() {
        // TODO: generated method stub
    }

}

