
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1998
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware

package com.ravnaandtines.ctcjava;

import java.awt.Dialog;
import java.awt.Frame;
import java11.awt.event.*;
import dtai.gwt.*;
import java.net.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.security.MessageDigest;

public class SMTPDialog extends Dialog
{
    static ResourceBundle res = ResourceBundle.getBundle("com.ravnaandtines.ctcjava.Res");
    GadgetPanel bodyPanel = new GadgetPanel();
    GadgetBorderLayout borderLayout1 = new GadgetBorderLayout();

    PanelGadget top = new PanelGadget();

    private GadgetGridBagLayout grid = new GadgetGridBagLayout();
    private GadgetGridBagConstraints c = new GadgetGridBagConstraints();

    LabelGadget label0 = new LabelGadget();
    TextFieldGadget from = new TextFieldGadget();
    LabelGadget label1 = new LabelGadget();
    TextFieldGadget to = new TextFieldGadget();
    LabelGadget label2 = new LabelGadget();
    TextFieldGadget subject = new TextFieldGadget();
    LabelGadget label3 = new LabelGadget();
    TextFieldGadget server = new TextFieldGadget();

    PanelGadget midfield = new PanelGadget();

    GroupBox box = new GroupBox(res.getString("Status"));
    GroupBox headers = new GroupBox(res.getString("Other_Mail_Headers:"));

    TextAreaGadget headerTA = new TextAreaGadget(5, 30);
    LabelGadget status = new LabelGadget();

    PanelGadget buttons = new PanelGadget();
    GadgetFlowLayout flow = new GadgetFlowLayout(GadgetFlowLayout.CENTER,
    5,5);
    ButtonGadget send = new ButtonGadget(res.getString("Send"));
    ButtonGadget can = new ButtonGadget(res.getString("Quit"));

    String message = "";
    Frame owner = null;

    public SMTPDialog(Frame frame, String title, boolean modal)
    {
        super(frame, title, modal);
        try
        {
            owner = frame;
            jbInit();
            pack();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    public SMTPDialog(Frame frame)
    {
        this(frame, true);
    }


    public SMTPDialog(Frame frame, boolean modal)
    {
        this(frame, res.getString("SMTP_transmission"), modal);
    }


    public SMTPDialog(Frame frame, String title)
    {
        this(frame, title, true);
    }

    void jbInit() throws Exception
    {
        this.enableEvents(java.awt.AWTEvent.WINDOW_EVENT_MASK);
        this.add("Center", bodyPanel);
        bodyPanel.setLayout(borderLayout1);
        bodyPanel.add("Center", midfield);
        bodyPanel.add("North", top);

        // gridbag these layouts here and in POP3 

        top.setLayout(grid);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0.0;
        c.weighty = 1.0;
        c.fill = GadgetGridBagConstraints.HORIZONTAL;

        label0.setLabel(res.getString("From:"));
        label0.setHorizAlign(LabelGadget.HORIZ_ALIGN_RIGHT);
        grid.setConstraints(label0, c);
        top.add(label0);

        c.gridx = 1;
        c.weightx = 0.7;
        c.gridwidth = 2;
        String tmp = CJGlobals.settings.getProperty("SMTPusername", "");
        from.setText(tmp);
        grid.setConstraints(from, c);
        top.add(from);

        ++c.gridy;
        c.gridx = 0;
        c.weightx = 0.0;
        c.gridwidth = 1;
        label1.setLabel(res.getString("To:"));
        label1.setHorizAlign(LabelGadget.HORIZ_ALIGN_RIGHT);
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
        label2.setLabel(res.getString("Subject:"));
        label2.setHorizAlign(LabelGadget.HORIZ_ALIGN_RIGHT);
        grid.setConstraints(label2, c);
        top.add(label2);

        c.gridx = 1;
        c.weightx = 0.7;
        c.gridwidth = 2;
        tmp = CJGlobals.settings.getProperty("SMTPsubject", "CTC encrypted message");
        subject.setText(tmp);
        grid.setConstraints(subject, c);
        top.add(subject);

        ++c.gridy;
        c.gridx = 0;
        c.weightx = 0.0;
        c.gridwidth = 1;
        label3.setLabel(res.getString("SMTP_server:"));
        label3.setHorizAlign(LabelGadget.HORIZ_ALIGN_RIGHT);
        grid.setConstraints(label3, c);
        top.add(label3);

        c.gridx = 1;
        c.weightx = 0.7;
        c.gridwidth = 2;
        tmp = CJGlobals.settings.getProperty("SMTPservername", "");
        server.setText(tmp);
        grid.setConstraints(server, c);
        top.add(server);

        bodyPanel.add("South", buttons);
        buttons.add(send);
        buttons.add(can);

        midfield.setLayout(new GadgetBorderLayout());

        headers.setLayout(new GadgetBorderLayout());
        headers.add("Center", headerTA);
        headerTA.setWordWrap(false);
        box.setLayout(new GadgetBorderLayout());
        box.add("Center", status);

        midfield.add("South", box);
        midfield.add("Center", headers);

        can.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancel();
            }
        }
        );

