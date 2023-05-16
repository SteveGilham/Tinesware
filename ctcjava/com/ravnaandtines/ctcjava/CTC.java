
//Title:      CTC2.0 for Java
//Version:    
//Copyright:  Copyright (c) 1997
//Author:     Mr. TInes
//Company:    Ravna & Tines
//Description:Free World Freeware 
//Public key encryption
package com.ravnaandtines.ctcjava;

import java.awt.Dimension;

public class CTC {
    boolean packFrame = false;

    //Construct the application
    public CTC() {
        CTCJMainFrame frame = new CTCJMainFrame();
        //Pack frames that have useful preferred size info, e.g. from their layout
        //Validate frames that have preset sizes
        if (packFrame)
            frame.pack();
        else
            frame.validate();
        //Center the window
        Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height)
            frameSize.height = screenSize.height;
        if (frameSize.width > screenSize.width)
            frameSize.width = screenSize.width;
        frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        frame.setVisible(true);
        frame.toFront();
    }

    //Main method
    public static void main(String[] args) {
        if(args.length > 0)
        {
            CJGlobals.config = args[0];
        }
        System.runFinalizersOnExit(true);
        new CTC();
    }

    private static native void registerJVM();

    static {
        System.loadLibrary("ctcjlib");
        registerJVM();
    }
}

