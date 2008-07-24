package com.googlecode.camelrouteviewer.views;

import org.eclipse.osgi.util.NLS;

/**
 * Helper class to get NLSed messages.
 */
final class RouteViewMessages extends NLS {

	private static final String BUNDLE_NAME = RouteViewMessages.class.getName();

	public static String GotoInputAction_label;
	public static String GotoInputAction_tooltip;
	public static String GotoInputAction_description;
	public static String RouteView_action_toggleLinking_toolTipText;

	public static String RouteView_action_toogleLinking_text;

	private RouteViewMessages() {
		// Do not instantiate
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, RouteViewMessages.class);
	}
}
