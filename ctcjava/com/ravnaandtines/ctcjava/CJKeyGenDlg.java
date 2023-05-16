
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware

package com.ravnaandtines.ctcjava;

import java.awt.Dialog;
import java.awt.Frame;
import dtai.gwt.*;
import java11.awt.event.*;

public class CJKeyGenDlg extends Dialog
{
    GadgetPanel bodyPanel = new GadgetPanel();
    java.util.ResourceBundle res = java.util.ResourceBundle.getBundle("com.ravnaandtines.ctcjava.Res");
    GadgetBorderLayout borderLayout1 = new GadgetBorderLayout();
    PanelGadget buttonBar = new PanelGadget();
    GadgetGridLayout gridLayout1 = new GadgetGridLayout(7,1);
    ButtonGadget OK = new ButtonGadget();
    LabelGadget label1 = new LabelGadget();
    ButtonGadget Cancel = new ButtonGadget();
    LabelGadget label2 = new LabelGadget();
    PanelGadget mainPanel = new PanelGadget();
    GadgetBorderLayout borderLayout2 = new GadgetBorderLayout();
    GroupBox uidPanel = new GroupBox(res.getString("User_ID"));
    TextFieldGadget uid = new TextFieldGadget();
    GadgetBorderLayout borderLayout3 = new GadgetBorderLayout();
    GroupBox algPanel = new GroupBox(res.getString("Key_algorithm_and"));
    GadgetGridLayout gridLayout2 = new GadgetGridLayout(5,1);
    CheckboxGadgetGroup keyAlgGroup = new CheckboxGadgetGroup();
    CheckboxGadget rsa1024 = new CheckboxGadget("",false,keyAlgGroup);
    CheckboxGadget rsa2000 = new CheckboxGadget("",false,keyAlgGroup);
    CheckboxGadget rsa2048 = new CheckboxGadget("",false,keyAlgGroup);
    CheckboxGadget rsa4096 = new CheckboxGadget("",false,keyAlgGroup);
    CheckboxGadget pegwit8 = new CheckboxGadget("",false,keyAlgGroup);
    boolean accept = false;
    Frame parent = null;


    public CJKeyGenDlg(Frame frame, String title, boolean modal)
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


    public CJKeyGenDlg(Frame frame)
    {
        this(frame, "", false);
    }


    public CJKeyGenDlg(Frame frame, boolean modal)
    {
        this(frame, "", modal);
    }


    public CJKeyGenDlg(Frame frame, String title)
    {
        this(frame, title, false);
    }

    void jbInit() throws Exception
    {
        bodyPanel.setLayout(borderLayout1);
        buttonBar.setLayout(gridLayout1);
        OK.setLabel(res.getString("OK"));
        OK.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                OK_actionPerformed(e);
            }
        }
        );
        Cancel.setLabel(res.getString("Cancel"));
        Cancel.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                Cancel_actionPerformed(e);
            }
        }
        );
        uidPanel.setLayout(borderLayout3);
        rsa1024.setState(true);
        rsa1024.setLabel(res.getString("RSA_1024_bits_(2_6)"));
        rsa2000.setLabel(res.getString("RSA_2000_bits_(some_2"));
        rsa2048.setLabel(res.getString("RSA_2048_bits_(some_2"));
        rsa4096.setLabel(res.getString("RSA_4096_bits"));
        pegwit8.setLabel(res.getString("Eliiptic_curve"));

        /* GF(2^Composite) has been broken - disallow it */
        pegwit8.setVisible(false);

        algPanel.setLayout(gridLayout2);
        mainPanel.setLayout(borderLayout2);
        bodyPanel.add("East", buttonBar);
        buttonBar.add(label2);
        buttonBar.add(OK);
        buttonBar.add(label1);
        buttonBar.add(Cancel);
        bodyPanel.add("Center", mainPanel);
        mainPanel.add("North", uidPanel);
        uidPanel.add("Center", uid);
        mainPanel.add("Center", algPanel);
        algPanel.add(rsa1024);
        algPanel.add(rsa2000);
        algPanel.add(rsa2048);
        algPanel.add(rsa4096);
        algPanel.add(pegwit8);
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

    void Cancel_actionPerformed(ActionEvent e)
    {
        cancel();
    }
}

