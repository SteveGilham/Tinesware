
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware

package com.ravnaandtines.ctcjava;

import java.awt.Dialog;
import java11.awt.event.*;
import java.awt.Frame;
import dtai.gwt.*;

public class CJKeyProtDlg extends Dialog
{
    GadgetPanel bodyPanel = new GadgetPanel();
    java.util.ResourceBundle res = java.util.ResourceBundle.getBundle("com.ravnaandtines.ctcjava.Res");
    GroupBox phrases = new GroupBox(res.getString("Passphrase_and"));
    GroupBox algPanel = new GroupBox(res.getString("Protecting_encryption"));
    PanelGadget minorPanel = new PanelGadget();
    GadgetGridLayout gridLayout1 = new GadgetGridLayout(3,1);
    GadgetGridLayout gridLayout2 = new GadgetGridLayout(2,1);
    CJGetPassphrase pphrase1 = new CJGetPassphrase();
    CJGetPassphrase pphrase2 = new CJGetPassphrase();
    GadgetGridLayout gridLayout3 = new GadgetGridLayout(2,1);
    GadgetGridLayout gridLayout4 = new GadgetGridLayout(4,2);
    CheckboxGadgetGroup kpalgGroup = new CheckboxGadgetGroup();
    CheckboxGadget idea     = new CheckboxGadget("",false,kpalgGroup);
    CheckboxGadget blow16   = new CheckboxGadget("",false,kpalgGroup);
    CheckboxGadget TDES     = new CheckboxGadget("",false,kpalgGroup);
    CheckboxGadget rijndael = new CheckboxGadget("",false,kpalgGroup);
    CheckboxGadget cast5    = new CheckboxGadget("",false,kpalgGroup);
    CheckboxGadget TEA      = new CheckboxGadget("",false,kpalgGroup);
    CheckboxGadget tblow5   = new CheckboxGadget("",false,kpalgGroup);
    CheckboxGadget tway     = new CheckboxGadget("",false,kpalgGroup);
    CheckboxGadgetGroup hashAlgGroup = new CheckboxGadgetGroup();
    GroupBox hashes = new GroupBox(res.getString("Self_signature_hash"));
    BorderGadget buttons = new BorderGadget();
    GadgetGridLayout gridLayout5 = new GadgetGridLayout(1,5);
    GadgetGridLayout gridLayout6 = new GadgetGridLayout(1,2);
    CheckboxGadget md5 = new CheckboxGadget("",true,hashAlgGroup);
    CheckboxGadget sha = new CheckboxGadget("",false,hashAlgGroup);
    LabelGadget label1 = new LabelGadget();
    ButtonGadget Cancel = new ButtonGadget();
    LabelGadget label2 = new LabelGadget();
    ButtonGadget OK = new ButtonGadget();
    LabelGadget label3 = new LabelGadget();
    Frame parent = null;
    boolean accept = false;


    public CJKeyProtDlg(Frame frame, String title, boolean modal)
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


    public CJKeyProtDlg(Frame frame)
    {
        this(frame, "", false);
    }


    public CJKeyProtDlg(Frame frame, boolean modal)
    {
        this(frame, "", modal);
    }


    public CJKeyProtDlg(Frame frame, String title)
    {
        this(frame, title, false);
    }

    void jbInit() throws Exception
    {
        bodyPanel.setLayout(gridLayout1);
        phrases.setLayout(gridLayout2);
        minorPanel.setLayout(gridLayout3);
        idea.setLabel(res.getString("IDEA_128bit"));
        blow16.setLabel(res.getString("Blowfish_128bit"));
        TDES.setLabel(res.getString("TripleDES"));
        TDES.setEnabled(false);
        rijndael.setLabel(res.getString("AES128"));
        cast5.setLabel(res.getString("CAST_128bit"));
        TEA.setLabel(res.getString("TEA_128bit"));
        tblow5.setLabel(res.getString("Triple_Blowfish_40bit"));
        //tblow5.setEnabled(false);
        tway.setLabel(res.getString("TWAY_96bit"));
        md5.setLabel(res.getString("MD5_128bit"));
        sha.setLabel(res.getString("SHA1_160bit"));
        Cancel.setLabel(res.getString("Cancel"));
        Cancel.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                Cancel_actionPerformed(e);
            }
        }
        );
        OK.setLabel(res.getString("OK"));
        OK.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                OK_actionPerformed(e);
            }
        }
        );
        hashes.setLayout(gridLayout6);
        buttons.setLayout(gridLayout5);
        algPanel.setLayout(gridLayout4);
        bodyPanel.add(phrases);
        phrases.add(pphrase1);
        phrases.add(pphrase2);
        bodyPanel.add(algPanel);
        algPanel.add(idea);
        algPanel.add(cast5);
        algPanel.add(blow16);
        //algPanel.add(TDES);
        algPanel.add(rijndael);
        algPanel.add(TEA);
        //algPanel.add(tblow5);
        algPanel.add(tway);// - problem with resetting passphrase
        bodyPanel.add(minorPanel);
        minorPanel.add(hashes);
        hashes.add(md5);
        hashes.add(sha);
        minorPanel.add(buttons);
        buttons.add(label1);
        buttons.add(OK);
        buttons.add(label3);
        buttons.add(Cancel);
        buttons.add(label2);
        if(CTCIKeyConst.isIDEAenabled())
        {
            idea.setState(true);
        }
        else
        {
            cast5.setState(true);
            idea.setEnabled(false);
        }
        buttons.setMargins(5);
    }

    public void showSign(boolean b)
    {
        hashes.setVisible(b);
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
        if (e.getID() == java.awt.event.WindowEvent.WINDOW_CLOSING)
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


