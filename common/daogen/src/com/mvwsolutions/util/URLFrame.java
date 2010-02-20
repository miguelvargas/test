package com.mvwsolutions.util;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

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