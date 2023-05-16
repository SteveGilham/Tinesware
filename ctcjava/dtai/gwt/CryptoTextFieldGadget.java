
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
 **  $Id: TextFieldGadget.java,v 1.70 1998/03/10 20:30:42 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/TextFieldGadget.java,v $
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
import java.awt.Event;
import java.awt.Point;
import java.awt.Insets;
import java.util.Vector;
import java11.awt.event.ActionEvent;
import java11.awt.event.ActionListener;
import java11.awt.event.AWTEvent;
import java11.awt.event.FocusEvent;
import java11.awt.event.InputEvent;
import java11.awt.event.KeyEvent;
import java11.awt.event.MouseEvent;
import java11.awt.event.TextEvent;
import java11.awt.event.TextListener;

import com.ravnaandtines.ctcjava.CryptoString;
import com.ravnaandtines.ctcjava.CryptoTextBuffer;


/**
 * TextFieldGadget
 * @version 1.1
 * @author DTAI, Incorporated
 * @version CryptoTextFieldGadget 1.0
 * @author Mr. Tines
 */
public class CryptoTextFieldGadget extends BorderGadget implements TextGadget, Runnable {

    public static final int HORIZ_ALIGN_LEFT = TextLine.HORIZ_ALIGN_LEFT;
    public static final int HORIZ_ALIGN_CENTER = TextLine.HORIZ_ALIGN_CENTER;
    public static final int HORIZ_ALIGN_RIGHT = TextLine.HORIZ_ALIGN_RIGHT;

    public static final int VERT_ALIGN_TOP = TextLine.VERT_ALIGN_TOP;
    public static final int VERT_ALIGN_MIDDLE = TextLine.VERT_ALIGN_MIDDLE;
    public static final int VERT_ALIGN_BOTTOM = TextLine.VERT_ALIGN_BOTTOM;

    ActionListener actionListener;
    TextListener textListener;

    private boolean allowedToShrink = true;

    private long lastClickWhen = 0;
    private int lastClickPos = -1;
    private boolean ignoreMouse = false;
    private boolean ignoreDefaultAction = false;

    private CryptoTextLine line;

    private Thread thread;
    private boolean focusable = false;

    Color overrideBackground;

    public void wipe() // what it's all about!
    {
      if(null == line) return;
      line.wipe();
    }

    protected void finalize() throws Throwable
   {
      //System.out.println("CryptoTextFieldGagdet finalizing");
      wipe();
      super.finalize();
      //System.out.println("CryptoTextFieldGagdet finalized");
    }


   /**
     * Constructs a Text with no text.
     */

	public CryptoTextFieldGadget() {
		this( "", 0 );
	}

    /**
     * Constructs a Text with the specified text.
     * @param text - the text of the button
     */

	public CryptoTextFieldGadget(String text) {
	    this( text, 0 );
	}

    /**
     * Constructs a Text with the specified number of columns.
     * @param columns - the text of the button
     */

	public CryptoTextFieldGadget(int columns) {
	    this( "", columns );
	}

    /**
     * Constructs a Text with the specified text and number of columns.
     * @param text - the text of the button
     * @param columns - the number of columns
     */

	public CryptoTextFieldGadget(String text, int columns) {
	    this(text,columns,new CryptoTextLine());
	}

	/**
	 * TextFieldGadget
	 * @param text - TBD
	 * @param columns - TBD
	 * @param line - TBD
	 */
	protected CryptoTextFieldGadget(String text, int columns, CryptoTextLine line) {
	    this.line = line;
	    add("Center",line);
	    setColumns( columns );
	    setText( text );
	    setBorderType( BorderGadget.THREED_IN );
	    setBorderThickness( 2 );
	    setMargins( 1 );
      setEchoChar('*');
	}

	/**
	 * getLine
	 * @return TextLine
	 */
	protected CryptoTextLine getLine() {
	    return line;
	}

