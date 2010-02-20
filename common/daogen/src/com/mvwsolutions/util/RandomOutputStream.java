package com.mvwsolutions.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RandomOutputStream extends ByteArrayOutputStream
{
	private static final int SIZE_INCREMENT=1024;

	public RandomOutputStream()
	{
		super( SIZE_INCREMENT);
	}

	public byte[] getWrittenBytes()
		throws IOException
	{
		byte[] retVal=toByteArray();
		reset();
		return retVal;
	}
}