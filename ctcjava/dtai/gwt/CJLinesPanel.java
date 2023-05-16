/****************************************************************
 **
 **  $Id: LinesPanel.java,v 1.9 1998/03/10 20:30:42 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/LinesPanel.java,v $
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
 **
 **  History:-
 **  This class has been modified to wire in TextEvent handling for
 **  the GWT components.  --Mr. Tines, May '98
 **  Also using the JDk 1.1 clipboard, and a few other special-case
 **  editing functions for mailer style use.
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
import java11.awt.event.AWTEvent;
import java11.awt.event.FocusEvent;
import java11.awt.event.InputEvent;
import java11.awt.event.KeyEvent;
import java11.awt.event.MouseEvent;
import java11.awt.event.FocusListener;
import java11.awt.event.MouseListener;
import java11.awt.event.TextEvent;
import java11.awt.event.TextListener;
import java.awt.datatransfer.*;

/**
 * LinesPanel
 * @version 1.1
 * @author DTAI, Incorporated
 */
class CJLinesPanel extends PanelGadget implements Runnable {

    private TextLine invalidLine;
    private boolean invalidatingLine = false;
    private boolean wholeInvalid = false;
    private final static int NONE = 0;
    private final static int UP = 1;
    private final static int DOWN = 2;
    private final static int LEFT = 3;
    private final static int RIGHT = 4;

    private CJTextAreaGadget textarea;
    private int selectionStart = 0;
    private int selectionEnd = 0;
    private boolean ignoreMouse = false;
    private long lastClickWhen = 0;
    private int lastClickPos = -1;
    private int dragDirectionX = NONE;
    private int dragDirectionY = NONE;
    private Rectangle parentSize = new Rectangle (0, 0, 0, 0);

    private String undoText;
    private int undoSelectionStart = 0;
    private int undoSelectionEnd = 0;
    private int undoCaretPosition = 0;
    private boolean startUndo = true;

    private Thread thread;
    protected TextLine currentLine;
    boolean hasFocus = false;
    boolean focusable = false;
    private Color overrideBackground;
    private int saveStart, saveEnd;

    private Font lastFont = new Font("Dialog", Font.PLAIN, 12);
    private FontMetrics lastMetrics = null;
    private int[] lastWidths = null;

    TextListener textListener = null;
    MouseListener mouseListener = null;

    /**
     * LinesPanel
     * @param textarea - TBD
     */
    public CJLinesPanel(CJTextAreaGadget textarea) {
        this.textarea = textarea;
        setCursor(GadgetCursor.getPredefinedCursor(GadgetCursor.TEXT_CURSOR));
        setLayout(null);
    }

    /**
     * Sets the text of this TextComponent to the specified text.
     * @param t the new text to be set
     * @see #getText
     */
    public void setText(String text) {
	    if ( startUndo ) {
	        saveUndo();
	    }
    	removeAll();
        boolean newline = false;
        StringTokenizer toks = new StringTokenizer( text, "\n", true );
        while ( toks.hasMoreTokens() ) {
            String line = toks.nextToken();
            if (line.equals("\n")) {
                if (!newline) {
                    newline = true;
                    continue;
                }
                line = "";
            } else {
                newline = false;
            }
            TextLine newLine = new TextLine(line + " ");
    	    if (!line.equals(text) && toks.hasMoreTokens()) {
    	        newLine.setLineBreak(true);
    	    }
    	    add(newLine);
    	}

    	if (getGadgetCount() == 0) {
            TextLine newLine = new TextLine(text + " ");
    	    newLine.setLineBreak(false);
    	    add(newLine);
    	}
    	currentLine = (TextLine)getGadget(0);
    	invalidate();
	    sizeControl();
        setCaretPosition(0);
        textarea.invalidate();
    }

	/**
     * returns true if children can overlap each other.
     * @return false
	 */
	public boolean childrenCanOverlap() {
	    return false;
	}

    /**
     * update
     * @param g - TBD
     */
    public void update(GadgetGraphics g) {
        synchronized(getTreeLock()) {
            if (!textarea.isEditable() || !textarea.isFunctioning()) {
                overrideBackground = normalBackground;
                normalBackground = Gadget.transparent;
                super.update(g);
                normalBackground = overrideBackground;
                overrideBackground = null;
            }
            else {
                super.update(g);
            }
            if (textarea.getShowGuides()) {
                g = getGadgetGraphics();
                drawGuides(g);
                g.dispose();
            }
        }
    }

    private void drawGuides(GadgetGraphics g) {
        g.setColor(getForeground(g));
        Rectangle areaBounds = textarea.getBounds();
        Rectangle bounds = getBounds();
        areaBounds.x = -bounds.x;
        areaBounds.y = -bounds.y;
        for (int i = 0; i < gadgetCount; i++) {
            Rectangle lineBounds = gadgets[i].getBounds();
            if (areaBounds.intersects(lineBounds)) {
                int y = lineBounds.y+lineBounds.height-1;
                g.drawLine(0,y,bounds.width,y,1,1,1);
            }
        }
    }

    /**
     * Returns the text contained in this TextComponent.
     * @see #setText
     * @return String
     */
    public String getText() {
        synchronized(getTreeLock()) {
    		String text = new String("");
            TextLine line = null;
            for (int i = 0; i < gadgetCount; i++) {
                line = (TextLine) gadgets[i];
                if (line.length() > 0) {
                    int pos;
                    if (line.getText().charAt(line.length()-1) == ' ') {
                        pos = line.length()-1;
                    }
                    else {
                        pos = line.length();
                    }
                    text += line.getText().substring(0, pos);
                }
    	        if (line.getLineBreak() ||
    	            (!textarea.getWordWrap() && i > 0)) {
                    text += "\n";
                }
            }
            return text;
        }
    }

    public Rectangle getFirstSelectionRect() {
        TextLine line = findLine(getSelectionStart());
        if (line != null) {
            Rectangle rect = line.getSelectionRect();
            if (rect != null) {
                rect.x += line.x;
                rect.y += line.y;
            }
            return rect;
        }
        return null;
    }

