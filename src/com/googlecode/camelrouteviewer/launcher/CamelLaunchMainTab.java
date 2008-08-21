package com.googlecode.camelrouteviewer.launcher;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfiguration;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.debug.ui.IJavaDebugUIConstants;
import org.eclipse.jdt.internal.debug.ui.IJavaDebugHelpContextIds;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.debug.ui.SWTFactory;
import org.eclipse.jdt.internal.debug.ui.launcher.AbstractJavaMainTab;
import org.eclipse.jdt.internal.debug.ui.launcher.DebugTypeSelectionDialog;
import org.eclipse.jdt.internal.debug.ui.launcher.LauncherMessages;
import org.eclipse.jdt.internal.debug.ui.launcher.MainMethodSearchEngine;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.*;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.googlecode.camelrouteviewer.Activator;

public class CamelLaunchMainTab extends AbstractJavaMainTab {

	public static final String CAMEL_MAIN_TYPE_NAME = "org.apache.camel.spring.Main";

	protected Text fSpringXMLText;
	private Button fTraceCheckButton;

	public static final String ATTR_TRACE = Activator.PLUGIN_ID + ".ATTR_TRACE";
	public static final String ATTR_SPRING_XML = Activator.PLUGIN_ID
			+ ".ATTR_SPRING_XML";

	public void createControl(Composite parent) {
		Composite comp = SWTFactory.createComposite(parent, parent.getFont(),
				1, 1, GridData.FILL_BOTH);
		((GridLayout) comp.getLayout()).verticalSpacing = 0;
		createProjectEditor(comp);
		createVerticalSpacer(comp, 1);
		createCamelEditor(comp, "Camel Spring XML(Should be a List Table -_-)");
		setControl(comp);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),
				IJavaDebugHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_MAIN_TAB);
	}

	public Image getImage() {
		return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_CLASS);
	}

	public String getName() {
		return LauncherMessages.JavaMainTab__Main_19;
	}

	public String getId() {
		return "com.googlecode.camelrouteviewer.launcher.ui.camelLaunchMainTab"; //$NON-NLS-1$
	}

	public void initializeFrom(ILaunchConfiguration config) {
		super.initializeFrom(config);
		try {
			fSpringXMLText.setText(config.getAttribute(CamelLaunchMainTab.ATTR_SPRING_XML,""));
			boolean isTrace = config.getAttribute(
					CamelLaunchMainTab.ATTR_TRACE, false);
			fTraceCheckButton.setSelection(isTrace);
		} catch (CoreException e) {

			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.AbstractLaunchConfigurationTab#isValid(org.eclipse
	 * .debug.core.ILaunchConfiguration)
	 */
	public boolean isValid(ILaunchConfiguration config) {
		return true;
	}

	public void performApply(ILaunchConfigurationWorkingCopy config) {
		config.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, fProjText
						.getText().trim());

		mapResources(config);

		if (fTraceCheckButton.getSelection()) {
			config.setAttribute(ATTR_TRACE, true);
		}else {
			config.setAttribute(ATTR_TRACE, (String)null);
		}
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		IJavaElement javaElement = getContext();
		if (javaElement != null) {
			initializeJavaProject(javaElement, config);
		} else {
			config.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
					EMPTY_STRING);
		}
		config.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
				CAMEL_MAIN_TYPE_NAME);
	}

	protected void createCamelEditor(Composite parent, String text) {
		Font font = parent.getFont();
		Group mainGroup = SWTFactory.createGroup(parent, text, 2, 1,
				GridData.FILL_HORIZONTAL);
		Composite comp = SWTFactory.createComposite(mainGroup, font, 2, 2,
				GridData.FILL_BOTH, 0, 0);
		fSpringXMLText = SWTFactory.createSingleText(comp, 1);
		fSpringXMLText.setEnabled(false);

		fTraceCheckButton = SWTFactory.createCheckButton(mainGroup, "trace",
				null, false, 2);
		fTraceCheckButton.addSelectionListener(getDefaultListener());
	}

}
