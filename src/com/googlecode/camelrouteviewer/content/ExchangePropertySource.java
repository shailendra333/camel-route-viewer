package com.googlecode.camelrouteviewer.content;

import org.apache.camel.Exchange;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class ExchangePropertySource implements IPropertySource {

	private static final String ID = "camel.exchange.id";
	private static final String PATTERN = "camel.exchange.pattern";
	private static final String IN_MESSAGE = "camel.exchange.in.message";
	private static final String OUT_MESSAGE = "camel.exchange.out.message";

	private IPropertyDescriptor[] propertyDescriptors = {
			new TextPropertyDescriptor(ID, "ID"),
			new TextPropertyDescriptor(PATTERN, "Message Exchange Pattern"),
			new PropertyDescriptor(IN_MESSAGE, "In Message"),
			new PropertyDescriptor(OUT_MESSAGE, "Out Message"), };

	private final Exchange exchange;

	public ExchangePropertySource(Exchange exchange) {
		this.exchange = exchange;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return propertyDescriptors;
	}

	public Object getEditableValue() {
		return exchange;
	}

	public Object getPropertyValue(Object name) {
		if (name.equals(ID)) {
			return exchange.getExchangeId();
		} else if (name.equals(IN_MESSAGE)) {
			return new MessagePropertySource(exchange.getIn());
		} else if (name.equals(OUT_MESSAGE)) {
			return new MessagePropertySource(exchange.getOut());
		} else if (name.equals(PATTERN)) {
			return exchange.getPattern();
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
