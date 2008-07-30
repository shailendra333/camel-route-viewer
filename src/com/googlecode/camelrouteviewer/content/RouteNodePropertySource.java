package com.googlecode.camelrouteviewer.content;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class RouteNodePropertySource implements IPropertySource {

	private static final String PATTERN_URL = "camel.node.pattern.url";
	private static final String NAME = "camel.node.name";
	private static final String EXCHANGES = "camel.node.exchanges";

	private IPropertyDescriptor[] propertyDescriptors = { 
			new TextPropertyDescriptor(NAME, "Pattern Name"),

			// TODO I wonder if we can show this as an actual link?
			new TextPropertyDescriptor(PATTERN_URL, "Pattern Documentation URL"),

			new PropertyDescriptor(EXCHANGES, "Exchanges"), };

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
		} else if (name.equals(EXCHANGES)) {
			List<Exchange> exchanges = node.getExchanges();
			// TODO this should work with collections!
			if (!exchanges.isEmpty()) {
				// TODO this does not work!
				/*
				 * PropertySheetEntry entry = new PropertySheetEntry();
				 * entry.setValues(exchanges.toArray());
				 * entry.setPropertySourceProvider(new IPropertySourceProvider()
				 * { public IPropertySource getPropertySource(Object object) {
				 * return new ExchangePropertySource((Exchange) object); }});
				 * return entry;
				 */
				// TODO how to render a list?
				return new ExchangePropertySource(exchanges.get(0));
			}
/*			List<ExchangePropertySource> sources = new ArrayList<ExchangePropertySource>(exchanges.size());
			for (Exchange exchange : exchanges) {
				sources.add(new ExchangePropertySource(exchange));
			}
			return sources;
*/		}
		return null;
	}

	public boolean isPropertySet(Object name) {
		return true;
	}

	public void resetPropertyValue(Object name) {
	}

	public void setPropertyValue(Object name, Object value) {
		// TODO
	}
}
