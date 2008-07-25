package com.googlecode.camelrouteviewer.content;

import org.apache.camel.Message;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class MessagePropertySource implements IPropertySource {

	private static final String ID = "camel.message.id";
	private static final String BODY = "camel.message.body";

	private IPropertyDescriptor[] propertyDescriptors = {
			new TextPropertyDescriptor(ID, "ID"),
			new TextPropertyDescriptor(BODY, "Body"), };

	private final Message message;

	public MessagePropertySource(Message message) {
		this.message = message;
	}

	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return propertyDescriptors;
	}

	public Object getEditableValue() {
		return message;
	}

	public Object getPropertyValue(Object name) {
		if (name.equals(ID)) {
			return message.getMessageId();
		} else if (name.equals(BODY)) {
			return message.getBody();
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
