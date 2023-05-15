//package UK.co.demon.windsong.tines.util
//import package UK.co.demon.windsong.tines.?
/** 
*  Class EphemerisIIControlPanel 
*
*  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1997
*  All rights reserved.  For full licence details see file Main.java
*
* @author Mr. Tines
* @version 1.0 11-Oct-1997
*
*/
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

//====CLASS E2TimePicker========================================================
class E2TimePicker extends Dialog implements ActionListener, SlideListener{
    private Button ok;
    private Slider scalebar;
    private Label scale;
    private Checkbox cbmin, cbhr, cbday, cbyr;
    private CheckboxGroup cb;
    private double delta;

    public E2TimePicker(Frame f, double last)
    {
        super(f, "Time Picker", true);
        setLayout(new BorderLayout(3,3));
        delta = last;

        Panel p = new Panel();
				p.setLayout(new GridLayout(1,0));
        ok = new Button("OK");
        ok.addActionListener(this);
				p.add(new Label(""));
				p.add(new Label(""));
				p.add(ok);
				p.add(new Label(""));
				p.add(new Label(""));
				add("South", p);
        p = new Panel();

				p.setLayout(new GridLayout(1,0));
        scalebar = new Slider(30, 5,
                    0, 60);
        scale = new Label("30");
        p.add(scale);
        p.add(scalebar);
        scalebar.addSlideListener(this);
        add("North", p);

        p = new Panel();
        p.setLayout(new GridLayout(0,1));
        cb = new CheckboxGroup();

 		    cbmin = new Checkbox("Minutes", cb, last < 1.0/24.0);
		    p.add(cbmin);
 		    cbhr = new Checkbox("Hour", cb, last < 1 && last >= 1.0/24.0);
		    p.add(cbhr);
        cbday = new Checkbox("Days", cb, last < 294 && last >= 1);
		    p.add(cbday);
        cbyr = new Checkbox("Years", cb, last >= 294);
		    p.add(cbyr);
        add("Center", p);

        Event e = new Event(this, Event.ACTION_EVENT, this );
        action(e, this);

        pack();
    }
    public double get() {return delta;}
    public void actionPerformed(ActionEvent e)
    { setVisible(false); }
    public boolean  action(Event  evt, Object  what)
    {
         if(Event.ACTION_EVENT != evt.id) return false;
		     Checkbox x = cb.getSelectedCheckbox();
         int val;
		     if(x == cbmin)
         {
            val = (int)Math.rint(1440*delta);
            scalebar.setValues(Math.min(val,60), 5, 0, 60);
            scale.setText(""+scalebar.getValue());
          }
         else if (x == cbhr)
         {
            val = (int)Math.rint(24*delta);
            scalebar.setValues(Math.min(val,24), 5, 0, 24);
            scale.setText(""+scalebar.getValue());
         }
         else if (x == cbday)
         {
            val = (int)Math.rint(delta);
            scalebar.setValues(Math.min(val,294), 5, 0, 294);
            scale.setText(""+scalebar.getValue());
         }
         else
         {
            val = (int)Math.rint(delta/29.4);
            scalebar.setValues(Math.min(val,100), 5, 0, 100);
            double v = scalebar.getValue()/10.0;
            scale.setText(""+v);
         }
         return true;
    }
	  public void slideEvent(Slider s)
	  {
		     Checkbox x = cb.getSelectedCheckbox();
		     if(x == cbmin)
         {
            delta = scalebar.getValue()/1440.0;
            scale.setText(""+scalebar.getValue());
         }
         else if (x == cbhr)
         {
            delta = scalebar.getValue()/24.0;
            scale.setText(""+scalebar.getValue());
         }
         else if (x == cbday)
         {
            delta = scalebar.getValue();
            scale.setText(""+scalebar.getValue());
         }
         else
         {
            delta = scalebar.getValue()*29.4;
            double v = scalebar.getValue()/10.0;
            scale.setText(""+v);
         }
    }

 }

//====CLASS E2LocatorPanel======================================================
class E2LocatorPanel extends Canvas implements MouseListener  {
    double r=0.0, theta=0.0;
    // square represents inner world half-side 5,750 km
    // dome radius is 20,000 km
    private static final double drad = 20.0;
    private static final double innr = 5.75;
    private static final double frac = innr/drad;
    private   int ix,iy,iw,ih;
    EphemerisIIControlPanel scaler;
    Image map;

    /**
    * default constructor
    */
    public E2LocatorPanel(EphemerisIIControlPanel owner)
    {
        super();
        scaler = owner;

	      // image is 194 <> by 168 up/down
        // centre is at 90.5, 97.5 (0 to n-1) so has 102.5 to the right
        // assume 103 pixels map to the half-size
	      map  = Toolkit.getDefaultToolkit().createImage(new InnerWorld());
        setBackground(Color.lightGray);
        addMouseListener(this);
    }

