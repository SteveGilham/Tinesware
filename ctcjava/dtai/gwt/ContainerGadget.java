/****************************************************************
 **
 **  $Id: ContainerGadget.java,v 1.117 1998/03/04 20:53:38 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/ContainerGadget.java,v $
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.Vector;
import java11.awt.event.AWTEvent;
import java11.awt.event.MouseEvent;
import java11.util.EventObject;

/**
 * A generic GWT container object is a component
 * that can contain other GWT components.
 * <p>
 * Components added to a container are tracked in a list.  The order
 * of the list will define the components' front-to-back stacking order
 * within the container.  If no index is specified when adding a
 * component to a container, it will be added to the end of the list
 * (and hence to the bottom of the stacking order).
 * @version 	1.1
 * @author 	DTAI, Incorporated
 */
public class ContainerGadget extends Gadget {

    /**
     * The number of gadgets in this container.
     */
    int gadgetCount;

    /**
     * The gadgets in this container.
     */
    Gadget gadgets[] = new Gadget[4];

    /**
     * Layout manager for this container.
     */
    GadgetLayoutManager layoutMgr;

    private Gadget mouseDownIn;
    private Gadget lastMouseGadget;
	boolean childrenValid = false;

	boolean paintAllChildren = true;
	boolean someDamage = true;
	private boolean onlyDefaultButtons = true;

	private Gadget lastGroupedFocus;
	private boolean saveGraphicsForChildren = false;
	private boolean savePicture = false;
	boolean paintAnyOrder = false;

	int paintStartIdx = -1;
	int paintEndIdx = -1;

	private Image picture;
	private boolean painting = false; // limited use right now!!!

    /**
     * Constructs a new Container. Containers should not be subclassed or
     * instantiated directly.
     */
    protected ContainerGadget() {
    }

    /**
     * addPaintIdx
     *
     * @param idx	index of a new Gadget to be painted.
     */
    public void addPaintIdx(int idx) {
        synchronized(getTreeLock()) {
    	    paintEndIdx = Math.max(paintEndIdx,idx);
    	    if ( paintStartIdx < 0 ) {
    	        paintStartIdx = idx;
    	    }
    	    else {
    	        paintStartIdx = Math.min(paintStartIdx,idx);
    	    }

	    }
    }

    /**
     * getPaintStartIdx
     *
     * @return int
     */
    public int getPaintStartIdx() {
        return paintStartIdx;
    }

    /**
     * getPaintEndIdx
     *
     * @return int
     */
    public int getPaintEndIdx() {
        return paintEndIdx;
    }

	/**
     * setSaveGraphicsForChildren
     *
     * @param saveGraphicsForChildren	new boolean value for variable
     */
     public void setSaveGraphicsForChildren(boolean saveGraphicsForChildren) {
		this.saveGraphicsForChildren = saveGraphicsForChildren;
	}

	/**
     * getSaveGraphicsForChildren
     *
     * @return boolean
     */
	public boolean getSaveGraphicsForChildren() {
		return saveGraphicsForChildren;
	}

	/**
     * if true, save the picture for refreshes, in an offscreen image
     *
     * @param savePicture	new boolean value for variable
     */
     public void setSavePicture(boolean savePicture) {
		this.savePicture = savePicture;
	}

	/**
     * return flag, save the picture for refreshes, in an offscreen image
     *
     * @return boolean
     */
	public boolean getSavePicture() {
	    GadgetShell shell = getShell();
        return (savePicture &&
                shell != null &&
                !shell.isOverridingGraphics());
	}

	/**
	 * setPaintAnyOrder
	 *
	 * @param paintAnyOrder	new boolean value for variable
	 */
	public void setPaintAnyOrder(boolean paintAnyOrder) {
		this.paintAnyOrder = paintAnyOrder;
	}

	/**
	 * getPaintAnyOrder
	 *
	 * @return boolean
	 */
	public boolean getPaintAnyOrder() {
		return paintAnyOrder;
	}

    /**
     * setLastGroupedFocus
     *
     * @param lastGroupedFocus	new value for variable
     */
    protected void setLastGroupedFocus( Gadget lastGroupedFocus ) {
        this.lastGroupedFocus = lastGroupedFocus;
    }

    /**
     * getLastGroupedFocus
     *
     * @return gadget
     */
    protected Gadget getLastGroupedFocus() {
        return lastGroupedFocus;
    }

