package com.googlecode.camelrouteviewer.contentDescriber;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.internal.content.XMLContentDescriber;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.content.IContentDescription;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A content describer for Camel Spring DSL.
 * reference:org.eclipse.ant.internal.core.contentDescriber.AntBuildfileContentDescriber
 */
public class SpringContentDescriber extends XMLContentDescriber implements
		IExecutableExtension {

	/* (Intentionally not included in javadoc)
	 * Determines the validation status for the given contents.
	 * 
	 * @param contents the contents to be evaluated
	 * @return one of the following:<ul>
	 * <li><code>VALID</code></li>,
	 * <li><code>INVALID</code></li>,
	 * <li><code>INDETERMINATE</code></li>
	 * </ul>
	 * @throws IOException
	 */
	private int checkCriteria(InputSource contents) throws IOException {
		SpringContentHandler springContentHandler = new SpringContentHandler();
		try {
			if (!springContentHandler.parseContents(contents)) {
				return INDETERMINATE;
			}
		} catch (SAXException e) {
			// we may be handed any kind of contents... it is normal we fail to
			// parse
			return INDETERMINATE;
		} catch (ParserConfigurationException e) {
			// some bad thing happened - force this describer to be disabled
			String message = "Internal Error: XML parser configuration error during content description for Ant buildfiles"; //$NON-NLS-1$
			throw new RuntimeException(message);
		}
		
		// Check to see if we matched our criteria.
		if (springContentHandler.hasRootBeansElement()) {
			if (springContentHandler.hasCamelContextElement()){
					return VALID;
            }
            //only a top level project element...maybe an Ant buildfile
            return INDETERMINATE;
        } 
			
		return INDETERMINATE;

	}
	
	/* (Intentionally not included in javadoc)
	 * @see IContentDescriber#describe(InputStream, IContentDescription)
	 */
	public int describe(InputStream contents, IContentDescription description) throws IOException {
		// call the basic XML describer to do basic recognition
		if (super.describe(contents, description) == INVALID) {
			return INVALID;
		}
		// super.describe will have consumed some chars, need to rewind		
		contents.reset();
		// Check to see if we matched our criteria.		
		return checkCriteria(new InputSource(contents));
	}

	/* (Intentionally not included in javadoc)
	 * @see IContentDescriber#describe(Reader, IContentDescription)
	 */
	public int describe(Reader contents, IContentDescription description) throws IOException {
		// call the basic XML describer to do basic recognition
		if (super.describe(contents, description) == INVALID) {
			return INVALID;
		}
		// super.describe will have consumed some chars, need to rewind
		contents.reset();
		// Check to see if we matched our criteria.
		return checkCriteria(new InputSource(contents));
	}

	public void setInitializationData(IConfigurationElement arg0, String arg1,
			Object arg2) throws CoreException {
		// empty is ok
	}

}