    /**
     * Returns the selected text contained in this TextComponent.
     * @see #setText
     * @return String
     */
    public String getSelectedText(boolean force)
    {
        String text = "";
        TextLine line = null;
        boolean firstTime = true;
        for (int i = 0; i < gadgetCount; i++)
        {
            line = (TextLine) gadgets[i];
            String selText = line.getSelectedText();
            boolean wholeLine = false;
            if (selText.length() > 0 )
            {
                if ((firstTime) &&
                  (selectionEnd > selectionStart+selText.length()))
                {
                    wholeLine = true;
                    firstTime = false;
                }
                if (selText.length() == line.length())
                {
                    selText = selText.substring(0, selText.length()-1);
                    wholeLine = true;
                }
                if(force)
                {
                    int x = selText.length()-1;
                    while(Character.isWhitespace(selText.charAt(x))) --x;
                }
                text += selText;
                if ((line.getLineBreak()||force) && wholeLine)
                {
                    text += "\n";
                }
            }
        }
        return text;
    }

    /**
     * Returns the Caret Position.
     * @return int
     */
    public int getCaretPosition() {
    	return selectionEnd;
    }

    /**
     * Sets the Caret Position.
     * @param int - TBD
     */
    public void setCaretPosition(int position) {
        select(position, position);
    }

    /**
     * Returns the selected text's start position.
     * @return int
     */
    public int getSelectionStart() {
    	return selectionStart;
    }

    /**
     * Returns the selected text's end position.
     * @return int
     */
    public int getSelectionEnd() {
    	return selectionEnd;
    }

    /**
     * Set the Selection Start
     * @param position - TBD
     */
    public void  setSelectionStart(int position) {
        select(position, selectionEnd);
    }

    /**
     * Set the Selection End
     * @param position - TBD
     */
    public void setSelectionEnd(int position) {
        select(selectionStart, position);
    }

    /**
     * Selects all the text in the TextComponent.
     */
    public void selectAll() {
        select(0, getLastPosition());
    }

    /**
     * run
     */
    public void run() {
        Thread thisThread = Thread.currentThread();
        int wait = 0;
        while ( thread == thisThread ) {

            if ((wait % 5) == 0) {
                if ((!isTextSelected()) && (currentLine != null) && (hasFocus) && textarea.isEditable()) {
                    currentLine.toggleCursor();
                    wait = 0;
                }
            }

            if (dragDirectionY == UP) {
                moveUp( false, true );
            } else if (dragDirectionY == DOWN) {
                moveDown( false, true );
            }
            if (dragDirectionX == RIGHT) {
                if (!atEndOfLine()) {
                    moveRight( false, true );
                }
            } else if (dragDirectionX == LEFT) {
                if (!atBeginOfLine()) {
                    moveLeft( false, true );
                }
            }
            try {
                thread.sleep( 100 );
                wait++;
            }
            catch ( InterruptedException ie ) {
            }
        }
    }

    private boolean atEndOfLine() {
        TextLine line = findLine(selectionEnd);
        if (line.getSelectionEnd() == line.length()-1) {
            return true;
        }
        return false;
    }

    private boolean atBeginOfLine() {
        TextLine line = findLine(selectionEnd);
        if (line.getSelectionStart() == 0) {
            return true;
        }
        return false;
    }

    /**
     * findLine
     * @param position - TBD
     * @return TextLine
     */
    protected TextLine findLine(int position) {
        int totalChars = 0;
        TextLine line = null;
        for (int i = 0; i < gadgetCount; i++) {
            line = (TextLine) gadgets[i];
            totalChars += line.length();
            if (!line.getLineBreak()) {
                totalChars -= 1;
            }
            if (totalChars > position) {
                return line;
            }
        }
        return line;
    }

    private int findLineOffset( TextLine line ) {
        int linePos = getGadgetIndex(line);
        int totalOffset = 0;
        for (int i = 0; i < linePos; i++) {
            line = (TextLine) gadgets[i];
            totalOffset = totalOffset + line.length();
            if (!line.getLineBreak()) {
                totalOffset -= 1;
            }
        }
        return totalOffset;
    }

    private int getLastPosition() {
        int totalChars = 0;
        TextLine line = null;
        for (int i = 0; i < gadgetCount; i++) {
            line = (TextLine) gadgets[i];
            totalChars += line.length();
            if (!line.getLineBreak()) {
                totalChars -= 1;
            }
        }
        //if (totalChars == 1) totalChars--; // I say always drop last char
        //totalChars--; // but we don't need this either
        return totalChars;
    }

    private void stopThread() {
        synchronized(getTreeLock()) {
            dragDirectionX = NONE;
            dragDirectionY = NONE;
            if ( thread != null ) {
                thread = null;
                if (currentLine != null) {
                    currentLine.setShowCursor(false);
                }
            }
        }
    }

    private void startThread() {
        synchronized(getTreeLock()) {
            if ( thread == null ) {
                if (currentLine == null && gadgetCount > 1) {
                    currentLine = (TextLine)gadgets[1];
                }
                if ((!isTextSelected()) && (currentLine != null) && textarea.isEditable()) {
                    currentLine.setShowCursor(false);
                    currentLine.repaint();
                }
                thread = new Thread(this, "dtai.gwt.TextAreaGadget");
                thread.start();
            }
        }
    }

    /**
     * isTextSelected
     * @return boolean
     */
    public boolean isTextSelected() {
        if (selectionStart == selectionEnd) {
            return false;
        }
        return true;
    }

    /**
     * select
     * @param startPos - TBD
     * @param endPos - TBD
     */
    public void select (int startPos, int endPos) {
        int runningCount = 0;
        boolean selecting = false;

        this.selectionStart = startPos;
        this.selectionEnd = endPos;
        clearAllSelections();
        if (startPos == endPos) {
            TextLine line = findLine(startPos);
            int startOffset = findLineOffset(line);
            line.select(startPos-startOffset);
            if (textarea.isEditable()) {
                line.setShowCursor(true);
            }
            currentLine = line;
        }
        else {
            if (endPos < startPos) {
                int tmp = startPos;
                startPos = endPos;
                endPos = tmp;
            }
            for (int i = 0; i < gadgetCount; i++) {
                TextLine line = (TextLine) gadgets[i];
                runningCount = runningCount + line.length();
                if (!line.getLineBreak()) {
                    runningCount -= 1;
                }
                if ( (startPos < runningCount) && (selecting == false) ){
                    int offset = startPos - (runningCount - line.length());
                    if (!line.getLineBreak()) {
                        offset -= 1;
                    }
                    if (endPos < runningCount) {
                        int endOffset = endPos - (runningCount - line.length());
                        if (!line.getLineBreak()) {
                            endOffset -= 1;
                        }
                        line.select(offset, endOffset);
                        scrollVisible();
                        return;
                    } else {
                        line.select(offset, line.length());
                    }
                    selecting = true;
                } else if (selecting == true) {
                    line.selectAll();
                }
                if (endPos < runningCount) {
                    int offset = endPos - (runningCount - line.length());
                    if (!line.getLineBreak()) {
                        offset -= 1;
                    }
                    line.select(0, offset);
                    scrollVisible();
                    return;
                }
            }
        }
        scrollVisible();
        startUndo = true;
    }

