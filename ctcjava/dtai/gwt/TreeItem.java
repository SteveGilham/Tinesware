/****************************************************************
 **
 **  $Id: TreeItem.java,v 1.10 1997/09/30 03:15:52 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/TreeItem.java,v $
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
 * Objects of the TreeItem class hold the pertinent information
 * about TreeViewGadget objects, such as name, attributes, etc.
 *
 * @version 1.1
 * @author DTAI, Incorporated
 */
public interface TreeItem {

    /**
     * setParent
     * @param parent	parent of this TreeItem
     */
    public abstract void setParent(TreeNode parent);

    /**
     * getParent
     * @return TreeNode
     */
    public abstract TreeNode getParent();

    /**
     * getLevel
     * @return int
     */
    public abstract int getLevel();

    /**
     * getRoot
     * @return TreeItem
     */
    public abstract TreeItem getRoot();

    /**
     * Returns arg.
     * @return arg.
     */
	public abstract Object getArg();

    /**
     * Sets arg.
     * @param arg	new value for arg
     */
    public abstract void setArg( Object arg );

    /**
     * setTreePanel
     * @param treePanel		the Panel this TreeItem is on
     */
    public abstract void setTreePanel(TreePanel treePanel);

    /**
     * getTreePanel
     * @return TreePanel
     */
    public abstract TreePanel getTreePanel();

    /**
     * getGadget
     * @return Gadget
     */
    public abstract Gadget getGadget();

    /**
     * isSelected
     * @return true if the gadget is selected
     */
    public abstract boolean isSelected();

    /**
     * isShowing
     * @return true if there is no parent, or if the parent and ancestors are expanded
     */
    public abstract boolean isShowing();

    /**
     * get the number of visible items including this item and any visible
     * descendents
     * @return int
     */
    public abstract int getVisibleCount();
}
