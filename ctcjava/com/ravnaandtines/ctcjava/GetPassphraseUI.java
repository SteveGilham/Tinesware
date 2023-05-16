
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;

import javax.swing.*;
import java.awt.event.TextEvent;

public class GetPassphraseUI {
    JPanel shell = new JPanel();
    java.awt.GridLayout gridLayout1 = new java.awt.GridLayout(2,1);
    private static final int WIDTH = 36;
    JPasswordField phrase = new JPasswordField(WIDTH);
    JLabel Checksum = new JLabel();
    String hardtext = GlobalData.getResourceString("Checksum:");

    public GetPassphraseUI() {
        phrase.setEchoChar('*');

        phrase.getDocument().addDocumentListener(new GetPassphraseUI_phrase_textAdapter(this));
        Checksum.setText(hardtext);

        shell.setLayout(gridLayout1);
        shell.add(phrase);
        shell.add(Checksum);
    }
    
    public java.awt.Component getShell()
    {
        return shell;
    }
    
    public void applyLabel(JLabel l)
    {
        l.setLabelFor(phrase);
    }
    
    public void addDocumentListener(javax.swing.event.DocumentListener l)
    {
        phrase.getDocument().addDocumentListener(l);
    }
    
    public CryptoString getResult()
    {
        char [] pw = phrase.getPassword();
        if(pw == null)
            return null;
        return new CryptoString(pw);
    }

    private String hash(char[] pwd) {
        int result = 0;
        if(pwd.length < 10)
        {
            return "<TOO SHORT>";
        }

        for(int i=0; i<pwd.length; ++i)
        {
            // allow for multi-byte encodings; the series of
            // fragmented 1-character strings are less of a leak
            String tmp1 = new String(pwd,i,1);
            byte[] tmp = null;
            if(null != GlobalData.encoding)
            {
                try {
                    tmp = tmp1.getBytes(GlobalData.encoding);
                } 
                catch ( java.io.UnsupportedEncodingException ex ) {
                }
            }
            if(null==tmp) tmp = tmp1.getBytes();
            for(int k=0; k<tmp.length; ++k)
            {
                result = result * 104729 + tmp[k];
                tmp[k] = 0;
            }
        }
        result &= 0xFFFF;
        return Integer.toHexString(result);
    }    
    
    void phrase_textValueChanged() {
        if(Root.instance().isVisibleChecksums()) Checksum.setText(hardtext+hash(phrase.getPassword()));
        else Checksum.setText(hardtext+GlobalData.getResourceString("<concealed>"));
    }

    public void clearText()
    {
        // can we do any better than this??
        // would have to get at the PlainDocument and wipe it
        phrase.setText("");
    }
}

class GetPassphraseUI_phrase_textAdapter implements javax.swing.event.DocumentListener{
    GetPassphraseUI adaptee;

    GetPassphraseUI_phrase_textAdapter(GetPassphraseUI adaptee) {
        this.adaptee = adaptee;
    }

    public void changedUpdate(javax.swing.event.DocumentEvent e) {
        adaptee.phrase_textValueChanged();
    }
    public void insertUpdate(javax.swing.event.DocumentEvent e) {
        adaptee.phrase_textValueChanged();
    }
    public void removeUpdate(javax.swing.event.DocumentEvent e) {
        adaptee.phrase_textValueChanged();
    }
}
