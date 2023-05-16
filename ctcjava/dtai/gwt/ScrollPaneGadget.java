/****************************************************************
 **
 **  $Id: ScrollPaneGadget.java,v 1.47 1997/11/25 22:19:12 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/ScrollPaneGadget.java,v $
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

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java11.awt.Adjustable;
import java11.awt.event.AdjustmentEvent;
import java11.awt.event.AdjustmentListener;
import java11.awt.event.KeyEvent;

/**
 * A container class which implements automatic horizontal and/or vertical
 * scrolling for a single child gadget. The display policy for the
 * scrollbars can be set to:
 *
 *    1.as needed: scrollbars created and shown only when needed by scrollpane
 *    2.always: scrollbars created and always shown by the scrollpane
 *    3.never: scrollbars never created or shown by the scrollpane
 *
 * The state of the horizontal and vertical scrollbars is represented by
 * two objects (one for each dimension) which implement the Adjustable
 * interface. The API
 * provides methods to access those objects such that the attributes on
 * the Adjustable object (such as unitIncrement, value, etc.) can be manipulated.
 *
 * Certain adjustable properties (minimum, maximum, blockIncrement, and
 * visibleAmount) are set internally by the scrollpane in accordance with
 * the geometry of the
 * scrollpane and its child and these should not be set by programs using
 * the scrollpane.
 *
 * If the scrollbar display policy is defined as "never", then the scrollpane
 * can still be programmatically scrolled using the setScrollPosition()
 * method and the scrollpane
 * will move and clip the child's contents appropriately. This policy is useful
 * if the program needs to create and manage its own adjustable controls.
 *
 * The placement of the scrollbars is controlled by platform-specific properties
 * set by the user outside of the program.
 *
 * The initial size of this container is set to 100x100, but can be reset using setSize().
 *
 * Insets are used to define any space used by scrollbars and any borders created
 * by the scroll pane.
 *
 * I DON'T HANDLE THIS RIGHT NOW, BUT IN AWT:
 * getInsets() can be used to get the current value for the insets. If
 * the value of scrollbarsAlwaysVisible is false, then the value of the insets
 * will change dynamically depending on whether the scrollbars are currently
 * visible or not.
 *
 * @version 1.1
 * @author DTAI, Incorporated
 */

public class ScrollPaneGadget extends BorderGadget implements AdjustmentListener {

    /**
     * Specifies that horizontal/vertical scrollbar should be shown only when
     * the size of the child exceeds the size of the scrollpane in the horizontal/vertical
     * dimension.
     */
    public final static int SCROLLBAR_AS_NEEDED = 0;

    /**
     * Specifies that horizontal/vertical scrollbars should always be shown
     * regardless of the respective sizes of the scrollpane and child.
     */
    public final static int SCROLLBAR_ALWAYS = 1;

    /**
     * Specifies that horizontal/vertical scrollbars should never be shown
     * regardless of the respective sizes of the scrollpane and child.
     */
    public final static int SCROLLBAR_NEVER = 2;

    private int verticalScrollbarDisplayPolicy;
    private int horizontalScrollbarDisplayPolicy;

    ScrollbarGadget hScrollbar;
    ScrollbarGadget vScrollbar;
    PanelGadget viewport;

    private Gadget scrolledGadget;

    private boolean stretchingHorizontal = false;
    private boolean stretchingVertical = false;
    private boolean shrinkingHorizontal = false;
    private boolean shrinkingVertical = false;
    private boolean normalizingScrolled = false;

	private boolean constructed = false;

