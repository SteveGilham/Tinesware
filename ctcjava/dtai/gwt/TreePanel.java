/****************************************************************
 **
 **  $Id: TreePanel.java,v 1.26 1998/02/13 19:46:38 ccp Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/TreePanel.java,v $
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
import java.awt.Event;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java11.awt.event.ActionListener;
import java11.awt.event.ActionEvent;
import java11.awt.event.MouseListener;
import java11.awt.event.MouseEvent;
import java11.awt.event.KeyEvent;
import java11.awt.event.FocusEvent;
import java11.awt.event.AWTEvent;
import java.util.Vector;

/**
 * The TreePanel class actually maintains the
 * TreeViewGadget menu choices.
 *
 * @version 1.1
 * @author			DTAI, Incorporated
 */
public class TreePanel extends BorderGadget implements TreeSelectable, MouseListener {

    protected static final int INDENT = 16;
    private static final int HGAP = 2;
    protected static final int CONTROL_WIDTH = 9;

    ActionListener actionListener;
    TreeListener treeListener;

    private TreeItem root = null;

    private int tempWidth;
    private int tempHeight;
    private boolean ignoreDefaultAction = false;
    private boolean expandingOnAction = true;

    private TreeItemPanel selectedItem;

    private int topIndex = 0;
    private int numVisible = 0;
    private int numDisplayed = 0;

    private boolean continueDisplay;

    private TreeItem topItem;
    private TreeItem bottomItem;

    private Vector lastOffBottom = new Vector();
    private boolean layoutOK = false;

	/**
	 * Constructor initializes the TreeViewGadget.
	 */
    public TreePanel() {
        setMargins(1);
        setLayout( null );
	}

	/**
     * returns true if children can overlap each other.
     * @return false
	 */
	public boolean childrenCanOverlap() {
	    return false;
	}

	/**
	 * setRoot
	 * @param root - TBD
	 */
	public void setRoot(TreeItem root) {
	    if (root != this.root) {
	        if (this.root != null) {
	            this.root.setTreePanel(null);
	        }
	        this.root = root;
	        if (this.root != null) {
                this.root.setTreePanel(this);
            }
            invalidate();
	    }
	}

	/**
	 * returns the root item in the tree
	 * @return root
	 */
	public TreeItem getRoot() {
	    return root;
	}

	/**
	 * isFocusTraversable
	 * @return boolean
	 */
	public boolean isFocusTraversable() {
	    return true;
	}

	/**
	 * getIgnoreDefaultAction
	 * @return boolean
	 */
	 public boolean getIgnoreDefaultAction() {
		return ignoreDefaultAction;
	}

	/**
	 * setIgnoreDefaultAction
	 * @param ignoreDefaultAction - TBD
	 */
	public void setIgnoreDefaultAction( boolean ignoreDefaultAction ) {
	    this.ignoreDefaultAction = ignoreDefaultAction;
	}

	/**
	 * isExpandingOnAction
	 * @return boolean
	 */
	public boolean isExpandingOnAction() {
		return expandingOnAction;
	}

	/**
	 * setExpandingOnAction
	 * @param expandingOnAction - TBD
	 */
	public void setExpandingOnAction( boolean expandingOnAction ) {
	    this.expandingOnAction = expandingOnAction;
	}

    /**
     * getNumVisible
     * @return int
     */
    public int getNumVisible() {
        return numVisible;
    }

    /**
     * getNumDisplayed
     * @return int
     */
    public int getNumDisplayed() {
        return numDisplayed;
    }

    /**
     * getTopIndex
     * @return int
     */
    public int getTopIndex() {
        return topIndex;
    }

    /**
     * setTopIndex
     * @param topIndex - TBD
     */
    public void setTopIndex(int topIndex) {
        synchronized(getTreeLock()) {
            if (topIndex != this.topIndex) {
                this.topIndex = topIndex;
                invalidate();
                repaint();
            }
        }
    }

    private void notifyFontChange(TreeItem item) {
	    Object arg = item.getGadget().getArg();
	    if (arg != null) {
	        TreeItemPanel itemPanel = (TreeItemPanel)arg;
            if (itemPanel.parent == null) {
                itemPanel.fontChanged();
            }
    	    if (item instanceof TreeNode) {
    	        TreeNode node = (TreeNode)item;
    	        int count = node.getItemCount();
    	        for (int i = 0; i < count; i++) {
    	            notifyFontChange(node.getItem(i));
    	        }
    	    }
	    }
	}

