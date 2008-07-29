package com.googlecode.camelrouteviewer.launcher;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

/**
 * An initial attempt at a launcher for Camel using the Spring main
 */
public class CamelLaunchConfigurationDelegate extends
		AbstractJavaLaunchConfigurationDelegate implements
		ILaunchConfigurationDelegate {

	public static final String LAUNCH_CONFIG_TYPE_ID = "com.googlecode.camelrouteviewer.launchConfigurationType";
	
	private ILaunchConfiguration currentLaunchConfiguration;

	
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {

//		hi~~james.strachan:I just to try these codes ~~~,you can restore your code any time~~~:)
/*		System.out.println(configuration);
		System.out
				.println("You selected Camel launchconfiguration type to run");

		Process process;

		try {
			process = Runtime.getRuntime().exec("ipconfig");

			RuntimeProcess runtimeProcess = new RuntimeProcess(launch, process,
					"Camel Process", null);

			launch.addProcess(runtimeProcess);

		} catch (IOException e) {

			e.printStackTrace();

		}
*/		
		
		IVMInstall vm = verifyVMInstall(configuration);
		IVMRunner runner = vm.getVMRunner(mode);

		File workingDirectory = getWorkingDirectory(configuration);

		String arguments = getProgramArguments(configuration);
		String vmargs = getVMArguments(configuration);

		String[] classpath = getClasspath(configuration);
		VMRunnerConfiguration runConfig = new VMRunnerConfiguration("jre",
				classpath);
		runConfig.setProgramArguments(new String[] { "org.apache.camel.spring.Main", arguments });
		runConfig.setVMArguments(new String[] { "-Dcom.sun.management.jmxremote", "-Dorg.apache.camel.jmx.usePlatformMBeanServer=true", vmargs });

		// TODO we need to set the project to be the currently active project to see the classpath
		// of local stuff
		
		if (workingDirectory != null) {
			runConfig.setWorkingDirectory(workingDirectory.getAbsolutePath());
		}
		// Bootpath
		String[] bootpath = getBootpath(configuration);
		runConfig.setBootClassPath(bootpath);

		// Launch the configuration
		this.currentLaunchConfiguration = configuration;
		runner.run(runConfig, launch, monitor);
	}

}