    /**
     * Create a new scrollpane container with a scrollbar display policy of "as needed".
     */
    public ScrollPaneGadget() {

        super.setLayout( null );

        setBorderType( BorderGadget.THREED_IN );
        setBorderThickness( 2 );

        viewport = new PanelGadget();
        viewport.setLayout( null );
        add( viewport );

        hScrollbar = new ScrollbarGadget( ScrollbarGadget.HORIZONTAL );
        hScrollbar.setMinimum( 0 );
        hScrollbar.setUnitIncrement( 20 );
        hScrollbar.addAdjustmentListener( this );
        add( hScrollbar );

        vScrollbar = new ScrollbarGadget( ScrollbarGadget.VERTICAL );
        vScrollbar.setMinimum( 0 );
        vScrollbar.setUnitIncrement( 20 );
        vScrollbar.addAdjustmentListener( this );
        add( vScrollbar );

        setVerticalScrollbarDisplayPolicy( SCROLLBAR_AS_NEEDED );
        setHorizontalScrollbarDisplayPolicy( SCROLLBAR_AS_NEEDED );

		constructed = true;
    }

	/**
     * returns true if children can overlap each other.
     * @return false
	 */
	public boolean childrenCanOverlap() {
	    return false;
	}

    /**
     * Sets the scrollbars' active update flag
     * @param activeUpdate if true (usually the default) the scrollbar will
     * update the display while dragging.
     */
    public void setActiveUpdate(boolean activeUpdate) {
        hScrollbar.setActiveUpdate(activeUpdate);
        vScrollbar.setActiveUpdate(activeUpdate);
    }

    /**
     * Sets the scrollbars' auto scrolling flag
     * @param autoScroll if true (usually the default) the user can hold down
     * the scroll buttons or drag area to automatically scroll.
     */
    public void setAutoScrolling(boolean autoScroll) {
        hScrollbar.setAutoScrolling(autoScroll);
        vScrollbar.setAutoScrolling(autoScroll);
    }

    /**
     * Adds the specified gadget to this scroll pane container. If the scroll
     * pane has an existing child gadget, that gadget is removed and the new one is
     * added.
     * Overrides:
     *      add in class Container
     *
     * @param gadget - the gadget to be added
     * @param pos - position of child gadget (must be <= 0)
     * @return Gadget
     */
    public Gadget add( Gadget gadget, int pos ) {
        if ( ( gadget == hScrollbar ) ||
             ( gadget == vScrollbar ) ||
             ( gadget == viewport ) ) {
            return super.add( gadget, pos );
        }
        else {
            if ( gadget != scrolledGadget ) {
                if ( scrolledGadget != null ) {
                    viewport.remove( scrolledGadget );
                }
                scrolledGadget = gadget;
                if ( gadget != null ) {
                    return viewport.add( scrolledGadget );
                }
            }
            return gadget;
        }
    }

    /**
     * isStretchingHorizontal
     *
     * @return boolean
     */
    public boolean isStretchingHorizontal() {
        return stretchingHorizontal;
    }

    /**
     * setStretchingHorizontal
     * @param stretchingHorizontal	description
     */
    public void setStretchingHorizontal( boolean stretchingHorizontal ) {
        if ( this.stretchingHorizontal != stretchingHorizontal ) {
            this.stretchingHorizontal = stretchingHorizontal;
            invalidate();
        }
    }

    /**
     * isStretchingVertical
     * @return boolean
     */
    public boolean isStretchingVertical() {
        return stretchingVertical;
    }

    /**
     * setStretchingVertical
     *
     * @param stretchingVertical	description
     */
    public void setStretchingVertical( boolean stretchingVertical ) {
        if ( this.stretchingVertical != stretchingVertical ) {
            this.stretchingVertical = stretchingVertical;
            invalidate();
        }
    }

    /**
     * isShrinkingHorizontal
     * @return boolean
     */
    public boolean isShrinkingHorizontal() {
        return shrinkingHorizontal;
    }

    /**
     * setShrinkingHorizontal
     * @param shrinkingHorizontal	description
     */
    public void setShrinkingHorizontal( boolean shrinkingHorizontal ) {
        if ( this.shrinkingHorizontal != shrinkingHorizontal ) {
            this.shrinkingHorizontal = shrinkingHorizontal;
            invalidate();
        }
    }

