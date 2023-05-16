
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;

import java.awt.Frame;
import java.awt.FileDialog;
import dtai.gwt.*;
import java11.awt.event.*;
import java.util.*;
import com.ravnaandtines.util.MessageBox;

public class CJFilesPanel extends PanelGadget {
    GadgetGridLayout gridLayout1 = new GadgetGridLayout(2,1);
    ResourceBundle res = ResourceBundle.getBundle("com.ravnaandtines.ctcjava.Res");
    GroupBox pubBox = new GroupBox(res.getString("Public_Keyring_File"));
    GroupBox secBox = new GroupBox(res.getString("Secret_Keyring_File"));
    TextFieldGadget pubField = new TextFieldGadget();
    TextFieldGadget secField = new TextFieldGadget();
    ButtonGadget pubButton = new ButtonGadget();
    ButtonGadget secButton = new ButtonGadget();
    LabelGadget secLabel = new LabelGadget(res.getString("Loaded"));
    LabelGadget pubLabel = new LabelGadget(res.getString("Loaded"));
    GadgetFlowLayout flowLayout2 = new GadgetFlowLayout();
    GadgetFlowLayout flowLayout3 = new GadgetFlowLayout();
    FileDialog filer = null;
    Frame parent;
    String pubfile, pubdir;
    String secfile, secdir;
    boolean pubValid = false;
    boolean secValid = false;

    public CJFilesPanel() {
        try {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CJFilesPanel(Frame f)
    {
        this();
        parent = f;
    }

    public void jbInit() throws Exception{
        pubBox.setLayout(flowLayout2);
        secBox.setLayout(flowLayout3);
        pubField.setEditable(false);
        pubField.setColumns(20);
        secField.setEditable(false);
        secField.setColumns(20);
        pubButton.addActionListener(new java11.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                pubButton_actionPerformed();
            }
        }
        );
        pubButton.setLabel(res.getString("Browse_"));
        secButton.addActionListener(new java11.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                secButton_actionPerformed();
            }
        }
        );
        secButton.setLabel(res.getString("Browse_"));

        this.setLayout(gridLayout1);
        this.add(pubBox);
        pubBox.add(pubField);
        pubBox.add(pubButton);
        this.add(secBox);
        secBox.add(secField);
        secBox.add(secButton);
    }

    public void loadConfig()
    {
        filer = new FileDialog(CJGlobals.mainFrame,res.getString("Open_keyring_"),FileDialog.LOAD);
        pubfile = CJGlobals.settings.getProperty("PublicKeyring", "");
        pubdir  = CJGlobals.settings.getProperty("PublicKeyringDirectory", "");
        pubField.setText(pubdir + pubfile);
        secfile = CJGlobals.settings.getProperty("SecretKeyring", "");
        secdir  = CJGlobals.settings.getProperty("SecretKeyringDirectory", "");
        secField.setText(secdir + secfile);
    }

    void pubButton_actionPerformed()
    {
        filer.setDirectory(pubdir);
        filer.setFile(pubfile);
        filer.show();
        String pd = filer.getDirectory();
        String pf = filer.getFile();
        if(pd != null && pf != null)
        {
            if(!CJGlobals.stubFile(pd+pf)) return;
            pubField.setText(pd+pf);
            CJGlobals.settings.put("PublicKeyringDirectory", pd);
            CJGlobals.settings.put("PublicKeyring", pf);
            pubdir = pd;
            pubfile = pf;
            invalidatePubkeys();
        }
    }

    void secButton_actionPerformed()
    {
        filer.setDirectory(secdir);
        filer.setFile(secfile);
        filer.show();
        String sd = filer.getDirectory();
        String sf = filer.getFile();
        if(sd != null && sf != null)
        {
            if(!CJGlobals.stubFile(sd+sf)) return;
            secField.setText(sd+sf);
            CJGlobals.settings.put("SecretKeyringDirectory", sd);
            CJGlobals.settings.put("SecretKeyring", sf);
            secdir = sd;
            secfile = sf;
            invalidateSeckeys();
        }
    }

    void loadPubkeys()
    {
        if(pubValid) return;
        if(0==pubdir.length() && 0==pubfile.length()) return;
        if(!CJGlobals.stubFile(pubdir+pubfile)) return;
        CJGlobals.decodeContext = readPubring(pubdir+pubfile);
        pubValid = (null != CJGlobals.decodeContext);
        if(pubValid)
        {
            pubBox.remove(pubButton);
            pubBox.add(pubLabel);
        }
    }

    void invalidatePubkeys()
    {
        CJGlobals.decodeContext = null;
        if(!clearKeyrings())
        {
            MessageBox messageBox = new MessageBox();
            messageBox.setFrame(CJGlobals.mainFrame);
            messageBox.setType(MessageBox.ERROR);
            messageBox.setMessage(res.getString("Changes_to_keyrings"));
            messageBox.setTitle(res.getString("CTC_Keyring_shutdown"));
            messageBox.show();
            System.exit(0);
        }
        if(pubValid)
        {
            pubBox.add(pubButton);
            pubBox.remove(pubLabel);
            pubValid = false;
        }
        if(secValid)
        {
            secBox.add(secButton);
            secBox.remove(secLabel);
            secValid = false;
        }
    }

    void loadSeckeys()
    {
        if(secValid) return;
        if(0==secdir.length() && 0==secfile.length()) return;
        if(!CJGlobals.stubFile(secdir+secfile)) return;
        loadPubkeys();
        if(pubValid)
        {
            secValid = readSecring(secdir+secfile);
        }
        if(secValid)
        {
            secBox.remove(secButton);
            secBox.add(secLabel);
        }
    }

    void invalidateSeckeys()
    {
        invalidatePubkeys();
        loadPubkeys();
    }

    private native CJdecodeContext readPubring(String filename);
    private native boolean readSecring(String filename);
    private native boolean clearKeyrings();
}

