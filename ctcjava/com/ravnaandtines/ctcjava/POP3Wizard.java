
//Title:        CTC2.0 for Java
//Version:
//Copyright:    Copyright (c) 1998
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware

package com.ravnaandtines.ctcjava;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.net.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.security.MessageDigest;
import com.nexes.wizard.*;
import javax.swing.*;

public class POP3Wizard {
    Wizard w = null;
    ServerStep s = null;
    MessageStep m = null;
    Frame owner;
    
    public POP3Wizard(Frame owner) {
        this.owner = owner;
        w = new Wizard(owner);
        w.setTitle(GlobalData.getResourceString("POP3_retrieval"));
        
        s = new ServerStep(this);
        w.registerWizardPanel(s.getPanelDescriptorIdentifier(),s);
        w.setCurrentPanel(s.getPanelDescriptorIdentifier());
        
        m = new MessageStep(this);
        w.registerWizardPanel(m.getPanelDescriptorIdentifier(),m);
    }
    
    public int show() {
        
        try {
            int result =  w.showModalDialog();
            if(result != Wizard.FINISH_RETURN_CODE)
                return result;
            retrieveAction();
            return result;
        } finally {
            if(m.connected) {
                try {
                    mOut.println("QUIT ");
                    getOK();
                    m.connected = false;
                    s.apopTag = null;
                } catch(Exception ex1) {            /* not a lot we can do */
                }
            }            
        }
    }
    
    private class ServerStep extends CommonDescriptor {
        JTextField username = new JTextField(Root.instance().getProperty("POP3username", ""));
        JTextField pop3server = new JTextField(Root.instance().getProperty("POP3servername", ""));
        JPasswordField password = new JPasswordField();
        JCheckBox apopCheck = new JCheckBox(GlobalData.getResourceString("Use_secure_login"), false);
        POP3Wizard owner = null;
        String apopTag = null;
        
        ServerStep(POP3Wizard owner) {
            super();
            this.owner = owner;
            setPanelDescriptorIdentifier(ServerStep.class.getName());
            JLabel title = new JLabel(GlobalData.getResourceString("POP3 Configuration"));
            JLabel rubric = new JLabel("  "+GlobalData.getResourceString("POP3 stuff"));
            top.add(title);
            top.add(rubric);
            
            body.setLayout(new java.awt.GridBagLayout());
            java.awt.GridBagConstraints c = new java.awt.GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.gridwidth = 1;
            c.gridheight = 1;
            c.weightx = 0.0;
            c.weighty = 1.0;
            c.fill = java.awt.GridBagConstraints.HORIZONTAL;
            c.ipadx = 5;
            c.anchor = java.awt.GridBagConstraints.EAST;
            
            JLabel label0 = new JLabel(GlobalData.getResourceString("POP3_user_name"));
            body.add(label0, c);
            
            c.gridx = 1;
            c.weightx = 1;
            c.gridwidth = 2;
            body.add(username, c);
            
            ++c.gridy;
            c.gridx = 0;
            c.weightx = 0;
            c.gridwidth = 1;
            c.ipadx = 5;
            c.anchor = java.awt.GridBagConstraints.EAST;
            JLabel label1 = new JLabel(GlobalData.getResourceString("POP3_server_name"));
            body.add(label1, c);
            
            c.gridx = 1;
            c.weightx = 1;
            c.gridwidth = 2;
            body.add(pop3server, c);
            
            ++c.gridy;
            c.gridx = 0;
            c.weightx = 0;
            c.gridwidth = 1;
            c.ipadx = 5;
            c.anchor = java.awt.GridBagConstraints.EAST;
            JLabel label2 = new JLabel(GlobalData.getResourceString("POP3_password"));
            body.add(label2, c);
            
            c.gridx = 1;
            c.weightx = 1;
            c.gridwidth = 2;
            password.setEchoChar('*');
            body.add(password, c);
            
            ++c.gridy;
            c.gridx = 0;
            c.weightx = 1;
            c.gridwidth = 2;
            body.add(apopCheck, c);
            apopCheck.setEnabled(false);
            
            POP3Wizard.this.w.getDialog().addWindowListener(
                    new java.awt.event.WindowListener() {
                public void windowActivated(WindowEvent e) {
                    password.setRequestFocusEnabled(true);
                    password.requestFocusInWindow();
                }
                public void windowClosed(WindowEvent e) {
                }
                public void windowClosing(WindowEvent e) {
                }
                public void windowDeactivated(WindowEvent e) {
                }
                public void windowDeiconified(WindowEvent e) {
                }
                public void windowIconified(WindowEvent e) {
                }
                public void windowOpened(WindowEvent e) {
                    password.setRequestFocusEnabled(true);
                    password.requestFocusInWindow();
                }
            }
            );
        }
        public String getNextPanelDescriptor() {
            return MessageStep.class.getName();
        }
 /*       
        public void aboutToDisplayPanel() {
            password.setRequestFocusEnabled(true);
            password.requestFocusInWindow();
        }
  */
        
    }
    
