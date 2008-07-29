package com.googlecode.camelrouteviewer.launcher;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

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

		String main = getMainTypeName(configuration);
		if (main == null || main.length() == 0) {
			main = "org.apache.camel.spring.Main";
		}
		String arguments = getProgramArguments(configuration);
		String vmargs = getVMArguments(configuration);

		String[] classpath = getClasspath(configuration);
		VMRunnerConfiguration runConfig = new VMRunnerConfiguration(main, classpath);

		// TODO we need to deal properly with arguments and vmargs containing a list of arguments
		List<String> argList = new ArrayList<String>();
		addCommandLineArgs(argList, arguments);
		if (!argList.isEmpty()) {
			runConfig.setProgramArguments(toStringArray(argList));
		}
		
		List<String> vmArgList = new ArrayList<String>();
		vmArgList.add("-Dcom.sun.management.jmxremote");
		vmArgList.add("-Dorg.apache.camel.jmx.usePlatformMBeanServer=true");
		addCommandLineArgs(vmArgList, vmargs);
		if (!vmArgList.isEmpty()) {
			runConfig.setVMArguments(toStringArray(vmArgList));
		}
		
		// TODO we need to set the project to be the currently active project to see the classpath
		// of local stuff
		
		if (workingDirectory != null) {
			runConfig.setWorkingDirectory(workingDirectory.getAbsolutePath());
		}
		// Bootpath
		String[] bootpath = getBootpath(configuration);
		if (bootpath != null) {
			runConfig.setBootClassPath(bootpath);
		}
		
		// Launch the configuration
		this.currentLaunchConfiguration = configuration;
		
		System.out.println("About to run main: " + runConfig.getClassToLaunch() );
		System.out.println("args: " + Arrays.asList(runConfig.getProgramArguments()));
		System.out.println("VM args: " + Arrays.asList(runConfig.getVMArguments()));
		System.out.println("Classpath: " + Arrays.asList(runConfig.getClassPath()));
		runner.run(runConfig, launch, monitor);
	}


	protected String[] toStringArray(List<String> list) {
		String[] answer = new String[list.size()];
		list.toArray(answer);
		return answer;
	}


	private void addCommandLineArgs(List<String> argList, String arguments) {
		if (arguments != null && arguments.length() > 0) {
			StringTokenizer iter = new StringTokenizer(arguments);
			while (iter.hasMoreTokens()) {
				argList.add(iter.nextToken());
			}
		}
	}

}
