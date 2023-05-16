
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;

import dtai.gwt.*;
import java11.awt.event.TextEvent;

public class CJGetPassphrase extends PanelGadget {
    GadgetGridLayout gridLayout1 = new GadgetGridLayout(2,1);
    java.util.ResourceBundle res =
      java.util.ResourceBundle.getBundle("com.ravnaandtines.ctcjava.Res");
    private static final int WIDTH = 36;
    CryptoTextFieldGadget phrase = new CryptoTextFieldGadget(WIDTH);
    LabelGadget Checksum = new LabelGadget();
    String hardtext = res.getString("Checksum:");

    public CJGetPassphrase() {
        try {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void jbInit() throws Exception{
        phrase.setEchoChar('*');
        phrase.addTextListener(new CJGetPassphrase_phrase_textAdapter(this));
        Checksum.setLabel(hardtext);
        Checksum.setHorizAlign(LabelGadget.HORIZ_ALIGN_LEFT);
        this.setLayout(gridLayout1);
        this.add(phrase);
        this.add(Checksum);
    }

    void phrase_textValueChanged(TextEvent e) {
        if(CJGlobals.showChecksums) Checksum.setLabel(hardtext+phrase.toString());
        else Checksum.setLabel(hardtext+res.getString("<concealed>"));
    }

    public void clearText()
    {
        phrase.wipe();
        phrase.setText("");
    }
}

class CJGetPassphrase_phrase_textAdapter implements java11.awt.event.TextListener{
    CJGetPassphrase adaptee;

    CJGetPassphrase_phrase_textAdapter(CJGetPassphrase adaptee) {
        this.adaptee = adaptee;
    }

    public void textValueChanged(TextEvent e) {
        adaptee.phrase_textValueChanged(e);
    }
}
