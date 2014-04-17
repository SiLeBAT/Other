package de.bund.bfr.knime.paroa.strat;

import java.io.IOException;
import java.util.ArrayList;

public class StratosphereConnection {
	
	private String m_path;
	private boolean connected;
	public StratosphereConnection(String stratospherePath) {
		setPath(stratospherePath);
		setConnected(true);
	}
	public boolean isConnected() {
		return connected;
	}
	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	
	public void runParoa(	FileHandle paroa_jar, FileHandle paroa_input_outbreaks, FileHandle paroa_input_sales,
			FileHandle paroa_output) {
    	
    	final String process_location = getPath()  + "/bin/stratosphere";
    	final String process_cmd = "run";
    	final String arg_jar= "-j " + paroa_jar.getPath();
    	final String arg_arg= "-a";
    	final String arg_input_outbreaks = "file:" + paroa_input_outbreaks.getPath();
    	final String arg_input_sales = "file:" + paroa_input_sales.getPath();
    	final String arg_output = "file:" + paroa_output.getPath();
    	final String arg_paral = "1";
    	final String arg_debug = "-w";
    	
    	ArrayList<String> arguments = new ArrayList<String>();
    	arguments.add(process_location);
    	arguments.add(process_cmd);
    	arguments.add(arg_jar);
    	arguments.add(arg_arg);
    	arguments.add(arg_input_outbreaks);
    	arguments.add(arg_input_sales);
    	arguments.add(arg_output);
    	arguments.add(arg_paral);
    	arguments.add(arg_debug);
    	
		ProcessBuilder process_b= new ProcessBuilder(arguments);
		process_b.inheritIO();		
		Process p;
		
		try {
			System.out.println(process_b.command());
			
			p = process_b.start();
			final int exit_status = p.waitFor();
			
			System.out.println(exit_status);
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}		
	}
	private String getPath() {
		return m_path;
	}
	private void setPath(String m_path) {
		this.m_path = m_path;
	}
}