    /**
     * getPreferredSize
     * @return Dimension
     */
    public Dimension getPreferredSize() {
		parentSize = getParent().getBounds();
        int width = 0, height = 0;
        for (int i = 0; i < gadgetCount; i++) {
            Dimension d = gadgets[i].getPreferredSize();
            height = height + d.height;
            if (d.width > width) width = d.width;
        }
        return new Dimension(width, height+1);
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
        if (startUndo) {
            saveUndo();
        }
        select(start,end);
        deleteSelection();
        insert(str,start);
        startUndo = true;
    }


    /**
     * insert
     * @param str - TBD
     * @param pos - TBD
     */
    public void insert(String str, int pos) {
        select(pos, pos);
        String extraText = null;
        if (currentLine != null) {
            boolean hasLineBreaks = false;
            boolean firstTime = true;
            TextLine newLine;
            int linePos = getGadgetIndex(currentLine);
            boolean newline = false;
            StringTokenizer toks = new StringTokenizer( str, "\n", true );
            while ( toks.hasMoreTokens() ) {
                String line = toks.nextToken();
                if (line.equals("\n")) {
                    if (!newline) {
                        newline = true;
                        continue;
                    }
                    line = "";
                } else {
                    newline = false;
                }
    	        if (line.equals(str)) {
    	            int lineSelStart = currentLine.getSelectionStart();
    	            if ( lineSelStart >= currentLine.length()-1) {
                        currentLine.insertText(str+" ", lineSelStart);
                        select(pos+str.length(), pos+str.length());
                    } else {
                        currentLine.insertText(str, lineSelStart);
                        select(pos+str.length(), pos+str.length());
                    }
                    invalidateLine();
                    return;
    	        }
    	        TextLine textLine;
    	        if (firstTime) {
    	            firstTime = false;
    	            textLine = findLine(selectionStart);
    	            textLine.selectFromCursor(textLine.length());//-1);
    	            extraText = textLine.getSelectedText();
                    textLine.deleteSelection();
                  //  textLine.insertText(line + " ", -1);
                    textLine.insertText(line + " ", textLine.getSelectionStart());
    	        } else {
                    textLine = new TextLine(line + " ");
        	        add(textLine, ++linePos);
        	        hasLineBreaks = true;
                }
                if (toks.hasMoreTokens()) {
       	            textLine.setLineBreak(true);
       	        }
                invalidateLine(textLine);
    	    }
        }
        select(pos+str.length(), pos+str.length());
        if ((extraText != null) && (currentLine != null)) {
            currentLine.select(-1);
            currentLine.insertText(extraText, currentLine.getSelectionStart());
        }
        //invalidate();
    }

    /**
     * append
     * @param str - TBD
     */
    public void append (String str) {
        insert(str, getLastPosition());
    }

    /**
     * doLayout
     */
    public void doLayout() {
        synchronized(getTreeLock()) {
            Point saveScroll = textarea.getScrollPosition();
            if (!textarea.getWordWrap()) {
                positionControls();
            } else {
                saveStart = selectionStart;
                saveEnd = selectionEnd;

                int startPosBefore = getGadgetIndex(findLine(selectionStart));
                int endPosBefore = getGadgetIndex(findLine(selectionEnd));
                mergeLineBreaks();
                adjustForWordWrap();
                positionControls();
                int startPosAfter = getGadgetIndex(findLine(saveStart));
                int endPosAfter = getGadgetIndex(findLine(saveEnd));
                //saveStart = saveStart - (startPosBefore - startPosAfter);
                //saveEnd = saveEnd - (endPosBefore - endPosAfter);
                select(saveStart, saveEnd);
                startUndo = false;
            }
           // textarea.setScrollPosition(saveScroll);
            scrollVisible();
            invalidLine = null;
            wholeInvalid = false;
         }
    }

    private void mergeLineBreaks() {
        TextLine line;
        if (gadgetCount > 1) {
            TextLine lastLine = (TextLine) gadgets[0];
            boolean inInvalidParagraph = false;
            int i = 1;
            while (i < gadgetCount) {
                if (lastLine == invalidLine) {
                    inInvalidParagraph = true;
                }
                TextLine currentLine = (TextLine) gadgets[i];
                if (!lastLine.getLineBreak()) {
                    if (wholeInvalid || inInvalidParagraph) {
                        lastLine.select(lastLine.length()-1, lastLine.length());
                        lastLine.deleteSelection();
                        lastLine.setLineBreak(currentLine.getLineBreak());
                        lastLine.appendText(currentLine.getText());
                        remove(currentLine);
                    } else {
                        lastLine = currentLine;
                        i++;
                    }
                } else {
                    inInvalidParagraph = false;
                    lastLine = currentLine;
                    i++;
                }
            }
        }
    }

    private void positionControls() {
        int yOffset = 0;
        int lastHeight = 0;
        int maxWidth = this.width;
        for (int i = 0; i < gadgetCount; i++) {
            Dimension d = gadgets[i].getPreferredSize();
            if (!textarea.getWordWrap() && d.width > maxWidth) {
                maxWidth = d.width;
            }
            lastHeight = d.height;
            gadgets[i].setBounds(0, yOffset, maxWidth, lastHeight, false);
            yOffset = yOffset + lastHeight;
        }
        setSize(maxWidth, Math.max(this.height,(yOffset+1)));
    }

    private boolean wordWrapNeeded(TextLine line) {
        Dimension lineSize = line.getPreferredSize();
        if (lineSize.width > parentSize.width) {
            return true;
        }
        return false;
    }