    private class MessageStep extends CommonDescriptor {
        JPanel pane = new JPanel();
        JLabel status = new JLabel();
        boolean topSupport = false;
        JCheckBox[] collect = null;
        JCheckBox[] delete = null;
        JTextArea[] headers = null;
        boolean connected = false;
        POP3Wizard owner = null;
        
        MessageStep(POP3Wizard owner) {
            super();
            this.owner = owner;
            setPanelDescriptorIdentifier(MessageStep.class.getName());
            JLabel title = new JLabel(GlobalData.getResourceString("Messages title"));
            JLabel rubric = new JLabel("  "+GlobalData.getResourceString("Messages stuff"));
            top.add(title);
            top.add(rubric);
            
            body.setLayout(new java.awt.BorderLayout());
            body.add(new JScrollPane(pane), java.awt.BorderLayout.CENTER);
            body.add(status, java.awt.BorderLayout.NORTH);
        }
        public String getNextPanelDescriptor() {
            return WizardPanelDescriptor.FinishIdentifier;
        }
        
        public void aboutToDisplayPanel() {
            if(!owner.connectAction()) {
                getWizard().forceCancel();
            }
        }
        
    }
    
    public Vector<POP3RetrievedMessage> getMessage() {
        return values;
    }
    

    private String getOK() throws IOException {
        // check for response
        String line = mIn.readLine();
        if(line.substring(0,3).equals("+OK")) {
            return line;
        }
        throw new IOException(line);
        // the compiler understands I don't need to return
        // unlike many C++ compilers :(
    }
    
