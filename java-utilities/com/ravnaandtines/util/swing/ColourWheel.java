package com.ravnaandtines.util.swing;

import javax.swing.colorchooser.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import com.ravnaandtines.util.gui.Colour;
import java.util.*;

/**
*  Class ColourWheel - This is a panel that can be used within the
*  javax.swing.ColorChooser
* <p>
* Copyright Mr. Tines &lt;tines@windsong.demon.co.uk&gt; 1998
* <p>
* For the purposes of the licence, this source file and the minimum set of
* other source files in package com.ravnaandtines.util and its sub-
* packages required to link together to satisfy all class
* references not belonging to standard Javasoft-published APIs constitute
* the library.  Thus it is not necessary to distribute source to classes
* that you do not actually use.
* <p>
* Note that Java's dynamic class loading means that the distribution of class
* files (as is, or in jar or zip form) which use this library is sufficient to
* allow run-time binding to any interface compatible version of this library.
* The GLPL is thus far less onerous for Java than for the usual run of 'C'/C++
* library.
* <p>
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Library General Public
* License as published by the Free Software Foundation; either
* version 2 of the License, or (at your option) any later version.
* <p>
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Library General Public License for more details.
* <p>
* You should have received a copy of the GNU Library General Public
* License along with this library; if not, write to the Free
* Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*<p>
* @author Mr. Tines
* @version 1.0 10-Mar-1999
*/