    /**
     * sets the shell of the component.
	 *
	 * @param shell		a GadgetShell
     */
	protected void setShell( GadgetShell shell ) {
		super.setShell( shell );
	    synchronized(getTreeLock()) {
        	for ( int i = 0 ; i < gadgetCount ; i++ ) {
        	    Gadget gadget = gadgets[i];
        	    if ( gadget != null ) {
        	        gadget.setShell( shell );
        	    }
        	}
        }
	}

    /**
	 * setGadgetHelp
	 *
	 * @param gadgetHelp		a GadgetHelp
     */
	public void setGadgetHelp( GadgetHelp gadgetHelp ) {
		super.setGadgetHelp( gadgetHelp );
    	for ( int i = 0 ; i < gadgetCount ; i++ ) {
    	    Gadget gadget = gadgets[i];
    	    if ( ( gadget != null ) &&
    	         ( gadget.getGadgetHelp() == null ) ) {
    	        gadget.setGadgetHelp( gadgetHelp );
    	    }
    	}
	}

	/**
	 * paintChildren
	 *
	 * @param paintAll	paint all children or not
	 * @param g			GadgetGraphics for graphics
	 * @param clip		clip rectangle
	 */
	protected void paintChildren( boolean paintAll, GadgetGraphics g, Rectangle clip/*, Vector damageList*/ ) {
	    Font font = getFont(g);
	    Color normalForeground = getNormalForeground(g,false);
	    Color selectedForeground = getSelectedForeground(g,false);
	    Color normalBackground = getNormalBackground(g);
	    Color selectedBackground = getSelectedBackground(g);
	    boolean saveGraphics = saveGraphicsForChildren;
	    boolean paintingAll = g.isPaintingAll();
	    if (paintingAll) {
	        saveGraphics = true; // painting all implies g is not from gadget shell
	    }
		if ( saveGraphics ) {
			g.ancestorFont = font;
			g.ancestorNormalForeground = normalForeground;
			g.ancestorSelectedForeground = selectedForeground;
			g.ancestorNormalBackground = normalBackground;
			g.ancestorSelectedBackground = selectedBackground;
		}
		else {
			g.dispose();
		}
		int start = 0;
		int end = gadgetCount;
        synchronized(getTreeLock()) {
    		if ( ( ! paintAll ) && paintAnyOrder &&
    		     paintStartIdx >= 0 && paintEndIdx <= gadgetCount-1 ) {
    		    start = paintStartIdx;
    		    end = paintEndIdx+1;
            }
            paintStartIdx = -1;
            paintEndIdx = -1;
        }
/*
long startTime = 0;
if (parent instanceof dtai.map.renderers.gadgets.MapRender) {
    System.err.println("painting "+(end-start));
    startTime = System.currentTimeMillis();
}
*/
    	for (int i = start; i < end; i++) {
    	    Gadget gadget = gadgets[i];
    	    if ( ( gadget != null ) &&
    	         ( gadget.isVisible() ) ) {
                boolean doPaint = false;
    	        boolean hasSomeDamage = false;
                if ( gadget instanceof ContainerGadget ) {
                    hasSomeDamage = ((ContainerGadget)gadget).someDamage;
                }
                if ( gadget.damaged || paintAll || hasSomeDamage ) {
                    doPaint = true;
                }
//                if (!doPaint) {
                if (doPaint) {
                    Rectangle bounds = gadget.getBounds();
                    /*
                    if (bounds.intersects(clip)) {
                        doPaint = true;
                    }
                    */
                    if (!bounds.intersects(clip)) {
                        doPaint = false;
                    }
                    /**/

                    /*
                    if (damageList == null) {
                        if (bounds.intersects(clip)) {
                            doPaint = true;
                        }
                    } else if (g.damageIntersects(bounds)) {
                        doPaint = true;
                    }
                    */
                }
                if (doPaint) {
        	        if ( gadget.isFrozen() ) {
        	            gadget.invalidate(false);
        	        }
        	        else {
                        if ( paintAll ) {
                            gadget.damaged = true;
                            if (gadget instanceof ContainerGadget) {
                                ((ContainerGadget)gadget).paintAllChildren = true;
                            }
                        }

                        GadgetGraphics childG;
                        if ( gadget.clippingRequired ||
							 ( ! saveGraphics ) ) {
							Rectangle gadgetclip = new Rectangle( gadget.x, gadget.y,
																  gadget.width, gadget.height );
							Rectangle newclip = clip.intersection( gadgetclip );
							if ( saveGraphics ) {
							    childG = g.create(gadget.x,gadget.y,gadget.width,gadget.height);
    							childG.clipRect( newclip.x - gadget.x, newclip.y - gadget.y,
    											 newclip.width, newclip.height );
							} else {
    							childG = getGadgetGraphics(
    							             newclip.x, newclip.y,
											 newclip.width, newclip.height);
                                childG.translate(gadget.x,gadget.y);
                                /*
                                childG.setDamageList(damageList);
                                */
                                childG.setPaintingAll(paintingAll); // paint only what's necessary
    						}
                			childG.ancestorFont = font;
                			childG.ancestorNormalForeground = normalForeground;
                			childG.ancestorSelectedForeground = selectedForeground;
                			childG.ancestorNormalBackground = normalBackground;
                			childG.ancestorSelectedBackground = selectedBackground;
						}
						else {
							childG = g;
							childG.translate(gadget.x,gadget.y);
						}
                	    gadget.update(childG);
                        if ( gadget.clippingRequired ||
							 ( ! saveGraphics ) ) {
            				childG.dispose();
						}
						else {
							childG.translate(-gadget.x,-gadget.y);
						}
            	    }
        	    }
    	    }
    	}
		if ( saveGraphics ) {
			g.dispose();
		}
/*
if (parent instanceof dtai.map.renderers.gadgets.MapRender) {
    System.err.println("paint took "+((System.currentTimeMillis()-startTime)/1000.0)+" seconds");
}
*/
	}

