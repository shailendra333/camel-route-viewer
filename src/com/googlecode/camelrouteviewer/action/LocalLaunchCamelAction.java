package com.googlecode.camelrouteviewer.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.Configuration;

import org.apache.camel.model.RouteType;
import org.apache.camel.spring.Main;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.googlecode.camelrouteviewer.views.RouteView;

public class LocalLaunchCamelAction implements IObjectActionDelegate,
		IJavaLaunchConfigurationConstants {
	protected static final String LAUNCHER_NAME = "Start Camel";

	ISelection selection;

	public void run(IAction action) {
		try {
			ILaunchManager manager = DebugPlugin.getDefault()
					.getLaunchManager();

			ILaunchConfiguration config = null;
			ILaunchConfigurationType type = manager
					.getLaunchConfigurationType(ID_JAVA_APPLICATION);
			ILaunchConfiguration[] configurations = manager
					.getLaunchConfigurations(type);
			for (int i = 0; i < configurations.length; i++) {
				ILaunchConfiguration configuration = configurations[i];
				if (configuration.getName().equals(LAUNCHER_NAME)) {
					config = configuration;
					break;
				}
			}
			if (config == null) {
				ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(
						null, LAUNCHER_NAME);
				config = workingCopy.doSave();
			}

			final Launch localCamelLaunch = new Launch(config, "run", null);
			final Main main = new Main();
			IProcess process = new IProcess() {

				public String getAttribute(String key) {
					// TODO Auto-generated method stub
					return null;
				}

				public int getExitValue() throws DebugException {
					return 0;
				}

				public String getLabel() {
					// TODO Auto-generated method stub
					return "Camel process";
				}

				public ILaunch getLaunch() {
					return localCamelLaunch;
				}

				public IStreamsProxy getStreamsProxy() {
					// TODO Auto-generated method stub
					return null;
				}

				public void setAttribute(String key, String value) {
					// TODO Auto-generated method stub

				}

				public Object getAdapter(Class adapter) {
					// TODO Auto-generated method stub
					return null;
				}

				public boolean canTerminate() {
					return !(main.isStopped() || main.isStopping());
				}

				public boolean isTerminated() {
					return main.isStopped();
				}

				public void terminate() throws DebugException {
					try {
						main.stop();
					} catch (Exception e) {
						// TODO how to convert to debug exception?
						System.out.println("Failed to terminate: " + e);
						e.printStackTrace();
					}
				}
			};
			localCamelLaunch.addProcess(process);

			System.out
					.println("About to get the launch manager to start it...");
			manager.addLaunch(localCamelLaunch);

			String uri = getApplicationContextUri();
			System.out.println("now starting the main with uri: " + uri);
			if (uri != null) {
				main.setApplicationContextUri(uri);
			}
			main.start();
			List<RouteType> routeDefinitions = main.getRouteDefinitions();
			onRoutesCreated(routeDefinitions);
			System.out.println("main has started!!!");

			/*
			 * // TODO can't think of a better way Thread thread = new Thread()
			 * {
			 * 
			 * @Override public void run() { try { main.start(); } catch
			 * (Exception e) { System.out.println("Failed to start: "+ e);
			 * e.printStackTrace(); } }
			 * 
			 * }; thread.start();
			 */} catch (Exception e) {
			handleException(e);
		}
	}

	protected void handleException(Exception e) {
		// TODO: handle exception
		System.out.println("Caught: " + e);
		e.printStackTrace();
	}

	protected void onRoutesCreated(List<RouteType> routeDefinitions) {
		try {
			RouteView routeView = (RouteView) PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage().findView(
							RouteView.VIEW_ID);
			PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().bringToTop(routeView);
			routeView.setRouteDefinitions(routeDefinitions);
		} catch (Exception e) {
			handleException(e);
		}

	}

	public String getApplicationContextUri() {
		if (selection instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection) selection;
			Object element = treeSelection.getFirstElement();
			System.out.println(element.getClass());

			if (element instanceof IFile) {
				IFile file = (IFile) element;
				// TODO we should be able to use the name on the classpath!!
				/*
				 * String name = file.getName(); return "classpath:" + name;
				 */

				return "file:" + file.getLocation().toPortableString();
			}
		}
		return null;
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub

	}

	public void selectionChanged(IAction arg0, ISelection selection) {
		this.selection = selection;

	}

}
