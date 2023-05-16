/****************************************************************
 **
 **  $Id: Gadget.java,v 1.141 1998/03/10 20:44:56 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/Gadget.java,v $
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
 **  29-Dec-00 tines@ravnaandtines.com - made 1.1-only (reshape->setBounds)
 ****************************************************************/

package dtai.gwt;

import java.applet.AppletContext;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.net.URL;
import java.util.Vector;
import java11.awt.event.AWTEvent;
import java11.awt.event.ComponentEvent;
import java11.awt.event.ComponentListener;
import java11.awt.event.FocusEvent;
import java11.awt.event.FocusListener;
import java11.awt.event.KeyEvent;
import java11.awt.event.KeyListener;
import java11.awt.event.MouseEvent;
import java11.awt.event.MouseListener;
import java11.awt.event.MouseMotionListener;

/**
 * The Gadget class.
 * @version 1.1
 * @author   DTAI, Incorporated
 */
 public class Gadget implements ImageObserver {

    public static final Font defaultFont = new Font( "Dialog", Font.PLAIN, 12 );
    public static final Color transparent = new Color(0);
    public static final Color defaultColor = new Color(0);
    public static final Color defaultBackground = Color.lightGray;
    public static final Color defaultSelectedBackground = new Color( 0, 0, 153 );

    Color normalBackground = null;
    Color selectedBackground = null;
    Color normalForeground = null;
    Color selectedForeground = null;
    Font font = null;
    Font lastFont = null;
    GadgetCursor cursor = null;

    ComponentListener componentListener;
    FocusListener focusListener;
    KeyListener keyListener;
    MouseListener mouseListener;
    MouseMotionListener mouseMotionListener;

	int x = 0;
	int y = 0;
	int width;
	int height;
	protected int pref_width = -1;
	protected int pref_height = -1;

	boolean valid = false;
	boolean damaged = true;
	boolean visible = true;
	boolean enabled = true;
	boolean selected = false;
    boolean consumingTransparentClicks = false;
    boolean ignoringMouse = false;
    boolean clippingRequired = true;

    private boolean notifyShellOfDefault = false;

	ContainerGadget parent;
	GadgetShell shell;

	private ContainerGadget focusGroup;

	private GadgetHelp gadgetHelp;
	String helpId;
	private String tip;
	private String statusTip;

	int frozen = 0;

	Gadget nextFocusGadget;

	Object arg;

	private static final Object LOCK = new Object();

	/**
	 * getTreeLock
	 * @return Object
	 */
    public final Object getTreeLock() {
		return Lock.getTreeLock();
	}

	/**
 	 * Gets the parent of the component.
 	 * @return		ContainerGadget
	 */
	public ContainerGadget getParent() {
		return parent;
	}

    /**
	 * Sets the parent of the component.
	 * @param parent	ContainerGadgaet
	 */
	protected final void setParent( ContainerGadget parent ) {
	    setParent(parent, true);
	}

	/**
	 * Sets the parent of the component.
	 * @param parent	ContainerGadgaet
	 * @param invalidateParent - TBD
	 */
	protected void setParent( ContainerGadget parent, boolean invalidateParent) {
		if ( parent != this.parent ) {
		    ContainerGadget oldparent = this.parent;
    		this.parent = parent;
    		if ( parent != null ) {
        		setShell( parent.getShell() );
        		if ( gadgetHelp == null ) {
        		    setGadgetHelp( parent.getGadgetHelp() );
        		}
        		if (font == null) {
            		Font font = parent.getFont();
            		if (font != lastFont && !font.equals(lastFont)) {
                	    fontChanged();
                	}
                }
        	}
        	else {
        	    setShell( null );
        	}
        	if (invalidateParent) {
        	    invalidate();
            }
        	if (oldparent == null && parent != null && parent.isShowing()) {
    			ComponentEvent component = new ComponentEvent(null, ComponentEvent.COMPONENT_SHOWN);
    			component.setSource(this);
                processEvent(component);
            }
        }
	}

	/**
	 * Gets the shell of the component.
	 * @return 	Gadgetshell
	 */
	public final GadgetShell getShell() {
		return shell;
	}

	/**
	 * Gets the panel in which to draw overlays (menus, tips).
	 * @return	OverlayPanelGadget
	 */
    public final OverlayPanelGadget getOverlayPanel() {
        return ( shell == null ) ? null : shell.getOverlayPanel();
    }

	/**
	 * Sets the shell of the component.
	 * @param shell - TBD
	 */
	protected void setShell( GadgetShell shell ) {
		this.shell = shell;
		if ( shell != null ) {
    		shell.addSubGadget( this );
    		if ( notifyShellOfDefault ) {
    	        shell.setDefaultGadget(this);
    		    notifyShellOfDefault = false;
    		}
    	}
	}

	/**
	 * Gets the current GadgetHelp object.
	 * @return		GadgetHelp
	 */
	public final GadgetHelp getGadgetHelp() {
	    return gadgetHelp;
	}

	/**
	 * Checks whether this Gadget is the default gadget for the shell.
	 * @return	false if the shell variable is null, else
	 *    returns whether or not the shell's defaultGadget is this Gadget
	 */
	public boolean isNowDefault() {
	    GadgetShell shell = this.shell;
	    if ( shell == null ) return false;
	    return ( shell.getDefaultGadget() == this );
	}

	/**
	 * setAsDefault
	 */
	public void setAsDefault() {
	    GadgetShell shell = getShell();
	    if ( shell != null ) {
	        shell.setDefaultGadget(this);
	    }
	    else {
	        notifyShellOfDefault = true;
	    }
	}

    /**
     * doDefaultAction
     * @param e - TBD
     */
    public void doDefaultAction(AWTEvent e) {
    }

    /**
     * invokeDefault
     * @param e - TBD
     */
    public void invokeDefault(AWTEvent e) {
        GadgetShell shell = getShell();
        if ( shell != null ) {
            shell.invokeDefault(e);
        }
	}

	/**
	 * Sets nextFocusGadget variable to the given object.
	 * @param nextFocusGadget	new value for nextFocusGadget
	 */
	public void setNextFocusGadget( Gadget nextFocusGadget ) {

        this.nextFocusGadget = nextFocusGadget;
	}

	/**
	 * Gets the nextFocusGadget.
	 * @return Gadget
	 */
	public Gadget getNextFocusGadget() {
	    if ( nextFocusGadget == null ) {
	        return null;
	    }
    	return nextFocusGadget.findNextFocusGadget();
	}

	/**
	 * Returns this Gadget if it is focusTraversable.
	 * @return	this Gadget, if it is focusTraversable, else null.
	 */
	public Gadget findNextFocusGadget() {
	    if ( isFocusTraversable() ) {
	        return this;
	    }
	    else {
	        return null;
	    }
	}

	/**
	 * Sets the focusGroup to the supplied ContainerGadget.
	 * @param focusGroup	the new value for the focusGroup
	 */
	public void setFocusGroup(ContainerGadget focusGroup) {
	    this.focusGroup = focusGroup;
	}

	/**
	 * Sets the focusGroup.
	 * @return ContainerGadget
	 */
	public final ContainerGadget getFocusGroup() {
	    return focusGroup;
	}

	/**
	 * Sets gadgetHelp variable to the given object.
	 * @param gadgetHelp	new value for gadgetHelp
	 */
	public void setGadgetHelp( GadgetHelp gadgetHelp ) {
	    this.gadgetHelp = gadgetHelp;
	}

	/**
	 * Returns current HelpId variable.
	 * @return 	String - TBD
	 */
	public String getHelpId() {
	    String help = helpId;
	    if (help == null) {
	        ContainerGadget parent = getParent();
	        if (parent == null) {
	            return "";
	        }
	        return parent.getHelpId();
	    }
	    return help;
	}

	/**
	 * Sets helpId variable to the given String.
	 * @param setHelpId	new String value for helpId variable
	 */
	public final void setHelpId( String helpId ) {
	    this.helpId = helpId;
	}

	/**
	 * Returns current tip variable.
	 * @return String
	 */
	public final String getTip() {
	    return tip;
	}

	/**
	 * Sets tip variable to the given String.
	 * @param tip		new String value for tip variable
	 */
	public final void setTip( String tip ) {
	    this.tip = tip;
	}

	/**
	 * Returns current status variable.
	 * @return String
	 */
	public final String getStatusTip() {
	    return statusTip;
	}

	/**
	 * Sets status variable to the given String.
	 * @param status		new String value for status variable
	 */
	public final void setStatusTip( String statusTip ) {
	    this.statusTip = statusTip;
	}

	/**
	 * Returns the current arg variable.
	 * @return 	Object
	 */
	public final Object getArg() {
	    return arg;
	}

	/**
	 * Sets arg variable to the given object.
	 * @param arg		new value for arg
	 */
	public final void setArg( Object arg ) {
	    this.arg = arg;
	}

	/**
	 * Checks if this Component is valid. Components are
	 * invalidated when they are first shown on the screen.
	 * @return		boolean valid status
	 * @see #validate
	 * @see #invalidate
	 */
	public final boolean isValid() {
		return valid;
	}

	/**
	 * Checks if all Components are valid. Components are
	 * invalidated when they are first shown on the screen.
	 * @return		boolean valid status
	 * @see #validate
	 * @see #invalidate
	 */
	 /*
	public boolean isAllValid() {
		return valid;
	}
	*/

	/**
	 * Checks if this Component is visible. Components are initially
	 * visible (with the exception of top level components such as Frame)
	 * @return		boolean visible status
	 */
	public final boolean isVisible() {
		return visible;
	}

	/**
	 * Checks if this Component is transparent.
	 * @return		boolean result of checking background for null
	 * @see #getBackground
	 */
	public final boolean isTransparent() {
		return ( getBackground() == Gadget.transparent );
	}

	/**
	 * Checks if this Component is showing on screen. This means
	 * that the component must be visible, and it must be in a
	 * container that is visible and showing.
	 * @return		boolean result of showing checks
	 * @see #setVisible
	 */
	public final boolean isShowing() {
	    return isShowing(true);
	}

	/**
	 * Checks if this Component is showing on screen. This means
	 * that the component must be visible, and it must be in a
	 * container that is visible and showing.
	 * @return		boolean result of showing checks
	 * @see #setVisible
	 */
	public final boolean isShowing(boolean checkShell) {
	    if ( checkShell && shell == null ) {
	        return false;
	    }
	    if ( ! visible ) {
	        return false;
	    }
		if ( parent != null ) {
			return parent.isShowing(checkShell);
		}
		else if ( shell != null ) {
			if ( checkShell ) {
    			return shell.isShowing();
    		}
    		else {
    		    return true;
    		}
		}
		else {
		    return false;
		}
	}

	/**
	 * Checks if this component is selected, or
	 * if its parent is selected.
	 * @return		boolean result of selected checks
	 */
	public boolean isShowingSelected() {
	    if ( selected ) {
	        return true;
	    }
		if ( parent != null ) {
			return parent.isShowingSelected();
		}
		return false;
	}

	/**
	 * Checks if this Component is functioning on screen. This means
	 * that the component must be enabled, and it must be in a
	 * container that is enabled and functioning.
	 * @return		boolean result of functioning checks
	 * @see #setVisible
	 */
	public final boolean isFunctioning() {
	    if ( ! enabled ) {
	        return false;
	    }
		if ( parent != null ) {
			return parent.isFunctioning();
		}
		else if ( shell != null ) {
			return shell.isEnabled();
		}
		else {
		    return true;
		}
	}

	/**
	 * Checks if this Component is selected.  Components are not
	 * initially selected.
	 * @return		boolean selected status
	 * @see #setSelected
	 */
	public final boolean isSelected() {
		return selected;
	}

	/**
	 * Sets selection of a component.
	 * Components are not initially selected.
	 * @param b		new boolean selected status
	 * @see #isSelected
	 */
	public void setSelected(boolean b) {
	    if ( selected != b ) {
    		selected = b;
    		repaint();
    	}
	}

	/**
	 * Checks if this Component is enabled. Components are initially enabled.
	 * @return		boolean enabled status
	 * @see #setEnabled
	 */
	public final boolean isEnabled() {
		return enabled;
	}

	/**
	 * Enables a component.
	 * @param b		new boolean enabled status
	 * @see #isEnabled
	 */
	public void setEnabled(boolean b) {
	    if ( enabled != b ) {
    		enabled = b;
    		GadgetShell shell = this.shell;
    		if ( ( enabled == false ) &&
    		     ( shell != null ) &&
    		     ( this == shell.getFocusOwner() ) ) {
		        nextFocus();
		    }
    		repaint();
    	}
	}

	/**
	 * Shows or hides the component depending on the boolean flag b.
	 * @param b  	if true, show the component; otherwise, hide the component.
	 * @see #isVisible
	 */
	public final void setVisible(boolean b) {
	    setVisible( b, true );
	}

	/**
	 * Shows or hides the component depending on the boolean flag b.
	 * @param b  			if true, show the component; otherwise, hide the component.
	 * @param invalidateParent if true, invalidate Parent, else do not.
	 * @see #isVisible
	 */
	public void setVisible(boolean b, boolean invalidateParent) {
	    if ( visible != b ) {
    		visible = b;
			notifyStateChange();
	        invalidate(invalidateParent);
		    if ( ! visible ) {
        		forceRepaint();
        	}
        	else {
        	    repaint();
        	}
			ComponentEvent component = new ComponentEvent(null,
			    b ? ComponentEvent.COMPONENT_SHOWN : ComponentEvent.COMPONENT_HIDDEN);
			component.setSource(this);
            processEvent(component);
    	}
	}

    /**
     * If transparent, mouse clicks in uncovered areas will still be caught.
     * Otherwise, they are passed to the objects below them.
     * @param consumingTransparentClicks - TBD
     */
	public final void setConsumingTransparentClicks( boolean consumingTransparentClicks ) {
	    this.consumingTransparentClicks = consumingTransparentClicks;
	}

	/**
	 * isConsumingTransparentClicks
	 * @return boolean
	 */
	public final boolean isConsumingTransparentClicks() {
	    return consumingTransparentClicks;
	}

    /**
     * By default, LinesGadget ignores the mouse.
     * @param ignoringMouse - TBD
     */
	public final void setIgnoringMouse( boolean ignoringMouse ) {
	    this.ignoringMouse = ignoringMouse;
	}

	/**
	 * isIgnoringMouse
	 * @return boolean
	 */
    public final boolean isIgnoringMouse() {
	    return ignoringMouse;
	}

	/**
	 * setClippingRequired
     * @param clippingRequired - TBD
     */
	public final void setClippingRequired( boolean clippingRequired ) {
	    this.clippingRequired = clippingRequired;
	}

	/**
     * isClippingRequired
     * @return boolean
     */
	public final boolean isClippingRequired() {
	    return clippingRequired;
	}

	/**
	 * Gets the normal Foreground color.
	 * @see #setNormalForeground
	 * @return Color
	 */
	public final Color getNormalForeground() {
	    return getNormalForeground(null,true);
	}

	/**
     * getNormalForeground
     * @param g - TBD
     * @return Color
     */
	protected final Color getNormalForeground(
	        GadgetGraphics g ) {
	    return getNormalForeground(g,true);
	}

	/**
	 * calcForeground
	 * @param background - TBD
	 * @return Color.
	 */
	public Color calcForeground(Color background) {
        int blue=background.getBlue();
        int red=background.getRed();
        int green=background.getGreen();
        if ( ( blue < 100 && ( red + green ) > 200 ) ||
             ( red + green > 300 ) ||
             ( blue < 100 && green > 160 ) ) {
            return Color.black;
        }
        else {
            return Color.white;
        }
	}

	/**
	 * getNormalForeground
	 * @param g - TBD
	 * @param processDefault - TBD
	 * @return Color
	 */
	protected final Color getNormalForeground(
	        GadgetGraphics g,
	        boolean processDefault) {
		Color cur = normalForeground;
		if ( cur == null ) {
		    if ( ( parent != null && g != null ) &&
		         ( g.ancestorNormalForeground != null ) &&
	             ( normalBackground == parent.normalBackground ) ) {
		        cur = g.ancestorNormalForeground;
		    }
		    else if ( parent == null ) {
	            cur = defaultColor;
		    }
		    else {
		        cur = parent.getNormalForeground(null,false);
		    }
		}
	    if ( cur == defaultColor && processDefault ) {
	        Color bg = getFinalNormalBackground(g);

	        if ( bg == null ) {
		        return Color.black;
	        }
	        else {
	            return calcForeground(bg);
	        }
	    }
	    return cur;
	}

	/**
	 * Gets the selectedBackground color.
	 * @see #setSelectedBackground
	 * @return	Color
	 */
	public final Color getSelectedBackground() {
	    return getSelectedBackground(null);
	}

	/**
	 * getSelectedBackground
	 * @param g - TBD
	 * @return Color
	 */
	protected final Color getSelectedBackground(GadgetGraphics g) {
		Color cur = selectedBackground;
		if ( cur == null ) {
		    if ( ( parent != null && g != null ) &&
		         g.ancestorSelectedBackground != null ) {
		        cur = g.ancestorSelectedBackground;
		    }
		    else if ( parent == null ) {
	            cur = defaultSelectedBackground;
		    }
		    else {
		        cur = parent.getSelectedBackground();
		    }
		}
	    return cur;
	}

	/**
	 * Gets the selectedForeground color.
	 * @see #setSelectedForeground
	 * @return	Color
	 */
	public final Color getSelectedForeground() {
	    return getSelectedForeground(null,true);
	}

	/**
	 * getSelectedForeground
	 * @param g - TBD
	 * @return Color
	 */
	protected final Color getSelectedForeground(GadgetGraphics g ) {
	    return getSelectedForeground(g,true);
	}

	/**
	 * getSelectedForeground
	 * @param g - TBD
	 * @return Color
	 */
	protected final Color getSelectedForeground(
	        GadgetGraphics g,
	        boolean processDefault ) {
		Color cur = selectedForeground;
		if ( cur == null ) {
		    if ( ( parent != null && g != null ) &&
		         (g.ancestorSelectedForeground != null) &&
		         ( selectedBackground == parent.selectedBackground)) {
		        cur = g.ancestorSelectedForeground;
		    }
		    else if ( parent == null ) {
	            cur = defaultColor;
		    }
		    else {
		        cur = parent.getSelectedForeground(null,false);
		    }
		}
	    if ( cur == defaultColor && processDefault) {
	        Color bg = getFinalSelectedBackground(g);

	        if ( bg == null ) {
	            return Color.white;
	        }
	        else {
                float hsbvals[] = new float[3];

	            Color.RGBtoHSB( bg.getRed(), bg.getGreen(), bg.getBlue(), hsbvals );

	            if ( hsbvals[2] /* brightness */ > 0.7 ) {
	                return Color.black;
	            }
	            else {
	                return Color.white;
	            }
	        }
	    }
	    return cur;
	}

	/**
	 * Gets the foreground color.
	 * @see #setForeground
	 * @return	Color
	 */
	public final Color getForeground() {
	    return getForeground(null);
	}

	/**
	 * getForeground
	 * @param g - TBD
	 * @return Color
	 */
	protected final Color getForeground(
	        GadgetGraphics g ) {
	    if ( isShowingSelected() ) {
	        return getSelectedForeground(g,true);
	    }
	    else {
	        return getNormalForeground(g,true);
	    }
	}

/**
 * Gets the background color.
 * @see setbackground
 * @return Color
 */

	public final Color getBackground() {
	    return getBackground(null);
	}

	/**
	 * getBackground
	 * @param g - TBD
	 * @return Color
	 */
	protected final Color getBackground( GadgetGraphics g ) {
	    if ( isShowingSelected() ) {
	        return getSelectedBackground(g);
	    }
	    else {
	        return getNormalBackground(g);
	    }
	}

	/**
	 * Sets the Foreground color.
	 * @param c		the Color
	 * @see #getNormalForeground
	 * @see #setNormalForeground
	 */
	public void setForeground(Color c) {
	    setNormalForeground(c);
	}

	/**
	 * Sets the normalForeground color.
	 * @param c		the Color
	 * @see #getNormalForeground
	 */
	public void setNormalForeground(Color c) {
		Color cur = normalForeground;
		if ( ( c != cur ) &&
		     ( ( c == null ) ||
		       ( c == transparent ) || ( cur == transparent ) ||
		       ( c == defaultColor ) || ( cur == defaultColor ) ||
		       ( ! c.equals( cur ) ) ) ) {
    	    normalForeground = c;
    		repaint();
        }
	}

	/**
	 * Sets the selectedForeground color.
	 * @param c 	the Color
	 * @see #getSelectedForeground
	 */
	public void setSelectedForeground(Color c) {
		Color cur = selectedForeground;
		if ( ( c != cur ) &&
		     ( ( c == null ) ||
		       ( c == transparent ) || ( cur == transparent ) ||
		       ( c == defaultColor ) || ( cur == defaultColor ) ||
		       ( ! c.equals( cur ) ) ) ) {
    	    selectedForeground = c;
    		repaint();
        }
	}

/**
 * Gets the background color.
 * @see setBackground
 * @return Color
 */

	public final Color getNormalBackground() {
	    return getNormalBackground(null);
	}

	/**
	 * getNormalBackground
	 * @param g - TBD
	 * @return Color
	 */
	protected Color getNormalBackground(GadgetGraphics g) {
	    Color cur = normalBackground;
		if ( cur == null ) {
		    if ( ( parent != null && g != null ) &&
		         g.ancestorNormalBackground != null ) {
		        cur = g.ancestorNormalBackground;
		    }
		    else if ( parent == null ) {
		        if ( shell != null ) {
		            cur = shell.getBackground();
		        }
	            if ( cur == null ) {
	                cur = defaultBackground;
	            }
		    }
		    else {
		        cur = parent.getNormalBackground();
		    }
		}
		return cur;
	}

/**
 * If transparent (background is null), gets its ancestor's final background.
 * @see setBackground
 * @return Color
 */

	public final Color getFinalNormalBackground() {
	    return getFinalNormalBackground(null);
	}

	/**
	 * getFinalNormalBackground
	 * @param g - TBD
	 * @return Color
	 */
	protected Color getFinalNormalBackground(GadgetGraphics g) {
	    Color cur = getNormalBackground(g);
	    if ( cur != Gadget.transparent ) {
	        return cur;
	    }
        if ( parent != null ) {
            return parent.getFinalNormalBackground();
        }
	    if ( shell != null ) {
	        return shell.getBackground();
	    }
	    return defaultBackground;
	}

	/**
	 * If selected background is null, gets its ancestor's final selected background.
	 * @return	selected background or parent's selected background
	 */
	public final Color getFinalSelectedBackground() {
	    return getFinalSelectedBackground(null);
	}

	/**
	 * getFinalSelectedBackground
	 * @param g - TBD
	 * @return Color
	 */
	protected final Color getFinalSelectedBackground(GadgetGraphics g) {
	    Color cur = getSelectedBackground(g);
	    if ( cur != Gadget.transparent ) {
	        return cur;
	    }
        if ( parent != null ) {
            if (parent.isSelected()) {
                return parent.getFinalSelectedBackground();
            } else {
                return parent.getFinalNormalBackground();
            }
        }
	    return defaultSelectedBackground;
	}

	/**
	 * getFinalBackground
	 * @return Color
	 */
	public final Color getFinalBackground() {
	    return getFinalBackground(null);
	}

	/**
	 * getFinalBackground
	 * @param g - TBD
	 * @return Color
	 */
	protected Color getFinalBackground(GadgetGraphics g) {
	    Color background = getBackground(g);
	    if ( background != Gadget.transparent ) {
	        return background;
	    }
        if ( parent != null ) {
            return parent.getFinalBackground();
        }
	    if ( isShowingSelected() ) {
	        return defaultSelectedBackground;
	    }
	    else {
	        return defaultBackground;
	    }
	}

/**
 * Sets the background color.
 * @param     c - the Color
 * @see getBackground
 */

	public void setNormalBackground(Color c) {
		Color cur = normalBackground;
		if ( ( c != cur ) &&
		     ( ( c == null ) ||
		       ( c == transparent ) || ( cur == transparent ) ||
		       ( c == defaultColor ) || ( cur == defaultColor ) ||
		       ( ! c.equals( cur ) ) ) ) {
    	    if ( ( ( normalBackground == transparent ) ||
    	           ( c == transparent ) ) &&
    	         parent != null &&
    	         parent.getLayout() instanceof GadgetCardLayout ) {
    	        parent.invalidate();
    	    }
    	    normalBackground = c;
    		repaint();
        }
	}

	/**
	 * setBackground
	 * @param c - TBD
	 */
	public final void setBackground(Color c) {
	    setNormalBackground(c);
	}

/**
 * Sets the background color.
 * @param  c - the Color
 * @see getBackground
 */

	/**
	 * setSelectedBackground
	 * @param c - TBD
	 */
	public void setSelectedBackground(Color c) {
		Color cur = selectedBackground;
		if ( ( c != cur ) &&
		     ( ( c == null ) ||
		       ( c == transparent ) || ( cur == transparent ) ||
		       ( c == defaultColor ) || ( cur == defaultColor ) ||
		       ( ! c.equals( cur ) ) ) ) {
    	    selectedBackground = c;
    		repaint();
        }
	}

	/**
	 * Gets the font of the component.
	 * @see #setFont
	 * @return Font
	 */
	public final Font getFont() {
	    return getFont(null);
	}

	/**
	 * returns the exact font setting, without inheriting from the parent
	 * @return Font
	 */
	public final Font getFontSetting() {
	    return font;
	}

	/**
	 * returns the exact normalBackground setting, without inheriting from the parent
	 * @return Color
	 */
	public final Color getNormalBackgroundSetting() {
	    return normalBackground;
	}

	/**
	 * returns the exact selectedBackground setting, without inheriting from the parent
	 * @return Color
	 */
	public final Color getSelectedBackgroundSetting() {
	    return selectedBackground;
	}

	/**
	 * returns the exact normalForeground setting, without inheriting from the parent
	 * @return Color
	 */
	public final Color getNormalForegroundSetting() {
	    return normalForeground;
	}

	/**
	 * returns the exact selectedForeground setting, without inheriting from the parent
	 * @return Color
	 */
	public final Color getSelectedForegroundSetting() {
	    return selectedForeground;
	}

	/**
	 * getFont
	 * @param g - TBD
	 * @return Font
	 */
	 public Font getFont(GadgetGraphics g) {
		Font cur = font;
		if ( cur == null ) {
		    if ( ( parent != null && g != null ) &&
		         g.ancestorFont != null ) {
		        cur = g.ancestorFont;
		    }
		    else if ( parent != null ) {
		        cur = parent.getFont();
		    }
		    else if ( shell != null ) {
	            cur = shell.getFont();
		    }
		}
        if ( cur == null ) {
            cur = defaultFont;
        }
        lastFont = cur;
		return cur;
	}

/**
 * Sets the font of the component.
 * @param  f - the font
 * @see getFont
 */

	public void setFont(Font f) {
	    synchronized(getTreeLock()) {
    		Font cur = font;
    		if ( ( f != cur ) &&
    		     ( ( f == null ) ||
    		       ( ! f.equals( cur ) ) ) ) {
        	    font = f;
        	    invalidate();
        	    fontChanged();
        		repaint();
            }
        }
	}

	/**
	 * fontChanged
	 */
	public void fontChanged() {
	    invalidate(false);
	}

/**
 * Gets the locale of the component.
 * If the component does not have a locale, the
 * locale of its parent is returned.
 *     Throws: IllegalComponentStateException
 *          If the Component does not have its own locale and has not
 * 			yet been added to a containment hierarchy such that the locale can be
 *			determined from the containing parent.
 * @see setLocale
 */

//	public Locale getLocale() {
//		return false;
//	}

/**
 * Sets the locale of the component.
 * @param  l - the locale
 * @see getLocale
 */

//	public void setLocale(Locale l) {
//		return false;
//	}

/**
 * Gets the ColorModel used to display the component on the output device.
 * @see ColorModel
 * @return ColorModel
 */

//	public ColorModel getColorModel() {
//		return false;
//	}

/**
 * Returns the current location of this component. The location will be in the parent's coordinate space.
 * @see setLocation
 * @return Point
 */

	public final Point getLocation() {
		return new Point( x, y );
	}

/**
 * Returns the current location of this component.
 * The location will be in the parent's coordinate space.
 * @param rv if not null, this is used so a new Point does not have to be
 * allocated.
 * @see setLocation
 * @return Point
 */

	public final Point getLocation(Point rv) {
	    if (rv == null) rv = new Point(x,y);
	    rv.move(x,y);
		return rv;
	}

/**
 * Moves the Component to a new location. The x and y coordinates are in the parent's coordinate space.
 * @param  x - the x coordinate
 * @param  y - the y coordinate
 * @see getLocation, setBounds
 */

	public final void setLocation(int x, int y) {
		setBounds(x, y, width, height);
	}

/**
 * Moves the Component to a new location. The point p is given in the parent's coordinate space.
 * @param  p - the new location for the coordinate
 * @see getLocation, setBounds
 */

	public final void setLocation(Point p) {
		setBounds(p.x, p.y, width, height);
	}

/**
 * Returns the current size of this component.
 * @see setSize
 * @return Dimension
 */

	public final Dimension getSize() {
		return new Dimension( width, height );
	}

/**
 * Returns the current size of this component.
 * @param rv if not null, this is used so a new Dimension does not have to be
 * allocated.
 * @see setSize
 * @return Dimension
 */

	public final Dimension getSize(Dimension rv) {
	    if (rv == null) rv = new Dimension();
	    rv.width = width;
	    rv.height = height;
		return rv;
	}

/**
 * Resizes the Component to the specified width and height.
 * @param  width - the width of the component
 * @param  height - the height of the component
 * @see size, setBounds
 */

	public final void setSize(int width, int height) {
		setSize(width, height, true);
	}

	/**
	 * setSize
	 * @param width - TBD
	 * @param height - TBD
	 * @param invalidateParent - TBD
	 */
	public final void setSize(int width, int height, boolean invalidateParent) {
		setBounds(x,y,width,height, invalidateParent);
	}

/**
 * Resizes the Component to the specified dimension.
 * @param  d - the component dimension
 * @see size, setBounds
 */

	public final void setSize(Dimension d) {
		setSize(d, true);
	}

	/**
	 * setSize
	 * @param d - TBD
	 * @param invalidateParent - TBD
	 */
	public final void setSize(Dimension d, boolean invalidateParent) {
		setBounds(x,y,d.width,d.height, invalidateParent);
	}

/**
 * Returns the current bounds of this component.
 * @see setBounds
 * @return Rectangle
 */

	public final Rectangle getBounds() {
		return new Rectangle( x, y, width, height );
	}

/**
 * Returns the current bounds of this component.
 * @param rv if not null, this is used so a new Rectangle does not have to be
 * allocated.
 * @see setBounds
 * @return Rectangle
 */

	public final Rectangle getBounds(Rectangle rv) {
	    if (rv == null) rv = new Rectangle();
	    //rv.reshape(x, y, width, height); //1.0.2
      rv.setBounds(x, y, width, height); //1.1 and up
		return rv;
	}

/**
 * Reshapes the Component to the specified bounding box.
 * @param  x - the x coordinate
 * @param  y - the y coordinate
 * @param  width - the width of the component
 * @param  height - the height of the component
 * @see getBounds, setLocation, setSize
 */

	public final void setBounds(int x, int y, int width, int height) {
	    setBounds( x, y, width, height, true );
	}

	/**
     * setBounds reshapes the Component to the specified bounding box.
 	 * @param  x - the x coordinate
 	 * @param  y - the y coordinate
	 * @param  width - the width of the component
 	 * @param  height - the height of the component
     * @param invalidateParent - TBD
     */
	public void setBounds(int x, int y, int width, int height, boolean invalidateParent) {
	    synchronized(getTreeLock()) {
    	    boolean resized = (this.width != width) ||
    	                      (this.height != height);
    	    boolean moved = (this.x != x) ||
    	                    (this.y != y);
    	    if ( resized || moved ) {
    	        if (visible) {
    	            forceRepaint();
    	        }
        		this.x = x;
        		this.y = y;
        		this.width = Math.max(0,width);
        		this.height = Math.max(0,height);
        		if ( resized ) {
        		    invalidate( invalidateParent );
        		}
    	        repaint();
    	        if (moved) {
        			ComponentEvent component = new ComponentEvent(null, ComponentEvent.COMPONENT_MOVED);
        			component.setSource(this);
                    processEvent(component);
                }
    	        if (resized) {
        			ComponentEvent component = new ComponentEvent(null, ComponentEvent.COMPONENT_RESIZED);
        			component.setSource(this);
                    processEvent(component);
                }
            }
        }
	}

/**
 * Reshapes the Component to the specified bounding box.
 * @param  r - the new bounding rectangle for the component
 * @see getBounds, setLocation, setSize
 */

	public final void setBounds(Rectangle r) {
		setBounds( r.x, r.y, r.width, r.height, true );
	}

	/**
	 * setBounds reshapes the Component to the specified bounding box.
 	 * @param  r - the new bounding rectangle for the component
	 * @param invalidateParent - TBD
	 */
	public final void setBounds(Rectangle r, boolean invalidateParent) {
		setBounds( r.x, r.y, r.width, r.height, invalidateParent );
	}

/**
 * Returns the preferred size of this component.
 * @see getMinimumSize, LayoutManager
 * @return Dimension
 */

	public Dimension getPreferredSize() {
	    int pw = pref_width;
	    int ph = pref_height;
	    if ( ( pw >= 0 ) && ( ph >= 0 ) ) {
	        return new Dimension( pw, ph );
	    }
	    synchronized(getTreeLock()) {
    		return getMinimumSize();
    	}
	}

/**
 * Returns the preferred size of this component.
 * @param rv if not null, this is used so a new Dimension does not
 * ALWAYS have to be allocated.  GWT has not been fully retrofitted to
 * take advantage of this new signature, so this MAY allocate a new
 * dimension anyway.
 * @see getMinimumSize, LayoutManager
 * @return Dimension
 */

	public Dimension getPreferredSize(Dimension rv) {
	    if (rv == null) rv = new Dimension();
	    int pw = pref_width;
	    int ph = pref_height;
	    if ( ( pw >= 0 ) && ( ph >= 0 ) ) {
	        rv.width = pw;
	        rv.height = ph;
	    } else {
	        Dimension pref = new Dimension();
	        rv.width = pref.width;
	        rv.height = pref.height;
	    }
	    return rv;
	}

    /**
     * requiresVertScrollbar
     * @return boolean
     */
    protected boolean requiresVertScrollbar() {
        return false;
    }

    /**
     * requiresHorizScrollbar
     * @return boolean
     */
    protected boolean requiresHorizScrollbar() {
        return false;
    }

    /**
     * Returns the mininimum size of this component.
     * @see getPreferredSize, LayoutManager
     *
     * @return Dimension
	 */
	public Dimension getMinimumSize() {
		// return getSize(); // this is how AWT does it, but I don't
		return new Dimension(0,0);
	}

    /**
     * Returns the mininimum size of this component.
     * @param rv if not null, this is used so a new Dimension does not
     * ALWAYS have to be allocated.  GWT has not been fully retrofitted to
     * take advantage of this new signature, so this will allocate a new
     * dimension anyway.  Later revisions of GWT may take advantage of this,
     * but for now, it is just added for consistency with getPreferredSize,
     * getSize, getLocation, and getBounds.
     * @see getPreferredSize, LayoutManager
     * @return Dimension
	 */
	public Dimension getMinimumSize(Dimension rv) {
	    if (rv == null) {
	        rv = getMinimumSize();
	    } else {
	        Dimension min = getMinimumSize();
	        rv.width = min.width;
	        rv.height = min.height;
	    }
	    return rv;
	}

    /**
     * Lays out the component.
     * This is usually called when the component
     * (more specifically, container) is validated.
     * @see validate, LayoutManager
     */
	public void doLayout() {
	}

	/**
     * isFrozen
     * @return boolean
     */
	public boolean isFrozen() {
	    return ( frozen > 0 );
	}

	/**
     * freeze
     */
	public void freeze() {
	    frozen++;
	}

	/**
	 * thaw
	 */
	public void thaw() {
	    if ( frozen > 0 ) {
	        frozen--;
	        if ( frozen == 0 ) {
    	        if ( ! valid ) {
    	            valid = true;
    	            invalidate();
    	            repaint();
    	        }
    	    }
	    }
	}

/**
 * Ensures that a component has a valid layout.
 * This method is primarily intended to operate on Container instances.
 * @see invalidate, layout, LayoutManager, validate
 */
	public void validate() {
		if ( ! valid ) {
		    if ( frozen == 0 ) {
    			valid = true;
    			if ( visible && width > 0 && height > 0 ) {
        			doLayout();
        		}
    			if ( valid ) {
    			    Dimension pref = getPreferredSize();
    			    pref_width = pref.width;
    			    pref_height = pref.height;
    			}
    		}
		}
	}

/**
 * Invalidates the component. The component and all parents above it are
 * marked as needing to be laid out. This method can be called often, so
 * it needs to
 * execute quickly.
 * @see validate, layout, LayoutManager
 */
	public final void invalidate() {
        invalidate( true );
	}

	/**
	 * invalidate
	 * @param invalidateParent - TBD
	 */
	public void invalidate( boolean invalidateParent ) {
        pref_width = -1;
        pref_height = -1;
	    if ( valid ) {
	        valid = false;
            if ( frozen > 0 ) {
                return;
            }
            ContainerGadget parent = this.parent;
//	        synchronized(getTreeLock()) { // removed to wait for image in ImageGadget
        		if ( parent != null ) {
                    if ( invalidateParent ) {
            		    if ( parent.valid ) {
                			parent.invalidate( true );
                		}
                	}
                	else {
            	        parent.notifyInvalid();
                	}
                }
        	    else {
        	        GadgetShell shell = this.shell;
            	    if ( shell != null ) {
            	        shell.notifyInvalid();
            	    }
            	}
        	}
//    	}
	}

/**
 * Gets the x and y offsets to add to the drawing coordinates for this
 * component.
 * @see paint
 * @return Point
 */
	public Point getOffset() {
		Point p;
		if ( parent == null ) {
		    p = new Point( 0, 0 );
		}
		else {
		    p = parent.getOffset();
		    p.translate( x, y );
		}
		return p;
	}

/**
 * Returns the offset used to calculate the next focus gadget, usually
 * the value of getOffset(), but might be an ancestors offset in some
 * cases (like combo box).
 * @see getOffset
 * @return Point
 */
    public Point getFocusOffset() {
        return getOffset();
    }

/**
 * Gets a GadgetGraphics context for this component.
 * This method will return null if the component is currently not on the screen.
 * @see paint
 * @return GadgetGraphics
 */
	public final GadgetGraphics getGadgetGraphics() {
	    return getGadgetGraphics(0,0,width,height,false);
	}

/**
 * Gets a GadgetGraphics context for this component.
 * This method will return null if the component is currently not on the screen.
 * @param paintingAll if true, we may be painting the entire tree with this one
 *   GadgetGraphics object.  The default is false
 * @see paint
 * @return GadgetGraphics
 */
	public final GadgetGraphics getGadgetGraphics(int x, int y, int width, int height) {
	    return getGadgetGraphics(x,y,width,height,false);
	}

/**
 * Gets a GadgetGraphics context for this component.
 * This method will return null if the component is currently not on the screen.
 * @param paintingAll if true, we may be painting the entire tree with this one
 *   GadgetGraphics object.  The default is false
 * @see paint
 * @return GadgetGraphics
 */
	public final GadgetGraphics getGadgetGraphics(int x, int y, int width, int height, boolean paintingAll) {
	    synchronized (getTreeLock()) {
	        Rectangle clip = new Rectangle(x,y,width,height);
    		GadgetGraphics permg = getTranslatedGraphics(clip);
    		if (permg == null) {
    		    return null;
    		}
    		permg.setPaintingAll(paintingAll);
    		permg.clipRect(clip.x,clip.y,clip.width,clip.height);
    	    return permg;
    	}
	}

/**
 * Returns the graphics to use.
 * @return GadgetGraphics
 */
	protected GadgetGraphics getTranslatedGraphics(Rectangle clip) {
		GadgetGraphics g;
		if ( parent == null ) {
		    g = shell.getPermGraphics();
		}
		else {
		    clip.x += x;
		    clip.y += y;
		    if (clip.x < 0) {
		        clip.width -= clip.x;
		        clip.x = 0;
		    }
		    if (clip.y < 0) {
		        clip.height -= clip.y;
		        clip.y = 0;
		    }
		    int right = clip.x+clip.width;
		    if (right > parent.width) {
		        clip.width -= right-parent.width;
		    }
		    int bottom = clip.y+clip.height;
		    if (bottom > parent.height) {
		        clip.height -= bottom-parent.height;
		    }
		    g = parent.getTranslatedGraphics(clip);
		    clip.x -= x;
		    clip.y -= y;
		    if (g != null) {
    		    g.translate( x, y );
    		}
		}
		if (g != null) {
    		g.setGadget(this);
    	}
		return g;
	}

/**
 * Gets a GadgetGraphics context for this component for doing XOR draws or for
 * drawing very temporary objects (maybe menus, tips, etc.).  May return null.
 * @see paint
 * @return GadgetGraphics
 */
	public final GadgetGraphics getTempGraphics() {
	    synchronized (getTreeLock()) {
    		GadgetGraphics tempg = shell.getTempGraphics();
    		if (tempg == null) {
    		    return null;
    		}
    		Point offset = getOffset();
    		tempg.translate(offset.x,offset.y);
    		tempg.clipRect(0,0,width,height);
    	    return tempg;
    	}
	}

/**
 * Gets the font metrics for this component.
 * @param font - the font
 * @see getFont
 * @return FontMetrics
 */

	public final FontMetrics getFontMetrics(Font font) {
        if (font instanceof SpecialFont) {
            return ((SpecialFont)font).getMetrics();
        } else {
            GadgetShell shell = getShell();
            if (shell != null) {
                GadgetGraphics g = shell.getPermGraphics();
                if (g != null) {
            		return g.getFontMetrics( font );
            	}
            }
    		return Toolkit.getDefaultToolkit().getFontMetrics( font );
    	}
	}

/**
 * Set the cursor.
 * @param cursor - a Cursor object.
 * @see Cursor
 */

	public final void setCursor(GadgetCursor cursor) {
	    if (cursor != this.cursor) {
	        this.cursor = cursor;
	        GadgetShell shell = getShell();
	        if (shell != null) {
	            Gadget mouseOwner = shell.getMouseOwner();
	            if (mouseOwner != null &&
	                (mouseOwner == this ||
    	             (this instanceof ContainerGadget &&
    	              mouseOwner.isDescendentOf((ContainerGadget)this)))) {
    	            shell.notifyCursorChanged();
    	        }
	        }
	    }
	}

/**
 * Gets the cursor set on this component.
 * @return Cursor
 */

	public final GadgetCursor getCursor() {
	    if (cursor == null) {
	        ContainerGadget parent = getParent();
	        if (parent == null) {
	            return GadgetCursor.getDefaultCursor();
	        }
	        return parent.getCursor();
	    }
	    return cursor;
	}


/**
 * Paints the component.
 * @param g - the specified GadgetGraphics window
 */

	public void paint( GadgetGraphics g ) {
	}

	/**
     * clear.
     * @param g - the specified GadgetGraphics window
     */
	public void clear( GadgetGraphics g ) {
	    damaged = false;
        Color bg = getBackground(g);
	    if ( ( bg == Gadget.transparent ) &&
    	     ( parent == null ) ) {
	        bg = shell.getBackground();
	    }
	    if ( bg != Gadget.transparent ) {
    	    g.setColor(bg);
    	    g.fillRect(0,0,width,height);
	    }
	    g.setFont(getFont(g));
	    g.setColor(getForeground(g));
    }

	/**
	 * update
	 * @param g - TBD
	 */

	public void update( GadgetGraphics g ) {
	    clear( g );
        paint( g );
    }

/**
 * Repaints the component. This will result in a call to paint as soon as possible.
 * @see paint
 */
	public final void repaint() {
		repaint( 0, 0, width, height );
	}

/**
 * Repaints the component. This will result in a call to paint within tm milliseconds.
 * @param tm - maximum time in milliseconds before paint
 * @see paint
 */

//	public void repaint(long tm) {
//		return false;
//	}


	public void forceRepaint() {
	    repaint(0,0,width,height,true,true);
	}

    /**
     * Repaints part of the component. This will result in a call to paint as soon as possible.
     * Notifies the GadgetShell of a damaged area to repair.
     * @param x - the x coordinate
     * @param y - the y coordinate
     * @param width - the width
     * @param height - the height
     * @see repaint
     */
	public final void repaint(int x, int y, int width, int height) {
	    repaint(x,y,width,height,true,false);
	}

	/**
     * Repaints part of the component. This will result in a call to paint as soon as possible.
     * Notifies the GadgetShell of a damaged area to repair.
     * @param x - the x coordinate
     * @param y - the y coordinate
     * @param width - the width
     * @param height - the height
     * @param setPaintFlag - TBD
     * @param forced - TBD
     * @see repaint
     */
	public void repaint(int x, int y, int width, int height, boolean setPaintFlag,boolean forced) {
	    //synchronized(getTreeLock()) { removed so I can wait for image in ImageGadget
	        GadgetShell shell = this.shell;
	        Rectangle bounds = getBounds();
    	    if ( (shell == null) || (! forced && ! visible) ) {
    	        return;
    	    }
            if ( x < 0 ) {
                width += x;
                x = 0;
            }
            if ( x + width > bounds.width ) {
                width = bounds.width-x;
            }
            if ( width <= 0 ) {
                return;
            }
            if ( y < 0 ) {
                height += y;
                y = 0;
            }
            if ( y + height > bounds.height ) {
                height = bounds.height-y;
            }
            if ( height <= 0 ) {
                return;
            }
            damaged = true;
            ContainerGadget parent = this.parent;
            // this if block used to be after the next repaint below, and was if !forced and !setPaintFlag
			if ( parent != null && ! parent.paintAnyOrder ) {
				parent.paintAllChildrenAfter(this);
			}
            if ( parent != null && ( forced || ! parent.paintAnyOrder ) ) {
				if ( setPaintFlag || forced ) {
					parent.paintAllChildren = true;
					if ( visible && ! isTransparent() ) {
						setPaintFlag = false;
					}
				}
				parent.repaint( bounds.x + x, bounds.y + y, width, height,
								setPaintFlag, /*false*/forced );
            }
            else {
				if ( parent == null ) {
    				shell.addDamagedArea( x	+ bounds.x, y + bounds.y, width, height );
				}
				else {
                    parent.notifySomeDamage();
	                int cnt = parent.gadgetCount;
	                if (this == parent.gadgets[cnt-1] ) {
					    parent.addPaintIdx(cnt-1);
	                } else if (this == parent.gadgets[cnt-2] ) {
					    parent.addPaintIdx(cnt-2);
	                } else {
					    parent.addPaintIdx(parent.getGadgetIndex(this));
					}

					Point offset = getOffset();
    				shell.addDamagedArea( x + offset.x, y + offset.y, width, height );
				}
    	    }
    	//}
	}

/**
 * Repaints part of the component. This will result in a call to paint width tm millseconds.
 * @param tm - maximum time in milliseconds before paint
 * @param x - the x coordinate
 * @param y - the y coordinate
 * @param width - the width
 * @param height - the height
 * @see repaint
*/

//	public void repaint(long tm, int x, int y, int width, int height) {
//		return false;
//	}

/**
 * Prints this component. The default implementation of this method calls paint.
 * @param g - the specified GadgetGraphics window
 * @see paint
 */

//	public void print(GadgetGraphics g) {
//		return false;
//	}

/**
 * Prints the component and its subcomponents.
 * @param g - the specified GadgetGraphics window
 * @see print
 */

//	public void printAll(GadgetGraphics g) {
//		return false;
//	}

/**
 * imageUpdate
 * @param img - TBD
 * @param flags - TBD
 * @param x - TBD
 * @param y - TBD
 * @param w - TBD
 * @param h - TBD
 * @return boolean
 */

	public boolean imageUpdate(Image img, int flags, int x, int y, int w, int h) {

		int rate = -1;

		if ( ( flags & (HEIGHT|WIDTH|FRAMEBITS|ALLBITS) ) != 0) {
			rate = 0;
		}
//		else if ( ( flags & SOMEBITS ) != 0 ) {
//			String isInc = System.getProperty("awt.image.incrementaldraw");
//			if (isInc == null || isInc.equals("true")) {
//				String incRate = System.getProperty("awt.image.redrawrate");
//				try {
//					rate = (incRate != null) ? Integer.parseInt(incRate) : 100;
//					if (rate < 0)
//					rate = 0;
//				} catch (Exception e) {
//					rate = 100;
//				}
//			}
//		}

		if (rate >= 0) {
		    new RepaintThread(this).start();
		    //synchronized(getTreeLock()) { // removed so I can have wait for image
		                                    // in ImageGadget paint method...hope
		                                    // this doesn't hurt

    		 //   invalidate();
        	//	repaint();
        	//}
		}
		return ( ( flags & (ALLBITS|ABORT) ) == 0 );
	}

/**
 * Creates an image from the specified image producer.
 * @param producer - the image producer
 * @return Image
 */
	public final Image createImage(ImageProducer producer) {
		return (shell != null)
			? shell.createImage(producer)
			: Toolkit.getDefaultToolkit().createImage(producer);
	}

/**
 * Creates an off-screen drawable Image to be used for double buffering.
 * @param width 		the specified width
 * @param height 		the specified height
 * @return Image
 */
	public final Image createImage(int width, int height) {
		return (shell != null)
			? shell.createImage( width, height )
			: null;
	}

/**
 * Prepares an image for rendering on this Component. The image
 * data is downloaded asynchronously in another thread and the
 * appropriate screen representation of the image is generated.
 * @param image		the Image to prepare a screen representation for
 * @param observer	the ImageObserver object to be notified as the image is being prepared
 * @return  true if the image has already been fully prepared
 * @see ImageObserver
 */
	public final boolean prepareImage(Image image, ImageObserver observer) {
        return prepareImage(image, -1, -1, observer);
	}

/**
 * Prepares an image for rendering on this Component at the
 * specified width and height. The image data is downloaded
 * asynchronously in another thread and an appropriately
 * scaled screen representation of the image is generated.
 * @param image 		the Image to prepare a screen representation for
 * @param width 		the width of the desired screen representation
 * @param height 		the height of the desired screen representation
 * @param observer		the ImageObserver object to be notified as the image is being prepared
 * @return  true if the image has already been fully prepared
 * @see ImageObserver
 */
	public final boolean prepareImage(Image image, int width, int height, ImageObserver observer) {
		if (image instanceof Sprite) return true;
		return (shell != null)
			? shell.prepareImage(image, width, height, observer)
			: Toolkit.getDefaultToolkit().prepareImage(image, width, height, observer);
	}

/**
 * Returns the status of the construction of a screen
 * representation of the specified image. This method
 * does not cause the image to begin loading. Use the
 * prepareImage method to force the loading of an image.
 * @param image 		the Image to check the status of
 * @param observer 		the ImageObserver object to be notified as the image is being prepared
 * @return  the boolean OR of the ImageObserver flags for the data that is currently available
 * @see ImageObserver
 * @see #prepareImage
 */
	public final int checkImage(Image image, ImageObserver observer) {
        return checkImage(image, -1, -1, observer);
	}

/**
 * Returns the status of the construction of a scaled screen
 * representation of the specified image. This method does not
 * cause the image to begin loading, use the prepareImage method
 * to force the loading of an image.
 * @param image		the Image to check the status of
 * @param width		the width of the scaled version to check the status of
 * @param height	the height of the scaled version to check the status of
 * @param observer	the ImageObserver object to be notified as the image is being prepared
 * @return    the boolean OR of the ImageObserver flags for the data that is currently available
 * @see ImageObserver
 * @see #prepareImage
 */
	public final int checkImage(Image image, int width, int height, ImageObserver observer) {
		if (image instanceof Sprite) return ImageObserver.ALLBITS|
                                            ImageObserver.FRAMEBITS|
                                            ImageObserver.WIDTH|
                                            ImageObserver.HEIGHT|
                                            ImageObserver.PROPERTIES;
		return (shell != null)
			? shell.checkImage(image, width, height, observer)
			: Toolkit.getDefaultToolkit().checkImage(image, width, height, observer);
	}

	/**
	 * getImage
	 * @param url - TBD
	 * @return Image
	 */
	public final Image getImage( URL url ) {
        AppletContext context = AppletManager.getDefaultAppletContext();
        if ( context != null ) {
            return context.getImage( url );
        }
        else {
            return Toolkit.getDefaultToolkit().getImage( url );
        }
	}

	/**
	 * getImage
	 * @param filename - TBD
	 * @return Image
	 */
	public final Image getImage( String filename ) {
		return Toolkit.getDefaultToolkit().getImage( filename );
	}

/**
 * Checks whether this component "contains" the specified (x, y)
 * location, where x and y are defined to be relative to the
 * coordinate system of this component.
 * @param x			the x coordinate
 * @param y 		the y coordinate
 * @param exclude 	the gadget to ignore (if not null)
 * @see #getGadgetAt
 * @return boolean
 */
	public final boolean contains(int x, int y) {
	    return contains( x, y, null );
    }

/**
 * Checks whether this component "contains" the specified (x, y)
 * location, where x and y are defined to be relative to the
 * coordinate system of this component.
 * @param x 		the x coordinate
 * @param y         y - the y coordinate
 * @see #getGadgetAt
 * @return boolean
 */
	public boolean contains(int x, int y, Gadget exclude) {
        synchronized(getTreeLock()) {
    		return (x >= 0) && (x <= width) && (y >= 0) && (y <= height) &&
    		        (this != exclude) && visible;
    	}
	}

/**
 * Checks whether this component "contains" the specified point,
 * where x and y in the point are defined to be relative to the
 * coordinate system of this component.
 * @param p 		the point
 * @return			true if Point is contained in this component, else false.
 * @see #getGadgetAt
 */
	public final boolean contains(Point p) {
		return contains( p.x, p.y );
	}

/**
 * Returns the component or subcomponent that contains the x,y location.
 * @param x 		the x coordinate
 * @param y 		the y coordinate
 * @return 			this Gadget if it contains the point, else null
 * @see #contains
 */
	public Gadget getGadgetAt(int x, int y) {
		if ( contains(x,y) ) {
		    return this;
		}
		return null;
	}

/**
 * Returns the component or subcomponent that contains the specified point.
 * @param p 		the point
 * @return 			the Gadget that contains the point
 * @see #contains
 */
	public final Gadget getGadgetAt(Point p) {
		return getGadgetAt( p.x, p.y );
	}

/**
 * Delivers an event to this component or one of its sub components.
 * @param e 		the event
 * @see #handleEvent
 * @see #postEvent
 */
//	public void deliverEvent(Event e) {
//		return false;
//	}

/**
 * Posts an event to this component.
 * This will result in a call to handleEvent. If handleEvent
 * returns false the event is passed on to the parent of this component.
 * @param e 		the event
 * @see #handleEvent
 * @see #deliverEvent
 * @return boolean
 */
//	public boolean postEvent(Event e) {
//		return false;
//	}

    /**
     * Adds the specified component listener to receive component events
     * from this component.
     * @param l the component listener
     */
    public synchronized void addComponentListener(ComponentListener l) {
        componentListener = GWTEventMulticaster.add(componentListener, l);
    }
    /**
     * Removes the specified listener so it no longer receives component
     * events from this component.
     * @param l the component listener
     */
    public synchronized void removeComponentListener(ComponentListener l) {
        componentListener = GWTEventMulticaster.remove(componentListener, l);
    }

    /**
     * Adds the specified focus listener to receive focus events
     * from this component.
     * @param l the focus listener
     */
    public synchronized void addFocusListener(FocusListener l) {
        focusListener = GWTEventMulticaster.add(focusListener, l);
    }

    /**
     * Removes the specified focus listener so it no longer receives focus
     * events from this component.
     * @param l the focus listener
     */
    public synchronized void removeFocusListener(FocusListener l) {
        focusListener = GWTEventMulticaster.remove(focusListener, l);
    }

    /**
     * Adds the specified key listener to receive key events
     * from this component.
     * @param l the key listener
     */
    public synchronized void addKeyListener(KeyListener l) {
        keyListener = GWTEventMulticaster.add(keyListener, l);
    }

    /**
     * Removes the specified key listener so it no longer receives key
     * events from this component.
     * @param l the key listener
     */
    public synchronized void removeKeyListener(KeyListener l) {
        keyListener = GWTEventMulticaster.remove(keyListener, l);
    }

    /**
     * Adds the specified mouse listener to receive mouse events
     * from this component.
     * @param l the mouse listener
     */
    public synchronized void addMouseListener(MouseListener l) {
        mouseListener = GWTEventMulticaster.add(mouseListener,l);
    }

    /**
     * Removes the specified mouse listener so it no longer receives mouse
     * events from this component.
     * @param l the mouse listener
     */
    public synchronized void removeMouseListener(MouseListener l) {
        mouseListener = GWTEventMulticaster.remove(mouseListener, l);
    }

    /**
     * Adds the specified mouse motion listener to receive mouse motion events
     * from this component.
     * @param l the mouse motion listener
     */
    public synchronized void addMouseMotionListener(MouseMotionListener l) {
        mouseMotionListener = GWTEventMulticaster.add(mouseMotionListener,l);
    }

    /**
     * Removes the specified mouse motion listener so it no longer
     * receives mouse motion events from this component.
     * @param l the mouse motion listener
     */
    public synchronized void removeMouseMotionListener(MouseMotionListener l) {
        mouseMotionListener = GWTEventMulticaster.remove(mouseMotionListener, l);
    }

    /**
     * Processes events occurring on this component.  By default this
     * method will call the appropriate processXXXEvent method for the
     * class of event.
     * Classes overriding this method should call super.processEvent()
     * to ensure default event processing continues normally.
     * @see #processComponentEvent
     * @see #processFocusEvent
     * @see #processKeyEvent
     * @see #processMouseEvent
     * @see #processMouseMotionEvent
     * @param e the event
     */
    protected void processEvent(AWTEvent e) {

	    if ( ! isFunctioning() ) {
	        return;
	    }

        if (e instanceof FocusEvent) {
            processFocusEvent((FocusEvent)e);

        } else if (e instanceof MouseEvent) {
            if (!e.isConsumed()) {
                switch(e.getID()) {
                  case MouseEvent.MOUSE_PRESSED:
                  case MouseEvent.MOUSE_RELEASED:
                  case MouseEvent.MOUSE_CLICKED:
                  case MouseEvent.MOUSE_ENTERED:
                  case MouseEvent.MOUSE_EXITED:
                    processMouseEvent((MouseEvent)e);
                    break;
                  case MouseEvent.MOUSE_MOVED:
                  case MouseEvent.MOUSE_DRAGGED:
                    processMouseMotionEvent((MouseEvent)e);
                    break;
                }
            }

        } else if (e instanceof KeyEvent) {
            if (!e.isConsumed()) {
                processKeyEvent((KeyEvent)e);
            }

        } else if (e instanceof ComponentEvent) {
            processComponentEvent((ComponentEvent)e);
        }
    }

    /**
     * Processes component events occurring on this component by
     * dispatching them to any registered ComponentListener objects.
     * NOTE: This method will not be called unless component events
     * are enabled for this component; this happens when one of the
     * following occurs:
     * a) A ComponentListener object is registered via addComponentListner()
     * b) Component events are enabled via enableEvents()
     * Classes overriding this method should call super.processComponentEvent()
     * to ensure default event processing continues normally.
     * @param e the component event
     */
    protected void processComponentEvent(ComponentEvent e) {
        if (componentListener != null) {
            int id = e.getID();
            switch(id) {
              case ComponentEvent.COMPONENT_RESIZED:
                componentListener.componentResized(e);
                break;
              case ComponentEvent.COMPONENT_MOVED:
                componentListener.componentMoved(e);
                break;
              case ComponentEvent.COMPONENT_SHOWN:
                componentListener.componentShown(e);
                break;
              case ComponentEvent.COMPONENT_HIDDEN:
                componentListener.componentHidden(e);
                break;
            }
        }
    }

    /**
     * Processes focus events occurring on this component by
     * dispatching them to any registered FocusListener objects.
     * NOTE: This method will not be called unless focus events
     * are enabled for this component; this happens when one of the
     * following occurs:
     * a) A FocusListener object is registered via addFocusListener()
     * b) Focus events are enabled via enableEvents()
     * Classes overriding this method should call super.processFocusEvent()
     * to ensure default event processing continues normally.
     * @param e the focus event
     */
    protected void processFocusEvent(FocusEvent e) {
        if (focusListener != null) {
            int id = e.getID();
            switch(id) {
              case FocusEvent.FOCUS_GAINED:
                focusListener.focusGained(e);
                break;
              case FocusEvent.FOCUS_LOST:
                focusListener.focusLost(e);
                break;
            }
        }
    }

    /**
     * Processes key events occurring on this component by
     * dispatching them to any registered KeyListener objects.
     * NOTE: This method will not be called unless key events
     * are enabled for this component; this happens when one of the
     * following occurs:
     * a) A KeyListener object is registered via addKeyListener()
     * b) Key events are enabled via enableEvents()
     * Classes overriding this method should call super.processKeyEvent()
     * to ensure default event processing continues normally.
     * @param e the key event
     */
    protected void processKeyEvent(KeyEvent e) {
        if (keyListener != null) {
            int id = e.getID();
            switch(id) {
              case KeyEvent.KEY_TYPED:
                keyListener.keyTyped(e);
                break;
              case KeyEvent.KEY_PRESSED:
                keyListener.keyPressed(e);
                break;
              case KeyEvent.KEY_RELEASED:
                keyListener.keyReleased(e);
                break;
            }
        }
    }

    /**
     * Processes mouse events occurring on this component by
     * dispatching them to any registered MouseListener objects.
     * NOTE: This method will not be called unless mouse events
     * are enabled for this component; this happens when one of the
     * following occurs:
     * a) A MouseListener object is registered via addMouseListener()
     * b) Mouse events are enabled via enableEvents()
     * Classes overriding this method should call super.processMouseEvent()
     * to ensure default event processing continues normally.
     * @param e the mouse event
     */
    protected void processMouseEvent(MouseEvent e) {
        if (mouseListener != null) {
            int id = e.getID();
            switch(id) {
              case MouseEvent.MOUSE_PRESSED:
                mouseListener.mousePressed(e);
                break;
              case MouseEvent.MOUSE_RELEASED:
                mouseListener.mouseReleased(e);
                break;
              case MouseEvent.MOUSE_CLICKED:
                mouseListener.mouseClicked(e);
                break;
              case MouseEvent.MOUSE_EXITED:
                mouseListener.mouseExited(e);
                break;
              case MouseEvent.MOUSE_ENTERED:
                mouseListener.mouseEntered(e);
                break;
            }
        }
        if (e.getID() == MouseEvent.MOUSE_ENTERED) {
            String statusTip = this.statusTip;
            if (statusTip != null) {
                dtai.util.ShowUser.status(statusTip);
            }
        }
        if (e.getID() == MouseEvent.MOUSE_EXITED) {
            String statusTip = this.statusTip;
            if (statusTip != null) {
                dtai.util.ShowUser.status("");
            }
        }
    }

    /**
     * Processes mouse motion events occurring on this component by
     * dispatching them to any registered MouseMotionListener objects.
     * NOTE: This method will not be called unless mouse motion events
     * are enabled for this component; this happens when one of the
     * following occurs:
     * a) A MouseMotionListener object is registered via addMouseMotionListener()
     * b) Mouse Motion events are enabled via enableEvents()
     * Classes overriding this method should call super.processMouseMotionEvent()
     * to ensure default event processing continues normally.
     * @param e the mouse motion event
     */
    protected void processMouseMotionEvent(MouseEvent e) {
        if (mouseMotionListener != null) {
            int id = e.getID();
            switch(id) {
              case MouseEvent.MOUSE_MOVED:
                mouseMotionListener.mouseMoved(e);
                break;
              case MouseEvent.MOUSE_DRAGGED:
                mouseMotionListener.mouseDragged(e);
                break;
            }
        }
    }

	/**
	 * Requests the input focus. The gotFocus()
	 * Method will be called if this method is successful.  The component
	 * must be visible on the screen for this request to be granted.
	 */
	public void requestFocus() {
	    if ( shell != null ) {
	        shell.giveFocusTo( findNextFocusGadget() );
	    }
	}

	/**
	 * Transfers the focus to the next component.
	 */
	public final void transferFocus() {
		nextFocus();
	}

	/**
	 * nextFocus
	 */
	public final void nextFocus() {
	    GadgetShell shell = this.shell;
		if ( shell != null ) {
			shell.focusForward(false);
		}
	}

	/**
	 * prevFocus
	 */
	public final void prevFocus() {
	    GadgetShell shell = this.shell;
		if ( shell != null ) {
			shell.focusBackward(false);
		}
	}

	/**
	 * nexGroupFocus
	 * @return boolean result
	 */
	public final boolean nextGroupFocus() {
	    GadgetShell shell = this.shell;
		if ( shell != null ) {
			return shell.focusForward(true);
		}
        return false;
	}

	/**
	 * prevGroupFocus
	 * @return boolean result
	 */
	public final boolean prevGroupFocus() {
	    GadgetShell shell = this.shell;
		if ( shell != null ) {
			return shell.focusBackward(true);
		}
		return false;
	}

	/**
	 * hasFocus
	 * @return boolean result
	 */
	public boolean hasFocus() {
	    GadgetShell shell = this.shell;
		if ( shell == null ) {
		    return false;
		}
		else {
			if ( shell.getFocusOwner() == this ) {
			    return true;
			}
			Gadget parent = this.parent;
			if ( parent == null ) {
			    return false;
			}
			return parent.hasFocus();
		}
	}


	/**
	 * hasMouse
	 * @return boolean
	 */
	public final boolean hasMouse() {
		if ( shell == null ) {
		    return false;
		}
		else {
			return ( shell.getMouseOwner() == this );
		}
	}

	/**
	 * isDescendentOf
	 * @param container - TBD
	 * @return boolean
	 */
	public final boolean isDescendentOf( ContainerGadget container ) {
	    if ( parent == null ) {
	        return false;
	    }
	    if ( parent == container ) {
	        return true;
	    }
	    return parent.isDescendentOf(container);
	}

/**
 * Adds the specified popup menu to the component.
 * @param popup - the popup menu to be added to the component
 */

//	public synchronized void add(PopupMenu popup) {
//		return false;
//	}

/**
 * Removes the specified popup menu from the component.
 * @param popup - the popup menu to be removed
 */

//	public synchronized void remove(MenuComponent popup) {
//		return false;
//	}

/**
 * Returns the parameter String of this Component.
 * @return String
 */

//	protected String paramString() {
//		return false;
//	}

/**
 * Returns the String representation of this Component's values.
 *   Overrides:
 *   toString in class Object
 * @return String
 */

//	public String toString() {
//		return false;
//	}

/**
 * Prints a listing to a print stream.
 */

//	public void list() {
//		return false;
//	}

	/**
	 * Prints a listing to the specified print out stream.
	 * @param out		the Stream name
	 */
//	public void list(PrintStream out) {
//		return false;
//	}

	/**
	 * Prints out a list, starting at the specified indention, to the specified print stream.
	 * @param out 		the Stream name
	 * @param indent	the start of the list
	 */
//	public void list(PrintStream out, int indent) {
//		return false;
//	}

	/**
	 * Returns whether this component can be traversed using
	 * Tab or Shift-Tab keyboard focus traversal.
	 * @return	boolean
	 */
	public boolean isFocusTraversable() {
		return false;
	}

	/**
	 * notifyStateChange
	 */
	public void notifyStateChange() {
	}

}

class RepaintThread extends Thread {
    Gadget g;
    
    RepaintThread(Gadget g) {
        this.g = g;
    }
    
    public void run() {
	    g.invalidate();
		g.repaint();
	}
}