public class ColourWheel extends AbstractColorChooserPanel
    implements ChangeListener, ActionListener, MouseMotionListener, MouseListener
{
    private class Wheel extends Component
    {
        public Dimension getPreferredSize() {return new Dimension(150,150);}
        public Dimension getMinimumSize() {return getPreferredSize();}
        public Dimension getMaximumSize() {return getPreferredSize();}

        private final int WIDTH = 150;
        private final int HEIGHT = 150;
        int[] rawImage;

        Polygon tri;
        Rectangle bounds;

        Polygon[] arm = new Polygon[6];
        Rectangle[] arms = new Rectangle[6];
        int[] armImages = null;

        Polygon[] wedge = new Polygon[6];
        Rectangle[] wbox = new Rectangle[6];

        double hue, oldHue = -5000;
        double lightMark, oldLight = -5000;
        double satMark, oldSat = -5000;
        Point oldPt;

        Point[] triangle_pts = new Point[3 * 6];
        java.util.Random rnd = new java.util.Random(0x123456789ABCDEF0L);

        Image triIm = null;
        Image[] armIm = new Image[6];

        Point hex_centre;
        double innerRadius, outerRadius;

        public Wheel(Colour c)
        {
            super();

            hue = c.h_hls();

            int[] x = new int[13];
            int[] y = new int[13];
            outerRadius = (WIDTH-5)/2.0;
            hex_centre = new Point(WIDTH/2, HEIGHT/2);
            innerRadius = outerRadius - 15;

            // corners of hexagon
            float sizeLength = find_triangle_pts(triangle_pts,
                      outerRadius, innerRadius);
            int i,j;

            // sides of hexagon
            for(i=0; i<6; ++i)
            {
                int ip = (i+1)%6;
                x[4] = x[0] = triangle_pts[3*i].x;
                y[4] = y[0] = triangle_pts[3*i].y;
                x[1] = triangle_pts[3*ip].x;
                y[1] = triangle_pts[3*ip].y;
                x[2] = triangle_pts[1+ip*3].x;
                y[2] = triangle_pts[1+ip*3].y;
                x[3] = triangle_pts[2+i*3].x;
                y[3] = triangle_pts[2+i*3].y;
                arm[i] = new Polygon(x,y,5);
                arms[i] = arm[i].getBounds();
            }

            // inner triangle
            x[0] = triangle_pts[0].x;
            y[0] = triangle_pts[0].y;

            x[1] = triangle_pts[6].x;
            y[1] = triangle_pts[6].y;

            x[2] = triangle_pts[12].x;
            y[2] = triangle_pts[12].y;

            x[3] = x[0]; y[3] = y[0];
            tri = new Polygon(x, y, 4);

            bounds = tri.getBounds();
            rawImage = new int[bounds.width*bounds.height];
        }

        public void paint(Graphics g)
        {
            int i, j;

            boolean recolour = (Math.abs(hue - oldHue) > 1) || triIm==null;
            Rectangle clip = g.getClipBounds();
            if(recolour || clip.intersects(bounds) )
            {
                for(j=0; recolour && j<bounds.width; ++j)
                {
                    for(i=0; i<bounds.height; ++i)
                    {
                        // i increases down
                        // j increases to the right

                        rawImage[j+bounds.width*i] =
                            (tri.contains(j+bounds.x, i+bounds.y)) ?
                        getHue(j, i).getRGB() : 0;
                    }
                }
                if(recolour)
                {
                    MemoryImageSource mem = new MemoryImageSource(
                        bounds.width, bounds.height,
                        rawImage,0, bounds.width);

                    if(triIm != null) triIm.flush();
                    g.drawImage(triIm = createImage(mem), bounds.x, bounds.y, null);
                    oldHue = hue;
                }
                else
                {
                    g.drawImage(triIm, bounds.x, bounds.y, null);
                }
                draw_tri_marker(g, lightMark, satMark, false);
                oldHue = hue;
            }
            else
            {
                boolean ls =
                 (Math.abs(lightMark - oldLight) > 1) ||
                 (Math.abs(satMark - oldSat) > 1);

                draw_tri_marker(g, lightMark, satMark, ls);
            }
            oldLight = lightMark;
            oldSat = satMark;

            drawTriangles(g);
            drawRectangles(g);

            draw_hue_marker(g, (float)(hue/60.0), recolour);

        }

        private void setColour(Colour col)
        {
            lightMark = col.l_hls();
            satMark = col.s_hls();
            if(satMark > 0) hue = col.h_hls();
            repaint();
        }

        private Color getHue(int j, int i)
        {
            // saturation increases vertically
            // lightness increases horizontally

            float side = bounds.width;
            double ydist = bounds.height;

            float yratio = (float)(i / (ydist-1));
            int halfwidth = (int) (yratio * side + 0.5f) / 2;
            int dx = (int)(j - side/2);

            int sat = 0;
            if (i < ydist - 1)
            {
                if (Math.abs(dx) == halfwidth)
                {
                    sat = 240;      // full saturation at the edge
                }
                else
                {
                    double height = (halfwidth == 0) ?
                        ydist :
                        ydist * (1.0f - Math.abs((double)dx/(double)(side/2.0f)));

                    sat = (height < 0.01) ?
                        0 :
                        (int)((ydist - i) / height * 240.0f + 0.5f);

                    if (sat > 240) sat = 240;
                }
            }

            // project x value to the triangle bottom, where lightness
            // is 0% at the left, 100% at the right
            int light = (int)((side/2.0f + dx) / side * 240.0f + 0.5f);
            if (i == ydist - 1)
            {
                if (dx == -halfwidth) light = 0;
                if (dx ==  halfwidth) light = 240;
            }

            if (light > 240) light = 240;
            return Colour.hls(hue, light/240.0, sat/240.0).color();
        }

        /*
        * Find coordinates of the 6 triangles in the corners of the hexagon shape
        * (rectangles are drawn along the sides, joined to these triangles)
        * The first coordinate of each triangle is its innermost point
        */
        private float find_triangle_pts(Point[] triangle_pts,
            double outer_rad,
            double inner_rad)
        {
            // assume equilateral triangles between the outer and inner radii
            float sideLength = (float)(outer_rad - inner_rad);

            // calculate corners with centre at (0,0), starting at top of screen
            Point[] pt = new Point[3];
            pt[0] = new Point(0, - (int)inner_rad);
            pt[1] = new Point(- (int)((sideLength)/2.0f), - (int)outer_rad);
            pt[2] = new Point(+ (int)((sideLength)/2.0f), - (int)outer_rad);

            int[] x = new int[4];
            int[] y = new int[4];

            int tri_index = 0;
            for (int hue = 0; hue < 6; hue++)
            {
                // rotate initial points by seg*60 degrees
                double cos = Math.cos(hue*Math.PI/3.0);
                double sin = Math.sin(hue*Math.PI/3.0);

                // apply transform to rotate points, and copy to a CPoint array
                for (int i = 0; i < 3; i++)
                {
                    Point p = new Point((int)(0.5+cos*pt[i].x-sin*pt[i].y),
                                    (int)(0.5+sin*pt[i].x+cos*pt[i].y));
                    x[i] = hex_centre.x + p.x;
                    y[i] = hex_centre.y + p.y;
                    triangle_pts[ tri_index++ ] = new Point(x[i], y[i]);
                }
                if(wedge[hue] == null)
                {
                    x[3] = x[0];
                    y[3] = y[0];
                    wedge[hue] = new Polygon(x,y,4);
                    wbox[hue] = wedge[hue].getBounds();
                }
            }
            return sideLength;
        }

        /*
        * Draw the 6 triangles in the corners of the hexagon colour wheel
        */
        private void drawTriangles(Graphics g)
        {
            for (int hue = 0; hue < 6; hue++)
            {
                if(g.getClipBounds().intersects(wbox[hue]))
                {
                    Colour c = Colour.hsv(hue*60, 1, 1);
                    g.setColor(c.color());
                    g.fillPolygon(wedge[hue]);
                }
            }
        }

        /*
        * draw rectangular strips of hexagon between triangles into the bitmap
        *
        *                     5  0
        *                   4      1
        *                     3  2
        */
        private void drawRectangles(Graphics g)
        {
            for (int hue = 0; hue < 6; hue++)
            {
                if(g.getClipBounds().intersects(arms[hue]))
                {
                    imageRectangle(g, hue);
                }
           } // next hue
        }// drawRectangles

        private void imageRectangle(Graphics g, int hue)
        {
            int i,j;

            if(armIm[hue]==null)
            {
                armImages = new int[arms[hue].width*arms[hue].height];

                for(j=0; j<arms[hue].width; ++j)
                {
                    for(i=0; i<arms[hue].height; ++i)
                    {
                        // i increases down
                        // j increases to the right

                        armImages[j+arms[hue].width*i] =
                            (arm[hue].contains(j+arms[hue].x, i+arms[hue].y)) ?
                            getArmHue(j+arms[hue].x, i+arms[hue].y, hue).getRGB() : 0;
                    }
                }

                MemoryImageSource mem = new MemoryImageSource(
                    arms[hue].width, arms[hue].height,
                    armImages,
                    0, arms[hue].width);
                armIm[hue] = createImage(mem);
                armImages = null;
            }
            g.drawImage(armIm[hue], arms[hue].x, arms[hue].y, null);
        }// imageRectangle

        private Color getArmHue(int j, int i, int hue)
        {
            int dx = j - triangle_pts[hue*3].x;
            int dy = i -  triangle_pts[hue*3].y;
            int hp = ((hue+1)%6);
            int dx1 = triangle_pts[hp*3].x - triangle_pts[hue*3].x;
            int dy1 = triangle_pts[hp*3].y -  triangle_pts[hue*3].y;

            double dot = (dx*dx1+dy*dy1)/((double)(dx1*dx1+dy1*dy1));

            double h=(hue+dot)*60;
            Colour col=Colour.hsv(h,1,1);
            return col.color();
        }

        /*
        * Draw/undraw a circular marker on the hue wheel at a given point
        */
        void draw_hue_marker(Graphics g, Point point)
        {
            g.setColor(Color.black);
            int d = (int) ((outerRadius - innerRadius) * 0.5f);
            Rectangle lastHueRect =
                new Rectangle(new Point(point.x - d, point.y - d));
            lastHueRect.add(new Point(point.x + d, point.y + d));

            g.drawArc(lastHueRect.x, lastHueRect.y, lastHueRect.width,
                lastHueRect.height, 0, 360);
        }

        /*
        * Draw/undraw a circular marker on the hue wheel given the (HLS) hue
        */
        void draw_hue_marker(Graphics g, float hue, boolean fancy)
        {
            int side = (int) Math.floor(hue);
            int hp = (side+1)%6;
            double frac = hue-side;
            if(side < 0) side += 6;

            int dx = triangle_pts[hp*3].x - triangle_pts[side*3].x;
            int dy = triangle_pts[hp*3].y - triangle_pts[side*3].y;

            int dx1 = triangle_pts[side*3 + 2].x - triangle_pts[side*3].x;
            int dy1 = triangle_pts[side*3 + 2].y - triangle_pts[side*3].y;

            Point pt = new Point(
                triangle_pts[side*3].x + (int)(0.5 + frac*dx) + dx1/2,
                triangle_pts[side*3].y + (int)(0.5 + frac*dy) + dy1/2);

            if(fancy)
            {
                imageRectangle(g, side);
                if(frac > 0.8)
                {
                    drawTriangles(g);
                    imageRectangle(g, hp);
                }
                else if (frac < 0.2)
                {
                    drawTriangles(g);
                    int hm = (side+5)%6;
                    imageRectangle(g, hp);
                }
            }
            draw_hue_marker(g, pt);
        }

        /*
        * Draw/undraw a cross marker on the triangle given a point
        */
        void draw_tri_marker(Graphics g, Point point)
        {
            int d = (int) ((outerRadius - innerRadius) * 0.5f);

            double h = hue-180;
            if(h < 0) h=hue+180;
            Colour opposite = Colour.hsv(h,1,1);

            g.setColor(opposite.color());

            g.drawLine(point.x-d, point.y, point.x+d, point.y);
            g.drawLine(point.x, point.y-d, point.x, point.y+d);
        }
        /*
        * Draw/undraw a cross on the triangle given the lightness and saturation
        */
        void draw_tri_marker(Graphics g, double light, double sat, boolean fancy)
        {
            float side = bounds.width;
            int j = (int)(light*side + 0.5);
            int dx = (int)(j - side/2);

            double ydist = bounds.height;
            double height = ydist * (1.0f - Math.abs((double)dx/(double)(side/2.0f)));

            sat*=height;
            int i = (int)(ydist - sat + 0.5);


            Point pt = new Point(bounds.x+j, bounds.y+i);
            if(fancy && triIm != null)
            {
                oldPt = null;
                g.drawImage(triIm, bounds.x, bounds.y, null);
            }
            oldPt = pt;
            draw_tri_marker(g, oldPt);
        }


        private boolean dragInTriangle = false;
        public void setDragStart(Point pt)
        {
            dragInTriangle = tri.contains(pt);
        }

        public Colour setColour(Point pt)
        {
            Colour c = null;
            if(dragInTriangle)
            {
                Color c2 = getHue(pt.x-bounds.x, pt.y-bounds.y);
                c = Colour.build(c2);
            }
            else
            {
                int dx = pt.x - hex_centre.x;
                int dy = pt.y - hex_centre.y;
                double ang = 180.0* Math.atan2(dx, -dy) / Math.PI;
                if(ang < 0) ang += 360;
                c = Colour.hls((ang+0.5), lightMark, satMark);
            }
            setColour(c);
            return c;
        }
    }

    private class Swatch extends Component
    {
        public Dimension getPreferredSize() {return new Dimension(50,50);}
        public Dimension getMinimumSize() {return getPreferredSize();}
        public Dimension getMaximumSize() {return new Dimension(250,250);}

        public void paint(Graphics g)
        {
            Dimension sz = getSize();
            g.setColor(ColourWheel.this.getColorSelectionModel().getSelectedColor());
            g.fillRect(5,5, sz.width-10, sz.height-10);
        }
    }

    private class NumberField extends JTextField
    {
        public NumberField(int n) {super(n);}
        protected void processKeyEvent(KeyEvent e)
        {
            if(e.getModifiers()==0)
            {
                int keyChar = e.getKeyChar();
                if('\n'==keyChar ||
                ('0' <= keyChar && '9' >= keyChar))
                {
                    super.processKeyEvent(e);
                    postActionEvent();
                }
                int keyCode = e.getKeyCode();
                if(KeyEvent.VK_DELETE==keyCode ||
                   KeyEvent.VK_BACK_SPACE==keyCode)
                {
                    super.processKeyEvent(e);
                    postActionEvent();
                }
                if(KeyEvent.VK_LEFT==keyCode ||
                   KeyEvent.VK_RIGHT==keyCode)
                {
                    super.processKeyEvent(e);
                }
            }
        }
    }

    private int sliderMode;
    private JSlider slider1, slider2, slider3;
    private JLabel label1, label2, label3;
    private NumberField value1, value2, value3;
    private JRadioButton rad1, rad2, rad3;
    private ButtonGroup choice;
    private Colour colour;
    boolean activeModel = false;
    boolean inChangeListener = false;

    private Wheel wheel;
    private Swatch swatch;

    /**
    * Constructs an instance of the class in RGB feedback mode
    */
    public ColourWheel()
    {
        this(SLIDER_RGB);
    }

    /**
    * Updates the colour values - necessary interface routine
    */
    public void updateChooser()
    {
        if(inChangeListener) return;
        // put model colour to display
        Color c = getColorSelectionModel().getSelectedColor();
        colour = Colour.build(c);
        wheel.setColour(colour);
        updateSlider();
    }

    /**
    * Constructs an instance of the class with the desired mode
    * of sliders HLS, RGB or HSV
    * @param mode chooser mode to start with
    */
    public ColourWheel(int mode)
    {
        setLayout(new BorderLayout());
        colour=Colour.rgb(0, 0, 0);

        wheel = new Wheel(colour);
        add(wheel, BorderLayout.CENTER);
        wheel.addMouseListener(this);
        wheel.addMouseMotionListener(this);

        JPanel bulk = new JPanel();
        add(bulk, BorderLayout.EAST);
        bulk.setLayout(new GridLayout(2,1));

        swatch = new Swatch();
        bulk.add(swatch);

        JPanel controls = new JPanel();
        bulk.add(controls);

        controls.setLayout(new BorderLayout());

        bulk = new JPanel();
        bulk.setLayout(new GridLayout(1,3));
        choice = new ButtonGroup();
        rad1 = new JRadioButton("HLS", false);
        rad1.addChangeListener(this);
        choice.add(rad1);
        bulk.add(rad1);
        rad2 = new JRadioButton("HSV", false);
        rad2.addChangeListener(this);
        choice.add(rad2);
        bulk.add(rad2);
        rad3 = new JRadioButton("RGB", true);
        rad3.addChangeListener(this);
        choice.add(rad3);
        bulk.add(rad3);

        controls.add(bulk, BorderLayout.NORTH);

        bulk = new JPanel();
        bulk.setLayout(new GridLayout(3,1));
        bulk.add(label1 = new JLabel(""));
        bulk.add(label2 = new JLabel(""));
        bulk.add(label3 = new JLabel(""));
        controls.add(bulk, BorderLayout.WEST);

        bulk = new JPanel();
        bulk.setLayout(new GridLayout(3,1));
        bulk.add(value1 = new NumberField(3));
        value1.addActionListener(this);
        bulk.add(value2 = new NumberField(3));
        value2.addActionListener(this);
        bulk.add(value3 = new NumberField(3));
        value3.addActionListener(this);
        controls.add(bulk, BorderLayout.EAST);

        bulk = new JPanel();
        bulk.setLayout(new GridLayout(3,1));
        bulk.add(slider1 = new JSlider(0,100,50));
        slider1.addChangeListener(this);
        slider1.addMouseListener(this);
        bulk.add(slider2 = new JSlider(0,100,50));
        slider2.addChangeListener(this);
        slider2.addMouseListener(this);
        bulk.add(slider3 = new JSlider(0,100,50));
        slider3.addChangeListener(this);
        slider3.addMouseListener(this);
        controls.add(bulk, BorderLayout.CENTER);

        label1.setText("");
        label2.setText("");
        label3.setText("");

        switch(mode)
        {
        case SLIDER_HLS:
            setSliderMode(SLIDER_HLS);
            break;
        case SLIDER_HSV:
            setSliderMode(SLIDER_HSV);
            break;
        default:
            setSliderMode(SLIDER_RGB);
            break;
        }
    }

    /**
    * Listener interface for the changes to the scrollbars
    * @param e event signalled
    */
    public void stateChanged(ChangeEvent e)
    {
        if(inChangeListener) return;
        inChangeListener = true;
        try {
            if(e.getSource() == rad1 || e.getSource() == rad2 || e.getSource() == rad3)
            {
                if(rad1.isSelected()) setSliderMode(SLIDER_HLS);
                else if(rad2.isSelected()) setSliderMode(SLIDER_HSV);
                else setSliderMode(SLIDER_RGB);
            }
            else
            {
                colour = getSliderColour();
                if(activeModel)
                {
                    ColorSelectionModel csm = getColorSelectionModel();
                    if(csm != null) csm.setSelectedColor(colour.color());
                }
                swatch.repaint();
                wheel.setColour(colour);
            }
        }finally{
            inChangeListener = false;
        }
    }

    private void updateFromMouse(Point pt)
    {
        boolean save = inChangeListener;
        try {
            inChangeListener = true;
            colour = wheel.setColour(pt);
            if(activeModel)
            {
                ColorSelectionModel csm = getColorSelectionModel();
                if(csm != null) csm.setSelectedColor(colour.color());
            }
            swatch.repaint();
            updateSlider();
        } finally {
            inChangeListener = false;
        }
    }

    /**
    * Listener interface for the mouse cursor on the indicator wheel
    * @param e event signalled
    */
    public void mouseDragged(MouseEvent e)
    {
        if(e.getSource() == wheel)
        {
            updateFromMouse(e.getPoint());
        }
    }

    /**
    * Listener interface dummy
    * @param event signalled
    */
    public void mouseMoved(MouseEvent e)
    {
    }

    /**
    * Listener interface for the mouse cursor on the indicator wheel
    * @param e event signalled
    */
    public void mouseClicked(MouseEvent e)
    {
        if(e.getSource() == wheel)
        {
            updateFromMouse(e.getPoint());
        }
    }

    /**
    * Listener interface for the mouse cursor on the indicator wheel or sliders
    * @param e event signalled
    */
    public void mousePressed(MouseEvent e)
    {
        if(e.getSource() == wheel)
        {
            wheel.setDragStart(e.getPoint());
            return;
        }
        inChangeListener = (
            sliderMode == SLIDER_RGB) ||
            e.getSource() == slider1;
    }

    /**
    * Listener interface for the mouse cursor on the sliders - gesture has
    * ended so time-consuming updates can happen
    * @param e event signalled
    */
    public void mouseReleased(MouseEvent e)
    {
        inChangeListener = false;
        stateChanged(new ChangeEvent(e.getSource()));
        wheel.repaint();
    }

    /**
    * Listener interface dummy
    * @param event signalled
    */
    public void mouseEntered(MouseEvent e)
    {
    }

    /**
    * Listener interface dummy
    * @param event signalled
    */
    public void mouseExited(MouseEvent e)
    {
    }

    /**
    * Listener interface for the changes to the text fields
    * @param e event signalled
    */
    public void actionPerformed(ActionEvent e)
    {
        int v1=0, v2=0, v3=0;
        try {
            v1 = Integer.parseInt(value1.getText());
        } catch (NumberFormatException ex1) {v1 = 0;}
        try {
            v2 = Integer.parseInt(value2.getText());
        } catch (NumberFormatException ex1) {v1 = 0;}
        try {
            v3 = Integer.parseInt(value3.getText());
        } catch (NumberFormatException ex1) {v1 = 0;}

        if(v1 < 0) v1 = 0;
        if(v1 > SliderModeMax[sliderMode][0]) v1 = SliderModeMax[sliderMode][0];
        if(value1.getText().length() > 0) value1.setText(""+v1);

        if(v2 < 0) v2 = 0;
        if(v2 > SliderModeMax[sliderMode][1]) v2 = SliderModeMax[sliderMode][1];
        if(value2.getText().length() > 0) value2.setText(""+v2);

        if(v3 < 0) v3 = 0;
        if(v3 > SliderModeMax[sliderMode][2]) v3 = SliderModeMax[sliderMode][2];
        if(value3.getText().length() > 0) value3.setText(""+v3);

        Colour col = Colour.rgb(0,0,0);
        switch (sliderMode)
        {
            case SLIDER_RGB:
            {
                col = Colour.rgb(v1/255.0, v2/255.0, v3/255.0);
                break;
            }
            case SLIDER_HLS:
            {
                col = Colour.hls(v1, v2/100.0, v3/100.0);
                break;
            }
            case SLIDER_HSV:
            {
                col = Colour.hsv(v1, v2/100.0, v3/100.0);
                break;
            }
        }
        getColorSelectionModel().setSelectedColor(col.color());
    }


    /**
    * Sliders show RGB
    */
    public static final int SLIDER_RGB = 0;
    /**
    * Sliders show HLS
    */
    public static final int SLIDER_HLS = 1;
    public static final int SLIDER_HSV = 2;

    /**
    * Sliders show HSV
    */
    private static final String[][] SliderModeResource =
    {
        { "R", "G", "B" },       // SLIDER_RGB
        { "H", "L", "S" },       // SLIDER_HLS
        { "H", "S", "V" }        // SLIDER_HSV
    };

    private static final int[][] SliderModeMax =
    {
        { 255, 255, 255 },      // SLIDER_RGB
        { 359, 100, 100 },      // SLIDER_HLS
        { 359, 100, 100 }       // SLIDER_HSV
    };

    private final void remax(int mode)
    {
        try {
        inChangeListener = true;
        slider1.setMaximum(SliderModeMax[mode][0]);
        slider2.setMaximum(SliderModeMax[mode][1]);
        slider3.setMaximum(SliderModeMax[mode][2]);
        } finally {
        inChangeListener = false;
        }
    }

    private void setSliderMode(int mode)
    {
        sliderMode = mode;

        // set slider ranges and put labels on left of slider bars
        remax(mode);

        label1.setText(SliderModeResource[mode][0]);
        label2.setText(SliderModeResource[mode][1]);
        label3.setText(SliderModeResource[mode][2]);

        // Update slider positions and colour patch: the position
        // on the triangle and hexagon colour wheel shouldn't change

        updateSlider();
    }

    /*
    * Get the current colour indicated by the sliders
    */
    private Colour getSliderColour()
    {
        value1.setText(""+slider1.getValue());
        value2.setText(""+slider2.getValue());
        value3.setText(""+slider3.getValue());
        switch (sliderMode)
        {
            case SLIDER_RGB:
            {
                int r = slider1.getValue();
                int g = slider2.getValue();
                int b = slider3.getValue();
                return Colour.rgb(r/255.0, g/255.0, b/255.0);
            }
            case SLIDER_HLS:
            {
                int hue = slider1.getValue();
                int lum = slider2.getValue();
                int sat = slider3.getValue();
                return Colour.hls(hue, lum/100.0, sat/100.0);
            }
            case SLIDER_HSV:
            {
                int hue = slider1.getValue();
                int sat = slider2.getValue();
                int val = slider3.getValue();
                return Colour.hsv(hue, sat/100.0, val/100.0);
            }
        }
        return null;
    }

    /*
    * Update the sliders to indicate the current value of m_colour
    */
    private void updateSlider()
    {
        boolean save = inChangeListener;
        try {
        inChangeListener = true;
        switch (sliderMode)
        {
        case SLIDER_RGB:
        {
            slider1.setValue((int)(colour.r()*255+0.5));
            slider2.setValue((int)(colour.g()*255+0.5));
            slider3.setValue((int)(colour.b()*255+0.5));
            break;
        }
        case SLIDER_HLS:
        {
            double h=colour.h_hls();
            if (h<0) h+= 360;
            slider1.setValue((int)(h+0.5));
            slider2.setValue((int)(colour.l_hls()*100+0.5));
            slider3.setValue((int)(colour.s_hls()*100+0.5));
            break;
        }
        case SLIDER_HSV:
        {
            double h=colour.h_hsv();
            if (h<0) h+= 360;
            slider1.setValue((int)(h+0.5));
            slider2.setValue((int)(colour.s_hsv()*100+0.5));
            slider3.setValue((int)(colour.v_hsv()*100+0.5));
            break;
        }
        }
        value1.setText(""+slider1.getValue());
        value2.setText(""+slider2.getValue());
        value3.setText(""+slider3.getValue());
        }finally{
            inChangeListener = save;
        }
    }

    static JFrame parent;
    static Color c;
    /**
    * Builds as sample colour chooser using this class
    * @param required by language
    */
    public static void main(String[] args)
    {
        parent = new JFrame("Colour Chooser Demo");
        parent.setSize(200, 100);
        final JButton go = new JButton("Show chooser");
        go.addActionListener(new ActionListener() {
            final JColorChooser chooser = new JColorChooser();
            boolean first = true;
            public void actionPerformed(ActionEvent e) {
            if(first)
            {
                first = false;
                ColourWheel x = new ColourWheel();
                AbstractColorChooserPanel[] old =
                    chooser.getChooserPanels();
                AbstractColorChooserPanel[] n = new
                    AbstractColorChooserPanel[old.length+1];
                System.arraycopy(old, 0, n, 1, old.length);
                n[0] = x;
                chooser.setChooserPanels(n);
            }
            JDialog dlg = JColorChooser.createDialog(
                ColourWheel.parent, "Demo", true, chooser,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        ColourWheel.c = chooser.getColor();
                }}, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                }});
            dlg.show();
            go.setBackground(c);
        }});
        parent.getContentPane().add(go);
        JButton stop = new JButton("Exit");
        stop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
        }});
        parent.getContentPane().add(stop, BorderLayout.SOUTH);


        parent.setVisible(true);

    }

    /**
    * Tab decoration - necessary interface routine
    */
    public Icon getSmallDisplayIcon(){return null;}
    /**
    * Tab decoration - necessary interface routine
    */
    public Icon getLargeDisplayIcon(){return null;}
    /**
    * Tab decoration - necessary interface routine
    */
    public String getDisplayName() {return "Colour wheel";}
    /**
    * Sets the chooser panel initial state - necessary interface routine
    */
    public void buildChooser(){
        Color c = colour.color();
        ColorSelectionModel m = getColorSelectionModel();
        m.setSelectedColor(c);
        activeModel = true;
    } //otherwise done in constructor;
}
