import java.awt.*;
import java.awt.event.*;

/** 
*  Class EphemerisIIFrame
*
*/

class logoImagePanel extends Panel
{
	Image i = Toolkit.getDefaultToolkit().createImage(new TinesLogo());

	public void paint(Graphics  g)
	{
    Dimension d = getSize();
		g.drawImage(i, 0,0, d.width, d.height, this);
	}

  public Dimension minimumSize()
  {
         return new Dimension(59,59);
  }
  public Dimension maximumSize()
  {
         return new Dimension(236,236);
  }
  public Dimension preferredSize()
  {
         return new Dimension(118,118);
  }
}

class E2WizBox extends Frame implements SlideListener {

      Slider scalebar;
      Label scale;
      CheckboxGroup cbg;
      Checkbox nine;
      Checkbox yuthu;
      Checkbox variable;
      Label tilted;

      Slider stilt, etilt, wtilt;
      Label stl, etl, wtl;
      Slider sday, wday;
      Label sdl, wdl;

      Checkbox uleria;

      Slider sharday;
      Label shardayl;
      Slider sharhr;
      Label sharhrl;

      Slider twinday;
      Label twindayl;
      Slider twinhr;
      Label twinhrl;

      Slider artiaday;
      Label artiadayl;
      Slider artiahr;
      Label artiahrl;

      public E2WizBox()
      {
             super("Wizard mode controls");
             setLayout(new GridLayout(1,0));
             setBackground(Color.lightGray);

             Panel p = new Panel();
             p.setLayout(new GridLayout(0,1));
             p.add(new Label("Dome size 000s km"));
             scalebar = new Slider(20, 5,
                    20, 250);
             scalebar.addSlideListener(this);
             p.add(scalebar);
             scale = new Label("20");
             p.add(scale);
             cbg = new CheckboxGroup();
             nine = new Checkbox("9/10.6 degree tilt",cbg,true);
             p.add(nine);
             nine.addItemListener(new ItemListener() {
              public void itemStateChanged(ItemEvent e) {
                   E2Param.yuthu = yuthu.getState();
                   if(nine.getState())
                   {
                       E2Param.summerTilt = 9.0;
                       E2Param.winterTilt = -10.6;
                       E2Param.equinoxTilt = 0;
                       E2Param.summerDay = 0.1;
                       E2Param.winterDay = -1.06/9.0;
                   }
              }
             });

             yuthu = new Checkbox("Overhead at Yuthuppa (3600km N of centre)", cbg,false);
             p.add(yuthu);
             yuthu.addItemListener(new ItemListener() {
              public void itemStateChanged(ItemEvent e) {
                 E2Param.yuthu = yuthu.getState();
              }
             });
             tilted = new Label(tilt());
             p.add(tilted);
             variable = new Checkbox("Freehand control of tilt and day length",cbg,false);
             p.add(variable);
             variable.addItemListener(new ItemListener() {
              public void itemStateChanged(ItemEvent e) {
                   E2Param.yuthu = yuthu.getState();
                   E2Param.summerTilt = stilt.getValue();
                   E2Param.winterTilt = wtilt.getValue();
                   E2Param.equinoxTilt = etilt.getValue();
                   E2Param.summerDay = 0.01*sday.getValue();
                   E2Param.winterDay = 0.01*wday.getValue();
              }
             });

             uleria = new Checkbox("Uleria period 1/3 siderial day");
             p.add(uleria);
             uleria.setState(false);
             uleria.addItemListener(new ItemListener() {
              public void itemStateChanged(ItemEvent e) {
                 E2Param.uleria = uleria.getState();
              }
             });

             add(p);

             p = new Panel();
             p.setLayout(new GridLayout(0,1));

             stilt = new Slider(9, 5, 0, 90);
             stilt.addSlideListener(this);
             p.add(stilt);
             stl = new Label("Summer tilt 9 deg");
             p.add(stl);
             etilt = new Slider(0, 5, -11, 9);
             etilt.addSlideListener(this);
             p.add(etilt);

             etl = new Label("Equinox tilt 0 deg");
             p.add(etl);
             wtilt = new Slider(-11, 5, -90, 0);
             p.add(wtilt);
             wtilt.addSlideListener(this);
             wtl = new Label("Winter tilt -11 deg");
             p.add(wtl);

             sday = new Slider(10, 5, 0, 50);
             sday.addSlideListener(this);
             p.add(sday);
             sdl = new Label("Summer day length 14.4h");
             p.add(sdl);
             wday = new Slider(-12, 5, -50, 0);
             wday.addSlideListener(this);
             p.add(wday);
             wdl = new Label("Winter day length 9.1h");
             p.add(wdl);

             add(p);

             p = new Panel();
             p.setLayout(new GridLayout(0,1));
             sharday = new Slider(0, 5, 0, 28);
             sharday.addSlideListener(this);
             p.add(sharday);
             shardayl = new Label("Shargash rises day 1 year 1");
             p.add(shardayl);
             sharhr = new Slider(0, 5, 0, 144);
             sharhr.addSlideListener(this);
             p.add(sharhr);
             sharhrl = new Label("at 0h 0m");
             p.add(sharhrl);

             twinday = new Slider(0, 1, 0, 6);
             twinday.addSlideListener(this);
             p.add(twinday);
             twindayl = new Label("Twinstar rises day 1 year 1");
             p.add(twindayl);
             twinhr = new Slider(0, 5, 0, 144);
             twinhr.addSlideListener(this);
             p.add(twinhr);
             twinhrl = new Label("at 0h 0m");
             p.add(twinhrl);

             artiaday = new Slider(0, 5, 0, 112);
             artiaday.addSlideListener(this);
             p.add(artiaday);
             artiadayl = new Label("Artia rises day 1 year 1");
             p.add(artiadayl);
             artiahr = new Slider(0, 5, 0, 144);
             artiahr.addSlideListener(this);
             p.add(artiahr);
             artiahrl = new Label("at 0h 0m");
             p.add(artiahrl);
             add(p);

             pack();
      }

