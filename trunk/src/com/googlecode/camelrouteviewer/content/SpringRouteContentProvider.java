package com.googlecode.camelrouteviewer.content;

import java.util.List;

import org.apache.camel.model.RouteType;
import org.apache.camel.spring.Main;
import org.eclipse.jface.viewers.Viewer;

public class SpringRouteContentProvider extends RouteContentProviderSupport {

	private Main main;

	private final String applicationContextUri;

	/**
	 * Constructs a spring based route content provider with an optional application context URI
	 * 
	 * @param applicationContextUri maybe be null which will effectively default to the value of 
	 * <code>META-INF/spring/*.xml</code> which is the default used by the Camel maven tooling
	 * and the Spring Dynamic Modules mechanism.
	 */
	public SpringRouteContentProvider(String applicationContextUri) {
		this.applicationContextUri = applicationContextUri;
	}
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO could the input maybe be the applicationContextURI I wonder?
		
		if (main != null) {
			routesTempStore.clear();
			try {
				main.stop();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		main = new Main();
		if (applicationContextUri != null) {
			main.setApplicationContextUri(applicationContextUri);
		}
		try {
			main.start();
			List<RouteType> routes = main.getRouteDefinitions();
			buildRoute(routes);
			viewer.refresh();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}