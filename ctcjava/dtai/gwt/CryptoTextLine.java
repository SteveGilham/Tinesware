
//Title:        CTC2.0 for Java
//Version:
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware

// have to be in this package for access to ForegroundSize
package dtai.gwt;


/****************************************************************
 **
 **  $Id: TextLine.java,v 1.27 1998/02/22 00:26:10 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/TextLine.java,v $
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
 **  This class has been modified to use the CryptoTextBuffer and
 **  CryptoString classes, with their memory wiping capabilities.
 **  This leaks a little in cut and paste opeartions, alas.
 **  This also gives me a chance to wire in TextEvent handling for
 **  the GWT components.  --Mr. Tines, May '98
 **
 ****************************************************************/

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import com.ravnaandtines.ctcjava.CryptoString;
import com.ravnaandtines.ctcjava.CryptoTextBuffer;
import com.ravnaandtines.ctcjava.CryptoTextTokenizer;

/**
 * TextLine
 * @version 1.1
 * @author DTAI, Incorporated
 * @version CryptoTextLine 1.0
 * @author Mr. Tines
 */
public class CryptoTextLine extends DisplayGadget {

    CryptoTextBuffer text;
    Color overrideForeground;
    private boolean editable = true;
    private int columns;
    private int drawXOffset = 0;
    private char echoChar = '\0';
    int selectionStart = 0;
    int selectionEnd = 0;
    int caretPosition = 0;
    boolean mouseDown = false;
    private boolean showCursor = false;
    private CryptoTextBuffer undoText;
    private int undoSelectionStart = 0;
    private int undoSelectionEnd = 0;
    private int undoCaretPosition = 0;
    private boolean startUndo = true;

    private boolean lineBreak = false;
    private Rectangle selectionRect;

    public void wipe() // what it's all about!
    {
        if(null == text) return;
        text.wipe();
    }
    protected void finalize() throws Throwable
    {
        //System.out.println("CryptoTextLine finalizing");
        wipe();
        super.finalize();
        //System.out.println("CryptoTextLine finalized");
    }

    /**
     * TextLine
     */
    public CryptoTextLine() {
        this("");
    }

    /**
     * TextLine
     * @param text
     */
    public CryptoTextLine(String text) {
        setBackground(Gadget.transparent);
        setHorizAlign( HORIZ_ALIGN_LEFT );
        setText(text);
        setCursor(GadgetCursor.getPredefinedCursor(GadgetCursor.TEXT_CURSOR));
    }

    /**
     * gets the preffered size
     * @return Dimension
     */
    public Dimension getPreferredSize() {
        FontMetrics metrics = getFontMetrics(getFont());
        CryptoString str = new CryptoString(text.getText());
        pref_width = metrics.charsWidth(str.getText(),0,str.length())+2;
        if (metrics instanceof SpecialFontMetrics) {
            pref_height = ((SpecialFontMetrics)metrics).charsAscent(str.getText(),0,str.length()) +
                metrics.getDescent() + metrics.getLeading();
        } 
        else {
            pref_height = metrics.getHeight();
        }
        return new Dimension( pref_width, pref_height );
    }

    /**
     * gets the text length
     * @return int
     */
    public int getTextLength() {
        return text.length();
    }

    /**
     * gets the text of the button
     * @return String
     */
    public char[] getText() {
        return text.getText();
    }

    /**
     * toString
     * @return String
     */
    public String toString() {
        return text.toString();
    }

    /**
     * setText
     * @param text
     */
    public void setText( String text ) {
        synchronized(getTreeLock()) {

            if ( startUndo ) {
                saveUndo();
            }
            if ( text == null ) {
                text = "";
                select( 0 );
            }
            if ( ( this.text == null ) ||
                ( ! this.text.toString().equals( text ) ) ) {
                this.text = new CryptoTextBuffer(text.length());
                char [] delim = {
                    '\t','\n','\r'                };
                CryptoTextTokenizer st = new CryptoTextTokenizer(
                text.toCharArray(),
                delim );
                while ( st.hasMoreTokens() ) {
                    this.text.append( st.nextToken() );
                    if ( st.hasMoreTokens() ) {
                        this.text.append( " " );
                    }
                }
                select( 0 );
                repaint();
            }
        }
    }