    private int findLastVisibleWord(TextLine line) {
        int maxWidth = parentSize.width;
        FontMetrics metrics;
        int[] widths;
        synchronized(getTreeLock()) {
            Font font = getFont();
            if (font != lastFont) {
                lastFont = font;
                lastMetrics = getFontMetrics(lastFont);
                lastWidths = lastMetrics.getWidths();
            }
            metrics = lastMetrics;
            widths = lastWidths;
        }

        String text = line.getText();
        int len = text.length();
        char[] chars = new char[len];
        text.getChars(0, len, chars, 0);
        int currentWidth = 0;
        if (len > 0) {
            if (chars[0] >= widths.length) {
                currentWidth += metrics.charsWidth(chars,0,1);
            } else {
                currentWidth += widths[chars[0]];
            }
        }
        for (int i = 1; i < len; i++) {
            if (chars[i] >= widths.length) {
                currentWidth += metrics.charsWidth(chars,i,1);
            } else {
                currentWidth += widths[chars[i]];
            }
	        if (currentWidth >= maxWidth-widths['W']) {
	            for (int j = i-1; j > 0; j--) {
	                if (chars[j] == ' ') {
	                    return j+1;
	                }
	            }
                return i;
	        }
        }
        return 0;
    }

// THIS FUNCTION IS still a little SLOW!!!
    private void adjustForWordWrap() {
        TextLine line;
        Gadget parent = getParent();
        if (parent != null) {
            parentSize = parent.getBounds();
        }
        if (parentSize.width > 0) {
            for (int i = 0; i < gadgetCount; i++) {
                line = (TextLine) gadgets[i];
                if (wordWrapNeeded(line)) {
                    int pos = findLastVisibleWord(line);
                    int offset = findLineOffset(line);
                    int breakOffset;
                    if (currentLine != null) {
                        if (pos != 0) {
                            breakOffset = pos+offset;
                            select(breakOffset, breakOffset);
                            currentLine.selectFromCursor(currentLine.length());
                            if (currentLine.getSelectionStart() > 0) {
                                // THIS TextLine allocation is SLOW!! (Use a pool of them?)
                                TextLine newLine = new TextLine(currentLine.getSelectedText());
                                newLine.setLineBreak(currentLine.getLineBreak());
                                currentLine.setLineBreak(false);
                                currentLine.deleteSelection();
                                currentLine.setText(currentLine.getText() + " ");
                                int linePos = getGadgetIndex(currentLine);
                                add(newLine, linePos+1);
                            }
                        }
                    }
                }
            }
        }
    }

	/**
	 * isFocusTraversable
	 * @return boolean
	 */
	public boolean isFocusTraversable() {
	    if (textarea.isEditable()) {
    		return true;
    	}
    	return focusable;
	}

    /**
     * clearAllSelections
     */
    protected void clearAllSelections() {
        for (int i = 0; i < gadgetCount; i++) {
            TextLine line = ((TextLine) gadgets[i]);
            line.setShowCursor(false);
            if (line.isTextSelected()) {
                line.select(-1);
            }
        }
    }

    /**
     * collapse
     */
    protected void collapse() {
        int start, end;

        TextLine startLine = findLine(selectionStart);
        TextLine endLine = findLine(selectionEnd);

        if (startLine.y < endLine.y) {
            start = startLine.y;
            end = endLine.y;
        } else {
            start = endLine.y;
            end = startLine.y;
        }
        for (int i = gadgetCount-1; i >= 0; i--) {
            if ((gadgets[i].y > start) && (gadgets[i].y < end)) {
                remove(gadgets[i]);
            }
        }
        if (startLine == endLine) {
            startLine.deleteSelection();
            if (startLine.length() < 1) {
                if (gadgetCount > 1) {
                    remove(startLine);
                } else {
                    if (textarea.isEditable()) {
                       startLine.setShowCursor(true);
                    }
                }
            }
        } else {
            int pos;
            if (startLine.y < endLine.y) {
                joinLine(startLine,endLine);
            }
            else {
                joinLine(endLine,startLine);
            }
        }
        if (selectionStart < selectionEnd) {
            select(selectionStart, selectionStart);
        } else {
            select(selectionEnd, selectionEnd);
        }
        //sizeControl();
    }

    private void joinLine(TextLine top, TextLine bottom) {
        top.deleteSelection();
        bottom.deleteSelection();
        int pos = top.length();
        top.appendText(bottom.getText());
        top.setLineBreak(bottom.getLineBreak());
        remove(bottom);
        if (textarea.isEditable()) {
            top.setShowCursor(true);
        }
        selectionStart = findLineOffset(top) + pos;
        selectionEnd = selectionStart;
        if ((textarea.getWordWrap()) /*&& (wordWrapNeeded(top))*/) {
             invalidateLine(top);
        }
        //sizeControl();
        //invalidate();
    }

    /**
     * deleteSelection
     */
    protected void deleteSelection() {
        if (isTextSelected()) {
    	    if ( startUndo ) {
    	        saveUndo();
    	    }
            collapse();
            //invalidate();
	        startUndo = true;
        }
    }

    private void insertLine() {
        TextLine line = findLine(selectionStart);
        int linePos = getGadgetIndex(line);
        int cursorPos = line.getSelectionStart();
        line.select(cursorPos, line.length());

        TextLine newLine = new TextLine(line.getSelectedText());
        newLine.setLineBreak(line.getLineBreak());
        line.deleteSelection();
        line.appendText(" ");
        line.setLineBreak(true);
        add(newLine, linePos+1);
        selectionStart++;
        selectionEnd = selectionStart;
        invalidateLine(newLine);
    }

    /**
     * sizeControl
     */
    public void sizeControl() {
        Dimension d = getPreferredSize();
        setSize(d.width, d.height);
        repaint();
    }

    /**
     * backspaceKey
     */
    protected void backspaceKey() {
        if (isTextSelected()) {
            deleteSelection();
            scrollVisible();
            repaint();
        }
        else {
            TextLine line = findLine(selectionStart);
            if (line != null) {
                if (selectionStart > 0) {
                    selectionStart--;
                    selectionEnd = selectionStart;
                    int cursorPos = line.getSelectionStart();
                    if (cursorPos == 0) {
                        int linePos = getGadgetIndex(line);
                        if (linePos != 0) {
                            TextLine top = (TextLine) gadgets[linePos-1];
                            joinLine(top, line);
                        }
                        select(selectionStart, selectionStart);
                        TextLine newLine = findLine(selectionStart);
                        newLine.backspaceKey();
                        selectionStart--;
                    }
                    else {
                        line.backspaceKey();
        	            if ((textarea.getWordWrap()) /*&& (wordWrapNeeded(line))*/) {
                            invalidateLine(line);
        	            }
            	    }
            	    //sizeControl();
                    //textarea.invalidate();
                    select(selectionStart, selectionStart);
                    //textarea.invalidate();
    	        }
            }
	    }
    }

