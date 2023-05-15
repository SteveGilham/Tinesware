package com.ravnaandtines.castor;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;
import com.ravnaandtines.net.gui.AdderPanel;
import com.ravnaandtines.util.image.*;
import com.ravnaandtines.net.socks5.UDPTunnel;
/**
*  Class Castor - GUI for com.ravnaandtines.net.socks5.UDPTunnel
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

public class Castor extends JFrame
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
  AdderPanel ap = new AdderPanel("Intermediates", "Socks proxies between here and the server");
  NumberField nf = new NumberField(10);
  JTextField host = new JTextField(10);
  JButton b = null;
  JCheckBox cb = new JCheckBox("Verbose? ", false);

  public Castor()
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
    this.setTitle("Castor");
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
    p.setLayout(new GridLayout(1,2));
    this.getContentPane().add(p, BorderLayout.CENTER);
    p.add(ap);
    JPanel p2 = new JPanel();
    p.add(p2);

    JComponent c = null;
    p2.add(c = new JLabel("Pollux server (HTTP Proxy)"));
    c.setToolTipText("blank means localhost");
    p2.add(host);
    p2.add(new JLabel("HTTP listening port:"));
    p2.add(nf);
    nf.setText("80");

    b = new JButton("Go", new ImageIcon(
      IconFoundry.getIcon(IconFoundry.TICK)));
    p2.add(b);
    p2.add(cb);

    b.addActionListener(new ActionListener() {
      int port = 0;
      InetAddress hserver = null;
      Object [] proxies = null;
      public void actionPerformed(ActionEvent e) {
        try {
          port = Integer.parseInt(nf.getText());
          port &= 0xFFFF;
        } catch (NumberFormatException nfe) {return;}
        try {
          hserver = InetAddress.getByName(host.getText());
        } catch (Exception uhex) {return;}
        proxies = ap.save();

        Thread t = new Thread( new Runnable() {
          public void run() { try {
            ServerSocket server = new ServerSocket (UDPTunnel.port);
            while (true) {
              Socket client = server.accept ();
              UDPTunnel echo = new UDPTunnel (client, hserver, port);
              echo.setProxies(proxies);
              echo.start ();
            }} catch(java.io.IOException ioe) {return;}
          }});
        UDPTunnel.setVerbose(cb.isSelected());
        t.start();
        b.setEnabled(false);
        cb.setEnabled(false);
        ap.lock();
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
    Castor_AboutBox dlg = new Castor_AboutBox(this);
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

 