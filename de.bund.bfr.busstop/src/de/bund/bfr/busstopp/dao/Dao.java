package de.bund.bfr.busstopp.dao;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import de.bund.bfr.busstopp.Constants;

public enum Dao {
	instance;

	private Map<Long, ItemLoader> contentProvider = new LinkedHashMap<>();
	private Map<Long, ItemLoader> contentDelProvider = new LinkedHashMap<>();
	
	public static String outFolder = null;

	private Dao() {
		fillCP();
	}
	private void fillCP() {
		contentProvider = new LinkedHashMap<>();
		contentDelProvider = new LinkedHashMap<>();
		try {
			// create new file
			File f = new File(Constants.SERVER_UPLOAD_LOCATION_FOLDER);

			// returns pathnames for files and directory
			File[] paths = f.listFiles();
			if (paths != null && paths.length > 0) {
				Arrays.sort(paths, Collections.reverseOrder());

				// for each pathname in pathname array
				for (File path : paths) {				
					//System.out.println(path);
					if (path.isDirectory()) {
						String pn = path.getName();
						if (!pn.startsWith("out_")) {
							long l = Long.parseLong(pn);
							ItemLoader item = new ItemLoader(l, path);
							if (!item.isDeleted()) contentProvider.put(l, item);	
							else contentDelProvider.put(l, item);
						}
						else {
							if (outFolder == null) outFolder = path.getAbsolutePath();
						}
					}
				}
			}
		} catch (Exception e) {
			// if any error occurs
			e.printStackTrace();
		}		
	}
	public Map<Long, ItemLoader> getModel() {
		return contentProvider;
	}
	public Map<Long, ItemLoader> getModelDel() {
		return contentDelProvider;
	}

	public int clearBin(long id) {
		int result = 0;
		try {
			File path = new File(Constants.SERVER_UPLOAD_LOCATION_FOLDER + File.separator + id);
			if (path.isDirectory()) {
				ItemLoader item = new ItemLoader(id, path);
				if (item.isDeleted()) {
					contentDelProvider.remove(id);
					if (deleteDir(path)) result++;
				}
			}
		} catch (Exception e) {
			// if any error occurs
			e.printStackTrace();
		}		
		return result;
	}
	public int clearBin() {
		int result = 0;
		try {
			// create new file
			File f = new File(Constants.SERVER_UPLOAD_LOCATION_FOLDER);

			// returns pathnames for files and directory
			File[] paths = f.listFiles();
			if (paths != null && paths.length > 0) {
				// for each pathname in pathname array
				for (File path : paths) {				
					//System.out.println(path);
					if (path.isDirectory()) {
						String pn = path.getName();
						if (!pn.startsWith("out_")) {
							long l = Long.parseLong(pn);
							ItemLoader item = new ItemLoader(l, path);
							if (item.isDeleted()) { //  || item.getXml().getIn().getFilename().indexOf(":") >= 0
								contentDelProvider.remove(l);
								if (deleteDir(path)) result++;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// if any error occurs
			e.printStackTrace();
		}		
		return result;
	}
	public int deleteAll() {
		int result = 0;
		try {
			File f = new File(Constants.SERVER_UPLOAD_LOCATION_FOLDER);

			// returns pathnames for files and directory
			File[] paths = f.listFiles();
			if (paths != null && paths.length > 0) {
				// for each pathname in pathname array
				for (File path : paths) {				
//					System.out.println(path);
					if (path.isDirectory()) {
						String pn = path.getName();
						if (!pn.startsWith("out_")) {
							long l = Long.parseLong(pn);
							ItemLoader item = new ItemLoader(l, path);
							if (!item.isDeleted()) {
								item.delete();
								contentProvider.remove(l);
								contentDelProvider.put(l, item);
								result++;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// if any error occurs
			System.out.println(e.getMessage());
			e.printStackTrace();
		}		
		//if (result > 0) fillCP();
		return result;
	}
	private boolean deleteDir(File folder) {
	    File[] contents = folder.listFiles();
	    if (contents != null) {
	        for(int i = contents.length-1;i>=0;i--) {
	        	contents[i].delete();
	        }
	    }
	    return folder.delete();
	}		
}
