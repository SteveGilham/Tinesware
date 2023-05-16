/****************************************************************
 **
 **  $Id: GadgetGridBagLayout.java,v 1.16 1997/11/27 02:13:07 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/GadgetGridBagLayout.java,v $
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
/* DERIVED FROM...
 *
 * @(#)GridBagConstraints.java	1.5 95/12/01 Doug Stein
 *
 * Copyright (c) 1995 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

package dtai.gwt;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Hashtable;

/** GadgetGridBagLayout
 * @version 1.1
 * @author DTAI, Incorporated
 */
class GadgetGridBagLayoutInfo {
  int width, height;		/* number of cells horizontally, vertically */
  int startx, starty;		/* starting point for layout */
  int minWidth[];		/* largest minWidth in each column */
  int minHeight[];		/* largest minHeight in each row */
  double weightX[];		/* largest weight in each column */
  double weightY[];		/* largest weight in each row */

  GadgetGridBagLayoutInfo () {
    minWidth = new int[GadgetGridBagLayout.MAXGRIDSIZE];
    minHeight = new int[GadgetGridBagLayout.MAXGRIDSIZE];
    weightX = new double[GadgetGridBagLayout.MAXGRIDSIZE];
    weightY = new double[GadgetGridBagLayout.MAXGRIDSIZE];
  }
}