    /**
     * notifySomeDamage
     */
    protected void notifySomeDamage() {
        if ( ! someDamage ) {
            someDamage = true;
            if ( parent != null ) {
                parent.notifySomeDamage();
    			if ( ! parent.paintAnyOrder ) {
    				parent.paintAllChildrenAfter(this);
    			}
            }
        }
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
	    //synchronized(getTreeLock()) { removed to wait for image in ImageGadget
            if (getSavePicture() && setPaintFlag && !painting) {
                picture = null;
            }
    	    super.repaint(x,y,width,height,setPaintFlag,forced);
    	//}
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
	    if (width != this.width || height != this.height) {
    	    picture = null;
	    }
	    super.setBounds(x,y,width,height,invalidateParent);
    }

    /**
     * Returns the graphics to use.
     * @return GadgetGraphics
     */
	protected GadgetGraphics getTranslatedGraphics(Rectangle clip) {
        if (getSavePicture()) {
	        if (picture == null) {
	            picture = createImage(width,height);
	        }
	        if (picture == null) {
	            return null;
	        } else {
        	    GadgetGraphics g = new GadgetGraphics(
        	        picture.getGraphics(),0,0,width,height,null);
        	    return g;
        	}
        } else {
            return super.getTranslatedGraphics(clip);
        }
	}

    /**
     * Paints the gadget.
     *
     * @param g 	the specified GadgetGraphics window
     */
	public void update( GadgetGraphics g ) {
	    synchronized(getTreeLock()) {
    	    boolean wasDamaged = damaged || g.isPaintingAll();
    	    damaged = false;
    	    boolean paintAll;
    	    paintAll = paintAllChildren || g.isPaintingAll();
    	    paintAllChildren = false;
            Rectangle clip = g.getClipRect();
            if (getSavePicture()) {
                GadgetGraphics saveG = null;
    	        if (g.isPaintingAll()) {
    	            saveG = g;
    	        } else {
    	            g.dispose();
    	        }
    	        Image oldPicture = picture;
        	    GadgetGraphics imageg = getGadgetGraphics();
        	    boolean paintTheChildren = false;
    	        Image picture = this.picture;
        	    if (picture != oldPicture) {
        	        paintTheChildren = true;
        	    }
        	    if (picture != null && imageg != null) {
        	        if (paintTheChildren || someDamage) {
                	    imageg.setPaintingAll(false);
                	    if (!paintTheChildren) {
                            imageg.clipRect(clip.x,clip.y,clip.width,clip.height);
                        } else {
                            clip = new Rectangle(0,0,width,height);
                        }
                	    if ( paintTheChildren /*|| (wasDamaged && paintAll)*/) {
                	        paintTheChildren = true;
                	        painting = true;
                    	    super.update(imageg);
                    	    painting = false;
                	        if (imageg.isDisposed()) {
                    	        imageg = new GadgetGraphics(
                        	        picture.getGraphics(),0,0,width,height,null);
                        	    imageg.setPaintingAll(false);
                        	    if (!paintTheChildren) {
                                    imageg.clipRect(clip.x,clip.y,clip.width,clip.height);
                                } else {
                                    clip = new Rectangle(0,0,width,height);
                                }
                            }
                    	}
                	    if ( paintTheChildren || wasDamaged || someDamage /*|| paintAll*/ ) {
                        	paintChildren( paintTheChildren/*paintAll*/, imageg, clip/*, null*/ );
                        }
                    }
        	        g = saveG;
        	        if (g == null) {
            	        g = parent.getGadgetGraphics(x+clip.x,y+clip.y,clip.width,clip.height);
            	        g.translate(x,y);
            	    }
            	    g.drawImage(picture,0,0,this);
            	}
            } else {
                //Vector damageList = g.getDamageList();
        	    if ( wasDamaged && paintAll ) {
        	        GadgetGraphics updateg = g;
        	        if (g.isPaintingAll()) {
        	            updateg = g.create();
        	            g.clipRect(clip.x,clip.y,clip.width,clip.height);
        	        }
            	    super.update(updateg);
            	    if (updateg != g) {
            	        updateg.dispose();
            	    } else if (g.isDisposed()) {
            	        g = getGadgetGraphics(clip.x,clip.y,clip.width,clip.height);
            	        //g.setDamageList(damageList);
            	    }
            	}
        	    if ( wasDamaged || someDamage || paintAll ) {
                	paintChildren( paintAll, g, clip/*, damageList*/ );
                }
            }
        	someDamage = false;
        }
	}

