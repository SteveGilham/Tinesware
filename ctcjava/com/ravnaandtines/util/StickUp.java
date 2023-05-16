//Title:        CTC2.0 for Java
//Version:
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.util;

import java.awt.Dialog;
import java.awt.Frame;
import java11.awt.event.*;
import java.awt.event.*;
import dtai.gwt.*;

/**
 *  Class StickUp 
 *
 *  Generic modal dialog box utility class for warnings and such
 *  Puts a Label's worth of text up, along with some decorations
 *  and buttons to act on the information.
 *
 *  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1997
 *  All rights reserved.  For full licence details see file Main.java
 *
 * @author Mr. Tines
 * @version 1.0 31-Jul-1997
 * @version 1.1 1-Dec-1997 misc tidying
 * @version 1.2 8-May-1998 Java 1.1, tidying, CTC
 *
 */

public class StickUp extends Dialog implements java11.awt.event.ActionListener{
    public static final int WARN = 1;
    java.util.ResourceBundle res = java.util.ResourceBundle.getBundle("com.ravnaandtines.util.Res");
    public static final int QUERY = 2;
    public static final int INFO = 3;
    public static final int ERROR = 4;

    private ButtonGadget[] buttons = null;
    private GadgetPanel body = new GadgetPanel();
    private int type;
    private Frame owner = null;
    private PanelGadget innerP = null;
    private boolean hidden = false;
    private int result = -1;

    private void setType(int TYPE)
    {
        type = TYPE;
        if(TYPE == INFO) {
            setTitle(res.getString("INFORMATION"));
        }
        else if(TYPE == QUERY) {
            setTitle(res.getString("QUERY"));
        }
        else if(TYPE == ERROR) {
            setTitle(res.getString("ERROR"));
        }
        else {
            type = WARN; 
            setTitle(res.getString("WARNING"));
        }
    }

    /**
     * Constructor for a 1-button stickup.  Does *not* call show()
     * The calling code can do that after adjusting the title as required.
     * @param parent Frame affected
     * @param TYPE int value indicating what sort of dialog (defaults to WARN)
     * @param message String saying what's up e.g. "File could not be opened."
     * @param label String to put on the button e.g. "OK"
     * @param effect Actor to handle pushing the button.  Default is to dismiss.
     */
    public StickUp(Frame parent, int TYPE, 
    String message, String labelText)
    {
        super(parent, true);
        owner = parent;
        setType(TYPE);

        buttons = new ButtonGadget[1];
        buttons[0] = new ButtonGadget(labelText);
        buttons[0].addActionListener(this);
        decorate(message);
    }

    /**
     * Constructor for simplest possible a 1-button stickup.  Does *not* call show()
     * The calling code can do that after adjusting the title as required.
     * @param parent Frame affected
     * @param message String saying what's up e.g. "File could not be opened."
     */
    public StickUp(Frame parent, String message)
    {
        this(parent, WARN, message,
        java.util.ResourceBundle.getBundle("com.ravnaandtines.util.Res").getString("OK"));
    }

    /**
     * Constructor for a multi-button stickup.  Does *not* call show()
     * The calling code can do that after adjusting the title as required.
     * @param parent Frame affected
     * @param TYPE int value indicating what sort of dialog (defaults to WARN)
     * @param message String saying what's up e.g. "File could not be opened."
     * @param label String[] to put on the buttons e.g. "YES","NO"
     *              label.length many buttons are created.
     */
    public StickUp(Frame parent, int TYPE,
    String message, String[] labelText)
    {
        super(parent, true);
        owner = parent;

        setType(TYPE);
        buttons = new ButtonGadget[labelText.length];

        for(int i=0; i<labelText.length; ++i)
        {
            buttons[i] = new ButtonGadget(labelText[i]);
            buttons[i].addActionListener(this);
        }
        decorate(message);
    }