    /**
     * isShrinkingVertical
     * @return boolean
     */
    public boolean isShrinkingVertical() {
        return shrinkingVertical;
    }

    /**
     * setShrinkingVertical
     * @param shrinkingVertical	description
     */
    public void setShrinkingVertical( boolean shrinkingVertical ) {
        if ( this.shrinkingVertical != shrinkingVertical ) {
            this.shrinkingVertical = shrinkingVertical;
            invalidate();
        }
    }

    /**
     * isNormalizingScrolled
     * @return boolean
     */
    public boolean isNormalizingScrolled() {
        return normalizingScrolled;
    }

    /**
     * setNormalizingScrolled
     * @param normalizingScrolled	description
     */
    public void setNormalizingScrolled( boolean normalizingScrolled ) {
        if ( this.normalizingScrolled != normalizingScrolled ) {
            this.normalizingScrolled = normalizingScrolled;
            invalidate();
        }
    }

    /**
     * getVerticalScrollbarDisplayPolicy
     * @return int
     */
    public int getVerticalScrollbarDisplayPolicy() {
        return verticalScrollbarDisplayPolicy;
    }

    /**
     * setVerticalScrollbarDisplayPolicy
     * @param verticalScrollbarDisplayPolicy	description
     */

    public void setVerticalScrollbarDisplayPolicy( int verticalScrollbarDisplayPolicy ) {
        if ( this.verticalScrollbarDisplayPolicy != verticalScrollbarDisplayPolicy ) {
            this.verticalScrollbarDisplayPolicy = verticalScrollbarDisplayPolicy;

            if ( verticalScrollbarDisplayPolicy == SCROLLBAR_ALWAYS ) {
                vScrollbar.setVisible( true );
            }
            else if ( verticalScrollbarDisplayPolicy == SCROLLBAR_NEVER ) {
                vScrollbar.setVisible( false );
            }
        }
    }

    /**
     * setHorizontalScrollbarDisplayPolicy
     * @return int
     */

    public int getHorizontalScrollbarDisplayPolicy() {
        return horizontalScrollbarDisplayPolicy;
    }

    /**
     * getHorizontalScrollbarDisplayPolicy
     * @param horizontalScrollbarDisplayPolicy	description
     */

    public void setHorizontalScrollbarDisplayPolicy( int horizontalScrollbarDisplayPolicy ) {
        if ( this.horizontalScrollbarDisplayPolicy != horizontalScrollbarDisplayPolicy ) {
            this.horizontalScrollbarDisplayPolicy = horizontalScrollbarDisplayPolicy;

            if ( horizontalScrollbarDisplayPolicy == SCROLLBAR_ALWAYS ) {
                hScrollbar.setVisible( true );
            }
            else if ( horizontalScrollbarDisplayPolicy == SCROLLBAR_NEVER ) {
                hScrollbar.setVisible( false );
            }
        }
    }

    /**
     * Returns the hScrollbar to any menber of this package
     *
     * @return ScrollbarGadget
     */

    protected ScrollbarGadget getHScrollbar() {
        return hScrollbar;
    }

    /**
      * Returns the vScrollbar to any menber of this package
      *
      * @return ScrollbarGadget
      */

    protected ScrollbarGadget getVScrollbar() {
        return vScrollbar;
    }

    /**
     * Returns the current size of the scroll pane's view port.
     *
     * @return Gadget
     */

    public Gadget getViewport() {
        return viewport;
    }

    /**
     * Returns the gadget being scrolled.
     *
     * @return Gadget
     */

    public Gadget getScrolledGadget() {
        return scrolledGadget;
    }

    /**
     * Returns the height that would be occupied by a horizontal scrollbar, which is
     * independent of whether it is currently displayed by the scroll pane or not.
     *
     * @return int
     */

    public final int getHScrollbarHeight() {
        return hScrollbar.height;
    }

