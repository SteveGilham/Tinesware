
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;

import dtai.gwt.*;
import java11.awt.event.*;
import java.util.*;

public class CJAlgPanel extends PanelGadget implements ActionListener {
    GadgetGridLayout gridLayout1 = new GadgetGridLayout(7,1);
    PanelGadget panel0 = new PanelGadget();
    PanelGadget panel1 = new PanelGadget();
    PanelGadget panel2 = new PanelGadget();
    PanelGadget panel3 = new PanelGadget();
    PanelGadget panel4 = new PanelGadget();
    PanelGadget panel5 = new PanelGadget();
    PanelGadget panel6 = new PanelGadget();
    LabelGadget label1 = new LabelGadget();
    ChoiceGadget CEAbox = null;
    GadgetGridLayout gridLayout2 = new GadgetGridLayout(1,2,5,0);
    LabelGadget label2 = new LabelGadget();
    ChoiceGadget CEMbox = null;
    GadgetGridLayout gridLayout3 = new GadgetGridLayout(1,2,5,0);
    LabelGadget label3 = new LabelGadget();
    ChoiceGadget MDAbox = null;
    GadgetGridLayout gridLayout4 = new GadgetGridLayout(1,2,5,0);
    GadgetGridLayout gridLayout5 = new GadgetGridLayout(1,2,5,0);
    LabelGadget label4 = new LabelGadget();
    ChoiceGadget CPAbox = null;
    LabelGadget label5 = new LabelGadget();
    ChoiceGadget ARMbox = null;
    GadgetGridLayout gridLayout6 = new GadgetGridLayout(1,2,5,0);
    int base = 0;
    boolean loaded = false;
    ResourceBundle res = ResourceBundle.getBundle("com.ravnaandtines.ctcjava.Res");

    String[] algs = {
        "IDEA_128bit", "CAST_128bit",
        "Blowfish_128bit",
        "SAFER_SK128", "TEA_128bit", "TripleDES",
        "AES128", "AES256"    };
    int []   algn = {
        CTCIKeyConst.CEA_IDEA, CTCIKeyConst.CEA_CAST5,
        CTCIKeyConst.CEA_GPG_BLOW16,
        CTCIKeyConst.CEA_EBP_SAFER_MIN, CTCIKeyConst.CEA_TEA,
        CTCIKeyConst.CEA_3DES,
        CTCIKeyConst.CEA_OPGP_AES_128,
        CTCIKeyConst.CEA_OPGP_AES_256    };

    String[] mode = {
        "CFB", "CBC"    };
    int[]    modn = {
        CTCIKeyConst.CEM_CFB, CTCIKeyConst.CEM_CBC    };

    String[] cpa  = {
        "No_Compression", "Deflate", "Splay_tree"    };
    int[]    cpan = {
        CTCIKeyConst.CPA_NONE, CTCIKeyConst.CPA_DEFLATE,
        CTCIKeyConst.CPA_SPLAY    };

    String[] mda  = {
        "MD5_128bit", "SHA1_160bit",
        "RIPEM_160bit", "HAVEL_256bit"    };
    int[]    mdan = {
        CTCIKeyConst.MDA_MD5, CTCIKeyConst.MDA_PGP5_SHA1,
        CTCIKeyConst.MDA_PGP5_RIPEM160,CTCIKeyConst.MDA_EBP_HAVAL_MIN    };

    String[] arm  = {
        "Unarmoured", "PGP_armour", "UUencode"    };
    int[]      armn = {
        CTCIKeyConst.ARM_NONE, CTCIKeyConst.ARM_PGP,
        CTCIKeyConst.ARM_UUENCODE    };

