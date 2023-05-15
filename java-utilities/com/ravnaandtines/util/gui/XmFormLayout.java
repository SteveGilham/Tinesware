package com.ravnaandtines.util.gui;

/**<pre>
 *  XmFormLayout.java (a Java layout manager which was
 *  inspired by the Motif XmForm widget)
 *  Copyright (C) 1996 Softbear Inc.  (info@softbear.com)
 *  Latest version at http://www.softbear.com/java/xmformlm
 *
 *  This library (class) is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free
 *  Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *  Modified 3-Sep-1997 by Mr.Tines to allow * and / qualifiers;
 *				"Humpty.bottom=form.bottom*23"
 *                      means 23% of form.bottom
 *				"Humpty.bottom=form.bottom/3"
 *                      means 1/3 of form.bottom
 * This avoids having to do GridLayout panels to get things even;
 * and you can also do things in 2:3:2:3 ratio easily.
 *
 * Modified 28-Dec-1998 by Mr.Tines &lt;tines@windsong.demon.co.uk&gt;
 * to place in a systematic package and port to java 1.1
 *
 *	XmFormLayout is a Java layout manager which arranges display
 *	components according to constraints (it was inspired by
 *	Motif's XmForm widget).  Here is an example of how it is used:
 *
 *		public class TestXFL extends java.applet.Applet {
 *
 *			public void init() {
 *			String constraints[] = {
 *				"Humpty.bottom=form.bottom-23",
 *				"Humpty.left=form.left+21",
 *				"Humpty.right=Tweedle.right",
 *				"Tweedle.left=Humpty.left",
 *				"Tweedle.top=form.top+24",
 *				"Tweedle.bottom=Humpty.top-10",
 *				"Dumpty.bottom=Humpty.bottom",
 *				"Dumpty.left=Humpty.right+30",
 *				"Dumpty.right=form.right-22",
 *				"Dumpty.top=Humpty.top",
 *				"Dee.bottom=Tweedle.bottom",
 *				"Dee.top=Tweedle.top",
 *				"Dee.left=Dumpty.left",
 *				"Dee.right=Dumpty.right",
 *			};
 *
 *			this.setLayout(new XmFormLayout(constraints));
 *			this.add("Humpty", new java.awt.Button("Humpty"));
 *			java.awt.TextArea tweedle = new java.awt.TextArea(15,15);
 *			tweedle.appendText("Tweedle");
 *			this.add("Tweedle", tweedle);
 *			this.add("Dumpty", new java.awt.Button("Dumpty"));
 *			java.awt.TextArea dee = new java.awt.TextArea();
 *			dee.appendText("Dee");
 *			this.add("Dee", dee);
 *		}
 *	}
 *
 * </pre>
*/

public class XmFormLayout implements java.awt.LayoutManager {

	/*
	 *	Component tuple constants, these must range from 0...6.
	 *	Attribute numbers must range from BOTTOM...TOP.
	*/
	final static int	COMPONENT = 0;
	final static int	BOTTOM = 1;
	final static int	LEFT = 2;
	final static int	RIGHT = 3;
	final static int	TOP = 4;
	final static int	BITSET = 5;
	final static int	NAME = 6;
	final static int  ADD = 0;
	final static int  TIMES = 1;
	final static int  DIVIDE = 2;

	/*
	 *	Other constants.
	*/
	final static int	FORM = -32765;
	final static int	INVALID = -32766;
	final static int	UNBOUND = -32767;
	final static boolean	debug = false;
	final static boolean	bigDebug = false;

	/*
	 *	The constraints and the results of the
	 *	last reshap() computations.
	*/
	String			myName;
	java.awt.Dimension	formDimensions;	// (as of last reconstrain)
	java.awt.Point		formLocation;	// (as of last reconstrain)
	java.util.Vector	tuples;		// (4 attrs, component, bitset)
	java.util.Hashtable	nameToIndex;
	String			constraints[];
	boolean			changedFlag;

	/*
	 *	Add a component.
	*/
	public void addLayoutComponent(String name, java.awt.Component component) {
		Object tuple[] = new Object[7];
		tuple[XmFormLayout.COMPONENT] = component;
		tuple[XmFormLayout.BITSET] = new java.util.BitSet();
		tuple[XmFormLayout.NAME] = name;
		int componentNr = this.tuples.size();
		this.tuples.addElement(tuple);
		this.nameToIndex.put(name, new Integer(componentNr));
		this.changedFlag = true;
	}

