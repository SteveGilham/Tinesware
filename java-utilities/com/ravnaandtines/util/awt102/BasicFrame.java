package com.ravnaandtines.util.awt102;

import java.awt.*;
import java.applet.*;
import com.ravnaandtines.util.Licence;
import java.util.*;

/**
*  Class BasicFrame - This is a Frame with a minimal menu bar
*  (File-&gt;Exit and Help-&gt;About-&gt;Text field with GPL information);
*  can be used for an application or as a slave from an applet (in which
*  case the Exit entry is disabled as is the window-level shutdown.
* <P>
* Alas, Java 1.0 doesn't have i18n support...
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
* @version 1.0 24-Jul-1997
*/
public class BasicFrame extends Frame implements Actor{

   	private MenuItem exitMI;
   	private MenuItem helpMI;
	private MenuItem extraHMI = null;
	private Actor extraHMIAction = null;
   	private Menu file, help;
   	private ActionButton close;
   	private Dialog d;
   	private TextArea ta;
    private boolean slave = false;

   	/**
   	* Default constructor with title"A BasicFrame instance" 
   	*/
   	public BasicFrame()
   	{
		this("A BasicFrame instance");
   	}

	/**
	* Builds a frame with minimal decoration
	* @param title String to use as Frame title
	*/	
   	public BasicFrame(String title)
   	{
    	super(title);

		MenuBar mb = new MenuBar();

		file = new Menu("File");
		mb.add(file);

		help = new Menu("Help");
		mb.setHelpMenu(help);

		exitMI = new MenuItem("Exit");
		file.add(exitMI);

		helpMI = new MenuItem("About");
		help.add(helpMI);

		setMenuBar(mb);
		close = null;
      	d = null;
      	ta = null;
   }

   /**
   * Switches off the Exit menu item and window action
   */
   public void makeSlave()
   {
      exitMI.enable(false);
      slave = true;
   }
    /**
    * Handles window closure event by closing the JVM if non-slave
    * otherwise does default handling
    * @param evt the event to handle
    * @return true if the event has been handled here. 
    */
    public boolean handleEvent(Event evt)
    {
        if(!slave && (evt.id == Event.WINDOW_DESTROY))
        {
            System.exit(0);
            return true;
        }
        return super.handleEvent(evt);
    }

   /**
   * Adds an application specific help menu item
   * @param a Actor to do what the item wants
   * @param label String to label the item
   * @return false if already used
   */
   public boolean addHelpItem(Actor a, String label)
   {
	if(extraHMI != null) return false;
	extraHMI = new MenuItem(label);
	help.remove(helpMI);
	help.add(extraHMI);
	help.add(helpMI);
	extraHMIAction = a;
	return true;
   }


   /**
   * Listens for the action - JDK 1.0 style
   * @param e Event for action
   * @param o Object acted on
   * @return boolean value if event handled
   * @see Component#action
   */
   public boolean action(Event e, Object o )
   {
		if(!(e.target instanceof MenuItem)) return false;
		if((String)o == exitMI.getLabel())
		{
			System.exit(0);
		}
		else if((String)o == helpMI.getLabel())
		{
			if(d == null) 
			{
				d = new Dialog(this, "Licence conditions", true);
				d.setLayout(new BorderLayout(5,5));
				if(ta == null)
					ta = new TextArea(Licence.GNU_General_Public_Licence(), 20, 60);
				d.add("Center", ta);
				Panel p = new Panel();
				p.setLayout(new GridLayout(1,0));
				close = new ActionButton("Close", this);
				p.add(new Label(""));
				p.add(new Label(""));
				p.add(close);
				p.add(new Label(""));
				p.add(new Label(""));
				d.add("South", p);
				d.pack();
			}
			d.show();
			return true;
		}
		else if((String)o == extraHMI.getLabel())
		{
			extraHMIAction.doIt(null);
			return true;
		}
		return false;
   	}

	/**
	* Sets the text on the About dialog's text area
	* @param s String to place on the TextArea
	*/
   	public void setText(String s)
   	{
		if(ta == null)
			ta = new TextArea(s, 20, 60);
		else
			ta.setText(s);
   	}
	

   	/**
   	* Listens for the action - JDK 1.0 style
   	* and dismisses the About dialog
   	* @param b Component that acted 
   	*/

   	public void doIt(Component b)
   	{
		d.hide();
   	}
}

/* end of file basicFrame.java */