	/**
     * returns true if children can overlap each other.
     * The default is taken from the layout manager, or if null, true.
     *
     * @return the flag
	 */
	public boolean childrenCanOverlap() {
	    if (layoutMgr != null) {
	        return layoutMgr.childrenCanOverlap();
	    }
	    return true;
	}

	/**
     * Paints all children after the gadget.
     *
     * @param g 	the specified Gadget
	 */
    public void paintAllChildrenAfter(Gadget g) {
        //synchronized(getTreeLock()) { removed to wait for image in ImageGadget
            if (childrenCanOverlap()) {
                boolean found = false;
                Gadget[] gadgets = getGadgets();
                for ( int i = 0; i < gadgetCount; i++ ) {
                    Gadget gadget = gadgets[i];
                    if ( found ) {
                        gadget.damaged = true;
                        if ( gadget instanceof ContainerGadget ) {
                            ((ContainerGadget)gadget).paintAllChildren = true;
                        }
                    }
                    else if ( gadget == g ) {
                        found = true;
                    }
                }
            }
        //}
    }

	/**
	 * Processes events occurring on this component.
	 * By default this method will call the appropriate "handleXXX"
	 * method for the type of event.
	 *
	 * @param e		an AWTEvent
	 */
	protected void processEvent( AWTEvent e ) {
	    Gadget newfocus = null;
	    if ( e instanceof MouseEvent ) {
	        MouseEvent mouse = (MouseEvent)e;
	        int mouse_x = mouse.getX();
	        int mouse_y = mouse.getY();
			int id = mouse.getID();
			Gadget g = getGadgetAt( mouse_x, mouse_y );
            switch ( id ) {
                case MouseEvent.MOUSE_RELEASED: {
                    g = mouseDownIn;
                    mouseDownIn = null;
                    break;
                }
                case MouseEvent.MOUSE_PRESSED: {
                    if ( ( g != null ) &&
                         ( g.isFocusTraversable() ) ) {
                        newfocus = g;
                    }
                    mouseDownIn = g;
                    break;
                }
                case MouseEvent.MOUSE_DRAGGED: {
                    g = mouseDownIn;
                    break;
                }
                case MouseEvent.MOUSE_ENTERED:
                case MouseEvent.MOUSE_EXITED: {
                    super.processEvent( e );
                    return;
                }
            }

	        if ( g == null ) {
	            return;
	        }
	        else if ( ( g != this ) &&
	                  ( g.isFunctioning() ) ) {
                mouse.translatePoint( -g.x, -g.y );
                mouse.setSource( g );
    	        g.processEvent( mouse );
            	mouse.setSource( this );
            	mouse.translatePoint( g.x, g.y );
        	    if (mouse.isConsumed()) {
        	        if (newfocus != null) {
                        newfocus.requestFocus();
                    }
        	        return;
        	    }
            }
	    }
    	super.processEvent( e );
        if (newfocus != null) {
            newfocus.requestFocus();
        }
	}

    /**
     * Returns the number of gadgets in this panel.
	 *
	 * @return	int
     * @see #getGadget
     */
    public final int getGadgetCount() {
	    return gadgetCount;
    }