    /**
     * Returns the boolean indicating whether this TextComponent is editable or not.
     * @see setEditable
     * @return boolean
     */
    public final boolean isEditable() {
        return editable;
    }

    /**
     * Sets the specified boolean to indicate whether or not this TextComponent should be editable.
     * @param  b - the boolean to be set
     * @see isEditable
     */
    public final void setEditable(boolean b) {
        editable = b;
        if (editable) {
            setCursor(GadgetCursor.getPredefinedCursor(GadgetCursor.TEXT_CURSOR));
        } 
        else {
            setCursor(null);
        }
    }

    /**
     * insertText
     * @param str
     * @param pos
     */
    public void insertText(String str, int pos) {
        if (pos < 0) {
            pos = Math.max(0,text.length()-(pos+1));
        }
        text.insert( pos, str );
    }

    /**
     * insertText
     * @param c
     * @param pos
     */
    public void insertText(char c, int pos) {
        text.insert( caretPosition, c );
    }

    /**
     * appendText
     * @param str
     */
    public void appendText(String str) {
        text.append(str);
    }

    /**
     * appendText
     * @param c
     */
    public void appendText(char c) {
        text.append(c);
    }

    /**
     * replaceText
     * @param str
     * @param start
     * @param end
     */
    public void replaceText(String str, int start, int end) {
        CryptoString string = new CryptoString(text.getText());
        text = new CryptoTextBuffer( string.getText(), 0, start);
        text.append( str );
        text.append( string.getText(), end );
    }

    /**
     * replaceText
     * @param c
     * @param start
     * @param end
     */
    public void replaceText(char c, int start, int end) {
        CryptoString string = new CryptoString(text.getText());
        text = new CryptoTextBuffer( string.getText(), 0, start );
        text.append( c );
        text.append( string.getText(), end);
    }

    /**
     * getShowCursor
     * @return boolean
     */
    public boolean getShowCursor() {
        return showCursor;
    }

    /**
     * setShowCursor
     * @param showCursor
     */
    public void setShowCursor( boolean showCursor ) {
        this.showCursor = showCursor;
        repaint();
    }

    /**
     * getMouseDown
     * @return boolean
     */
    public boolean getMouseDown() {
        return mouseDown;
    }

    /**
     * setMouseDown
     * @param mouseDown
     */
    public void setMouseDown( boolean mouseDown ) {
        this.mouseDown = mouseDown;
    }

    /**
     * toggleCursor
     */
    public void toggleCursor() {
        setShowCursor( ! showCursor );
    }

    /**
     * getColumns
     * @return int
     */
    public int getColumns() {
        return columns;
    }

    /**
     * isTextSelected
     * @return boolean
     */
    public boolean isTextSelected() {
        if (selectionStart == selectionEnd) {
            return false;
        } 
        else {
            return true;
        }
    }

    /**
     * setColumns
     * @param columns
     */
    public void setColumns( int columns ) {
        synchronized(getTreeLock()) {
            this.columns = columns;
            Dimension min_size = getMinimumSize();

            if ( ( width != min_size.width ) ||
                ( height != min_size.height ) ) {
                invalidate();
            }
        }
    }

    /**
     * getEchoChar
     * @return char
     */
    public char getEchoChar() {
        return echoChar;
    }

    /**
     * setEchoChar
     * @param c
     */
    public void setEchoChar(char c) {
        echoChar = c;
        repaint();
    }

    /**
     * echoCharIsSet
     * @return boolean
     */
    public boolean echoCharIsSet() {
        return ( echoChar > '\0' );
    }

    /**
     * saveUndo
     */
    protected void saveUndo() {
        undoText = text;
        undoSelectionStart = selectionStart;
        undoSelectionEnd = selectionEnd;
        undoCaretPosition = caretPosition;
        startUndo = false;
    }

    /**
     * undo
     */
    protected void undo() {
        if ( undoText != null ) {
            CryptoTextBuffer tempText = text;
            int tempSelectionStart = selectionStart;
            int tempSelectionEnd = selectionEnd;
            int tempCaretPosition = caretPosition;

            text = undoText;
            selectionStart = undoSelectionStart;
            selectionEnd = undoSelectionEnd;
            caretPosition = undoCaretPosition;

            undoText = tempText;
            undoSelectionStart = tempSelectionStart;
            undoSelectionEnd = tempSelectionEnd;
            undoCaretPosition = tempCaretPosition;
            repaint();
        }
    }

