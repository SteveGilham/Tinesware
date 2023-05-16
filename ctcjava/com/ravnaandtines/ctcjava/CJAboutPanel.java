
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;

import dtai.gwt.*;
import java11.awt.event.*;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.*;

class CJImageControl extends ImageGadget
{
    public CJImageControl()
    {
        super( Toolkit.getDefaultToolkit().createImage(
        new com.ravnaandtines.util.Logo()) );
        this.addMouseListener(new CJImageControl_this_mouseAdapter(this));
    }
    public Dimension getMaximumSize()
    {
        return new Dimension(118,118);
    }
    public Dimension getMinimumSize()
    {
        return new Dimension(118,118);
    }
    public Dimension getPreferredSize()
    {
        return new Dimension(118,118);
    }

    void this_mouseEntered(MouseEvent e) {
        CJGlobals.theStatusBar.setText(res.getString("ATines_pack_(after_V"));
    }

    void this_mouseExited(MouseEvent e) {
        CJGlobals.theStatusBar.setText("");
    }
    ResourceBundle res = ResourceBundle.getBundle("com.ravnaandtines.ctcjava.Res");
}

class CJImageControl_this_mouseAdapter extends java11.awt.event.MouseAdapter {
    CJImageControl adaptee;

    CJImageControl_this_mouseAdapter(CJImageControl adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseExited(MouseEvent e) {
        adaptee.this_mouseExited(e);
    }

    public void mouseEntered(MouseEvent e) {
        adaptee.this_mouseEntered(e);
    }
}



public class CJAboutPanel extends PanelGadget {

    GadgetBorderLayout borderLayout1 = new GadgetBorderLayout();
    ResourceBundle res = ResourceBundle.getBundle("com.ravnaandtines.ctcjava.Res");
    ImageGadget imageControl1 = new CJImageControl();
    BorderGadget bevelPanel1 = new BorderGadget();
    GadgetGridLayout gridLayout1 = new GadgetGridLayout(11,1);
    LabelGadget productLabel   = new LabelGadget();
    LabelGadget copyrightLabel = new LabelGadget();
    LabelGadget tinesLabel     = new LabelGadget();
    LabelGadget ianLabel       = new LabelGadget();
    LabelGadget spaceLabel     = new LabelGadget("");
    LabelGadget icon1Label     = new LabelGadget();
    LabelGadget icon2Label     = new LabelGadget();
    LabelGadget icon3Label     = new LabelGadget();
    LabelGadget icon4Label     = new LabelGadget();
    LabelGadget icon5Label     = new LabelGadget();
    LabelGadget icon6Label     = new LabelGadget();


    public CJAboutPanel() {
        try {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void jbInit() throws Exception{

        Object[] args = { 
            CJGlobals.libVersion(), "v1.2"         };
        productLabel.setLabel(java.text.MessageFormat.format(
        res.getString("CTC2_0_for_Java_Free"),
        args));
        productLabel.addMouseListener(new java11.awt.event.MouseAdapter() {
            public void mouseEntered(MouseEvent e)
            {
                productLabel_mouseEntered();
            }
            public void mouseExited(MouseEvent e)
            {
                productLabel_mouseExited();
            }
        }
        );
        productLabel.setHorizAlign(DisplayGadget.HORIZ_ALIGN_LEFT);

        Object[] args2 = {
            "© 1998-2001"        };
        copyrightLabel.setLabel(java.text.MessageFormat.format(
        res.getString("Copyright_©_1998"),
        args2));
        copyrightLabel.addMouseListener(new java11.awt.event.MouseAdapter() {
            public void mouseEntered(MouseEvent e)
            {
                copyrightLabel_mouseEntered();
            }
            public void mouseExited(MouseEvent e)
            {
                copyrightLabel_mouseExited();
            }
        }
        );
        copyrightLabel.setHorizAlign(DisplayGadget.HORIZ_ALIGN_LEFT);
        tinesLabel.setLabel(res.getString("Mr_Tines"));
        tinesLabel.setHorizAlign(DisplayGadget.HORIZ_ALIGN_LEFT);
        ianLabel.setLabel(res.getString("&Ian_Miller"));
        ianLabel.setHorizAlign(DisplayGadget.HORIZ_ALIGN_LEFT);

        icon1Label.setLabel(res.getString("Button_top_icons"));
        icon1Label.setHorizAlign(DisplayGadget.HORIZ_ALIGN_LEFT);
        icon2Label.setLabel(res.getString("Dean_S_Jones"));
        icon2Label.setHorizAlign(DisplayGadget.HORIZ_ALIGN_LEFT);
        icon3Label.setLabel(res.getString("www_gallant_com_icons"));
        icon3Label.setHorizAlign(DisplayGadget.HORIZ_ALIGN_LEFT);
        icon4Label.setLabel(res.getString("GWT"));
        icon4Label.setHorizAlign(DisplayGadget.HORIZ_ALIGN_LEFT);
        icon5Label.setLabel(res.getString("GWT_CPR"));
        icon5Label.setHorizAlign(DisplayGadget.HORIZ_ALIGN_LEFT);
        icon6Label.setLabel(res.getString("GWT_URL"));
        icon6Label.setHorizAlign(DisplayGadget.HORIZ_ALIGN_LEFT);

        bevelPanel1.setLayout(gridLayout1);
        this.setLayout(borderLayout1);
        this.add("West", imageControl1);
        this.add("Center", bevelPanel1);
        bevelPanel1.setBorderThickness(5);
        bevelPanel1.add(productLabel);
        bevelPanel1.add(copyrightLabel);
        bevelPanel1.add(tinesLabel);
        bevelPanel1.add(ianLabel);
        bevelPanel1.add(spaceLabel);
        bevelPanel1.add(icon1Label);
        bevelPanel1.add(icon2Label);
        bevelPanel1.add(icon3Label);
        bevelPanel1.add(icon4Label);
        bevelPanel1.add(icon5Label);
        bevelPanel1.add(icon6Label);
    }

    void productLabel_mouseEntered() {
        Object[] args = { 
            CJGlobals.libVersion()         };
        CJGlobals.theStatusBar.setText(java.text.MessageFormat.format(
        res.getString("Uses_CTClib_2_x"),
        args));
    }

    void productLabel_mouseExited() {
        CJGlobals.theStatusBar.setText("");
    }

    void copyrightLabel_mouseEntered() {
        CJGlobals.theStatusBar.setText(res.getString("Copyleft_actually_see"));
    }

    void copyrightLabel_mouseExited() {
        CJGlobals.theStatusBar.setText("");
    }
}

