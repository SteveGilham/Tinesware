package com.ravnaandtines.util.swing;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.*;

/**
*  Class JExternalFrame - A JInternalFrame on a Window, that tries to combine the 
*  functionality and pluggable L & F of  a JInternalFrame with the top-level status of 
 * a Window or Frame.  You use it just like a JFrame (except I wouldn't recommend doing 
 * any fancy stuff with setContentPane, setGlassPane, or setLayeredPane.)  Call
 * getContentPane() and set the layout, add components, etc.  Then, throw it
 * out on your screen somewhere with a pack() and a setVisible(true).  
 * <p>
 * You can also programmatically minimize it & iconize it just like a 
 * JInternalFrame using setIcon() & setMaximum().  Unfortunately, since it's
 * really just a JWindow in disguise, it won't actually go down to the task
 * bar in Windows 95/98/NT like a JFrame will.  
 * <p>
 * Because the window is drawn using a JInternalFrame, it will use whatever
 * your current Look & Feel is for a JInternalFrame.  This means it can look
 * like a Metal Frame.  Pretty cool, huh?
 * <p>
 * Written by Daniel P Grieves, who is the first-person voice throughout these comments
 * <pre>
 * Do not remove these comments under penalty of my displeasure.
 * <p>
 * I'm not smart like those GNU guys, so I'll just say briefly that you can
 * pretty much do what you want with this software, except:
 * 1.  Sue me if it doesn't work or causes you any problems.
 * 2.  Claim that you (or anyone other than me) wrote it.
 * 3.  Sell it to anyone.
 * <p>
 * See <a href="http://www.enteract.com/~grieves/">my website</a> for more info on JExternalFrame.
 * <p>
 * JExternalFrame  *
 * </pre>
 * @author Daniel P Grieves
 * @version f
 * @see javax.swing.JInternalFrame
 */

public class JExternalFrame extends JWindow implements WindowConstants {

  //******************************** Inner Classes ***************************
  protected class MiniDesktopManager extends DefaultDesktopManager 
    implements SwingConstants {
    public void setBoundsForFrame(JComponent f, 
				  int newX,
				  int newY,
				  int newWidth,
				  int newHeight) {
      Dimension d = getSize();
      boolean didResize = ((d.width != newWidth) ||
			   (d.height != newHeight));
      Point p = getLocationOnScreen();
      setBounds(p.x + newX, p.y + newY, newWidth, newHeight);
      if (didResize) {
	validate();
      } //Ends if
    } //Ends method setBoundsForFrame
    public void dragFrame(JComponent f, int newX, int newY) {
      setBoundsForFrame(f, newX, newY, f.getWidth(), f.getHeight());
    } //Ends method dragFrame
    public void endDraggingFrame(JComponent f) {
    } //Ends method endDraggingFrame
    public void iconifyFrame(JInternalFrame f) {
      setIconified(true);
      super.iconifyFrame(f);
      f.getDesktopIcon().setLocation(0, 0);
      Dimension size = f.getDesktopIcon().getSize();
      setSize(size);
    } //Ends method iconifyFrame
    public void deiconifyFrame(JInternalFrame f) {
      setIconified(false);
      super.deiconifyFrame(f);
      Rectangle r = f.getBounds();
      setBoundsForFrame(f, r.x, r.y, r.width, r.height);
    } //Ends method deiconifyFrame
    private Point LowerRight, UpperLeft;
    private int direction = -1;
    public void beginResizingFrame(JComponent f, int direction) {
      this.direction = direction;
      UpperLeft = getLocationOnScreen();
      Dimension size = f.getSize();
      LowerRight = new Point(UpperLeft.x + size.width, 
			     UpperLeft.y + size.height);
    } //Ends method beginResizingFrame
    public void resizeFrame(JComponent f, int newX, int newY, 
			    int newWidth, int newHeight) {
      Point location = getLocation();
      if ((direction == NORTH) || (direction == NORTH_EAST) ||
	  (direction == NORTH_WEST)) {
	newHeight = LowerRight.y - location.y;
      } //Ends if
      if ((direction == WEST) || (direction == NORTH_WEST) ||
	  (direction == SOUTH_WEST)) {
	newWidth = LowerRight.x - location.x;
      } //Ends if
      super.resizeFrame(f, newX, newY, newWidth, newHeight);
    } //Ends method resizeFrame
    public void endResizingFrame(JComponent f) {
    } //Ends method endResizingFrame
    public void closeFrame(JInternalFrame f) {
      super.closeFrame(f);
    } //Ends method closeFrame
    public void maximizeFrame(JInternalFrame f) {
      if (!f.isIcon()) {
	setPreviousBounds(f, getBounds());
      } else {
	try { f.setIcon(false); } catch (PropertyVetoException e) {}
      } //Ends if..else
      setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
      validate();
      try { f.setSelected(true); } catch (PropertyVetoException e) {}
      removeIconFor(f);
    } //Ends method maximizeFrame
  } //Ends class MiniDesktopManager

