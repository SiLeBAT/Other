package de.bund.bfr.busstopp.dao;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import de.bund.bfr.busstopp.Constants;

public enum Dao {
	instance;

	private Map<String, Environment> environments = new LinkedHashMap<>();
	
	private Dao() {
		fillCP();
	}
	private void fillCP() {
		environments = new LinkedHashMap<>();
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
						if (pn.startsWith("out_")) {
							deleteDir(path);
						}
						else {
							long l = Long.parseLong(pn);
							ItemLoader item = new ItemLoader(l, path);
							String e = item.getXml().getIn().getEnvironment().trim();
							if (!environments.containsKey(e)) environments.put(e, new Environment(e));
							Environment en = environments.get(e);
							if (!item.isDeleted()) en.getContentProvider().put(l, item);	
							else en.getContentDelProvider().put(l, item);
						}
					}
				}
			}
		} catch (Exception e) {
			// if any error occurs
			e.printStackTrace();
		}		
	}
	public Map<Long, ItemLoader> getModel(String environment) {
		Environment e = environments.get(environment);
		return e == null ? null : e.getContentProvider();
	}
	public Map<Long, ItemLoader> getModelDel(String environment) {
		Environment e = environments.get(environment);
		return e == null ? null : e.getContentDelProvider();
	}
	public Map<String, Environment> getEnvironments() {
		return environments;
	}
	public void setEnvironments(Map<String, Environment> environments) {
		this.environments = environments;
	}

	public int clearBin(String environment, long id) {
		int result = 0;
		try {
			File path = new File(Constants.SERVER_UPLOAD_LOCATION_FOLDER + File.separator + id);
			if (path.isDirectory()) {
				ItemLoader item = new ItemLoader(id, path);
				if (environments.containsKey(environment) && item.getXml().getIn().getEnvironment().equals(environment)) {
					if (item.isDeleted()) {
						environments.get(environment).getContentDelProvider().remove(id);
						if (deleteDir(path)) result++;
					}
				}
			}
		} catch (Exception e) {
			// if any error occurs
			e.printStackTrace();
		}		
		return result;
	}
	public int clearBin(String environment) {
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
						long l = Long.parseLong(pn);
						ItemLoader item = new ItemLoader(l, path);
						if ((environment == null || environment.trim().isEmpty()) && item.getXml().getIn().getEnvironment().isEmpty() || environments.containsKey(environment) && item.getXml().getIn().getEnvironment().equals(environment)) {
							if (item.isDeleted()) { //  || item.getXml().getIn().getFilename().indexOf(":") >= 0
								environments.get(item.getXml().getIn().getEnvironment()).getContentDelProvider().remove(l);
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
	public void delete(String environment, Long id, ItemLoader item) {
		if (environments.containsKey(environment) && item.getXml().getIn().getEnvironment().equals(environment)) {
			environments.get(environment).getContentProvider().remove(id);
			environments.get(environment).getContentDelProvider().put(id, item);
		}
	}
	public int deleteAll(String environment) {
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
						long l = Long.parseLong(pn);
						ItemLoader item = new ItemLoader(l, path);
						String uen = item.getXml().getIn().getEnvironment().trim();
						if (uen.equals(environment)) {
							if (!item.isDeleted()) {
								item.delete();
								delete(uen, l, item);
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