    /**
     * fontChanged
     */
    public void fontChanged() {
        super.fontChanged();
	    synchronized(getTreeLock()) {
	        if (root != null) {
    	        notifyFontChange(root);
    	    }
	    }
    }

    public void invalidate(boolean b) {
        if (isValid()){
            layoutOK = false;
        }
        super.invalidate(b);
    }

    /**
     * doLayout
     */
    public void doLayout() {
        if (layoutOK) {
            return;
        }
	    synchronized(getTreeLock()) {
	        layoutOK = false;
            Insets insets = getInsets();
	        tempWidth = insets.left;
	        tempHeight = insets.top;
	        numDisplayed = 0;
	        numVisible = 0;
	        continueDisplay = true;
	        lastOffBottom.removeAllElements();
	        topItem = null;
	        bottomItem = null;
	        if (root != null) {
    	        root.getGadget().setVisible(true);
//    	        removeAll();
                for (int i = 0; i < gadgetCount; i++) {
                    ((TreeItemPanel)gadgets[i]).added = false;
                }
    	        layoutItem(root, true, insets.left);
                for (int i = 0; i < gadgetCount; i++) {
                    TreeItemPanel itemPanel = (TreeItemPanel)gadgets[i];
                    if (!itemPanel.added) {
                        remove(itemPanel,i);
                        i--;
                    }
                }
    	    }
    	    tempWidth += insets.right;
    	    tempHeight += insets.bottom;
    	    layoutOK = true;
    	    repaint();
    	}
	}

	/**
	 * getPreferredSize
	 * @return Dimension
	 */
	public Dimension getPreferredSize() {
	    synchronized(getTreeLock()) {
    	    if (pref_width < 0 || pref_height < 0) {
	            doLayout();
    	        pref_width = tempWidth;
    	        pref_height = tempHeight;
    	    }
    	    return new Dimension(pref_width, pref_height);
    	}
    }

    /**
     * requiresVertScrollbar
     * @return boolean
     */
    protected boolean requiresVertScrollbar() {
        if ( numDisplayed > numVisible ) {
            return true;
        }
        return false;
    }

	private void layoutItem(TreeItem item, boolean visible, int curX) {
	    Font font = getFont();
	    Gadget gadget = item.getGadget();
	    TreeItemPanel itemPanel;
	    Object arg = gadget.getArg();
	    if (arg == null) {
	        itemPanel = new TreeItemPanel(item);
	        itemPanel.addMouseListener(this);
	    } else {
	        itemPanel = (TreeItemPanel)arg;
	    }

	    int prefHeight = 0;
	    boolean hide = true;
	    if (visible) {
	        itemPanel.setFont(font);
    	    int prefWidth = itemPanel.pref_width;
    	    prefHeight = itemPanel.pref_height;
    	    if (prefWidth < 0 || prefHeight < 0) {
    	        Dimension pref = itemPanel.getPreferredSize();
    	        prefWidth = pref.width;
    	        prefHeight = pref.height;
    	    }
            numDisplayed++;
        	tempWidth = Math.max(tempWidth, curX+prefWidth);
	        if (numDisplayed > topIndex) {
	            if (numDisplayed == (topIndex+1)) {
                    topItem = item;
                }
	            if (tempHeight < height) {
    	            hide = false;
    	            itemPanel.added = true;
    	            if (itemPanel.parent != this) {
                	    add(itemPanel);
                	}
            	    itemPanel.setBounds(curX, tempHeight, prefWidth, prefHeight, false);
                    if ((tempHeight+prefHeight) < height) {
                        numVisible++;
                        bottomItem = item;
                    }
                    tempHeight += prefHeight + HGAP;
                } else if (height > 0) {
                    continueDisplay = false;
    	            lastOffBottom.addElement(item);
                }
            }
        }
	    if (item instanceof TreeNode) {
	        TreeNode node = (TreeNode)item;
	        if (visible && !node.isExpanded()) {
	            visible = false;
	        }
	        curX += INDENT;
	        int count = node.getItemCount();
	        if (continueDisplay) {
	            TreeItem subitem = null;
    	        for (int i = 0; i < count; i++) {
    	            subitem = node.getItem(i);
    	            if (continueDisplay) {
            	        layoutItem(subitem, visible, curX);
            	        if (!continueDisplay) {
            	            if (i < count-1) {
                	            subitem = node.getItem(i);
                	            lastOffBottom.addElement(subitem);
                	        }
            	        }
            	    } else {
            	        numDisplayed += subitem.getVisibleCount();
            	    }
        	    }
        	}
        	else if (visible) {
        	    numDisplayed += node.getVisibleCount();
        	}
    	}
	}

