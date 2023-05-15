package com.ravnaandtines.util.swing;

import javax.swing.*;

/**
*  Class GroupBox - a JPanel with etched titled border and a ButtonGroup
*  that can be used to bundle together a bunch of radio buttons both
*  logically and physically.
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
* @version 1.0 20-Nov-1998
*/


public class GroupBox extends JPanel
{
    private ButtonGroup group = null;

    /**
    * Public constructor
    * @param title String to put in etched border
    */
    public GroupBox(String title)
    {
        try 
        {
            jbInit(title);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void jbInit(String title) throws Exception
    {
        setBorder(BorderFactory.createTitledBorder(title));
    }

    /**
    * Add a button to the associated ButtonGroup, creating it at need
    * @param b AbstractButton to add to the group
    */
    public void addButton(AbstractButton b)
    {
        if(group == null)
        {
            group = new ButtonGroup();
        }
        group.add(b);
    }
}

