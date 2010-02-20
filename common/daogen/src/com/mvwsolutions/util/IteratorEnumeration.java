package com.mvwsolutions.util;

import java.util.Enumeration;
import java.util.Iterator;


/**
 * Create an Enumeration from an Iterator
 */
public class IteratorEnumeration implements Enumeration
{
    public IteratorEnumeration( Iterator i)
    {
        base=i;
    }

    public boolean hasMoreElements()
    {
        return base.hasNext();
    }

    public Object nextElement()
    {
        return base.next();
    }

    private Iterator base;
}