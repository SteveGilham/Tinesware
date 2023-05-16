
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

public class CJConvKeyAlgDlg extends Dialog
{
    java.util.ResourceBundle res = java.util.ResourceBundle.getBundle("com.ravnaandtines.ctcjava.Res");
    BorderLayout framing = new BorderLayout();
    GadgetPanel panel1 = new GadgetPanel();
    GadgetBorderLayout buttons = new GadgetBorderLayout();
    PanelGadget bar = new PanelGadget();
    GadgetGridLayout bargrid = new GadgetGridLayout(1,3);
    PanelGadget radio = new PanelGadget();
    ButtonGadget OK = null;
    ButtonGadget cancel = null;
    GroupBox algs = null;
    GroupBox mode = null;
    GadgetBorderLayout boxes = new GadgetBorderLayout();
    CheckboxGadgetGroup algsG = new CheckboxGadgetGroup();
    CheckboxGadgetGroup modeG = new CheckboxGadgetGroup();

    GadgetGridLayout a = new GadgetGridLayout(5,2);
    GadgetGridLayout m = new GadgetGridLayout(1,2);
    CheckboxGadget idea = null;
    CheckboxGadget frfb = null;
    CheckboxGadget b128 = null;
    CheckboxGadget teax = null;
    CheckboxGadget squr = null;
    CheckboxGadget CAST = null;
    CheckboxGadget b403 = null;
    CheckboxGadget des3 = null;
    CheckboxGadget tway = null;

    CheckboxGadget cfb = null;
    CheckboxGadget cbc = null;

    byte[] result = null;


    public CJConvKeyAlgDlg(Frame frame, String title, boolean modal)
    {
        super(frame, title, modal);
        try {
            jbInit();
            add(panel1, BorderLayout.CENTER);
            pack();
            addWindowListener(new java.awt.event.WindowAdapter()
            {
                  public void windowClosing(java.awt.event.WindowEvent e)
                  {
                      result = null;
                      setVisible(false);
                      dispose();
                  }
            }
            );
        }
        catch (Exception e) {
            e.printStackTrace();
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

    public CJConvKeyAlgDlg(Frame frame)
    {
        this(frame, "", false);
    }

    public CJConvKeyAlgDlg(Frame frame, boolean modal)
    {
        this(frame, "", modal);
    }

    public CJConvKeyAlgDlg(Frame frame, String title)
    {
        this(frame, title, false);
    }

    private void jbInit() throws Exception
    {
        idea = new CheckboxGadget(res.getString("IDEA_128bit"),false,algsG);
        frfb = new CheckboxGadget(res.getString("FRF_Blowfish_40bit"),false,algsG);
        b128 = new CheckboxGadget(res.getString("Blowfish_128bit"),false,algsG);
        teax = new CheckboxGadget(res.getString("TEA_128bit"),false,algsG);
        squr = new CheckboxGadget(res.getString("AES128"),false,algsG);
        CAST = new CheckboxGadget(res.getString("CAST_128bit"),false,algsG);
        b403 = new CheckboxGadget(res.getString("Triple_Blowfish_40bit"),false,algsG);
        des3 = new CheckboxGadget(res.getString("TripleDES"),false,algsG);
        tway = new CheckboxGadget(res.getString("TWAY_96bit"),false,algsG);

        cfb = new CheckboxGadget(res.getString("CFB"),true,modeG);
        cbc = new CheckboxGadget(res.getString("CBC"),false,modeG);

        OK = new ButtonGadget(res.getString("OK"));
        algs = new GroupBox(res.getString("Algorithms"));
        mode = new GroupBox(res.getString("Modes"));

        panel1.setLayout(buttons);
        bar.add(new LabelGadget(""));
        bar.add(OK);
        OK.addActionListener(new java11.awt.event.ActionListener() {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                OK_actionPerformed(e);
            }
        }
        );
        bar.add(new LabelGadget(""));
        cancel = new ButtonGadget(res.getString("Cancel"));
        bar.add(cancel);
        cancel.addActionListener(new java11.awt.event.ActionListener() {
            public void actionPerformed(java11.awt.event.ActionEvent e)
            {
                OK_actionPerformed(e); // yes, really
            }
        }
        );
        bar.add(new LabelGadget(""));
        panel1.add("South",bar);

        radio.setLayout(boxes);
        radio.add("Center", algs);
        radio.add("South", mode);
        panel1.add("Center", radio);

        algs.setLayout(a);
        mode.setLayout(m);

        algs.add(idea);
        algs.add(frfb);
        algs.add(b128);
        algs.add(teax);
        algs.add(squr);
        algs.add(CAST);
        algs.add(b403);
        algs.add(des3);
        algs.add(tway);
        if(CTCIKeyConst.isIDEAenabled())
        {
            idea.setState(true);
        }
        else
        {
            idea.setEnabled(false);
            CAST.setState(true);
        }
        mode.add(cfb); 
        cfb.setState(true);
        mode.add(cbc);
    }

    void OK_actionPerformed(ActionEvent e)
    {
        if (e.getSource() == OK)
        {
            result = new byte[8];
            result[0] = result[1] =
                result[2] = result[3] =
                result[4] = result[5] =
                result[6] = result[7] = 0;

            if(idea.getState())
            {
                result[0] = CTCIKeyConst.CEA_IDEAFLEX;
            }
            else if(b128.getState())
            {
                result[0] = CTCIKeyConst.CEA_BLOW16;
            }
            else if(squr.getState())
            {
                result[0] = CTCIKeyConst.CEA_OPGP_AES_128;
            }
            else if(b403.getState())
            {
                result[0] = CTCIKeyConst.CEA_BLOW5;
                result[1] = CTCIKeyConst.CEM_TRIPLE_FLAG;
            }
            else if(tway.getState())
            {
                result[0] = CTCIKeyConst.CEA_3WAY;
            }
            else if(frfb.getState())
            {
                result[0] = (byte)(CTCIKeyConst.CEA_BLOW5 | CTCIKeyConst.CEA_MORE_FLAG);
                result[2] = result[0];
                result[4] = CTCIKeyConst.CEA_BLOW5;
                result[3] = CTCIKeyConst.CEM_REVERSE_FLAG;
            }
            else if(teax.getState())
            {
                result[0] = CTCIKeyConst.CEA_TEA;
            }
            else if(CAST.getState())
            {
                result[0] = CTCIKeyConst.CEA_CAST5FLEX;
            }
            else
            {
                result[0] = CTCIKeyConst.CEA_3DESFLEX;
            }

            int i;
            if(cfb.getState())
            {
                for(i=0; result[2*i]!=0; ++i) result[1+2*i] |= CTCIKeyConst.CEM_CFB;
            }
            else
            {
                for(i=0; result[2*i]!=0; ++i) result[1+2*i] |= CTCIKeyConst.CEM_CBC;
            }
        } // if OK event
        else
        {
            result = null;
        }
        setVisible(false);
        dispose();
    }
}