    /**
     * Returns the width that would be occupied by a vertical scrollbar, which
     * is independent of whether it is currently displayed by the scroll pane or not.
     *
     * @return int
     */

    public final int getVScrollbarWidth() {
        return vScrollbar.width;
    }

    /**
     * Returns the Adjustable object which represents the state of the vertical
     * scrollbar.
     * @return java11.awt.Adjustable
     */

    public final java11.awt.Adjustable getVAdjustable() {
        return vScrollbar;
    }

    /**
     * Returns the Adjustable object which represents the state of the horizontal
     * scrollbar.
     * @return java11.awt.Adjustable
     */

    public final java11.awt.Adjustable getHAdjustable() {
        return hScrollbar;
    }

    /**
     * Scrolls to the specified position within the child gadget. A call to this
     * method is only valid if the scroll pane contains a child and the specified
     * position is
     * within legal scrolling bounds of the child. Legal bounds are defined to be the
     * rectangle: x = 0, y = 0, width = (child width - view port width), height = (child
     * height - view port height). This is a convenience method which interfaces
     * with the Adjustable objects which respresent the state of the scrollbars.
     * Throws: IllegalArgumentException
     *      if specified coordinates are not within the legal scrolling bounds of
     *      the child gadget.
     *
     * @param x - the x position to scroll to
     * @param y - the y position to scroll to
     */

    public void setScrollPosition( int x, int y ) {
        hScrollbar.setValue( x );
        vScrollbar.setValue( y );
    }

    /**
     * Scrolls to the specified position within the child gadget. A call to this
     * method is only valid if the scroll pane contains a child and the specified position is
     * within legal scrolling bounds of the child. Legal bounds are defined to be
     * the rectangle: x = 0, y = 0, width = (child width - view port width), height
     * = (child height - view port height). This is a convenience method which interfaces
     * with the Adjustable objects which respresent the state of the scrollbars.
     * Throws: IllegalArgumentException
     *      if specified coordinates are not within the legal scrolling bounds of the
     *      child gadget.
     *
     * @param p - the Point representing the position to scroll to
     */

    public final void setScrollPosition( Point p ) {
        setScrollPosition( p.x, p.y );
    }

    /**
     * Returns the current x,y position within the child which is displayed at the 0,0
     * location of the scrolled panel's view port. This is a convenience method which
     * interfaces with the adjustable objects which respresent the state of the scrollbars.
     *
     * @returns the coordinate position for the current scroll position
     */

    public Point getScrollPosition() {
        return new Point( hScrollbar.getValue(), vScrollbar.getValue() );
    }

    /**
     * Sets the layout manager for this container. This method is overridden to
     * prevent the layout mgr from being set.
     * Overrides:
     *      setLayout in class Container
     *
     * @param mgr - the specified layout manager
     *
     */

    public final void setLayout( GadgetLayoutManager mgr ) {
		if ( constructed ) {
			throw new IllegalArgumentException();
		}
    }

    /**
     * Override this in a subclass if you want the scrolled pane
     * to default to a certain size.
     * @return Dimension
     */

    public Dimension getScrolledPreferredSize() {
        if ( scrolledGadget == null ) {
            return new Dimension();
        }
        else {
            return scrolledGadget.getPreferredSize();
        }
    }

    /**
     * getScrolledMinimumSize
     * @return Dimension
     */
    public Dimension getScrolledMinimumSize() {
        if ( scrolledGadget == null ) {
            return new Dimension();
        }
        else {
            return scrolledGadget.getMinimumSize();
        }
    }