	/*
	 *	Parses an expression 'a.b+c' (or 'a.b-c' or 'a.b' or
	 *	'+c' or '-c') into an rvalue 'a.b' and a signed
	 *	number 'c'.  If the rvalue is unbound then return
	 *	XmFormLayout.UNBOUND, otherwise return the value of
	 *	the expression.
	*/
	private final int evaluate(String expression) throws Exception {
		int i, v = 0, s = 1;
		int mode = ADD;
		if (this.debug) {
			System.out.println(this.myName + "'evaluate() expr: " +
				expression + "\n");
		}

		i = expression.indexOf('+');
		if (i == -1) {
			i = expression.indexOf('-');
			s = -1;
		}
		if(i == -1)
		{
			i = expression.indexOf('*');
			mode = TIMES;
		}
		if(i == -1)
		{
			i = expression.indexOf('/');
			mode = DIVIDE;
		}

		if (i == -1) {
			v = this.getAttr(expression);
		} else if (i > 1) {
			String rvalue = expression.substring(0, i);
			v = this.getAttr(rvalue);
		}

		if (i != -1 && v != XmFormLayout.UNBOUND) {
			String unicodeNumber = expression.substring(i+1);
			int number = Integer.parseInt(unicodeNumber, 10);
			if(mode == ADD) v += (s*number);
			else if(mode == TIMES)
			{
				double temp = v;
				v = (int) (temp*number/100.0);
			}
			else if (mode == DIVIDE)
			{
				v /= number;
			}
		}

		return v;
	}

	/*
	 *	Enforce explicit constraints by binding lvalues and
	 *	also marking those lvalues which can't be bound.
	 *	Parses an explicit constraint 'a.b=c.d+e' into
	 *	an lvalue 'a.b' and an expression 'c.d+e'.
	 *	Returns true if all possible explicit lvalues are bound.
	*/
	private final boolean explicit(java.util.BitSet constraintBitset) throws Exception {
		if (this.debug) {
			System.out.println(this.myName + "'explicit()...\n");
		}
		boolean deferFlag = false;
		if (this.constraints == null || this.constraints.length == 0) {
			throw new Exception(this.myName + "'explicit() no constraints");
		}
		for (int i = 0; i < this.constraints.length; ++i) {
			if (constraintBitset.get(i)) {
				continue;
			}
			String constraint = this.constraints[i];
			if (this.debug) {
				System.out.println(this.myName + "'explicit() constraint: " + 
constraint + "\n"); 
			}
			int j = constraint.indexOf('=');
			if (j < 3) {
				throw new Exception(this.myName + "'explicit() invalid constraint: "
+ constraint);
			}
			String lvalue =  constraint.substring(0, j);
			String expression =  constraint.substring(j+1);
			if (this.setAttr(lvalue, this.evaluate(expression))) {
				constraintBitset.set(i);
			} else {
				deferFlag = true;
			}
		}
		return !deferFlag;
	}

	/*
	 *	If the attribute is bound then return its'
	 *	value, otherwise return XmFormLayout.UNBOUND.
	*/
	private final int getAttr(int componentNr, int attrNr) {
		if (this.bigDebug) {
			System.out.println(this.myName + "'getAttr(): c=" +
				componentNr + ", a=" + attrNr + "\n");
		}
		int v = XmFormLayout.UNBOUND;
		if (componentNr == XmFormLayout.FORM) {
			if (attrNr == XmFormLayout.BOTTOM) {
				v = this.formLocation.y +
					this.formDimensions.height;
			} else if (attrNr == XmFormLayout.LEFT) {
				v = this.formLocation.x;
			} else if (attrNr == XmFormLayout.RIGHT) {
				v = this.formLocation.x +
					this.formDimensions.width;
			} else if (attrNr == XmFormLayout.TOP) {
				v = this.formLocation.y;
			}
		} else if (isBound(componentNr, attrNr)) {
			Object tuple[] = (Object[]) this.tuples.elementAt(componentNr);
			v = ((Integer) tuple[attrNr]).intValue();
		}
		return v;
	}

	/*
	 *	If the attribute is bound then return its'
	 *	value, otherwise return XmFormLayout.UNBOUND.
	*/
	private final int getAttr(String fqAttrName) throws Exception {
		int tuple[] = this.parseAttr(fqAttrName);
		int value = this.getAttr(tuple[0], tuple[1]);
		if (this.debug) {
			System.out.println(this.myName + "'getAttr(): fqAttr=" +
				fqAttrName + ", value=" + value + "\n");
		}
		return value;
	}