/**
    GadgetGridBagLayout is a flexible layout manager
    that aligns components vertically and horizontally,
    without requiring that the components be the same size.
    Each GadgetGridBagLayout uses a rectangular grid of cells,
    with each component occupying one or more cells
    (called its <em>display area</em>).
    Each component managed by a GadgetGridBagLayout
    is associated with a
    <a href=java.awt.GadgetGridBagConstraints.html>GadgetGridBagConstraints</a> instance
    that specifies how the component is laid out
    within its display area.
    How a GadgetGridBagLayout places a set of components
    depends on each component's GadgetGridBagConstraints and minimum size,
    as well as the preferred size of the components' container.
    <p>

    To use a GadgetGridBagLayout effectively,
    you must customize one or more of its components' GadgetGridBagConstraints.
    You customize a GadgetGridBagConstraints object by setting one or more
    of its instance variables:
    <dl>
    <dt> <a href=java.awt.GadgetGridBagConstraints.html#gridx>gridx</a>,
         <a href=java.awt.GadgetGridBagConstraints.html#gridy>gridy</a>
    <dd> Specifies the cell at the upper left of the component's display area,
	 where the upper-left-most cell has address gridx=0, gridy=0.
	 Use GadgetGridBagConstraints.RELATIVE (the default value)
	 to specify that the component be just placed
	 just to the right of (for gridx)
	 or just below (for gridy)
	 the component that was added to the container
	 just before this component was added.
    <dt> <a href=java.awt.GadgetGridBagConstraints.html#gridwidth>gridwidth</a>,
         <a href=java.awt.GadgetGridBagConstraints.html#gridheight>gridheight</a>
    <dd> Specifies the number of cells in a row (for gridwidth)
	 or column (for gridheight)
	 in the component's display area.
	 The default value is 1.
	 Use GadgetGridBagConstraints.REMAINDER to specify
	 that the component be the last one in its row (for gridwidth)
	 or column (for gridheight).
	 Use GadgetGridBagConstraints.RELATIVE to specify
	 that the component be the next to last one
	 in its row (for gridwidth) or column (for gridheight).
    <dt> <a href=java.awt.GadgetGridBagConstraints.html#fill>fill</a>
    <dd> Used when the component's display area
   	 is larger than the component's requested size
	 to determine whether (and how) to resize the component.
	 Valid values are
	      GadgetGridBagConstraint.NONE
	      (the default),
	      GadgetGridBagConstraint.HORIZONTAL
	      (make the component wide enough to fill its display area
	      horizontally, but don't change its height),
	      GadgetGridBagConstraint.VERTICAL
	      (make the component tall enough to fill its display area
	      vertically, but don't change its width),
	      and
	      GadgetGridBagConstraint.BOTH
	      (make the component fill its display area entirely).
    <dt> <a href=java.awt.GadgetGridBagConstraints.html#ipadx>ipadx</a>,
         <a href=java.awt.GadgetGridBagConstraints.html#ipady>ipady</a>
    <dd> Specifies the internal padding:
	 how much to add to the minimum size of the component.
	 The width of the component will be at least
	 its minimum width plus ipadx*2 pixels
	 (since the padding applies to both sides of the component).
	 Similarly, the height of the component will be at least
	 the minimum height plus ipady*2 pixels.
    <dt> <a href=java.awt.GadgetGridBagConstraints.html#insets>insets</a>
    <dd> Specifies the external padding of the component --
	 the minimum amount of space between the component
	 and the edges of its display area.
    <dt> <a href=java.awt.GadgetGridBagConstraints.html#anchor>anchor</a>
    <dd> Used when the component is smaller than its display area
	 to determine where (within the area) to place the component.
	 Valid values are
	 GadgetGridBagConstraints.CENTER (the default),
	 GadgetGridBagConstraints.NORTH,
	 GadgetGridBagConstraints.NORTHEAST,
	 GadgetGridBagConstraints.EAST,
	 GadgetGridBagConstraints.SOUTHEAST,
	 GadgetGridBagConstraints.SOUTH,
	 GadgetGridBagConstraints.SOUTHWEST,
	 GadgetGridBagConstraints.WEST, and
	 GadgetGridBagConstraints.NORTHWEST.
    <dt> <a href=java.awt.GadgetGridBagConstraints.html#weightx>weightx</a>,
         <a href=java.awt.GadgetGridBagConstraints.html#weighty>weighty</a>
    <dd> Used to determine how to distribute space;
	 this is important for specifying resizing behavior.
	 Unless you specify a weight
	 for at least one component in a row (weightx)
	 and column (weighty),
	 all the components clump together in the center of
	 their container.
	 This is because when the weight is zero (the default),
	 the GadgetGridBagLayout puts any extra space
	 between its grid of cells and the edges of the container.
    </dl>

    The following figure shows ten components (all buttons)
    managed by a GadgetGridBagLayout:
    <blockquote>
    <img src=images/java.awt/GadgetGridBagEx.gif width=262 height=155>
    </blockquote>

    All the components have fill=GadgetGridBagConstraints.BOTH.
    In addition, the components have the following non-default constraints:
    <ul>
    <li>Button1, Button2, Button3:
        weightx=1.0
    <li>Button4:
        weightx=1.0,
        gridwidth=GadgetGridBagConstraints.REMAINDER
    <li>Button5:
        gridwidth=GadgetGridBagConstraints.REMAINDER
    <li>Button6:
        gridwidth=GadgetGridBagConstraints.RELATIVE
    <li>Button7:
        gridwidth=GadgetGridBagConstraints.REMAINDER
    <li>Button8:
        gridheight=2, weighty=1.0,
    <li>Button9, Button 10:
        gridwidth=GadgetGridBagConstraints.REMAINDER
    </ul>

    Here is the code that implements the example shown above:
    <blockquote>
    <pre>

public class GadgetGridBagEx1 extends Applet {

    protected void makebutton(String name,
                              GadgetGridBagLayout gridbag,
                              GadgetGridBagConstraints c) {
        Button button = new Button(name);
        gridbag.setConstraints(button, c);
        add(button);
    }

    public void init() {
        GadgetGridBagLayout gridbag = new GadgetGridBagLayout();
        GadgetGridBagConstraints c = new GadgetGridBagConstraints();

        setFont(new Font("Helvetica", Font.PLAIN, 14));
        setLayout(gridbag);

        c.fill = GadgetGridBagConstraints.BOTH;
        c.weightx = 1.0;
        makebutton("Button1", gridbag, c);
        makebutton("Button2", gridbag, c);
        makebutton("Button3", gridbag, c);

	    c.gridwidth = GadgetGridBagConstraints.REMAINDER; //end row
        makebutton("Button4", gridbag, c);

        c.weightx = 0.0;		   //reset to the default
        makebutton("Button5", gridbag, c); //another row

	    c.gridwidth = GadgetGridBagConstraints.RELATIVE; //next-to-last in row
        makebutton("Button6", gridbag, c);

	    c.gridwidth = GadgetGridBagConstraints.REMAINDER; //end row
        makebutton("Button7", gridbag, c);

	    c.gridwidth = 1;	   	   //reset to the default
	    c.gridheight = 2;
        c.weighty = 1.0;
        makebutton("Button8", gridbag, c);

        c.weighty = 0.0;		   //reset to the default
	    c.gridwidth = GadgetGridBagConstraints.REMAINDER; //end row
	    c.gridheight = 1;		   //reset to the default
        makebutton("Button9", gridbag, c);
        makebutton("Button10", gridbag, c);

        resize(300, 100);
    }

    public static void main(String args[]) {
	Frame f = new Frame("GadgetGridBag Layout Example");
	GadgetGridBagEx1 ex1 = new GadgetGridBagEx1();

	ex1.init();

	f.add("Center", ex1);
	f.pack();
	f.resize(f.preferredSize());
	f.setVisible( true );;
    }
}
    </pre>
    </blockquote>
 *
 * @version 1.1
 * @author DTAI, Incorporated
 */
public class GadgetGridBagLayout implements GadgetLayoutManager {

  protected static final int MAXGRIDSIZE = 128;
  protected static final int MINSIZE = 1;
  protected static final int PREFERREDSIZE = 2;

  protected Hashtable gadgettable;
  protected GadgetGridBagConstraints defaultConstraints;
  protected GadgetGridBagLayoutInfo layoutInfo;