    /**
     * Gets the nth gadget in this container.
     *
     * @param n the number of the gadget to get
	 * @return	Gadget
     * @exception ArrayIndexOutOfBoundsException If the nth value does not
     * 											 exist.
     */
    public final Gadget getGadget(int n) {
        synchronized(this) {
        	if ((n < 0) || (n >= gadgetCount)) {
        	    throw new ArrayIndexOutOfBoundsException("No such child: " + n);
        	}
        	return gadgets[n];
        }
    }

    /**
     * Gets all the gadgets in this container.
	 *
	 * @return	Gadget[]
     */
    public final Gadget[] getGadgets() {
        synchronized(this) {
        	Gadget list[] = new Gadget[gadgetCount];
        	System.arraycopy(gadgets, 0, list, 0, gadgetCount);
        	return list;
        }
    }

    /**
     * Gets all the visible gadgets in this container.
	 *
	 * @return	Gadget[]
     */
    public final Gadget[] getVisibleGadgets() {
        synchronized(this) {
            int numvis = 0;
            for ( int i = 0; i < gadgetCount; i++ ) {
                if ( gadgets[i].visible ) {
                    numvis++;
                }
            }
        	Gadget list[] = new Gadget[numvis];
        	int idx = 0;
            for ( int i = 0; i < gadgetCount; i++ ) {
                if ( gadgets[i].visible ) {
                    list[idx++] = gadgets[i];
                }
            }
        	return list;
        }
    }

    /**
     * Gets the index of the specified gadget.
	 *
	 * @param gadget	which Gadget's index to get
	 * @return	int
     */
    public int getGadgetIndex( Gadget gadget ) {
        synchronized(this) {
        	if (gadget.parent == this)  {
        	    for (int i = 0 ; i < gadgetCount ; i++) {
            		if (gadgets[i] == gadget) {
            		    return i;
            		}
            	}
            }
        	return -1;
        }
    }

    /**
     * Adds the specified gadget to this container, and
	 * returns the Gadget being added.
     *
     * @param gadget 	the gadget to be added
     * @return  	the gadget to be added
     */
    public final Gadget add(Gadget gadget) {
	    return add(gadget, -1);
    }

    /**
     * Adds the specified gadget to this container at the given position.
     *
     * @param gadget  the gadget to be added
     * @param pos 	  the position at which to insert the gadget. -1
     * 				  means insert at the end.
	 * @return		  the gadget being added
     * @see #remove
     */
    public Gadget add(Gadget gadget, int pos) {
        synchronized(getTreeLock()) {
        	if (gadget.parent != null) {
        	    gadget.parent.remove(gadget);
        	}
            synchronized(this) {
            	if (pos > gadgetCount || (pos < 0 && pos != -1)) {
            	    throw new IllegalArgumentException("illegal gadget position");
            	}
            	// check to see that gadget isn't one of this container's parents
            	if (gadget instanceof ContainerGadget) {
            	    for (ContainerGadget cn = this; cn != null; cn=cn.parent) {
                		if (cn == gadget) {
                		    throw new IllegalArgumentException("adding container's parent to itself");
                		}
            	    }
            	}

            	if (gadgetCount == gadgets.length) {
            	    Gadget newgadgets[] = new Gadget[gadgetCount * 2];
            	    System.arraycopy(gadgets, 0, newgadgets, 0, gadgetCount);
            	    gadgets = newgadgets;
            	}
            	if (pos == -1 || pos==gadgetCount) {
            	    gadgets[gadgetCount++] = gadget;
            	} else {
            	    System.arraycopy(gadgets, pos, gadgets, pos+1, gadgetCount-pos);
            	    gadgets[pos] = gadget;
            	    gadgetCount++;
            	}
        	}
    	    gadget.setParent( this, false );
        	if ( layoutMgr != null ) {
            	if (valid) {
    				gadget.x = width;
    				gadget.y = height; // move won't cause repaint in old pos
            	    invalidate();
            	}
            }
    		else {
        	    if (!gadget.valid) {
        	        notifyInvalid();
        	    }
        	    if (gadget.width > 0 && gadget.height > 0) {
    				gadget.repaint();
    			} else {
    			    repaint(gadget.x,gadget.y,1,1,false,false); // need this to invoke paint to invoke validate
    			                                    // to actually set the gadget's size sometimes
    			}
    		}
            if ( onlyDefaultButtons ) {
                if ( gadget instanceof ButtonGadget &&
                     ((ButtonGadget)gadget).getDefaultThickness() > 0 ) {
                    gadget.setFocusGroup(this);
                }
                else {
                    onlyDefaultButtons = false;
                }
            }
            return gadget;
        }
    }