    private void drawItemLines(GadgetGraphics g, int idx) {
        TreeItemPanel itemPanel = (TreeItemPanel)gadgets[idx];
        TreeItem item = itemPanel.treeItem;
        int level = item.getLevel();
        if (level > 1) {
            int rightX = itemPanel.x;
            int leftX = (rightX - INDENT) + (CONTROL_WIDTH / 2);
            int lineY = itemPanel.y + (itemPanel.height / 2);
            g.drawLine(leftX+1, lineY, rightX, lineY, 1, 1, 1);

            TreeNode parent = item.getParent();
            int itemIndex = parent.getItemIndex(item);
            if (itemIndex == (parent.getItemCount()-1)) {
                drawLineFromParent(g, parent, leftX, lineY);
            }
        }
    }

    /**
     * drawLineFromParent
     * @param g - TBD
     * @param parent - TBD
     * @param lineX - TBD
     * @param toY - TBD
     */
    public void drawLineFromParent(GadgetGraphics g, TreeNode parent, int lineX, int toY) {
        if (parent == null || parent.getGadget() == null) {
            return;
        }
        TreeItemPanel parentPanel = (TreeItemPanel)(parent.getGadget().getArg());
        if (parentPanel == null) {
            return;
        }
        int parentY = 0;
        if (parentPanel.getParent() == this) {
            parentY = parentPanel.y + parentPanel.control.y + parentPanel.control.getBottom();
        }
        g.drawLine(lineX, parentY, lineX, toY, 1, 1, 1);
    }

    /**
     * paintLines
     * @param g - TBD
     */
    public void paintLines(GadgetGraphics g) {
        for (int i = 0; i < gadgetCount; i++) {
            drawItemLines(g, i);
        }
        Insets insets = getInsets();
        int endY = height;
        int size = lastOffBottom.size();
        for (int i = 0; i < size; i++) {
            TreeItem item = (TreeItem)lastOffBottom.elementAt(i);
            int level = item.getLevel();
            int lineX = insets.left + ((level-2) * INDENT) + (CONTROL_WIDTH/2);
            TreeNode parent = item.getParent();
            drawLineFromParent(g, item.getParent(), lineX, endY);
        }
    }

	/**
	 * clear
	 * @param g - TBD
	 */
	public void clear(GadgetGraphics g) {
	    synchronized(getTreeLock()) {
    	    super.clear(g);
    	    if (this.root != null) {
                g.setColor(getForeground(g));
    	        paintLines(g);
        	}
    	    else if (hasFocus()) {
    	        drawFocus(g,getForeground(g),1);
    	    }
    	}
	}

    /**
     * getSelectedTreeItem
     * @return TreeItem
     */
    public TreeItem getSelectedTreeItem() {
        return selectedItem == null ? null : selectedItem.treeItem;
    }

    /**
     * select
     * @param item - TBD
     */
    public void select(TreeItem item) {
        select(item, null);
    }

    /**
     * select
     * @param item - TBD
     * @param e - TBD
     */
    public void select(TreeItem item, AWTEvent e) {
        TreeItemPanel itemPanel = (TreeItemPanel)(item.getGadget().getParent());
        if (selectedItem != null) {
            selectedItem.setSelected(false);
        }
        selectedItem = itemPanel;
        if (selectedItem != null) {
            selectedItem.setSelected(true);
        }
	    selectionChanged(e);
//	    repaint();
    }

	/**
	 * selectionChanged
	 * @param event - TBD
	 */
	public void selectionChanged( AWTEvent event ) {
	    Event evt;
	    if ( event != null ) {
            evt = event.getEvent();
        }
        else {
            evt = new Event( this, -1, null );
        }
	    TreeSelectable source = this;
	    if (parent instanceof TreeSelectable) {
	        source = (TreeSelectable)parent;
	    } else if (parent != null && parent.parent instanceof TreeSelectable) {
	        source = (TreeSelectable)parent.parent;
	    }

	    int x = 0;
	    int y = 0;
	    if (event != null) {
            Gadget g = (Gadget) event.getSource();
            Point suboffset = g.getOffset();
            Point offset = getOffset();
            x = suboffset.x-offset.x;
            y = suboffset.y-offset.y;
            if (event instanceof MouseEvent) {
                x += ((MouseEvent)event).getX();
                y += ((MouseEvent)event).getY();
            }
        }

	    processEvent( new TreeEvent( source, evt,
	                                 TreeEvent.TREE_STATE_CHANGED,
	                                 TreeEvent.SELECTED,
	                                 getSelectedTreeItem(),
	                                 x, y ) );
	}

