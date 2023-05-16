
//Title:      CTC2.0 for Java
//Version:    
//Copyright:  Copyright (c) 1997
//Author:     Mr. TInes
//Company:    Ravna & Tines
//Description:Free World Freeware 
//Public key encryption
package com.ravnaandtines.ctcjava;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class CTC {

    private CTC() {
    }

    private static String[] invokedWith = null;
    public static String[] getArgs()
    {
        return invokedWith;
    }
    
    //Main method
    public static void main(String[] args) {
        invokedWith = args;
        SplashWindow.splash(CTC.class.getResource("/com/ravnaandtines/ctcjava/splash.png"));
        SplashWindow.invokeInitialiser("com.ravnaandtines.ctcjava.GlobalData");
        SplashWindow.disposeSplash();
    }

    private static native void registerJVM();

    static {
        System.loadLibrary("ctcjlib");
        registerJVM();
    }
}

/*
 * @(#)SplashWindow.java  2.2  2005-04-03
 *
 * Copyright (c) 2003-2005 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * This software is in the public domain.
 */


/**
 * <a href="http://www.randelshofer.ch/oop/javasplash/javasplash.html">A Splash window</a>.
 *  <p>
 * Usage: MyApplication is your application class. Create a Splasher class which
 * opens the splash window, invokes the main method of your Application class,
 * and disposes the splash window afterwards.
 * Please note that we want to keep the Splasher class and the SplashWindow class
 * as small as possible. The less code and the less classes must be loaded into
 * the JVM to open the splash screen, the faster it will appear.
 * <pre>
 * class Splasher {
 *    public static void main(String[] args) {
 *         SplashWindow.splash(Startup.class.getResource("splash.gif"));
 *         MyApplication.main(args);
 *         SplashWindow.disposeSplash();
 *    }
 * }
 * </pre>
 *
 * Special-cased for CTCJava; note also this class <strong>must</strong>
 * sub-class window as it hooks into the update/repaint API.
 *
 * @author  Werner Randelshofer
 * @version 2.1 2005-04-03 Revised.
 */

class SplashWindow extends Window
{
    public static void invokeInitialiser(String className) {
        try {
            Class.forName(className)
            .getMethod("initialiseApplication", new Class[] {})
            .invoke(null, new Object[] {});
        } catch (Exception e) {
            InternalError error =
                    new InternalError("Failed to invoke GlobalData.initialiseApplication method");
            error.initCause(e);
            throw error;
        }
    }
    
    /**
     * The current instance of the splash window.
     * (Singleton design pattern).
     */
    private static SplashWindow instance;
    
    /**
     * The splash image which is displayed on the splash window.
     */
    private Image image;
    
    /**
     * This attribute indicates whether the method
     * paint(Graphics) has been called at least once since the
     * construction of this window.<br>
     * This attribute is used to notify method splash(Image)
     * that the window has been drawn at least once
     * by the AWT event dispatcher thread.<br>
     * This attribute acts like a latch. Once set to true,
     * it will never be changed back to false again.
     *
     * @see #paint
     * @see #splash
     */
    private boolean paintCalled = false;
    
    
    /**
     * Creates a new instance.
     * @param parent the parent of the window.
     * @param image the splash image.
     */
    private SplashWindow(Frame parent, Image image) {
        super(parent);
        this.image = image;

        // Load the image
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(image,0);
        try {
            mt.waitForID(0);
        } catch(InterruptedException ie){}
        
        // Center the window on the screen
        int imgWidth = image.getWidth(this);
        int imgHeight = image.getHeight(this);
        setSize(imgWidth, imgHeight);
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(
        (screenDim.width - imgWidth) / 2,
        (screenDim.height - imgHeight) / 2
        );
        
        // Users shall be able to close the splash window by
        // clicking on its display area. This mouse listener
        // listens for mouse clicks and disposes the splash window.
        MouseAdapter disposeOnClick = new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                // Note: To avoid that method splash hangs, we
                // must set paintCalled to true and call notifyAll.
                // This is necessary because the mouse click may
                // occur before the contents of the window
                // has been painted.
                synchronized(SplashWindow.this) {
                    SplashWindow.this.paintCalled = true;
                    SplashWindow.this.notifyAll();
                }
                SplashWindow.this.dispose();
            }
        };
        addMouseListener(disposeOnClick);
    }
    
    /**
     * Updates the display area of the window.
     */
    public void update(Graphics g) {
        // Note: Since the paint method is going to draw an
        // image that covers the complete area of the component we
        // do not fill the component with its background color
        // here. This avoids flickering.
        paint(g);
    }
    /**
     * Paints the image on the window.
     */
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, this);
        g.drawRect(0,0, getSize().width-1, getSize().height-1);
        g.drawString("Copyright \u00A9 1995\u20132006 Mr.Tines", 217, 245);
        g.drawString("Version 2.0", 14, 50);
        
        // Notify method splash that the window
        // has been painted.
        // Note: To improve performance we do not enter
        // the synchronized block unless we have to.
        if (! paintCalled) {
            paintCalled = true;
            synchronized (this) { notifyAll(); }
        }
    }
    
    /**
     * Open's a splash window using the specified image.
     * @param image The splash image.
     */
    public static void splash(Image image) {
        if (instance == null && image != null) {
            Frame f = new Frame();
            
            // Create the splash image
            instance = new SplashWindow(f, image);
            
            // Show the window.
            instance.setVisible(true);
            
            // Note: To make sure the user gets a chance to see the
            // splash window we wait until its paint method has been
            // called at least once by the AWT event dispatcher thread.
            // If more than one processor is available, we don't wait,
            // and maximize CPU throughput instead.
            if (! EventQueue.isDispatchThread() 
            && Runtime.getRuntime().availableProcessors() == 1) {
                synchronized (instance) {
                    while (! instance.paintCalled) {
                        try { instance.wait(); } catch (InterruptedException e) {}
                    }
                }
            }
        }
    }
    /**
     * Open's a splash window using the specified image.
     * @param imageURL The url of the splash image.
     */
    public static void splash(URL imageURL) {
        if (imageURL != null) {
            splash(Toolkit.getDefaultToolkit().createImage(imageURL));
        }
    }
    
    /**
     * Closes the splash window.
     */
    public static void disposeSplash() {
        if (instance != null) {
            instance.getOwner().dispose();
            instance = null;
        }
    }
    
}
