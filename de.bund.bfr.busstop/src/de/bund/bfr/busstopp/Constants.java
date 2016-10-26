package de.bund.bfr.busstopp;

public class Constants {
	public static final boolean IS_TEST = false;
	//public static final String SERVER_UPLOAD_LOCATION_FOLDER = "C:/Users/Armin/Desktop/busstop_folder/";
	//public static final String SERVER_UPLOAD_LOCATION_FOLDER = "C:/Users/weiser/Desktop/busstop_folder/";
	//public static final String SERVER_UPLOAD_LOCATION_FOLDER = "/Users/arminweiser/busstop_folder/";
	public static final String SERVER_UPLOAD_LOCATION_FOLDER = IS_TEST ? "/home/knime/busstop_folder_test/" : "/home/knime/busstop_folder/";
}