    /**
    * overrides superclass method to draw dome and hit point
    */
    public void paint(Graphics  g)
    {
       Dimension d = getSize();
       int lwi = d.width;
       int lhi = d.height;
       int x = lwi;
       int buf = 0;
       if (lhi < lwi)
       {
        x = lhi;
        buf = (lwi-lhi)/2;
       }
       double px, py;
       int spx, spy;


	     // now put in the image
       // the centre is at buf+x/2, x/2 and x/2 maps to 103 pixels
       double factor = (double)x/206.0;
       ix = buf+x/2-(int)Math.round(91.0*factor);
       iy = x/2 - (int)Math.round(98.0*factor);
	     iw = (int)Math.round(194.0*factor);
	     ih = (int)Math.round(168.0*factor);
	     g.drawImage(map, ix, iy, iw, ih, this);

       g.setColor(Color.lightGray);

	     for(int i=1; i<4; ++i)
	     {
	 	       g.draw3DRect(ix-i, iy-i, iw+2*i, ih+2*i, true);
		       if((i==ix) || (i==iy) || (ix+iw+i == x) || (iy+ih+i == x))
			                break;
       }

       // Viewpoint
       px = ((double)(x-2))/2.0*(1.0+r*Math.cos(theta)/frac);
       py = ((double)(x-2))/2.0*(1.0-r*Math.sin(theta)/frac);
       spx = (int)Math.round(px);
       spy = (int)Math.round(py);
       g.setColor(Color.yellow);
       g.drawOval(buf+spx-2,spy-2,5,5);
       g.setColor(Color.darkGray);
       g.drawOval(buf+spx-1,spy-1,3,3);
    }
    /**
    * Performs event handling as per Java 1.0 for hits
    * @return boolean state
    */
  public void mouseClicked(MouseEvent e) {
    //TODO: implement this java.awt.event.MouseListener method;
  }

  public void mousePressed(MouseEvent e) {
    //TODO: implement this java.awt.event.MouseListener method;
  }

  public void mouseEntered(MouseEvent e) {
    //TODO: implement this java.awt.event.MouseListener method;
  }

  public void mouseExited(MouseEvent e) {
    //TODO: implement this java.awt.event.MouseListener method;
  }

  public void mouseReleased(MouseEvent e) {

	     if( (e.getX() < ix) || (e.getX() > ix+iw) ||
           (e.getY() < iy) || (e.getY() > iy+ih) )
       {
          return;
       }

       Dimension d = getSize();
       int x = d.width;
       int buf = 0;
       if(x > d.height)
       {
        x = d.height;
        buf = (d.width-d.height)/2;
       }
       double centre = ((double)(x-2))/2.0;
       double px = (e.getX()-buf) - centre;
       double py = centre - e.getY() ;
       double tr = Math.sqrt(px*px + py*py)/centre;
       r = tr*frac;
       theta = Math.atan2(py, px);
       repaint();
    }
}

//====CLASS E2LookPanel=========================================================
class E2LookPanel extends Canvas implements MouseListener, MouseMotionListener {
    boolean up = true;
    double bearing = 0.0;
	  boolean scan = false;
	  double feed = 0.0;

