//The contents of this file are subject to the Mozilla Public License Version 1.1
//(the "License"); you may not use this file except in compliance with the 
//License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
//
//Software distributed under the License is distributed on an "AS IS" basis,
//WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License 
//for the specific language governing rights and
//limitations under the License.
//
//The Original Code is "The Columba Project"
//
//The Initial Developers of the Original Code are Frederik Dietz and Timo Stich.
//Portions created by Frederik Dietz and Timo Stich are Copyright (C) 2003. 
//
//All Rights Reserved.
package org.columba.mail.gui.config.filter.plugins;

import javax.swing.JComboBox;

import org.columba.core.filter.FilterCriteria;
import org.columba.mail.gui.config.filter.CriteriaList;
import org.columba.mail.plugin.FilterExtensionHandler;

public class FlagsCriteriaRow extends DefaultCriteriaRow {
	private JComboBox matchComboBox;

	private JComboBox flagsComboBox;

	public FlagsCriteriaRow(FilterExtensionHandler pluginHandler,
			CriteriaList criteriaList, FilterCriteria c) {
		super(pluginHandler, criteriaList, c);
	}

	public void updateComponents(boolean b) {
		super.updateComponents(b);

		if (b) {
			matchComboBox.setSelectedItem(criteria.getCriteriaString());

			String flag = criteria.getPatternString();
			flagsComboBox.setSelectedItem(flag);
		} else {
			criteria
					.setCriteriaString((String) matchComboBox.getSelectedItem());
			criteria.setPatternString((String) flagsComboBox.getSelectedItem());
		}
	}

	public void initComponents() {
		super.initComponents();

		matchComboBox = new JComboBox();
		matchComboBox.addItem("is");
		matchComboBox.addItem("is not");

		addComponent(matchComboBox);

		flagsComboBox = new JComboBox();
		flagsComboBox.addItem("Answered");
		flagsComboBox.addItem("Deleted");
		flagsComboBox.addItem("Flagged");
		flagsComboBox.addItem("Recent");
		flagsComboBox.addItem("Draft");
		flagsComboBox.addItem("Seen");
		flagsComboBox.addItem("Spam");

		addComponent(flagsComboBox);
	}
}
