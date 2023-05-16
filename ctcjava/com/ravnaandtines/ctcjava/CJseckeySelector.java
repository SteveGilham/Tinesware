
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;

import java.awt.*;
import dtai.gwt.*;
import java11.awt.event.*;
import java.util.Vector;
import com.ravnaandtines.util.QSort;
import com.ravnaandtines.util.Sortable;

public class CJseckeySelector extends Dialog {
    GadgetPanel panel1 = new GadgetPanel();
    ListGadget keyIDList = new ListGadget();
    GadgetBorderLayout borderLayout1 = new GadgetBorderLayout();
    PanelGadget panel2 = new PanelGadget();
    GadgetGridLayout gridLayout1 = new GadgetGridLayout(1,3);
    LabelGadget label1 = new LabelGadget();
    ButtonGadget OK = new ButtonGadget();
    LabelGadget label2 = new LabelGadget();
    java.util.ResourceBundle res = java.util.ResourceBundle.getBundle("com.ravnaandtines.ctcjava.Res");

    public int selection = -1;

    public CJseckeySelector(Frame frame, String title, boolean modal) {
        super(frame, title, modal);
        try {
            jbInit();
            add(panel1);
            pack();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CJseckeySelector(Frame frame) {
        this(frame, "", false);
    }

    public CJseckeySelector(Frame frame, boolean modal) {
        this(frame, "", modal);
    }

    public CJseckeySelector(Frame frame, String title) {
        this(frame, title, false);
    }

    private void jbInit() throws Exception{
        keyIDList.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                System.out.println("list action performed");  // not called
                OK_actionPerformed();
            }
        }
        );
        keyIDList.addMouseListener(new java11.awt.event.MouseAdapter()
        {
            public void mouseClicked(java11.awt.event.MouseEvent e)
            {
                System.out.println("list action performed "+e.getClickCount());
                //OK_actionPerformed(); // not called
            }
        }
        );
        OK.setLabel(res.getString("OK"));
        OK.addActionListener(new java11.awt.event.ActionListener()
        {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                OK_actionPerformed();
            }
        }
        );
        panel2.setLayout(gridLayout1);
        panel1.setLayout(borderLayout1);
        panel1.add("Center", keyIDList);
        panel1.add("South", panel2);
        panel2.add(label1);
        panel2.add(OK);
        panel2.add(label2);
    }

    public void fill(String[] names)
    {
        System.out.println("seckey selector fill");  // not called
        keyIDList.removeAll();
        // QSort.sort(names); No!!!
        for(int i=0; i<names.length; ++i)
        {
            keyIDList.add(names[i]);
        }
    }

    void OK_actionPerformed()
    {
        System.out.println("seckey selector action performed");  // not called
        setVisible(false);
        selection = keyIDList.getSelectedIndex();
        dispose();
    }

    public void show()
    {
        Container p = getParent();
        Point parentAt = p!=null ? p.getLocation()
          : new Point(0,0);
        Dimension d = p!=null ? p.getSize()
          : java.awt.Toolkit.getDefaultToolkit().getScreenSize();;
        Dimension d2 = this.getSize();
        this.setLocation(parentAt.x+(d.width-d2.width)/2,
        parentAt.y+(d.height-d2.height)/2);
        this.setResizable(false);
        super.show();
    }

}

