package com.googlecode.camelrouteviewer.launcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class CamelLaunchMainTab extends AbstractLaunchConfigurationTab {

	private Text nameText;

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);

		composite.setLayout(new GridLayout(2, false));

		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label label = new Label(composite, SWT.NONE);

		label.setText("Camel Spring DSL");

		nameText = new Text(composite, SWT.SINGLE | SWT.BORDER);

		nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		setControl(composite);

	}

	public String getName() {
		return "Camel Main";
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		try {

			String name = configuration.getAttribute("dslLocation", "");

			nameText.setText(name);

		} catch (CoreException e) {

			e.printStackTrace();

		}

	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute("dslLocation", nameText.getText());

	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute("dslLocation", "<unnamed>");

	}

}
