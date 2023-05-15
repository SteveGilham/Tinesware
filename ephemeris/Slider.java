

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
public class Slider extends Canvas implements MouseListener, MouseMotionListener{

  private int value;
  private int visible;
  private int minimum;
  private int maximum;

  public Slider(int value, int visible, int minimum, int maximum)
  {
    this.value = value;
    this.visible = visible;
    this.minimum = minimum;
    this.maximum = maximum;

    addMouseListener(this);
    addMouseMotionListener(this);
  }

  public void setValues(int value, int visible, int minimum, int maximum)
  {
    this.value = value;
    this.visible = visible;
    this.minimum = minimum;
    this.maximum = maximum;
  }

  public int getValue()
  {
    return value;
  }

  public void setValue(int value)
  {
    if(value > this.maximum) value = this.maximum;
    if(value < this.minimum) value = this.minimum;
    this.value = value;
    repaint();
  }

  public Dimension minimumSize()
  {
    return getMinimumSize();
  }

  public Dimension getMinimumSize()
  {
    return new Dimension(30+visible, 15);
  }

  public void paint(Graphics  g)
  {
    Dimension d = getSize();

    Color b = getBackground();
    Color bright = b.brighter().brighter();
    Color dark = b.darker().darker();

    int tlx = 10;
    int tly = 5;
    int brx = d.width-10;
    int bry = 10;

    g.setColor(dark);
    g.drawLine(tlx, tly, brx, tly);
    g.drawLine(tlx, tly+1, brx-1, tly+1);
    g.drawLine(tlx, tly, tlx, bry);
    g.drawLine(tlx+1, tly+1, tlx+1, bry-1);

    g.setColor(bright);
    g.drawLine(tlx, bry, brx, bry);
    g.drawLine(tlx+1, bry-1, brx, bry-1);
    g.drawLine(brx, tly, brx, bry);
    g.drawLine(brx-1, tly+1, brx-1, bry-1);

    double perPixel = (maximum-minimum);
    perPixel /= (d.width-20);
    double offset = (value-minimum)/perPixel;

    int ioff = (int)(offset-visible/perPixel/2);
    g.setColor(b);

    tlx = 10+ioff;
    tly = 2;
    brx = tlx+(int)(visible/perPixel);
    bry = 13;
    g.fillRect(tlx, tly, brx-tlx, bry);

    g.setColor(bright);
    g.drawLine(tlx, tly, brx, tly);
    g.drawLine(tlx, tly+1, brx-1, tly+1);
    g.drawLine(tlx, tly, tlx, bry);
    g.drawLine(tlx+1, tly+1, tlx+1, bry-1);

    g.setColor(dark);
    g.drawLine(tlx, bry, brx, bry);
    g.drawLine(tlx+1, bry-1, brx, bry-1);
    g.drawLine(brx, tly, brx, bry);
    g.drawLine(brx-1, tly+1, brx-1, bry-1);

  }

  java.util.Vector v = new java.util.Vector(2);
  public void addSlideListener(SlideListener l)
  {
    v.addElement(l);
  }

  private void fireEvent()
  {
    int n = v.size();
    for(int i=0; i<n; ++i)
    {
      SlideListener l = (SlideListener)v.elementAt(i);
      l.slideEvent(this);
    }
  }

  public void mouseClicked(MouseEvent e) {
    double perPixel = (maximum-minimum);
    perPixel /= (getSize().width-20);
    double offset = (value-minimum)/perPixel;

    int ioff = (int)(offset-visible/perPixel/2);
    int tlx = 10+ioff;
    int brx = tlx+(int)(visible/perPixel);

    if(e.getX() < tlx)
      setValue(value-visible);
    else if(e.getX() > brx)
      setValue(value+visible);

    fireEvent();
  }

  boolean dragging = false;

  public void mousePressed(MouseEvent e) {
    //TODO: implement this java.awt.event.MouseListener method;
    dragging = false;

    double perPixel = (maximum-minimum);
    perPixel /= (getSize().width-20);
    double offset = (value-minimum)/perPixel;

    int ioff = (int)(offset-visible/perPixel/2);
    int tlx = 10+ioff;
    int brx = tlx+(int)(visible/perPixel);

    if(e.getX() < tlx) return;
    if(e.getX() > brx) return;

    if(e.getY() < 2) return;
    if(e.getY() > 13) return;

    dragging = true;

  }

  public void mouseReleased(MouseEvent e) {
    //TODO: implement this java.awt.event.MouseListener method;
    mouseDragged(e);
    if(dragging)
      fireEvent();
    dragging = false;
  }

  public void mouseEntered(MouseEvent e) {
    //TODO: implement this java.awt.event.MouseListener method;
  }

  public void mouseExited(MouseEvent e) {
    //TODO: implement this java.awt.event.MouseListener method;
  }

  public void mouseDragged(MouseEvent e) {
    //TODO: implement this java.awt.event.MouseMotionListener method;
    if(!dragging) return;

    int offset = e.getX()-10;

    double perPixel = (maximum-minimum);
    perPixel /= (getSize().width-20);

    int newValue = (int)(offset*perPixel) + minimum;

    setValue(newValue);
  }

  public void mouseMoved(MouseEvent e) {
    //TODO: implement this java.awt.event.MouseMotionListener method;
  }
}