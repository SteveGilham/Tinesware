/****************************************************************
 **
 **  $Id: TreeNode.java,v 1.7 1997/09/30 03:15:52 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/TreeNode.java,v $
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

/**
 * Objects of the TreeNode class hold the pertinent information
 * about TreeViewGadget objects, such as name, attributes, etc.
 *
 * @version 1.1
 * @author		DTAI, Incorporated
 */
public interface TreeNode extends TreeItem {

    /**
     * add
     * @param child	the TreeItem to be added as a child of this TreeNode
     */
    public abstract void add(TreeItem child);

    /**
     * insert
     * @param child	the TreeItem to be inserted as a child of this TreeNode
     * @param pos	position of the child to be inserted
     */
    public abstract void insert(TreeItem child, int pos);

    /**
     * remove
     * @param child	the TreeItem to be a child of this TreeNode
     */
    public abstract void remove(TreeItem child);

    /**
     * remove
     * @param pos	the position to be removed
     */
    public abstract void remove(int pos);

    /**
     * getItemCount
     * @return int
     */
    public abstract int getItemCount();

    /**
     * getItems
     * @return TreeItem[]
     */
    public abstract TreeItem[] getItems();

    /**
     * getItem
     * @param i		the position of the item to be returned
     * @return TreeItem
     */
    public abstract TreeItem getItem(int i);

    /**
     * getItemIndex
     * @param item	the item whose index is wanted
     * @return int
     */
    public abstract int getItemIndex(TreeItem item);

    /**
     * setExpanded
     * @param expanded	new value for expanded variable
     */
    public abstract void setExpanded(boolean expanded);

    /**
     * isExpanded
     * @return boolean
     */
    public abstract boolean isExpanded();

    /**
     * change the number of visible items
     * @param change the number by which the visible descendent count changes
     */
    public abstract void alterVisibleCount(int change);
}
