package com.googlecode.camelrouteviewer.action;

import org.eclipse.jdt.core.IType;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class SpringVisualizeAction implements IObjectActionDelegate {
	ISelection selection;

	public void setActivePart(IAction arg0, IWorkbenchPart arg1) {
		// TODO Auto-generated method stub

	}

	public void run(IAction arg0) {
		if (!(selection instanceof IStructuredSelection))
			return;
		IStructuredSelection structured = (IStructuredSelection) selection;
		IType type = (IType) structured.getFirstElement();

	}

	public void selectionChanged(IAction arg0, ISelection arg1) {
		this.selection = selection;

	}

}