  public int columnWidths[];
  public int rowHeights[];
  public double columnWeights[];
  public double rowWeights[];

  boolean useMinimum = false;

  /**
   * Creates a gridbag layout.
   */
  public GadgetGridBagLayout () {
    gadgettable = new Hashtable();
    defaultConstraints = new GadgetGridBagConstraints();
  }

  public void setUseMinimum(boolean useMinimum) {
    this.useMinimum = useMinimum;
  }

  public boolean getUseMinimum() {
    return useMinimum;
  }

  /**
   * returns true if children can overlap each other.
   *
   * @return false
   */
  public boolean childrenCanOverlap() {
      return false;
  }

  /**
   * Sets the constraints for the specified gadget.
   * @param gadget the gadget to be modified
   * @param constraints the constraints to be applied
   */
  public void setConstraints(Gadget gadget, GadgetGridBagConstraints constraints) {
    gadgettable.put(gadget, constraints.clone());
  }

  /**
   * Retrieves the constraints for the specified gadget.  A copy of
   * the constraints is returned.
   * @param gadget the gadget to be queried
   * @return GadgetGridBagConstraints
   */
  public GadgetGridBagConstraints getConstraints(Gadget gadget) {
    GadgetGridBagConstraints constraints = (GadgetGridBagConstraints)gadgettable.get(gadget);
    if (constraints == null) {
      setConstraints(gadget, defaultConstraints);
      constraints = (GadgetGridBagConstraints)gadgettable.get(gadget);
    }
    return (GadgetGridBagConstraints)constraints.clone();
  }

  /**
   * Retrieves the constraints for the specified gadget.  The return
   * value is not a copy, but is the actual constraints class used by the
   * layout mechanism.
   * @param gadget the gadget to be queried
   * @param constraints - TBD
   * @return GadgetGridBagConstraints
   */
  protected GadgetGridBagConstraints lookupConstraints(Gadget gadget) {
    GadgetGridBagConstraints constraints = (GadgetGridBagConstraints)gadgettable.get(gadget);
    if (constraints == null) {
      setConstraints(gadget, defaultConstraints);
      constraints = (GadgetGridBagConstraints)gadgettable.get(gadget);
    }
    return constraints;
  }

  /**
   * getLayoutOrigin
   * @return Point
   */
  public Point getLayoutOrigin () {
    Point origin = new Point(0,0);
    if (layoutInfo != null) {
      origin.x = layoutInfo.startx;
      origin.y = layoutInfo.starty;
    }
    return origin;
  }

  /**
   * gets the layout dimensions
   * @return int [][]
   */
  public int [][] getLayoutDimensions () {
    if (layoutInfo == null)
      return new int[2][0];

    int dim[][] = new int [2][];
    dim[0] = new int[layoutInfo.width];
    dim[1] = new int[layoutInfo.height];

    System.arraycopy(layoutInfo.minWidth, 0, dim[0], 0, layoutInfo.width);
    System.arraycopy(layoutInfo.minHeight, 0, dim[1], 0, layoutInfo.height);

    return dim;
  }

  /**
   * getLayoutWeights
   * @return double [][]
   */
  public double [][] getLayoutWeights () {
    if (layoutInfo == null)
      return new double[2][0];

    double weights[][] = new double [2][];
    weights[0] = new double[layoutInfo.width];
    weights[1] = new double[layoutInfo.height];

    System.arraycopy(layoutInfo.weightX, 0, weights[0], 0, layoutInfo.width);
    System.arraycopy(layoutInfo.weightY, 0, weights[1], 0, layoutInfo.height);

    return weights;
  }

  /**
   * location
   * @param x - TBD
   * @param y - TBD
   * @return Point
   */
  public Point location(int x, int y) {
    Point loc = new Point(0,0);
    int i, d;

    if (layoutInfo == null)
      return loc;

    d = layoutInfo.startx;
    for (i=0; i<layoutInfo.width; i++) {
      d += layoutInfo.minWidth[i];
      if (d > x)
	    break;
    }
    loc.x = i;

    d = layoutInfo.starty;
    for (i=0; i<layoutInfo.height; i++) {
      d += layoutInfo.minHeight[i];
      if (d > y)
	    break;
    }
    loc.y = i;

    return loc;
  }

  /**
   * Adds the specified gadget with the specified name to the layout.
   * @param name the name of the gadget
   * @param gadget the gadget to be added
   */
  public void addLayoutGadget(String name, Gadget gadget) {
  }

  /**
   * Removes the specified gadget from the layout. Does not apply.
   * @param gadget the gadget to be removed
   */
  public void removeLayoutGadget(Gadget gadget) {
  }

