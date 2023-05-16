
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;

import dtai.gwt.*;
import java11.awt.event.*;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;

public class CJGet1Passphrase extends Dialog {
    GadgetPanel panel1 = new GadgetPanel();
    java.util.ResourceBundle res = java.util.ResourceBundle.getBundle("com.ravnaandtines.ctcjava.Res");
    GadgetBorderLayout borderLayout1 = new GadgetBorderLayout();
    LabelGadget KeyName = new LabelGadget();
    PanelGadget panel2 = new PanelGadget();
    GadgetGridLayout gridLayout1 = new GadgetGridLayout(1,5);
    LabelGadget Caps = new LabelGadget();
    ButtonGadget OK = new ButtonGadget();
    ButtonGadget cancel = new ButtonGadget();
    LabelGadget label1 = new LabelGadget();
    String hardtext = res.getString("CAPS");
    String keyname = res.getString("Key:");
    CJGetPassphrase core = new CJGetPassphrase();
    CryptoString result = null;
    Frame parentFrame;

    public CJGet1Passphrase(Frame frame, String title, boolean modal) {
        super(frame, title, modal);
        try {
            jbInit();
            parentFrame = frame;
            addWindowListener(new java.awt.event.WindowAdapter()
            {
                  public void windowClosing(java.awt.event.WindowEvent e)
                  {
                      core.phrase.wipe();
                      result = null;
                      setVisible(false);
                      dispose();
                  }
            }
            );
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CJGet1Passphrase(Frame frame) {
        this(frame, false);
    }

    public CJGet1Passphrase(Frame frame, boolean modal) {
        this(frame, java.util.ResourceBundle.getBundle("com.ravnaandtines.ctcjava.Res").getString("Enter_passphrase"), modal);
    }

    public CJGet1Passphrase(Frame frame, String title) {
        this(frame, title, false);
    }

    public void show()
    {
        Point parentAt = parentFrame.getLocation();
        Dimension d = parentFrame.getSize();
        Dimension d2 = this.getSize();
        this.setLocation(parentAt.x+(d.width-d2.width)/2,
        parentAt.y+(d.height-d2.height)/2);
        this.setResizable(false);
        super.show();
    }

    private void jbInit() throws Exception{
        KeyName.setLabel(keyname);
        Caps.setLabel(hardtext);
        OK.setLabel(res.getString("OK"));
        OK.addActionListener(new CJGet1Passphrase_OK_actionAdapter(this));
        cancel.setLabel(res.getString("Cancel"));
        cancel.addActionListener(new CJGet1Passphrase_OK_actionAdapter(this));
        core.phrase.addActionListener(new CJGet1Passphrase_OK_actionAdapter(this));
        panel2.setLayout(gridLayout1);
        panel1.setLayout(borderLayout1);
        label1.setHorizAlign(LabelGadget.HORIZ_ALIGN_LEFT);
        panel1.add("North", KeyName);
        panel1.add("Center", core);
        panel1.add("South", panel2);
        panel2.add(Caps);
        Caps.setLabel(""); // should toggle on Caps-lock state
        panel2.add(OK);
        panel2.add(label1);
        panel2.add(cancel);
        panel2.add(new LabelGadget(""));
        this.add(panel1);
        this.pack();
    }

    public void setKeyname(String name)
    {
        KeyName.setLabel(keyname+name);
    }

    public void clearText()
    {
        core.clearText();
    }

    void OK_actionPerformed(ActionEvent e) {
        if (e.getSource() == OK || e.getSource() == core.phrase)
        {
            result = core.phrase.getCryptoText();
        }
        else
        {
            result = null;
        }
        core.phrase.wipe();
        setVisible(false);
        dispose();
    }
}

class CJGet1Passphrase_OK_actionAdapter implements java11.awt.event.ActionListener {
    CJGet1Passphrase adaptee;

    CJGet1Passphrase_OK_actionAdapter(CJGet1Passphrase adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.OK_actionPerformed(e);
    }
}
