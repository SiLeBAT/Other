package de.bund.bfr.knime.aaw.json;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

public class TransformerNodeDialog extends DefaultNodeSettingsPane {

	protected TransformerNodeDialog() {
    	SettingsModelString sms = new SettingsModelString(TransformerNodeModel.JSON_PREFIX, "");
     	addDialogComponent(new DialogComponentString(sms, "Enter a prefix:"));
	}
}

