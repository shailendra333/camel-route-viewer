package com.googlecode.camelrouteviewer.launcher;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.core.model.RuntimeProcess;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.jface.viewers.deferred.SetModel;

/**
 * An initial attempt at a launcher for Camel using the Spring main
 * 
 * TODO this does not seem to work yet!
 */
public class CamelLaunchConfigurationDelegate extends
		AbstractJavaLaunchConfigurationDelegate implements
		ILaunchConfigurationDelegate {

	private ILaunchConfiguration currentLaunchConfiguration;

	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {

//		hi~~james.strachan:I just to try these codes ~~~,you can restore your code any time~~~:)
		System.out.println(configuration);
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
		
		
//		IVMInstall vm = verifyVMInstall(configuration);
//		IVMRunner runner = vm.getVMRunner(mode);
//
//		File workingDirectory = getWorkingDirectory(configuration);
//
//		String arguments = getProgramArguments(configuration);
//		String vmargs = getVMArguments(configuration);
//
//		String[] classpath = getClasspath(configuration);
//		VMRunnerConfiguration runConfig = new VMRunnerConfiguration("jre",
//				classpath);
//		runConfig.setProgramArguments(new String[] { "org.apache.camel.spring.Main", arguments });
//		runConfig.setVMArguments(new String[] { vmargs });
//
//		runConfig.setWorkingDirectory(workingDirectory.getAbsolutePath());
//		// Bootpath
//		String[] bootpath = getBootpath(configuration);
//		runConfig.setBootClassPath(bootpath);
//
//		// Launch the configuration
//		this.currentLaunchConfiguration = configuration;
//		runner.run(runConfig, launch, monitor);
	}

}