	/**
	 * blackAndWhite
	 * @param g - TBD
	 * @return boolean
	 */
	protected boolean blackAndWhite(GadgetGraphics g) {
	    return ( overrideBackground != null ||
	             ( ( getBackground(g) != Gadget.transparent ) &&
	               ( normalBackground == null ) ) );
	}

	/**
	 * clear
	 * @param g - TBD
	 */
	public void clear(GadgetGraphics g) {
        if (getBorderType() == ROUND_RECT) {
            super.clear(g);
        }
        else {
    	    if ( blackAndWhite(g) ) {
                if ( overrideBackground == null ) {
                    if (isFunctioning() &&
                        (isEditable() || parent instanceof ChoiceGadget)) {
                	    g.setColor( Color.white );
            	    } else {
                	    g.setColor(getNormalBackground(g));
            	    }
            	} else {
            	    g.setColor( overrideBackground );
                }
        	    g.fillRect(0,0,width,height);
                if (hasFocus() && !isEditable() && parent instanceof ChoiceGadget) {
                    g.setColor(line.getSelectBg(g,getForeground(g)));
            	    g.fillRect(1,1,width-2,height-2);
                }
        	}
        }
    }

    /**
     * update
     * @param g - TBD
     */
    public void update(GadgetGraphics g) {
        synchronized(getTreeLock()) {
            if (normalBackground != null &&
                normalBackground != Gadget.transparent &&
                (getBorderType() == THREED_IN ||
                 getBorderType() == THREED_OUT)) {
                overrideBackground = normalBackground;
                normalBackground = null;
                super.update(g);
                normalBackground = overrideBackground;
                overrideBackground = null;
            }
            else {
                super.update(g);
            }
        }
    }

	/**
	 * paint
	 * @param g - TBD
	 */
	public void paint( GadgetGraphics g ) {
	    if ( normalForeground == null ) {
    	    if ( blackAndWhite(g) ) {
    	        if ( overrideBackground == null ) {
    	            line.overrideForeground = Color.black;
    	        }
    	        else {
    	            line.overrideForeground = calcForeground(overrideBackground);
    	        }
    	    }
    	    else {
    	        line.overrideForeground = null;
        	}
        }
  	    super.paint(g);
	}

	/**
	 * sets the horizontal alignment
	 * @param horizAlign - TBD
	 */
	public void setHorizAlign(int horizAlign) {
	    line.setHorizAlign(horizAlign);
	}

	/**
	 * gets the horizontal alignment
	 * @return int - TBD
	 */
	public int getHorizAlign() {
	    return line.getHorizAlign();
	}

	/**
	 * sets the vertical alignment
	 * @param vertAlign - TBD
	 */
	public void setVertAlign(int vertAlign) {
	    line.setVertAlign(vertAlign);
	}

	/**
	 * gets the vertical alignment
	 * @return int
	 */
	public int getVertAlign() {
	    return line.getVertAlign();
	}

    /**
     * Gets the text of the button.
     * @see setText
     * @return String
     */
	public CryptoString getCryptoText() {
		return new CryptoString(line.getText());
	}
  
	public String getText() {
    if(true) throw new RuntimeException("CryptoTextFieldGadget does not return Strings");
		return "";
	}

	/**
	 * toString
	 * @return String
	 */
	public String toString() {
	    return line.toString();
	}

/**
 * Sets the button with the specified text.
 * @param text - the text to set the button with
 * @see getText
 */
	public void setText( String text ) {
	    line.setText(text);
	}

    /**
     * Inserts the specified text at the specified position.
     * @param str the text to insert.
     * @param pos the position at which to insert.
     * @see dtai.gwt.TextGadget#setText
     * @see #replaceText
     */
    public void insertText(String str, int pos) {
        line.insertText(str,pos);
    }

    /**
     * Inserts the specified character at the specified position.
     * @param c the character to insert.
     * @param pos the position at which to insert.
     * @see dtai.gwt.TextGadget#setText
     * @see #replaceText
     */
    public void insertText(char c, int pos) {
        line.insertText(c,pos);
    }

    /**
     * Appends the given text to the end.
     * @param str the text to insert
     * @see #insertText
     */
    public void appendText(String str) {
        line.appendText(str);
    }

