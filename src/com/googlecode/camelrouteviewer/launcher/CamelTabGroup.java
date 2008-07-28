package com.googlecode.camelrouteviewer.launcher;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

import org.eclipse.jdt.debug.ui.launchConfigurations.AppletArgumentsTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.AppletMainTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.AppletParametersTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaClasspathTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaJRETab;

public class CamelTabGroup extends AbstractLaunchConfigurationTabGroup {

	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {

		setTabs(new ILaunchConfigurationTab[] { new CamelLaunchMainTab(),
				new CommonTab() });

	}

}
