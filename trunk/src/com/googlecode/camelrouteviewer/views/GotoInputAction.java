package com.googlecode.camelrouteviewer.views;
/**
 * reference:java jdt javadoc view
 */
import org.eclipse.core.runtime.Assert;

import org.eclipse.jface.action.Action;

import org.eclipse.ui.PlatformUI;

import org.eclipse.jdt.core.IJavaElement;

import org.eclipse.jdt.ui.actions.OpenAction;

import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.JavaPluginImages;

class GotoInputAction extends Action {

	private RouteView fInfoView;

	public GotoInputAction(RouteView infoView) {
		Assert.isNotNull(infoView);
		fInfoView = infoView;

		JavaPluginImages.setLocalImageDescriptors(this, "goto_input.gif"); //$NON-NLS-1$
//		setText(RouteViewMessages.GotoInputAction_label);
//		setToolTipText(RouteViewMessages.GotoInputAction_tooltip);
//		setDescription(RouteViewMessages.GotoInputAction_description);

		PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
				IJavaHelpContextIds.OPEN_INPUT_ACTION);
	}

	public void run() {
		// IJavaElement inputElement= fInfoView.getInput();
		// new OpenAction(fInfoView.getViewSite()).run(new Object[] {
		// inputElement });
	}
}
