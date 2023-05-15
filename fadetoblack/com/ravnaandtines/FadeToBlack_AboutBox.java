package com.ravnaandtines;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import com.ravnaandtines.util.text.Mnemonic;

/**
*  Class MainFrame_AboutBox
*  <P>
*  Coded & copyright Mr. Tines &lt;tines@windsong.demon.co.uk&gt; 1998
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
* @version 1.0 07-Nov-2001
*
*/


public class FadeToBlack_AboutBox extends JDialog implements ActionListener {

  Container panel1 = null;
  JPanel insetsPanel1 = new JPanel();
  JButton button1 = new JButton();
  BorderLayout borderLayout1 = new BorderLayout();

  FlowLayout flowLayout1 = new FlowLayout();

  private JTabbedPane mainTabset = new JTabbedPane();
  private Mnemonic about = new Mnemonic("A&bout");
  private Mnemonic gpl = new Mnemonic("&GPL");


  public FadeToBlack_AboutBox(Frame parent) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try  {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    pack();
  }

  private void jbInit() throws Exception  {
    this.setTitle("About Fade To Black");
    setResizable(false);
    panel1 = this.getContentPane();
    /*
    UIManager.put("TabbedPane.selected", Color.green);
    mainTabset.updateUI();
    */
    panel1.setLayout(borderLayout1);
    panel1.add(mainTabset, BorderLayout.CENTER);

    JComponent area0 = new JPanel();
    area0.setLayout(new GridLayout(0,1));
    area0.setBorder(javax.swing.BorderFactory.createEmptyBorder(15,85,35,15));
    area0.add(new JLabel("Fade to Black... - v0.8"));
    area0.add(new JSeparator(JSeparator.HORIZONTAL));
    area0.add(new JLabel("Copyright © 2004"));
    area0.add(new JLabel("Mr. Tines <tines@windsong.demon.co.uk>"));
    area0.add(new JSeparator(JSeparator.HORIZONTAL));
    area0.add(new JLabel("Fade-over filter for images"));
    area0.add(new JLabel("Select colour and direction of fade from the toolbar"));
    area0.add(new JLabel("Resize the image frame to get a solid patch of colour"));
    area0.add(new JLabel("beyond the input image."));
    area0.add(new JLabel("Use the slider to control where the fade starts."));
    mainTabset.add(area0, about, 0);
    mainTabset.registerKeyboardAction(
                new TabSelectAction(0),
                KeyStroke.getKeyStroke(
                    Character.toUpperCase(about.getMnemonic()),
                    ActionEvent.ALT_MASK, false),
                    JComponent.WHEN_IN_FOCUSED_WINDOW);

    JTextArea area2 = new JTextArea();
    area2.setLineWrap(true);
    area2.setWrapStyleWord(true);
    area2.setEditable(false);
    area2.setText(com.ravnaandtines.util.Licence.GNU_General_Public_Licence());
    area2.setCaretPosition(0);
    JScrollPane scroll = new JScrollPane();
    scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scroll.getViewport().add(area2);
    scroll.setPreferredSize(area0.getPreferredSize());
    mainTabset.add(scroll, gpl, 1);
    mainTabset.registerKeyboardAction(
                new TabSelectAction(1),
                KeyStroke.getKeyStroke(
                    Character.toUpperCase(gpl.getMnemonic()),
                    ActionEvent.ALT_MASK, false),
                    JComponent.WHEN_IN_FOCUSED_WINDOW);

    insetsPanel1.setLayout(flowLayout1);
    button1.setText("OK");
    button1.addActionListener(this);
    insetsPanel1.add(button1, null);

    panel1.add(insetsPanel1, BorderLayout.SOUTH);

  }

  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      cancel();
    }
    super.processWindowEvent(e);
  }

  void cancel() {
    dispose();
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == button1) {
      cancel();
    }
  }

  private class TabSelectAction extends AbstractAction
  {
    private int index = 0;
    public TabSelectAction(int index)
    {
      this.index = index;
    }
    public void actionPerformed(ActionEvent e)
    {
        FadeToBlack_AboutBox.this.mainTabset.setSelectedIndex(index);
    }
  }

}

 