        send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendAction();
            }
        }
        );
        java.awt.Point parentAt = owner.getLocation();
        java.awt.Dimension d = owner.getSize();

        pack();

        java.awt.Dimension d2 = getSize();
        setLocation(parentAt.x+(d.width-d2.width)/2,
        parentAt.y+(d.height-d2.height)/2);
        setResizable(true);
    }

    protected void processWindowEvent(java.awt.event.WindowEvent e)
    {
        if (e.getID() == java.awt.event.WindowEvent.WINDOW_CLOSING)
        {
            cancel();
        }
        super.processWindowEvent(e);
    }

    void cancel()
    {
        dispose();
    }

    void setText(String s)
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
        }
        catch (java.security.NoSuchAlgorithmException NSA)
        {
            System.out.println("NSA");
        }
        catch (IOException io) {
            System.out.println("IO");
        }
    }

    protected String getAddress(String part)
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


    void sendAction()
    {
        // need to code SMTP transaction here

        // phase 1 - parse the user names (crudely)
        StringTokenizer names = new StringTokenizer(to.getText(), ",", false);
        Vector recipients = new Vector(names.countTokens());
        while(names.hasMoreTokens())
        {
            String part = names.nextToken();
            if(part.indexOf('@') < 0)
            {
                status.setLabel(res.getString("Bad_recipient_email")+ part);
                return;
            }
            recipients.addElement(getAddress(part));
        }

        if(from.getText().indexOf('@') < 0)
        {
            status.setLabel(res.getString("Bad_sender_email")+from.getText());
            return;
        }

        String sender =  getAddress(from.getText());

        CJGlobals.settings.put("SMTPservername", server.getText());
        CJGlobals.settings.put("SMTPusername", from.getText());
        CJGlobals.settings.put("SMTPsubject", subject.getText());

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
            status.setLabel(
            res.getString("Connection_failure:")+ex1.getLocalizedMessage());
            return;
        }

        // login
        try {
            out.println("HELO "+ smtp.getInetAddress().getHostAddress());
            read(in);
        } 
        catch(Exception ex2) {
            status.setLabel(
            res.getString("Login_failure:")+ex2.getLocalizedMessage());
            return;
        }

        // say who from and to
        try{
            out.println("MAIL FROM: "+sender);
            read(in);
        } 
        catch(Exception ex3) {
            status.setLabel(
            res.getString("Bad_sender:")+ex3.getLocalizedMessage());
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
                status.setLabel(
                res.getString("Bad_recipient:")+ex4.getLocalizedMessage());
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
            status.setLabel(
            res.getString("Transmission_error:")+ex5.getLocalizedMessage());
            return;
        }

        cancel();
    }// end send

    protected String read (BufferedReader in) throws IOException
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
            throw new IOException(res.getString("No_response_code:")+ line);
        }
        return line;
    }

}

