package com.googlecode.camelrouteviewer.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.googlecode.camelrouteviewer.launcher.CamelLaunchConfigurationDelegate;

public class LaunchCamelAction implements IObjectActionDelegate, IJavaLaunchConfigurationConstants{
	protected static final String LAUNCHER_NAME = "Start Local Camel";
	protected static final String ID_LOCAL_CAMEL = LaunchCamelAction.class.getName();

	public void run(IAction action) {
		try {
			ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
			String configTypeId = CamelLaunchConfigurationDelegate.LAUNCH_CONFIG_TYPE_ID;
			ILaunchConfigurationType type = manager.getLaunchConfigurationType(configTypeId);
			if (type == null) {
				System.out.println("No launch configuration type for ID: " + configTypeId);
				throw new NullPointerException("No launch configuration type for ID: " + configTypeId);
			}

			ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(null, "Run Camel");


			// Delete old configurations...
			ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(type);
			for (int i = 0; i < configurations.length; i++) {
				ILaunchConfiguration configuration = configurations[i];
				if (configuration.getName().equals(LAUNCHER_NAME)) {
					configuration.delete();
					break;
				}
			}

			// specify main type and program arguments
			workingCopy.setAttribute(ATTR_MAIN_TYPE_NAME, "org.apache.camel.spring.Main");
			workingCopy.setAttribute(ATTR_PROGRAM_ARGUMENTS, "-trace");
			
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
			workingCopy.setAttribute(ATTR_DEFAULT_CLASSPATH, true);
			
			// specify System properties
			//workingCopy.setAttribute(ATTR_VM_ARGUMENTS, "-Djava.endorsed.dirs=\"..\\common\\endorsed\" -Dcatalina.base=\"..\" -Dcatalina.home=\"..\" -Djava.io.tmpdir=\"..\\temp\"");
			
			// specify working diretory
			//File workingDir = JavaCore.getClasspathVariable("TOMCAT_HOME").append("bin").toFile();
			//workingCopy.setAttribute(ATTR_WORKING_DIRECTORY, workingDir.getAbsolutePath());
			
			
			// save and launch
			ILaunchConfiguration configuration = workingCopy.doSave();
			DebugUITools.launch(configuration, ILaunchManager.RUN_MODE);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Caught: " + e);
			e.printStackTrace();
		}
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub
		
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

}
