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
package de.bund.bfr.knime.hdfs.port;

import java.awt.BorderLayout;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import com.google.common.collect.Iterables;

import de.bund.bfr.knime.hdfs.HDFSFile;

final class HDFSFilesObjectView extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7638638767267140428L;

	HDFSFilesObjectView(final Set<HDFSFile> files) {
		super(new BorderLayout());
		super.setName("HDFS files");
		StringBuilder buf = new StringBuilder("<html><body>");
		buf.append("<h2>HDFS files</h2>");
		buf.append("<br/>");
		if (!files.isEmpty()) {
			buf.append("<strong>HDFS:</strong><br/>");
			buf.append("<tt>" +
				Iterables.getFirst(files, null).getHdfsSettings().getConfiguration().get("fs.default.name") + "</tt><br/>");
			buf.append("<strong>Files:</strong><br/>");
			for (HDFSFile file : files)
				buf.append("<tt>" + file.getLocation() + "</tt>");
		}
		buf.append("<br/>");
		buf.append("</body></html>");
		JTextPane f = new JTextPane();
		f.setContentType("text/html"); 
		f.setEditable(false); 
		f.setBackground(null); 
		f.setBorder(null);
		f.setText(buf.toString());
		final JScrollPane jsp = new JScrollPane(f);
		super.add(jsp, BorderLayout.CENTER);
	}
}