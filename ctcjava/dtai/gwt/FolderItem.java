/****************************************************************
 **
 **  $Id: FolderItem.java,v 1.18 1997/10/02 01:15:40 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/FolderItem.java,v $
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
import java.awt.Color;
import java.awt.Image;
import java.util.Vector;

/**
 * Objects of the FolderItem class hold the pertinent information
 * about TreeViewGadget objects, such as name, attributes, etc.
 *
 * @version 1.1
 * @author	  DTAI, Incorporated
 */
public class FolderItem implements TreeItem {

    private FolderItem root;
    private Object parent;
    private PanelGadget gadget;
    private LabelGadget labelGadget;
    private ImageGadget imageGadget;
	private Object arg;

    /**
     * FolderItem
     */
    public FolderItem() {
        this("",null);
    }

    /**
     * FolderItem
     * @param label	- label of the FolderItem
     */
    public FolderItem(String label) {
        this(label,null);
    }

    /**
     * FolderItem
     * @param label	- label of the FolderItem
     * @param image	- image for the FolderItem
     */
    public FolderItem(String label, Image image) {
        root = this;
        imageGadget = new ImageGadget();
        labelGadget = new LabelGadget();

        gadget = new FolderItemPanel(this, imageGadget, labelGadget);

        setLabel(label);
        setImage(image);
    }

    /**
     * Returns arg.
     * @return arg.
     */
	public final Object getArg() {
        return arg;
    }

    /**
     * Sets arg.
     * @param arg	new value for arg
     */
    public final void setArg( Object arg ) {
        this.arg = arg;
    }

    /**
     * getTreeLock
     * @return Object
     */
    public final Object getTreeLock() {
        Object lock = getTreePanel();
        return lock == null ? root : lock;
    }

    /**
     * Sets the label of the labelGadget.
     * @param label		label of the labelGadget
     */
    public void setLabel(String label) {
        labelGadget.setLabel(label);
    }

    /**
     * gets the label of the labelGadget
     * @return label of the labelGadget
     */
    public String getLabel() {
        return labelGadget.getLabel();
    }

    /**
     * gets the label of the labelGadget
     * @return label of the labelGadget
     */
    public String toString() {
        return getLabel();
    }

    /**
     * sets the image of the imageGadget
     * @param image - image of the imageGadget
     */
    public void setImage(Image image) {
        imageGadget.setImage(image);
    }

    /**
     * gets the image of the imageGadget
     * @return image of the imageGadget
     */
    public Image getImage() {
        return imageGadget.getImage();
    }

    /**
     * sets the parent of the item
     * @param parent	parent of this item
     */
    public void setParent(TreeNode parent) {
        synchronized(getTreeLock()) {
            if (parent != this.parent) {
                this.parent = parent;
                if (parent == null) {
                    root = this;
                } else {
                    root = (FolderItem)((TreeFolder)parent).getRoot();
                }
            }
        }
    }

    /**
     * gets the parent object of the item
     * @return the parent object, if it is a TreeNode object, else null
     */
    public final TreeNode getParent() {
        if (parent instanceof TreeNode) {
            return (TreeNode)parent;
        }
        return null;
    }

    /**
     * get the number of visible items including this item and any visible
     * descendents
     * @return int
     */
    public int getVisibleCount() {
        return isShowing() ? 1 : 0;
    }

    /**
     * gets the level of the item on the tree of TreeNodes
     * @return what level this item is on in the tree of TreeNodes
     */
    public int getLevel() {
        if (parent instanceof TreeNode) {
            return ((TreeNode)parent).getLevel()+1;
        }
        return 1;
    }

    /**
     * gets the root of the TreeNode tree
     * @return root of TreeNode tree
     */
    public final TreeItem getRoot() {
        return root;
    }

    /**
     * sets the panel this tree is on
     * @param treePanel	Panel this tree is on (parent of the root of this tree)
     */
    public void setTreePanel(TreePanel treePanel) {
        if (treePanel != root.parent) {
            root.parent = treePanel;
        }
    }

    /**
     * gets the panel this item is on
     * @return TreePanel this item is on (the parent of the root of this tree)
     */
    public final TreePanel getTreePanel() {
        return (TreePanel)root.parent;
    }

    /**
     * redraw
     */
    public void redraw() {
        TreePanel treePanel = getTreePanel();
        if (treePanel != null) {
            treePanel.invalidate();
        }
    }