  protected class FocusKeeper implements FocusListener, ContainerListener {
    public void focusGained(FocusEvent evt) {
      try { setSelected(true); } catch (PropertyVetoException e) {}
    } //Ends method focusGained
    public void focusLost(FocusEvent evt) {
      try { setSelected(false); } catch (PropertyVetoException e) {}
    } //Ends method setSelected
    private void recursiveAdd(Component c) {
      c.addFocusListener(this);
      if (c instanceof Container) {
	((Container)c).addContainerListener(this);
	Component[] children = ((Container)c).getComponents();
	for (int i = 0; i < children.length; i++) recursiveAdd(children[i]);
      } //Ends if
    } //Ends method recursiveAdd
    public void componentAdded(ContainerEvent evt) {
      recursiveAdd(evt.getChild());
    } //Ends method componentAdded
    private void recursiveRemove(Component c) {
      c.removeFocusListener(this);
      if (c instanceof Container) {
	((Container) c).removeContainerListener(this);
	Component[] children = ((Container)c).getComponents();
	for (int i = 0; i < children.length;i++) recursiveRemove(children[i]);
      } //Ends if
    } //Ends method recursiveRemove
    public void componentRemoved(ContainerEvent evt) {
      recursiveRemove(evt.getChild());
    } //Ends method componentRemoved
  } //Ends class FocusKeeper
  //******************************** Ends Inner Classes **********************


  //******************************** Constants *******************************
  /** Used for maximizing the frame*/
  public static final int SCREEN_WIDTH =
    Toolkit.getDefaultToolkit().getScreenSize().width;
  /** Used for maximizing the frame*/
  public static final int SCREEN_HEIGHT =
    Toolkit.getDefaultToolkit().getScreenSize().height;
  //******************************** Ends Constants **************************


  //******************************** Attributes ******************************
  private JInternalFrame realFrame;
  private JDesktopPane framePane;
  private boolean iconified = false;
  //******************************** Ends Attributes *************************

  //******************************** Constructors ****************************
  /** This method works more like the zero-arg constructor for JFrame than
   *  JInternalFrame because I replace JFrames with it.  No title, resizable,
   *  closable, maximizable, iconifiable
   */
  public JExternalFrame() {
    this("");
  } //Ends constructor JExternalFrame

  /** Make a JExternalFrame with the given title; resizable, closable,
   *  maximizable, iconifiable
   *  @param title Put this title on top of the frame
   */
  public JExternalFrame(String title) {
    this(title, true);
  } //Ends constructor JExternalFrame

  /** Like above, but can control resizability
   *  @param title Put this title on top of the frame
   *  @param resizable Can the user resize the window?
   */
  public JExternalFrame(String title, boolean resizable) {
    this(title, resizable, true);
  } //Ends constructor JExternalFrame

  /** Like above, but can control closability
   *  @param title Put this title on top of the frame
   *  @param resizable Can the user resize the window?
   *  @param closable Can the user close the window?
   */
  public JExternalFrame(String title, boolean resizable, boolean closable) {
    this(title, resizable, closable, true);
  } //Ends constructor JExternalFrame

  /** Like above, but can control maximizability
   *  @param title Put this title on top of the frame
   *  @param resizable Can the user resize the window?
   *  @param closable Can the user close the window?
   *  @param maximizable Can the user maximize the window?
   */
  public JExternalFrame(String title, boolean resizable, boolean closable,
			boolean maximizable) {
    this(title, resizable, closable, maximizable, true);
  } //Ends constructor JExternalFrame