    /**
     * deleteKey
     */
    protected void deleteKey() {
	    if ( startUndo ) {
	        saveUndo();
	    }
        if (isTextSelected()) {
            deleteSelection();
            scrollVisible();
            repaint();
        }
        else {
            TextLine line = findLine(selectionStart);
            if (line != null) {
                if (selectionStart < getLastPosition()-1) {
                    int cursorPos = line.getSelectionStart();
                    if (cursorPos == line.length()-1) {
                        int linePos = getGadgetIndex(line);
                        if (linePos != getGadgetCount()-1) {
                            TextLine bottom = (TextLine) gadgets[linePos+1];
                            joinLine(line,bottom);
                            selectionStart--;
                        }
                        select(selectionStart, selectionStart);
                        TextLine newLine = findLine(selectionStart);
                        newLine.deleteKey();
                    }
                    else {
                        line.deleteKey();
        	            if ((textarea.getWordWrap()) /*&& (wordWrapNeeded(line))*/) {
                            invalidateLine(line);
        	            }
            	    }
            	    //sizeControl();
                    //textarea.invalidate();
                    select(selectionStart, selectionStart);
                    //textarea.invalidate();
    	        }
            }
	    }
	    startUndo = true;
	}

	/**
	 * typeChar
	 * @param c - TBD
	 */
	protected void typeChar( char c ) {
	    if ( startUndo ) {
	        saveUndo();
	    }
	    deleteSelection();
	    startUndo = false;
	    int code = (int) c;
	    if (currentLine != null) {
	        if (code == 10) {
	            insertLine();
	        }
	        else {
	            currentLine.typeChar( c );
        	    startUndo = false;
	            int lineNumber = getGadgetIndex(currentLine);
	            if ((textarea.getWordWrap()) && (wordWrapNeeded(currentLine))) {
                     invalidateLine();
	            }
        	    selectionStart++;
	        }
	    }
	    //sizeControl();
        //textarea.invalidate();
	    startUndo = false;
        select(selectionStart, selectionStart);
	    startUndo = false;
	}

	protected final void invalidateLine() {
	    invalidateLine(currentLine);
	}

	protected void invalidateLine(TextLine line) {
	    synchronized(getTreeLock()) {
	        if (invalidLine == null) {
    	        invalidatingLine = true;
        	    invalidLine = line;
                int index = getGadgetIndex(line);
                if (index > 0 && (!((TextLine)gadgets[index-1]).getLineBreak())) {
                    invalidLine = (TextLine)gadgets[index-1];
                }
        	    invalidate();
    	        invalidatingLine = false;
    	    } else if (invalidLine != line) {
    	        invalidate();
    	    }
    	}
	}

	public void invalidate(boolean invalidateParent) {
	    synchronized(getTreeLock()) {
    	    super.invalidate(invalidateParent);
    	    if (!invalidatingLine) {
    	        wholeInvalid = true;
    	    }
    	}
	}

	/**
	 * saveUndo
	 */
	protected void saveUndo() {
        undoText = getText();
        undoSelectionStart = selectionStart;
        undoSelectionEnd = selectionEnd;
        undoCaretPosition = selectionEnd;
        startUndo = false;
	}

	/**
	 * undo
	 */
	protected void undo() {
	    if ( undoText != null ) {
	        String tempText = getText();
            int tempSelectionStart = selectionStart;
            int tempSelectionEnd = selectionEnd;
            int tempCaretPosition = selectionEnd;

            int newSelectionStart = undoSelectionStart;
            int newSelectionEnd = undoSelectionEnd;
            int newCaretPosition = undoCaretPosition;
            setText(undoText);
            select(newSelectionStart,newSelectionEnd);
            //caretPosition = newCaretPosition;

            undoText = tempText;
            undoSelectionStart = tempSelectionStart;
            undoSelectionEnd = tempSelectionEnd;
            undoCaretPosition = tempCaretPosition;
            repaint();
        }
	}

    /**
	 * cut
	 */
	protected void cut() {
	    copy();
	    deleteSelection();
	    if (selectionStart <= selectionEnd) {
	        select(selectionStart, selectionStart);
	    } else {
	        select(selectionEnd, selectionEnd);
	    }
      repaint();
	}

	/**
	 * copy
	 */
	protected void copy() { //--1.1'd Tines
       StringSelection clipstring = new StringSelection(getSelectedText(true));
       Clipboard c = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
       c.setContents(clipstring, clipstring);
	}

	/**
	 * paste
	 */
	protected void paste() {  //--1.1'd Tines
	    if ( startUndo ) {
	        saveUndo();
	    }
	    deleteSelection();
       Clipboard c = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
       Transferable t = c.getContents(this);
       String tclip = null;
       try{
         tclip = (String)t.getTransferData(DataFlavor.stringFlavor);
         } catch(Exception ex) {
         return;
       }
       if(null != tclip)
       {
        insert(tclip, selectionStart);
        repaint();
       }
	}

	protected void rot13() {
       String clip = getSelectedText(true);
       if(null != clip)
       {
           StringBuffer tclip = new StringBuffer(clip);
           for(int i=0; i<tclip.length(); ++i)
           {
             char c = tclip.charAt(i);
             int delta = 0;
             if(('a'<=c && c<='m') || ('A'<=c && c<='M')) delta = 13;
             else if(('n'<=c && c<='z') || ('N'<=c && c<='Z')) delta = -13;
             tclip.setCharAt(i, (char)(c+delta));
          }
          deleteSelection();
          insert(tclip.toString(), selectionStart);
          repaint();
       }
	}

	protected void enquote(String quote)
    {
      int last = findLineNo(getSelectionEnd());
      int first = findLineNo(getSelectionStart());
      int i;
      int totalChars = 0;
      TextLine line = null;
      for (i = 0; i < last; i++)
      {
          line = (TextLine) gadgets[i];
          if(i >= first)
          {   // could handle word wrap better
              insert(quote, totalChars);
              line.setLineBreak(true);
          }
          totalChars += line.length();
          if (!line.getLineBreak())
          {
              --totalChars;
          }
      }
      repaint();
    }

  protected int findLineNo(int position)
  {
        int totalChars = 0;
        TextLine line = null;
        int i;
        for (i = 0; i < gadgetCount; i++)
        {
            line = (TextLine) gadgets[i];
            totalChars += line.length();
            if (!line.getLineBreak())
            {
                --totalChars;
            }
            if (totalChars > position)
            {
                return i;
            }
        }
        return i;
    }

