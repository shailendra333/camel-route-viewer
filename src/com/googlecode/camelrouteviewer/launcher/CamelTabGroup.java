package com.googlecode.camelrouteviewer.launcher;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.AppletArgumentsTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.AppletMainTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.AppletParametersTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaClasspathTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaJRETab;

public class CamelTabGroup extends AbstractLaunchConfigurationTabGroup {

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTabGroup#createTabs(org.eclipse.debug.ui.ILaunchConfigurationDialog, java.lang.String)
	 */
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
			new AppletMainTab(),
			new AppletParametersTab(),
			new AppletArgumentsTab(),
			new JavaJRETab(),
			new JavaClasspathTab(),
			new SourceLookupTab(),	
			new CommonTab()
		};
		setTabs(tabs);
	}
}