    /**
    * default constructor
    */
    public E2LookPanel()
    {
        super();
        setBackground(Color.lightGray);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    /**
    * overrides superclass method to draw horizon and look-at
    */
    public void paint(Graphics  g)
    {
       Dimension d = getSize();
       int lwi = d.width;
       int lhi = d.height;
       int buf = 0;
       int x = lwi;
       if (lhi < lwi)
       {
        x = lhi;
        buf = (lwi-lhi)/2;
       }
       int halfx = x/2;
	     int rad = x/3;
	     int inner = rad/2;
	     int cx = buf+halfx;
	     int cy = halfx;
       g.setColor(Color.lightGray);
       g.fillOval(cx-rad,cy-rad,2*rad,2*rad);
       g.setColor(Color.green);
       g.drawOval(cx-rad,cy-rad,2*rad,2*rad);
       g.drawOval(cx-(rad-1),cy-(rad-1),2*(rad-1),2*(rad-1));

	     g.setColor(Color.lightGray.brighter());
       g.drawArc(cx-(rad+1),cy-(rad+1),2*(rad+1),2*(rad+1),45,180);
       g.drawArc(cx-(rad+2),cy-(rad+2),2*(rad+2),2*(rad+2),45,180);
	     g.setColor(Color.lightGray.darker());
       g.drawArc(cx-(rad+1),cy-(rad+1),2*(rad+1),2*(rad+1),45,-180);
       g.drawArc(cx-(rad+2),cy-(rad+2),2*(rad+2),2*(rad+2),45,-180);

       g.setColor(Color.blue);
       g.drawOval(cx-inner,     cy-inner,     2*inner,        2*inner);
       g.drawLine(cx-rad,       halfx,        cx-inner,       halfx);
       g.drawLine(cx+inner,     halfx,        cx+rad,         halfx);
       g.drawLine(buf+halfx,    cy-rad,       buf+halfx,      cy-inner);
       g.drawLine(buf+halfx,    cy+inner,     buf+halfx,      cy+rad);

       // Viewpoint
       g.setColor(Color.red);
       if(up)
       {
          g.fillOval(buf+halfx-2,halfx-2,5,5);
       }
       else
       {
          double px = ((double)rad)*Math.sin(bearing);
          double py = ((double)rad)*Math.cos(bearing);
          int spx = cx+(int)Math.round(px);
          int spy = cy - (int)Math.round(py);
          g.fillOval(spx-2,spy-2,5,5);
          g.drawLine(cx,cy,spx,spy);
       }

	     if(scan)
	     {
	        g.setColor(Color.darkGray);
          double px = ((double)rad)*Math.sin(feed);
          double py = ((double)rad)*Math.cos(feed);
          int spx = cx+(int)Math.round(px);
          int spy = cy - (int)Math.round(py);
          g.fillOval(spx-2,spy-2,5,5);
          g.drawLine(cx,cy,spx,spy);

	        feed *= 180.0/Math.PI;
	        if(feed<0) feed+=360.0;
	        String f = ""+feed;
	        if (f.length() > 7) f = f.substring(0,7);
	        g.drawChars(f.toCharArray(), 0, f.length(), cx,cy);
       }
	     scan = false;
    }
    /**
    * Performs event handling as per Java 1.0 for hits
    * @return boolean state
    */

  public void mouseClicked(MouseEvent e) {
    doEvent(e, false, false, true, false);
  }

  public void mousePressed(MouseEvent e) {
    //TODO: implement this java.awt.event.MouseListener method;
  }

  public void mouseReleased(MouseEvent e) {
    //TODO: implement this java.awt.event.MouseListener method;
  }

  public void mouseEntered(MouseEvent e) {
    //TODO: implement this java.awt.event.MouseListener method;
  }

  public void mouseExited(MouseEvent e) {
    doEvent(e, false, false, false, true);
  }

  public void mouseDragged(MouseEvent e) {
    doEvent(e, false, true, false, false);
  }

  public void mouseMoved(MouseEvent e) {
    doEvent(e, true, false, false, false);
  }


	  public boolean doEvent(MouseEvent event,
       boolean move, boolean drag, boolean clik, boolean exit)
    {
        Dimension d = getSize();
        int lwi = d.width;
        int lhi = d.height;
        int buf = 0;
        int x = lwi;
        if (lhi < lwi)
        {
           x = lhi;
           buf = (lwi-lhi)/2;
        }
        int halfx = x/2;
	      int rad = x/3;
	      int inner = rad/2;
	      int cx = buf+halfx;
	      int cy = halfx;

        double px = event.getX() - cx;
        double py = cy - event.getY() ;
        double tr = Math.sqrt(px*px + py*py);

	      scan = false;

	      if(tr > rad || exit){;}
	      else if(!clik)
	      {
		        if(tr > inner)
		        {
			          scan = true;
			          feed = Math.atan2(px, py);
            }
        }
	      else
	      {
       	     up = (tr < inner);
       	     if(!up) bearing = Math.atan2(px, py);
        }
        repaint();
        return true;
    }
}


//====INTERFACE E2TimeOfDay=====================================================
interface E2TimeOfDay {
	public void setTimeOfDay(int i);
}

//====CLASS E2SelfButton========================================================
class E2SelfButton extends Button
{
	 E2TimeOfDay core;
	 int type;
	 public E2SelfButton(String label, E2TimeOfDay target, int i)
	 {
		super(label);
		core = target;
		type = i;
   }
   public boolean action(Event e, Object o)
   {
	   if(e.target != this) return false;
	   core.setTimeOfDay(type);
	   return true;
   }
}

//====CLASS EphemerisIIControlPanel=============================================
public class EphemerisIIControlPanel extends Panel implements E2TimeOfDay,
 SlideListener
{
    /**
   	* List of cults whose holy days are stored here
   	*/
   	private String[] cults = {
          "Voria","Gorgorma","Uleria","Flamal","Triolina","Dormal","Bagog"
          ,"[Summer Solstice]","Invisible God","Red Goddess","Yelmalio"
          ,"Pamalt","Babeester Gor","Asrelia","Earth Goddesses","Lodril"
          ,"Maran Gor","Lokarnos","Kyger Litor, 7 Mothers"
          ,"Argan Argar","Zorak Zoran","Waha the Butcher","Xiola Umbar","Valind"
          ,"Magasta","Subere","Godunya","Ty Kora Tek","[Winter Solstice]","Donandar"
          ,"Humakt","Storm Bull","Tsankth","Orlanth","Unholy Trio","Issaries"
          ,"Lhankor Mhy"};
   	/**
   	* The day of the year (from 1 to 294 on which these fall)
   	*/
   	private int[] dates = {
         1,3,7,23,28,37,43,
         68,80,97,110,
         116,127,134,136,138,
         139,152,175,
         177,183,189,190,193,
         196,197,201,213,215,235,
         242,258,261,263,281,286,
         287};

    /**
    * UI components
    */
	  public TextField calendar;
    private List holyDays;
	  private Slider minbar, hrbar, dowbar;
	  private Slider wkbar, yrbar, cenbar;
	  private static final String[] dow = {"Freezeday", "Waterday", "Clayday", "Windsday",
						"Fireday", "Wildday", "Godsday"};
    private static final String[] wos = {"Disorder","Harmony","Death","Fertility","Stasis",
						"Movement","Illusion","Truth","Luck","Fate"};
    private static final String[] soy = {"Sea", "Fire", "Earth", "Dark", "Storm"};

    private int seconds = 0;

    /**
    * Current date
    */
	  public int year=1600, week=0, day=0;
	  public int season = 8;

	  /**
    * hour is in days from midnight
	  * yrpart is in years from spring equinox, to nearest day before
    * harmonic is dome tilt in radians, + to North.
    */
	  public double hour=0.75, yrpart=0.0;
    private double baseHarmonic=0.0;
    public double getBaseHarmonic() {return baseHarmonic;}

    /**
    * More UI components
    */
	  private boolean ticking=false;
	  private Button ticker;

	  private Checkbox cbsec, cbmin, cbhr, cbday, cbsday, cbten, cbreverse;
    private Checkbox cbsolar, cbfree;
    public  Checkbox cbring;
    private Button freehand;
	  private CheckboxGroup cb;
	  private double delta, delta0 = 293.0/294.0, freeDelta = 1.0/48.0;

	  //private Checkbox cbsunpath, cbsouthpath, cbnames, cbframe, cblight, cbobscure;

    private E2LocatorPanel locator;
    private E2LookPanel lookat;

	  private Checkbox cbTest, cbWest, cbEast, cbPamalt, cbOrlanth, cbPeloria;
	  private CheckboxGroup cbTime;

    private Frame context;
    public void associate(Frame f) {context=f;}

    public void getSolarDelta(boolean next)
    {
        double dayLength =  E2Param.getDayLength(getBaseHarmonic());
        double dawn = (1.0-dayLength)/2.0; // fraction of day after midnight
        boolean day = (hour >= dawn) && ((1.0-hour) > dawn);
        double phase = 0;
        if(day) phase = (hour-dawn)/dayLength;
        else if(hour > 0.5) phase = (hour -(1.0-dawn))/(1.0-dayLength);
        else phase = (hour+dawn)/(1.0-dayLength);

        dayLength = E2Param.getDayLength(
            E2Param.getHarmonic(week, next? this.day+1 : this.day-1, hour));
        dawn = (1.0-dayLength)/2.0;

        double newhour = hour;
        if(day) newhour = phase*dayLength+dawn;
        else if(hour > 0.5) newhour = phase*(1.0-dayLength)+(1.0-dawn);
        else newhour = phase*(1.0-dayLength)-dawn;

        if(next) {delta = 1.0+newhour-hour;}
        else {delta = newhour-(hour+1.0);}
    }

    /**
    * Steps the current ephemeris time by one appropriate unit of time
    */
	  public void tick()
	  {
       if(!ticking) return;
		   Checkbox x = cb.getSelectedCheckbox();
		   if(x == cbsec) delta = 1.0/86400.0;
		   else if(x == cbmin) delta = 1.0/1440.0;
		   else if(x == cbten) delta = 1.0/144.0;
		   else if(x == cbhr) delta = 1.0/24.0;
		   else if(x == cbday) delta = 1.0;
       else if(x == cbsolar) getSolarDelta(!cbreverse.getState());
       else if(x == cbfree) delta = freeDelta;
		   else delta = delta0;

		   if(cbreverse.getState() && x!=cbsolar) hour -= delta;
		   else hour += delta;
		   setTime();
    }

	  public void setTime()
	  {
        if(Math.abs(hour) >= 1.0)
        {
             double ddays = Math.floor(Math.abs(hour));
             int idays = (int)Math.rint(ddays);
             if(hour > 0)
             {
                day += idays;
                hour -= idays;
                while(day >= 294) {day-=294; ++year;}
             }
             else
             {
                day -= idays;
                hour += idays;
                while(day < 0) {day += 294; --year;}
             }
        }

		    if(hour >= 0.999995)
		    {
			     hour -=1;
			     day += 1;
                }
		    else if (hour < 0)
		    {
                       day -=1;
			     hour += 1;
                }
		    double hval = 24.0*hour;
		    int hr = (int)(24*hour);
		    if(hval-hr > 0.9999)
		    {
			     hr+=1;
                }

		    hrbar.setValue(hr);
		    int min = (int)(hour*1440.0 - 60*hr);
                seconds = (int)(hour*86400.0 - 3600*hr - 60*min);
                minbar.setValue(min);
		    while(day > 6)
		    {
			      day -= 7;
			      week+=1;
                }
		    while (day < 0)
		    {
			      day += 7;
			      week-=1;
                }
		    dowbar.setValue(day);

		    while(week > 41)
		    {
			      year+=1;
			      week-=42;
                }
		    while (week < 0)
		    {
			      year -= 1;
			      week += 42;
                }
		    wkbar.setValue(week);

                int yp = year+11000;
                int y100 = yp%100;
                int c100 = (yp/100);
		    yrbar.setValue(y100);
		    cenbar.setValue(c100);
		    double saveHour = hour;
		    setLabel();
		    hour = saveHour;
    }

    /**
    * Sets the labels to follow the slider values; computes tilt of dome
    */
	  private void setLabel()
	  {
		    year = (cenbar.getValue()-110)*100 + yrbar.getValue();
		    day = dowbar.getValue();

		    int min = minbar.getValue();

		    int hhr = hrbar.getValue();

		    hour  = ((double)hrbar.getValue())/24.0;
		    hour += ((double)minbar.getValue())/1440.0;
		    hour += ((double)seconds)/86400.0;

		    week = wkbar.getValue();

		    if(week>41) week=41;
		    wkbar.setValues(week, 5, 0, 41);

		    double dday = setHarmonic();
        setLocal();
    }

    public void setLocal()
    {
        double dawn =(1.0-E2Param.getDayLength(baseHarmonic))/2.0; // fraction of day after midnight
        double dusk =(1.0+E2Param.getDayLength(baseHarmonic))/2.0; // fraction of day after midnight
        double part, h24;
        int hr, min, sec;
        String hh, mm, ss, text;

        Checkbox x = cbTime.getSelectedCheckbox();
        if(x==cbTest)
		    {
			      h24 = hour*24.0;
			      hr = (int) h24;
			      min = (int)((h24-hr)*60.0);
			      sec = (int)(((h24 - hr)*60.0 - min)*60.0);
			      hh = ""+hr;
			      if(hr<10) hh = "0"+hh;
			      mm = ""+min;
			      if(min < 10) mm = "0"+mm;
            ss = ""+sec;
            if(sec<10) ss = "0"+ss;

			      text = hh+":"+mm+":"+ss+"    "+dow[day]+"    ";

			      if(week < 5* season)
			      {
				       text += wos[week%season]+" week, "+
					     soy[week/season]+" season "+year+"ST    ";
            }
			      else
			      {
				       int w = week - 5*season + 1;
				       text += "Sacred Week "+w+" "+year+"ST    ";
            }
        		double dday = (7.0*week+day)+hour;
			      String dds = ""+dday;
			      if(dds.length() > 7) dds = dds.substring(0,7);

			      String hd = ""+E2Param.getTilt(baseHarmonic);
			      if(hd.length() > 7) hd = hd.substring(0,7);
			      text += "Day "+dds+" tilt "+hd+"    ";

			      double dd = E2Param.getDayLength(baseHarmonic)*24.0;
			      hr = (int)dd;
			      min = (int)((dd-hr)*60.0);
			      text+="day len/24h = "+hr+"h"+min+"m";
        }
        else if(x==cbWest)  // assumes 64 minute hours
        {
            if(hour<dawn)
            {
                part = 8.0*((hour/dawn)+1.0);
                hr = (int)part;
                min = (int)((part-hr)*64.0);
                mm = ""+min; if(min < 10) mm = "0"+mm;
                text = "Night hour "+hr+":"+mm;
            }
            else if (hour < dusk)
            {
                part = 16.0*(hour-dawn)/(dusk-dawn);
                hr = (int) part;
                min = (int)((part-hr)*64.0);
                mm = ""+min; if(min < 10) mm = "0"+mm;
                text = "Day hour "+hr+":"+mm;
            }
            else
            {
                part = 8.0*(hour-dusk)/(1.0-dusk);
                hr = (int)part;
                min = (int)((part-hr)*64.0);
                mm = ""+min; if(min < 10) mm = "0"+mm;
                text = "Night hour "+hr+":"+mm;
            }
			      int dd = day+7*week;
			      text += "    Day "+dd+" "+year+"ST";
        }
        else if (x==cbEast)
        {
			      h24 = hour*24.0;
			      hr = (int) h24;
			      min = (int)((h24-hr)*60.0);
			      sec = (int)(((h24 - hr)*60.0 - min)*60.0);
			      hh = ""+hr;
			      if(hr<10) hh = "0"+hh;
			      mm = ""+min;
			      if(min < 10) mm = "0"+mm;
            ss = ""+sec;
            if(sec<10) ss = "0"+ss;

			      text = hh+":"+mm+":"+ss+"    ";
            int num = day+1;

            text += ""+num+"-day    The week of ";
            switch (week)
            {
                   case  0: text += "Wise Passivity"; break;
                   case  1: text += "Tranquil Composure"; break;
                   case  2: text += "Lucid Stillness"; break;
                   case  3: text += "Taciturn Solemnity"; break;
                   case  4: text += "Fortunate Incapacity"; break;
                   case  5: text += "Profound Solitude"; break;
                   case  6: text += "Futile Annihilation"; break;

                   case  7: text += "Erudite Obfuscation"; break;
                   case  8: text += "Concealed Truths"; break;
                   case  9: text += "Privy Trust"; break;
                   case 10: text += "Inner Knowledge"; break;
                   case 11: text += "Constrained Discretion"; break;
                   case 12: text += "Esoteric Reality"; break;
                   case 13: text += "Lurking Ambuscade"; break;

                   case 14: text += "Naked Essence"; break;
                   case 15: text += "the Fervid Soul"; break;
                   case 16: text += "Cheery Exhilaration"; break;
                   case 17: text += "Vitality"; break;
                   case 18: text += "Absolute Innascibility"; break;
                   case 19: text += "Pleasant Torpor"; break;
                   case 20: text += "the Journey's End"; break;

                   case 21: text += "Practiced Sagacity"; break;
                   case 22: text += "Adroit Readiness"; break;
                   case 23: text += "Conscious Insight"; break;
                   case 24: text += "Ingenious Success"; break;
                   case 25: text += "Exquisite Sensation"; break;
                   case 26: text += "Poignant Memory"; break;
                   case 27: text += "Dull Oblivion"; break;

                   case 28: text += "Assured Credence"; break;
                   case 29: text += "Seeking Comprehension"; break;
                   case 30: text += "Intelligent Incredulity"; break;
                   case 31: text += "Sufficient Omniscience"; break;
                   case 32: text += "Hesitant Cognizance"; break;
                   case 33: text += "Mature Nescience"; break;
                   case 34: text += "Mindless Dolour"; break;

                   case 35: text += "Exuberant Creation"; break;
                   case 36: text += "Portentous Gloom"; break;
                   case 37: text += "the Unpathed Waters"; break;
                   case 38: text += "the Living Glebe"; break;
                   case 39: text += "Effulgent Radiance"; break;
                   case 40: text += "Novel Tempestuousness"; break;
                   case 41: text += "Universal Ruin"; break;
            }
            text += "    The month of ";
            switch (week/7)
            {
                   case 0: text += "Silence"; break;
                   case 1: text += "Secrets"; break;
                   case 2: text += "Being"; break;
                   case 3: text += "Experience"; break;
                   case 4: text += "Thought"; break;
                   case 5: text += "Spirit"; break;
            }
			      text += "    "+year+"ST";
        }
        else if (x==cbPamalt)
        {
            double del = (dusk-dawn)/4.0;
            double del2 = (1.0-dusk)/2.0;
            if(hour<(dawn/2)) text = "Late Night";
            else if (hour<dawn) text = "Dawning";
            else if (hour<dawn+del) text = "Early Morning";
            else if (hour<dawn+2*del) text = "Early Day";
            else if (hour<dawn+3*del) text = "Early Eve";
            else if (hour<dusk) text = "Late Eve";
            else if (hour<dusk+del2) text = "Gloaming";
            else text = "Early Night";
			      int dd = day+7*week;
			      text += "    Day "+dd+" "+year+"ST";
        }
        else if (x==cbOrlanth)
        {
            if(hour<dawn)
            {
                part = 6.0*((hour/dawn)+1.0);
                hr = (int)part;
                min = (int)((part-hr)*60.0);
                mm = ""+min; if(min < 10) mm = "0"+mm;
                text = "Night hour "+hr+":"+mm;
            }
            else if (hour < dusk)
            {
                part = 12.0*(hour-dawn)/(dusk-dawn);
                hr = (int)part;
                min = (int)((part-hr)*60.0);
                mm = ""+min; if(min < 10) mm = "0"+mm;
                text = "Day hour "+hr+":"+mm;
            }
            else
            {
                part = 6.0*(hour-dusk)/(1.0-dusk);
                hr = (int)part;
                min = (int)((part-hr)*60.0);
                mm = ""+min; if(min < 10) mm = "0"+mm;
                text = "Night hour "+hr+":"+mm;
            }
			      text += "    "+dow[day]+"    ";

			      if(week < 5* season)
			      {
				        text += wos[week%season]+" week, "+
					      soy[week/season]+" season "+year+"ST";

            }
			      else
			      {
                int w = week - 5*season + 1;
				        text += "Sacred Week "+w+" "+year+"ST";
            }
        }
        else //cbPeloria;
        {
             if(hour<dawn)
             {
                 part = 5.0*((hour/dawn)+1.0);
                 hr = (int)part;
                 min = (int)((part-hr)*100.0);
                 mm = ""+min; if(min < 10) mm = "0"+mm;
                 text = "Night hour "+hr+"."+mm;
             }
             else if (hour < dusk)
             {
                 part = 15.0*(hour-dawn)/(dusk-dawn);
                 hr = (int)part;
                 min = (int)((part-hr)*100.0);
                 mm = ""+min; if(min < 10) mm = "0"+mm;
                 text = "Day hour "+hr+"."+mm;
             }
             else
             {
                 part = 5.0*(hour-dusk)/(1.0-dusk);
                 hr = (int)part;
                 min = (int)((part-hr)*100.0);
                 mm = ""+min; if(min < 10) mm = "0"+mm;
                 text = "Night hour "+hr+"."+mm;
             }
			       int dd = day+7*week;
			       text += "    Day "+dd+" "+year+"ST";
        }
	   	  calendar.setText(text);
    }



	  private double setHarmonic()
	  {
        double dday = (7.0*week+day);
		    yrpart = dday/294.0;
        baseHarmonic = E2Param.getHarmonic(week, day, hour);
        dday+=hour;
		    return dday;
    }

    /**
    * Spin angle of the dome from Spring equinox
    * @return double spin angle in radians
    */
	  public double spin()
	  {
		   return Math.PI*2.0*
			 ((hour-0.75) // daily motion
			              +yrpart);	// annual motion
    }
    /**
    * Tilt of the dome from zero, + -> North
    * @return double tilt angle in radians
    */
	  public double slide()
	  {
		    return E2Param.getTilt(baseHarmonic)*Math.PI/180.0;
    }


    /**
    * Constructor - places UI components in a Panel
    */
	  public EphemerisIIControlPanel(Panel aux)
	  {
		   super();
		   setLayout(new GridLayout(0,2));

       // Time and day reporting/ holyday selection  (top left)
		   Panel p = new Panel();
		   p.setLayout(new GridLayout(0,1));
	     p.setBackground(Color.lightGray);
       Panel p2 = new Panel();
		   p2.setLayout(new GridLayout(0,1));
	     p2.setBackground(Color.lightGray);

       cbTime = new CheckboxGroup();
       cbTest = new Checkbox("Astronometric", cbTime, true);
       cbTest.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e)
        { setLocal();}});
       p2.add(cbTest);
       cbWest = new Checkbox("Western", cbTime, false);
       cbWest.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e)
        { setLocal();}});
       p2.add(cbWest);
       cbEast = new Checkbox("Eastern", cbTime, false);
       cbEast.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e)
        { setLocal();}});
       p2.add(cbEast);
       cbPamalt = new Checkbox("Pamaltelan", cbTime, false);
       cbPamalt.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e)
        { setLocal();}});
       p2.add(cbPamalt);
       cbOrlanth = new Checkbox("Orlanthi", cbTime, false);
       cbOrlanth.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e)
        { setLocal();}});
       p2.add(cbOrlanth);
       cbPeloria = new Checkbox("Pelorian", cbTime, false);
       cbPeloria.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e)
        { setLocal();}});
       p2.add(cbPeloria);

       p.add(p2);

		   aux.setLayout(new GridLayout(1,0));
		   calendar = new TextField();
		   calendar.setEditable(false);
		   aux.add(calendar);

		   calendar.setText("Freezeday 00:00:00     Disorder week Sea season 1600ST");

       holyDays = new List(4, false);
       holyDays.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          handleList();}});
       holyDays.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          handleList();}});
       for(int hd=0; hd<cults.length; ++hd) holyDays.addItem(cults[hd]);
       p.add(holyDays);
       holyDays.select(0);
	     // holyDays.enable(false);
		   add(p);

       // Time control slider  (top right)
		   p = new Panel();
		   p.setLayout(new GridLayout(0,1));
	     p.setBackground(Color.lightGray);
		   //p.add(new Label(""));
		   p.add(new Label("Minutes"));
		   minbar = new Slider(0, 5,
             0, 59);
       minbar.addSlideListener(this);
       p.add(minbar);
		   p.add(new Label("Hours"));
		   hrbar = new Slider(0, 4,
			       0, 23);
       hrbar.addSlideListener(this);
       p.add(hrbar);
		   p.add(new Label("Day of week"));
		   dowbar = new Slider(0, 1,
             0, 6);
       dowbar.addSlideListener(this);
       p.add(dowbar);
		   p.add(new Label("Week"));
       wkbar = new Slider(0, 5,
			       0, 41);
       wkbar.addSlideListener(this);
       p.add(wkbar);
		   p.add(new Label("Years"));
		   yrbar = new Slider(0, 5,
			       0, 99);
       yrbar.addSlideListener(this);
       p.add(yrbar);
		   p.add(new Label("Centuries"));
		   cenbar = new Slider(126, 5,
             0, 129);
       cenbar.addSlideListener(this);
       p.add(cenbar);
       cbring = new Checkbox("Real size objects");
       cbring.setState(false);
       p.add(cbring);
		   add(p);
       setLabel();

       // Step interval selection (2nd row left)
		   p = new Panel();
		   p.setLayout(new GridLayout(0,1));
       p.setBackground(Color.lightGray);

		   p.add(new Label(""));
		   cb = new CheckboxGroup();
		   cbsec = new Checkbox("1 second", cb, false);
		   p.add(cbsec);
		   cbmin = new Checkbox("1 minute", cb, true);
		   p.add(cbmin);
		   cbten = new Checkbox("10 minutes", cb, true);
		   p.add(cbten);
		   cbhr  = new Checkbox("1 hour", cb, false);
		   p.add(cbhr);
		   cbday = new Checkbox("1 day", cb, false);
		   p.add(cbday);
		   cbsday= new Checkbox("1 sidereal day", cb, false);
		   p.add(cbsday);
       cbsolar = new Checkbox("1 solar day", cb, false);
       p.add(cbsolar);

       p2 = new Panel();
       p2.setLayout(new GridLayout(1,0));
       cbfree = new Checkbox("Ad lib.", cb, false);
       p2.add(cbfree);
       freehand = new Button("Adjust...");
       freehand.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e)
        {
            E2TimePicker pick = new E2TimePicker(context, freeDelta);
            pick.show();
            freeDelta = pick.get();
            cbfree.setState(true);
        }});
       p2.add(freehand);
       p.add(p2);

		   cbreverse= new Checkbox("Reverse", null, false);
		   p.add(cbreverse);
		   ticker = new Button("Start");
       ticker.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e)
        {
		       ticking = !ticking;
		       if(ticking)
		       {
			         ticker.setLabel("Stop");
           }
		       else ticker.setLabel("Start");
        }});

		   p.add(ticker);
		   p.add(new Label(""));
		   add(p);

       // Display selection (2nd row right)
		   p = new Panel();
		   p.setLayout(new GridLayout(0,1));
		   p.add(new Label(""));
       p.setBackground(Color.lightGray);
		   p.add(new E2SelfButton("Midnight 00h", this, 0));
		   p.add(new E2SelfButton("Dawn", this, 1));
		   p.add(new E2SelfButton("Noon", this, 2));
		   p.add(new E2SelfButton("Dusk", this, 3));
		   p.add(new E2SelfButton("Midnight 24h", this, 4));
		   p.add(new Label(""));
		   add(p);

       locator = new E2LocatorPanel(this);
       add(locator);

       lookat = new E2LookPanel();
       add(lookat);

		   setSize(300, 500);
       holyDays.makeVisible(0);
    }
	  public void setTimeOfDay(int i)
	  {
		    switch (i)
		    {
			      case 0: hour = 0.0; break;
			      case 1: hour = 0.25;
				         setHarmonic();
    				     hour = (1.0-E2Param.getDayLength(baseHarmonic))/2.0; // fraction of day after midnight
				         break;
            default:
			      case 2: hour = 0.5; break;
			      case 3: hour = 0.75;
				         setHarmonic();
    				     hour = (1.0+E2Param.getDayLength(baseHarmonic))/2.0; // fraction of day after midnight
				         break;
            case 4: hour = 86399.0/86400.0; break;
        }
		    setTime();
    }

    /**
    * Gets radial position of viewer
    * @return double fraction of distance from viewpoint to dome
    */
    public double getOffset()
    {
       if(null == locator) return 0;
       return locator.r;
    }

    /**
    * Gets angular position of viewer
    * @return double angle of viewer anticlockwise from west
    */
    public double getBearing()
    {
       if(null == locator) return 0;
       return (Math.PI/2.0)-locator.theta;
    }

    /**
    * Sets holy day list visibility suitably
    */
	  public void adjust() {holyDays.makeVisible( holyDays.getSelectedIndex() );}

    public boolean getUp() {return lookat.up;}
    public double getLook () {return lookat.bearing;}

    public void slideEvent(Slider s)
    {
      if(ticking) return;
            seconds = 0;
            setLabel();
    }

    /**
    * Performs event handling as per Java 1.0 for sliders
    * @return boolean state
    */


	  public void handleList()
	  {
          if(ticking) return ;
          int day = dates[holyDays.getSelectedIndex()] -1;
          dowbar.setValue(day % 7);
          wkbar.setValue(day/7);
          setLabel();
    }

    /**
    * Performs button event handling
    public void doIt(Component b)
	  {
        if(ticker == b)
        {
		       ticking = !ticking;
		       if(ticking)
		       {
			         ticker.setLabel("Stop");
           }
		       else ticker.setLabel("Start");
        }
        else if(freehand == b)
        {
            E2TimePicker pick = new E2TimePicker(context, freeDelta);
            pick.show();
            freeDelta = pick.get();
            cbfree.setState(true);
        }
    }
    */
}

/* end of file ControlPanel.java */