    Socket mSocket = null;
    PrintWriter mOut = null;
    BufferedReader mIn = null;
    int size = -1;
    boolean topSupport = false;
    Vector<POP3RetrievedMessage> values = null;
    
    
    boolean connectAction() {
        String server = s.pop3server.getText();
        String user = s.username.getText();
        
        Root.instance().setPOP3Options(server, user);
        
        try {
            // make the socket
            if(mSocket != null) {
                if(m.connected) {
                    mOut.println("QUIT ");
                    getOK();
                    m.connected = false;
                    s.apopTag = null;
                }
                
                mOut.close();
                mIn.close();
                mSocket.close();
            }
            mSocket = new Socket(server, 110);
            mOut = new PrintWriter(mSocket.getOutputStream(), true);
            mIn = new BufferedReader(new InputStreamReader(
                    mSocket.getInputStream() ));
            String line = getOK();
            m.connected = true;
            
            // set APOP tag if any
            {
                StringTokenizer t = new StringTokenizer(line);
                String token = t.nextToken();
                while(t.hasMoreTokens()) {
                    token = t.nextToken();
                }
                // crude parsing
                if(token.charAt(0) == '<' &&
                        token.indexOf('@') > 0 &&
                        token.indexOf('>') == token.length()-1) {
                    s.apopTag = token;
                    s.apopCheck.setEnabled(true);
                } else {
                    s.apopTag = null;
                    s.apopCheck.setEnabled(false);
                }
                
            }
            
        } catch(Exception ex1) {
            JOptionPane.showMessageDialog(owner,
                    GlobalData.getResourceString("Connection_failure:")+ex1.getLocalizedMessage(),
                    GlobalData.getResourceString("POP3_retrieval"),
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try { // login
            
            if(s.apopTag == null || !s.apopCheck.isSelected()) {
                mOut.println("USER "+user);
                getOK();
                
                mOut.print("PASS ");
                char [] pw = s.password.getPassword();
                mOut.println(pw);
                java.util.Arrays.fill(pw, (char)0);
                getOK();
            } else // build APOP message
            {
                MessageDigest hash = MessageDigest.getInstance("MD5");
                ByteArrayOutputStream b1 = new ByteArrayOutputStream(8);
                DataOutputStream cover = new DataOutputStream(b1);
                cover.writeUTF(s.apopTag);
                CryptoString cs = new CryptoString(s.password.getPassword());
                byte[] utf = new byte[cs.utf8length()];
                cs.getUTF8(utf);
                cs.wipe();
                cover.write(utf);
                java.util.Arrays.fill(utf, (byte)0);
                cover.flush();
                cover.close();
                b1.close();
                byte[] apop = hash.digest(b1.toByteArray());
                
                StringBuffer x = new StringBuffer("APOP ");
                x.append(s.username.getText());
                x.append(" ");
                
                DataInputStream i1 = new DataInputStream(
                        new ByteArrayInputStream(apop));
                long chunk = i1.readLong();
                x.append(Long.toHexString(chunk).toLowerCase());
                chunk = i1.readLong();
                x.append(Long.toHexString(chunk).toLowerCase());
                i1.close();
                
                mOut.println(x.toString());
                getOK();
                
            }
        } catch (Exception ex2) {
            JOptionPane.showMessageDialog(owner,
                    GlobalData.getResourceString("Logon_failure:")+ex2.getLocalizedMessage(),
                    GlobalData.getResourceString("POP3_retrieval"),
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        size = -1;
        try { // get count
            mOut.println("STAT");
            String line = getOK();
            StringTokenizer st = new StringTokenizer(line);
            st.nextToken(); // skip status message
            NumberFormat nf = NumberFormat.getInstance();
            size = nf.parse(st.nextToken()).intValue();
        } catch (Exception ex3) {
            JOptionPane.showMessageDialog(owner,
                    GlobalData.getResourceString("Count_failure:")+ex3.getLocalizedMessage(),
                    GlobalData.getResourceString("POP3_retrieval"),
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        switch(size) {
            case 0:
                m.status.setText(GlobalData.getResourceString("There_are_no_messages"));
                break;
            case 1:
                m.status.setText(GlobalData.getResourceString("There_is_one_message"));
                break;
            default:
            {
                Object[] args = {
                    new Integer(size)                 };
                    m.status.setText(MessageFormat.format(
                            GlobalData.getResourceString("There_are_0_number"),
                            args));
                    break;
            }
        }
        if(size > 0) {
            // does the POP3 server support TOP?
            try {
                mOut.println("TOP 1 0");
                String line = getOK();
                POP3RetrievedMessage mtop = new POP3RetrievedMessage(mIn);
                topSupport = true;
            } catch (IOException ex4) {
            }
            
            // int n = topSupport ? 3 : 2;
            
            java.awt.GridLayout lay = new java.awt.GridLayout(size, 1);
            m.pane.setLayout(lay);
            
            
            m.collect = new JCheckBox[size];
            m.delete = new JCheckBox[size];
            if(topSupport) {
                m.headers = new JTextArea[size];
            }
            
            for(int i=1; i<=size; ++i) {
                JPanel unit = new JPanel();
                unit.setBorder(BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
                
                Object[] args = new Object[1];
                args[0] = new Integer(i);
                
                m.collect[i-1] = new JCheckBox(
                        MessageFormat.format(GlobalData.getResourceString("Collect_message_0"), args),
                        false);
                
                m.delete[i-1] = new JCheckBox(
                        MessageFormat.format(GlobalData.getResourceString("Delete_message_0"), args),
                        false);
                if(topSupport) {
                    try{
                        mOut.println("TOP "+i+" 5");
                        String line = getOK();
                        POP3RetrievedMessage msg = new POP3RetrievedMessage(mIn);
                        m.headers[i-1] = new JTextArea(msg.toString(), 3, 30);
                        m.headers[i-1].setEditable(false);
                        
                        JPanel bar = new JPanel();
                        bar.add(m.collect[i-1]);
                        bar.add(m.delete[i-1]);
                        unit.setLayout(new java.awt.BorderLayout());
                        unit.add(bar, java.awt.BorderLayout.SOUTH);
                        unit.add(new JScrollPane(m.headers[i-1]),java.awt.BorderLayout.CENTER);
                    } catch (IOException ex1) {
                        unit.add(m.collect[i-1]);
                        unit.add(m.delete[i-1]);
                    }
                } else {
                    unit.add(m.collect[i-1]);
                    unit.add(m.delete[i-1]);
                }
                m.pane.add(unit);
            }
            m.pane.invalidate();
        }
        return true;
    }
    
    void retrieveAction() {
        int upper = size;
        POP3RetrievedMessage msg = null;
        boolean OK = true;
        String line = "";
        
        for(int i=1; i<=size && OK; ++i) {
            if(!m.collect[i-1].isSelected()) continue;
            
            Object[] args = { new Integer(i), new Integer(size)  };
            Application.instance().setStatusText(MessageFormat.format(
                    GlobalData.getResourceString("Retrieving_message_0"),
                    args));
            try {
                line = "";
                mOut.println("RETR "+i);
                line = getOK();
                msg = new POP3RetrievedMessage(mIn);
            } catch (IOException ex1) {
                OK = false;
                
            JOptionPane.showMessageDialog(owner,
                    MessageFormat.format(
                        GlobalData.getResourceString("Problem_retrieving"),
                        args) + line + " " + ex1.getLocalizedMessage(),
                    GlobalData.getResourceString("POP3_retrieval"),
                    JOptionPane.ERROR_MESSAGE);
                break;
            }
            if(msg != null) {
                if(null == values) values = new Vector<POP3RetrievedMessage>(upper);
                values.addElement(msg);
            }
            
            if(m.delete[i-1].isSelected()) {
                try {
                    line = "";
                    mOut.println("DELE "+i);
                    line = getOK();
                    // can't do any more with it
                    m.collect[i-1].setEnabled(false);
                    m.delete[i-1].setEnabled(false);
                } catch (IOException ex1) {
                    //Delete fails safe, so don't stop.
                    Application.instance().setStatusText(MessageFormat.format(
                            GlobalData.getResourceString("Problem_deleting"),
                            args) + line + " " + ex1.getLocalizedMessage());
                    break;
                }
            }
        }
    }
}

