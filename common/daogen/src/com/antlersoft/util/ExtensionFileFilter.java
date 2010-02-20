package com.antlersoft.util;

import java.io.File;

import java.util.ArrayList;
import java.util.Iterator;

public class ExtensionFileFilter extends javax.swing.filechooser.FileFilter {

    private String m_description;
    private ArrayList m_extensions;
    private boolean m_accepts_directories;

    public ExtensionFileFilter( String description)
    {
        m_description=description;
        m_extensions=new ArrayList();
        m_accepts_directories=true;
    }
    public boolean accept(File candidate) {
        if ( candidate.isDirectory() && m_accepts_directories)
            return true;
        String extension=candidate.getName();
        int offset=extension.lastIndexOf('.');
        if ( offset!= -1)
            extension=extension.substring( offset+1);
        for ( Iterator i=m_extensions.iterator(); i.hasNext();)
            if ( i.next().equals(extension))
                return true;
        return false;
    }
    public boolean getAcceptsDirectories() {
        return m_accepts_directories;
    }
    public String getDescription() {
        return m_description;
    }
    public void setAcceptsDirectories( boolean accepts_directories) {
        m_accepts_directories=accepts_directories;
    }
    public void addExtension( String to_add)
    {
        m_extensions.add( to_add);
    }
    public void removeExtension( String to_remove)
    {
        m_extensions.remove( to_remove);
    }
    public void removeAllExtensions()
    {
        m_extensions.clear();
    }
}