package com.googlecode.camelrouteviewer.launcher;

import java.util.Iterator;

import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;

public class CamelLaunchShortcut implements ILaunchShortcut{

	public void launch(ISelection selection, String mode) {
		// TODO Auto-generated method stub
		System.out.println("Launching: " + selection + " with " + mode);
		if (selection instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection) selection;
			Iterator iterator = treeSelection.iterator();
			while (iterator.hasNext()) {
				Object value = iterator.next();
				System.out.println("Found: " + value + " of type: " + value.getClass());
			}
			
		}
	}

	public void launch(IEditorPart arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

}