    /**
     * Appends the given character to the end.
     * @param c the character to insert
     * @see #insertText
     */
    public void appendText(char c) {
        line.appendText(c);
    }

    /**
     * Replaces text from the indicated start to end position with the
     * new text specified.
     * @param str the text to use as the replacement.
     * @param start the start position.
     * @param end the end position.
     * @see #insertText
     * @see #replaceText
     */
    public void replaceText(String str, int start, int end) {
        line.replaceText(str,start,end);
    }

    /**
     * Replaces text from the indicated start to end position with the
     * new character specified.
     * @param c the character to use as the replacement.
     * @param start the start position.
     * @param end the end position.
     * @see #insertText
     * @see #replaceText
     */
    public void replaceText(char c, int start, int end) {
        line.replaceText(c,start,end);
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
	 * getColumns
	 * @return int
	 */
	public int getColumns() {
		return line.getColumns();
	}

	/**
	 * setColumns
	 * @param columns - TBD
	 */
	public void setColumns( int columns ) {
		line.setColumns(columns);
	}

	/**
	 * run
	 */
	public void run() {
        Thread thisThread = Thread.currentThread();

	    while ( thread == thisThread ) {
    	    try {
    	        thread.sleep( 500 );
    	    }
    	    catch ( InterruptedException ie ) {
    	    }
    	    if ( thread == thisThread ) {
        	    synchronized(getTreeLock()) {
            	    ignoreMouse = false;
                    line.toggleCursor();
                }
            }
    	}
	}


    private void resetCursor() {
		synchronized(getTreeLock()) {
    	    if (( thread != null) && (hasFocus())) {
    	        line.setShowCursor( line.getSelectionStart() == line.getSelectionEnd() );
    	        thread = new Thread(this, "dtai.gwt.TextFieldGadget.resetCursor");
    	        thread.start();
    	    }
    	}
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
	public synchronized void addTextListener(TextListener l) {
        textListener = GWTEventMulticaster.add(textListener, l);
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
	public synchronized void removeTextListener(TextListener l) {
        textListener = GWTEventMulticaster.remove(textListener, l);
	}

	/**
	 * processEvent
	 *
	 * @param e		a ActionEvent
	 * @return boolean result
	 */
	protected void processEvent(AWTEvent e) {
		if (e instanceof ActionEvent) {
		    processActionEvent((ActionEvent)e);
		} else if (e instanceof TextEvent) {
		    processTextEvent((TextEvent)e);
		} else {
		    super.processEvent(e);
		}
	}

	protected void processActionEvent(ActionEvent e) {
	    if (actionListener != null) {
    		if(e.getID() == ActionEvent.ACTION_PERFORMED) {
    			actionListener.actionPerformed(e);
    		}
    	}
	}
	protected void processTextEvent(TextEvent e) {
	    if (textListener != null) {
    		if(e.getID() == TextEvent.TEXT_VALUE_CHANGED) {
    			textListener.textValueChanged(e);
    		}
    	}
	}

	/**
	 * isFocusTraversable
	 * @return boolean
	 */
	public boolean isFocusTraversable() {
	    if (isEditable() || parent instanceof ChoiceGadget) {
    	    return true;
    	}
    	return focusable;
	}

/**
 * Returns the offset used to calculate the next focus gadget, usually
 * the value of getOffset(), but might be an ancestors offset in some
 * cases (like combo box).
 * @see getOffset
 * @return Point
 */
    public Point getFocusOffset() {
	    if (parent instanceof ComboBoxGadget) {
            return parent.getOffset();
        } else {
            return super.getFocusOffset();
        }
    }

	/**
	 * invokeAction
	 * @param e - TBD
	 */
	public void invokeAction(AWTEvent e) {
	    ActionEvent action = new ActionEvent( this, e.getEvent(),
	                               ActionEvent.ACTION_PERFORMED );
	    processEvent( action );
		if ( ! action.isConsumed() &&
	         ( ! ignoreDefaultAction ) ) {
	        invokeDefault(e);
	    }
	}

  public void invokeChange(AWTEvent e) {
	    TextEvent action = new TextEvent( this, e.getEvent(),
	                               TextEvent.TEXT_VALUE_CHANGED );
	    processEvent( action );
		  if ( ! action.isConsumed() && ( ! ignoreDefaultAction ) )
      {
	        invokeDefault(e);
	    }
	}

    /**
     * processKeyEvent
     * @param e	the KeyEvent
     */
    protected void processKeyEvent(KeyEvent e)
    {
        if (e.getID() == KeyEvent.KEY_PRESSED)
        {
            if (dtai.util.Debug.getLevel() == 80)
            {
                System.err.println("e.isActionKey()="+e.isActionKey()+
                " e.getKeyCode()="+e.getKeyCode()+" e.getKeyChar()="
                +((int)e.getKeyChar()));
            }
    	      boolean handled = true;
    		    if (!isEditable())
            {
    			     if ( ! e.isActionKey() )
               {
    				      if ( e.isControlDown() || e.isMetaDown() )
                  {
    				         switch (e.getKeyCode())
                     {
    					          case 3:
                        {
    						          line.copy();
    						          break;
    					          }
        				        case KeyEvent.ENTER:
                        {
        				           invokeAction(e);
        					         break;
        				        }
        				        default:
        					         handled = false;
                     } //switch
                  } // control or meta
               } // not action
        	     else
               {
        	        handled = false;
        	     }
            } // !editable
    		    else
            {
    			      if ( !e.isActionKey() )
                {
    				        if ( e.isControlDown() || e.isMetaDown() )
                    {
    					          switch ( e.getKeyCode() )
                        {
    						           case 3:
                           { // Ctrl-C
    							           line.copy();
                             break;
                           }
    						           case 22:
                           { // Ctrl-V
    							           line.paste();
                             invokeChange(e);
    							           break;
    						           }
    						           case 24:
                           { // Ctrl-X
    							           line.cut();
                             invokeChange(e);
    							           break;
    						           }
    						           case 26:
                           { // Ctrl-Z
    							           line.undo();
                             invokeChange(e);
    							           break;
    						           }
    						           default:
    						             handled = false;
                        } //switch
                    } // control or meta
    				        else if ( ! e.isMetaDown() )
                    {
            			     line.typeChar( e.getKeyChar() );
                       invokeChange(e);
                    }
                } // !action
        		    else
                {
        			     int nextPos = -1;
        			     switch( e.getKeyCode() )
                   {
        				      case KeyEvent.UP:
                      {
        					       line.moveLeft( e.isControlDown(), e.isShiftDown() );
        					       handled = false;
        					       break;
        				      }
        				      case KeyEvent.LEFT:
                      {
        					       line.moveLeft( e.isControlDown(), e.isShiftDown() );
        					       break;
        				      }
        				      case KeyEvent.DOWN:
                      {
                         line.moveLeft( e.isControlDown(), e.isShiftDown() );
        					       handled = false;
        					       break;
        				      }
        				      case KeyEvent.RIGHT:
                      {
        					       line.moveRight( e.isControlDown(), e.isShiftDown() );
        					       break;
        				      }
                      case KeyEvent.HOME:
                      {
        					       line.moveTo( 0, e.isShiftDown() );
        					       break;
        				      }
        				      case KeyEvent.END:
                      {
                         line.moveTo( line.getTextLength(), e.isShiftDown() );
        					       break;
        				      }
        				      case KeyEvent.BACK_SPACE:
                      {
                			   line.backspaceKey();
                         invokeChange(e);
        					       break;
        				      }
        				      case KeyEvent.DELETE:
                      {
                			   line.deleteKey();
                         invokeChange(e);
        					       break;
        				      }
        				      case KeyEvent.ENTER:
                      {
        				         invokeAction(e);
        				         handled = false;
        					       break;
        				      }
        				      default:
        					      handled = false;
                   }// switch
                }// action
            } // editable
    		    if ( handled )
            {
        		    e.consume();
        	  }
        } // is key pressed
	      super.processKeyEvent( e );
    }

	/**
	 * processFocusEvent
	 * @param e		the firing FocusEvent
	 */
	protected void processFocusEvent(FocusEvent e) {
        if (e.getID() == FocusEvent.FOCUS_GAINED) {
    		synchronized(getTreeLock()) {
                if ((thread == null ) && isEditable()) {
        	        thread = new Thread(this, "TextFieldGadget.processFocueEvent");
        	        ignoreMouse = true; // until first iteration of thread
        	        //selectAll();
            	    line.setShowCursor( true );
                	thread.start();
        	    }
        	    repaint();
        	}
        } else {
    		synchronized(getTreeLock()) {
                focusable = false;
        	    if ( thread != null ) {
        	        thread = null;
        	    }
                if ( line.getShowCursor() ) {
                    line.setShowCursor( false );
                }
                else {
                    repaint();
                }
            }
        }
	    super.processFocusEvent( e );
	}


    /**
     * processMouseEvent
     * @param mouse the MouseEvent
     */
    public void processMouseEvent( MouseEvent e ) {
        if (e.getID() == MouseEvent.MOUSE_PRESSED) {
            if ( ! ignoreMouse ) {
                e.consume();
                focusable = true;
                boolean hadFocus = hasFocus();
                if (!hadFocus) {
                    requestFocus();
                }
    			line.setMouseDown( true );
    			if (isEditable() && (!hadFocus)) {
    			    selectAll();
    			} else {
        			int pos = line.findTextPos( e.getX()-line.x );
        			lastClickPos = pos;
        			if ( pos >= 0 ) {
        				if ( e.isShiftDown() ) {
        				    line.selectFromCursor(pos);
        				}
        				else {
                			long when = e.getWhen();
        	    			if ( ( when - lastClickWhen ) < 300 ) { // doubleclick
                				CryptoString str = getCryptoText();
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
                				while ( ( pos < str.length() ) &&
                					    ( str.charAt( pos ) == ' ' ) ) {
                				 pos++;
                				}
                				select( start, pos );
                			}
                			else {
                                select( pos );
                            }
                		}
        			}
        		}
            }
        } else if (e.getID() == MouseEvent.MOUSE_RELEASED) {
    	    if ( ! ignoreMouse ) {
    	        e.consume();
    			line.setMouseDown( false );
    			lastClickWhen = e.getWhen();
        	}
        }
	    super.processMouseEvent( e );
	}


    /**
     * processMouseMotionEvent
     * @param mouse the MouseEvent
     */
    public void processMouseMotionEvent( MouseEvent e ) {
        if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
    	    if ( ! ignoreMouse ) {
    			int pos = line.findTextPos( e.getX()-line.x );
    			if ( pos >= 0 && pos != lastClickPos) {
    			    line.selectFromCursor(pos);
    			}
    			lastClickPos = pos;
        	}
        }
	    super.processMouseMotionEvent( e );
	}

    /**
     * Returns the selected text contained in this TextComponent.
     * @see setText
     * @return String
     */
    public String getSelectedText() {
        return line.getSelectedText();
    }

    /**
     * Returns the boolean indicating whether this TextComponent is allowedToShrink or not.
     * @see setEditable
     * @return boolean
     */
    public boolean isAllowedToShrink() {
        return allowedToShrink;
    }

    /**
     * Sets the specified boolean to indicate whether or not this TextComponent should be allowedToShrink.
     * @param b - the boolean to be set
     * @see isEditable
     */
    public void setAllowedToShrink(boolean b) {
        allowedToShrink = b;
    }

    /**
     * Returns the boolean indicating whether this TextComponent is editable or not.
     * @see setEditable
     * @return boolean
     */
    public final boolean isEditable() {
        return line.isEditable();
    }

    /**
     * Sets the specified boolean to indicate whether or not this TextComponent should be editable.
     * @param editable - the boolean to be set
     * @see isEditable
     */
    public final void setEditable(boolean editable) {
        line.setEditable(editable);
        if (!editable && parent instanceof ChoiceGadget) {
            setFocusThickness(1);
        }
        else {
            setFocusThickness(0);
        }
        repaint();
    }

    /**
     * Returns the cursor position.
     * @return int
     */
    public int getCursorPos() {
        return getCaretPosition();
    }

    /**
     * Returns the cursor position.
     * @return int
     */
    public int getCaretPosition() {
        return line.getCaretPosition();
    }

    /**
     * Returns the cursor position.
     * @param caretPosition - TBD
     */
    public void setCaretPosition(int caretPosition) {
        line.setCaretPosition(caretPosition);
    }

    /**
     * Returns the selected text's start position.
     * @param int - TBD
     * @return int
     */

    public int getSelectionStart() {
        return line.getSelectionStart();
    }

    /**
     * Sets the selection start to the specified position. The new starting point is constrained to be before or at the current selection end.
     * @param selectionStart - the start position of the text
     */
    public void setSelectionStart(int selectionStart) {
        line.setSelectionStart(selectionStart);
    }

    /**
     * Returns the selected text's end position.
     * @return int
     */
    public int getSelectionEnd() {
        return line.getSelectionEnd();
    }

    /**
     * Sets the selection end to the specified position. The new end point is constrained to be at or after the current selection start.
     * @param selectionEnd - the start position of the text
     */
    public void setSelectionEnd(int selectionEnd) {
        line.setSelectionEnd(selectionEnd);
    }

    /**
     * Selects the text found between the specified start and end locations.
     * @param selectionStart - the start position of the text
     * @param selectionEnd - the end position of the text
     */
    public void select( int selectionStart, int selectionEnd ) {
        line.select(selectionStart,selectionEnd);
    }

    /**
     * select
     * @param pos - TBD
     */
    public void select( int pos ) {
        line.select(pos);
    }

    /**
     * Selects all the text in the TextComponent.
     */
    public void selectAll() {
        line.selectAll();
    }

    /**
     * Returns the character to be used for echoing.
     * @see setEchoChar
     * @see echoCharIsSet
     * @return char
     */
    public char getEchoChar() {
        return line.getEchoChar();
    }

    /**
     * Sets the echo character for this TextField. This is
	 * useful for fields where the user input shouldn't
	 * be echoed to the screen, as in the case of a TextField that
     * represents a password.
     * @param c - the echo character for this TextField
     * @see echoCharIsSet
     * @see getEchoChar
     */
    public void setEchoChar(char c) {
        line.setEchoChar(c);
    }

    /**
     * Returns true if this TextField has a character set for echoing.
     * @see setEchoChar
     * @see getEchoChar
     * @return boolean
     */
    public boolean echoCharIsSet() {
        return line.echoCharIsSet();
    }

    /**
     * Returns the preferred size Dimensions needed for this TextField with the specified amount of columns.
     * @param columns - the number of columns in this TextField
     * @return Dimension
     */
    public Dimension getPreferredSize(int columns) {

        Dimension pref = line.getForegroundSize( null, columns );
        Insets i = getInsets();
        pref.width+=i.left + i.right;
        pref.height+=i.top + i.bottom;
        return pref;
    }

    /**
     * Returns the preferred size Dimensions needed for this TextField.
     * Overrides:
     *      getPreferredSize in class Component
     * @return Dimension
     */
    public Dimension getPreferredSize() {
        line.resetForegroundSize( null );
        return getPreferredSize( line.getColumns() );
    }

    /**
     * Returns the minimum size Dimensions needed for this TextField with the specified amount of columns.
     * @param columns - the number of columns in this TextField
     * @return Dimension
     */
    public Dimension getMinimumSize(int columns) {
        if ( allowedToShrink ) {
            return getPreferredSize( 1 );
        }
        else {
            return getPreferredSize( columns );
        }
    }

    /**
     * Returns the minimum size Dimensions needed for this TextField.
     * @return Dimensions
     */
    public Dimension getMinimumSize() {
        line.resetForegroundSize( null );
        return getMinimumSize( line.getColumns() );
    }
}