      private String tilt()
      {
             double size = 3.6/scalebar.getValue();
             double t = Math.asin(size)*180.0/Math.PI;
             double t2 = 10.6*t/9;
             return ""+t+"/"+t2+" degree tilt";
      }

      	public void slideEvent(Slider target)
	      {
               if(target == scalebar)
               {
                   scale.setText(""+scalebar.getValue());
                   tilted.setText(tilt());
                   E2Param.domeRadius = scalebar.getValue();
                   return;
               }
               else if(target == etilt)
               {
                   stilt.setValues(stilt.getValue(), 5, etilt.getValue(), 90);
                   wtilt.setValues(wtilt.getValue(), 5, -90, etilt.getValue());
                   etl.setText("Equinox tilt "+etilt.getValue()+" deg");
                   if(variable.getState()) E2Param.equinoxTilt = etilt.getValue();
                   return;
               }
              else if(target == stilt)
               {
                   etilt.setValues(etilt.getValue(), 5, wtilt.getValue(), stilt.getValue());
                   stl.setText("Summer tilt "+stilt.getValue()+" deg");
                   if(variable.getState()) E2Param.summerTilt = stilt.getValue();
                   return;
               }
              else if(target == wtilt)
               {
                   etilt.setValues(etilt.getValue(), 5, wtilt.getValue(), stilt.getValue());
                   wtl.setText("Winter tilt "+wtilt.getValue()+" deg");
                   if(variable.getState()) E2Param.winterTilt = wtilt.getValue();
                   return;
               }
               else if(target == sday)
               {
                   double frac = sday.getValue()*0.01;
                   if(variable.getState()) E2Param.summerDay = frac;
                   frac *= 24;
                   frac += 12;
                   sdl.setText("Summer day length "+frac+"h");
                   return;
               }
               else if(target == wday)
               {
                   double frac = wday.getValue()*0.01;
                   if(variable.getState()) E2Param.winterDay = frac;
                   frac *= 24;
                   frac += 12;
                   wdl.setText("Winter day length "+frac+"h");
                   return;
               }
               else if(target == sharday || target == sharhr)
               {
                   E2Param.shargashRise = sharday.getValue()+sharhr.getValue()/144.0;
                   shardayl.setText("Shargash rises day "+sharday.getValue()+" year 1");
                   int hr = sharhr.getValue()/6;
                   int min = (sharhr.getValue()%6)*10;
                   sharhrl.setText("at "+hr+"h "+min+"m");
                   return;
               }
               else if(target == twinday || target == twinhr)
               {
                   E2Param.twinRise = twinday.getValue()+twinhr.getValue()/144.0;
                   twindayl.setText("Twinstar rises day "+twinday.getValue()+" year 1");
                   int hr = twinhr.getValue()/6;
                   int min = (twinhr.getValue()%6)*10;
                   twinhrl.setText("at "+hr+"h "+min+"m");
                   return;
               }
               else if(target == artiaday || target == artiahr)
               {
                   E2Param.artiaRise = artiaday.getValue()+artiahr.getValue()/144.0;
                   artiadayl.setText("Artia rises day "+artiaday.getValue()+" year 1");
                   int hr = artiahr.getValue()/6;
                   int min = (artiahr.getValue()%6)*10;
                   artiahrl.setText("at "+hr+"h "+min+"m");
                   return;
               }
        }
}