    /**
     * Replaces the specified gadget in this container at the given position.
     *
     * @param gadget the gadget to be replaced
     * @param pos the position at which to replace the gadget.
	 * @return		the gadget being replaced.
     * @see #add
	 * @see #remove
     */
    public Gadget replace(Gadget gadget, int pos) {
        synchronized(getTreeLock()) {
    		Gadget oldGadget = getGadget( pos );
            boolean gvis = oldGadget.visible;
            oldGadget.setVisible(false);
    	    if (layoutMgr != null) {
    		    layoutMgr.removeLayoutGadget(oldGadget);
    	    }
        	if (gadget.parent != null) {
        	    gadget.parent.remove(gadget);
        	}
            synchronized(this) {

            	// check to see that gadget isn't one of this container's parents
            	if (gadget instanceof ContainerGadget) {
            	    for (ContainerGadget cn = this; cn != null; cn=cn.parent) {
                		if (cn == gadget) {
                		    throw new IllegalArgumentException("adding container's parent to itself");
                		}
            	    }
            	}

           	    gadgets[pos] = gadget;
            }
        	gadget.setParent( this, false );
        	oldGadget.setParent( null, false );
        	if ( layoutMgr != null ) {
            	if (valid) {
            	    invalidate();
            	}
            }
            oldGadget.setVisible( gvis );
            return gadget;
        }
    }

    /**
     * Adds the specified gadget to this container. The gadget
     * is also added to the layout manager of this container using the
     * name specified
     *
     * @param name the gadget name
     * @param gadget the gadget to be added
	 * @return		the gadget being added.
     * @see #remove
     * @see GadgetLayoutManager
     */
    public Gadget add(String name, Gadget gadget) { // don't make this final
        synchronized(getTreeLock()) {
        	Gadget g = add(gadget);
        	GadgetLayoutManager layoutMgr = this.layoutMgr;
        	if (layoutMgr != null) {
        	    layoutMgr.addLayoutGadget(name, gadget);
        	}
        	return g;
        }
    }

    /**
     * Removes the specified gadget from this container.
     *
     * @param gadget the gadget to be removed
     * @see #add
     */
    public void remove(Gadget gadget) {
	    remove( gadget, getGadgetIndex( gadget ) );
    }

	/**
	 * Removes the gadget at the specified index from this container.
	 *
	 * @param index		the index of the gadget to be removed
	 * @see #add
	 */
    public void remove(int i) {
        remove( getGadget(i), i );
    }

    /**
	 * Removes the specified gadget from this container.
     *
     * @param gadget the gadget to be removed
	 * @param i		the index of the gadget to be removed
     */
    protected void remove( Gadget gadget, int i ) {
        synchronized(getTreeLock()) {
    	    if ( gadget.parent == this ) {
                boolean gvis = gadget.visible;
                gadget.setVisible(false,(layoutMgr!=null));
        	    if ( gadget.parent == this ) {
            	    if (layoutMgr != null) {
            		    layoutMgr.removeLayoutGadget(gadget);
            	    }
            	    gadget.setParent( null, false );
                    synchronized(this) {
                	    System.arraycopy(gadgets, i + 1, gadgets, i, gadgetCount - i - 1);
                	    gadgets[--gadgetCount] = null;
                	}
            	    if ( layoutMgr != null ) {
            		    if (valid) {
            			    invalidate();
            		    }
            		}
                	gadget.setVisible(gvis);
                }
        	}
        }
    }

    /**
     * Removes all the gadgets from this container.
     *
     * @see #add
     * @see #remove
     */
    public void removeAll() {
        synchronized(getTreeLock()) {
            if ( gadgetCount > 0 ) {
            	while (gadgetCount > 0) {
            	    Gadget gadget = null;
                    synchronized(this) {
                        if (gadgetCount > 0) {
                    	    gadget = gadgets[--gadgetCount];
                    	    gadgets[gadgetCount] = null;
                    	}
                	}

            	    if (gadget != null) {
                	    gadget.repaint();

                	    if (layoutMgr != null) {
                		    layoutMgr.removeLayoutGadget(gadget);
                	    }
                	    gadget.setParent( null, false );
                	}
            	}
            	if (valid && layoutMgr != null) {
            	    invalidate();
            	}
            }
        }
    }