    /**
     * getSelectBg
     * @param g
     * @param fg
     * @return Color
     */
    protected Color getSelectBg(GadgetGraphics g, Color fg) {
        Color selectionColor = getSelectedBackground(g);
        if ( selectionColor == null ) {
            return fg;
        }
        else {
            return selectionColor;
        }
    }

    private Color getSelectFg(GadgetGraphics g) {
        Color selectedTextColor = getSelectedForeground(g);
        if ( selectedTextColor == null ) {
            return getNormalBackground(g);
        }
        else {
            return selectedTextColor;
        }
    }

    private void paintSelection( GadgetGraphics g, Color fg, int x, int y, int text_y,
    CryptoString draw_string,
    int text_height ) {

        if ( selectionStart < selectionEnd ) {
            FontMetrics metrics = getFontMetrics(getFont());
            int start_x = x + metrics.charsWidth(
            draw_string.getText(), 0, selectionStart);
            int end_x = x + metrics.charsWidth(
            draw_string.getText(), 0, selectionEnd);

            g.setColor( getSelectBg(g,fg) );
            selectionRect = new Rectangle(start_x, y, ( end_x - start_x ), text_height);
            g.fillRect(selectionRect.x,selectionRect.y,
            selectionRect.width,selectionRect.height);

            g.setColor( getSelectFg(g) );
            g.drawChars(draw_string.getText(),
            selectionStart, selectionEnd-selectionStart+1,
            start_x, text_y );
        }
    }

    public Rectangle getSelectionRect() {
        if (selectionRect == null) {
            return new Rectangle(0, 0, 0, height);
        }
        return new Rectangle(selectionRect.x,selectionRect.y,selectionRect.width,selectionRect.height);
    }

    /**
     * paintDisabledForeground
     * @param g
     * @param x
     * @param y
     * @param width
     * @param height
     */
    protected void paintDisabledForeground( GadgetGraphics g,
    int x, int y, int width, int height) {
        paintForeground(g,x,y,width,height);
    }

    /**
     * paints the foreground
     * @param g
     * @param x
     * @param y
     * @param width
     * @param height
     */
    protected void paintForeground( GadgetGraphics g,
    int x, int y, int width, int height) {
        x=0;
        Color fg;
        if ( overrideForeground == null ) {
            fg = getForeground(g);
        }
        else {
            fg = overrideForeground;
        }
        if (!editable && parent instanceof TextFieldGadget &&
            parent.parent instanceof ChoiceGadget && hasFocus()) {
            g.setColor(getSelectBg(g,fg));
            g.fillRect(0,0,width,height);
            fg = getSelectFg(g);
        }
        if ( fg != null ) {
            CryptoString draw_string;

            if ( echoCharIsSet() ) {
                CryptoTextBuffer echo = new CryptoTextBuffer( text.length() );
                for ( int i = 0; i < text.length(); i++ ) {
                    echo.append( echoChar );
                }
                draw_string = new CryptoString(echo.getText());
            }
            else {
                draw_string = new CryptoString(getText());
            }

            FontMetrics metrics = g.getFontMetrics();
            int ascent;
            if (metrics instanceof SpecialFontMetrics) {
                ascent = ((SpecialFontMetrics)metrics).charsAscent(
                draw_string.getText(), 0, draw_string.length());
            } 
            else {
                ascent = metrics.getAscent();
            }
            int text_y = y + ascent;
            int text_height = ascent + metrics.getDescent() + metrics.getLeading();

            int visibleWidth = getSize().width;

            int string_width = metrics.charsWidth( draw_string.getText(), 0, draw_string.length() );
            int pixels_to_cursor = metrics.charsWidth( draw_string.getText(), 0, caretPosition );

            int align = getHorizAlign();

            if ( ( visibleWidth <= 0 ) ||
                ( string_width + 2 < visibleWidth ) ) {
                drawXOffset = 0;
            }
            else {
                if ( hasFocus() ) {
                    align = HORIZ_ALIGN_LEFT;
                }

                int offset_pixels_to_cursor = drawXOffset + pixels_to_cursor;

                while ( offset_pixels_to_cursor < 0 ) {
                    drawXOffset += visibleWidth / 5;
                    if ( drawXOffset > 0 ) {
                        drawXOffset = 0;
                    }
                    offset_pixels_to_cursor = drawXOffset + pixels_to_cursor;
                }
                while ( offset_pixels_to_cursor > visibleWidth ) {
                    drawXOffset -= visibleWidth / 5;
                    if ( drawXOffset < ( visibleWidth - string_width - 1 ) ) {
                        drawXOffset = visibleWidth - string_width - 1;
                    }
                    offset_pixels_to_cursor = drawXOffset + pixels_to_cursor;
                }
            }

            if ( align != HORIZ_ALIGN_LEFT ) {
                drawXOffset = visibleWidth - ( string_width + 2 );
            }
            if ( align == HORIZ_ALIGN_CENTER ) {
                drawXOffset /= 2;
            }

            x += drawXOffset;

            g.setColor(fg);
            g.drawChars( draw_string.getText(), 0, draw_string.length(), x + 1             /* room for cursor */, text_y );

            if ( hasFocus()) {
                selectionRect = null;
                paintSelection( g, fg, x + 1                 /* room for cursor */, y, text_y,
                draw_string, text_height );

                if ( showCursor && editable ) {
                    int cursor_x = x + 1 + pixels_to_cursor;
                    g.setColor( fg );
                    g.drawLine( cursor_x, y, cursor_x, y + text_height - 1 );
                    if (selectionRect == null) {
                        selectionRect = new Rectangle(cursor_x, y, 1, text_height);
                    }
                }
            }
        }
    }

