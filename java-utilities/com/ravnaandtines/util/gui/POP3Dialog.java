package com.ravnaandtines.util.gui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.security.MessageDigest;
import com.ravnaandtines.util.swing.*;

/**
*  Class POP3Dialog - very simple dialog to manage POP3 reception
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
public class POP3Dialog extends XDialog
{
    private Container bodyPanel = getContentPane();
    private static ResourceBundle res =
        ResourceBundle.getBundle("com.ravnaandtines.util.gui.SMTPPOP3");
    private BorderLayout borderLayout1 = new BorderLayout();
    private Vector values = null;

    private JCheckBox [] selectors = null;
    private GroupBox mails = new GroupBox(res.getString("Available_messages"));
    private JPanel top = new JPanel();

    private GridBagLayout grid = new GridBagLayout();
    private GridBagConstraints c = new GridBagConstraints();

    private JLabel label0 = new JLabel();
    private JTextField username = new JTextField();

    private JLabel label1 = new JLabel();
    private JTextField pop3server = new JTextField();

    private JLabel label2 = new JLabel();
    private JPasswordField password = new JPasswordField();

    private JCheckBox apopCheck = new JCheckBox(res.getString("Use_secure_login"), false);

    private JPanel buttons = new JPanel();
    private FlowLayout flow = new FlowLayout(FlowLayout.CENTER,
        5,5);
    private JButton connect = new JButton(res.getString("Connect_to_Server"));
    private JButton retrieve = new JButton(res.getString("Retrieve_selection"));
    private JButton can = new JButton(res.getString("Cancel"));

    private Socket mSocket = null;
    private PrintWriter mOut = null;
    private BufferedReader mIn = null;
    private int size = -1;
    private String apopTag = null;

    private JScrollPane paneHolder = new JScrollPane();
    private JPanel pane = new JPanel();
    private JLabel status = new JLabel();
    private boolean topSupport = false;
    private JCheckBox[] collect = null;
    private JCheckBox[] delete = null;
    private JTextArea[] headers = null;
    private Frame owner = null;
    private boolean connected = false;

    /**
    * Create dialog as per JDialog
    * @param frame owning frame
    * @param title for Dialog title bar
    * @param modal true for modal dialog
    */
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

    /**
    * Retrieves any messages received
    * @return Vector of messages
    */
    public Vector getMessage()
    {
        return values;
    }

    /**
    * Create modal dialog as per JDialog
    * @param frame owning frame
    */
    public POP3Dialog(Frame frame)
    {
        this(frame, true);
    }

    
    /**
    * Create dialog as per JDialog
    * @param frame owning frame
    * @param modal true for modal dialog
    */
    public POP3Dialog(Frame frame, boolean modal)
    {
        this(frame, res.getString("POP3_retrieval") , modal);
    }

    
    /**
    * Create modal dialog as per JDialog
    * @param frame owning frame
    * @param title for Dialog title bar
    */
    public POP3Dialog(Frame frame, String title)
    {
        this(frame, title, false);
    }

    private void jbInit() throws Exception
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
        c.fill = GridBagConstraints.HORIZONTAL;

        label0.setText(res.getString("POP3_user_name") );
        label0.setHorizontalTextPosition(SwingConstants.RIGHT);
        grid.setConstraints(label0, c);
        top.add(label0);

        c.gridx = 1;
        c.weightx = 1;
        c.gridwidth = 2;
        grid.setConstraints(username, c);
        setInitialFocus(username);
        top.add(username);

        ++c.gridy;
        c.gridx = 0;
        c.weightx = 0;
        c.gridwidth = 1;
        label1.setText(res.getString("POP3_server_name"));
        label1.setHorizontalTextPosition(SwingConstants.RIGHT);
        grid.setConstraints(label1, c);
        top.add(label1);

        c.gridx = 1;
        c.weightx = 1;
        c.gridwidth = 2;
        grid.setConstraints(pop3server, c);
        top.add(pop3server);

        ++c.gridy;
        c.gridx = 0;
        c.weightx = 0;
        c.gridwidth = 1;
        label2.setText(res.getString("POP3_password"));
        label2.setHorizontalTextPosition(SwingConstants.RIGHT);
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

        mails.setLayout(new BorderLayout());
        mails.add("North",status);

        can.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancel();
        }});
        connect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                connectAction();
        }});
        retrieve.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                retrieveAction();
        }});
        this.enableEvents(java.awt.AWTEvent.WINDOW_EVENT_MASK);
		java.awt.Point parentAt = owner.getLocation();
		java.awt.Dimension d = owner.getSize();

		pack();

		java.awt.Dimension d2 = getSize();
		setLocation(parentAt.x+(d.width-d2.width)/2,
			parentAt.y+(d.height-d2.height)/2);
        setResizable(true);
    }

    /**
    * Activates the dialog titlebar close button
    * @param e Any window event on this dialog
    */
    protected void processWindowEvent(java.awt.event.WindowEvent e)
    {
        if (e.getID() == java.awt.event.WindowEvent.WINDOW_CLOSING)
        {
            cancel();
        }
        super.processWindowEvent(e);
    }

    private void cancel()
    {
        if(connected)
        {
        try {
            mOut.println("QUIT ");
            getOK();
            connected = false;
            apopTag = null;
            }catch(Exception ex1) {/* not a lot we can do */}
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

    private void connectAction()
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

            } catch(Exception ex1) {
            status.setText(
                res.getString("Connection_failure_")+ex1.getLocalizedMessage());
            return;
        }

        try { // login

            if(apopTag == null || !apopCheck.isSelected())
            {
                String user = username.getText();
                mOut.println("USER "+user);
                getOK();

                mOut.println("PASS "+new String(password.getPassword()));
                getOK();
            }
            else // build APOP message
            {
                MessageDigest hash = MessageDigest.getInstance("MD5");
                ByteArrayOutputStream b1 = new ByteArrayOutputStream(8);
                DataOutputStream cover = new DataOutputStream(b1);
                cover.writeUTF(apopTag);
                cover.writeUTF(new String(password.getPassword()));
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
            } catch (Exception ex2) {
            status.setText(
                res.getString("Login_failure_")+ex2.getLocalizedMessage());
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
            } catch (Exception ex3) {
            status.setText(
                res.getString("Count_failure_")+ex3.getLocalizedMessage());
            return;
        }

        switch(size)
        {
		case 0: status.setText("There are no messages" ); break;
	      case 1: status.setText("There is one message"); break;
		default:
		{ 
            	Object[] args = { new Integer(size) };
            	status.setText(MessageFormat.format(
                		res.getString("There_are_messages"),
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
                } catch (IOException ex4) {
            }

            int n = topSupport ? 3 : 2;
            GridLayout lay = new GridLayout(n, 1);
            pane.setLayout(lay);
		    paneHolder.add(pane);

            collect = new JCheckBox[n];
            delete = new JCheckBox[n];
            if(topSupport)
            {
                headers = new JTextArea[n];
            }

            for(int i=1; i<=size; ++i)
            {
                JPanel unit = new JPanel();
                unit.setBorder(BorderFactory.createEtchedBorder());
                Object[] args = new Object[1];
                args[0] = new Integer(i-1);

                collect[i-1] = new JCheckBox(
                    MessageFormat.format(res.getString("Collect_message"), args),
                    false);
                delete[i-1] = new JCheckBox(
                    MessageFormat.format(res.getString("Delete_message"), args),
                    false);
                if(topSupport)
                {
                    try{
                        mOut.println("TOP "+i+" 0");
                        String line = getOK();
                        Message m = new Message(mIn);
                        headers[i-1] = new JTextArea(m.toString(), 3, 30);
                        headers[i-1].setLineWrap(false);
                        headers[i-1].setEditable(false);
                        JPanel bar = new JPanel();
                        bar.setLayout(new FlowLayout());
                        bar.add(collect[i-1]);
                        bar.add(delete[i-1]);
                        unit.setLayout(new BorderLayout());
                        unit.add("South", bar);
                        unit.add("Center", new JScrollPane(headers[i-1]));
                    }
                    catch (IOException ex1) {
                        unit.setLayout(new FlowLayout());
                        unit.add(collect[i-1]);
                        unit.add(delete[i-1]);
                    }
                }
                else
                {
                    unit.setLayout(new FlowLayout());
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
            if(!collect[i-1].isSelected()) continue;

            Object[] args = { new Integer(i), new Integer(size) };
            status.setText(MessageFormat.format(
                res.getString("Retrieving_message"),
                args));
            try {
                line = "";
                mOut.println("RETR "+i);
                line = getOK();
                m = new Message(mIn);
                } catch (IOException ex1) {
                OK = false;
                status.setText(MessageFormat.format(
                    res.getString("Problem_retrieving"),
                    args) + line);
                break;
            }
            if(m != null)
            {
                if(null == values) values = new Vector(upper);
                values.addElement(m);
            }

            if(delete[i-1].isSelected())
            {
                try {
                    line = "";
                    mOut.println("DELE "+i);
                    line = getOK();
                    // can't do any more with it
                    collect[i-1].setEnabled(false);
                    delete[i-1].setEnabled(false);
                    } catch (IOException ex1) {
                    //Delete fails safe, so don't stop.
                    status.setText(MessageFormat.format(
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
                } catch (IOException ex1) {
                OK = false;
                status.setText(line);
            }
        }
    }
}