    /**
     * brings the gadget to the front
     *
     * @param gadget	the Gadget to bring to the front
     */
    public void bringToFront( Gadget gadget ) {
	    int i = getGadgetIndex( gadget );
	    if ( ( i >= 0 ) &&
	         ( i != gadgetCount-1 ) ) {
		    System.arraycopy(gadgets, i + 1, gadgets, i, gadgetCount - i - 1);
		    gadgets[gadgetCount-1] = gadget;
		    gadget.repaint();
		}
    }

    /**
     * sends the gadget to the back
     *
     * @param gadget	the Gadget to sent to the back
     */
    public void sendToBack( Gadget gadget ) {
	    int i = getGadgetIndex( gadget );
	    if ( ( i >= 0 ) &&
	         ( i != 0 ) ) {
		    System.arraycopy(gadgets, 0, gadgets, 1, i);
		    gadgets[0] = gadget;
		    gadget.repaint();
		}
    }

    /**
     * brings the gadget forward
     *
     * @param gadget	the Gadget to bring forward
     */
    public void bringForward( Gadget gadget ) {
	    int i = getGadgetIndex( gadget );
	    if ( ( i >= 0 ) &&
	         ( i != gadgetCount-1 ) ) {
		    gadgets[i] = gadgets[i+1];
		    gadgets[i+1] = gadget;
		    gadget.repaint();
		}
    }

    /**
     * sens the gadget backward
     *
     * @param gadget	the Gadget to send backward
     */
    public void sendBackward( Gadget gadget ) {
	    int i = getGadgetIndex( gadget );
	    if ( ( i >= 0 ) &&
	         ( i != 0 ) ) {
		    gadgets[i] = gadgets[i-1];
		    gadgets[i-1] = gadget;
		    gadget.repaint();
		}
    }

    /**
     * Gets the layout manager for this container.
	 *
	 * @return	the GadgetLayoutManager layoutMgr
     * @see #setLayout
     */
    public final GadgetLayoutManager getLayout() {
	    return layoutMgr;
    }

    /**
     * Sets the layout manager for this container.
     *
     * @param mgr the specified layout manager
     * @see #getLayout
     */
    public void setLayout(GadgetLayoutManager mgr) {
    	layoutMgr = mgr;
    	if (valid) {
    	    invalidate();
    	}
    }

    /**
     * Does a layout on this Container.
     *
     * @see #setLayout
     */
    public void doLayout() {
    	GadgetLayoutManager layoutMgr = this.layoutMgr;
    	if (layoutMgr != null) {
    	    layoutMgr.layoutContainerGadget(this);
    	}
    }

    /**
     * Validates this Container and all of the gadgets contained within it.
     *
     * @see #validate
     * @see Gadget#invalidate
     */
    public void validate() {
        if ( frozen == 0 ) {
            if ( ! valid || ! childrenValid ) {
                childrenValid = true;
                boolean saveValid = valid;
                valid = true;
            	for (int i = 0 ; i < gadgetCount ; i++) {
            	    Gadget gadget = gadgets[i];
            	    if ( gadget != null &&
            	         ( ( ! gadget.valid ) ||
                	         ( gadget instanceof ContainerGadget &&
                	           ! ((ContainerGadget)gadget).childrenValid ) ) ) {
        	            gadget.validate();
            	    }
            	}
            	if ( ! saveValid ) {
            	    if ( ! valid ) {
            	        notifyInvalid();
            	    }
            	    valid = false;
            	    super.validate();
                }
            }
        }
    }

	/**
	 * thaw
	 */
	public void thaw() {
	    if ( frozen > 0 ) {
	        super.thaw();
	        if ( frozen == 0 ) {
    	        if ( ! childrenValid ) {
    	            childrenValid = true;
    	            notifyInvalid();
    	        }
    	    }
	    }
	}

	/**
	 * notifyInvalid
	 */
	public void notifyInvalid() {
        //synchronized(getTreeLock()) { removed to wait for image in ImageGadget
    	    childrenValid = false;
            if ( frozen > 0 ) {
                return;
            }
	        ContainerGadget parent = this.parent;
    	    if ( parent != null ) {
    	        parent.notifyInvalid();
    	    }
    	    else {
    	        GadgetShell shell = this.shell;
    	        if ( shell != null ) {
        	        shell.notifyInvalid();
        	    }
    	    }
    	//}
	}

