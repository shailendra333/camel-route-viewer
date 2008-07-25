package com.googlecode.camelrouteviewer.content;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class RouteNodePropertySource implements IPropertySource {

	private static final String PATTERN_URL = "camel.node.pattern.url";

	private static final String NAME = "camel.node.name";

	private IPropertyDescriptor[] propertyDescriptors = {
			new TextPropertyDescriptor(NAME, "Pattern Name"),
			
			// TODO I wonder if we can show this as an actual link?
			new TextPropertyDescriptor(PATTERN_URL, "Pattern Documentation URL"),
			};

	private final RouteNode node;

	public RouteNodePropertySource(RouteNode node) {
		this.node = node;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return propertyDescriptors;
	}

	public Object getEditableValue() {
		return node;
	}

	public Object getPropertyValue(Object name) {
		if (name.equals(NAME)) {
			return node.patternName;
		} else if (name.equals(PATTERN_URL)) {
			return node.patternDocumentationUrl;
		}
		return null;
	}

	public boolean isPropertySet(Object name) {
		return false;
	}

	public void resetPropertyValue(Object name) {
	}

	public void setPropertyValue(Object name, Object value) {
		// TODO
	}
}