	private final int getAttrNr(String attrName) { 
		if (this.bigDebug) {
			System.out.println(this.myName + "'getAttrNr() attr: " +
				attrName + "\n");
		}
		int attrNr = XmFormLayout.INVALID; // invalid attr # (never happens)
		if (attrName.equals("bottom")) {
			attrNr = XmFormLayout.BOTTOM;
		} else if (attrName.equals("left") || attrName.equals("x")) {
			attrNr = XmFormLayout.LEFT;
		} else if (attrName.equals("right")) {
			attrNr = XmFormLayout.RIGHT;
		} else if (attrName.equals("top") || attrName.equals("y")) {
			attrNr = XmFormLayout.TOP;
		}
		return attrNr;
	}

	private final java.util.BitSet getBitset(int componentNr) {
		Object tuple[] = (Object[]) this.tuples.elementAt(componentNr);
		return (java.util.BitSet) tuple[XmFormLayout.BITSET];
	}

	private final java.awt.Component getComponent(int componentNr) {
		Object tuple[] = (Object[]) this.tuples.elementAt(componentNr);
		return (java.awt.Component) tuple[XmFormLayout.COMPONENT];
	}

	private final String getComponentName(int componentNr) {
		Object tuple[] = (Object[]) this.tuples.elementAt(componentNr);
		return (String) tuple[XmFormLayout.NAME];
	}

	private final int getComponentNr(String componentName) throws Exception 
{
		if (this.bigDebug) {
			System.out.println(this.myName +
				"'getComponentNr() attr: " +
				componentName + "\n");
		}
		Object n = this.nameToIndex.get(componentName);
		if (n == null) {
			throw new Exception(this.myName +
				"'getComponentNr() not found: " +
				componentName);
		}
		return ((Integer)n).intValue();
	}

	/*
	 *	Returns the preferred offset (width or height)
	 *	between the specified attribute and its' opposite.
	*/
	private final int getOffset(int componentNr, int attrNr) {
		int offset = XmFormLayout.INVALID; // invalid offset (never happens)
		java.awt.Component component = this.getComponent(componentNr);

		if (attrNr == XmFormLayout.BOTTOM) {
			offset = -component.getPreferredSize().height;
		} else if (attrNr == XmFormLayout.LEFT) {
			offset = component.getPreferredSize().width;
		} else if (attrNr == XmFormLayout.RIGHT) {
			offset = -component.getPreferredSize().width;
		} else if (attrNr == XmFormLayout.TOP) {
			offset = component.getPreferredSize().height;
		}
		return offset;
	}



	/*
	 *	Enforces implicit constraints by binding component
	 *	attributes which haven't an lvalue in ANY explicit
	 *	constraint and whose opposite attribute is already
	 *	bound, e.g. if foo.top isn't an lvalue in any explicit
	 *	constraint, and foo.bottom is bound, and foo has a
	 *	preferred height of 123, then enforce this implicit
	 *	constraint:
	 *		 "foo.top=foo.bottom-123"
	 *	Returns true if all possible implicit constraints are bound.
	*/
	private final boolean implicit() throws Exception {
		if (this.debug) {
			System.out.println(this.myName + "'implicit()...\n");
		}
		boolean deferFlag = false;
		for (int c = 0; c < this.tuples.size(); ++c) {
			for (int a = XmFormLayout.BOTTOM; a <= XmFormLayout.TOP; ++a) {
				if (this.isMarked(c, a) == false) {
					if (this.debug) {
						System.out.println(this.myName +
							"'implicit(): c=" +
							 c + ", a=" + a + "\n"); 
					}
					int o = this.opposite(a);
					if (this.isBound(c, o) == true) {
						// this also marks the attr 
						this.setAttr(c, a, this.getAttr(c, o) + 
						this.getOffset(c, o));
					} else {
						deferFlag = true;
					}
				}
			}
		}
		return !deferFlag;
	}

	/*
	 *	Returns true if this component's attribute was
	 *	bound; that is, if this.setAttr() was called.
	*/
	private final boolean isBound(int componentNr, int attributeNr) {
		java.util.BitSet bitset = this.getBitset(componentNr);
		return bitset.get(attributeNr);
	}

	/*
	 *	Returns true if this component's attribute is
	 *	an lvalue in any explicit constraint; that is,
	 *	if this.setMarked() was called.
	*/
	private final boolean isMarked(int componentNr, int attributeNr) {
		java.util.BitSet bitset = this.getBitset(componentNr);
		return bitset.get(attributeNr + (XmFormLayout.TOP+1));
	}

