
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;

import dtai.gwt.*;
import java11.awt.event.*;

public class CJMiscPanel extends PanelGadget implements ItemListener{
    GadgetGridLayout gridLayoutx = new GadgetGridLayout(7,1);
    java.util.ResourceBundle res = java.util.ResourceBundle.getBundle("com.ravnaandtines.ctcjava.Res");

    PanelGadget panel0 = new PanelGadget();
    CheckboxGadget showChecksums = new CheckboxGadget(res.getString("Show_passphrase"));

    PanelGadget panel1 = new PanelGadget();
    LabelGadget label1 = new LabelGadget();
    TextFieldGadget quoteField = new TextFieldGadget();
    GadgetGridLayout gridLayout1 = new GadgetGridLayout(1,2,5,0);

    PanelGadget panel2 = new PanelGadget();
    GadgetGridLayout gridLayout2 = new GadgetGridLayout(1,2,5,0);
    LabelGadget label2 = new LabelGadget();
    TextFieldGadget idField = new TextFieldGadget();

    PanelGadget panel3 = new PanelGadget();
    CheckboxGadget manual = new CheckboxGadget(res.getString("Manual_random"));

    public CJMiscPanel() {
        try {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void jbInit() throws Exception{
        this.setLayout(gridLayoutx);

        panel0.add(showChecksums);
        showChecksums.addItemListener(this);
        this.add(panel0);

        panel1.setLayout(gridLayout1);
        label1.setLabel(res.getString("Quote_string"));
        label1.setHorizAlign(LabelGadget.HORIZ_ALIGN_RIGHT);
        panel1.add(label1);
        panel1.add(quoteField);
        this.add(panel1);

        panel2.setLayout(gridLayout2);
        label2.setLabel(res.getString("Default_ID"));
        label2.setHorizAlign(LabelGadget.HORIZ_ALIGN_RIGHT);
        panel2.add(label2);
        panel2.add(idField);
        this.add(panel2);

        panel3.add(manual);
        manual.addItemListener(this);
        this.add(panel3);
    }

    public void loadConfig()
    {
        String tmp = CJGlobals.settings.getProperty(res.getString("ShowChecksums"), "");
        CJGlobals.showChecksums = tmp.length() > 0;
        showChecksums.setState(CJGlobals.showChecksums);

        tmp = CJGlobals.settings.getProperty("DefaultSigner", "");
        idField.setText(tmp);

        tmp = CJGlobals.settings.getProperty("QuoteString", "> ");
        quoteField.setText(tmp);

        tmp = CJGlobals.settings.getProperty(res.getString("MouseRandom"), "");
        CJGlobals.manual = tmp.length() > 0;
        manual.setState(CJGlobals.manual);
    }

    public void itemStateChanged(ItemEvent parm1) {
        CJGlobals.showChecksums = showChecksums.getState();
        CJGlobals.settings.put("ShowChecksums", (CJGlobals.showChecksums?res.getString("yes"):""));
        CJGlobals.manual = manual.getState();
        CJGlobals.settings.put("MouseRandom", (CJGlobals.manual?res.getString("yes"):""));
    }
}