    /**
     * getOuterPreferredSize
     * @return Dimension
     */
    public Dimension getOuterPreferredSize() {
        Dimension pref = new Dimension();
        Insets i = getInsets();
        pref.width += i.left + i.right;
        pref.height += i.top + i.bottom;
        if ( verticalScrollbarDisplayPolicy != SCROLLBAR_NEVER ) {
            if ( ! vScrollbar.valid || vScrollbar.pref_width < 0 ) {
                pref.width += vScrollbar.getPreferredSize().width;
            }
            else {
                pref.width += vScrollbar.pref_width;
            }
        }
        if ( horizontalScrollbarDisplayPolicy != SCROLLBAR_NEVER ) {
            if ( ! hScrollbar.valid || hScrollbar.pref_height < 0 ) {
                pref.height += hScrollbar.getPreferredSize().height;
            }
            else {
                pref.height += hScrollbar.pref_height;
            }
        }
        return pref;
    }

    /**
     * gets the minimum size
     * @return Dimension
     */
    public Dimension getMinimumSize() {
	    Dimension min = getOuterPreferredSize();
        Dimension scrolledSize = getScrolledMinimumSize();
        min.width += scrolledSize.width;
        min.height += scrolledSize.height;
        return min;
    }

    /**
     * gets the preferred size
     * @return Dimension
     */
    public Dimension getPreferredSize() {
	    if ( valid && ( pref_width >= 0 ) && ( pref_height >= 0 ) ) {
	        return new Dimension( pref_width, pref_height );
	    }
	    Dimension pref = getOuterPreferredSize();
        Dimension scrolledSize = getScrolledPreferredSize();
        pref.width += scrolledSize.width;
        pref.height += scrolledSize.height;
        return pref;
    }

    /**
     * Lays out this container by resizing its child to its preferred size. If the new preferred size of the child causes the current scroll position to be invalid, the scroll
     * position is set to the closest valid position.
     *
     * Overrides:
     *      doLayout in class Container
     * @see validate
     */

