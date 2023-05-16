/****************************************************************
 **
 **  $Id: TreeViewGadget.java,v 1.9 1997/08/06 23:27:14 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/TreeViewGadget.java,v $
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
import java11.awt.event.AdjustmentEvent;
import java11.awt.event.ActionListener;

/**
 * The TreeViewGadget class holds as a component the TreeViewGadget menu.
 *
 * @version 1.1
 * @author 		DTAI, Incorporated
 */
public class TreeViewGadget extends ScrollPaneGadget implements TreeSelectable {

    public TreePanel treePanel;

	/**
	 * Constructor creates an explorer menu gadget
	 * for user interaction.
	 * @param warehouse		the warehouse at the root of
	 *						the overlay tree
	 * @param path			path to the warehouse-will be
	 *						converted to a url
	 */
	public TreeViewGadget() {
	    this(null);
	}

	/**
	 * TreeViewGadget
	 * @param roo - TBD
	 */
	public TreeViewGadget(TreeNode root) {
		treePanel = new TreePanel();
		setStretchingHorizontal( true );
		setStretchingVertical( true );
		setShrinkingVertical( true );
		add( treePanel );
	    setRoot(root);
	}

	/**
	 * setRoot
	 * @param root - TBD
	 */
	public void setRoot(TreeNode root) {
	    treePanel.setRoot(root);
	}

	/**
	 * returns the root item in the tree
	 * @return root
	 */
	public TreeItem getRoot() {
	    return treePanel.getRoot();
	}

	/**
	 * doLayout
	 */
	public void doLayout() {
	    super.doLayout();
        ScrollbarGadget vScrollbar = getVScrollbar();
        if ( ! treePanel.isValid() ) {
            valid = false;
            return;
        }
        if ( vScrollbar.isVisible() ) {
            int numVisible = treePanel.getNumVisible();
            int numGadgets = treePanel.getNumDisplayed();
            int topIndex = treePanel.getTopIndex();
            if ( numGadgets > numVisible ) {
        	    vScrollbar.setValue( topIndex );
                vScrollbar.setMaximum( numGadgets - numVisible );
                vScrollbar.setVisibleAmount( numVisible );
                vScrollbar.setBlockIncrement( numVisible - 1 );
                vScrollbar.setUnitIncrement( 1 );
            }
        }
    }

    /**
     * adjustmentValueChanged
     * @param e - TBD
     */
    public void adjustmentValueChanged(AdjustmentEvent e) {
        synchronized(getTreeLock()) {
            ScrollbarGadget vScrollbar = getVScrollbar();
            if ( e.getAdjustable() == getVAdjustable() ) {
                int value = vScrollbar.getValue();
                treePanel.setTopIndex(value);
                int pw = treePanel.pref_width;
                int ph = treePanel.pref_height;
                treePanel.invalidate(false);
                treePanel.pref_width = pw;
                treePanel.pref_height = ph;
            }
            else {
                super.adjustmentValueChanged(e);
            }
        }
    }

    /**
     * getNumVisible
     * @return int
     */
    public int getNumVisible() {
        return treePanel.getTopIndex();
    }

    /**
     * getTopIndex
     * @return int
     */
    public int getTopIndex() {
        return treePanel.getTopIndex();
    }

	/**
	 * getIgnoreDefaultAction
	 * @return boolean
	 */
	public boolean getIgnoreDefaultAction() {
		return treePanel.getIgnoreDefaultAction();
	}

	/**
	 * setIgnoreDefaultAction
	 * @param ignoreDefaultAction - TBD
	 */
	public void setIgnoreDefaultAction( boolean ignoreDefaultAction ) {
	    treePanel.setIgnoreDefaultAction(ignoreDefaultAction);
	}

    /**
     * getSelectedTreeItem
     * @return TreeItem
     */
    public TreeItem getSelectedTreeItem() {
        return treePanel.getSelectedTreeItem();
    }

    /**
     * select
     * @param item - TBD
     */
    public void select(TreeItem item) {
        treePanel.select(item);
    }

    /**
     * Adds the specified action listener to receive action events
	 * from this list. Action events occur when a list item is double-clicked.
     *
     * @param l - the action listener
     */
    public void addActionListener(ActionListener l) {
        treePanel.addActionListener(l);
    }

    /**
     * Removes the specified action listener so it no longer receives action events from this list.
     *
     * @param l - the action listener
     */
    public void removeActionListener(ActionListener l) {
        treePanel.removeActionListener(l);
    }


    /**
     * Adds the specified tree item listener to recieve tree item events from this list.
     *
     * @param l - the tree item listener
     */
    public void addTreeListener(TreeListener l) {
        treePanel.addTreeListener(l);
    }

    /**
     * Removes the specified tree item listener so it no longer
	 * receives tree item events from this list.
     *
     * @param l - the tree item listener
     */
    public void removeTreeListener(TreeListener l) {
        treePanel.removeTreeListener(l);
    }
}
