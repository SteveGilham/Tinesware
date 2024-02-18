//package UK.co.demon.windsong.tines.awt102
//import package UK.co.demon.windsong.tines.?

import java.awt.*;
import java.applet.*;

/** 
*  Class BasicFrame
*
*  This is a Frame with a minimal menu bar (File->Exit and 
*  Help->About->Text field with GPL information
*
*
*  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1997
*  All rights reserved.  For full licence details see file Main.java
*
* @author Mr. Tines
* @version 1.0 24-Jul-1997
*
*/


public class BasicFrame extends Frame implements Actor {

    private MenuItem exitMI;
    private MenuItem helpMI;
    private MenuItem extraHMI = null;
    private Actor extraHMIAction = null;
    private Menu file, help;
    private ActionButton close;
    private Dialog d;
    private TextArea ta;
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
        file = new Menu(StringTable.FILE);
        mb.add(file);

        help = new Menu(StringTable.HELP);
        mb.setHelpMenu(help);

        exitMI = new MenuItem(StringTable.EXIT);
        file.add(exitMI);

        helpMI = new MenuItem(StringTable.ABOUT);
        help.add(helpMI);

        setMenuBar(mb);
        close = null;
        d = null;
        ta = null;
   }

   /**
   * Switches off the Exit menu item
   */
   public void makeSlave()
   {
        exitMI.enable(false);
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
               d = new Dialog(this, StringTable.LICTEXT, true);
               d.setLayout(new BorderLayout(5,5));
               if(ta == null)
                  ta = new TextArea(Main.GNU_General_Public_Licence(), 20, 60);
               d.add("Center", ta);
               Panel p = new Panel();
               //p.setLayout(new GridLayout(1,0));
               close = new ActionButton(StringTable.CLOSE, this);
               //p.add(new Label(""));
               //p.add(new Label(""));
               p.add(close);
               //p.add(new Label(""));
               //p.add(new Label(""));
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
    * Handles window closure event by closing the JVM if non-slave
    * otherwise does default handling
    * @param evt the event to handle
    * @return true if the event has been handled here. 
    */
    public boolean handleEvent(Event evt)
    {
        if(exitMI.isEnabled() && evt.id == Event.WINDOW_DESTROY)
        {
            System.exit(0);
            return true;
        }
        return super.handleEvent(evt);
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