    public void doLayout() {

        Insets i = getInsets();

        int vpWidth = width - i.left - i.right;
        int vpHeight = height - i.bottom - i.top;

        int scrolledGadgetX = 0;
        int scrolledGadgetY = 0;
        int scrolledGadgetWidth = 0;
        int scrolledGadgetHeight = 0;

        if ( scrolledGadget != null ) {
            scrolledGadgetX = scrolledGadget.x;
            scrolledGadgetY = scrolledGadget.y;
            scrolledGadgetWidth = scrolledGadget.width;
            scrolledGadgetHeight = scrolledGadget.height;

            if ( stretchingHorizontal || stretchingVertical ||
                 shrinkingHorizontal || shrinkingVertical ||
                 normalizingScrolled ) {
                if ( ( ! scrolledGadget.valid ) ||
                     ( scrolledGadget.pref_width < 0 ) ||
                     ( scrolledGadget.pref_height < 0 ) ) {
                    Dimension pref = scrolledGadget.getPreferredSize();
                    scrolledGadgetWidth = pref.width;
                    scrolledGadgetHeight = pref.height;
                }
                else {
                    scrolledGadgetWidth = scrolledGadget.pref_width;
                    scrolledGadgetHeight = scrolledGadget.pref_height;
                }
            }
            else if ( scrolledGadgetWidth == 0 || scrolledGadgetHeight == 0 ) {
                // added June 5, 1997 for TextArea to work first time
                Dimension pref = scrolledGadget.getPreferredSize();
                scrolledGadgetWidth = pref.width;
                scrolledGadgetHeight = pref.height;
            }
        }

        int vScrollbarWidth;
        if ( ( ! vScrollbar.valid ) ||
             ( vScrollbar.pref_width < 0 ) ||
             ( vScrollbar.pref_height < 0 ) ) {
            Dimension pref = vScrollbar.getPreferredSize();
            vScrollbarWidth = pref.width;
        }
        else {
            vScrollbarWidth = vScrollbar.pref_width;
        }

        int hScrollbarHeight;
        if ( ( ! hScrollbar.valid ) ||
             ( hScrollbar.pref_width < 0 ) ||
             ( hScrollbar.pref_height < 0 ) ) {
            Dimension pref = hScrollbar.getPreferredSize();
            hScrollbarHeight = pref.height;
        }
        else {
            hScrollbarHeight = hScrollbar.pref_height;
        }

        boolean requiresHoriz = scrolledGadget.requiresHorizScrollbar();
        boolean requiresVert = scrolledGadget.requiresVertScrollbar();

        int testHeight = vpHeight;

        if ( horizontalScrollbarDisplayPolicy == SCROLLBAR_AS_NEEDED ) {
            if ( ( ( scrolledGadget == null ) ||
                   ( scrolledGadgetWidth <= vpWidth ) ) &&
                 ( ! requiresHoriz ) ) {
                hScrollbar.setVisible( false, false );
            }
            else {
                hScrollbar.setVisible( true, false );
                testHeight -= hScrollbarHeight;
            }
        }

        if ( verticalScrollbarDisplayPolicy == SCROLLBAR_AS_NEEDED ) {
            if ( ( ( scrolledGadget == null ) ||
                   ( scrolledGadgetHeight <= testHeight ) ) &&
                 ( ! requiresVert ) ) {
                vScrollbar.setVisible( false, false );
            }
            else {
                vScrollbar.setVisible( true, false );

                // test for horizontal scroll again
                if ( ( horizontalScrollbarDisplayPolicy == SCROLLBAR_AS_NEEDED ) &&
                     ( scrolledGadgetWidth > (vpWidth - vScrollbarWidth) ) &&
                     ( ! hScrollbar.isVisible() ) ) {
                    hScrollbar.setVisible( true, false );
                }
            }
        }

        boolean hVisible = hScrollbar.isVisible();
        boolean vVisible = vScrollbar.isVisible();

        if (hVisible) {
            vpHeight -= hScrollbarHeight;
        }
        if (vVisible) {
            vpWidth -= vScrollbarWidth;
        }

        if ( hVisible ) {
            if ( shrinkingHorizontal ) {
                scrolledGadgetX = 0;
                scrolledGadgetWidth = vpWidth;
            }
        }
        if ( vVisible ) {
            if ( shrinkingVertical ) {
                scrolledGadgetY = 0;
                scrolledGadgetHeight = vpHeight;
            }
        }

        if ( hVisible &&
             ( scrolledGadget != null ) &&
             ( scrolledGadgetWidth > vpWidth ) ) {
            hScrollbar.setMaximum( scrolledGadgetWidth - vpWidth );
            scrolledGadgetX = -hScrollbar.getValue();
            hScrollbar.setVisibleAmount( vpWidth );
            hScrollbar.setBlockIncrement( vpWidth );
        } else if (!requiresHoriz) {
            hScrollbar.setMaximum( 0 );
            scrolledGadgetX = -hScrollbar.getValue();
        }

        if ( vVisible &&
             ( scrolledGadget != null ) &&
             ( scrolledGadgetHeight > vpHeight ) ) {
            vScrollbar.setMaximum( scrolledGadgetHeight - vpHeight );
            scrolledGadgetY = -vScrollbar.getValue();
            vScrollbar.setVisibleAmount( vpHeight );
            vScrollbar.setBlockIncrement( vpHeight );
        } else if (!requiresVert) {
            vScrollbar.setMaximum( 0 );
            scrolledGadgetY = -vScrollbar.getValue();
        }

        if ( hVisible ) {
            hScrollbar.setBounds( i.left, height - i.top - hScrollbarHeight,
                                  vpWidth, hScrollbarHeight, false );
        }
        else {
            scrolledGadgetX = 0;
            if ( stretchingHorizontal ) {
                scrolledGadgetWidth = vpWidth;
            }
        }

        if ( vVisible ) {
            vScrollbar.setBounds( width - i.left - vScrollbarWidth, i.top,
                                  vScrollbarWidth, vpHeight, false );
        }
        else {
            scrolledGadgetY = 0;
            if ( stretchingVertical ) {
                scrolledGadgetHeight = vpHeight;
            }
        }

        viewport.setBounds( i.left, i.top, vpWidth, vpHeight, false );

        if ( scrolledGadget != null ) {
            if ( stretchingHorizontal || stretchingVertical ||
                 shrinkingHorizontal || shrinkingVertical ||
                 normalizingScrolled ) {
                resizeScrolled( scrolledGadget, viewport, scrolledGadgetX, scrolledGadgetY,
                                scrolledGadgetWidth, scrolledGadgetHeight, false );
            }
            else {
				if ( scrolledGadgetWidth == 0 || scrolledGadgetHeight == 0 ) {
					Dimension pref = scrolledGadget.getPreferredSize();
					scrolledGadgetWidth = pref.width;
					scrolledGadgetHeight = pref.height;
				}
				scrolledGadget.setBounds(scrolledGadgetX, scrolledGadgetY,
										 scrolledGadgetWidth, scrolledGadgetHeight, false);
            }
        }
    }

