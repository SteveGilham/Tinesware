/****************************************************************
 **
 **  $Id: TextAreaGadget.java,v 1.50 1998/02/27 18:02:23 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/TextAreaGadget.java,v $
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
 **  History:-
 **  This class has been modified to wire in TextEvent handling for
 **  the GWT components.  --Mr. Tines, May '98
 ****************************************************************/

package dtai.gwt;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Color;
import java.io.PrintStream;
import java.util.StringTokenizer;
import java11.awt.event.FocusEvent;
import java11.awt.event.InputEvent;
import java11.awt.event.KeyEvent;
import java11.awt.event.MouseEvent;
import java11.awt.event.FocusListener;
import java11.awt.event.TextListener;

/**
 * A TextAreaGadget object is a multi-line area that displays text. It can
 * be set to allow editing or read-only modes.
 *
 * @version 1.1
 * @author 	DTAI, Incorporated
 */
public class TextAreaGadget extends ScrollPaneGadget implements TextGadget {
    /**
     * Create and display both vertical and horizontal scrollbars.
     */

    public final static int SCROLLBARS_BOTH = 0;

    /**
     * Create and display vertical scrollbar only.
     */

    public final static int SCROLLBARS_VERTICAL_ONLY = 1;

    /**
     * Create and display horizontal scrollbar only.
     */

    public final static int SCROLLBARS_HORIZONTAL_ONLY = 2;

    /**
     * Do not create or display any scrollbars for the text area.
     */

    public final static int SCROLLBARS_NONE = 3;

    /**
     * The number of rows in the TextAreaGadget.
     */
    private int	rows;

    /**
     * The number of columns in the TextAreaGadget.
     */
    private int	cols;

    /**
     * The default visibility of the scrollbars.
     */
    private int	scrollbarVisibility;

    /**
     * does the text wrap
     */
    private boolean wordWrap = true;

    /**
     * show the guides
     */
    private boolean showGuides = false;

    /**
     * A boolean indicating whether or not this TextComponent is editable.
     */
    private boolean editable = true;

    private LinesPanel linesPanel;

    /**
     * Constructs a new TextAreaGadget.
     */
    public TextAreaGadget() {
    	this("",0,0);
    }

    /**
     * Constructs a new TextAreaGadget with the specified number of rows and columns.
     * @param rows the number of rows
     * @param cols the number of columns
     */
    public TextAreaGadget(int rows, int cols) {
    	this("",rows,cols);
    }

    /**
     * Constructs a new TextAreaGadget with the specified text displayed.
     * @param text the text to be displayed
     */
    public TextAreaGadget(String text) {
    	this(text,0,0);
    }

    /**
     * Constructs a new TextAreaGadget with the specified text and number of rows
     * and columns.
     * @param text the text to be displayed
     * @param rows the number of rows
     * @param cols the number of cols
     */
    public TextAreaGadget(String text, int rows, int cols) {
    	this(text,rows,cols,SCROLLBARS_BOTH);
    }

    /**
     * Constructs a new TextAreaGadget with the specified text and number of rows
     * and columns.
     * @param text the text to be displayed
     * @param rows the number of rows
     * @param cols the number of cols
     * @param scrollbarVisibility the default scrollbar visibility
     */
    public TextAreaGadget(String text, int rows, int cols, int scrollbarVisibility) {
    	this.rows = rows;
    	this.cols = cols;
    	this.scrollbarVisibility = scrollbarVisibility;
    	if (scrollbarVisibility == SCROLLBARS_VERTICAL_ONLY) {
            setHorizontalScrollbarDisplayPolicy( SCROLLBAR_NEVER );
    	} else if (scrollbarVisibility == SCROLLBARS_HORIZONTAL_ONLY) {
            setVerticalScrollbarDisplayPolicy( SCROLLBAR_NEVER );
    	} else if (scrollbarVisibility == SCROLLBARS_NONE) {
            setVerticalScrollbarDisplayPolicy( SCROLLBAR_NEVER );
            setHorizontalScrollbarDisplayPolicy( SCROLLBAR_NEVER );
    	}
    	setWordWrap(true);
    	linesPanel = new LinesPanel(this);
    	setTextBackground(Color.white);
     	add(linesPanel);
    	setText( text );
    	setStretchingHorizontal(true);
    	setStretchingVertical(true);
    }

    /**
     * setTextBackground
     * @param color - TBD
     */
    public void setTextBackground(Color color) {
        linesPanel.setBackground(color);
    }

    /**
     * getTextBackground
     * @return Color
     */
    public Color getTextBackground() {
        return linesPanel.getBackground();
    }