public class EphemerisIIFrame extends Frame {

	EphemerisIIAnimation animation = null;
   	private MenuItem exitMI;
   	private MenuItem helpMI;
   	private MenuItem printMI;
    private CheckboxMenuItem wizardMI;
   	private Menu file, help, show;
   	private Button close;
   	private Dialog d;
   	private TextArea ta;
    private logoImagePanel lip;
    //private java.util.Properties pprop = new java.util.Properties();
    private E2WizBox wizbox = new E2WizBox();
	MenuBar mb;

	public CheckboxMenuItem cbsunpath, cbsouthpath, cbnames, 
			cbframe, cblight, cbobscure;

   	/**
   	* Default constructor with title"A BasicFrame instance" 
   	*/
   	public EphemerisIIFrame()
   	{
		this("A BasicFrame instance", true);
   	}

	public void associate(EphemerisIIAnimation a)
	{
		animation = a;
		animation.associate(this);
	}

	/**
	* Builds a frame with minimal decoration
	* @param title String to use as Frame title
	*/	
   	public EphemerisIIFrame(String title, boolean master)
   	{
    		super(title);

		    mb = new MenuBar();

		    if(master)
		    {
			    file = new Menu(StringTable.FILE);
			    mb.add(file);
          file.addSeparator();
			    exitMI = new MenuItem(StringTable.EXIT);
			    file.add(exitMI);
		    }

		    show = new Menu("Show");
		    mb.add(show);

		    cbsunpath = new CheckboxMenuItem("Show sunpath");
		    cbsunpath.setState(false);
		    show.add(cbsunpath);

		    cbsouthpath = new CheckboxMenuItem("Show southpath");
		    cbsouthpath.setState(false);
		    show.add(cbsouthpath);

		    cbnames = new CheckboxMenuItem("Show names");
		    cbnames.setState(true);
		    show.add(cbnames);

		    cbframe = new CheckboxMenuItem("Show Buserian frame");
		    cbframe.setState(false);
		    show.add(cbframe);

		    cblight = new CheckboxMenuItem("Show Lightfore path");
		    cblight.setState(false);
		    show.add(cblight);

		    cbobscure = new CheckboxMenuItem("Show stars during day");
		    cbobscure.setState(true);
		    show.add(cbobscure);

        show.addSeparator();
        wizardMI = new CheckboxMenuItem("Show wizard mode controls");
        show.add(wizardMI);


		    help = new Menu(StringTable.HELP);
		    if(master)
			    mb.setHelpMenu(help);
		    else
			    mb.add(help);

		    helpMI = new MenuItem(StringTable.ABOUT);
		    help.add(helpMI);

		    setMenuBar(mb);
		    close = null;
      	d = null;
        lip = null;
		    ta = new TextArea(Licence.text, 20, 60);
    }

   private void setupStickup()
   {
			if(d == null)
			{
				d = new Dialog(this, StringTable.LICTEXT, true);
				d.setLayout(new BorderLayout(5,5));
				d.add("Center", ta);
        lip = new logoImagePanel();
        Panel x = new Panel();
        x.add(lip);
        d.add("West", x);
				Panel p = new Panel();
				p.setLayout(new GridLayout(1,0));
				close = new Button(StringTable.CLOSE);
        close.addActionListener( new ActionListener() {
           	public void actionPerformed(ActionEvent e)
   	        {
		          d.setVisible(false);
  	        }});

				p.add(new Label(""));
				p.add(new Label(""));
				p.add(close);
				p.add(new Label(""));
				p.add(new Label(""));
				d.add("South", p);
				d.pack();
			}
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
		if(e.target == exitMI)
		{
			System.exit(0);
		}
		else if(e.target == helpMI)
		{
      ta.setText(Licence.text);
      setupStickup();
			d.show();
			return true;
		}
    else if(e.target == wizardMI)
    {
      if(wizardMI.getState()) wizbox.show();
      else wizbox.setVisible(false);
    }
		return false;
   	}


   	/**
   	* Listens for the action - JDK 1.0 style
   	* and dismisses the About dialog
   	* @param b Component that acted 
   	*/

}

/* end of file basicFrame.java */