  /** Like above, but can control iconifiability
   *  @param title Put this title on top of the frame
   *  @param resizable Can the user resize the window?
   *  @param closable Can the user close the window?
   *  @param maximizable Can the user maximize the window?
   *  @param iconifiable Can the user iconify the window?
   */
  public JExternalFrame(String title,
			boolean resizable,
			boolean closable,
			boolean maximizable,
			boolean iconifiable) {
    super();
    realFrame = new JInternalFrame(title, resizable, closable, maximizable,
				   iconifiable);
    realFrame.addInternalFrameListener(new InternalFrameListener() {
      public void internalFrameClosing(InternalFrameEvent evt) {
	forwardWindowEvent(WindowEvent.WINDOW_CLOSING);
      } //Ends method internalFrameClosing
      public void internalFrameActivated(InternalFrameEvent evt) {
	forwardWindowEvent(WindowEvent.WINDOW_ACTIVATED);
      } //Ends method internalFrameActivated
      public void internalFrameClosed(InternalFrameEvent evt) {
	forwardWindowEvent(WindowEvent.WINDOW_CLOSED);
	dispose();
      } //Ends method internalFrameClosed
      public void internalFrameDeactivated(InternalFrameEvent evt) {
	forwardWindowEvent(WindowEvent.WINDOW_DEACTIVATED);
      } //Ends method internalFrameDeactivated
      public void internalFrameDeiconified(InternalFrameEvent evt) {
	forwardWindowEvent(WindowEvent.WINDOW_DEICONIFIED);
      } //Ends method internalFrameDeiconified
      public void internalFrameIconified(InternalFrameEvent evt) {
	forwardWindowEvent(WindowEvent.WINDOW_ICONIFIED);
      } //Ends method internalFrameIconified
      public void internalFrameOpened(InternalFrameEvent evt) {
	forwardWindowEvent(WindowEvent.WINDOW_OPENED);
      } //Ends method internalFrameOpened
    }); //Ends addInternalFrameListener
    FocusKeeper fk = new FocusKeeper();
    Container c = super.getContentPane();
    c.addFocusListener(fk);
    c.addContainerListener(fk);
    c.setLayout(new GridLayout(1, 1, 0, 0)); //KISS
    framePane = new JDesktopPane();
    framePane.setDesktopManager(new MiniDesktopManager());
    c.add(framePane);
    framePane.add(realFrame);
  } //Ends constructor JExternalFrame
  //******************************** Ends Constructors ***********************


  //******************************** Methods *********************************
  /**Translate an InternalFrameEvent into a WindowEvent and pass it on*/
  void forwardWindowEvent(int WindowEventID) {
    WindowEvent NewEvent = new WindowEvent(this, WindowEventID);
    processWindowEvent(NewEvent);
  } //Ends method forwardWindowEvent
    
  /**Let the window know whether to setBounds on the internal frame or not*/
  void setIconified(boolean iconified) {
    this.iconified = iconified;
  } //Ends method setIconified

  /** Use the container returned by getContentPane() to put your stuff in.
   *  @return the Container where all your stuff should go.
   */
  public Container getContentPane() {
    return realFrame.getContentPane();
  } //Ends method getContentPane

  /** What should the frame do when the user clicks the close button?  This
   *  should be one of the constants from WindowConstants.
   *  @return What the frame should do when the user tries to close it.
   *  @see javax.swing.WindowConstants
   */
  public int getDefaultCloseOperation() {
    return realFrame.getDefaultCloseOperation();
  } //Ends method getDefaultCloseOperation

  /** Tell the frame what action it should take when the user tries to close 
   *  it.  This should be one of the constants from WindowConstants
   *  @param dco The new Default Close Operation
   *  @see javax.swing.WindowConstants
   */
  public void setDefaultCloseOperation(int dco) {
    realFrame.setDefaultCloseOperation(dco);
  } //Ends method setDefaultCloseOperation

  /** The JMenuBar this frame is currently showing.*/
  public JMenuBar getJMenuBar() {
    return realFrame.getJMenuBar();
  } //Ends method getJMenuBar

  /** The title of the frame*/
  public String getTitle() {
    return realFrame.getTitle();
  } //Ends method getTitle

  /** Is the frame closable?*/
  public boolean isClosable() {
    return realFrame.isClosable();
  } //Ends method isClosable

  /** Is the frame currently closed?*/
  public boolean isClosed() {
    return realFrame.isClosed();
  } //Ends method isClosed

  /** Is the frame iconified?*/
  public boolean isIcon() {
    return realFrame.isIcon();
  } //Ends method isIcon