    /**
     * findTextPos
     * @param x
     * @return int
     */
    protected int findTextPos( int x ) {
        // for now, assume we have not scrolled at all
        FontMetrics metrics = getFontMetrics( getFont() );
        x -= drawXOffset;
        if ( x < 0 ) {
            return 0;
        }
        CryptoString str = new CryptoString(getText());
        int prev_x = 0;
        int i;
        for ( i = 0; i < str.length(); i++ ) {
            int next_x = metrics.charsWidth( str.getText(), 0, i+1 );
            if ( x < ( next_x - ( ( next_x - prev_x ) / 2 ) ) ) {
                break;
            }
            prev_x = next_x;
        }
        return i;
    }

    /**
     * findCaretPosition
     * @param pos
     * @return int
     */
    protected int findCaretPosition( int pos ) {
        FontMetrics metrics = getFontMetrics( getFont() );
        CryptoString str = new CryptoString(getText());
        if (pos > length()) {
            pos = length();
        }
        return metrics.charsWidth( str.getText(), 0, pos );
    }

    /**
     * getForegroundSize
     * @param g
     * @return Dimension
     */
    protected Dimension getForegroundSize( GadgetGraphics g ) {
        return getForegroundSize( g, columns );
    }

    /**
     * getForegroundSize
     * @param g
     * @param columns
     * @return Dimension
     */
    protected Dimension getForegroundSize( GadgetGraphics g, int columns ) {
        FontMetrics metrics = getFontMetrics(getFont(g));
        Dimension fg = new Dimension();
        CryptoString str = new CryptoString(text.getText());
        if (metrics instanceof SpecialFontMetrics) {
            fg.height = ((SpecialFontMetrics)metrics).charsAscent(str.getText(), 0, str.length()) +
                metrics.getDescent() + metrics.getLeading();
        } 
        else {
            fg.height = metrics.getHeight();
        }
        if (columns == 0) {
            CryptoString st2 = new CryptoString(getText());
            fg.width = metrics.charsWidth(st2.getText(), 0, st2.length());
        }
        else {
            int maxwidth = metrics.stringWidth( "W" );
            fg.width = (columns * maxwidth);
        }
        fg.width += 2; // the 1-pixel-wide cursor has to fit
        // at beginning or end of the string.
        return fg;
    }

    /**
     * resets the foreground size
     * @param g
     */
    protected void resetForegroundSize( GadgetGraphics g ) {
        Dimension fgsize = getForegroundSize(g);
        fgHeight = fgsize.height;
        fgWidth = width;
    }

    /**
     * selectFromCursor
     * @param pos
     */
    protected void selectFromCursor(int pos) {
        if ( caretPosition == selectionStart ) {
            select( selectionEnd, pos );
        }
        else {
            select( selectionStart, pos );
        }
    }

