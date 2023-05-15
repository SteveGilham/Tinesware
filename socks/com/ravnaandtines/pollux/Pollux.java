package com.ravnaandtines.pollux;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;
import com.ravnaandtines.util.image.*;
import com.ravnaandtines.net.socks5.HTTPProxy;

/**
*  Class Pollux - GUI for com.ravnaandtines.net.socks5.HTTPProxy
* <p>
* Copyright Mr. Tines &lt;tines@windsong.demon.co.uk&gt; 2000
* <p>
* For the purposes of the licence, this source file and the minimum set of
* other source files in package com.ravnaandtines and its sub-
* packages required to link together to satisfy all class
* references not belonging to standard Javasoft-published APIs constitute
* the library.  Thus it is not necessary to distribute source to classes
* that you do not actually use.
* <p>
* Note that Java's dynamic class loading means that the distribution of class
* files (as is, or in jar or zip form) which use this library is sufficient to
* allow run-time binding to any interface compatible version of this library.
* The GLPL is thus far less onerous for Java than for the usual run of 'C'/C++
* library.
* <p>
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Library General Public
* License as published by the Free Software Foundation; either
* version 2 of the License, or (at your option) any later version.
* <p>
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Library General Public License for more details.
* <p>
* You should have received a copy of the GNU Library General Public
* License along with this library; if not, write to the Free
* Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*<p>
* @author Mr. Tines
* @version 1.0 21-Feb-2000
*
*/

public class Pollux extends JFrame
{
    private class NumberField extends JTextField
    {
        public NumberField() {super();}
        public NumberField(int n) {super(n);}
        protected void processKeyEvent(KeyEvent e)
        {
            if(e.getModifiers()==0)
            {
                int keyChar = e.getKeyChar();
                if('\n'==keyChar ||
                ('0' <= keyChar && '9' >= keyChar))
                {
                    super.processKeyEvent(e);
                    postActionEvent();
                }
                int keyCode = e.getKeyCode();
                if(KeyEvent.VK_DELETE==keyCode ||
                   KeyEvent.VK_BACK_SPACE==keyCode)
                {
                    super.processKeyEvent(e);
                    postActionEvent();
                }
                if(KeyEvent.VK_LEFT==keyCode ||
                   KeyEvent.VK_RIGHT==keyCode)
                {
                    super.processKeyEvent(e);
                }
            }
        }
    }

  //Construct the frame
  BorderLayout borderLayout1 = new BorderLayout();
  JMenuBar menuBar1 = new JMenuBar();
  JMenu menuFile = new JMenu();
  JMenuItem menuFileExit = new JMenuItem();
  JMenu menuHelp = new JMenu();
  JMenuItem menuHelpAbout = new JMenuItem();
  JLabel statusBar = new JLabel();
  NumberField nf = new NumberField(10);
  JCheckBox cb = new JCheckBox("Verbose? ", false);
  JButton b = null;

  public Pollux()
  {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try 
    {
      jbInit();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
//Component initialization
  
  private void jbInit() throws Exception 
  {
    this.setIconImage(IconFoundry.getIcon(IconFoundry.ICON));
    this.getContentPane().setLayout(borderLayout1);
    this.setSize(new Dimension(400, 300));
    this.setTitle("Pollux");
    statusBar.setText(" ");
    menuFile.setText("File");
    menuFileExit.setText("Exit");
    menuFileExit.addActionListener(new ActionListener() 
    {
      public void actionPerformed(ActionEvent e)
      {
        fileExit_actionPerformed(e);
      }
    });
    menuHelp.setText("Help");
    menuHelpAbout.setText("About");
    menuHelpAbout.addActionListener(new ActionListener() 
    {
      public void actionPerformed(ActionEvent e)
      {
        helpAbout_actionPerformed(e);
      }
    });
    menuFile.add(menuFileExit);
    menuHelp.add(menuHelpAbout);
    menuBar1.add(menuFile);
    menuBar1.add(menuHelp);
    this.setJMenuBar(menuBar1);
    this.getContentPane().add(statusBar, BorderLayout.SOUTH);

    JPanel p = new JPanel();
    this.getContentPane().add(p, BorderLayout.NORTH);
    p.add(new JLabel("HTTP listening port:"));
    p.add(nf);
    nf.setText("80");

    p = new JPanel();
    p.setLayout(new BorderLayout());
    this.getContentPane().add(p, BorderLayout.CENTER);
    b = new JButton("Go", new ImageIcon(
      IconFoundry.getIcon(IconFoundry.TICK)));
    JPanel p2 = new JPanel(); p2.add(b);
    p.add( p2, BorderLayout.NORTH );
    p.add(cb, BorderLayout.SOUTH );

    b.addActionListener(new ActionListener() {
      int port = 0;
      public void actionPerformed(ActionEvent e) {
        try {
          port = Integer.parseInt(nf.getText());
          port &= 0xFFFF;
        } catch (NumberFormatException nfe) {return;}
        Thread t = new Thread( new Runnable() {
          public void run() { try {
            ServerSocket server = new ServerSocket (port);
            while (true) {
              Socket client = server.accept ();
              HTTPProxy echo = new HTTPProxy (client);
              echo.start ();
            }} catch(java.io.IOException ioe) {return;}
          }});
        HTTPProxy.setVerbose(cb.isSelected());
        t.start();
        b.setEnabled(false);
        cb.setEnabled(false);
        statusBar.setText("Running...");
      }});

  }
//File | Exit action performed
  
  public void fileExit_actionPerformed(ActionEvent e)
  {
    System.exit(0);
  }
//Help | About action performed
  
  public void helpAbout_actionPerformed(ActionEvent e)
  {
    Pollux_AboutBox dlg = new Pollux_AboutBox(this);
    Dimension dlgSize = dlg.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
    dlg.setModal(true);
    dlg.show();
  }
//Overriden so we can exit on System Close
  
  protected void processWindowEvent(WindowEvent e)
  {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING)
    {
      fileExit_actionPerformed(null);
    }
  }
}

 