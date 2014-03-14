package de.bund.bfr.knime.pmm.core.ui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class StandardFileFilter extends FileFilter {

	private String fileExtension;
	private String description;

	public StandardFileFilter(String fileExtension, String description) {
		this.fileExtension = fileExtension;
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public boolean accept(File f) {
		return f.isDirectory()
				|| f.getName().toLowerCase().endsWith(fileExtension);
	}
}