	protected void wrapQ()
  {
    TextLine line = findLine(selectionStart);
    int indent = line.getSelectionStart();
    String quote = line.getText().substring(0, indent);
    int last = findLineNo(getSelectionEnd());
    int first = findLineNo(getSelectionStart());

    // extract the appropriate sub-strings
    StringBuffer buff = new StringBuffer("");
    int i;
    for(i = first; i < last; ++i)
    {
        line = (TextLine) gadgets[i];
        if(line.getText().length() < indent) continue;
        buff.append(line.getText().substring(indent));
    }
    line = (TextLine) gadgets[last];
    if(line.getSelectionEnd() >= indent)
    {
         buff.append(line.getText().substring(indent, line.getSelectionEnd()));
    }
    int k,n;
    for(k=0,n=0; k<buff.length(); ++k)
    {
       if(buff.charAt(k) == 0) ++n;
    }
    System.out.println("Original trawl "+n);

    // collapse whitespace

    StringBuffer buff2 = new StringBuffer(buff.length());
    boolean prevWhite = true;
    for(i=0; i<buff.length(); ++i)
    {
        char c = buff.charAt(i);
        boolean isWhite = Character.isWhitespace(c);
        if(isWhite)
        {
            if(!prevWhite) buff2.append(' ');
            prevWhite = true;
        }
        else
        {
            prevWhite = false;
            buff2.append(c);
        }
    }
    for(k=0,n=0; k<buff2.length(); ++k)
    {
       if(buff2.charAt(k) == 0) ++n;
    }
    System.out.println("collapsedl "+n);

    // construct wrapped text
    buff.setLength(0);
    java.text.BreakIterator l = java.text.BreakIterator.getLineInstance(); //TODO: locale
    l.setText(buff2.toString());
    int prev = 0;
    char[] aLine = new char[100];
    for (int brk = l.following(prev);
          brk != java.text.BreakIterator.DONE;
          brk = l.next())
     {
          if(brk-prev > 72-indent)
          {
            int section = l.previous();
            buff2.getChars(prev, section, aLine, 0);
            buff.append(aLine, 0, section-prev);
            buff.append("\n"+quote);
            prev = section;
          }
     }
     if(prev < buff2.length()-1)
     {
            int section = buff2.length();
            buff2.getChars(prev, section, aLine, 0);
            buff.append(aLine, 0, section-prev);
     }
    for(k=0,n=0; k<buff.length(); ++k)
    {
       if(buff.charAt(k) == 0) ++n;
    }
    System.out.println("wrapped "+n);

     // replace;
	    deleteSelection();
      insert(buff.toString(), selectionStart);
      repaint();
  }


	/**
	 * moveLeft
	 * @param ctrlDown - TBD
	 * @param shiftDown - TBD
	 */
	protected void moveLeft( boolean ctrlDown, boolean shiftDown ) {
        if (shiftDown) {
            if (selectionEnd > 0) {
                selectionEnd--;
                //TextLine line = findLine(selectionEnd);
                //if ((line.getSelectionStart() == 0) && (selectionEnd > 0)) {
                //    selectionEnd--;
                //}
            }
        } else {
            if (selectionStart > 0) {
                selectionStart--;
                selectionEnd = selectionStart;
            } else {
               selectionStart = selectionEnd;
            }
        }
        select(selectionStart, selectionEnd);
    }

	/**
	 * moveRight
	 * @param ctrlDown - TBD
	 * @param shiftDown - TBD
	 */
	protected void moveRight( boolean ctrlDown, boolean shiftDown ) {
        if ((shiftDown) && (selectionEnd < getLastPosition()-1)) {
            selectionEnd++;
            //TextLine line = findLine(selectionEnd);
            //if ((line.getSelectionEnd() == 0)) {
            //    selectionEnd++;
            //}
        } else if (selectionEnd < getLastPosition()-1) {
               selectionEnd++;
               selectionStart = selectionEnd;
        } else if (!shiftDown) {
               selectionStart = selectionEnd;
        }
        select(selectionStart, selectionEnd);
	}

	/**
	 * moveEnd
	 * @param ctrlDown - TBD
	 * @param shiftDown - TBD
	 */
	protected void moveEnd( boolean ctrlDown, boolean shiftDown ) {
	    TextLine currentLine = findLine(selectionEnd);
	    int cur = currentLine.getSelectionEnd();
	    int end = currentLine.getTextLength();
        if ((shiftDown) && (selectionEnd < getLastPosition())) {
            selectionEnd += (end-cur-1);
        } else if (selectionEnd < getLastPosition()) {
               selectionEnd += (end-cur-1);
               selectionStart = selectionEnd;
        }
        select(selectionStart, selectionEnd);
	}

	/**
	 * moveHome
	 * @param ctrlDown - TBD
	 * @param shiftDown - TBD
	 */
	protected void moveHome( boolean ctrlDown, boolean shiftDown ) {
	    TextLine currentLine = findLine(selectionStart);
	    int cur = currentLine.getSelectionStart();
        if ((shiftDown) && (selectionEnd < getLastPosition())) {
            selectionStart -= cur;
        } else if (selectionEnd < getLastPosition()) {
               selectionStart -= cur;
               selectionEnd = selectionStart;
        }
        select(selectionStart, selectionEnd);
	}

	/**
	 * moveDown
	 * @param ctrlDown - TBD
	 * @param shiftDown - TBD
	 */
	protected void moveDown( boolean ctrlDown, boolean shiftDown ) {
	    TextLine currentLine = findLine(selectionEnd);
	    if (currentLine != null) {
            int linePos = getGadgetIndex(currentLine);
            if (linePos < gadgetCount-1) {
                TextLine newLine = (TextLine)gadgets[linePos+1];
                int offsetPos = selectionEnd - findLineOffset(currentLine);
                if (offsetPos >= newLine.length()) {
                    selectionEnd = findLineOffset(newLine) + newLine.length()-1;
                } else {
                    selectionEnd = findLineOffset(newLine) + offsetPos;
                }
                if (!shiftDown) {
                    selectionStart = selectionEnd;
                }
                select(selectionStart, selectionEnd);
            }
        }
    }

