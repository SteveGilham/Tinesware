/****************************************************************
 **
 **  $Id: TreeEvent.java,v 1.11 1997/08/06 23:27:13 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/TreeEvent.java,v $
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

import java.awt.Event;
import java.awt.Point;
import java11.awt.event.InputEvent;
import java11.util.EventObject;

/**
 * TreeEvent
 * @version 1.1
 * @author DTAI, Incorporated
 */
public class TreeEvent extends InputEvent {

	/**
	 * The tree item state changed event type.
 	 */
    public final static int TREE_STATE_CHANGED = 125;

	/**
	 * The tree item is a TreeNode and was expanded.
 	 */
    public final static int TREE_NODE_EXPANDED = 126;

	/**
	 * The tree item is a TreeNode and was condensed.
 	 */
    public final static int TREE_NODE_CONDENSED = 127;

    public final static int TREE_FIRST = 125;
    public final static int TREE_LAST = 127;

	/**
	 * The tree item selected state change type.
	 */
    public final static int SELECTED = 1;

	/**
	 * The tree item de-selected state change type.
	 */
    public final static int DESELECTED = 0;

    private int stateChange;
    private int x;
    private int y;
    private TreeItem item;

    /**
     * TreeEvent
     */
    public TreeEvent() {
    }

    /**
     * TreeEvent
     * @param source - TBD
     * @param evt - TBD
     * @param id - TBD
     * @param stateChange - TBD
     * @param x - TBD
     * @param y - TBD
     */
    public TreeEvent( TreeSelectable source, Event evt, int id,
                      int stateChange, TreeItem item, int x, int y ) {
        super( evt, id );
        setSource( source );
        this.stateChange = stateChange;
        this.x = x;
        this.y = y;
        this.item = item;
    }

    /**
     * TreeEvent
     * @param source - TBD
     * @param id - TBD
     * @param state - TBD
     */
    public TreeEvent(TreeSelectable source, int id, int state, TreeItem item) {
        super(null, id);
        setSource( source );
        this.stateChange = stateChange;
        this.item = item;
    }

	/**
	 * Returns the TreeSelectable object where this event originated.
	 * @return TreeSelectable
	 */
    public TreeSelectable getTreeSelectable() {
        return (TreeSelectable)getSource();
    }

	/**
	 * Returns the state change type which generated the event.
	 *
	 * @see SELECTED, DESELECTED
	 * @return int
	 */
    public int getStateChange() {
        return stateChange;
    }

    /**
     * returns the item selected/expanded/condensed
     * @return TreeItem
     */
    public TreeItem getTreeItem() {
        return item;
    }

    /**
     * returns the x location of the mouse, if mouse produced the event,
     * otherwise the lower left corner
     * @return int
     */
    public int getX() {
        return x;
    }

    /**
     * returns the y location of the mouse, if mouse produced the event,
     * otherwise the lower left corner
     * @return int
     */
    public int getY() {
        return y;
    }

    /**
     * returns the location of the mouse, if mouse produced the event,
     * otherwise the lower left corner
     * @return Point
     */
    public synchronized final Point getPoint() {
        return new Point( x, y );
    }
}
