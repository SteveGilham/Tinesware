
//Title:      Janus
//Version:    
//Copyright:  Copyright (c) 2000
//Author:     Mr. Tines
//Company:    Ravna&Tines
//Description:A multi-hop SOCKS relay service
package com.ravnaandtines.janus;
/**
*  Class MainFrame
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
import java.io.*;
import java.util.*;
import javax.swing.*;
import com.ravnaandtines.util.image.*;

public class MainFrame extends JFrame
{

  //Construct the frame
  BorderLayout borderLayout1 = new BorderLayout();
  ResourceBundle res = ResourceBundle.getBundle("com.ravnaandtines.janus.Res");
  JMenuBar menuBar1 = new JMenuBar();
  JMenu menuFile = new JMenu();
  JMenuItem menuFileExit = new JMenuItem();
  JMenu menuHelp = new JMenu();
  JMenuItem menuHelpAbout = new JMenuItem();
  JToolBar toolBar = new JToolBar();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  JButton jButton3 = new JButton();
  JButton jButton4 = new JButton();
  JCheckBox jCheck = new JCheckBox(res.getString("SQUID"), false);
  ImageIcon image1;
  ImageIcon image2;
  ImageIcon image3;
  ImageIcon image4;
  JLabel statusBar = new JLabel();

  AdderPanel proxies = null;
  AdderPanel targets = null;
  String file;
  static JLabel status = null;

  public MainFrame(String[] args)
  {
    file = (args != null && args.length > 0) ? args[0] : null;
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    status = statusBar;
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
    image1 = new ImageIcon(IconFoundry.getIcon(IconFoundry.FILEOPEN));
    image2 = new ImageIcon(IconFoundry.getIcon(IconFoundry.FILESAVE));
    image3 = new ImageIcon(IconFoundry.getIcon(IconFoundry.HELP));
    image4 = new ImageIcon(IconFoundry.getIcon(IconFoundry.TICK));
    this.getContentPane().setLayout(borderLayout1);
    this.setSize(new Dimension(400, 300));
    this.setTitle("Janus");
    statusBar.setText(" ");
    menuFile.setText(res.getString("File"));
    menuFileExit.setText(res.getString("Exit"));
    menuFileExit.addActionListener(new ActionListener() 
    {
      public void actionPerformed(ActionEvent e)
      {
        fileExit_actionPerformed(e);
      }
    });
    menuHelp.setText(res.getString("Help"));
    menuHelpAbout.setText(res.getString("About"));
    menuHelpAbout.addActionListener(new ActionListener() 
    {
      public void actionPerformed(ActionEvent e)
      {
        helpAbout_actionPerformed(e);
      }
    });
    jButton1.setIcon(image1);
    jButton1.setToolTipText(res.getString("Open_configuration"));
    jButton2.setIcon(image2);
    jButton2.setToolTipText(res.getString("Save_configuration"));
    jButton3.setIcon(image3);
    jButton3.setToolTipText(res.getString("Help_About"));
    jButton4.setIcon(image4);
    jButton4.setToolTipText(res.getString("Run"));
    toolBar.add(jButton1);
    toolBar.add(jButton2);
    toolBar.add(jButton3);
    toolBar.add(jButton4);
    toolBar.add(jCheck);
    menuFile.add(menuFileExit);
    menuHelp.add(menuHelpAbout);
    menuBar1.add(menuFile);
    menuBar1.add(menuHelp);
    this.setJMenuBar(menuBar1);
    this.getContentPane().add(toolBar, BorderLayout.NORTH);
    this.getContentPane().add(statusBar, BorderLayout.SOUTH);

    JPanel main = new JPanel();
    this.getContentPane().add(main, BorderLayout.CENTER);
    main.setLayout(new GridLayout(1,2));
    main.add(proxies = new AdderPanel(res.getString("SOCKS5_Proxies"),
      res.getString("local_proxy_at_top")));
    main.add(targets = new AdderPanel(res.getString("Targets_host_port"),
      res.getString("localhost_listens_on")));

    doLoad();

    jButton1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      FileDialog f = new FileDialog(MainFrame.this,
        res.getString("Open_configuration"), FileDialog.LOAD);
      if(null != file && file.length() > 0 )
      {
        File wantFile = new File(file);
        String x = wantFile.getName();
        if(x != null) f.setFile(x);
        x = wantFile.getParent();
        if(x != null) f.setDirectory(x);
      }
      f.show();
      if(f.getFile() == null) return;
      if(f.getDirectory() == null) return;
      file = f.getDirectory()+f.getFile();
      doLoad();
    }});

    jButton2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      FileDialog f = new FileDialog(MainFrame.this,
        res.getString("Save_configuration"), FileDialog.SAVE);
      if(null != file && file.length() > 0 )
      {
        File wantFile = new File(file);
        String x = wantFile.getName();
        if(x != null) f.setFile(x);
        x = wantFile.getParent();
        if(x != null) f.setDirectory(x);
      }
      f.show();
      if(f.getFile() == null) return;
      if(f.getDirectory() == null) return;
      file = f.getDirectory()+f.getFile();
      doSave();
    }});

    jButton3.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        helpAbout_actionPerformed(e);
    }});

    jButton4.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String [] to = targets.save();
        String [] via = proxies.save();
        if(to == null || to.length == 0) return;
        if(via == null || via.length == 0) return;

        boolean state = jCheck.getModel().isSelected();

        proxies.lock(); targets.lock();
        for(int i=0; i<to.length;++i)
        {
          // launch servers
          int ix = to[i].indexOf(":");
          if(ix <=0 ) continue;
          String host = to[i].substring(0, ix);
          try {
            String p = to[i].substring(ix+1);
            int port = Integer.parseInt(p);
            ServerSocket server = new ServerSocket(host, port,via, state);
            server.start();
          } catch (Exception exj4)
          {
            JOptionPane.showMessageDialog(null,
            exj4.toString()+" for "+to[i]+"   .",
            "Janus", JOptionPane.ERROR_MESSAGE);
          }
        }
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
    MainFrame_AboutBox dlg = new MainFrame_AboutBox(this);
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

  private void doLoad()
  {
    if(null == file || file.length() < 1 ) return;
    File wantFile = new File(file);
    if(!wantFile.exists() || !wantFile.isFile()
        || !wantFile.canWrite() || !wantFile.canRead()) return;
    try {
         FileInputStream fis = new FileInputStream(file);
         Properties stat = new Properties();
         stat.load(fis);
         fis.close();
         Vector v = new Vector();
         String s = null;
         int i = 0;
         for(;;) {
          s = stat.getProperty("proxy"+i);
          ++i; if (null==s) break;
          v.addElement(s);
         }
         proxies.load(v);
         v = new Vector();
         i = 0;
         for(;;) {
          s = stat.getProperty("target"+i);
          ++i; if (null==s) break;
          v.addElement(s);
         }
         targets.load(v);
         s = stat.getProperty("squid");
         boolean isSquid = s!=null && s.equals("true");
         jCheck.getModel().setSelected(isSquid);
      } catch (IOException loadex) {return;}
  }

  private void doSave()
  {
    if(null == file || file.length() < 1 ) return;
    try {
         FileOutputStream fos = new FileOutputStream(file);
         Properties stat = new Properties();
         String[] proxy = proxies.save();
         int i;
         for(i=0; i<proxy.length;++i) stat.put("proxy"+i, proxy[i]);
         String[] target = targets.save();
         for(i=0; i<target.length;++i) stat.put("target"+i, target[i]);
         Boolean b = new Boolean(jCheck.getModel().isSelected());
         stat.put("squid", b.toString());
         stat.save(fos, "Janus configuration file");
         fos.close();
      } catch (IOException loadex) {return;}
  }
}

 