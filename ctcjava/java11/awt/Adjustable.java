/****************************************************************
 **
 **  $Id: Adjustable.java,v 1.3 1997/08/06 23:25:16 cvs Exp $
 **
 **  $Source: /cvs/classes/java11/awt/Adjustable.java,v $
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

package java11.awt;

import java11.awt.event.AdjustmentListener;

public interface Adjustable {

    public final static int HORIZONTAL = 0;
    public final static int VERTICAL = 1;

/**
 *    Gets the orientation of the adjustable object.
 */

  public abstract int getOrientation();

/**
 *     Sets the minimum value of the adjustable object.
 *    Parameters:
 *         min - the minimum value
 */

  public abstract void setMinimum(int min);

/**
 *     Gets the minimum value of the adjustable object.
 */

  public abstract int getMinimum();

/**
 *     Sets the maximum value of the adjustable object.
 *    Parameters:
 *         max - the maximum value
 */

  public abstract void setMaximum(int max);

/**
 *     Gets the maximum value of the adjustable object.
 */

  public abstract int getMaximum();

/**
 *     Sets the unit value increment for the adjustable object.
 *    Parameters:
 *         u - the unit increment
 */

  public abstract void setUnitIncrement(int u);

/**
 *     Gets the unit value increment for the adjustable object.
 */

  public abstract int getUnitIncrement();

/**
 *     Sets the block value increment for the adjustable object.
 *    Parameters:
 *         b - the block increment
 */

  public abstract void setBlockIncrement(int b);

/**
 *     Gets the block value increment for the adjustable object.
 */

  public abstract int getBlockIncrement();

/**
 *     Sets the length of the proportionl indicator of the adjustable object.
 *    Parameters:
 *         v - the length of the indicator
 */

  public abstract void setVisibleAmount(int v);

/**
 *     Gets the length of the propertional indicator.
 */

  public abstract int getVisibleAmount();

/**
 *     Sets the current value of the adjustable object. This value must be within the range defined by the minimum and maximum values for this object.
 *    Parameters:
 *         v - the current value
 */

  public abstract void setValue(int v);

/**
 *     Gets the current value of the adjustable object.
 */

  public abstract int getValue();

/**
 *     Add a listener to recieve adjustment events when the value of the adjustable object changes.
 *    Parameters:
 *         l - the listener to recieve events
 *    See Also:
 *         AdjustmentEvent
 */

  public abstract void addAdjustmentListener(AdjustmentListener l);

/**
 *     Removes an adjustment listener.
 *    Parameters:
 *         l - the listener being removed
 *    See Also:
 *         AdjustmentEvent
 */

  public abstract void removeAdjustmentListener(AdjustmentListener l);
}