    /**
     * Put the buttons and the other stuff on.
     * @param message String containing purpose of dialog
     */
    private void decorate(String message)
    {
        setLayout(new java.awt.BorderLayout());
        add("Center", body);
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent e)
            {
                result = -1;
                setVisible(false);
                hidden = true;
            }
        }
        );

        GadgetGridBagLayout gb = new GadgetGridBagLayout();
        GadgetGridBagConstraints gbc = new GadgetGridBagConstraints();
        body.setLayout(gb);

        gbc.gridwidth = GadgetGridBagConstraints.REMAINDER;
        gbc.gridx = 0;

        // Type-pseudographic
        gbc.gridheight = 6;
        gbc.anchor = GadgetGridBagConstraints.NORTHWEST;
        gbc.ipady = 2;
        LabelGadget l = null;
        if(type == INFO) l = new LabelGadget("i");
        else if(type == QUERY) l = new LabelGadget("?");
        else l = new LabelGadget("!");
        java.awt.Font f = l.getFont();

        if(f == null)
        {
            String fn = java.awt.Toolkit.getDefaultToolkit().getFontList()[0];
            l.setFont(new java.awt.Font(fn, java.awt.Font.BOLD, 24));
        }
        else
        {
            l.setFont(new java.awt.Font(f.getName(),
            java.awt.Font.BOLD,
            3*f.getSize()
                ));
        }
        gb.setConstraints(l, gbc);
        body.add(l);

        // The message
        gbc.gridheight = 2;
        gbc.anchor = GadgetGridBagConstraints.CENTER;
        gbc.ipady = 7;
        innerP = new PanelGadget();
        innerP.setLayout(new GadgetGridLayout(0,1));
        innerP.add(new LabelGadget(message, LabelGadget.HORIZ_ALIGN_CENTER));
        gb.setConstraints(innerP, gbc);
        body.add(innerP);

        // The buttons
        gbc.ipady = 2;
        PanelGadget p = new PanelGadget();
        p.setLayout(new GadgetGridLayout(1,buttons.length+2,10,0));
        p.add (new LabelGadget("")); // keep buttons from edge of box
        for(int i=0; i<buttons.length; ++i)
        {
            p.add(buttons[i]);
        }
        p.add (new LabelGadget("")); // keep buttons from edge of box
        gb.setConstraints(p, gbc);
        body.add(p);

        gbc.gridheight = 1;
        gbc.ipady = 0;
        LabelGadget pad = new LabelGadget("");
        gb.setConstraints(pad,gbc);
        body.add(pad);

        java.awt.Point parentAt = owner.getLocation();
        java.awt.Dimension d = owner.getSize();

        pack();

        java.awt.Dimension d2 = getSize();
        setLocation(parentAt.x+(d.width-d2.width)/2,
        parentAt.y+(d.height-d2.height)/2);
        setResizable(false);
    }

    /**
     * Associates a component with the message
     * @param c Component to add
     */
    public void addInnerComponent(Gadget c)
    {
        innerP.add(c);
        pack();
    }

    /**
     * default method to dismiss the StickUp
     * @param b Component that caused the event now being served
     */
    public void actionPerformed(java11.awt.event.ActionEvent e)
    {
        for(int i=0; i<buttons.length; ++i)
        {
            if(e.getSource()==buttons[i])
            {
                result = i;
                break;
            }
        }
        setVisible(false);
        hidden = true;
    }

    /**
     * default method to test dismissal of the StickUp
     * @return boolean done value
     */
    public boolean done()
    {
        return hidden;
    }


    /**
     * Return the nth button - useful to help an Actor
     * figure out what has been signalled
     * @param n int index of Button to inspect
     * @return Button of that index, or null if out of range
     */
    public ButtonGadget getButton(int n)
    {
        return (n>=0 && n < buttons.length) ? buttons[n] : null;
    }

    /**
     * Return the result - figure out what has been signalled
     * @return Button index, or -1 if out of range
     */
    public int getResult()
    {
        return result;
    }
}

/* end of file StickUp.java */

