
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

public class POP3Dialog extends Dialog
{
    GadgetPanel bodyPanel = new GadgetPanel();
    static ResourceBundle res = ResourceBundle.getBundle("com.ravnaandtines.ctcjava.Res");
    GadgetBorderLayout borderLayout1 = new GadgetBorderLayout();
    Vector values = null;

    CheckboxGadget [] selectors = null;
    GroupBox mails = new GroupBox(res.getString("Available_messages"));
    PanelGadget top = new PanelGadget();

    private GadgetGridBagLayout grid = new GadgetGridBagLayout();
    private GadgetGridBagConstraints c = new GadgetGridBagConstraints();

    LabelGadget label0 = new LabelGadget();
    TextFieldGadget username = new TextFieldGadget();

    LabelGadget label1 = new LabelGadget();
    TextFieldGadget pop3server = new TextFieldGadget();

    LabelGadget label2 = new LabelGadget();
    TextFieldGadget password = new TextFieldGadget();

    CheckboxGadget apopCheck = new CheckboxGadget(res.getString("Use_secure_login"), false);

    PanelGadget buttons = new PanelGadget();
    GadgetFlowLayout flow = new GadgetFlowLayout(GadgetFlowLayout.CENTER,
    5,5);
    ButtonGadget connect = new ButtonGadget(res.getString("Connect_to_Server"));
    ButtonGadget retrieve = new ButtonGadget(res.getString("Retrieve_selection"));
    ButtonGadget can = new ButtonGadget(res.getString("Cancel"));

    Socket mSocket = null;
    PrintWriter mOut = null;
    BufferedReader mIn = null;
    int size = -1;
    String apopTag = null;

    ScrollPaneGadget paneHolder = new ScrollPaneGadget();
    PanelGadget pane = new PanelGadget();
    LabelGadget status = new LabelGadget();
    boolean topSupport = false;
    CheckboxGadget[] collect = null;
    CheckboxGadget[] delete = null;
    TextAreaGadget[] headers = null;
    Frame owner = null;
    boolean connected = false;