    /**
     * returns if the item is/is not selected
     * @return this item is/is not selected
     */
    public boolean isSelected() {
        if (getTreePanel().getSelectedTreeItem() == this) {
            return true;
        }
        return false;
    }

    /**
     * isShowing
     * @return true if there is no parent, or if the parent and ancestors are expanded
     */
    public boolean isShowing() {
        if (this == root || parent == null) {
            return true;
        }
        return ((TreeNode)parent).isExpanded() &&
               ((TreeNode)parent).isShowing();
    }

    /**
     * getGadget
     * @return Gadget
     */
    public Gadget getGadget() {
        return gadget;
    }
}

/**
 * FolderItemPanel
 * @version 1.1
 * @author   DTAI, Incorporated
 */
class FolderItemPanel extends PanelGadget {

    private FolderLabelBorder labelBorder;
    private LabelGadget labelGadget;
    private ImageGadget imageGadget;

    /**
     * FolderItemPanel
     * @param item
     * @param imageGadget
     * @param labelGadget
     */
    public FolderItemPanel(FolderItem item, ImageGadget imageGadget,
                           LabelGadget labelGadget) {
        this.imageGadget = imageGadget;
        this.labelGadget = labelGadget;
        setLayout(null);

        labelBorder = new FolderLabelBorder(item);

        add(imageGadget);
        add(labelBorder);
        labelBorder.add("Center", labelGadget);
    }

	/**
     * returns true if children can overlap each other.
     * @return false
	 */
	public boolean childrenCanOverlap() {
	    return false;
	}

    /**
     * gets the preferred dimensions
     * @return Dimension
     */
    public Dimension getPreferredSize() {
        int pw = pref_width;
        int ph = pref_height;
        if (pw < 0 || ph < 0) {
            synchronized(getTreeLock()) {
                pw = 0;
                ph = 0;
                if (imageGadget.getImage() != null) {
                    int cw = imageGadget.pref_width;
                    int ch = imageGadget.pref_height;
                    if (cw < 0 || ch < 0) {
                        Dimension pref = imageGadget.getPreferredSize();
                        cw = pref.width;
                        ch = pref.height;
                    }
                    ph = ch;
                    pw += cw + 4;
                }
                int cw = labelBorder.pref_width;
                int ch = labelBorder.pref_height;
                if (cw < 0 || ch < 0) {
                    Dimension pref = labelBorder.getPreferredSize();
                    cw = pref.width;
                    ch = pref.height;
                }
                ph = Math.max(ph,ch);
                pw += cw;
                pref_width = pw;
                pref_height = ph;
            }
        }
        return new Dimension(pw,ph);
    }

    /**
     * doLayout
     */
    public void doLayout() {
        synchronized(getTreeLock()) {
            int curX = 0;
            if (imageGadget.getImage() != null) {
                int pw = imageGadget.pref_width;
                int ph = imageGadget.pref_height;
                if (pw < 0 || ph < 0) {
                    Dimension pref = imageGadget.getPreferredSize();
                    pw = pref.width;
                    ph = pref.height;
                }
                imageGadget.setBounds(curX,0,pw,height,false);
                curX += pw + 4;
            }
            int pw = labelBorder.pref_width;
            int ph = labelBorder.pref_height;
            if (pw < 0 || ph < 0) {
                Dimension pref = labelBorder.getPreferredSize();
                pw = pref.width;
                ph = pref.height;
            }
            labelBorder.setBounds(curX,0,pw,height,false);
        }
    }

    public String toString() {
        return labelGadget.toString();
    }
}

/**
 * FolderLabelBorder
 * @version 1.1
 * @author   DTAI, Incorporated
 */
class FolderLabelBorder extends BorderGadget {

    private FolderItem item;

    public FolderLabelBorder(FolderItem item) {
        this.item = item;
        setMargins(2);
        setFocusThickness(1);
        setSelectedBackground(Gadget.defaultSelectedBackground);
    }

    public void paint(GadgetGraphics g) {
        super.paint(g);
        TreePanel treePanel = item.getTreePanel();
        if (treePanel != null &&
            treePanel.getSelectedTreeItem() == item &&
            treePanel.hasFocus()) {
            drawFocus(g);
        }
    }
}