	/**
	 * Adds the specified listener to be notified when component
	 * events occur on this component.
	 *
	 * @param l 	the listener to receive the events
	 */
	public synchronized void addActionListener(ActionListener l) {
        actionListener = GWTEventMulticaster.add(actionListener, l);
	}

	/**
	 * Removes the specified listener so it no longer receives
	 * action events on this action.
	 *
	 * @param l 		the listener to remove
	 */
	public synchronized void removeActionListener(ActionListener l) {
        actionListener = GWTEventMulticaster.remove(actionListener, l);
	}

	/**
	 * Adds the specified listener to be notified when component
	 * events occur on this component.
	 *
	 * @param l 	the listener to receive the events
	 */
	public synchronized void addTreeListener(TreeListener l) {
        treeListener = GWTEventMulticaster.add(treeListener, l);
	}

	/**
	 * Removes the specified listener so it no longer receives
	 * tree events on this tree.
	 *
	 * @param l 		the listener to remove
	 */
	public synchronized void removeTreeListener(TreeListener l) {
        treeListener = GWTEventMulticaster.remove(treeListener, l);
	}

	/**
	 * processEvent
	 *
	 * @param e		a TreeEvent
	 * @return boolean result
	 */
	protected void processEvent(AWTEvent e) {
		if (e instanceof TreeEvent) {
		    processTreeEvent((TreeEvent)e);
		} else if (e instanceof ActionEvent) {
		    processActionEvent((ActionEvent)e);
		} else {
		    super.processEvent(e);
		}
	}

	protected void processTreeEvent(TreeEvent e) {
	    TreeListener treeListener = this.treeListener;
		if (treeListener != null) {
    	    switch(e.getID()) {
    			case TreeEvent.TREE_STATE_CHANGED:
        			treeListener.treeStateChanged(e);
        			break;
    			case TreeEvent.TREE_NODE_EXPANDED:
        			treeListener.treeNodeExpanded(e);
        			break;
    			case TreeEvent.TREE_NODE_CONDENSED:
        			treeListener.treeNodeCondensed(e);
        			break;
    	    }
    	}
	}

	protected void processActionEvent(ActionEvent e) {
	    ActionListener actionListener = this.actionListener;
	    if (actionListener != null) {
    	    switch(e.getID()) {
    			case ActionEvent.ACTION_PERFORMED:
        			actionListener.actionPerformed(e);
        			break;
    		}
    	}
	}

	/**
	 * invokeAction
	 * @param e - TBD
	 */
	public void invokeAction(AWTEvent e) {
        TreeItem item = getSelectedTreeItem();
        if (item != null && item instanceof TreeNode && expandingOnAction) {
            TreeNode node = (TreeNode)item;
            node.setExpanded(!node.isExpanded());
        }
	    TreeSelectable source = this;
	    if (parent instanceof TreeSelectable) {
	        source = (TreeSelectable)parent;
	    } else if (parent != null && parent.parent instanceof TreeSelectable) {
	        source = (TreeSelectable)parent.parent;
	    }
	    ActionEvent action = new ActionEvent(source, e.getEvent(),
    		                              ActionEvent.ACTION_PERFORMED);
		processEvent(action);
		if (!action.isConsumed()) {
	        if (!ignoreDefaultAction) {
    	        GadgetShell shell = getShell();
    	        if ( shell != null ) {
    	            Gadget defaultGadget = shell.getDefaultGadget();
    	            if ( defaultGadget != null ) {
    	                defaultGadget.doDefaultAction(e);
    	            }
    	        }
    	    }
	    }
	}

	/**
	 * invokeExpansion
	 * @param the node that was expanded or condensed
	 */
	public void invokeExpansion(TreeNode node) {
	    TreeSelectable source = this;
	    if (parent instanceof TreeSelectable) {
	        source = (TreeSelectable)parent;
	    } else if (parent != null && parent.parent instanceof TreeSelectable) {
	        source = (TreeSelectable)parent.parent;
	    }
	    int reason;
	    if (node.isExpanded()) {
	        reason = TreeEvent.TREE_NODE_EXPANDED;
	    } else {
	        reason = TreeEvent.TREE_NODE_CONDENSED;
	    }
	    int state;
	    if (node.isSelected()) {
	        state = TreeEvent.SELECTED;
	    } else {
	        state = TreeEvent.DESELECTED;
	    }
	    processEvent( new TreeEvent( source, reason, state, node ) );
	    invalidate();
	}

