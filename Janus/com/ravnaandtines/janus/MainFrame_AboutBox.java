
//Title:      Janus
//Version:    
//Copyright:  Copyright (c) 2000
//Author:     Mr. Tines
//Company:    Ravna&Tines
//Description:A multi-hop SOCKS relay service
package com.ravnaandtines.janus;
/**
*  Class MainFrame_ABoutBox
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
import javax.swing.*;
import javax.swing.border.*;

public class MainFrame_AboutBox extends Dialog implements ActionListener
{

  JPanel panel1 = new JPanel();
  JPanel panel2 = new JPanel();
  JPanel insetsPanel1 = new JPanel();
  JPanel insetsPanel2 = new JPanel();
  JPanel insetsPanel3 = new JPanel();
  JButton button1 = new JButton();
  JLabel imageControl1 = new JLabel();
  ImageIcon imageIcon;
  JLabel label1 = new JLabel();
  JLabel label2 = new JLabel();
  JLabel label3 = new JLabel();
  JLabel label4 = new JLabel();
  BorderLayout borderLayout1 = new BorderLayout();
  BorderLayout borderLayout2 = new BorderLayout();
  FlowLayout flowLayout1 = new FlowLayout();
  FlowLayout flowLayout2 = new FlowLayout();
  GridLayout gridLayout1 = new GridLayout();
  String product = "Janus 1.2.0 - A multi-hop relay service";
  String version = "This application is free software; you can redistribute it and/or";
  String copyright = "modify it under the terms of the GNU General Public License";
  String comments = "Copyright © Mr.Tines 2000-2002 (thanks also to Shelby Cain).";

  public MainFrame_AboutBox(Frame parent)
  {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try 
    {
      jbInit();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    //imageControl1.setIcon(imageIcon);
    pack();
  }

  private void jbInit() throws Exception 
  {
    //imageIcon = new ImageIcon(getClass().getResource("widen.gif"));
    this.setTitle("About");
    setResizable(false);
    panel1.setLayout(borderLayout1);
    panel2.setLayout(borderLayout2);
    insetsPanel1.setLayout(flowLayout1);
    insetsPanel2.setLayout(flowLayout1);
    insetsPanel2.setBorder(new EmptyBorder(10, 10, 10, 10));
    gridLayout1.setRows(4);
    gridLayout1.setColumns(1);
    label1.setText(product);
    label2.setText(version);
    label3.setText(copyright);
    label4.setText(comments);
    insetsPanel3.setLayout(gridLayout1);
    insetsPanel3.setBorder(new EmptyBorder(10, 60, 10, 10));
    button1.setText("OK");
    button1.addActionListener(this);
    insetsPanel2.add(imageControl1, null);
    panel2.add(insetsPanel2, BorderLayout.WEST);
    this.add(panel1, null);
    insetsPanel3.add(label1, null);
    insetsPanel3.add(label2, null);
    insetsPanel3.add(label3, null);
    insetsPanel3.add(label4, null);
    panel2.add(insetsPanel3, BorderLayout.CENTER);
    insetsPanel1.add(button1, null);
    panel1.add(insetsPanel1, BorderLayout.SOUTH);
    panel1.add(panel2, BorderLayout.NORTH);
  }

  protected void processWindowEvent(WindowEvent e)
  {
    if (e.getID() == WindowEvent.WINDOW_CLOSING)
    {
      cancel();
    }
    super.processWindowEvent(e);
  }

  void cancel()
  {
    dispose();
  }

  public void actionPerformed(ActionEvent e)
  {
    if (e.getSource() == button1)
    {
      cancel();
    }
  }
}

 