    /**
     * deleteSelection
     */
    protected void deleteSelection() {
        //synchronized(getTreeLock()) {
        if ( selectionStart < selectionEnd ) {
            if ( startUndo ) {
                saveUndo();
            }
            CryptoString str = new CryptoString(getText());
            text = new CryptoTextBuffer();
            if ( selectionStart > 0 ) {
                text.append( str.getText(), 0, selectionStart );
            }
            if ( selectionEnd < str.length() ) {
                text.append( str.getText(), selectionEnd, str.length() );
            }

            selectionEnd = selectionStart;
            caretPosition = selectionStart;
            startUndo = true;
        }
        //}
    }

    /**
     * backspaceKey
     */
    protected void backspaceKey() {
        synchronized(getTreeLock()) {
            if ( ( selectionStart == selectionEnd ) &&
                ( selectionStart > 0 ) ) {
                selectionStart--;
            }
            deleteSelection();
        }
    }

    /**
     * deleteKey
     */
    protected void deleteKey() {
        synchronized(getTreeLock()) {
            if ( startUndo ) {
                saveUndo();
            }
            if ( ( selectionStart == selectionEnd ) &&
                ( selectionEnd < text.length() ) ) {
                selectionEnd++;
            }
            deleteSelection();
        }
    }

    /**
     * typeChar
     * @param c
     */
    protected void typeChar( char c ) {
        synchronized(getTreeLock()) {
            if ( startUndo ) {
                saveUndo();
            }
            deleteSelection();
            insertText(c,caretPosition);
            select( caretPosition+1 );
            repaint();
        }
    }

    /**
     * cut
     */
    protected void cut() {
        synchronized(getTreeLock()) {
            GadgetClipboard.setText( getSelectedText() );
            deleteSelection();
            repaint();
        }
    }

    /**
     * copy
     */
    protected void copy() {
        synchronized(getTreeLock()) {
            GadgetClipboard.setText( getSelectedText() );
        }
    }

    /**
     * paste
     */
    protected void paste() {
        synchronized(getTreeLock()) {
            if ( startUndo ) {
                saveUndo();
            }
            deleteSelection();
            String toPaste = GadgetClipboard.getText();
            text.insert( caretPosition, toPaste );
            select( selectionStart+toPaste.length() );
            repaint();
        }
    }

    /**
     * moveTo
     * @param nextPos
     * @param shiftDown
     */
    protected void moveTo( int nextPos, boolean shiftDown ) {
        synchronized(getTreeLock()) {
            if ( nextPos >= 0 ) {
                if ( shiftDown ) {
                    if ( caretPosition == selectionStart ) {
                        select( selectionEnd, nextPos );
                    }
                    else {
                        select( selectionStart, nextPos );
                    }
                }
                else {
                    select( nextPos );
                }
            }
        }
    }

    /**
     * moveLeft
     * @param ctrlDown
     * @param shiftDown
     */
    protected void moveLeft( boolean ctrlDown, boolean shiftDown ) {
        synchronized(getTreeLock()) {
            int nextPos = -1;
            if ( ! ctrlDown ) {
                nextPos = caretPosition - 1;
            }
            else {
                nextPos = caretPosition;
                CryptoString str = new CryptoString(getText());
                if ( ( nextPos == str.length() ) ||
                    ( str.charAt( nextPos ) != ' ' ) ) {
                    nextPos--;
                }
                while ( ( nextPos >= 0 ) &&
                    ( str.charAt( nextPos ) == ' ' ) ) {
                    nextPos--;
                }
                while ( ( nextPos >= 0 ) &&
                    ( str.charAt( nextPos ) != ' ' ) ) {
                    nextPos--;
                }
                if ( ( nextPos >= 0 ) &&
                    ( str.charAt( nextPos ) == ' ' ) ) {
                    nextPos++;
                }
            }
            if ( nextPos < 0 ) {
                nextPos = 0;
            }
            moveTo( nextPos, shiftDown );
        }
    }