	/**
	 * moveUp
	 * @param ctrlDown - TBD
	 * @param shiftDown - TBD
	 */
	protected void moveUp( boolean ctrlDown, boolean shiftDown ) {
	    TextLine currentLine = findLine(selectionEnd);
	    if (currentLine != null) {
            int linePos = getGadgetIndex(currentLine);
            if (linePos > 0) {
                TextLine newLine = (TextLine)gadgets[linePos-1];
                int offsetPos = selectionEnd - findLineOffset(currentLine);
                if (offsetPos >= newLine.length()) {
                    selectionEnd = findLineOffset(newLine) + newLine.length()-1;
                } else {
                    selectionEnd = findLineOffset(newLine) + offsetPos;
                }
                if (!shiftDown) {
                    selectionStart = selectionEnd;
                }
                select(selectionStart, selectionEnd);
            }
        }
	}

    /**
     * handleTab
     * @param shiftDown - TBD
     */
    protected void handleTab(boolean shiftDown) {
	    if (currentLine != null) {
	        currentLine.setShowCursor(false);
	    }
	    if (shiftDown) {
	        prevFocus();
	    }
	    else {
	        nextFocus();
	    }
	}

	  public synchronized void addTextListener(TextListener l) {
        textListener = GWTEventMulticaster.add(textListener, l);
	  }
	  public synchronized void removeTextListener(TextListener l) {
        textListener = GWTEventMulticaster.remove(textListener, l);
	  }
  	protected void processTextEvent(TextEvent e) {
	    if (textListener != null) {
    		if(e.getID() == TextEvent.TEXT_VALUE_CHANGED) {
    			textListener.textValueChanged(e);
    		}
    	}
	  }
    public void invokeChange(AWTEvent e) {
	    TextEvent action = new TextEvent( this, e.getEvent(),
	                               TextEvent.TEXT_VALUE_CHANGED );
	    processTextEvent( action );
		  if ( ! action.isConsumed() /*&& ( ! ignoreDefaultAction )*/ )
      {
	        invokeDefault(e);
	    }
	  }