    /**
     * resizeScrolled
     * @param scrolledGadget	description
     * @param viewport	description
     * @param x	description
     * @param y	description
     * @param width	description
     * @param height	description
     */
    public void resizeScrolled( Gadget scrolledGadget, PanelGadget viewport,
                                int x, int y, int width, int height ) {
        scrolledGadget.setBounds( x, y, width, height, true );
    }

    /**
     * resizeScrolled
     * @param scrolledGadget	description
     * @param viewport	description
     * @param x	description
     * @param y	description
     * @param width	description
     * @param height	description
     * @param invalidate	description
     */
    public void resizeScrolled( Gadget scrolledGadget, PanelGadget viewport,
                                int x, int y, int width, int height, boolean invalidateParent ) {
        scrolledGadget.setBounds( x, y, width, height, invalidateParent );
    }

    /**
     * overrideHorizontalScrollbar
     * @param sb	description
     * @return boolean
     */
    public boolean overrideHorizontalScrollbar( ScrollbarGadget sb ) {
        return false;
    }

    /**
     * overrideVerticalScrollbar
     * @param sb	description
     * @return boolean
     */
    public boolean overrideVerticalScrollbar( ScrollbarGadget sb ) {
        return false;
    }

    /**
     * adjustmentValueChanged
     * @param e	description
     */
    public void adjustmentValueChanged(AdjustmentEvent e) {
        if ( scrolledGadget != null ) {
            if ( e.getAdjustable() == hScrollbar ) {
                scrolledGadget.setLocation( -hScrollbar.getValue(), scrolledGadget.y );
            }
            else {
                scrolledGadget.setLocation( scrolledGadget.x, -vScrollbar.getValue() );
            }
        }
    }


    /**
     * processKeyEvent
     * @param e	the KeyEvent
     */
    protected void processKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_PRESSED) {
            boolean handled = true;
            switch ( e.getKeyCode() ) {
                case KeyEvent.PGUP:
                    if (vScrollbar.isVisible()) {
                        vScrollbar.setValue(vScrollbar.getValue()-vScrollbar.getBlockIncrement());
                    }
                    break;
                case KeyEvent.PGDN:
                    if (vScrollbar.isVisible()) {
                        vScrollbar.setValue(vScrollbar.getValue()+vScrollbar.getBlockIncrement());
                    }
                    break;
                case KeyEvent.HOME:
                    if (vScrollbar.isVisible()) {
                        vScrollbar.setValue(vScrollbar.getMinimum());
                    }
                    if (hScrollbar.isVisible()) {
                        hScrollbar.setValue(hScrollbar.getMinimum());
                    }
                    break;
                case KeyEvent.END:
                    if (vScrollbar.isVisible()) {
                        vScrollbar.setValue(vScrollbar.getMaximum());
                    }
                    if (hScrollbar.isVisible()) {
                        hScrollbar.setValue(hScrollbar.getMaximum());
                    }
                    break;
                default:
                    handled = false;
            }
            if (handled) {
                e.consume();
            }
        }
        super.processKeyEvent(e);
	}
}
