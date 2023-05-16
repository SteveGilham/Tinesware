/****************************************************************
 **
 **  $Id: TreeFolder.java,v 1.11 1998/03/10 20:30:42 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/TreeFolder.java,v $
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

import java.awt.Image;
import java.awt.image.MemoryImageSource;
import java.util.Vector;

/**
 * Objects of the TreeFolder class hold the pertinent information
 * about TreeViewGadget objects, such as name, attributes, etc.
 *
 * @version 1.1
 * @author		DTAI, Incorporated
 */
public class TreeFolder extends FolderItem implements TreeNode {

    private static final int OPEN_WIDTH = 16;
    private static final int OPEN_HEIGHT = 13;
    private static final int OPEN_PIXELS[] = {
        0xc0c0c0, 0xc0c0c0, 0xc0c0c0, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xc0c0c0, 0xc0c0c0, 0xc0c0c0, 0xc0c0c0, 0xc0c0c0, 0xc0c0c0, 0xc0c0c0, 0xc0c0c0,
        0xc0c0c0, 0xc0c0c0, 0xff868686, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xff868686, 0xc0c0c0, 0xc0c0c0, 0xc0c0c0, 0xc0c0c0, 0xc0c0c0, 0xc0c0c0, 0xc0c0c0,
        0xc0c0c0, 0xff868686, 0xffffffff, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffffffff, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xc0c0c0,
        0xc0c0c0, 0xff868686, 0xffffffff, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xff868686, 0xff000000,
        0xc0c0c0, 0xff868686, 0xffffffff, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xff868686, 0xff000000,
        0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xffcccc33, 0xff868686, 0xff000000,
        0xff868686, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xff868686, 0xff000000, 0xc0c0c0, 0xff868686, 0xff000000,
        0xff868686, 0xffffffff, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xff000000, 0xff868686, 0xff868686, 0xff000000,
        0xc0c0c0, 0xff868686, 0xffffffff, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xff868686, 0xff000000, 0xff868686, 0xff000000,
        0xc0c0c0, 0xff868686, 0xffffffff, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xff000000, 0xff868686, 0xff000000,
        0xc0c0c0, 0xc0c0c0, 0xff868686, 0xffffffff, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xff868686, 0xff000000, 0xff000000,
        0xc0c0c0, 0xc0c0c0, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff000000, 0xff000000,
        0xc0c0c0, 0xc0c0c0, 0xc0c0c0, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000,
    };

    private static final int CLOSED_WIDTH = 16;
    private static final int CLOSED_HEIGHT = 13;
    private static final int CLOSED_PIXELS[] = {
        0xc0c0c0, 0xc0c0c0, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xc0c0c0, 0xc0c0c0, 0xc0c0c0, 0xc0c0c0, 0xc0c0c0, 0xc0c0c0, 0xc0c0c0, 0xc0c0c0, 0xc0c0c0,
        0xc0c0c0, 0xff868686, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xff868686, 0xc0c0c0, 0xc0c0c0, 0xc0c0c0, 0xc0c0c0, 0xc0c0c0, 0xc0c0c0, 0xc0c0c0, 0xc0c0c0,
        0xff868686, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xc0c0c0, 0xc0c0c0,
        0xff868686, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xffffffff, 0xff868686, 0xff000000, 0xc0c0c0,
        0xff868686, 0xffffffff, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xff868686, 0xff000000, 0xc0c0c0,
        0xff868686, 0xffffffff, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xff868686, 0xff000000, 0xc0c0c0,
        0xff868686, 0xffffffff, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xff868686, 0xff000000, 0xc0c0c0,
        0xff868686, 0xffffffff, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xff868686, 0xff000000, 0xc0c0c0,
        0xff868686, 0xffffffff, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xff868686, 0xff000000, 0xc0c0c0,
        0xff868686, 0xffffffff, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xff868686, 0xff000000, 0xc0c0c0,
        0xff868686, 0xffffffff, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xffcccc33, 0xff868686, 0xff000000, 0xc0c0c0,
        0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff868686, 0xff000000, 0xc0c0c0,
        0xc0c0c0, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xff000000, 0xc0c0c0,
    };

    private static Image openFolder;
    private static Image closedFolder;

    private Vector items = new Vector();
    private Image expandedImage;
    private Image condensedImage;
    private boolean expanded;
    private int visibleCount;

    /**
     * TreeFolder
     */
    public TreeFolder() {
        this("");
    }

    /**
     * TreeFolder
     * @param label - TBD
     */
    public TreeFolder(String label) {
        this(label,null,null);
        Gadget g = getGadget();
        setImages(getClosedFolder(g), getOpenFolder(g));
    }

    /**
     * TreeFolder
     * @param label - TBD
     * @param condensedImage - TBD
     * @param expandedImage - TBD
     */
    public TreeFolder(String label, Image condensedImage, Image expandedImage) {
        super(label);
        setImages(condensedImage, expandedImage);
        visibleCount = 1;
    }

	private Image getClosedFolder(Gadget g) {
        synchronized(getTreeLock()) {
    	    if ( closedFolder == null ) {
                closedFolder = g.createImage(
                    new MemoryImageSource ( CLOSED_WIDTH, CLOSED_HEIGHT, CLOSED_PIXELS,
                                            0, CLOSED_WIDTH ) );
            }

            return closedFolder;
        }
	}

	private Image getOpenFolder(Gadget g) {
        synchronized(getTreeLock()) {
    	    if ( openFolder == null ) {
                openFolder = g.createImage(
                    new MemoryImageSource ( OPEN_WIDTH, OPEN_HEIGHT, OPEN_PIXELS,
                                            0, OPEN_WIDTH ) );
            }

            return openFolder;
        }
	}