    /**
     * cuts the selected text and puts it in the GWT clipboard
     */
    public void cut() {
        linesPanel.cut();
    }

    /**
     * copies the selected text to the GWT clipboard
     */
    public void copy() {
        linesPanel.copy();
    }

    /**
     * pastes the text from the GWT clipboard to the current position,
     * replacing any selected text
     */
    public void paste() {
        linesPanel.paste();
    }

    /**
     * undoes the last action
     */
    public void undo() {
        linesPanel.undo();
    }

    /**
     * Inserts the specified text at the specified position.
     * @param str the text to insert.
     * @param pos the position at which to insert.
     * @see dtai.gwt.TextGadget#setText
     * @see TextFieldGadget#replaceText
     */
    public void insert(String str, int pos) {
       linesPanel.insert(str, pos);
    }

    /**
     * Appends the given text to the end.
     * @param str the text to insert
     * @see TextFieldGadget#insertText
     */
    public void append(String str) {
       //linesPanel.append(str);
       setText(getText() + str);
    }

    /**
     * Replaces text from the indicated start to end position with the
     * new text specified.

     * @param str the text to use as the replacement.
     * @param start the start position.
     * @param end the end position.
     * @see TextFieldGadget#insertText
     * @see TextFieldGadget#replaceText
     */
    public void replaceRange(String str, int start, int end) {
	    linesPanel.replaceRange(str, start, end);
    }

    /**
     * Returns the number of rows in the TextAreaGadget.
     * @return int
     */
    public int getRows() {
    	return rows;
    }

    /**
     * set the number of rows in the TextAreaGadset.
     * @param rows - TBD
     */
    public void setRows(int rows) {
    	this.rows = rows;
    }

    /**
     * Returns the number of columns in the TextAreaGadget.
     * @return int
     */
    public int getColumns() {
    	return cols;
    }

    /**
     * set the number of columns in the TextAreaGadset.
     * @param cols - TBD
     */
    public void setColumns(int cols) {
    	this.cols = cols;
    }

    /**
     * Returns the number of columns in the TextAreaGadget.
     * @return int
     */
    public int getScrollbarVisibility() {
    	return scrollbarVisibility;
    }

    /**
     * Returns the specified row and column Dimensions of the TextAreaGadget.
     * @param rows the preferred rows amount
     * @param cols the preferred columns amount
     * @return Dimension
     */
    public Dimension getPreferredSize(int rows, int cols) {
        Dimension pref;
        if ((rows == 0) && (cols == 0)) {
            pref = linesPanel.getPreferredSize();
        } else {
            pref = fixedSize(rows, cols);
        }
        Insets i = getInsets();
        pref.width += i.left+i.right;
        pref.height += i.top+i.bottom;
        return pref;
    }

    /**
     * getScrolledPreferredSize
     * @return Dimension
     */
    public Dimension getScrolledPreferredSize() {
        if ((rows == 0) && (cols == 0)) {
            return linesPanel.getPreferredSize();
        } else {
            return fixedSize(rows, cols);
        }
    }

    /**
     * getScrolledMinimumSize
     * @return Dimension
     */
    public Dimension getScrolledMinimumSize() {
        if ((rows == 0) && (cols == 0)) {
            return linesPanel.getMinimumSize();
        } else {
            return fixedSize(1, 1);
        }
    }

    /**
     * Returns the specified minimum size Dimensions of the TextAreaGadget.
     * @param rows the minimum row size
     * @param cols the minimum column size
     * @return Dimension
     */
    public Dimension getMinimumSize(int rows, int cols) {
        Dimension min;
        if ((rows == 0) && (cols == 0)) {
            min = linesPanel.getMinimumSize();
        } else {
            min = fixedSize(rows, cols);
        }
        Insets i = getInsets();
        min.width += i.left+i.right;
        min.height += i.top+i.bottom;
        return min;
    }

    /**
     * returns the size need for give numner of rows and cols
     * @param row - TBD
     * @param cols - TBD
     * @return Dimension
     */
    public Dimension fixedSize(int row, int cols) {
	    FontMetrics metrics = getFontMetrics(getFont());
	    String str = "W";
	    int pref_width = metrics.stringWidth(str)*cols;
	    int pref_height = metrics.getHeight()*row;
        return new Dimension( pref_width, pref_height );
    }

    public Rectangle getFirstSelectionRect() {
        Rectangle rect = linesPanel.getFirstSelectionRect();
        if (rect != null) {
            Point vp = getViewport().getLocation();
            rect.x += linesPanel.x + vp.x;
            rect.y += linesPanel.y + vp.y;
        }
        return rect;
    }