    /**
     * mouseClicked- Empty method
     * mouseClicked
     * @param e - TBD
     */
    public void mouseClicked( MouseEvent e ) {
    }

	/**
	 * Handles mouse pressed events.
	 * @param e - a MouseEvent (pressed)
	 */
    public void mousePressed( MouseEvent e ) {
        select(((TreeItemPanel)e.getSource()).treeItem, e);
        if (e.getClickCount() == 2) {
            invokeAction(e);
        }
    }

	/**
	 * Handles mouse released events.
	 * @param e - a MouseEvent (released)
	 */
    public void mouseReleased( MouseEvent e ) {

    }

	/**
	 * keyTyoed - Empty method
	 * @param e - TBD
	 */
 	public void keyTyped(KeyEvent e) {
	}

	/**
	 * keyReleased - Empty method
	 * @param e - TBD
	 */
 	public void keyReleased(KeyEvent e) {
	}

    /**
     * processKeyEvent
     * @param e	the KeyEvent
     */
    protected void processKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_PRESSED) {
            boolean handled = false;
            switch ( e.getKeyCode() ) {
                case KeyEvent.UP:
                    handled = moveSelectionUp(e);
                    break;
                case KeyEvent.DOWN:
                    handled = moveSelectionDown(e);
                    break;
                case KeyEvent.LEFT:
                    handled = handleLeftKey(e);
                    break;
                case KeyEvent.RIGHT:
                    handled = handleRightKey(e);
                    break;
                case KeyEvent.ENTER:
                    invokeAction(e);
                    handled = true;
                    break;
            }
            if (handled) {
                e.consume();
            }
        } else if (e.getID() == KeyEvent.KEY_RELEASED) {
        }
        super.processKeyEvent(e);
    }

    private boolean moveSelectionUp(AWTEvent e) {
        synchronized(getTreeLock()) {
            if (selectedItem != null) {
                TreeItem selected = selectedItem.treeItem;
                if (selected != root) {
                    if (selected == topItem) {
                        setTopIndex(topIndex-1);
                        doLayout();
                    }
                    TreeNode parent = selected.getParent();
                    int idx = parent.getItemIndex(selected);
                    if (idx == 0) {
                        select(parent,e);
                    } else {
                        TreeItem next = parent.getItem(idx-1);
                        TreeNode node;
                        while(next instanceof TreeNode &&
                              (node = (TreeNode)next).isExpanded() &&
                              node.getItemCount() > 0) {
                            next = node.getItem(node.getItemCount()-1);
                        }
                        select(next,e);
                    }
                }
            }
        }
        return true;
    }

    private boolean moveSelectionDown(AWTEvent e) {
        synchronized(getTreeLock()) {
            if (selectedItem != null) {
                TreeItem selected = selectedItem.treeItem;
                boolean atBottom = (selected == bottomItem);
                TreeNode node;
                if (selected instanceof TreeNode &&
                    (node = (TreeNode)selected).isExpanded() &&
                    node.getItemCount() > 0) {
                    select(node.getItem(0),e);
                    if (atBottom) {
                        scrollUp();
                    }
                    return true;
                }
                TreeNode parent = selected.getParent();
                while (parent != null) {
                    int idx = parent.getItemIndex(selected);
                    if (idx < parent.getItemCount()-1) {
                        select(parent.getItem(idx+1),e);
                        if (atBottom) {
                            scrollUp();
                        }
                        return true;
                    }
                    selected = parent;
                    parent = selected.getParent();
                }
            }
            return true;
        }
    }

    private void scrollUp() {
        do {
            setTopIndex(topIndex+1);
            doLayout();
        } while (bottomItem != selectedItem.treeItem);
    }

    private boolean handleRightKey(AWTEvent e) {
        synchronized(getTreeLock()) {
            if (selectedItem != null) {
                TreeItem selected = selectedItem.treeItem;
                TreeNode node;
                if (selected instanceof TreeNode &&
                    !(node = (TreeNode)selected).isExpanded()) {
                    node.setExpanded(true);
                } else {
                    moveSelectionDown(e);
                }
            }
        }
        return true;
    }

    private boolean handleLeftKey(AWTEvent e) {
        synchronized(getTreeLock()) {
            if (selectedItem != null) {
                TreeItem selected = selectedItem.treeItem;
                TreeNode node;
                if (selected instanceof TreeNode &&
                    (node = (TreeNode)selected).isExpanded()) {
                    node.setExpanded(false);
                } else {
                    moveSelectionUp(e);
                }
            }
        }
        return true;
    }

	/**
	 * mouseEntered - Empty method
	 * @param e - TBD
	 */
    public void mouseEntered( MouseEvent e ) {
    }

	/**
	 * mouseExited - Empty method
	 * @param e - TBD
	 */
    public void mouseExited( MouseEvent e) {
    }

	/**
	 * processFocusEvent
	 * @param e		the firing FocusEvent
	 */
	protected void processFocusEvent(FocusEvent e) {
        if (e.getID() == FocusEvent.FOCUS_GAINED) {
    	    if (selectedItem == null && root != null) {
    	        selectedItem = (TreeItemPanel)(root.getGadget().getParent());
    	    }
    	    if (selectedItem != null) {
    	        selectedItem.setSelected(true);
    	    }
    	    if (root == null) {
    	        repaint();
    	    }
        } else {
    	    if (selectedItem != null) {
    	        selectedItem.setSelected(false);
    	    }
    	    if (root == null) {
    	        repaint();
    	    }
        }
	    super.processFocusEvent( e );
	}
}

