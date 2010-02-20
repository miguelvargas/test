package com.antlersoft.util;

import java.awt.event.*;

import java.io.IOException;

import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

/**
 * <p>Title: Warroom</p>
 * <p>Description: Whiteboarding for WWIIOL</p>
 * <p>Copyright (c) 2003-2006  Michael A. MacDonald<p>
 * ----- - - -- - - --
 * <p>
 *     This package is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 * <p>
 *     This package is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * <p>
 *     You should have received a copy of the GNU General Public License
 *     along with the package (see gpl.txt); if not, see www.gnu.org
 * <p>
 * ----- - - -- - - --
 * <p>Company: antlersoft</p>
 * @author Michael MacDonald
 * @version 1.0
 */

public class URLFrame extends JFrame
{
    public URLFrame( URL url)
    {
        JEditorPane editor;
        try
        {
            editor=new JEditorPane( url);
        }
        catch ( IOException ioe)
        {
            editor=new JEditorPane( "text/plain",
                "Unable to open page "+url.toString()+"\r\n\r\n"+
                ioe.getMessage());
        }
        editor.setEditable( false);
        getContentPane().add( new JScrollPane( editor));
        addWindowListener( new WindowAdapter() {
                public void windowClosing( WindowEvent e)
                {
                    dispose();
                }
            });
    }
}