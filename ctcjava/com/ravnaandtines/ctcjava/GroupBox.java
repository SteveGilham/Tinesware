
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;

import dtai.gwt.*;

public class GroupBox extends BorderGadget {
    LabelGadget caption;
    private java.awt.Color realNormalBg;
    private java.awt.Color realSelectedBg;

    public GroupBox(String s) {
        super();
        caption = new LabelGadget(s);
        java.awt.Dimension d = caption.getPreferredSize();
        setMargins(d.height);
        setBorderType(BorderGadget.ETCHED_IN);
        setBorderThickness(4);
    }

    public void update( GadgetGraphics g ) {
        realNormalBg = getNormalBackground();
        realSelectedBg = getSelectedBackground();
        super.update(g);
        setNormalBackground(realNormalBg);
        setSelectedBackground(realSelectedBg);
    }

    public void paint(GadgetGraphics g)
    {
        java.awt.Dimension d = caption.getPreferredSize();

        setNormalBackground(realNormalBg);
        setSelectedBackground(realSelectedBg);

        int mainWidth = getSize().width - getShadowXOffset();
        int mainHeight = getSize().height - getShadowYOffset()-d.height/2;
        int mainX = 0;
        int mainY = d.height/2;
        if ( getShadowXOffset() < 0 ) {
            mainX += getShadowXOffset();
        }
        if ( getShadowYOffset() < 0 ) {
            mainY += getShadowYOffset();
        }

        java.awt.Color fg = getForeground(g);
        java.awt.Color bg = getBackground(g);

        if ( ( ( getShadowXOffset() != 0 ) ||
            ( getShadowYOffset() != 0 ) ) &&
            ( getShadowColor() != null ) &&
            ( getShadowColor() != Gadget.transparent ) &&
            ( bg != null ) &&
            ( bg != Gadget.transparent ) ) {
            paintShadow( g, mainX, mainY, mainWidth, mainHeight );
            paintBackground( g, mainX, mainY, mainWidth, mainHeight, bg );
        }
        else if ( getBorderType() == ROUND_RECT ) {
            paintBackground( g, mainX, mainY, mainWidth, mainHeight, bg );
        }

        if ( ( getDefaultThickness() > 0 ) &&
            ( isNowDefault() ) ) {
            g.setColor(fg);
            g.drawRect(mainX,mainY,mainWidth-1,mainHeight-1);
            mainX += getDefaultThickness();
            mainY += getDefaultThickness();
            int defaultThickness2 = getDefaultThickness()*2;
            mainWidth -= defaultThickness2;
            mainHeight -= defaultThickness2;
        }

        if ( getBorderType() != NONE &&
            getBorderColor() != null &&
            getBorderColor() != Gadget.transparent &&
            getBorderThickness() > 0 ) {
            paintBorder( g, mainX, mainY, mainWidth, mainHeight, bg );
        }

        if ( ( getFocusThickness() > 0 ) &&
            ( getMinMargin() > 0 ) &&
            hasFocus() ) {
            drawFocus(g,fg,getFocusThickness());
        }

        g.setColor(getBackground());
        g.fillRect(d.height, 0, d.width+d.height, d.height);
        g.setColor(getForeground());
        g.drawString(caption.getLabel(),3*d.height/2, d.height);
    }

    private void paintShadow( GadgetGraphics g, int x, int y, int width, int height ) {
        if ( getShadowColor() != Gadget.transparent && getShadowColor() != null ) {
            g.setColor( getShadowColor() );
            if ( getBorderType() == ROUND_RECT ) {

                int arcSize = getMinMargin() * 8;

                g.fillRoundRect( x + getShadowXOffset(),
                y + getShadowYOffset(),
                width, height,
                arcSize, arcSize );
            }
            else {
                g.fillRect( x + getShadowXOffset(),
                y + getShadowYOffset(),
                width, height );
            }
        }
    }

    private void paintBackground( GadgetGraphics g,
    int x, int y, int width, int height,
    java.awt.Color background ) {
        if ( background == Gadget.transparent ) {
            return;
        }
        g.setColor( background );
        if ( getBorderType() == ROUND_RECT ) {
            int arcSize = getMinMargin()*8;
            if ( getBorderThickness() > 0 ) {
                g.fillRoundRect( x+1, y+1, width-2, height-2, arcSize, arcSize );
            }
            else {
                g.fillRoundRect( x, y, width, height, arcSize, arcSize );
            }
        }
        else {
            g.fillRect( x, y, width, height );
        }
    }

}
