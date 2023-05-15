
//Title:      Janus
//Version:    
//Copyright:  Copyright (c) 2000
//Author:     Mr. Tines
//Company:    Ravna&Tines
//Description:A multi-hop SOCKS relay service
package com.ravnaandtines;
/**
*  Class Janus
*  <P>
*  Coded & copyright Mr. Tines &lt;tines@windsong.demon.co.uk&gt; 2000
*  All rights reserved.
*  <P>
*  This application is free software; you can redistribute it and/or
*  modify it under the terms of the GNU General Public
*  License as published by the Free Software Foundation; either
*  version 2 of the License, or (at your option) any later version.
*  <P>
*  This application is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
*  General Public License for more details.
*  <P>
*  You should have received a copy of the GNU General Public
*  License along with this library (file "COPYING"); if not,
*  write to the Free Software Foundation, Inc.,
*  59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*  <P>
* @author Mr. Tines
* @version 1.0 10-Feb-2000
*
*/

import javax.swing.UIManager;
import java.awt.*;
import com.ravnaandtines.janus.*;

public class Janus
{
  public static final java.util.ResourceBundle res =
    java.util.ResourceBundle.getBundle("com.ravnaandtines.janus.Res");
  boolean packFrame = false;

  //Construct the application
  
  public Janus(String[] args)
  {
    MainFrame frame = new MainFrame(args);
    //Validate frames that have preset sizes
    //Pack frames that have useful preferred size info, e.g. from their layout
    if (packFrame)
      frame.pack();
    else
      frame.validate();
    //Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = frame.getSize();
    if (frameSize.height > screenSize.height)
      frameSize.height = screenSize.height;
    if (frameSize.width > screenSize.width)
      frameSize.width = screenSize.width;
    frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    frame.setVisible(true);
  }
//Main method
  
  public static void main(String[] args)
  {
    try 
    {
      //UIManager.setLookAndFeel(new com.sun.java.swing.plaf.windows.WindowsLookAndFeel());
      //UIManager.setLookAndFeel(new com.sun.java.swing.plaf.motif.MotifLookAndFeel());
      //UIManager.setLookAndFeel(new com.sun.java.swing.plaf.metal.MetalLookAndFeel());
    }
    catch (Exception e)
    {
    }
    new Janus(args);
  }
}

 