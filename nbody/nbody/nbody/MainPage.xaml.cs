using System;
using System.Net.Security;
using System.Threading;
using System.Windows.Controls;

namespace nbody
{
  public partial class MainPage : Page
  {
    private readonly NbodyAnimation ac = new NbodyAnimation();

    public MainPage()
    {
      this.InitializeComponent();

      // Enter construction logic here...
      var all = FindName("All") as Panel;
      var theta = FindName("Theta") as NumericUpDown;
      theta.ValueChanged += ((x0, x1) => { ac.theta = theta.Value * Math.PI / 180.0; ac.paint(all); });
      LayoutUpdated += ((x0, x1) => { ac.paint(all); });

      var stopgo = FindName("StopGo") as Button;
      stopgo.Click += ((x0, x1) =>
      {
        if (ac.begun)
        {
          ac.reset();
          theta.IsEnabled = true;
          stopgo.Content = "Start";
          ac.paint(all);
        }
        else
        {
          theta.IsEnabled = false;
          stopgo.Content = "Stop";
          ac.begin();
        }
      });
    }

#if TODO
    public boolean handleEvent(Event evt)
    {
      if (ac.begun) return super.handleEvent(evt);
      if (evt.target == sb)
      {
        double d = ((double)sb.getValue()) * 0.1;
        l.setText("" + d);
        ac.theta = d * Math.PI / 180.0;
        return true;
      }
      return super.handleEvent(evt);
    }

    /**
    * Main initialization
    * @see Applet#init
    */

    public void init()
    {
      ac = new NbodyAnimation();
      engine = new Animation(ac);

      setLayout(new GridLayout(1, 1));

      if (master)
      {
        display = new Frame("Lambda Serpentis II");
      }
      else
      {
        display = new BasicFrame("Lambda Serpentis II");
        ((BasicFrame)display).setText(getAppInfo());
        ((BasicFrame)display).makeSlave();
      }
      Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
      display.resize(d.width / 2, d.height / 2);
      display.show();

      // needed for java in JDK 1.1
      engine.resize(d.width / 2, d.height / 2);

      display.setLayout(new GridLayout(1, 1));
      display.add(engine);

      setLayout(new BorderLayout());

      Panel ps = new Panel();
      sb = new Scrollbar(Scrollbar.HORIZONTAL, 0, 50, 0, 3600);
      add("Center", ps);
      ps.setLayout(new BorderLayout());
      ps.add("South", sb);

      l = new Label("0.0");
      ps.add("North", l);
      add("North", new Label("Separation of the moons (degrees)"));

      Panel pb = new Panel();
      b = new ActionButton("Start", this);
      add("South", pb);
      pb.add(b);
    }

    public void doIt(Component bb)
    {
      if (ac.begun)
      {
        ac.reset();
        b.setLabel("Start");
      }
      else
      {
        ac.begin();
        b.setLabel("Stop");
        b.invalidate();
      }
    }

    /**
    * Main (re-)start; thread is
    * fired up at this point.
    */

    public void start()
    {
      if (display != null) display.show();
      if (null == appletDynamic)
      {
        appletDynamic = new Thread(this);
        appletDynamic.start();
      }
    }

    /**
    * Main graceful suppression (iconise,
    * leave page or whatever).  Called before
    * destroy()
    */

    public void stop()
    {
      if (display != null) display.hide();
      if (null != appletDynamic)
      {
        //appletDynamic.stop();
        appletDynamic = null;
      }
    }

    /**
    * Main final termination and tidy
    */

    public void destroy()
    {
    }

    /**
    * Output to screen
    * @param g Graphic to which to draw
    */

    public void paint(Graphics g)
    {
      engine.repaint();
    }

    /**
    * work routine
    */

    public void run()
    {
      for (; ; )
      {
        repaint();
        try { Thread.sleep(100); }
        catch (InterruptedException ignored) { }
      }
    }

    public String getAppletInfo()
    {
      return Nbody.getAppInfo();
    }

    /**
    * Return license etc details
    */

    public static String getAppInfo()
    {
      return
       "Nbody - An N-body simulation\r\n"
      + "Version 1.1 Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1997-2002\r\n"
      + "This program is an implementation of Sverre Aarseth's N-body code\r\n"
      + "as given in Binney & Tremaine's _Galactic Dynamics, and converted from\r\n"
      + "the original Fortran.  The demonstration case is the planet Lambda Serpentis II\r\n"
      + "and its moons, as per the RPG _Blue Planet (and that is me in the\r\n"
      + "Special Thanks section in the 1st edition of that game)\r\n\r\n"
      + Main.GNU_General_Public_Licence();
    }

    /**
    * Return parameter details
    * @see Applet.getParameterInfo
    */

    public String[][] getParameterInfo()
    {
      String[][] t = { { "None" }, { "N/A" }, { "This applet is purely GUI driven" } };
      return t;
    }

    /**
    * Applicationizer function
    */

    public static void main(String[] args)
    {
      Nbody self = new Nbody();
      self.master = true;
      BasicFrame window = new BasicFrame("Nbody");
      window.setText(getAppInfo());

      window.setLayout(new BorderLayout());
      window.setBackground(Color.lightGray);
      window.add("Center", self);
      self.init();
      Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

      window.resize(250, 150);

      window.show();
      self.run();
      self.stop();
      self.destroy();
    }
#endif
  }
}