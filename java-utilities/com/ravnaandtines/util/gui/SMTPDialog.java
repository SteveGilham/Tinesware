package com.ravnaandtines.util.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.security.MessageDigest;
import com.ravnaandtines.util.swing.*;

/**
*  Class SMTPDialog - very simple dialog to manage an SMTP transmission -
* not particularly careful about bare lines containing '.' in the message
* <p>
* Copyright Mr. Tines &lt;tines@windsong.demon.co.uk&gt; 1998
* <p>
* For the purposes of the licence, this source file and the minimum set of
* other source files in package com.ravnaandtines.util and its sub-
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
* @version 1.0 28-Dec-1998
*
*/
public class SMTPDialog extends XDialog
{
    private Container bodyPanel = getContentPane();
    private static ResourceBundle res =
        ResourceBundle.getBundle("com.ravnaandtines.util.gui.SMTPPOP3");
    private BorderLayout borderLayout1 = new BorderLayout();

    private JPanel top = new JPanel();

    private GridBagLayout grid = new GridBagLayout();
    private GridBagConstraints c = new GridBagConstraints();

    private JLabel label0 = new JLabel();
    private JTextField from = new JTextField();
    private JLabel label1 = new JLabel();
    private JTextField to = new JTextField();
    private JLabel label2 = new JLabel();
    private JTextField subject = new JTextField();
    private JLabel label3 = new JLabel();
    private JTextField server = new JTextField();

    private JPanel midfield = new JPanel();

    private GroupBox box = new GroupBox(res.getString("Status"));
    GroupBox headers = new GroupBox(res.getString("Other_Mail_Headers_"));

    private JTextArea headerTA = new JTextArea(5, 30);
    private JLabel status = new JLabel();

    private JPanel buttons = new JPanel();
    private FlowLayout flow = new FlowLayout(FlowLayout.CENTER,
        5,5);
    private JButton send = new JButton(res.getString("Send"));
    private JButton can = new JButton(res.getString("Quit"));

    private String message = "";
    private Frame owner = null;

    /**
    * Create dialog as per JDialog
    * @param frame owning frame
    * @param title for Dialog title bar
    * @param modal true for modal dialog
    */
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


    /**
    * Create modal dialog as per JDialog
    * @param frame owning frame
    */
    public SMTPDialog(Frame frame)
    {
        this(frame, true);
    }

    /**
    * Create dialog as per JDialog
    * @param frame owning frame
    * @param modal true for modal dialog
    */
    public SMTPDialog(Frame frame, boolean modal)
    {
        this(frame, res.getString("SMTP_transmission"), modal);
    }


    /**
    * Create modal dialog as per JDialog
    * @param frame owning frame
    * @param title for Dialog title bar
    */
    public SMTPDialog(Frame frame, String title)
    {
        this(frame, title, true);
    }

