package com.googlecode.camelrouteviewer.launcher;

import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;

public class CamelLaunchShortcut implements ILaunchShortcut{

	public void launch(ISelection arg0, String arg1) {
		// TODO Auto-generated method stub
		System.out.println("Launching: " + arg0 + " with " + arg1);
	}

	public void launch(IEditorPart arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

}
