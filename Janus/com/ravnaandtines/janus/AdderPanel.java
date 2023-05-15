
//Title:        Janus
//Version:      
//Copyright:    Copyright (c) 2000
//Author:       Mr. Tines
//Company:      Ravna&Tines
//Description:  A multi-hop SOCKS relay service

package com.ravnaandtines.janus;
/**
*  Class AdderPanel
*  <P>
*  Coded & copyright Mr. Tines &lt;tines@windsong.demon.co.uk&gt; 2000
*  All rights reserved.
*  <P>
*  This application is free software; you can redistribute it and/or
*  modify it under the terms of the GNU General Public
*  License as published by the Free Software Foundation; either
*  version 2 of the License, or (at your option) any later version.
*  <P>
*  This application is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
*  General Public License for more details.
*  <P>
*  You should have received a copy of the GNU General Public
*  License along with this library (file "COPYING"); if not,
*  write to the Free Software Foundation, Inc.,
*  59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*  <P>
* @author Mr. Tines
* @version 1.0 10-Feb-2000
*
*/

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

public class AdderPanel extends JPanel
{
  BorderLayout borderLayout1 = new BorderLayout(3,3);
  ResourceBundle res = ResourceBundle.getBundle("com.ravnaandtines.janus.Res");
  JList l = new JList();
  JTextField in = new JTextField("");
  JButton add = new JButton(res.getString("add"));
  JButton del = new JButton(res.getString("Delete"));
  String title = "";
  String tip = "";

  public AdderPanel(String s, String t)
  {
    title = s;
    tip = t;
    try 
    {
      jbInit();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  void jbInit() throws Exception
  {
    this.setLayout(borderLayout1);
    setBorder(new TitledBorder(new EtchedBorder(), title));
    l.setModel(new DefaultListModel());
    if(tip!=null && tip.length()>0) l.setToolTipText(tip);
    add(new JScrollPane(l), BorderLayout.CENTER);
    JPanel s = new JPanel();
    s.setLayout(new BorderLayout());
    s.add(in, BorderLayout.CENTER);
    JPanel b = new JPanel();
    b.setLayout(new FlowLayout(FlowLayout.CENTER));
    s.add(b, BorderLayout.SOUTH);
    add(s, BorderLayout.SOUTH);
    b.add(add);
    b.add(del);

    add.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String s = AdderPanel.this.in.getText();
        if(null == s || 0 == s.length()) return;
        DefaultListModel x = (DefaultListModel)(AdderPanel.this.l.getModel());
        x.addElement(s);
    }});

    del.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        DefaultListModel x = (DefaultListModel)(AdderPanel.this.l.getModel());
        int[] sel = AdderPanel.this.l.getSelectedIndices();
        for(int i=sel.length-1; i>=0; --i)
        {
          x.remove(sel[i]);
        }
    }});
  }

  public void load(java.util.Vector values)
  {
     DefaultListModel x = (DefaultListModel)(AdderPanel.this.l.getModel());
     x.clear();
     for(int i=0; i<values.size(); ++i) x.addElement(values.elementAt(i));
  }

  public String[] save()
  {
    DefaultListModel x = (DefaultListModel)(AdderPanel.this.l.getModel());
    String[] out = new String[x.getSize()];
    for(int i=0; i<out.length; ++i) out[i] = (String)(x.elementAt(i));
    return out;
  }

  public void lock()
  {
    add.setEnabled(false);
    del.setEnabled(false);
  }
}

