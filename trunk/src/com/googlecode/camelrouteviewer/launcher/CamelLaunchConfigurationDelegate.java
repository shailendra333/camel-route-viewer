package com.googlecode.camelrouteviewer.launcher;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventFilter;
import org.eclipse.debug.core.IExpressionListener;
import org.eclipse.debug.core.IExpressionManager;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IExpression;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.core.model.IWatchExpression;
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
		System.out.println("called with configuration.classpath: " + Arrays.asList(classpath));

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
		IDebugTarget debugTarget = launch.getDebugTarget();
		System.out.println(">>>> Debug Target: " + debugTarget);
		if (debugTarget != null) {
		}
		
		// TODO an attempt to evaluate expressions...
		DebugPlugin debugPlugin = DebugPlugin.getDefault();
		debugPlugin.addDebugEventFilter(new IDebugEventFilter() {

			public DebugEvent[] filterDebugEvents(DebugEvent[] events) {
				for (DebugEvent event : events) {
					System.out.println(">>>> debug event: " + event);
					Object source = event.getSource();
					if (source instanceof IThread) {
						IThread thread = (IThread) source;
						displayStackFrames(thread);
					}
				}
				return events;
			}
		});
		try {
			IExpressionManager expressionManager = debugPlugin.getExpressionManager();
			//String expressionText = "org.apache.camel.spring.Main.getInstance()";
			String expressionText = "new org.apache.camel.impl.DefaultCamelContext()";
			IWatchExpression expression = expressionManager.newWatchExpression(expressionText);
			expression.setEnabled(true);
//			IDebugElement expressionContext = null;
//			expression.setExpressionContext(expressionContext );
			expressionManager.addExpression(expression);
			expression.evaluate();
			System.out.println("Attempted to evaluate expression: " + expression);
			System.out.println("Value: " + expression.getValue());
			System.out.println("Value: " + Arrays.asList(expression.getErrorMessages()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Caught: " + e);
			e.printStackTrace();
		}
	}

	protected void displayStackFrames(IThread thread) {
		try {
			IStackFrame[] stackFrames = thread.getStackFrames();
			for (IStackFrame stackFrame : stackFrames) {
				System.out.println("Stack frame: " + stackFrame);
				IVariable[] variables = stackFrame.getVariables();
				for (IVariable variable : variables) {
					System.out.println("Variable: " + variable.getName() + " value: " + variable.getValue());
				}
			}
		} catch (DebugException e) {
			// TODO Auto-generated catch block
			System.out.println("Caught: " + e);
			e.printStackTrace();
		}
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
