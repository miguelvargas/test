/**
 * Copyright (C) 2009 Michael A. MacDonald
 */
package com.antlersoft.sqlitegen;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

import com.antlersoft.android.dbgen.SourceInterface;

/**
 * @author Michael A. MacDonald
 *
 */
public class PluginSourceInterface implements SourceInterface {
	
	private ArrayList<IPackageFragmentRoot> sourceRoots;
	IProgressMonitor monitor;
	
	class PluginSourceWriter extends PrintWriter
	{
		private StringWriter stringWriter;
		private IPackageFragment fragment;
		private String className;
		PluginSourceWriter(StringWriter sw, IPackageFragment frag, String name)
		{
			super(sw);
			stringWriter=sw;
			fragment=frag;
			className=name;
		}
		
		void done()
		throws SIException
		{
			close();
			try
			{
				fragment.createCompilationUnit(className+".java", stringWriter.toString(), true, monitor);
			}
			catch ( JavaModelException jme)
			{
				throw new SIException("Failed to create class "+className, jme);
			}
		}
	}
	
	public PluginSourceInterface(IPackageFragmentRoot[] projectRoots, IProgressMonitor mon)
		throws JavaModelException
	{
		monitor=mon;
		sourceRoots=new ArrayList<IPackageFragmentRoot>();
		for ( IPackageFragmentRoot root : projectRoots)
		{
			if ( root.getKind() == IPackageFragmentRoot.K_SOURCE )
			{
				sourceRoots.add(root);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.antlersoft.android.dbgen.SourceInterface#doneWithWriter(java.io.PrintWriter)
	 */
	public void doneWithWriter(PrintWriter w) throws IOException, SIException {
		((PluginSourceWriter)w).done();
	}

	/* (non-Javadoc)
	 * @see com.antlersoft.android.dbgen.SourceInterface#getWriterForClass(java.lang.String, java.lang.String)
	 */
	public PrintWriter getWriterForClass(String packageName, String className)
			throws IOException, SIException {
		for ( IPackageFragmentRoot root : sourceRoots)
		{
			try
			{
				if (root.getKind() == IPackageFragmentRoot.K_SOURCE)
				{
					if (root.getElementName().equals("gen"))
					{
						IPackageFragment fragment=root.getPackageFragment(packageName);
						return new PluginSourceWriter(new StringWriter(), fragment, className);
					}
				}
			}
			catch (JavaModelException jme)
			{
				
			}
		}
		for ( IPackageFragmentRoot root : sourceRoots)
		{
			IPackageFragment fragment=root.getPackageFragment(packageName);
			try
			{
				if (fragment.containsJavaResources())
				{
					return new PluginSourceWriter(new StringWriter(), fragment, className);
				}
			}
			catch (JavaModelException jme)
			{
				// Fragment doesn't actually exist in this root; no problem, try next root
			}
		}
		throw new SIException("No package "+packageName+" found in project");
	}

}