  /**
   * Returns the preferred dimensions for this layout given the gadgets
   * in the specified panel.
   * @param parent the gadget which needs to be laid out
   * @see #minimumLayoutSize
   * @return Dimension
   */
  public Dimension preferredLayoutSize(ContainerGadget parent) {
    GadgetGridBagLayoutInfo info;
    if (useMinimum) {
        info = GetLayoutInfo(parent, MINSIZE);
    } else {
        info = GetLayoutInfo(parent, PREFERREDSIZE);
    }
    return GetMinSize(parent, info);
  }

  /**
   * Returns the minimum dimensions needed to layout the gadgets
   * contained in the specified panel.
   * @param parent the gadget which needs to be laid out
   * @see #preferredLayoutSize
   * @return Dimension
   */
  public Dimension minimumLayoutSize(ContainerGadget parent) {
    GadgetGridBagLayoutInfo info = GetLayoutInfo(parent, MINSIZE);
    return GetMinSize(parent, info);
  }

  /**
   * Lays out the container in the specified panel.
   * @param parent the specified gadget being laid out
   * @see ContainerGadget
   */
  public void layoutContainerGadget(ContainerGadget parent) {
    ArrangeGadgetGrid(parent);
  }

  /**
   * Returns the String representation of this GadgetGridLayout's values.
   * @return String
   */
  public String toString() {
    return getClass().getName();
  }

  /**
   * Print the layout information.  Useful for debugging.
   */

  /* DEBUG
   *
   *  protected void DumpLayoutInfo(GadgetGridBagLayoutInfo s) {
   *    int x;
   *
   *    System.out.println("Col\tWidth\tWeight");
   *    for (x=0; x<s.width; x++) {
   *      System.out.println(x + "\t" +
   *			 s.minWidth[x] + "\t" +
   *			 s.weightX[x]);
   *    }
   *    System.out.println("Row\tHeight\tWeight");
   *    for (x=0; x<s.height; x++) {
   *      System.out.println(x + "\t" +
   *			 s.minHeight[x] + "\t" +
   *			 s.weightY[x]);
   *    }
   *  }
   */

  /**
   * Print the layout constraints.  Useful for debugging.
   */

  /* DEBUG
   *
   *  protected void DumpConstraints(GadgetGridBagConstraints constraints) {
   *    System.out.println(
   *		       "wt " +
   *		       constraints.weightx +
   *		       " " +
   *		       constraints.weighty +
   *		       ", " +
   *
   *		       "box " +
   *		       constraints.gridx +
   *		       " " +
   *		       constraints.gridy +
   *		       " " +
   *		       constraints.gridwidth +
   *		       " " +
   *		       constraints.gridheight +
   *		       ", " +
   *
   *		       "min " +
   *		       constraints.minWidth +
   *		       " " +
   *		       constraints.minHeight +
   *		       ", " +
   *
   *		       "pad " +
   *		       constraints.insets.bottom +
   *		       " " +
   *		       constraints.insets.left +
   *		       " " +
   *		       constraints.insets.right +
   *		       " " +
   *		       constraints.insets.top +
   *		       " " +
   *		       constraints.ipadx +
   *		       " " +
   *		       constraints.ipady);
   *  }
   */

  /*
   * Fill in an instance of the above structure for the current set
   * of managed children.  This requires three passes through the
   * set of children:
   *
   * 1) Figure out the dimensions of the layout grid
   * 2) Determine which cells the gadgets occupy
   * 3) Distribute the weights and min sizes amoung the rows/columns.
   *
   * This also caches the minsizes for all the children when they are
   * first encountered (so subsequent loops don't need to ask again).
   */


