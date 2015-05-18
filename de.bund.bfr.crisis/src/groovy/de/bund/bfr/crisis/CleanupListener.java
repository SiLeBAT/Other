package de.bund.bfr.crisis;

import java.beans.Introspector;
import java.sql.DriverManager;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import java.sql.Driver;

/*
put into web.xml:
<listener>
  <listener-class>de.bund.bfr.crisis.CleanupListener</listener-class>
</listener>
  */

public class CleanupListener implements ServletContextListener {
	public void contextInitialized(ServletContextEvent event) {
	}

	public void contextDestroyed(ServletContextEvent event) {
		net.sf.ehcache.CacheManager manager = net.sf.ehcache.CacheManager.getInstance();
		if (manager != null) {
			manager.shutdown();
		}
		
		
		try {
			Introspector.flushCaches();
			for (Enumeration<Driver> e = DriverManager.getDrivers(); e.hasMoreElements();) {
				Driver driver = (Driver) e.nextElement();
				if (driver.getClass().getClassLoader() == getClass().getClassLoader()) {
					DriverManager.deregisterDriver(driver);
				}
			}
		} catch (Throwable e) {
			System.err.println("Failled to cleanup ClassLoader for webapp");
			e.printStackTrace();
		}
	}
}