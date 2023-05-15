package com.ravnaandtines.util.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
*  Class StickUp - Generic modal dialog box utility class for warnings and such
*  Puts a Label's worth of text up, along with some decorations
*  and buttons to act on the information.  Pure AWT, no Swing
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
* @version 1.0 31-Jul-1997
* @version 1.1 1-Dec-1997 misc tidying
* @version 1.2 8-May-1998 Java 1.1, tidying, CTC
* @version 1.3 28-Dec-1998 raw AWT - easy enough to revert to 1.0.2 ActionButton/Actor
* @version 1.4 3-Jan-1999 Icon control, not just big letters
*/
public class StickUp extends Dialog implements ActionListener{
	public static final int WARN = 1;
    private static ResourceBundle res =
        ResourceBundle.getBundle("com.ravnaandtines.util.gui.MBox");
	public static final int QUERY = 2;
	public static final int INFO = 3;
	public static final int ERROR = 4;

	private Button[] buttons = null;
    private Panel body = new Panel();
	private int type;
	private Frame owner = null;
	private Panel innerP = null;
	private boolean hidden = false;
    private int result = -1;
    private Component icon;

	private void setType(int TYPE)
	{
		type = TYPE;
		if(TYPE == INFO) {setTitle(res.getString("INFORMATION"));}
		else if(TYPE == QUERY) {setTitle(res.getString("QUERY"));}
		else if(TYPE == ERROR) {setTitle(res.getString("ERROR"));}
		else {type = WARN; setTitle(res.getString("WARNING"));}
    }

	/**
	* Constructor for a 1-button stickup.  Does *not* call show()
	* The calling code can do that after adjusting the title as required.
	* @param parent Frame affected
	* @param TYPE int value indicating what sort of dialog (defaults to WARN)
	* @param message String saying what's up e.g. "File could not be opened."
	* @param label String to put on the button e.g. "OK"
	* @param icon value to decorate with (null means use a default).
   	*/
	public StickUp(Frame parent, int TYPE, 
			String message, String labelText, Component icon)
	{
		super(parent, true);
		owner = parent;
		setType(TYPE);
        this.icon = icon;

		buttons = new Button[1];
		buttons[0] = new Button(labelText);
        buttons[0].addActionListener(this);
		decorate(message);
	}

	/**
	* Constructor for simplest possible a 1-button stickup.  Does *not* call show()
	* The calling code can do that after adjusting the title as required.
	* @param parent Frame affected
	* @param message String saying what's up e.g. "File could not be opened."
	*/
	public StickUp(Frame parent, String message)
	{
		this(parent, WARN, message,
        java.util.ResourceBundle.getBundle("com.ravnaandtines.util.Res").getString("OK")
        ,null);
	}

	/**
	* Constructor for a multi-button stickup.  Does *not* call show()
	* The calling code can do that after adjusting the title as required.
	* @param parent Frame affected
	* @param TYPE int value indicating what sort of dialog (defaults to WARN)
	* @param message String saying what's up e.g. "File could not be opened."
	* @param label String[] to put on the buttons e.g. "YES","NO"
	*              label.length many buttons are created.
	* @param icon value to decorate with (null means use a default).
	*/
	public StickUp(Frame parent, int TYPE,
			String message, String[] labelText, Component icon)
	{
		super(parent, true);
		owner = parent;
        this.icon = icon;

		setType(TYPE);
		buttons = new Button[labelText.length];
		
		for(int i=0; i<labelText.length; ++i)
		{
			buttons[i] = new Button(labelText[i]);
            buttons[i].addActionListener(this);
		}
		decorate(message);
	}
	
