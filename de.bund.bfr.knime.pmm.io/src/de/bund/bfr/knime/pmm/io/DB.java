package de.bund.bfr.knime.pmm.io;

import java.io.File;
import java.util.Properties;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.teneo.PersistenceOptions;
import org.eclipse.emf.teneo.hibernate.HbDataStore;
import org.eclipse.emf.teneo.hibernate.HbHelper;
import org.hibernate.cfg.Environment;

import de.bund.bfr.knime.pmm.core.common.CommonPackage;
import de.bund.bfr.knime.pmm.core.data.DataPackage;
import de.bund.bfr.knime.pmm.core.models.ModelsPackage;

public class DB {

	private static HbDataStore dataStore = null;

	public static HbDataStore getDataStore() {
		if (dataStore == null) {
			Properties props = new Properties();

			props.setProperty(Environment.DRIVER, "org.hsqldb.jdbcDriver");
			props.setProperty(Environment.USER, "sa");
			props.setProperty(Environment.URL, "jdbc:hsqldb:file:" + getPath());
			props.setProperty(Environment.PASS, "");
			props.setProperty(Environment.DIALECT,
					org.hibernate.dialect.HSQLDialect.class.getName());
			props.setProperty(
					PersistenceOptions.CASCADE_POLICY_ON_NON_CONTAINMENT,
					"REFRESH,PERSIST,MERGE");
			props.setProperty(PersistenceOptions.EMAP_AS_TRUE_MAP, "false");

			dataStore = HbHelper.INSTANCE.createRegisterDataStore("PmmLab");
			dataStore.setProperties(props);
			dataStore.setEPackages(new EPackage[] { CommonPackage.eINSTANCE,
					DataPackage.eINSTANCE, ModelsPackage.eINSTANCE });
			dataStore.initialize();
		}

		return dataStore;
	}

	public static String getPath() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation()
				.toString()
				+ "/.pmmlab/DB";
	}

	public static boolean exists() {
		return new File(getPath() + ".properties").exists();
	}
}