/**
 * TreeItemPanel
 * @version 1.1
 * @author DTAI, Incorporated
 */
class TreeItemPanel extends PanelGadget {

    protected TreeControl control;
    protected TreeItem treeItem;
    private Gadget gadget;
    protected boolean added;

    /**
     * TreeItemPanel
     * @param treeItem - TBD
     */
    public TreeItemPanel(TreeItem treeItem) {
        this.treeItem = treeItem;
        setBackground(Gadget.transparent);
        setSelectedBackground(Gadget.transparent);
        if (treeItem instanceof TreeNode) {
            setLayout(new GadgetBorderLayout(6,0));
        } else {
            setLayout(new GadgetBorderLayout());
        }
        gadget = treeItem.getGadget();
        gadget.setArg(this);
        add("Center", gadget);
        if (treeItem instanceof TreeNode) {
            add("West", control = new TreeControl((TreeNode)treeItem));
        }
    }

    public String getHelpId() {
        String ghelp = gadget.helpId;
        if (ghelp != null) {
            return ghelp;
        }
        return super.getHelpId();
    }

    /**
     * hasFocus
     * @return boolean
     */
    public boolean hasFocus() {
        return false;
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
	    super.repaint(x,y,width,height,setPaintFlag,forced);
	    ContainerGadget parent = this.parent;
	    if (parent != null) {
    	    parent.repaint(this.x+x-TreePanel.INDENT,this.y+y,
    	                   width+TreePanel.INDENT,height);
    	}
    }
}

/**
 * TreeControl
 * @version 1.1
 * @author DTAI, Incorporated
 */
class TreeControl extends CanvasGadget {

    private TreeNode node;

    public TreeControl(TreeNode node) {
        this.node = node;
    }

    public Dimension getPreferredSize() {
        pref_width = TreePanel.CONTROL_WIDTH;
        pref_height = TreePanel.CONTROL_WIDTH;
        return new Dimension(pref_width, pref_height);
    }

    /**
     * clear
     * @param g - TBD
     */
    public void clear(GadgetGraphics g) {
        g.setColor(getNormalForeground(g));
    }

    private static final int mid_width = 4;
    private static final int left = 2;
    private static final int right = 6;

    /**
     * paint
     * @param g - TBD
     */
    public void paint(GadgetGraphics g) {
        int mid_height = (height / 2);
        int top = mid_height - 2;
        int bottom = mid_height + 2;

        g.drawLine( left, mid_height, right, mid_height );
        if (!node.isExpanded()) {
            g.drawLine( mid_width, top, mid_width, bottom );
        }
        g.drawRect(left-2, top-2, TreePanel.CONTROL_WIDTH-1, TreePanel.CONTROL_WIDTH-1);
    }

    /**
     * getBottom
     * @return int
     */
    public int getBottom() {
        return (height / 2) + 4;
    }

    /**
     * processMouseEvent
     * @param mouse the MouseEvent
     */
    public void processMouseEvent( MouseEvent e ) {
        if (e.getID() == MouseEvent.MOUSE_PRESSED) {
            node.setExpanded(!node.isExpanded());
            TreePanel panel = node.getTreePanel();
            if (panel != null) {
                panel.select(node,e);
            }
            repaint();
      	}
        e.consume();
        super.processMouseEvent( e );
    }
}
