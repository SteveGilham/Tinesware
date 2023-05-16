
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1998
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware

package com.ravnaandtines.ctcjava;

import java.net.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.security.MessageDigest;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;


public class SMTPTransmitter
{
    private JPanel bodyPanel = new JPanel();
    private JTextField from = new JTextField();
    private JTextField to = new JTextField();
    private JTextField subject = new JTextField();
    private JTextArea headerTA = new JTextArea(5, 30);
    private JTextField server = new JTextField();

    
    private String message = "";
    private java.awt.Frame owner = null;

    public SMTPTransmitter(java.awt.Frame frame, String messageBody)
    {
        owner = frame;

        JPanel top = new JPanel();
        
        GridBagLayout grid = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        
        JLabel label0 = new JLabel();
        JLabel label1 = new JLabel();
        JLabel label2 = new JLabel();
        JLabel label3 = new JLabel();
        
        JPanel headers = new JPanel();
        headers.setBorder(BorderFactory.createTitledBorder(GlobalData.getResourceString("Other_Mail_Headers:")));
       
        bodyPanel.setLayout(new BorderLayout());
        bodyPanel.add(headers, BorderLayout.CENTER);
        bodyPanel.add(top, BorderLayout.NORTH);

        top.setLayout(grid);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;

        label0.setText(GlobalData.getResourceString("From:"));
        label0.setHorizontalAlignment(JLabel.RIGHT);
        grid.setConstraints(label0, c);
        top.add(label0);

        c.gridx = 1;
        c.weightx = 0.7;
        c.gridwidth = 2;
        String tmp = Root.instance().getProperty("SMTPusername", "");
        from.setText(tmp);
        grid.setConstraints(from, c);
        top.add(from);

        ++c.gridy;
        c.gridx = 0;
        c.weightx = 0.0;
        c.gridwidth = 1;
        label1.setText(GlobalData.getResourceString("To:"));
        label1.setHorizontalAlignment(JLabel.RIGHT);
        grid.setConstraints(label1, c);
        top.add(label1);

        c.gridx = 1;
        c.weightx = 0.7;
        c.gridwidth = 2;
        to.setText("");
        grid.setConstraints(to, c);
        top.add(to);

        ++c.gridy;
        c.gridx = 0;
        c.weightx = 0.0;
        c.gridwidth = 1;
        label2.setText(GlobalData.getResourceString("Subject:"));
        label2.setHorizontalAlignment(JLabel.RIGHT);
        grid.setConstraints(label2, c);
        top.add(label2);

        c.gridx = 1;
        c.weightx = 0.7;
        c.gridwidth = 2;
        tmp = Root.instance().getProperty("SMTPsubject", "CTC encrypted message");
        subject.setText(tmp);
        grid.setConstraints(subject, c);
        top.add(subject);

        ++c.gridy;
        c.gridx = 0;
        c.weightx = 0.0;
        c.gridwidth = 1;
        label3.setText(GlobalData.getResourceString("SMTP_server:"));
        label3.setHorizontalAlignment(JLabel.RIGHT);
        grid.setConstraints(label3, c);
        top.add(label3);

        c.gridx = 1;
        c.weightx = 0.7;
        c.gridwidth = 2;
        tmp = Root.instance().getProperty("SMTPservername", "");
        server.setText(tmp);
        grid.setConstraints(server, c);
        top.add(server);

        headers.setLayout(new BorderLayout());
        headers.add(new JScrollPane(headerTA), BorderLayout.CENTER);
        //headerTA.setWordWrap(false);

        setText(messageBody);
    }