    /**
     * Sets the text of this TextComponent to the specified text.
     * @param t the new text to be set
     * @see #getText
     */
    public void setText(String text) {
    	linesPanel.setText(text);
    }

    /**
     * Returns the text contained in this TextComponent.
     * @see #setText
     * @return String
     */
    public String getText() {
    	return linesPanel.getText();
    }

    /**
     * Returns the selected text contained in this TextComponent.
     * @see #setText
     * @return String
     */
    public String getSelectedText() {
    	return linesPanel.getSelectedText();
    }

    /**
     * Returns the boolean indicating whether this TextComponent is editable or not.
     * @see #setEditable
     * @return boolean
     */
    public boolean isEditable() {
    	return editable;
    }

    /**
     * Sets the specified boolean to indicate whether or not this TextComponent should be
     * editable.
     * @param t the boolean to be set
     * @see #isEditable
     */
    public void setEditable(boolean t) {
    	editable = t;
        if (editable) {
            linesPanel.setCursor(GadgetCursor.getPredefinedCursor(GadgetCursor.TEXT_CURSOR));
        } else {
            linesPanel.setCursor(null);
        }
    }

    /**
     * Sets word wrap
     * @param wordWrap the boolean to be set
     */
    public void setWordWrap(boolean wordWrap) {
        this.wordWrap = wordWrap;
        if (wordWrap == true) {
            setHorizontalScrollbarDisplayPolicy( SCROLLBAR_NEVER );
        } else {
            setHorizontalScrollbarDisplayPolicy( SCROLLBAR_AS_NEEDED );
        }
    }

    /**
     * returns the value of the word wrap flag
     * @return boolean
     */
    public boolean getWordWrap() {
        return wordWrap;
    }

    /**
     * Sets show guides flag
     * @param showGuides the boolean to be set
     */
    public void setShowGuides(boolean showGuides) {
        this.showGuides = showGuides;
    }

    /**
     * returns the value of the show guides flag
     * @return boolean
     */
    public boolean getShowGuides() {
        return showGuides;
    }

    /**
     * Returns the Caret Position.
     * @return int
     */
    public int getCaretPosition() {
    	return linesPanel.getCaretPosition();
    }

    /**
     * Sets the Caret Position.
     * @param position - TBD
     */
    public void setCaretPosition(int position) {
    	linesPanel.setCaretPosition(position);
    }

    /**
     * Returns the selected text's start position.
     * @return int
     */
    public int getSelectionStart() {
    	return linesPanel.getSelectionStart();
    }

    /**
     * Returns the selected text's end position.
     * @return int
     */
    public int getSelectionEnd() {
    	return linesPanel.getSelectionEnd();
    }

    /**
     * Set the Selection Start
     * @param position - TBD
     */
    public void  setSelectionStart(int position) {
    	linesPanel.setSelectionStart(position);
    }

    /**
     * Set the Selection End
     * @param position - TBD
     */
    public void setSelectionEnd(int position) {
    	linesPanel.setSelectionEnd(position);
    }

    /**
     * Selects the text found between the specified start and end locations.
     * @param selStart the start position of the text
     * @param selEnd the end position of the text
     */
    public void select(int selStart, int selEnd) {
        linesPanel.select(selStart, selEnd);
     }

    /**
     * Selects all the text in the TextComponent.
     */
    public void selectAll() {
        linesPanel.selectAll();
    }

    public synchronized void addFocusListener(FocusListener l) {
        linesPanel.addFocusListener(l);
    }

    public synchronized void removeFocusListener(FocusListener l) {
         linesPanel.removeFocusListener(l);
    }
	  public synchronized void addTextListener(TextListener l) {
        linesPanel.addTextListener(l);
	  }
	  public synchronized void removeTextListener(TextListener l) {
        linesPanel.removeTextListener(l);
	  }


    public void setBounds(int x, int y, int w, int h, boolean notifyParent) {
        if (w != width || h != height) {
            LinesPanel linesPanel = this.linesPanel;
            if (linesPanel != null) {
                linesPanel.invalidate();
            }
        }
        super.setBounds(x, y, w, h, notifyParent);
    }

	/**
	 * processFocusEvent
	 * @param e		the firing FocusEvent
	 */
	protected void processFocusEvent(FocusEvent e) {
        if (e.getID() == FocusEvent.FOCUS_GAINED) {
    	    linesPanel.requestFocus();
        }
	    super.processFocusEvent( e );
	}
}
