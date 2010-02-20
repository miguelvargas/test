/**
 * Copyright (c) 2008 Michael A. MacDonald
 */
package com.mvwsolutions.classwriter;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * @author Michael A. MacDonald
 *
 */
public class RuntimeInvisibleParameterAnnotationsAttribute extends
		RuntimeVisibleParameterAnnotationsAttribute {

	public final static String typeString="RuntimeInvisibleParameterAnnotations";

	/**
	 * @param classStream
	 * @throws IOException
	 */
	public RuntimeInvisibleParameterAnnotationsAttribute(
			DataInputStream classStream) throws IOException {
		super(classStream);
	}

	/* (non-Javadoc)
	 * @see com.mvwsolutions.classwriter.Attribute#getTypeString()
	 */
	public String getTypeString() {
		return typeString;
	}
}
