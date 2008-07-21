package com.googlecode.camelrouteviewer.views;

import org.eclipse.osgi.util.NLS;


/**
 * Helper class to get NLSed messages.
 */
final class RouteViewMessages extends NLS {

	private static final String BUNDLE_NAME= RouteViewMessages.class.getName();

	private RouteViewMessages() {
		// Do not instantiate
	}

	

	static {
		NLS.initializeMessages(BUNDLE_NAME, RouteViewMessages.class);
	}
}