  /**
   * Gets the layout information
   * @param parent - TBD
   * @param sizeflag - TBD
   * @return GadgetGridBagLayoutInfo
   */
  protected GadgetGridBagLayoutInfo GetLayoutInfo(ContainerGadget parent, int sizeflag) {
    GadgetGridBagLayoutInfo r = new GadgetGridBagLayoutInfo();
    Gadget gadget;
    GadgetGridBagConstraints constraints;
    Dimension d;
    Gadget gadgets[] = parent.getGadgets();

    int gadgetindex, i, j, k, px, py, pixels_diff, nextSize;
    int curX, curY, curWidth, curHeight, curRow, curCol;
    double weight_diff, weight, start, size;
    int xMax[], yMax[];

    /*
     * Pass #1
     *
     * Figure out the dimensions of the layout grid (use a value of 1 for
     * zero or negative widths and heights).
     */

    r.width = r.height = 0;
    curRow = curCol = -1;
    xMax = new int[MAXGRIDSIZE];
    yMax = new int[MAXGRIDSIZE];

    for (gadgetindex = 0 ; gadgetindex < gadgets.length ; gadgetindex++) {
      gadget = gadgets[gadgetindex];
      if (!gadget.isVisible())
	    continue;
      constraints = lookupConstraints(gadget);

      curX = constraints.gridx;
      curY = constraints.gridy;
      curWidth = constraints.gridwidth;
      if (curWidth <= 0)
	    curWidth = 1;
      curHeight = constraints.gridheight;
      if (curHeight <= 0)
	    curHeight = 1;

      /* If x or y is negative, then use relative positioning: */
      if (curX < 0 && curY < 0) {
    	if (curRow >= 0)
    	  curY = curRow;
    	else if (curCol >= 0)
    	  curX = curCol;
    	else
    	  curY = 0;
      }
      if (curX < 0) {
    	px = 0;
    	for (i = curY; i < (curY + curHeight); i++)
    	  px = Math.max(px, xMax[i]);

    	curX = px - curX - 1;
    	if(curX < 0)
    	  curX = 0;
      }
      else if (curY < 0) {
    	py = 0;
    	for (i = curX; i < (curX + curWidth); i++)
    	  py = Math.max(py, yMax[i]);

    	curY = py - curY - 1;
    	if(curY < 0)
    	  curY = 0;
      }

      /* Adjust the grid width and height */
      for (px = curX + curWidth; r.width < px; r.width++);
      for (py = curY + curHeight; r.height < py; r.height++);

      /* Adjust the xMax and yMax arrays */
      for (i = curX; i < (curX + curWidth); i++) { yMax[i] = py; }
      for (i = curY; i < (curY + curHeight); i++) { xMax[i] = px; }

      /* Cache the current slave's size. */
      int childWidth = gadget.pref_width;
      int childHeight = gadget.pref_height;
      if (sizeflag == PREFERREDSIZE) {
        if ( childWidth < 0 || childHeight < 0 ) {
    	    d = gadget.getPreferredSize();
    	    childWidth = d.width;
    	    childHeight = d.height;
	    }
	  }
      else {
	    d = gadget.getMinimumSize();
	    childWidth = d.width;
	    childHeight = d.height;
	  }
      constraints.minWidth = childWidth;
      constraints.minHeight = childHeight;

      /* Zero width and height must mean that this is the last item (or
       * else something is wrong). */
      if (constraints.gridheight == 0 && constraints.gridwidth == 0)
	    curRow = curCol = -1;

      /* Zero width starts a new row */
      if (constraints.gridheight == 0 && curRow < 0)
	    curCol = curX + curWidth;

      /* Zero height starts a new column */
      else if (constraints.gridwidth == 0 && curCol < 0)
    	curRow = curY + curHeight;
    }

    /*
     * Apply minimum row/column dimensions
     */
    if (columnWidths != null && r.width < columnWidths.length)
      r.width = columnWidths.length;
    if (rowHeights != null && r.height < rowHeights.length)
      r.height = rowHeights.length;

    /*
     * Pass #2
     *
     * Negative values for gridX are filled in with the current x value.
     * Negative values for gridY are filled in with the current y value.
     * Negative or zero values for gridWidth and gridHeight end the current
     *  row or column, respectively.
     */

    curRow = curCol = -1;
    xMax = new int[MAXGRIDSIZE];
    yMax = new int[MAXGRIDSIZE];

    for (gadgetindex = 0 ; gadgetindex < gadgets.length ; gadgetindex++) {
      gadget = gadgets[gadgetindex];
      if (!gadget.isVisible())
    	continue;
      constraints = lookupConstraints(gadget);

      curX = constraints.gridx;
      curY = constraints.gridy;
      curWidth = constraints.gridwidth;
      curHeight = constraints.gridheight;

      /* If x or y is negative, then use relative positioning: */
      if (curX < 0 && curY < 0) {
    	if(curRow >= 0)
    	  curY = curRow;
    	else if(curCol >= 0)
    	  curX = curCol;
    	else
    	  curY = 0;
      }

      if (curX < 0) {
    	if (curHeight <= 0) {
    	  curHeight += r.height - curY;
    	  if (curHeight < 1)
    	    curHeight = 1;
	    }

    	px = 0;
    	for (i = curY; i < (curY + curHeight); i++)
    	  px = Math.max(px, xMax[i]);

    	curX = px - curX - 1;
    	if(curX < 0)
    	  curX = 0;
      }
      else if (curY < 0) {
    	if (curWidth <= 0) {
    	  curWidth += r.width - curX;
    	  if (curWidth < 1)
    	    curWidth = 1;
    	}

    	py = 0;
    	for (i = curX; i < (curX + curWidth); i++)
    	  py = Math.max(py, yMax[i]);

    	curY = py - curY - 1;
    	if(curY < 0)
    	  curY = 0;
      }

      if (curWidth <= 0) {
    	curWidth += r.width - curX;
    	if (curWidth < 1)
    	  curWidth = 1;
      }

      if (curHeight <= 0) {
    	curHeight += r.height - curY;
    	if (curHeight < 1)
    	  curHeight = 1;
      }

      px = curX + curWidth;
      py = curY + curHeight;

      for (i = curX; i < (curX + curWidth); i++) { yMax[i] = py; }
      for (i = curY; i < (curY + curHeight); i++) { xMax[i] = px; }

      /* Make negative sizes start a new row/column */
      if (constraints.gridheight == 0 && constraints.gridwidth == 0)
    	curRow = curCol = -1;
      if (constraints.gridheight == 0 && curRow < 0)
    	curCol = curX + curWidth;
      else if (constraints.gridwidth == 0 && curCol < 0)
        curRow = curY + curHeight;

      /* Assign the new values to the gridbag slave */
      constraints.tempX = curX;
      constraints.tempY = curY;
      constraints.tempWidth = curWidth;
      constraints.tempHeight = curHeight;
    }

    /*
     * Apply minimum row/column dimensions and weights
     */
    if (columnWidths != null)
      System.arraycopy(columnWidths, 0, r.minWidth, 0, columnWidths.length);
    if (rowHeights != null)
      System.arraycopy(rowHeights, 0, r.minHeight, 0, rowHeights.length);
    if (columnWeights != null)
      System.arraycopy(columnWeights, 0, r.weightX, 0, columnWeights.length);
    if (rowWeights != null)
      System.arraycopy(rowWeights, 0, r.weightY, 0, rowWeights.length);

    /*
     * Pass #3
     *
     * Distribute the minimun widths and weights:
     */

    nextSize = Integer.MAX_VALUE;

    for (i = 1;
	 i != Integer.MAX_VALUE;
	 i = nextSize, nextSize = Integer.MAX_VALUE) {
      for (gadgetindex = 0 ; gadgetindex < gadgets.length ; gadgetindex++) {
    	gadget = gadgets[gadgetindex];
    	if (!gadget.isVisible())
    	  continue;
    	constraints = lookupConstraints(gadget);

    	if (constraints.tempWidth == i) {
    	  px = constraints.tempX + constraints.tempWidth; /* right column */

	  /*
	   * Figure out if we should use this slave\'s weight.  If the weight
	   * is less than the total weight spanned by the width of the cell,
	   * then discard the weight.  Otherwise split the difference
	   * according to the existing weights.
	   */

    	  weight_diff = constraints.weightx;
    	  for (k = constraints.tempX; k < px; k++)
    	    weight_diff -= r.weightX[k];
    	  if (weight_diff > 0.0) {
    	    weight = 0.0;
    	    for (k = constraints.tempX; k < px; k++)
    	      weight += r.weightX[k];
    	    for (k = constraints.tempX; weight > 0.0 && k < px; k++) {
    	      double wt = r.weightX[k];
    	      double dx = (wt * weight_diff) / weight;
    	      r.weightX[k] += dx;
    	      weight_diff -= dx;
    	      weight -= wt;
    	    }
    	    /* Assign the remainder to the rightmost cell */
    	    r.weightX[px-1] += weight_diff;
    	  }

	  /*
	   * Calculate the minWidth array values.
	   * First, figure out how wide the current slave needs to be.
	   * Then, see if it will fit within the current minWidth values.
	   * If it will not fit, add the difference according to the
	   * weightX array.
	   */

    	  pixels_diff =
    	    constraints.minWidth + constraints.ipadx +
    	    constraints.insets.left + constraints.insets.right;

    	  for (k = constraints.tempX; k < px; k++)
    	    pixels_diff -= r.minWidth[k];
    	  if (pixels_diff > 0) {
    	    weight = 0.0;
    	    for (k = constraints.tempX; k < px; k++)
    	      weight += r.weightX[k];
    	    for (k = constraints.tempX; weight > 0.0 && k < px; k++) {
    	      double wt = r.weightX[k];
    	      int dx = (int)((wt * ((double)pixels_diff)) / weight);
    	      r.minWidth[k] += dx;
    	      pixels_diff -= dx;
    	      weight -= wt;
	    }
	    /* Any leftovers go into the rightmost cell */
	    r.minWidth[px-1] += pixels_diff;
	  }
	}
	else if (constraints.tempWidth > i && constraints.tempWidth < nextSize)
	  nextSize = constraints.tempWidth;


	if (constraints.tempHeight == i) {
	  py = constraints.tempY + constraints.tempHeight; /* bottom row */

	  /*
	   * Figure out if we should use this slave\'s weight.  If the weight
	   * is less than the total weight spanned by the height of the cell,
	   * then discard the weight.  Otherwise split it the difference
	   * according to the existing weights.
	   */

	  weight_diff = constraints.weighty;
	  for (k = constraints.tempY; k < py; k++)
	    weight_diff -= r.weightY[k];
	  if (weight_diff > 0.0) {
	    weight = 0.0;
	    for (k = constraints.tempY; k < py; k++)
	      weight += r.weightY[k];
	    for (k = constraints.tempY; weight > 0.0 && k < py; k++) {
	      double wt = r.weightY[k];
	      double dy = (wt * weight_diff) / weight;
	      r.weightY[k] += dy;
	      weight_diff -= dy;
	      weight -= wt;
	    }
	    /* Assign the remainder to the bottom cell */
	    r.weightY[py-1] += weight_diff;
	  }

	  /*
	   * Calculate the minHeight array values.
	   * First, figure out how tall the current slave needs to be.
	   * Then, see if it will fit within the current minHeight values.
	   * If it will not fit, add the difference according to the
	   * weightY array.
	   */

	  pixels_diff =
	    constraints.minHeight + constraints.ipady +
	    constraints.insets.top + constraints.insets.bottom;
	  for (k = constraints.tempY; k < py; k++)
	    pixels_diff -= r.minHeight[k];
	  if (pixels_diff > 0) {
	    weight = 0.0;
	    for (k = constraints.tempY; k < py; k++)
	      weight += r.weightY[k];
	    for (k = constraints.tempY; weight > 0.0 && k < py; k++) {
	      double wt = r.weightY[k];
	      int dy = (int)((wt * ((double)pixels_diff)) / weight);
	      r.minHeight[k] += dy;
	      pixels_diff -= dy;
	      weight -= wt;
	    }
	    /* Any leftovers go into the bottom cell */
	    r.minHeight[py-1] += pixels_diff;
	  }
	}
	else if (constraints.tempHeight > i &&
		 constraints.tempHeight < nextSize)
	  nextSize = constraints.tempHeight;
      }
    }

    return r;
  }