    /**
     * setImages
     * @param condensedImage - TBD
     * @param expandedImage - TBD
     */
    public void setImages(Image condensedImage, Image expandedImage) {
        if (condensedImage != this.condensedImage ||
            expandedImage != this.expandedImage) {
            this.condensedImage = condensedImage;
            this.expandedImage = expandedImage;
            if (expanded) {
                setImage(expandedImage);
            } else {
                setImage(condensedImage);
            }
        }
    }

    /**
     * getCondensedImage
     * @return Image
     */
    public Image getCondensedImage() {
        return condensedImage;
    }

    /**
     * getExpandedImage
     * @return Image
     */
    public Image getExpandedImage() {
        return expandedImage;
    }

    /**
     * add
     * @param child - TBD
     */
    public void add(TreeItem child) {
        synchronized(getTreeLock()) {
            items.addElement(child);
            child.setParent(this);
            if (child.isShowing()) {
                alterVisibleCount(child.getVisibleCount());
                redraw();
            }
        }
    }

    /**
     * addFolder
     * @param label - TBD
     * @return TreeFolder
     */
    public TreeFolder addFolder(String label) {
        TreeFolder folder = new TreeFolder(label);
        add(folder);
        return folder;
    }

    /**
     * addItem
     * @param label - TBD
     * @return FolderItem
     */
    public FolderItem addItem(String label) {
        FolderItem folder = new FolderItem(label);
        add(folder);
        return folder;
    }

    /**
     * insert
     * @param child - TBD
     * @param pos - TBD
     */
    public void insert(TreeItem child, int pos) {
        synchronized(getTreeLock()) {
            items.insertElementAt(child,pos);
            child.setParent(this);
            if (child.isShowing()) {
                alterVisibleCount(child.getVisibleCount());
                redraw();
            }
        }
    }

    /**
     * insertFolder
     * @param label - TBD
     * @param pos - TBD
     * @return TreeFolder
     */
    public TreeFolder insertFolder(String label, int pos) {
        TreeFolder folder = new TreeFolder(label);
        insert(folder, pos);
        return folder;
    }

    /**
     * insertItem
     * @param label - TBD
     * @param pos - TBD
     * @return FolderItem
     */
    public FolderItem insertItem(String label, int pos) {
        FolderItem folder = new FolderItem(label);
        insert(folder, pos);
        return folder;
    }

    /**
     * remove
     * @param child - TBD
     */
    public void remove(TreeItem child) {
        synchronized(getTreeLock()) {
            if (child.isShowing()) {
                alterVisibleCount(-child.getVisibleCount());
                redraw();
            }
            items.removeElement(child);
            child.setParent(null);
        }
    }

    /**
     * remove
     * @param pos - TBD
     */
    public void remove(int pos) {
        synchronized(getTreeLock()) {
            TreeItem child = (TreeItem)items.elementAt(pos);
            if (child.isShowing()) {
                alterVisibleCount(-child.getVisibleCount());
                redraw();
            }
            items.removeElementAt(pos);
            child.setParent(null);
        }
    }

    /**
     * removeAll
     */
    public void removeAll() {
        synchronized(getTreeLock()) {
            int count = getItemCount();
            if (count > 0 && isShowing() && isExpanded()) {
                alterVisibleCount(1-getVisibleCount());
                redraw();
            }
            for (int i = 0; i < count; i++) {
                getItem(i).setParent(null);
            }
            items.removeAllElements();
        }
    }

    /**
     * getItemCount
     * @return int
     */
    public final int getItemCount() {
        return items.size();
    }

    /**
     * change the number of visible items
     * @param change the number by which the visible descendent count changes
     */
    public final void alterVisibleCount(int change) {
        visibleCount += change;
        TreeNode parent = getParent();
        if (parent != null) {
            parent.alterVisibleCount(change);
        }
    }

    /**
     * get the number of visible items including this item and any visible
     * descendents
     * @return int
     */
    public final int getVisibleCount() {
        return visibleCount;
    }

    /**
     * getItems
     * @return TreeItem[]
     */
    public TreeItem[] getItems() {
        synchronized(getTreeLock()) {
            int count = getItemCount();
            TreeItem[] itemarray = new TreeItem[count];
            for (int i = 0; i < count; i++) {
                itemarray[i] = getItem(i);
            }
            return itemarray;
        }
    }

    /**
     * getItem
     * @param i - TBD
     * @param TreeItem - TBD
     */
    public final TreeItem getItem(int i) {
        return (TreeItem)items.elementAt(i);
    }

    /**
     * getItemIndex
     * @param item - TBD
     * @return int
     */
    public int getItemIndex(TreeItem item) {
        return items.indexOf(item);
    }

    /**
     * setExpanded
     * @param expanded - TBD
     */
    public void setExpanded(boolean expanded) {
        if (expanded != this.expanded) {
            this.expanded = expanded;
            if (expanded) {
                int count = getItemCount();
                for (int i = 0; i < count; i++) {
                    alterVisibleCount(getItem(i).getVisibleCount());
                }
                setImage(expandedImage);
            } else {
                alterVisibleCount(1-getVisibleCount());
                setImage(condensedImage);
            }
            getGadget().invalidate();
            TreePanel panel = getTreePanel();
            if (panel != null) {
                panel.invokeExpansion(this);
            }
        }
    }

    /**
     * isExpanded
     * @return boolean
     */
    public boolean isExpanded() {
        return expanded;
    }
}
