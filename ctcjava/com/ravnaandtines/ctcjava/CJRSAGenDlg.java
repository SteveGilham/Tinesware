
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware

package com.ravnaandtines.ctcjava;

import java.awt.Dialog;
import java.awt.Frame;
import java11.awt.event.*;
import dtai.gwt.*;

public class CJRSAGenDlg extends Dialog
{
    GadgetPanel bodyPanel = new GadgetPanel();
    java.util.ResourceBundle res = java.util.ResourceBundle.getBundle("com.ravnaandtines.ctcjava.Res");
    GroupBox keyType = new GroupBox(res.getString("Key_type"));
    GroupBox primeGen = new GroupBox(res.getString("Prime_generation"));
    PanelGadget buttons = new PanelGadget();
    GadgetGridLayout gridLayout1 = new GadgetGridLayout(1,3);
    GadgetGridLayout gridLayout2 = new GadgetGridLayout(3,1);
    GadgetGridLayout gridLayout3 = new GadgetGridLayout(3,1);
    GadgetGridLayout gridLayout4 = new GadgetGridLayout(3,1);
    CheckboxGadgetGroup keyTypeGroup = new CheckboxGadgetGroup();
    CheckboxGadgetGroup primeGenGroup = new CheckboxGadgetGroup();
    CheckboxGadget dualuse = new CheckboxGadget("",false,keyTypeGroup);
    CheckboxGadget encrypt = new CheckboxGadget("",false,keyTypeGroup);
    CheckboxGadget sign = new CheckboxGadget("",false,keyTypeGroup);
    CheckboxGadget simple = new CheckboxGadget("",false,primeGenGroup);
    CheckboxGadget jump = new CheckboxGadget("",false,primeGenGroup);
    CheckboxGadget sophie = new CheckboxGadget("",false,primeGenGroup);
    ButtonGadget OK = new ButtonGadget();
    LabelGadget label1 = new LabelGadget();
    ButtonGadget cancel = new ButtonGadget();
    boolean accept = false;
    Frame parent = null;


    public CJRSAGenDlg(Frame frame, String title, boolean modal)
    {
        super(frame, title, modal);
        parent = frame;
        enableEvents(java.awt.AWTEvent.WINDOW_EVENT_MASK);
        try 
        {
            jbInit();
            add(bodyPanel);
            pack();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    public CJRSAGenDlg(Frame frame)
    {
        this(frame, "", false);
    }


    public CJRSAGenDlg(Frame frame, boolean modal)
    {
        this(frame, "", modal);
    }


    public CJRSAGenDlg(Frame frame, String title)
    {
        this(frame, title, false);
    }

    void jbInit() throws Exception
    {
        keyType.setLayout(gridLayout2);
        dualuse.setState(true);
        dualuse.setLabel(res.getString("Encrypt_Sign_(2_6)"));
        encrypt.setLabel(res.getString("Encrypt_Only_(4_0)"));
        sign.setLabel(res.getString("Sign_Only_(4_0)"));

        simple.setLabel(res.getString("Simple_scan_(faster)"));
        jump.setLabel(res.getString("Jump_scan"));
        sophie.setLabel(res.getString("Sophie_Germain"));
        simple.setState(true);

        OK.setLabel(res.getString("OK"));
        OK.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                OK_actionPerformed(e);
            }
        }
        );
        cancel.setLabel(res.getString("Cancel"));
        cancel.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                cancel_actionPerformed(e);
            }
        }
        );
        buttons.setLayout(gridLayout4);
        primeGen.setLayout(gridLayout3);
        bodyPanel.setLayout(gridLayout1);
        bodyPanel.add(keyType);
        keyType.add(dualuse);
        keyType.add(encrypt);
        keyType.add(sign);
        bodyPanel.add(primeGen);
        primeGen.add(simple);
        primeGen.add(jump);
        primeGen.add(sophie);
        bodyPanel.add(buttons);
        buttons.add(OK);
        buttons.add(label1);
        buttons.add(cancel);
    }

    public void show()
    {
        java.awt.Point parentAt = parent.getLocation();
        java.awt.Dimension d = parent.getSize();

        pack();

        java.awt.Dimension d2 = getSize();
        setLocation(parentAt.x+(d.width-d2.width)/2,
        parentAt.y+(d.height-d2.height)/2);
        setResizable(false);
        super.show();
    }

    protected void processWindowEvent(java.awt.event.WindowEvent e)
    {
        if (e.getID() == WindowEvent.WINDOW_CLOSING)
        {
            cancel();
        }
        super.processWindowEvent(e);
    }

    void cancel()
    {
        dispose();
    }

    void OK_actionPerformed(ActionEvent e)
    {
        accept = true;
        setVisible(false);
        cancel();
    }

    void cancel_actionPerformed(ActionEvent e)
    {
        cancel();
    }
}