  /*
   * Adjusts the x, y, width, and height fields to the correct
   * values depending on the constraint geometry and pads.
   * @param constraints - TBD
   * @param r - TBD
   */
  protected void AdjustForGravity(GadgetGridBagConstraints constraints,
				  Rectangle r) {
    int diffx, diffy;

    r.x += constraints.insets.left;
    r.width -= (constraints.insets.left + constraints.insets.right);
    r.y += constraints.insets.top;
    r.height -= (constraints.insets.top + constraints.insets.bottom);

    diffx = 0;
    if ((constraints.fill != GadgetGridBagConstraints.HORIZONTAL &&
	 constraints.fill != GadgetGridBagConstraints.BOTH)
	&& (r.width > (constraints.minWidth + constraints.ipadx))) {
      diffx = r.width - (constraints.minWidth + constraints.ipadx);
      r.width = constraints.minWidth + constraints.ipadx;
    }

    diffy = 0;
    if ((constraints.fill != GadgetGridBagConstraints.VERTICAL &&
	 constraints.fill != GadgetGridBagConstraints.BOTH)
	&& (r.height > (constraints.minHeight + constraints.ipady))) {
      diffy = r.height - (constraints.minHeight + constraints.ipady);
      r.height = constraints.minHeight + constraints.ipady;
    }

    switch (constraints.anchor) {
    case GadgetGridBagConstraints.CENTER:
      r.x += diffx/2;
      r.y += diffy/2;
      break;
    case GadgetGridBagConstraints.NORTH:
      r.x += diffx/2;
      break;
    case GadgetGridBagConstraints.NORTHEAST:
      r.x += diffx;
      break;
    case GadgetGridBagConstraints.EAST:
      r.x += diffx;
      r.y += diffy/2;
      break;
    case GadgetGridBagConstraints.SOUTHEAST:
      r.x += diffx;
      r.y += diffy;
      break;
    case GadgetGridBagConstraints.SOUTH:
      r.x += diffx/2;
      r.y += diffy;
      break;
    case GadgetGridBagConstraints.SOUTHWEST:
      r.y += diffy;
      break;
    case GadgetGridBagConstraints.WEST:
      r.y += diffy/2;
      break;
    case GadgetGridBagConstraints.NORTHWEST:
      break;
    default:
      throw new IllegalArgumentException("illegal anchor value");
    }
  }

