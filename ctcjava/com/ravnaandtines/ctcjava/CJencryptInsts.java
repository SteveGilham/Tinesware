
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;
import java.awt.Dialog;
import java.awt.Point;
import java.awt.Dimension;
import java.util.Vector;
import dtai.gwt.*;
import java11.awt.event.*;
import com.ravnaandtines.util.QSort;
import com.ravnaandtines.util.Sortable;

class CJKeySelDlg implements java11.awt.event.ActionListener{
    Vector names;
    java.util.ResourceBundle res =
        java.util.ResourceBundle.getBundle("com.ravnaandtines.ctcjava.Res");
    boolean pub;
    String sign;
    ButtonGadget OK = null;
    ButtonGadget cancel = null;
    ListGadget keys = null;
    LabelGadget conv = null;
    Sortable [] result = null;
    Dialog dlg = null;
    boolean cancelled = false;

    CJKeySelDlg(Vector v, boolean pubkeys)
    {
        cancelled = false;
        OK = new ButtonGadget(res.getString("OK"));
        cancel = new ButtonGadget(res.getString("Cancel"));
        names = v;
        pub = pubkeys;
        sign = null;
        OK.addActionListener(this);
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent parm1) {
                CJKeySelDlg.this.result = null;
                CJKeySelDlg.this.dlg.setVisible(false);
                CJKeySelDlg.this.dlg.dispose();
                cancelled = true;
            }
        }
        );
    }

    public void setDefault(String s)
    {
        sign = s;
    }

    public void show(String s)
    {
        dlg = new Dialog(CJGlobals.mainFrame,
        ((s != null) ? s :
        (pub? res.getString("Select_recipients"):
              res.getString("Select_signing_key"))),
        true);
        dlg.addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent e)
            {
                cancelled = true;
                result = null;
                dlg.setVisible(false);
                dlg.dispose();
            }
        }
        );

        GadgetPanel panel1 = new GadgetPanel();
        GadgetBorderLayout borderLayout1 = new GadgetBorderLayout();
        dlg.add(panel1);
        panel1.setLayout(borderLayout1);

        PanelGadget buttonStick = new PanelGadget();
        GadgetGridLayout gridLayout1 = new GadgetGridLayout(1,3);
        buttonStick.setLayout(gridLayout1);
        buttonStick.add(new LabelGadget(""));
        buttonStick.add(OK);
        buttonStick.add(new LabelGadget(""));
        buttonStick.add(cancel);
        buttonStick.add(new LabelGadget(""));
        panel1.add("South", buttonStick);

        int numrows = names.size();
        if(numrows > 10) numrows = 10;
        keys = new ListGadget(numrows, pub);

        // add an acceleration step to the process
        keys.addActionListener(this);

        if(pub)
        {
            PanelGadget sub = new PanelGadget();
            GadgetBorderLayout b = new GadgetBorderLayout();
            sub.setLayout(b);
            sub.add("Center", keys);
            conv = new LabelGadget(res.getString("If_none_selected"));
            sub.add("South", conv);
            panel1.add("Center", sub);
        }
        else
        {
            panel1.add("Center", keys);
        }

        for(int i=0; i<names.size(); ++i)
        {
            String name = names.elementAt(i).toString();
            keys.add(name);
            if(sign != null)
            {
                if(name.indexOf(sign) >= 0)
                {
                    keys.select(i);
                }
            }
        }

        dlg.pack();
        Point parentAt = CJGlobals.mainFrame.getLocation();
        Dimension d = CJGlobals.mainFrame.getSize();
        Dimension d2 = dlg.getSize();
        dlg.setLocation(parentAt.x+(d.width-d2.width)/2,
        parentAt.y+(d.height-d2.height)/2);
        dlg.setResizable(false);
        dlg.show();
    }



    public void actionPerformed(ActionEvent parm1) {
        if(keys.getNumSelected() > 0)
        {
            int [] sel = keys.getSelectedIndexes();
            result = new Sortable[sel.length];
            for(int i=0; i<sel.length; ++i)
            {
                result[i] = (Sortable) names.elementAt(sel[i]);
            }
        }
        dlg.setVisible(false);
        dlg.dispose();
        cancelled = false;
    }
}

public class CJencryptInsts {

    private CJencryptInsts() {
    }

    // direct poking to the structure
    public static native void reset();
    public static native void setFile(String name, char type);
    public static native void setVersion(int v);
    public static native boolean addRecipient(long to);
    public static native void setSignatory(long from);
    public static native void setAlgs(int cea, int cem, int mda, 
    int cpa, int arm);

    static boolean cancelled;

    public static boolean isCancelled()
    {
        return cancelled;
    }

    // helpful enquiries
    public static  Sortable[] getRecipients()
    {
        return getRecipients(true, null);
    }

    public static  Sortable[] getRecipients(boolean multi, String title)
    {
        cancelled = false;
        if(CJGlobals.decodeContext != null)
        {
            CJPubkey key = CJGlobals.decodeContext.firstPubKey();
            Vector v = new Vector(20);
            while(key != null && !key.isNull())
            {
                if(key.isEnabled())
                {
                    for(Username u = key.getUsername(); !u.isNull();
                        u = u.nextUsername())
                    {
                        PubKeyFolder kf = new PubKeyFolder(key, u);
                        v.addElement(kf);
                    }
                }
                key = key.nextPubkey();
            }

            QSort.sortables(v);

            // create us a little dialog
            CJKeySelDlg dlg = new CJKeySelDlg(v, multi);
            dlg.show(title);
            cancelled = dlg.cancelled;
            return dlg.result;
        }
        else return null;
    }

    public static SecKeyFolder getSignatory()
    {
        cancelled = false;
        if(CJGlobals.decodeContext != null)
        {
            CJSeckey key = CJGlobals.decodeContext.firstSecKey();
            Vector v = new Vector(20);
            while(key != null && !key.isNull())
            {
                CJPubkey pub = key.getPubkey();
                for(Username u = pub.getUsername(); !u.isNull();
                        u = u.nextUsername())
                {
                    SecKeyFolder kf = new SecKeyFolder(key, u);
                    v.addElement(kf);
                }
                key = key.nextSeckey();
            }

            QSort.sortables(v);

            // create us a little dialog
            CJKeySelDlg dlg = new CJKeySelDlg(v, false);

            String signID = CJGlobals.mainFrame.miscPanel.idField.getText();
            if(signID != null && signID.length() > 0) dlg.setDefault(signID);

            dlg.show(null);
            cancelled = dlg.cancelled;
            if(null == dlg.result)
            {
                return null;
            }
            return (SecKeyFolder) dlg.result[0];
        }
        else return null;
    }
}
