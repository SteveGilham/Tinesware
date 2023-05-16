
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. Tines <tines@windsong.demon.co.uk>
//Company:      Ravna&Tines
//Description:  A Java[tm]1.1-based portable GUI to CTClib2.0


package com.ravnaandtines.ctcjava;

import java.awt.*;
import java.awt.event.*;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.border.BevelBorder;

public class LicenceDialog  {
    private static class JLimitedPanel extends JPanel
    {
        JLimitedPanel(Component c) {setLayout(new BorderLayout()); add(c, BorderLayout.CENTER); }
        public Dimension getMaximumSize() {            
            return new Dimension(super.getMaximumSize().width, 300);
        }
        public Dimension getPreferredSize() {
            return new Dimension(super.getPreferredSize().width, 300);
        }
    }

    static native String getGPL();
    static native String getIDEA();
    
    private static JPanel getAboutPanel()
    {
        JPanel about = new JPanel();
        about.setLayout(new BorderLayout());
        about.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        
        JLabel image = new JLabel();
        image.setBorder(BorderFactory.createEmptyBorder(7,7,7,7));
        
        try {
                java.io.InputStream in = LicenceDialog.class.getResourceAsStream("/com/ravnaandtines/tinelogo.png");
                java.awt.Image theImage  = javax.imageio.ImageIO.read(in);
                image.setIcon(new ImageIcon( theImage ));
            } catch (java.io.IOException io) {
            } catch (IllegalArgumentException io) {                
            }                        

                image.setToolTipText(GlobalData.getResourceString("ATines_pack_(after_V"));
        
        about.add(image, BorderLayout.WEST);
        
        JPanel bevelPanel = new JPanel();
        bevelPanel.setBorder(BorderFactory.createEmptyBorder(7,7,7,7));
        bevelPanel.setLayout(new GridLayout(11,1));
        about.add(bevelPanel, BorderLayout.CENTER);
        
        JLabel productLabel   = new JLabel();
        Object[] args = { GlobalData.libVersion(), "v2.0.-1" };
        productLabel.setText(java.text.MessageFormat.format(
            GlobalData.getResourceString("CTC2_0_for_Java_Free"), args));
        
        JLabel productLabel2   = new JLabel();
        Object[] args1 = {         GlobalData.libVersion()         };
        productLabel2.setText(java.text.MessageFormat.format(
                    GlobalData.getResourceString("Uses_CTClib_2_x"), args1));                
        
        JLabel copyrightLabel = new JLabel();
        Object[] args2 = {    "© 1998-2006"        };
        copyrightLabel.setText(java.text.MessageFormat.format(
            GlobalData.getResourceString("Copyright_©_1998"), args2));
        copyrightLabel.setToolTipText(GlobalData.getResourceString("Copyleft_actually_see"));
                
        JLabel tinesLabel     = new JLabel();
        JLabel ianLabel       = new JLabel();
        JLabel spaceLabel     = new JLabel("");
        JLabel icon1Label     = new JLabel();
        JLabel icon2Label     = new JLabel();
        JLabel icon3Label     = new JLabel();
        JLabel icon4Label     = new JLabel();
        JLabel icon5Label     = new JLabel();

        tinesLabel.setText(GlobalData.getResourceString("Mr_Tines"));
        ianLabel.setText(GlobalData.getResourceString("&Ian_Miller"));

        icon1Label.setText(GlobalData.getResourceString("Non-Tines"));
        icon2Label.setText(GlobalData.getResourceString("tango"));
        icon3Label.setText(GlobalData.getResourceString("CC-SA"));
        icon4Label.setText(GlobalData.getResourceString("DeansIcons"));
        icon5Label.setText(GlobalData.getResourceString("BSD"));        
        
        bevelPanel.add(productLabel);
        bevelPanel.add(productLabel2);
        bevelPanel.add(copyrightLabel);
        bevelPanel.add(tinesLabel);
        bevelPanel.add(ianLabel);
        bevelPanel.add(spaceLabel);
        bevelPanel.add(icon1Label);
        bevelPanel.add(icon2Label);
        bevelPanel.add(icon3Label);
        bevelPanel.add(icon4Label);
        bevelPanel.add(icon5Label);
        
        return about;
    }
    
    
    public static void showAll(Component parent)
    {
        JTabbedPane body = new JTabbedPane();
        JLimitedPanel l = new JLimitedPanel(body);
        
        
        body.add(GlobalData.getResourceString("About"),
                getAboutPanel());
        body.setMnemonicAt(0, java.awt.event.KeyEvent.VK_A);
        
        JTextArea a = new JTextArea();
        a.setEditable(false);
        a.setText(getGPL());
        a.setCaretPosition(0);
        JScrollPane s = new JScrollPane(a);
        
        body.add(GlobalData.getResourceString("Licence (GPL)"), s);
        body.setMnemonicAt(1, java.awt.event.KeyEvent.VK_G);

        if(KeyConstants.isIDEAenabled()) {
            a = new JTextArea();
            a.setEditable(false);
            a.setText(getIDEA());
            a.setCaretPosition(0);
            s = new JScrollPane(a);
            
            body.add(GlobalData.getResourceString("Licence (IDEA)"), s);
            body.setMnemonicAt(2, java.awt.event.KeyEvent.VK_I);
        }
      
        JOptionPane.showMessageDialog(parent, l, 
                GlobalData.getResourceString("About CTCJava"), JOptionPane.PLAIN_MESSAGE);        
    }
}