    public POP3Dialog(Frame frame, String title, boolean modal)
    {
        super(frame, title, modal);
        owner = frame;
        enableEvents(java.awt.AWTEvent.WINDOW_EVENT_MASK);
        try
        {
            jbInit();
            add("Center", bodyPanel);
            pack();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public Vector getMessage()
    {
        return values;
    }

    public POP3Dialog(Frame frame)
    {
        this(frame, true);
    }


    public POP3Dialog(Frame frame, boolean modal)
    {
        this(frame, res.getString("POP3_retrieval"), modal);
    }


    public POP3Dialog(Frame frame, String title)
    {
        this(frame, title, false);
    }

    void jbInit() throws Exception
    {
        bodyPanel.setLayout(borderLayout1);
        bodyPanel.add("Center", mails);
        bodyPanel.add("North", top);

        top.setLayout(grid);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0.0;
        c.weighty = 1.0;
        c.fill = GadgetGridBagConstraints.HORIZONTAL;

        label0.setLabel(res.getString("POP3_user_name"));
        label0.setHorizAlign(LabelGadget.HORIZ_ALIGN_RIGHT);
        grid.setConstraints(label0, c);
        top.add(label0);

        c.gridx = 1;
        c.weightx = 1;
        c.gridwidth = 2;
        String tmp = CJGlobals.settings.getProperty("POP3username", "");
        username.setText(tmp);
        grid.setConstraints(username, c);
        top.add(username);

        ++c.gridy;
        c.gridx = 0;
        c.weightx = 0;
        c.gridwidth = 1;
        label1.setLabel(res.getString("POP3_server_name"));
        label1.setHorizAlign(LabelGadget.HORIZ_ALIGN_RIGHT);
        grid.setConstraints(label1, c);
        top.add(label1);

        c.gridx = 1;
        c.weightx = 1;
        c.gridwidth = 2;
        tmp = CJGlobals.settings.getProperty("POP3servername", "");
        pop3server.setText(tmp);
        grid.setConstraints(pop3server, c);
        top.add(pop3server);

        ++c.gridy;
        c.gridx = 0;
        c.weightx = 0;
        c.gridwidth = 1;
        label2.setLabel(res.getString("POP3_password"));
        label2.setHorizAlign(LabelGadget.HORIZ_ALIGN_RIGHT);
        grid.setConstraints(label2, c);
        top.add(label2);

        c.gridx = 1;
        c.weightx = 1;
        c.gridwidth = 2;
        password.setEchoChar('?');
        grid.setConstraints(password, c);
        top.add(password);

        ++c.gridy;
        c.gridx = 0;
        c.weightx = 1;
        c.gridwidth = 2;
        grid.setConstraints(apopCheck, c);
        top.add(apopCheck);
        apopCheck.setEnabled(false);

        bodyPanel.add("South", buttons);
        buttons.add(connect);
        buttons.add(retrieve);
        retrieve.setEnabled(false);
        buttons.add(can);

        mails.setLayout(new GadgetBorderLayout());
        mails.add("North",status);

        can.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancel();
            }
        }
        );
        connect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                connectAction();
            }
        }
        );
        retrieve.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                retrieveAction();
            }
        }
        );
        this.enableEvents(java.awt.AWTEvent.WINDOW_EVENT_MASK);
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
        if(connected)
        {
            try {
                mOut.println("QUIT ");
                getOK();
                connected = false;
                apopTag = null;
            }
            catch(Exception ex1) {            /* not a lot we can do */
            }
        }
        dispose();
    }

    private String getOK() throws IOException
    {
        // check for response
        String line = mIn.readLine();
        if(line.substring(0,3).equals("+OK"))
        {
            return line;
        }
        throw new IOException(line);
        // the compiler understands I don't need to return
        // unlike many C++ compilers :(
    }

    void connectAction()
    {
        try {
            // make the socket
            if(mSocket != null)
            {
                if(connected)
                {
                    mOut.println("QUIT ");
                    getOK();
                    connected = false;
                    apopTag = null;
                }

                mOut.close();
                mIn.close();
                mSocket.close();
            }
            String server = pop3server.getText();
            CJGlobals.settings.put("POP3servername", server);
            mSocket = new Socket(server, 110);
            mOut = new PrintWriter(mSocket.getOutputStream(), true);
            mIn = new BufferedReader(new InputStreamReader(
            mSocket.getInputStream() ));
            String line = getOK();
            connected = true;

            // set APOP tag if any
            {
                StringTokenizer t = new StringTokenizer(line);
                String token = t.nextToken();
                while(t.hasMoreTokens())
                {
                    token = t.nextToken();
                }
                // crude parsing
                if(token.charAt(0) == '<' &&
                    token.indexOf('@') > 0 &&
                    token.indexOf('>') == token.length()-1)
                {
                    apopTag = token;
                    apopCheck.setEnabled(true);
                }
                else
                {
                    apopTag = null;
                    apopCheck.setEnabled(false);
                }

            }

        } 
        catch(Exception ex1) {
            status.setLabel(
            res.getString("Connection_failure:")+ex1.getLocalizedMessage());
            return;
        }

        try { // login

            if(apopTag == null || !apopCheck.getState())
            {
                String user = username.getText();
                CJGlobals.settings.put("POP3username", user);
                mOut.println("USER "+user);
                getOK();

                mOut.println("PASS "+password.getText());
                getOK();
            }
            else // build APOP message
            {
                MessageDigest hash = MessageDigest.getInstance("MD5");
                ByteArrayOutputStream b1 = new ByteArrayOutputStream(8);
                DataOutputStream cover = new DataOutputStream(b1);
                cover.writeUTF(apopTag);
                cover.writeUTF(password.getText());
                cover.flush();
                cover.close();
                b1.close();
                byte[] apop = hash.digest(b1.toByteArray());

                StringBuffer x = new StringBuffer("APOP ");
                x.append(username.getText());
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
        } 
        catch (Exception ex2) {
            status.setLabel(
            res.getString("Logon_failure:")+ex2.getLocalizedMessage());
            return;
        }

        size = -1;
        try { // get count
            mOut.println("STAT");
            String line = getOK();
            StringTokenizer st = new StringTokenizer(line);
            st.nextToken(); // skip status message
            NumberFormat nf = NumberFormat.getInstance();
            size = nf.parse(st.nextToken()).intValue();
        } 
        catch (Exception ex3) {
            status.setLabel(
            res.getString("Count_failure:")+ex3.getLocalizedMessage());
            return;
        }

        switch(size)
        {
        case 0: 
            status.setLabel(res.getString("There_are_no_messages")); 
            break;
        case 1: 
            status.setLabel(res.getString("There_is_one_message")); 
            break;
        default:
            { 
                Object[] args = { 
                    new Integer(size)                 };
                status.setLabel(MessageFormat.format(
                res.getString("There_are_0_number"),
                args));
                break;
            }
        }
        if(size > 0)
        {
            connect.setEnabled(false);
            retrieve.setEnabled(true);

            // does the POP3 server support TOP?
            try {
                mOut.println("TOP 1 0");
                String line = getOK();
                Message m = new Message(mIn);
                topSupport = true;
            } 
            catch (IOException ex4) {
            }

            int n = topSupport ? 3 : 2;
            GadgetGridLayout lay = new GadgetGridLayout(n, 1);
            pane.setLayout(lay);
            paneHolder.add(pane);

            collect = new CheckboxGadget[n];
            delete = new CheckboxGadget[n];
            if(topSupport)
            {
                headers = new TextAreaGadget[n];
            }

            for(int i=1; i<=size; ++i)
            {
                BorderGadget unit = new BorderGadget();
                unit.setBorderType(BorderGadget.ETCHED_IN);
                Object[] args = new Object[1];
                args[0] = new Integer(i-1);

                collect[i-1] = new CheckboxGadget(
                MessageFormat.format(res.getString("Collect_message_0"), args),
                false);
                delete[i-1] = new CheckboxGadget(
                MessageFormat.format(res.getString("Delete_message_0"), args),
                false);
                if(topSupport)
                {
                    try{
                        mOut.println("TOP "+i+" 0");
                        String line = getOK();
                        Message m = new Message(mIn);
                        headers[i-1] = new TextAreaGadget(m.toString(), 3, 30, TextAreaGadget.SCROLLBARS_BOTH);
                        headers[i-1].setWordWrap(false);
                        headers[i-1].setEditable(false);
                        PanelGadget bar = new PanelGadget();
                        bar.setLayout(new GadgetFlowLayout());
                        bar.add(collect[i-1]);
                        bar.add(delete[i-1]);
                        unit.setLayout(new GadgetBorderLayout());
                        unit.add("South", bar);
                        unit.add("Center", headers[i-1]);
                    }
                    catch (IOException ex1) {
                        unit.setLayout(new GadgetFlowLayout());
                        unit.add(collect[i-1]);
                        unit.add(delete[i-1]);
                    }
                }
                else
                {
                    unit.setLayout(new GadgetFlowLayout());
                    unit.add(collect[i-1]);
                    unit.add(delete[i-1]);
                }
                pane.add(unit);
            }
            mails.add("Center", paneHolder);
            {
                java.awt.Dimension d = getSize();
                setSize(d.width, d.height+200);
            }
            doLayout();
        }
    }

    void retrieveAction()
    {
        int upper = size;
        Message m = null;
        boolean OK = true;
        String line = "";

        for(int i=1; i<=size && OK; ++i)
        {
            if(!collect[i-1].getState()) continue;

            Object[] args = { 
                new Integer(i), new Integer(size)             };
            status.setLabel(MessageFormat.format(
            res.getString("Retrieving_message_0"),
            args));
            try {
                line = "";
                mOut.println("RETR "+i);
                line = getOK();
                m = new Message(mIn);
            } 
            catch (IOException ex1) {
                OK = false;
                status.setLabel(MessageFormat.format(
                res.getString("Problem_retrieving"),
                args) + line);
                break;
            }
            if(m != null)
            {
                if(null == values) values = new Vector(upper);
                values.addElement(m);
            }

            if(delete[i-1].getState())
            {
                try {
                    line = "";
                    mOut.println("DELE "+i);
                    line = getOK();
                    // can't do any more with it
                    collect[i-1].setEnabled(false);
                    delete[i-1].setEnabled(false);
                } 
                catch (IOException ex1) {
                    //Delete fails safe, so don't stop.
                    status.setLabel(MessageFormat.format(
                    res.getString("Problem_deleting"),
                    args) + line);
                    break;
                }
            }
        }

        // finish if all went OK or was a no-op
        if(OK)
        {
            retrieve.setEnabled(false);
            try {
                line = "";
                mOut.println("QUIT ");
                line = getOK();
                // and finish
                cancel();
            } 
            catch (IOException ex1) {
                OK = false;
                status.setLabel(line);
            }
        }
    }
}

