package com.googlecode.camelrouteviewer.launcher;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.camel.util.ObjectHelper;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;

public class CamelLaunchShortcut implements ILaunchShortcut, IJavaLaunchConfigurationConstants {
	public static final String LAUNCHER_NAME = "Run Camel Spring XML";

	public void launch(ISelection selection, String mode) {
		// TODO Auto-generated method stub
		System.out.println("Launching: " + selection + " with " + mode);
		if (selection instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection) selection;
			List<IFile> files = new ArrayList<IFile>();
			Iterator iterator = treeSelection.iterator();
			while (iterator.hasNext()) {
				Object value = iterator.next();
				System.out.println("Found: " + value + " of type: " + value.getClass());
				if (value instanceof IFile) {
					files.add((IFile) value);
				}
			}
			if (!files.isEmpty()) {
				launch(files);
			}
		}
	}

	public void launch(IEditorPart arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	protected void launch(List<IFile> files) {
		try {
			IFile firstFile = files.get(0);
			IProject project = firstFile.getProject();

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
			StringBuffer buffer = new StringBuffer("-trace -a ");
			boolean first = true;
			for (IFile file : files) {
				if (first) {
					first = false;
				} else {
					buffer.append(";");
				}
				// TODO we should be able to use the name on the classpath!!
				/*
				 * String name = file.getName(); return "classpath:" + name;
				 */
				String applicationContextUri = "\"file:" + file.getLocation().toPortableString()+"\"";
				buffer.append(applicationContextUri);
			}
			String arguments = buffer.toString();
			workingCopy.setAttribute(ATTR_PROGRAM_ARGUMENTS, arguments);

			System.out.println("About to run Camel main with arguments: " + arguments);

			String runMode = ILaunchManager.DEBUG_MODE;

			IJavaProject myJavaProject = toJavaProject(project);
			if (myJavaProject != null) {
				IVMInstall vmInstall = JavaRuntime.getVMInstall(myJavaProject);
				if (vmInstall == null)
					vmInstall = JavaRuntime.getDefaultVMInstall();
				if (vmInstall != null) {
					IVMRunner vmRunner = vmInstall.getVMRunner(runMode); // use Debug so we can communicate!
					if (vmRunner != null) {
						String[] classPath = null;
						try {
							classPath = JavaRuntime.computeDefaultRuntimeClassPath(myJavaProject);
						} catch (CoreException e) {
							System.out.println("Caught: " + e);
							e.printStackTrace();
						}
						if (classPath != null) {
							// this does not seem to work!
							// VMRunnerConfiguration vmConfig = new
							// VMRunnerConfiguration
							// ("org.apache.camel.spring.Main", classPath);
							// ILaunch launch = new Launch(configuration,
							// ILaunchManager.RUN_MODE, null);
							// vmRunner.run(vmConfig, launch, null);

							String name = project.getName();
							workingCopy.setAttribute(ATTR_PROJECT_NAME, name);
							List<String> classPathList = Arrays.asList(classPath);
							System.out.println("About to run with classpath: " + classPathList);
							workingCopy.setAttribute(ATTR_CLASSPATH, classPathList);
							
							ILaunchConfiguration configuration = workingCopy.doSave();

							DebugUITools.launch(configuration, runMode);

						} else {
							System.out.println("No classpath for project: " + myJavaProject);
						}
					} else {
						System.out.println("No vmrunner for project: " + myJavaProject);
					}
				} else {
					System.out.println("No vmInstall for project: " + myJavaProject);
				}
			} else {
				System.out.println("Project is not a Java project: " + project + " type: " + ObjectHelper.className(project));

				// specify classpath
				IVMInstall jre = JavaRuntime.getDefaultVMInstall();

				File jdkHome = jre.getInstallLocation();
				IPath toolsPath = new Path(jdkHome.getAbsolutePath()).append("lib");
				IRuntimeClasspathEntry toolsEntry = JavaRuntime.newArchiveRuntimeClasspathEntry(toolsPath);
				toolsEntry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);

				/*
				 * IPath bootstrapPath = new
				 * Path("TOMCAT_HOME").append("bin").append("bootstrap.jar");
				 * IRuntimeClasspathEntry bootstrapEntry =
				 * JavaRuntime.newVariableRuntimeClasspathEntry(bootstrapPath);
				 * bootstrapEntry
				 * .setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
				 */
				IPath systemLibsPath = new Path(JavaRuntime.JRE_CONTAINER);
				IRuntimeClasspathEntry systemLibsEntry = JavaRuntime.newRuntimeContainerClasspathEntry(systemLibsPath, IRuntimeClasspathEntry.STANDARD_CLASSES);

				List classpath = new ArrayList();
				classpath.add(toolsEntry.getMemento());
				/*
				 * classpath.add(bootstrapEntry.getMemento());
				 */
				classpath.add(systemLibsEntry.getMemento());

				if (project != null) {
					String name = project.getName();
					System.out.println("Project name: " + name);
					workingCopy.setAttribute(ATTR_PROJECT_NAME, name);
				}
				workingCopy.setAttribute(ATTR_CLASSPATH, classpath);

				// lets use the default classpath!
				// workingCopy.setAttribute(ATTR_DEFAULT_CLASSPATH, true);

				// specify System properties
				// workingCopy.setAttribute(ATTR_VM_ARGUMENTS,
				// "-Djava.endorsed.dirs=\"..\\common\\endorsed\" -Dcatalina.base=\"..\" -Dcatalina.home=\"..\" -Djava.io.tmpdir=\"..\\temp\""
				// );

				// specify working diretory
				// File workingDir =
				// JavaCore.getClasspathVariable("TOMCAT_HOME")
				// .append("bin").toFile();
				// workingCopy.setAttribute(ATTR_WORKING_DIRECTORY,
				// workingDir.getAbsolutePath());

				// save and launch
				ILaunchConfiguration configuration = workingCopy.doSave();
				DebugUITools.launch(configuration, ILaunchManager.RUN_MODE);
			}
		} catch (Throwable e) {
			// TODO: handle exception
			System.out.println("Caught: " + e);
			e.printStackTrace();
		}
	}

	protected IJavaProject toJavaProject(IProject project) {
		IJavaProject myJavaProject = (IJavaProject) Platform.getAdapterManager().getAdapter(project, IJavaProject.class);

		// IJavaProject myJavaProject = (IJavaProject)
		// project.getAdapter(IJavaProject.class);
		if (myJavaProject == null) {
			System.out.println("Cannot convert project to a Java project!: " + project);
			// TODO is there a better way to get the Java project for a
			// project file?
			myJavaProject = JavaCore.getJavaCore().create(project);
		}
		return myJavaProject;
	}
}
