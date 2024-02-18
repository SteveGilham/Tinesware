//package UK.co.demon.windsong.tines.util
//import package UK.co.demon.windsong.tines.?

import java.awt.*;

/** 
*  Class TextPager
*
*  A gadget that allows text to be displayed in chunks, perhaps 
* generated on the fly.
*
*  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1997
*  All rights reserved.  For full licence details see file Main.java
*
* @author Mr. Tines
* @version 1.0 24-Jul-1997
*
*/

public class TextPager extends Panel implements Actor
{
   Paged from;
   int page;
   TextArea t;
   ActionButton next, back;

   /**
   * Create a gadget to allow stepping throug hbit-sized chunks of text.
   * @param source Paged text to display
   */
   public TextPager(Paged source)
   {
      super();
      from = source;
      page = 0;

      setLayout(new BorderLayout());
      t = new TextArea(from.getPage(0), 20, 75);
      t.setEditable(false);
      add("Center", t);

      Panel p = new Panel();
      add("South", p);
      p.setLayout(new FlowLayout());
      back = new ActionButton("Back", this);
      back.disable();
      p.add(back);
      next = new ActionButton("Next", this);
      next.enable();
      p.add(next);
   }

   /**
   * Page back or forth through the document
   * @see Actor#doIt
   * @param b Component causing the action - used to tell which button
   */
   public void doIt(Component b)
   {
      if(b == next)
      {
         if(page < from.maxPages() -1) {++page; back.enable();}
         else next.disable();
      }
      else
      {
         if(page > 0) {--page; next.enable();}
         else back.disable();
      }
      t.setText(from.getPage(page));
   }
}

/* end of file Textpager.java */