    /**
     * processKeyEvent
     * @param e	the KeyEvent
     */
    protected void processKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_PRESSED) {
    	    boolean handled = true;

    		if (!textarea.isEditable()) {
    			if ( ! e.isActionKey() ) {
    				if ( e.isControlDown() || e.isMetaDown() ) {
    				    switch (e.getKeyCode()) {
    					case 3: {
    							copy();
    							break;
    			        }
        				default:
        					handled = false;
        	            }
        	        }
        	    }
        	}
    		else {
    			if ( ! e.isActionKey() ) {
    				if ( e.isControlDown() ||
    					 e.isMetaDown() ) {
    					switch ( e.getKeyCode() ) {

    						case 3: { // Ctrl-C
    							copy();
    							break;
    						}

    						case 22: { // Ctrl-V
    							paste();
                  invokeChange(e);
    							break;
    						}

    						case 24: { // Ctrl-X
    							cut();
                  invokeChange(e);
    							break;
    						}

    						case 26: { // Ctrl-Z
    							undo();
                  invokeChange(e);
    							break;
    						}

    						default: {
    						    handled = false;
    						}
    					}
    				}
    				else {
            			typeChar( e.getKeyChar() );
                  invokeChange(e);
            		}
        		}
        		else {
        			int nextPos = -1;

        			switch( e.getKeyCode() ) {
        				case KeyEvent.UP:{
        					moveUp( e.isControlDown(), e.isShiftDown() );
                            break;
                        }
        				case KeyEvent.LEFT: {
        					moveLeft( e.isControlDown(), e.isShiftDown() );
        					break;
        				}
        				case KeyEvent.RIGHT: {
        					moveRight( e.isControlDown(), e.isShiftDown() );
        					break;
        				}
        				case KeyEvent.DOWN: {
        					moveDown( e.isControlDown(), e.isShiftDown() );
        					break;
        				}
        				case KeyEvent.HOME: {
        					moveHome( e.isControlDown(), e.isShiftDown() );
        					break;
        				}
        				case KeyEvent.END: {
        					moveEnd( e.isControlDown(), e.isShiftDown() );
        					break;
        				}
        				case KeyEvent.BACK_SPACE: {
                			backspaceKey();
                      invokeChange(e);
        					break;
        				}
        				case KeyEvent.DELETE: {
                			deleteKey();
                      invokeChange(e);
        					break;
        				}
        				case KeyEvent.TAB: {
        				    handleTab(e.isShiftDown());
        					break;
        				}
        				case KeyEvent.ENTER: {
                   			typeChar( e.getKeyChar() );
                        invokeChange(e);
        					break;
        				}
        				default: {
        					handled = false;
        				}
        			}
        		}
    		}
    		if ( handled ) {
    		    e.consume();
        	}
    	}
	    super.processKeyEvent( e );
	}

	/**
	 * processFocusEvent
	 * @param e		the firing FocusEvent
	 */
	protected void processFocusEvent(FocusEvent e) {
        if (e.getID() == FocusEvent.FOCUS_GAINED) {
    	    hasFocus = true;
    	    select(selectionStart, selectionEnd);
    	    startThread();
        } else {
    	    hasFocus = false;
    	    focusable = false;
    	    repaint();
        }
	    super.processFocusEvent( e );
	}
    public synchronized void addMouseListener(MouseListener l) {
        mouseListener = GWTEventMulticaster.add(mouseListener, l);
    }
     public synchronized void removeMouseListener(MouseListener l) {
        mouseListener = GWTEventMulticaster.remove(mouseListener, l);
	  }

    /**
     * processMouseEvent
     * @param mouse the MouseEvent
     */
    public void processMouseEvent( MouseEvent e ) {
        if (e.getID() == MouseEvent.MOUSE_PRESSED) {
            if(mouseListener != null && (e.isMetaDown()))
            {
    	        mouseListener.mousePressed(e);
                e.consume();
                return;
            }
            e.consume();
            if ( ! ignoreMouse ) {
                focusable = true;
                if (!hasFocus()) {
                    requestFocus();
                }
    	        int mousex = e.getX();
    	        int mousey = e.getY();
    	        Gadget at = getGadgetAt(mousex,mousey);
                if ( at instanceof TextLine ) {
    			    TextLine line = (TextLine)at;
                    int lineOffset = findLineOffset(line);
                    int textOffset =  line.findTextPos( mousex-line.x );
    			    int endPos = lineOffset + textOffset;
       			    if (( textOffset >= line.length())) {
    			        endPos--;
    			    }
        			lastClickPos = endPos;
    				if ( e.isShiftDown() ) {
    				    select( selectionStart, endPos );
    				} else {
            			long when = e.getWhen();
        			    if ( ( when - lastClickWhen ) < 300 ) {
        			        int pos = line.findTextPos( e.getX()-line.x );
        				    String str = line.getText();
            				if ( pos < 0 ) {
            					pos = 0;
            				}
            				else if ( pos == str.length() ) {
            					pos--;
            				}
            				while ( ( pos >= 0 ) &&
            						( str.charAt( pos ) == ' ' ) ) {
            					pos--;
            				}
            				while ( ( pos >= 0 ) &&
            						( str.charAt( pos ) != ' ' ) ) {
            					pos--;
            				}
            				int start = ++pos;
            				while ( ( pos < str.length() ) &&
            					 ( str.charAt( pos ) != ' ' ) ) {
            					pos++;
            				}
            				//while ( ( pos < str.length() ) &&
            				//	    ( str.charAt( pos ) == ' ' ) ) {
            				// pos++;
            				//}

            				select( lineOffset+start, lineOffset+pos );
            			}
            			else {
           				    select( endPos, endPos );
            			}
                    }
        		}
            	else {
         	        int len = getText().length();
         	        select(len, len); // could use getLastPosition()
         	    }
            }
        } else if (e.getID() == MouseEvent.MOUSE_RELEASED) {
    	    dragDirectionX = NONE;
    	    dragDirectionY = NONE;
    	    if ( ! ignoreMouse ) {
    	        int mousex = e.getX();
    	        int mousey = e.getY();
    	        Gadget at = getGadgetAt(mousex,mousey);
                if ( at instanceof TextLine ) {
               		lastClickWhen = e.getWhen();
         	    }
            }
            e.consume();
        }
	    super.processMouseEvent( e );
	}

	/**
	 * scrollVisible
	 */
	protected void scrollVisible() {
	    Point scroll = textarea.getScrollPosition();
        TextLine currentLine = findLine(selectionEnd);
        if (currentLine != null) {
            Rectangle viewable = getParent().getBounds();
    	    if (viewable.width > 0 && viewable.height > 0) {
                Rectangle rect = currentLine.getBounds();
                int yS = calculateYScroll(rect.y, rect.height,
                                          viewable.height, scroll.y);
                int xS = calculateXScroll(currentLine, viewable.width, scroll.x);
                textarea.setScrollPosition(xS, yS);
            }
        }
    }

    private int calculateYScroll(int location, int size, int offset, int scroll) {
        int bottomPosition = location + size;
        int topPosition = location - size;
        if (bottomPosition > (offset+scroll)) {
            return  scroll+(bottomPosition-(offset+scroll))+1;
        } else if (topPosition < scroll) {
            return location;
        }
        return scroll;
    }

    private int calculateXScroll(TextLine line, int offset, int scroll) {
        if (line.getSelectionEnd() == 0) {
            return 0;
        }

        if (selectionEnd >= selectionStart) {
            int currentX = line.findCaretPosition(line.getSelectionEnd()+1);
            if (currentX > (offset+scroll)) {
                return  scroll+(currentX-(offset+scroll));
            }
        }
        if (selectionEnd <= selectionStart) {
            int currentX = line.findCaretPosition(line.getSelectionStart());
            if (currentX < scroll) {
                return currentX;
            }
        }
        return scroll;
    }

    /**
     * processMouseMotionEvent
     * @param mouse the MouseEvent
     */
    public void processMouseMotionEvent( MouseEvent e ) {
        if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
    	    if ( ! ignoreMouse ) {
    	        int mousex = e.getX();
    	        int mousey = e.getY();
    	        Rectangle viewable = getParent().getBounds();
    	        Point scroll = textarea.getScrollPosition();
    	        if (mousey > viewable.height + scroll.y) {
                    dragDirectionY = DOWN;
                } else if (mousey < scroll.y) {
                    dragDirectionY = UP;
    	        } else {
                    dragDirectionY = NONE;
    	        }

    	        if (mousex > viewable.width + scroll.x) {
                    dragDirectionX = RIGHT;
    	        } else if (mousex < scroll.x) {
                    dragDirectionX = LEFT;
    	        } else {
                    dragDirectionX = NONE;
        	    }

        	    if ((dragDirectionX == NONE) && (dragDirectionX == NONE)) {
        	        Gadget at = getGadgetAt(mousex,mousey);
                    if (at == this) {
        	            at = gadgets[gadgetCount-1];
              		}
        	        if ( at instanceof TextLine ) {
        	            TextLine line = (TextLine)at;
    	                int lineOffset = findLineOffset(line);
    	                int textOffset =  line.findTextPos( mousex-line.x );
        			    int endPos = lineOffset + textOffset;
        			    if (( textOffset >= line.length())) {
        			        endPos--;
        			    }
            			if ( endPos >= 0 && endPos != lastClickPos) {
           				    select( selectionStart, endPos );
            			}
            			lastClickPos = endPos;
            		}
                }
                startThread();
        	}
        }

	    super.processMouseMotionEvent( e );
	}
    public void print(java.awt.PrintJob pj)
    {
        // the print job is created and ended externally
        if(pj == null) return;

        int screenResolution = java.awt.Toolkit.getDefaultToolkit().getScreenResolution();
        int topMargin = screenResolution;  //1" top and bottom
        int leftMargin = screenResolution/2;  // 0.5" each side
        Font font = new Font("Monospaced", Font.PLAIN, 12);
        FontMetrics fm = java.awt.Toolkit.getDefaultToolkit().getFontMetrics(font);

        int startY = fm.getMaxAscent();  // top to base of first line
        int height = startY + fm.getMaxDescent();  // interline spacing

        java.awt.Dimension pagesize = pj.getPageDimension();
        pagesize.height -= 2*topMargin;
        pagesize.width -= 2*leftMargin;

        int linesPerPage = pagesize.height/height;
        java.awt.Graphics page = null;

        for (int i = 0; i < gadgetCount; i++)
        {
            if(null == page)
            {
                page = pj.getGraphics();
                page.setColor(java.awt.Color.black);
                page.translate(leftMargin, topMargin);
                page.setClip(0,0,pagesize.width,pagesize.height);
                page.setFont(font);
            }
            String line = ((TextLine) gadgets[i]).getText();
            int lineNo = i%linesPerPage;
            page.drawString(line, 0, startY+lineNo*height);
            if(lineNo == linesPerPage-1)
            {
                page.dispose();   // End the page--send it to the printer.
                page = null;
            }
        }
        if(null != page) page.dispose();  // End the page--send it to the printer.
    }
}

