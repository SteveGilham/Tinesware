
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware   


package com.ravnaandtines.ctcjava;

import dtai.gwt.*;
import java.awt.Image;
import java11.awt.event.ActionListener;

public class ButtonBar extends BorderGadget {
    GadgetFlowLayout flow = new GadgetFlowLayout(GadgetFlowLayout.LEFT,1,1);
    ButtonGadget[] buttons = null;

    public ButtonBar() {
        try {
            jbInit();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void jbInit() throws Exception{
        this.setBorderThickness(2);
        this.setDefaultThickness(0);
        this.setMargins(0);
        this.setLayout(flow);
        this.setBorderType(BorderGadget.THREED_OUT);
    }

    public void setVisible(int n, boolean state)
    {
        if(null == buttons) return;
        if(n<0 || n>=buttons.length) return;
        buttons[n].setVisible(state);
    }

    public void addButtons(Image[] active, Image[] greyed,
    String[] tips, ActionListener targets[])
    {
        buttons = new ButtonGadget[active.length];
        for(int i=0; i<active.length; ++i)
        {
            if(active[i] == null) // add space
            {
                LabelGadget shim = new LabelGadget(" ");
                add(shim);
                buttons[i] = null;
            }
            else // button
            {
                buttons[i] = new ButtonGadget(active[i]);
                if(greyed != null && i >= greyed.length && greyed[i] != null)
                {
                    buttons[i].setDisabledImage(greyed[i]);
                }
                buttons[i].setBorderThickness(2);
                buttons[i].setDefaultThickness(0);
                buttons[i].setMargins(0);
                if(tips != null && i < tips.length && tips[i] != null)
                {
                    buttons[i].setTip(tips[i]);
                }
                if(targets != null && i < targets.length && targets[i] != null)
                {
                    buttons[i].addActionListener(targets[i]);
                }
                add(buttons[i]);
            }
        }
    }

}
