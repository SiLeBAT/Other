package de.bund.bfr.knime.paroa.strat;

public class StratosphereConnection {
	
	private boolean connected;
	public StratosphereConnection() {
		setConnected(true);
	}
	public boolean isConnected() {
		return connected;
	}
	public void setConnected(boolean connected) {
		this.connected = connected;
	}
}