    public CJAlgPanel() {
        try {
            base = CTCIKeyConst.isIDEAenabled()? 0 : 1;
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void translate(String[] array)
    {
        for(int i=0; i<array.length; ++i)
        {
            array[i] = res.getString(array[i]);
        }
    }

    public void jbInit() throws Exception{
        translate(algs);
        translate(mode);
        translate(cpa);
        translate(mda);
        translate(arm);

        CEAbox = new ChoiceGadget(CTCIKeyConst.isIDEAenabled()?
        algs.length:algs.length-1);
        CEMbox = new ChoiceGadget(mode.length);
        MDAbox = new ChoiceGadget(mda.length);
        CPAbox = new ChoiceGadget(cpa.length);
        ARMbox = new ChoiceGadget(arm.length);

        this.add(panel0);
        panel5.setLayout(gridLayout6);
        panel4.setLayout(gridLayout5);
        panel3.setLayout(gridLayout4);
        panel2.setLayout(gridLayout3);
        panel1.setLayout(gridLayout2);
        label1.setLabel(res.getString("Conventional_Encryption"));
        label1.setHorizAlign(LabelGadget.HORIZ_ALIGN_RIGHT);
        label2.setLabel(res.getString("Encryption_Mode"));
        label2.setHorizAlign(LabelGadget.HORIZ_ALIGN_RIGHT);
        label3.setLabel(res.getString("Message_Digest"));
        label3.setHorizAlign(LabelGadget.HORIZ_ALIGN_RIGHT);
        label4.setLabel(res.getString("Compression_Scheme"));
        label4.setHorizAlign(LabelGadget.HORIZ_ALIGN_RIGHT);
        label5.setLabel(res.getString("Armour_Style"));
        label5.setHorizAlign(LabelGadget.HORIZ_ALIGN_RIGHT);
        this.setLayout(gridLayout1);
        this.add(panel1);
        panel1.add(label1);
        panel1.add(CEAbox);
        this.add(panel2);
        panel2.add(label2);
        panel2.add(CEMbox);
        this.add(panel3);
        panel3.add(label3);
        panel3.add(MDAbox);
        this.add(panel4);
        panel4.add(label4);
        panel4.add(CPAbox);
        this.add(panel5);
        panel5.add(label5);
        panel5.add(ARMbox);
        this.add(panel6);

        int i;
        for(i=base; i<algs.length; ++i)
            CEAbox.add(algs[i]);
        for(i=0; i<mode.length; ++i)
            CEMbox.add(mode[i]);
        for(i=0; i<mda.length; ++i)
            MDAbox.add(mda[i]);
        for(i=0; i<cpa.length; ++i)
            CPAbox.add(cpa[i]);
        for(i=0; i<arm.length; ++i)
            ARMbox.add(arm[i]);

        CEAbox.addActionListener(this);
        CEMbox.addActionListener(this);
        MDAbox.addActionListener(this);
        CPAbox.addActionListener(this);
        ARMbox.addActionListener(this);
    }

    public void loadConfig()
    {
        String tmp;
        int index;

        tmp = CJGlobals.settings.getProperty("EncryptionAlgorithm",
        Integer.toString(base));
        index = base;
        try {
            index = Integer.decode(tmp).intValue();
        } 
        catch (NumberFormatException e) {
            index = base;
        }
        index -= base;
        CEAbox.select(index);

        tmp = CJGlobals.settings.getProperty("EncryptionMode", "0");
        index = 0;
        try {
            index = Integer.decode(tmp).intValue();
        } 
        catch (NumberFormatException e) {
            index = 0;
        }
        CEMbox.select(index);

        tmp = CJGlobals.settings.getProperty("MessageDigestAlgorithm", "0");
        index = 0;
        try {
            index = Integer.decode(tmp).intValue();
        } 
        catch (NumberFormatException e) {
            index = 0;
        }
        MDAbox.select(index);

        tmp = CJGlobals.settings.getProperty("CompressionAlgorithm", "1");
        index = 1;
        try {
            index = Integer.decode(tmp).intValue();
        } 
        catch (NumberFormatException e) {
            index = 1;
        }
        CPAbox.select(index);

        tmp = CJGlobals.settings.getProperty("Armour", "1");
        index = 1;
        try {
            index = Integer.decode(tmp).intValue();
        } 
        catch (NumberFormatException e) {
            index = 1;
        }
        ARMbox.select(index);

        loaded = true;

    }

    public void setAlgs()
    {
        CJencryptInsts.setAlgs(algn[CEAbox.getSelectedIndex()+base],
        modn[CEMbox.getSelectedIndex()],
        mdan[MDAbox.getSelectedIndex()],
        cpan[CPAbox.getSelectedIndex()],
        armn[ARMbox.getSelectedIndex()]);
    }

    public void actionPerformed(ActionEvent parm1) {
        if(!loaded) return;

        CJGlobals.settings.put("EncryptionAlgorithm",
        Integer.toString(CEAbox.getSelectedIndex()+base));
        CJGlobals.settings.put("EncryptionMode",
        Integer.toString(CEMbox.getSelectedIndex()));
        CJGlobals.settings.put("MessageDigestAlgorithm",
        Integer.toString(MDAbox.getSelectedIndex()));
        CJGlobals.settings.put("CompressionAlgorithm",
        Integer.toString(CPAbox.getSelectedIndex()));
        CJGlobals.settings.put("Armour",
        Integer.toString(ARMbox.getSelectedIndex()));
    }
}
