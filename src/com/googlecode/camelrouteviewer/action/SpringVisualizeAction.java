package com.googlecode.camelrouteviewer.action;

import java.io.File;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractRulerActionDelegate;

import com.googlecode.camelrouteviewer.views.RouteView;

//TODO move to run as menu?
public class SpringVisualizeAction implements IObjectActionDelegate {
	ISelection selection;

	public void setActivePart(IAction arg0, IWorkbenchPart arg1) {
		// TODO Auto-generated method stub

	}

	public void run(IAction arg0) {
		if (!(selection instanceof TreeSelection)){
			return;
		}

		TreeSelection treeSelection = (TreeSelection) selection;
		Object element = treeSelection.getFirstElement();
		System.out.println(element.getClass());
		
		if(element instanceof IFile){
			IFile file = (IFile)element;
			String name = file.getName();
			Object adapter = file.getAdapter(File.class);
			String path = file.getFullPath().toPortableString();
			System.out.println(">>>>>>>> name: " + name + " file: " + path + " adapter: " + adapter);
			InputStream inputStream = null;
			try {
				inputStream = file.getContents();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			not yet work now			
//			try{
//				RouteView  routeView   =   (RouteView)PlatformUI.getWorkbench().
//			        getActiveWorkbenchWindow().getActivePage().findView(RouteView.VIEW_ID); 
//				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().bringToTop(routeView); 
//			}catch(Exception exception){
//				exception.printStackTrace();
//			}
			
			//routeView.getGraphViewer().setInput(inputStream);
			
		}
	}

	public void selectionChanged(IAction arg0, ISelection selection) {
		this.selection = selection;

	}

}
