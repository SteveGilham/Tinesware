
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;

import dtai.gwt.*;
import java11.awt.event.*;
import java.awt.FileDialog;

public class CJConfig extends PanelGadget {
    ButtonGadget browse = new ButtonGadget();
    java.util.ResourceBundle res = java.util.ResourceBundle.getBundle("com.ravnaandtines.ctcjava.Res");
    GadgetBorderLayout borderLayout1 = new GadgetBorderLayout();
    GadgetFlowLayout flowLayout1 = new GadgetFlowLayout();
    PanelGadget holder = new PanelGadget();
    FileDialog configFiler = null;
    CTCJMainFrame parent = null;
    PanelGadget bevelPanel1 = new PanelGadget();
    GadgetGridLayout gridLayout1 = new GadgetGridLayout(6,1);
    LabelGadget label1 = new LabelGadget();
    LabelGadget label2 = new LabelGadget();
    LabelGadget label3 = new LabelGadget();
    LabelGadget label4 = new LabelGadget();
    LabelGadget label5 = new LabelGadget();
    LabelGadget label6 = new LabelGadget();

    public CJConfig() {
        try {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public CJConfig(CTCJMainFrame p) {
        this();
        parent = p;
        configFiler = new FileDialog(parent,
        res.getString("Load_config_file"), FileDialog.LOAD);
    }

    public void jbInit() throws Exception{
        browse.setLabel(res.getString("Browse_"));
        browse.addActionListener(new CJConfig_browse_actionAdapter(this));
        bevelPanel1.setLayout(gridLayout1);
        this.setLayout(borderLayout1);
        this.add("South", holder);
        holder.setLayout(flowLayout1);
        holder.add(browse);
        this.add("Center", bevelPanel1);
        bevelPanel1.add(label1);
        bevelPanel1.add(label2);
        bevelPanel1.add(label3);
        bevelPanel1.add(label4);
        bevelPanel1.add(label5);
        bevelPanel1.add(label6);
        label5.setLabel(res.getString("Please_select_your"));
        label6.setLabel(res.getString("If_the_file_does_not"));
        updateText();
        if(null == CJGlobals.mainFrame) return;
        configFiler = new FileDialog(CJGlobals.mainFrame,
        res.getString("Load_config_file"), FileDialog.LOAD);
    }

    void browse_actionPerformed(ActionEvent e) {
        if(null == configFiler) configFiler = new FileDialog(CJGlobals.mainFrame,
        res.getString("Load_config_file"), FileDialog.LOAD);

        configFiler.setFile(CJGlobals.config);
        configFiler.show();  // does block, thank goodness
        String dir = configFiler.getDirectory();
        CJGlobals.config = configFiler.getFile();
        if(null == CJGlobals.config || null == dir) CJGlobals.config = "";
        else CJGlobals.config = dir + CJGlobals.config;

        if(CJGlobals.stubFile(CJGlobals.config))
        {
            parent.doUsualTabs();
        }
        else updateText();
    }

    private void updateText()
    {
        if(CJGlobals.config.length() == 0)
        {
            label1.setLabel(
            res.getString("This_program_expects"));
            label3.setLabel(res.getString("Syntax:_java_ctcjava"));
        }
        else
        {
            label1.setLabel(
            res.getString("Read_write_access"));
            label3.setLabel(res.getString("Filename:")+CJGlobals.config);
        }
    }
}

class CJConfig_browse_actionAdapter implements java11.awt.event.ActionListener{
    CJConfig adaptee;

    CJConfig_browse_actionAdapter(CJConfig adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.browse_actionPerformed(e);
    }
}

