package com.googlecode.camelrouteviewer.launcher;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaClasspathTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaJRETab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaMainTab;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;

public class CamelTabGroup extends AbstractLaunchConfigurationTabGroup implements IJavaLaunchConfigurationConstants {

	JavaMainTab javaMainTab = new JavaMainTab();
	JavaArgumentsTab javaArgumentsTab = new JavaArgumentsTab();
	JavaJRETab javaJRETab = new JavaJRETab();
	JavaClasspathTab javaClasspathTab = new JavaClasspathTab();

	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {


//		try {
//			ILaunchConfigurationWorkingCopy configuration = createDefaultConfiguration();
//			javaArgumentsTab.setDefaults(configuration);
//			javaJRETab.setDefaults(configuration);
//			javaClasspathTab.setDefaults(configuration);
//		} catch (Throwable e) {
//			// TODO Auto-generated catch block
//			System.out.println("Caught: " + e);
//			e.printStackTrace();
//		}
		
		setTabs(new ILaunchConfigurationTab[] { javaMainTab,
				javaArgumentsTab,
				javaJRETab,
				javaClasspathTab, 
				new CamelLaunchMainTab(),
				new CommonTab() });
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Auto-generated method stub
		System.out.println("Setting the default configurations to " + configuration);
		try {
			configureWorkingCopy(configuration);	
			// TODO do we need to do this??
			javaMainTab.setDefaults(configuration);
			javaArgumentsTab.setDefaults(configuration);
			javaJRETab.setDefaults(configuration);
			javaClasspathTab.setDefaults(configuration);

			System.out.println("attributes: " + configuration.getAttributes());
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			System.out.println("Caught: " + e);
			e.printStackTrace();
		}	
		super.setDefaults(configuration);
	}

	protected ILaunchConfigurationWorkingCopy createDefaultConfiguration() throws CoreException {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		String configTypeId = CamelLaunchConfigurationDelegate.LAUNCH_CONFIG_TYPE_ID;
		ILaunchConfigurationType type = manager.getLaunchConfigurationType(configTypeId);
		if (type == null) {
			System.out.println("No launch configuration type for ID: " + configTypeId);
			throw new NullPointerException("No launch configuration type for ID: " + configTypeId);
		}

		ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(null, "Run Camel");

		configureWorkingCopy(workingCopy);		
		return workingCopy;
	}

	private void configureWorkingCopy(ILaunchConfigurationWorkingCopy workingCopy) throws CoreException {
		// specify main type and program arguments
		workingCopy.setAttribute(ATTR_MAIN_TYPE_NAME, "org.apache.camel.spring.Main");

		// TODO also specify the XML?
		// -a foo.xml
		workingCopy.setAttribute(ATTR_PROGRAM_ARGUMENTS, Arrays.asList("-trace"));
		
		workingCopy.setAttribute(ATTR_VM_ARGUMENTS, Arrays.asList("-Dcom.sun.management.jmxremote", "-Dorg.apache.camel.jmx.usePlatformMBeanServer=true"));
		
		// specify classpath
		IVMInstall jre = JavaRuntime.getDefaultVMInstall();			

		File jdkHome = jre.getInstallLocation();
		IPath toolsPath = new Path(jdkHome.getAbsolutePath()).append("lib");
		IRuntimeClasspathEntry toolsEntry = JavaRuntime.newArchiveRuntimeClasspathEntry(toolsPath);
		toolsEntry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);

/*
		IPath bootstrapPath = new Path("TOMCAT_HOME").append("bin").append("bootstrap.jar");
		IRuntimeClasspathEntry bootstrapEntry = JavaRuntime.newVariableRuntimeClasspathEntry(bootstrapPath);
		bootstrapEntry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
*/			
		IPath systemLibsPath = new Path(JavaRuntime.JRE_CONTAINER);
		IRuntimeClasspathEntry systemLibsEntry = JavaRuntime.newRuntimeContainerClasspathEntry(systemLibsPath, IRuntimeClasspathEntry.STANDARD_CLASSES);
		
		List classpath = new ArrayList();
		classpath.add(toolsEntry.getMemento());
/*
		classpath.add(bootstrapEntry.getMemento());
*/			
		classpath.add(systemLibsEntry.getMemento());
		
		workingCopy.setAttribute(ATTR_CLASSPATH, classpath);
		
		// lets use the default classpath!
		//workingCopy.setAttribute(ATTR_DEFAULT_CLASSPATH, true);
	}

}
