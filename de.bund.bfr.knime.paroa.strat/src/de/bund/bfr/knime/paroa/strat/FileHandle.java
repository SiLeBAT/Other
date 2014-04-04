package de.bund.bfr.knime.paroa.strat;

public class FileHandle {

	private String mPath;

	public FileHandle(String inData) {
		this.mPath = inData;
	}
	
	public String getPath() {
		return mPath;
	}
	public void setPath(String mPath) {
		this.mPath = mPath;
	}
}
