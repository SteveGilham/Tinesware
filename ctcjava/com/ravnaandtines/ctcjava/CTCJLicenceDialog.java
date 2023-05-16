
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. Tines <tines@windsong.demon.co.uk>
//Company:      Ravna&Tines
//Description:  A Java[tm]1.1-based portable GUI to CTClib2.0


package com.ravnaandtines.ctcjava;

import java.awt.*;
import java.awt.event.*;

public class CTCJLicenceDialog extends Dialog {
    Panel panel1 = new Panel();
    BorderLayout borderLayout1 = new BorderLayout();
    TextArea textArea1 = new TextArea();
    Panel panel2 = new Panel();
    FlowLayout flowLayout1 = new FlowLayout();
    Button OK = new Button();
    java.util.ResourceBundle res = java.util.ResourceBundle.getBundle("com.ravnaandtines.ctcjava.Res");

    public CTCJLicenceDialog(Frame frame, String title, boolean modal) {
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

    public CTCJLicenceDialog(Frame frame) {
        this(frame, "", false);
    }

    public CTCJLicenceDialog(Frame frame, boolean modal) {
        this(frame, "", modal);
    }

    public CTCJLicenceDialog(Frame frame, String title) {
        this(frame, title, false);
    }

    public void jbInit() throws Exception{
        textArea1.setEditable(false);
        OK.setLabel(res.getString("OK"));
        OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                OK_actionPerformed(e);
            }
        }
        );
        panel1.setLayout(borderLayout1);
        panel1.add(textArea1, BorderLayout.CENTER);
        panel2.setLayout(flowLayout1);
        panel1.add(panel2, BorderLayout.SOUTH);
        panel2.add(OK, null);
        setGPL();
    }

    public void setGPL()
    {
        textArea1.setText(getGPL());
    }
    private native String getGPL();

    public void setIDEA()
    {
        textArea1.setText(getIDEA());
    }
    private native String getIDEA();

    void OK_actionPerformed(ActionEvent e) {
        if (e.getSource() == OK)
        {
            setVisible(false);
            dispose();
        }
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