	/*
	 *	Reshape all components in the container.  Typically,
	 *	this causes a call to this.reconstrain() only the first time.
	*/
	public void layoutContainer(java.awt.Container parent) {
		java.awt.Dimension dummy = this.preferredLayoutSize(parent); 
				// reconstrain if necessary
		if (this.debug) {
			System.out.println(this.myName +
				"'layoutContainer(): w=" + dummy.width +
				", h=" + dummy.height + ", changedFlag=" +
				changedFlag + " (should be false)\n");
		}
		if (!this.changedFlag) {
			for (int c = 0; c < this.tuples.size(); ++c) {
				Object tuple[] = (Object[]) this.tuples.elementAt(c);
				int x = ((Integer)tuple[XmFormLayout.LEFT]).intValue();
				int y = ((Integer)tuple[XmFormLayout.TOP]).intValue();
				int w = ((Integer)tuple[XmFormLayout.RIGHT]).intValue() - x;
				int h = ((Integer)tuple[XmFormLayout.BOTTOM]).intValue() - y;
				if (this.debug) {
					System.out.println(this.myName + "'layoutContainer(): c=" +
						(String)tuple[XmFormLayout.NAME] + ", x=" + x +
						", y=" + y + ", w=" + w + ", h=" + h + "\n");
				}
				java.awt.Component component = 
					(java.awt.Component) tuple[XmFormLayout.COMPONENT];
				component.setBounds(x, y, w, h);
			}
		}
	}

	/*
	 *	XmFormLayout knowns only one size (the preferred size).
	*/
	public java.awt.Dimension minimumLayoutSize(java.awt.Container parent) {
		return preferredLayoutSize(parent);
	}

	private final int opposite(int attrNr) {
		int oppositeAttrNr = XmFormLayout.INVALID; // invalid attr # (never happens)

		if (attrNr == XmFormLayout.BOTTOM) {
			oppositeAttrNr = XmFormLayout.TOP;
		} else if (attrNr == XmFormLayout.LEFT) {
			oppositeAttrNr = XmFormLayout.RIGHT;
		} else if (attrNr == XmFormLayout.RIGHT) {
			oppositeAttrNr = XmFormLayout.LEFT;
		} else if (attrNr == XmFormLayout.TOP) {
			oppositeAttrNr = XmFormLayout.BOTTOM;
		}
		return oppositeAttrNr;
	}


	/*
	 *	Parse a fully-qualified attribute name 'a.b' into
	 *	a component 'a' and an attribute 'b', and return
	 *	the component number and attribute number.
	*/
	private final int[] parseAttr(String fqAttrName) throws Exception {
		int i = fqAttrName.indexOf('.');
		if (i < 1) {
			throw new Exception(this.myName +
				"'parseAttr() invalid fqAttr: " + fqAttrName);
		}
		String componentName = fqAttrName.substring(0, i);
		String attrName = fqAttrName.substring(i+1);
		int tuple[] = new int[2];
		if (componentName.equals("form")) {
			tuple[0] = XmFormLayout.FORM;
		} else {
			tuple[0] = this.getComponentNr(componentName);
		}
		tuple[1] = this.getAttrNr(attrName);
		return tuple;
	}

	public java.awt.Dimension preferredLayoutSize(java.awt.Container parent) {
		java.awt.Insets insets = parent.getInsets();
		int dx = insets.left + insets.right;
		int dy = insets.top + insets.bottom;
		if (this.debug) {
			System.out.println(this.myName +
				"'preferredLayoutSize(): dx=" +
				dx + ", dy=" + dy + "\n");
		}
		int x = insets.left;
		int y = insets.top;
		int w = parent.getSize().width - dx;
		int h = parent.getSize().height - dy;
		this.reshape(x, y, w, h);

		return new java.awt.Dimension(this.formDimensions.width + dx,
			this.formDimensions.height + dy);
	}


	/*
	 *	Removes a component.  This isn't particularly fast,
	 *	but it is rarely called, if ever.
	*/
	public void removeLayoutComponent(java.awt.Component oldComponent) {
		int nrComponents = this.tuples.size();
		for (int c = 0; c < nrComponents; ++c) {
			if (this.getComponent(c) == oldComponent) {
				String componentName = this.getComponentName(c);
				this.nameToIndex.remove(componentName);
				this.changedFlag = true;
			}
		}
	}