    /**
     * moveRight
     * @param ctrlDown
     * @param shiftDown
     */
    protected void moveRight( boolean ctrlDown, boolean shiftDown ) {
        synchronized(getTreeLock()) {
            int nextPos = -1;
            if ( ! ctrlDown ) {
                nextPos = caretPosition + 1;
            }
            else {
                nextPos = caretPosition;
                CryptoString str = new CryptoString(getText());
                while ( ( nextPos < str.length() ) &&
                    ( str.charAt( nextPos ) == ' ' ) ) {
                    nextPos++;
                }
                while ( ( nextPos < str.length() ) &&
                    ( str.charAt( nextPos ) != ' ' ) ) {
                    nextPos++;
                }
                while ( ( nextPos < str.length() ) &&
                    ( str.charAt( nextPos ) == ' ' ) ) {
                    nextPos++;
                }
            }
            if ( nextPos > text.length() ) {
                nextPos = text.length();
            }
            moveTo( nextPos, shiftDown );
        }
    }

    /**
     * Returns the selected text's start position.
     * @return int
     */
    public int getSelectionStart() {
        return selectionStart;
    }

    /**
     * Sets the selection start to the specified position.
     * @param selectionStart - the start position of the text
     * The new starting point is constrained to be before or at the current selection end.
     */
    public void setSelectionStart(int selectionStart) {
        synchronized(getTreeLock()) {
            if ( selectionStart < 0 ) {
                selectionStart = 0;
            }
            else if ( selectionStart > text.length() ) {
                selectionStart = text.length();
            }
            this.selectionStart = selectionStart;
            caretPosition = selectionStart;
            startUndo = true;
            repaint();
        }
    }

    /**
     * Returns the selected text's end position.
     * @return int
     */
    public int getCaretPosition() {
        return caretPosition;
    }

    /**
     * Sets the selected text's end position.
     * @param caretPosition
     */
    public void setCaretPosition(int caretPosition) {
        if (caretPosition != this.caretPosition) {
            this.caretPosition = caretPosition;
            startUndo = true;
            repaint();
        }
    }

    /**
     * Returns the selected text's end position.
     * @return int
     */
    public int getSelectionEnd() {
        return selectionEnd;
    }

    /**
     * Sets the selection end to the specified position. The new end point is constrained to be at or after the current selection start.
     * @param selectionEnd - the start position of the text
     */
    public void setSelectionEnd(int selectionEnd) {
        synchronized(getTreeLock()) {
            if ( selectionEnd < 0 ) {
                selectionEnd = 0;
            }
            else if ( selectionEnd > text.length() ) {
                selectionEnd = text.length();
            }
            this.selectionEnd = selectionEnd;
            caretPosition = selectionEnd;
            startUndo = true;
            repaint();
        }
    }

    /**
     * Selects the text found between the specified start and end locations.
     * @param selectionStart - the start position of the text
     * @param selectionEnd - the end position of the text
     */
    public void select( int selectionStart,
    int selectionEnd ) {
        synchronized(getTreeLock()) {
            if ( selectionStart < 0 ) {
                selectionStart = 0;
            }
            else if ( selectionStart > text.length() ) {
                selectionStart = text.length();
            }
            if ( selectionEnd < 0 ) {
                selectionEnd = 0;
            }
            else if ( selectionEnd > text.length() ) {
                selectionEnd = text.length();
            }
            if ( selectionStart > selectionEnd ) {
                this.selectionStart = selectionEnd;
                this.selectionEnd = selectionStart;
            }
            else {
                this.selectionStart = selectionStart;
                this.selectionEnd = selectionEnd;
            }
            caretPosition = selectionEnd;
            startUndo = true;
            repaint();
        }
    }

    /**
     * select
     * @param pos
     */
    public void select( int pos ) {
        select( pos, pos );
    }

    /**
     * Selects all the text in the TextComponent.
     */
    public void selectAll() {
        select( 0, text.length() );
    }

    /**
     * Selects all the text in the TextComponent.
     * @return int
     */
    public int length() {
        return text.length();
    }

    /**
     * Returns the selected text contained in this TextComponent.
     * @see setText
     * @return String
     */
    public String getSelectedText() {
        synchronized(getTreeLock()) {
            CryptoString s = new CryptoString(getText());
            return new String(s.getText(), selectionStart, selectionEnd-selectionStart+1 );
        }
    }

    /**
     * sets if there is a carrage return at line of line
     * @param in
     */
    public void setLineBreak (boolean in) {
        lineBreak = in;
    }

    /**
     * returns true if the line had a carrage return at end
     * @return boolean
     */
    public boolean getLineBreak ( ) {
        return lineBreak;
    }

}

