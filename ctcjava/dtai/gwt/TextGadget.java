/****************************************************************
 **
 **  $Id: TextGadget.java,v 1.7 1997/08/06 23:27:13 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/TextGadget.java,v $
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
 * TextGadget
 * @version 1.1
 * @author DTAI, Incorporated
 */
public interface TextGadget {

    /**
     * Sets the text of this TextComponent to the specified text.
     *
     * @param t - the new text to be set
     * @see getText
     */
    public abstract void setText(String t);

    /**
     * Returns the text contained in this TextComponent.
     *
     * @see setText
     * @return String
     */
    public abstract String getText();

    /**
     * Returns the selected text contained in this TextComponent.
     *
     * @see setText
     * @return String
     */
    public abstract String getSelectedText();

    /**
     * Returns the boolean indicating whether this TextComponent is editable or not.
     *
     * @see setEditable
     * @return boolean
     */
    public abstract boolean isEditable();

    /**
     * Sets the specified boolean to indicate whether or not this TextComponent should be editable.
     *
     * @param b - the boolean to be set
     * @see isEditable
     */
    public abstract void setEditable(boolean b);

    /**
     * Returns the selected text's start position.
     * @return int
     */
    public abstract int getSelectionStart();

    /**
     * Sets the selection start to the specified position.
	 * The new starting point is constrained to be before or at the current selection end.
     *
     * @param selectionStart - the start position of the text
     */
    public abstract void setSelectionStart(int selectionStart);

    /**
     * Returns the selected text's end position.
     * @return int
     */
    public abstract int getSelectionEnd();

    /**
     * Sets the selection end to the specified position.
	 * The new end point is constrained to be at or after the current selection start.
     *
     * @param selectionEnd - the start position of the text
     */
    public abstract void setSelectionEnd(int selectionEnd);

    /**
     * Selects the text found between the specified start and end locations.
     *
     * @param selectionStart - the start position of the text
     * @param selectionEnd - the end position of the text
     */
    public abstract void select(int selectionStart,
                                  int selectionEnd);

    /**
     * Selects all the text in the TextComponent.
     */
    public abstract void selectAll();

    /**
     * Sets the position of the text insertion caret for the TextComponent
     * Throws: IllegalArgumentException
     *      If position is less than 0.
     *
     * @param position - the position
     */
    public abstract void setCaretPosition(int position);

    /**
     * Returns the position of the text insertion caret for the text component.
     *
     * @return the position of the text insertion caret for the text component.
     */
    public abstract int getCaretPosition();

    /**
     * Adds the specified text event listener to recieve text events from this textcomponent.
     *
     * @param l - the text event listener
     */
    //public abstract void addTextListener(TextListener l);

    /**
     * Removes the specified text event listener so that it
	 * no longer receives text events from this textcomponent
     * @param l
     */
    //public abstract void removeTextListener(TextListener l);

    /**
     * Processes text events occurring on this text component
	 * by dispatching them to any registered TextListener objects.
	 * NOTE: This method will not be called unless text events are
     * enabled for this component; this happens when one of the
	 * following occurs: a) A TextListener object is registered
	 * via addTextListener() b) Text events are enabled via
     * enableEvents()
     *
     * @param  e - the text event
     * @see enableEvents
     */

    //protected void processTextEvent(TextEvent e);
}
