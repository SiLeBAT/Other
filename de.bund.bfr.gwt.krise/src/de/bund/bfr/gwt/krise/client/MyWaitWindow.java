package de.bund.bfr.gwt.krise.client;

import com.smartgwt.client.widgets.Window;

public class MyWaitWindow extends Window {
	public MyWaitWindow() {
		this.setTitle("Please wait...");  
		this.setWidth(300);  
		this.setHeight(100);  
		this.setShowMinimizeButton(false);  
		this.setShowCloseButton(false);
		this.setIsModal(false);  
		this.setShowModalMask(false);  				
	}
}
