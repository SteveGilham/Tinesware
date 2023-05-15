package com.ravnaandtines.util.gui;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.*;
import com.ravnaandtines.util.text.Mnemonic;

/**
*  Class LicenceDialog - A dialog with a scrolling text area and an OK
* button to dismiss it.  Needs Swing 1.1 for word wrap on JTextArea
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
* @version 1.0 18-Nov-1998
*
*/
public class LicenceDialog extends com.ravnaandtines.util.swing.XDialog
{
    private JPanel panel1 = new JPanel();
    private BorderLayout borderLayout1 = new BorderLayout();
    private JTextArea textArea1 = new JTextArea(10,40);
    private JPanel panel2 = new JPanel();
    private FlowLayout flowLayout1 = new FlowLayout();
    private JButton OK = new JButton();

    /**
    * Creates a modal window to show some important text e.g.
    * program license conditions.  Don't use JOptionPane to
    * work around bug that doesn't reset the banner
    * @param frame Frame with respect to which to be modal
    * @param title Banner string for the dialog
    */
    public LicenceDialog(java.awt.Frame frame, String title)
    {
        super(frame, title, true);
        try {
            jbInit();
            getContentPane().add("Center",panel1);
            pack();
            } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception
    {
        enableEvents(java.awt.AWTEvent.WINDOW_EVENT_MASK);
        textArea1.setEditable(false);

        Mnemonic ok = new Mnemonic("&OK");

        OK.setText(ok.getLabel());
        OK.setMnemonic(ok.getMnemonic());
        OK.addActionListener(new OKAction());
        setInitialFocus(OK);
        panel1.setLayout(borderLayout1);
        panel2.setLayout(flowLayout1);
        panel1.add(panel2, BorderLayout.SOUTH);
        panel2.add(OK, null);

        textArea1.setLineWrap(true);
        textArea1.setWrapStyleWord(true);
        panel1.add(new JScrollPane(textArea1), BorderLayout.CENTER);

        // doesn't work to give button the focus;
        // neither does this ordering
        // textArea1.setRequestFocusEnabled(false);

        // we get arrow-key scrolling for free;
        // could use page-up and page-down.

        // keyboard enabling
        panel1.registerKeyboardAction(new OKAction(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        // keyboard enabling
        panel1.registerKeyboardAction(new OKAction(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
   }

    /**
    * Puts text into the central text area; parsing for
    * line breaks at appropriate locations is assumed to
    * have been done already.
    * @param s String to display
    */
    public void setText(String s)
    {
        textArea1.setText(s);
    }

    /**
    * Closes and disposes on user request
    * @param e WindowEvent that has occurred
    */
    protected void processWindowEvent(WindowEvent e)
    {
        if (e.getID() == WindowEvent.WINDOW_CLOSING)
        {
            OK_actionPerformed();
        }
        super.processWindowEvent(e);
    }

    private void OK_actionPerformed()
    {
        setVisible(false);
        dispose();
    }


    private class OKAction extends AbstractAction
    {
        public void actionPerformed(ActionEvent e)
        {
            LicenceDialog.this.OK_actionPerformed();
        }
    }


}

