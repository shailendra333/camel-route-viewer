package com.googlecode.camelrouteviewer.content;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class RouteLabelProvider extends LabelProvider {

	public Image getImage(Object element) {
		// RouteNode routeNode = (RouteNode) element;

		if (element instanceof RouteNode) {
			RouteNode routeNode = (RouteNode) element;
			return routeNode.image;
		}

		return null;
	}

	public String getText(Object element) {
		return element.toString();
	}

}