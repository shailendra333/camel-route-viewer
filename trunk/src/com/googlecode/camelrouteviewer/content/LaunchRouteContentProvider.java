package com.googlecode.camelrouteviewer.content;

import java.util.List;

import org.apache.camel.model.RouteType;
import org.eclipse.jface.viewers.Viewer;

import com.googlecode.camelrouteviewer.views.RouteSource;

/**
 * Can handle either a {@link List<RouteType>} or a {@link RouteSource} as the input
 * which is then converted into the content for the viewer
 */
public class LaunchRouteContentProvider extends RouteContentProviderSupport {

	private RouteSourceToDefinitionConverter converter = new RouteSourceToDefinitionConverter();

	public LaunchRouteContentProvider() {
	}
	
	public void setRouteDefinitions(List<RouteType> routes) {
			clear();
		try {
			buildRoute(routes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof List) {
			List<RouteType> routes = (List<RouteType>) newInput;
			setRouteDefinitions(routes);
		}
		else if (newInput instanceof RouteSource) {
			List<RouteType> routes = converter.convert((RouteSource) newInput);
			setRouteDefinitions(routes);
		}
	}
}