    public void show()
    {
        Object[] options = {
                    GlobalData.getResourceString("Send"),
                    GlobalData.getResourceString("Quit")};

        int result = JOptionPane.showOptionDialog(owner,
                bodyPanel,
                GlobalData.getResourceString("SMTP_transmission"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                1
                );
        if(0 == result)
            send();
    }
    
    private void setText(String s)
    {
        message = s;
        StringBuffer x = new StringBuffer("Message-Id: <");
        String sender =  getAddress(from.getText());
        if(null == sender) sender = "<>";

        try {
            // Build message ID header from time
            // and message, both digested for security
            long now = System.currentTimeMillis();
            MessageDigest hash = MessageDigest.getInstance("MD5");
            ByteArrayOutputStream b1 = new ByteArrayOutputStream(8);
            DataOutputStream cover = new DataOutputStream(b1);
            cover.writeLong(now);
            cover.flush();
            cover.close();
            b1.close();
            byte[] start = hash.digest(b1.toByteArray());

            DataInputStream i1 = new DataInputStream(
            new ByteArrayInputStream(start));
            now = i1.readLong();
            x.append(Long.toHexString(now));
            now = i1.readLong();
            x.append(Long.toHexString(now));
            i1.close();

            b1 = new ByteArrayOutputStream();
            cover = new DataOutputStream(b1);
            cover.writeUTF(s);
            cover.flush();
            cover.close();
            b1.close();
            byte[] next = hash.digest(b1.toByteArray());

            i1 = new DataInputStream(
            new ByteArrayInputStream(next));
            now = i1.readLong();
            x.append(Long.toHexString(now));
            now = i1.readLong();
            x.append(Long.toHexString(now));
            i1.close();

            x.append(sender.substring(1, sender.length()));

            headerTA.setText(x.toString());
            headerTA.setCaretPosition(0);
        }
        catch (java.security.NoSuchAlgorithmException NSA)
        {
            System.out.println("NSA");
        }
        catch (IOException io) {
            System.out.println("IO");
        }
    }

    private String getAddress(String part)
    {
        StringTokenizer parts = new StringTokenizer(part, " \t", false);
        while(parts.hasMoreTokens())
        {
            String thingy = parts.nextToken().trim();
            if(thingy.indexOf('@') < 0) continue;
            // hasan @ - may be an address.  Check for <xxx@xxx> form
            int index;
            if((index = thingy.lastIndexOf('<')) < 0)
            {
                thingy = '<'+thingy;
            }
            else
            {
                thingy = thingy.substring(index);
            }
            if((index = thingy.indexOf('>')) < 0)
            {
                thingy = thingy+'>';
            }
            else
            {
                thingy = thingy.substring(0, index+1);
            }
            return thingy;
        }
        return null;
    }


    private void send()
    {
        // need to code SMTP transaction here

        // phase 1 - parse the user names (crudely)
        StringTokenizer names = new StringTokenizer(to.getText(), ",", false);
        Vector<String> recipients = new Vector<String>(names.countTokens());
        while(names.hasMoreTokens())
        {
            String part = names.nextToken();
            if(part.indexOf('@') < 0)
            {
                JOptionPane.showMessageDialog(
                        owner,
                        GlobalData.getResourceString("Bad_recipient_email")+ part,
                        GlobalData.getResourceString("SMTP_transmission"),
                        JOptionPane.ERROR_MESSAGE
                        );
                return;
            }
            recipients.addElement(getAddress(part));
        }

        if(from.getText().indexOf('@') < 0)
        {
                JOptionPane.showMessageDialog(
                        owner,
                        GlobalData.getResourceString("Bad_sender_email")+from.getText(),
                        GlobalData.getResourceString("SMTP_transmission"),
                        JOptionPane.ERROR_MESSAGE
                        );
            return;
        }

        String sender =  getAddress(from.getText());

        Root.instance().setSMTPOptions(server.getText(), 
                from.getText(), subject.getText());

        // do SMTP thing
        Socket smtp;
        PrintWriter out;
        BufferedReader in;
        try{
            smtp = new Socket(server.getText(), 25);
            out = new PrintWriter(smtp.getOutputStream(), true);
            in = new BufferedReader( new InputStreamReader (
            smtp.getInputStream()));
        } 
        catch(Exception ex1) {
                JOptionPane.showMessageDialog(
                        owner,
                        GlobalData.getResourceString("Connection_failure:")+ex1.getLocalizedMessage(),
                        GlobalData.getResourceString("SMTP_transmission"),
                        JOptionPane.ERROR_MESSAGE
                        );
            return;
        }

        // login
        try {
            out.println("HELO "+ smtp.getInetAddress().getHostAddress());
            read(in);
        } 
        catch(Exception ex2) {
                JOptionPane.showMessageDialog(
                        owner,
                        GlobalData.getResourceString("Login_failure:")+ex2.getLocalizedMessage(),
                        GlobalData.getResourceString("SMTP_transmission"),
                        JOptionPane.ERROR_MESSAGE
                        );
            return;
        }

        // say who from and to
        try{
            out.println("MAIL FROM: "+sender);
            read(in);
        } 
        catch(Exception ex3) {
                JOptionPane.showMessageDialog(
                        owner,
                        GlobalData.getResourceString("Bad_sender:")+ex3.getLocalizedMessage(),
                        GlobalData.getResourceString("SMTP_transmission"),
                        JOptionPane.ERROR_MESSAGE
                        );
            return;
        }

        Enumeration e = recipients.elements();
        while(e.hasMoreElements())
        {
            String s = (String) e.nextElement();
            try {
                out.println("RCPT TO: "+s);
                read(in);
            } 
            catch(Exception ex4) {
                JOptionPane.showMessageDialog(
                        owner,
                        GlobalData.getResourceString("Bad_recipient:")+ex4.getLocalizedMessage(),
                        GlobalData.getResourceString("SMTP_transmission"),
                        JOptionPane.ERROR_MESSAGE
                        );
                return;
            }
        }

        // now do the message
        try{
            out.println("DATA");
            read(in);
            out.println("To: "+to.getText());
            out.println("From: "+from.getText());
            out.println("Subject: "+subject.getText());

            if(headerTA.getText() != null)
            {
                out.print(headerTA.getText());
                // force a blank line here
                out.println("");
            }

            out.println("");
            out.print(message);
            out.println("");
            out.println(".");
            out.flush();

            read(in);
        } 
        catch(Exception ex5) {
                JOptionPane.showMessageDialog(
                        owner,
                        GlobalData.getResourceString("Transmission_error:")+ex5.getLocalizedMessage(),
                        GlobalData.getResourceString("SMTP_transmission"),
                        JOptionPane.ERROR_MESSAGE
                        );
            return;
        }
    }// end send

    private String read (BufferedReader in) throws IOException
    {
        String line;
        do{
            line = in.readLine();
        }
        while(in.ready());

        try {
            NumberFormat nf = NumberFormat.getInstance();
            int code = nf.parse(line.substring(0,3)).intValue();
            if(code >= 400)
            {
                throw new IOException(line);
            }
        } 
        catch (ParseException ex1) {
            throw new IOException(GlobalData.getResourceString("No_response_code:")+ line);
        }
        return line;
    }

}