  /*
   * Figure out the minimum size of the
   * master based on the information from GetLayoutInfo()
   * @param parent - TBD
   * @param info - TBD
   * @return Dimension
   */
  protected Dimension GetMinSize(ContainerGadget parent, GadgetGridBagLayoutInfo info) {
    Dimension d = new Dimension();
    int i, t;
    Insets insets = parent.getInsets();

    t = 0;
    for(i = 0; i < info.width; i++)
      t += info.minWidth[i];
    d.width = t + insets.left + insets.right;

    t = 0;
    for(i = 0; i < info.height; i++)
      t += info.minHeight[i];
    d.height = t + insets.top + insets.bottom;

    return d;
  }

  /*
   * Lay out the grid
   * @param parent - TBD
   */
  protected void ArrangeGadgetGrid(ContainerGadget parent) {
    Gadget gadget;
    int gadgetindex;
    GadgetGridBagConstraints constraints;
    Insets insets = parent.getInsets();
    Gadget gadgets[] = parent.getGadgets();
    Dimension d;
    Rectangle r = new Rectangle();
    int i, diffw, diffh;
    double weight;
    GadgetGridBagLayoutInfo info;

    /*
     * If the parent has no slaves anymore, then don't do anything
     * at all:  just leave the parent's size as-is.
     */
    if (gadgets.length == 0 &&
    	(columnWidths == null || columnWidths.length == 0) &&
    	(rowHeights == null || rowHeights.length == 0)) {
          return;
    }

    /*
     * Pass #1: scan all the slaves to figure out the total amount
     * of space needed.
     */

    info = GetLayoutInfo(parent, PREFERREDSIZE);
    d = GetMinSize(parent, info);

/* RWK: changed from...
    if (d.width < parent.width || d.height < parent.height) {
 * to...
 */
    if (d.width > parent.width || d.height > parent.height) {
      info = GetLayoutInfo(parent, MINSIZE);
      d = GetMinSize(parent, info);
    }

    layoutInfo = info;
    r.width = d.width;
    r.height = d.height;

    /*
     * DEBUG
     *
     * DumpLayoutInfo(info);
     * for (gadgetindex = 0 ; gadgetindex < gadgets.length ; gadgetindex++) {
     * gadget = gadgets[gadgetindex];
     * if (!gadget.isVisible())
     *	continue;
     * constraints = lookupConstraints(gadget);
     * DumpConstraints(constraints);
     * }
     * System.out.println("minSize " + r.width + " " + r.height);
     */

    /*
     * If the current dimensions of the window don't match the desired
     * dimensions, then adjust the minWidth and minHeight arrays
     * according to the weights.
     */

    diffw = parent.width - r.width;
    if (diffw != 0) {
      weight = 0.0;
      for (i = 0; i < info.width; i++)
    	weight += info.weightX[i];
      if (weight > 0.0) {
    	for (i = 0; i < info.width; i++) {
    	  int dx = (int)(( ((double)diffw) * info.weightX[i]) / weight);
    	  info.minWidth[i] += dx;
    	  r.width += dx;
    	  if (info.minWidth[i] < 0) {
    	    r.width -= info.minWidth[i];
    	    info.minWidth[i] = 0;
    	  }
    	}
      }
      diffw = parent.width - r.width;
    }
    else {
      diffw = 0;
    }

    diffh = parent.height - r.height;
    if (diffh != 0) {
      weight = 0.0;
      for (i = 0; i < info.height; i++)
    	weight += info.weightY[i];
      if (weight > 0.0) {
    	for (i = 0; i < info.height; i++) {
    	  int dy = (int)(( ((double)diffh) * info.weightY[i]) / weight);
    	  info.minHeight[i] += dy;
    	  r.height += dy;
    	  if (info.minHeight[i] < 0) {
    	    r.height -= info.minHeight[i];
    	    info.minHeight[i] = 0;
    	  }
    	}
      }
      diffh = parent.height - r.height;
    }
    else {
      diffh = 0;
    }

    /*
     * DEBUG
     *
     * System.out.println("Re-adjusted:");
     * DumpLayoutInfo(info);
     */

    /*
     * Now do the actual layout of the slaves using the layout information
     * that has been collected.
     */

    info.startx = diffw/2 + insets.left;
    info.starty = diffh/2 + insets.top;

    for (gadgetindex = 0 ; gadgetindex < gadgets.length ; gadgetindex++) {
      gadget = gadgets[gadgetindex];
      if (!gadget.isVisible())
    	continue;
      constraints = lookupConstraints(gadget);

      r.x = info.startx;
      for(i = 0; i < constraints.tempX; i++)
    	r.x += info.minWidth[i];

      r.y = info.starty;
      for(i = 0; i < constraints.tempY; i++)
    	r.y += info.minHeight[i];

      r.width = 0;
      for(i = constraints.tempX;
    	  i < (constraints.tempX + constraints.tempWidth);
    	  i++) {
    	r.width += info.minWidth[i];
      }

      r.height = 0;
      for(i = constraints.tempY;
    	  i < (constraints.tempY + constraints.tempHeight);
    	  i++) {
    	r.height += info.minHeight[i];
      }

      AdjustForGravity(constraints, r);

      /*
       * If the window is too small to be interesting then
       * unmap it.  Otherwise configure it and then make sure
       * it's mapped.
       */

      if ((r.width <= 0) || (r.height <= 0)) {
    	gadget.setBounds(0, 0, 0, 0, false);
      }
      else {
    	if (gadget.x != r.x || gadget.y != r.y ||
    	    gadget.width != r.width || gadget.height != r.height) {
    	  gadget.setBounds(r.x, r.y, r.width, r.height, false);
    	}
      }
    }
  }
}