	/*
	 *	Layout the container according to constraints.
	 *	This may be time-consuming, so it should only be
	 *	done if changedFlag is true.
	*/
	private final void reconstrain() throws Exception {
		java.util.BitSet constraintBitset = new java.util.BitSet(); 
		for (int c = 0; c < this.tuples.size(); ++c) {
			java.util.BitSet componentBitset = this.getBitset(c);
			/*
			 *	The componentBitset has no clearAllBits()
			 *	method so AND with any blank bitset to clear it.
			*/
			componentBitset.and(constraintBitset);
		}
		boolean deferFlag;
		int nrTries = 0;
		do {
			if (this.debug) {
				System.out.println(this.myName + "'reconstrain(): iteration=" + 
					(nrTries+1) + "*****\n");
			}
			deferFlag = false;
			if (! this.explicit(constraintBitset)) {
				deferFlag = true;
			}
			if (! this.implicit()) {
				deferFlag = true;
			}
		} while (deferFlag && ++nrTries < 30);

		if (deferFlag) {
			throw new Exception(this.myName +
				"'reconstrain() too many iterations: " +
				nrTries);
		}
		this.changedFlag = false;
	}

	/*
	 *	If the container's location or shape has
	 *	changed, or if any of the component's have
	 *	changed, then re-compute the constraints;
	 *	otherwise use what was already computed.
	*/
	private final void reshape(int x, int y, int w, int h) {
		if (this.formDimensions.width != w ||
				this.formDimensions.height != h) {
			if (this.debug) {
				System.out.println(this.myName +
					"'reshape(): ***SIZE CHANGED*** w=" +
					w + ", h=" + h + " *******\n"); 
			}
			this.formDimensions.width = w;
			this.formDimensions.height = h;
			this.changedFlag = true;
		}
		if (this.formLocation.x != x ||
				this.formLocation.y != y) {
			this.formLocation.x = x;
			this.formLocation.y = y;
			this.changedFlag = true;
		}
		if (changedFlag) {
			try {
				this.reconstrain();
			} catch (Exception exception) {
				if (this.debug) {
					System.out.println(this.myName +
						"'EXCEPTION: " +
						exception.getMessage() + "\n"); 
				}
			}
		}
	}

	/*
	 *	Try to set a attribute; returns true if the was set
	 *	or false if it was deferred (the latter will happen
	 *	if the value is XmFormLayout.UNBOUND).
	*/
	private final boolean setAttr(int componentNr, int attrNr, int value) 
		throws Exception {
		if (this.debug) {
			System.out.println(this.myName +
				"'setAttr(): c=" + componentNr + ", a=" +
				attrNr + ", v=" + value + "\n");
		}
		if (componentNr == XmFormLayout.FORM) {
			throw new Exception(this.myName + "'setAttr() can't set form\n");
		}
		this.setMarked(componentNr, attrNr);
		boolean boundFlag = false;
		if (value != XmFormLayout.UNBOUND) {
			Object tuple[] = (Object[]) this.tuples.elementAt(componentNr);
			tuple[attrNr] = new Integer(value);
			this.setBound(componentNr, attrNr);
			boundFlag = true;
		}
		return boundFlag;
	}

	/*
	 *	Try to set a attribute 'c.a'; returns true if the
	 *	was set or false if it was deferred (the latter
	 *	will happen if the value is XmFormLayout.UNBOUND).
	*/
	private final boolean setAttr(String fqAttrName, int value) throws Exception {
		if (this.debug) {
			System.out.println(this.myName + "'setAttr() fqAttr=" +
				fqAttrName + ", value=" + value + "\n");
		}
		int tuple[] = this.parseAttr(fqAttrName);
		return this.setAttr(tuple[0], tuple[1], value);
	}

	private final void setBound(int componentNr, int attrNr) {
		if (this.bigDebug) {
			System.out.println(this.myName + "'setBound(): c=" +
				componentNr + ", a=" + attrNr + "\n");
		}
		java.util.BitSet bitset = this.getBitset(componentNr);
		bitset.set(attrNr);
	}

	private final void setMarked(int componentNr, int attrNr) {
		java.util.BitSet bitset = this.getBitset(componentNr);
		bitset.set(attrNr + (XmFormLayout.TOP+1));
	}

	/*
	 *	The constructor.
	*/
	public XmFormLayout(String newConstraints[]) {
		this.myName = (((Object) this).getClass()).getName();
		if (newConstraints == null && this.debug) {
			System.out.println(this.myName +
				"'XmFormLayout() newConstraints is null!\n"); 
		}
		this.constraints = newConstraints;
		this.formDimensions = new java.awt.Dimension(0,0);
		this.formLocation = new java.awt.Point(0,0);
		this.nameToIndex = new java.util.Hashtable();
		this.tuples = new java.util.Vector();
		this.changedFlag = true;
	}
}



                                                                                                                                                                                                                                        
