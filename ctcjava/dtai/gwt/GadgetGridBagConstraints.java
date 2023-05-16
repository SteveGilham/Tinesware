/****************************************************************
 **
 **  $Id: GadgetGridBagConstraints.java,v 1.8 1997/08/06 23:27:04 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/GadgetGridBagConstraints.java,v $
 **
 ****************************************************************
 **
 **  Gadget Windowing Toolkit (GWT) Java Class Library
 **  Copyright (C) 1997  DTAI, Incorporated (http://www.dtai.com)
 **
 **  This library is free software; you can redistribute it and/or
 **  modify it under the terms of the GNU Library General Public
 **  License as published by the Free Software Foundation; either
 **  version 2 of the License, or (at your option) any later version.
 **
 **  This library is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 **  Library General Public License for more details.
 **
 **  You should have received a copy of the GNU Library General Public
 **  License along with this library (file "COPYING.LIB"); if not,
 **  write to the Free Software Foundation, Inc.,
 **  59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 **
 ****************************************************************/
/* DERIVED FROM...
 *
 * @(#)GridBagConstraints.java	1.5 95/12/01 Doug Stein
 *
 * Copyright (c) 1995 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

package dtai.gwt;

import java.awt.Insets;

/**
 * GridBagConstraints is used to specify constraints for components
 * laid out using the GridBagLayout class.
 * @version 1.1
 * @author DTAI, Incorporated
 */
public class GadgetGridBagConstraints implements Cloneable {
  public static final int RELATIVE = -1;
  public static final int REMAINDER = 0;

  public static final int NONE = 0;
  public static final int BOTH = 1;
  public static final int HORIZONTAL = 2;
  public static final int VERTICAL = 3;

  public static final int CENTER = 10;
  public static final int NORTH = 11;
  public static final int NORTHEAST = 12;
  public static final int EAST = 13;
  public static final int SOUTHEAST = 14;
  public static final int SOUTH = 15;
  public static final int SOUTHWEST = 16;
  public static final int WEST = 17;
  public static final int NORTHWEST = 18;

  public int gridx, gridy, gridwidth, gridheight;
  public double weightx, weighty;
  public int anchor, fill;
  public Insets insets;
  public int ipadx, ipady;

  int tempX, tempY;
  int tempWidth, tempHeight;
  int minWidth, minHeight;

  /**
   * GadgetGridBagConstraints
   */
  public GadgetGridBagConstraints () {
    gridx = RELATIVE;
    gridy = RELATIVE;
    gridwidth = 1;
    gridheight = 1;

    weightx = 0;
    weighty = 0;
    anchor = CENTER;
    fill = NONE;

    insets = new Insets(0, 0, 0, 0);
    ipadx = 0;
    ipady = 0;
  }

  /**
   * clone
   * @return Object
   */
  public Object clone () {
      try {
	  GadgetGridBagConstraints c = (GadgetGridBagConstraints)super.clone();
	  c.insets = (Insets)insets.clone();
	  return c;
      } catch (CloneNotSupportedException e) {
	  // this shouldn't happen, since we are Cloneable
	  throw new InternalError();
      }
  }
}
