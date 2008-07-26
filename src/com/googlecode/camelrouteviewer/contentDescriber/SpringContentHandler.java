package com.googlecode.camelrouteviewer.contentDescriber;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * reference:org.eclipse.ant.internal.core.contentDescriber.AntHandler
 */
public class SpringContentHandler extends DefaultHandler {

	private int fLevel = -1;

	private SAXParserFactory fFactory;

	/**
	 * This is the name of the top-level element found in the XML file. This
	 * member variable is <code>null</code> unless the file has been parsed
	 * successful to the point of finding the top-level element. expected
	 * value:beans
	 */
	private String fTopElementFound = null;

	private boolean fCamelContextFound = false;

	/**
	 * An exception indicating that the parsing should stop.
	 * 
	 * @since 3.1
	 */
	private class StopParsingException extends SAXException {
		/**
		 * All serializable objects should have a stable serialVersionUID
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructs an instance of <code>StopParsingException</code> with a
		 * <code>null</code> detail message.
		 */
		public StopParsingException() {
			super((String) null);
		}
	}

	protected boolean parseContents(InputSource contents) throws IOException,
			ParserConfigurationException, SAXException {
		// Parse the file into we have what we need (or an error occurs).
		try {
			fFactory = getFactory();
			if (fFactory == null) {
				return false;
			}
			final SAXParser parser = createParser(fFactory);
			// to support external entities specified as relative URIs (see bug
			// 63298)
			contents.setSystemId("/"); //$NON-NLS-1$
			parser.parse(contents, this);
		} catch (StopParsingException e) {
			// Abort the parsing normally. Fall through...
		}
		return true;
	}

	/**
	 * Creates a new SAX parser for use within this instance.
	 * 
	 * @return The newly created parser.
	 * @throws ParserConfigurationException
	 *             If a parser of the given configuration cannot be created.
	 * @throws SAXException
	 *             If something in general goes wrong when creating the parser.
	 */
	private final SAXParser createParser(SAXParserFactory parserFactory)
			throws ParserConfigurationException, SAXException,
			SAXNotRecognizedException, SAXNotSupportedException {
		// Initialize the parser.
		final SAXParser parser = parserFactory.newSAXParser();
		final XMLReader reader = parser.getXMLReader();
		// disable DTD validation (bug 63625)
		try {
			// be sure validation is "off" or the feature to ignore DTD's will
			// not apply
			reader.setFeature("http://xml.org/sax/features/validation", false); //$NON-NLS-1$
			reader
					.setFeature(
							"http://apache.org/xml/features/nonvalidating/load-external-dtd", false); //$NON-NLS-1$
		} catch (SAXNotRecognizedException e) {
			// not a big deal if the parser does not recognize the features
		} catch (SAXNotSupportedException e) {
			// not a big deal if the parser does not support the features
		}
		return parser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
	 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public final void startElement(final String uri, final String elementName,
			final String qualifiedName, final Attributes attributes)
			throws SAXException {
		fLevel++;
		if (fTopElementFound == null) {
			fTopElementFound = elementName;
			if (!hasRootBeansElement()) {
				throw new StopParsingException();
			}
		}
		if (fLevel == 1 && "camelContext".equals(elementName)) {
			fCamelContextFound = true;
			throw new StopParsingException();
		}

	}
	
    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localName, String qName) throws SAXException {
    	super.endElement(uri, localName, qName);
    	fLevel--;
    }

	private SAXParserFactory getFactory() {
		synchronized (this) {
			if (fFactory != null) {
				return fFactory;
			}
			fFactory = SAXParserFactory.newInstance();
			fFactory.setNamespaceAware(true);
		}
		return fFactory;
	}

	public boolean hasRootBeansElement() {
		return "beans".equals(fTopElementFound);
	}

	public boolean hasCamelContextElement() {
		return fCamelContextFound;
	}
}
