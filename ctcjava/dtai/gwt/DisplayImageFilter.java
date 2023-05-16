/****************************************************************
 **
 **  $Id: DisplayImageFilter.java,v 1.2 1997/08/06 23:27:02 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/DisplayImageFilter.java,v $
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

package dtai.gwt;

import java.awt.image.RGBImageFilter;
import java.awt.Color;

/**
 * DisplayImageFilter
 * @version 1.1
 * @Author  DTAI, Incorporated
 */
class DisplayImageFilter extends RGBImageFilter
{
    Color blackToColor;
    int blackToColorRgb;
    Color background;
    boolean enabled;
    int rgb;


    /**
     * DisplayImageFilter
     * @param blackToColor	Color
     * @param background	Color
     * @param enabled		boolean
     */
    public DisplayImageFilter( Color blackToColor, Color background, boolean enabled ) {
        canFilterIndexColorModel = false;
        this.blackToColor = blackToColor;
        this.background = background;
        this.enabled = enabled;
        if ( background != null ) {
            this.rgb = background.getRGB() & 0xffffff;
        }
        if ( blackToColor != null ) {
            blackToColorRgb = blackToColor.getRGB();
        }
    }

    /**
     * filterRGB
     * @param x		int
     * @param y		int
     * @param rgb	int
     * @return int
     */
    public int filterRGB( int x, int y, int rgb ) {
        if ( ( blackToColor != null ) && ( rgb == 0xff000000 ) ) {
            rgb = blackToColorRgb;
        }
        if ( ( background != null ) &&
             ( this.rgb == ( rgb & 0xffffff ) ) ) {
              return this.rgb;
        }
        if ( ( ! enabled ) && ( ( ( x % 2 ) ^ ( y % 2 ) ) == 1 ) ) {
            return ( rgb & 0xffffff );
        }
        return rgb;
    }
}