    /**
     * Returns the preferred size of this container.
	 *
	 * @return	the preferred size as a Dimension
     * @see #getMinimumSize
     */
    public Dimension getPreferredSize() {
	    int pw = pref_width;
	    int ph = pref_height;
	    if ( ( pw >= 0 ) && ( ph >= 0 ) ) {
	        return new Dimension( pw, ph );
	    }
        synchronized(getTreeLock()) {
        	GadgetLayoutManager layoutMgr = this.layoutMgr;
        	Dimension pref;
        	if (layoutMgr != null) {
        	    pref = layoutMgr.preferredLayoutSize(this);
        	}
            else {
        	    pref = super.getPreferredSize();
        	}
        	return pref;
        }
    }

    /**
     * Returns the minimum size of this container.
	 *
	 * @return	the minimum size as a Dimension
     * @see #getPreferredSize
     */
    public Dimension getMinimumSize() {
        synchronized(getTreeLock()) {
        	GadgetLayoutManager layoutMgr = this.layoutMgr;
        	return (layoutMgr != null) ?
        	    layoutMgr.minimumLayoutSize(this)
        	    : super.getMinimumSize();
        }
    }

    /**
     * Locates the gadget that contains the x,y position.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return null if the gadget is not within the x and y
     * 				coordinates; returns the gadget otherwise.
     * @see Gadget#contains
     */
    public Gadget getGadgetAt(int x, int y) {
        return getGadgetAt( x, y, null );
    }

    /**
     * Locates the gadget that contains the x,y position.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param exclude the gadget to ignore (if not null)
     * @return null if the gadget is not within the x and y
     * 				coordinates; returns the gadget otherwise.
     * @see Gadget#contains
     */
	public Gadget getGadgetAt(int x, int y, Gadget exclude ) {
        synchronized(getTreeLock()) {
        	if (!contains(x, y, exclude)) {
        	    return null;
        	}
        	for (int i = gadgetCount - 1 ; i >= 0 ; i--) {
        	    Gadget gadget = gadgets[i];

        	    if ( ( gadget != null ) &&
        	         ( gadget != exclude ) &&
        	         gadget.visible &&
        	         gadget.contains(x - gadget.x, y - gadget.y, exclude) ) {
    				return gadget;
        	    }
        	}
        	if ( isTransparent() &&
        	     ( ! consumingTransparentClicks ) ) {
        	    return null;
        	}
        	else {
            	return this;
            }
        }
    }

	/**
     * Checks whether this component "contains" the specified (x, y)
	 * location, where x and y are defined to be relative to the
	 * coordinate system of this component.
     * @param x 		the x coordinate
     * @param y 		the y coordinate
     * @param exclude   the gadget to ignore (if not null)
	 * @return			boolean is/is not inside
     * @see #getGadgetAt
	 */
	public boolean contains(int x, int y, Gadget exclude) {
        synchronized(getTreeLock()) {
    		if ( ( ! visible ) ||
    		     ( this == exclude ) ||
    		     ( ! ( (x >= 0) &&
    		           (x < width) &&
    		           (y >= 0) &&
    		           (y < height) ) ) ) {
                return false;
            }
            if ( ( ! isTransparent() ) ||
                 ( consumingTransparentClicks ) ) {
                return true;
            }
            else {
            	for (int i = 0 ; i < gadgetCount ; i++) {
            	    Gadget gadget = gadgets[i];
            	    if ( ( gadget != null ) &&
            	         ( gadget != exclude ) &&
            	         gadget.isVisible() &&
            	         gadget.contains(x - gadget.x, y - gadget.y, exclude) ) {
                		return true;
            	    }
            	}
            	return false;
            }
        }
	}

    /**
     * gets the insets of the gadget
     *
     * @return Insets
     */
    public Insets getInsets() {
        return new Insets(0,0,0,0);
    }

	/**
	 * findNExtFocusGadget
	 *
	 * @return Gadget
	 */
	public Gadget findNextFocusGadget() {
	    if ( isFocusTraversable() ) {
	        return this;
	    }
    	for (int i = 0 ; i < gadgetCount ; i++) {
    	    Gadget next = gadgets[i].findNextFocusGadget();
    	    if ( next != null ) {
    	        return next;
    	    }
    	}
        return null;
    }

	/**
	 * fontChanged
	 */
	public void fontChanged() {
	    super.fontChanged();
		for (int i = 0 ; i < gadgetCount ; i++) {
    	    Gadget gadget = gadgets[i];
			gadget.fontChanged();
    	}
	}

    /**
     * notifyStateChange
     */
    public void notifyStateChange() {

		for (int i = 0 ; i < gadgetCount ; i++) {
    	    Gadget gadget = gadgets[i];
			gadget.notifyStateChange();
    	}
    }
}