	/**
	* Put the buttons and the other stuff on.
	* @param message String containing purpose of dialog
	*/
	private void decorate(String message)
	{
        this.enableEvents(java.awt.AWTEvent.WINDOW_EVENT_MASK);
        setLayout(new java.awt.BorderLayout());
        add("Center", body);
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent e)
            {
                result = -1;
                setVisible(false);
                hidden = true;
            }
        });

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		body.setLayout(gb);

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.gridx = 0;

		gbc.gridheight = 6;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.ipady = 2;

        if(null == icon)
        {
    		// Type-pseudographic
		    Label l = null;
		    if(type == INFO) l = new Label("i");
		    else if(type == QUERY) l = new Label("?");
		    else if(type == ERROR) l = new Label("X");
		    else l = new Label("!");
		    java.awt.Font f = l.getFont();

		    if(f == null)
		    {
			    String fn = java.awt.Toolkit.getDefaultToolkit().getFontList()[0];
			    l.setFont(new java.awt.Font(fn, java.awt.Font.BOLD, 24));
		    }
		    else
		    {
			    l.setFont(new java.awt.Font(f.getName(),
		                   java.awt.Font.BOLD,
		                   3*f.getSize()
		                   ));
		    }
		    gb.setConstraints(l, gbc);
		    body.add(l);
        }
        else
        {
		    gb.setConstraints(icon, gbc);
		    body.add(icon);
        }

		// The message
		gbc.gridheight = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.ipady = 7;
		innerP = new Panel();
		innerP.setLayout(new GridLayout(0,1));
		innerP.add(new Label(message, Label.CENTER));
		gb.setConstraints(innerP, gbc);
		body.add(innerP);

		// The buttons
		gbc.ipady = 2;
		Panel p = new Panel();
		p.setLayout(new GridLayout(1,buttons.length+2,10,0));
		p.add (new Label("")); // keep buttons from edge of box
		for(int i=0; i<buttons.length; ++i)
		{
			p.add(buttons[i]);
		}
		p.add (new Label("")); // keep buttons from edge of box
		gb.setConstraints(p, gbc);
		body.add(p);

		gbc.gridheight = 1;
		gbc.ipady = 0;
		Label pad = new Label("");
		gb.setConstraints(pad,gbc);
		body.add(pad);

		java.awt.Point parentAt = owner.getLocation();
		java.awt.Dimension d = owner.getSize();

		pack();

		java.awt.Dimension d2 = getSize();
		setLocation(parentAt.x+(d.width-d2.width)/2,
			parentAt.y+(d.height-d2.height)/2);
        setResizable(false);
	}

   	/**
   	* Associates a component with the message
   	* @param c Component to add
   	*/
	public void addInnerComponent(Component c)
	{
		innerP.add(c);
		pack();
	}

   	/**
   	* default method to dismiss the StickUp
   	* @param e event now being served
   	*/
   	public void actionPerformed(ActionEvent e)
   	{
      for(int i=0; i<buttons.length; ++i)
      {
         if(e.getSource()==buttons[i])
         {
             result = i;
             break;
         }
      }
      setVisible(false);
      hidden = true;
   	}

   	/**
   	* default method to test dismissal of the StickUp
   	* @return boolean done value
   	*/
   	public boolean done()
   	{
       return hidden;
   	}
  
 	
   	/**
   	* Return the nth button - useful to help an Actor
   	* figure out what has been signalled
   	* @param n int index of Button to inspect
   	* @return Button of that index, or null if out of range
   	*/
   	public Button getButton(int n)
   	{
   		return (n>=0 && n < buttons.length) ? buttons[n] : null;
   	}

   	/**
   	* Return the result - figure out what has been signalled
   	* @return Button index, or -1 if out of range (window cancel)
   	*/
   	public int getResult()
   	{
   		return result;
   	}

    /**
    * Activates the dialog titlebar close button
    * @param e Any window event on this dialog
    */
    protected void processWindowEvent(WindowEvent e)
    {
        if (e.getID() == WindowEvent.WINDOW_CLOSING)
        {
            setVisible(false);
            hidden = true;
        }
        super.processWindowEvent(e);
    }


}

/* end of file StickUp.java */

