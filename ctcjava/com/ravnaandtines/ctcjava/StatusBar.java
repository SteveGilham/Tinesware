
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;

import dtai.gwt.*;

public class StatusBar extends PanelGadget {
    GadgetFlowLayout flowLayout1 = new GadgetFlowLayout(GadgetFlowLayout.LEFT);
    LabelGadget label1 = new LabelGadget();

    public StatusBar() {
        try {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setText(String s)
    {
        label1.setLabel(s);
        label1.setHorizAlign(DisplayGadget.HORIZ_ALIGN_LEFT);
    }

    public String getText()
    {
        return label1.getLabel();
    }

    public void jbInit() throws Exception{
        this.setLayout(flowLayout1);
        this.add(label1);
    }
}