  /** Can the frame be iconified?*/
  public boolean isIconifiable() {
    return realFrame.isIconifiable();
  } //Ends method isIconifiable

  /** Can the frame be maximized?*/
  public boolean isMaximizable() {
    return realFrame.isMaximizable();
  } //Ends method isMaximizable

  /** Is the frame maximized? */
  public boolean isMaximum() {
    return realFrame.isMaximum();
  } //Ends method isMaximum

  /** Can the frame be resized?*/
  public boolean isResizable() {
    return realFrame.isResizable();
  } //Ends method isResizable

  /** Is the frame currently selected?*/
  public boolean isSelected() {
    return realFrame.isSelected();
  } //Ends method isSelected

  /** Set whether the frame can be closed.*/
  public void setClosable(boolean Closable) {
    realFrame.setClosable(Closable);
  } //Ends method setClosable

  /** Close the window.  I'm not sure this is a good idea.  You should
   *  probably use setVisible instead.
   *  @exception java.beans.PropertyVetoException If it wants to.
   */
  public void setClosed(boolean Closed) throws PropertyVetoException {
    realFrame.setClosed(Closed);
  } //Ends method setClosed

  /** Iconify / Deiconify the frame.
   *  @exception java.beans.PropertyVetoException If it wants to.
   */
  public void setIcon(boolean Icon) throws PropertyVetoException {
    realFrame.setIcon(Icon);
  } //Ends method setIcon

  /** Maximize / Demaximize the frame.
   *  @exception java.beans.PropertyVetoException If it wants to.
   */
  public void setMaximum(boolean Maximum) throws PropertyVetoException {
    realFrame.setMaximum(Maximum);
  } //Ends method setMaximum

  /** Set whether the frame can be maximized.*/
  public void setMaximizable(boolean Maximizable) {
    realFrame.setMaximizable(Maximizable);
  } //Ends method setMaximizable

  /** Set whether the frame can be resized.*/
  public void setResizable(boolean Resizable) {
    realFrame.setResizable(Resizable);
  } //Ends method setResizable

  /** Set whether the frame is selected.
   *  @exception java.beans.PropertyVetoException If it wants to.
   */
  public void setSelected(boolean Selected) throws PropertyVetoException {
    realFrame.setSelected(Selected);
  } //Ends method setSelected

  /** Give the frame a menu bar to use.*/
  public void setJMenuBar(JMenuBar m) {
    realFrame.setJMenuBar(m);
  } //Ends method setJMenuBar

  /** Change the title of the frame*/
  public void setTitle(String title) {
    realFrame.setTitle(title);
  } //Ends method setTitle

  /** What icon is the frame currently displaying?*/
  public Icon getFrameIcon() {
    return realFrame.getFrameIcon();
  } //Ends method getFrameIcon

  /** Set the icon the frame should display*/
  public void setFrameIcon(Icon icon) {
    realFrame.setFrameIcon(icon);
  } //Ends method setFrameIcon

  /** Do that packing thing.*/
  public void pack() {
    realFrame.pack();
    super.pack();
  } //Ends method pack

  /** Set the size and location of the frame*/
  public void setBounds(int x, int y, int width, int height) {
    super.setBounds(x, y, width, height);
    if (!iconified)
      realFrame.setBounds(0, 0, width, height);
  } //Ends method setBounds

  /** Show / hide the frame*/
  public void setVisible(boolean visible) {
    try {
      super.setVisible(visible);
      realFrame.setVisible(visible);
      realFrame.setSelected(visible);
    } catch (java.beans.PropertyVetoException e) {
    } //Ends try..catch
  } //Ends method setVisible
      
      
  /** A simple test of the JExternalFrame*/
  public static void main(String[] argv) {
    //Simple test of a JExternalFrame
    JExternalFrame jef = new JExternalFrame("Simple Test 1", true, true,
					    true, true);
    jef.getContentPane().add("Center", new JButton("Press Me"));
    jef.getContentPane().add("North", new JTextField(10));
    jef.addWindowListener(new WindowAdapter() {
      public void windowClosed(WindowEvent evt) {
	System.exit(0);
      } //Ends method windowClosed
    }); //Ends addWindowListener
    jef.setBounds(0, 0, 200, 100);
    jef.setVisible(true);
  } //Ends method main
    
  //******************************** Ends Methods ****************************

} //Ends class JExternalFrame