    private void jbInit() throws Exception
    {
        this.enableEvents(java.awt.AWTEvent.WINDOW_EVENT_MASK);
        this.add("Center", bodyPanel);
        bodyPanel.setLayout(borderLayout1);
        bodyPanel.add("Center", midfield);
        bodyPanel.add("North", top);

        top.setLayout(grid);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;

        label0.setText(res.getString("From_"));
        label0.setHorizontalTextPosition(SwingConstants.RIGHT);
        grid.setConstraints(label0, c);
        top.add(label0);

        c.gridx = 1;
        c.weightx = 0.7;
        c.gridwidth = 2;
        grid.setConstraints(from, c);
        top.add(from);
        setInitialFocus(from);

        ++c.gridy;
        c.gridx = 0;
        c.weightx = 0.0;
        c.gridwidth = 1;
        label1.setText(res.getString("To_"));
        label1.setHorizontalTextPosition(SwingConstants.RIGHT);
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
        label2.setText(res.getString("Subject_"));
        label2.setHorizontalTextPosition(SwingConstants.RIGHT);
        grid.setConstraints(label2, c);
        top.add(label2);

        c.gridx = 1;
        c.weightx = 0.7;
        c.gridwidth = 2;
        grid.setConstraints(subject, c);
        top.add(subject);

        ++c.gridy;
        c.gridx = 0;
        c.weightx = 0.0;
        c.gridwidth = 1;
        label3.setText(res.getString("SMTP_server_"));
        label3.setHorizontalTextPosition(SwingConstants.RIGHT);
        grid.setConstraints(label3, c);
        top.add(label3);

        c.gridx = 1;
        c.weightx = 0.7;
        c.gridwidth = 2;
        grid.setConstraints(server, c);
        top.add(server);

        bodyPanel.add("South", buttons);
        buttons.add(send);
        buttons.add(can);

        midfield.setLayout(new BorderLayout());

        headers.setLayout(new BorderLayout());
        headers.add("Center", headerTA);
        headerTA.setLineWrap(false);
        box.setLayout(new BorderLayout());
        box.add("Center", status);

        midfield.add("South", box);
        midfield.add("Center", headers);

        can.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancel();
        }});

        send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendAction();
        }});
		Point parentAt = owner.getLocation();
		Dimension d = owner.getSize();

		pack();

		Dimension d2 = getSize();
		setLocation(parentAt.x+(d.width-d2.width)/2,
			parentAt.y+(d.height-d2.height)/2);
        setResizable(true);
    }

    /**
    * Activates the dialog titlebar close button
    * @param e Any window event on this dialog
    */
    protected void processWindowEvent(WindowEvent e)
    {
        if (e.getID() == WindowEvent.WINDOW_CLOSING)
        {
            cancel();
        }
        super.processWindowEvent(e);
    }

    private void cancel()
    {
        dispose();
    }

    /**
    * Sets the message to transmit; uses this to generate message ID too
    * @param s the message
    */
    public void setText(String s)
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
        {/*System.out.println("NSA");*/}
        catch (IOException io) {/*System.out.println("IO");*/}
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


    private void sendAction()
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
                status.setText(res.getString("Bad_recipient_email")+ part);
                return;
            }
            recipients.addElement(getAddress(part));
        }

        if(from.getText().indexOf('@') < 0)
        {
            status.setText(res.getString("Bad_sender_email")+from.getText());
            return;
        }

        String sender =  getAddress(from.getText());

        // do SMTP thing
    	Socket smtp;
        PrintWriter out;
        BufferedReader in;
        try{
            smtp = new Socket(server.getText(), 25);
            out = new PrintWriter(smtp.getOutputStream(), true);
            in = new BufferedReader( new InputStreamReader (
                smtp.getInputStream()));
            } catch(Exception ex1) {
            status.setText(
                res.getString("Connection_failure_")+ex1.getLocalizedMessage());
            return;
        }

        // login
        try {
            out.println("HELO "+ smtp.getInetAddress().getHostAddress());
            read(in);
            } catch(Exception ex2) {
            status.setText(
                res.getString("Login_failure_")+ex2.getLocalizedMessage());
            return;
        }

        // say who from and to
        try{
            out.println("MAIL FROM: "+sender);
            read(in);
            } catch(Exception ex3) {
            status.setText(
                res.getString("Bad_sender_")+ex3.getLocalizedMessage());
            return;
        }

        Enumeration e = recipients.elements();
        while(e.hasMoreElements())
        {
            String s = (String) e.nextElement();
            try {
                out.println("RCPT TO: "+s);
                read(in);
                } catch(Exception ex4) {
                status.setText(
                    res.getString("Bad_recipient_")+ex4.getLocalizedMessage());
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
            } catch(Exception ex5) {
            status.setText(
                res.getString("Transmission_error_")+ex5.getLocalizedMessage());
            return;
        }
        
        cancel();
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
            } catch (ParseException ex1) {
            throw new IOException(res.getString("No_response_code_")+ line);
        }
        return line;
    }

}

