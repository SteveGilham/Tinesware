package com.ravnaandtines.util.swing;

import javax.swing.colorchooser.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import com.ravnaandtines.util.gui.Colour;

/**
*  Class ColourSwatch - This is a panel that can be used within the
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

public class ColourSwatch extends AbstractColorChooserPanel
    implements ChangeListener, MouseListener, MouseMotionListener, ActionListener
{
    private class HLSwatch extends Component
    {
        public Dimension getPreferredSize() {return new Dimension(240,100);}
        public Dimension getMinimumSize() {return getPreferredSize();}
        public Dimension getMaximumSize() {return getPreferredSize();}

        private final int WIDTH = 240;
        private final int HEIGHT = 100;

        Image light, sat;
        private boolean isSat;

        public HLSwatch()
        {
            super();
            isSat = true;

            int[] temp = new int[WIDTH*HEIGHT];
            int i, j;
            for(i=0; i<WIDTH; ++i) for(j=0; j<HEIGHT; ++j)
            {
                Color c = Colour.hls(360*i/((double)WIDTH), 0.5, j/((double)HEIGHT)).color();
                temp[i+j*WIDTH] = c.getRGB();
                MemoryImageSource mem = new MemoryImageSource(
                        WIDTH, HEIGHT, temp, 0, WIDTH);
                sat = createImage(mem);
            }
            int[] temp2 = new int[WIDTH*HEIGHT];
            for(i=0; i<WIDTH; ++i) for(j=0; j<HEIGHT; ++j)
            {
                Color c = Colour.hls(360*i/((double)WIDTH), j/((double)HEIGHT), 0.5).color();
                temp2[i+j*WIDTH] = c.getRGB();
                MemoryImageSource mem = new MemoryImageSource(
                        WIDTH, HEIGHT, temp2, 0, WIDTH);
                light = createImage(mem);
            }
        }

        public void paint(Graphics g)
        {
            if(isSat) g.drawImage(sat, 0, 0, null);
            else g.drawImage(light, 0, 0, null);

            int x = (int)(ColourSwatch.this.colour.h_hls()*WIDTH/360.0+0.5);
            if(x < 0) x += WIDTH;
            int y = isSat ?
                (int)(ColourSwatch.this.colour.s_hls()*HEIGHT + 0.5) :
                (int)(ColourSwatch.this.colour.l_hls()*HEIGHT + 0.5) ;

            g.setColor(Colour.hsv(
                ColourSwatch.this.colour.h_hls()+180, 1, 1).color());
            g.drawLine(x-5, y, x+5, y);
            g.drawLine(x, y-5, x, y+5);


        }

        public void setSat(boolean b)
        {
            isSat = b;
        }

        public Colour getColour(Point p)
        {
            if(p.x<0) p.x = 0; if(p.x>WIDTH)p.x=WIDTH;
            if(p.y<0) p.y = 0; if(p.y>HEIGHT)p.y=HEIGHT;
            return isSat?
                Colour.hls(360*p.x/((double)WIDTH),
                ColourSwatch.this.slider1.getValue()/100.0,
                p.y/((double)HEIGHT)):
                Colour.hls(360*p.x/((double)WIDTH),
                p.y/((double)HEIGHT),
                ColourSwatch.this.slider2.getValue()/100.0);
        }
    }

    private class Swatch extends Component
    {
        public Dimension getPreferredSize() {return new Dimension(200,150);}
        public Dimension getMinimumSize() {return getPreferredSize();}
        public Dimension getMaximumSize() {return new Dimension(250,250);}

        public void paint(Graphics g)
        {
            Dimension sz = getSize();
            g.setColor(ColourSwatch.this.getColorSelectionModel().getSelectedColor());
            g.fillRect(5,5, sz.width-10, sz.height-10);
        }
    }

    private class NumberField extends JTextField
    {
        public NumberField() {super();}
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

    private class LSwatch extends Component
    {
        private final int WIDTH = 240;
        private final int HEIGHT = 10;
        public Dimension getPreferredSize() {return new Dimension(WIDTH,HEIGHT);}
        public Dimension getMinimumSize() {return getPreferredSize();}
        public Dimension getMaximumSize() {return getPreferredSize();}

        Image light;

        public LSwatch()
        {
            super();
        }

        public void paint(Graphics g)
        {
            if(light!=null) light.flush();
            int[] temp = new int[WIDTH*HEIGHT];
            int i, j;
            double hue = ColourSwatch.this.colour.h_hls();
            double sat = ColourSwatch.this.colour.l_hls();

            for(i=0; i<WIDTH; ++i)
            {
                int c = Colour.hls(hue, ((double)i)/WIDTH , sat).color().getRGB();
                for(j=0; j<HEIGHT;++j) temp[i+j*WIDTH] = c;
                MemoryImageSource mem = new MemoryImageSource(
                        WIDTH, HEIGHT, temp, 0, WIDTH);
                light = createImage(mem);
            }
            g.drawImage(light, 0, 0, null);
        }
    }
    private class SSwatch extends Component
    {
        private final int WIDTH = 240;
        private final int HEIGHT = 10;
        public Dimension getPreferredSize() {return new Dimension(WIDTH,HEIGHT);}
        public Dimension getMinimumSize() {return getPreferredSize();}
        public Dimension getMaximumSize() {return getPreferredSize();}


        Image sat;

        public SSwatch()
        {
            super();
        }

        public void paint(Graphics g)
        {
            if(sat!=null) sat.flush();
            int[] temp = new int[WIDTH*HEIGHT];
            int i, j;
            double hue = ColourSwatch.this.colour.h_hls();
            double light = ColourSwatch.this.colour.l_hls();

            for(i=0; i<WIDTH; ++i)
            {
                int c = Colour.hls(hue, light, ((double)i)/WIDTH).color().getRGB();
                for(j=0; j<HEIGHT;++j) temp[i+j*WIDTH] = c;
                MemoryImageSource mem = new MemoryImageSource(
                        WIDTH, HEIGHT, temp, 0, WIDTH);
                sat = createImage(mem);
            }
            g.drawImage(sat, 0, 0, null);
        }
    }

    private HLSwatch hlswatch;
    private LSwatch lswatch;
    private SSwatch sswatch;
    private Swatch swatch;
    JSlider slider1, slider2;
    JRadioButton light, sat;
    ButtonGroup lsgrp;
    NumberField[] text = new NumberField[9];
    String[] names = {"R","G","B","H","L","S","H","S","V"};

    /**
    * Constructs an instance of the class
    */
    public ColourSwatch()
    {
        colour = Colour.rgb(0,0,0);
        hlswatch = new HLSwatch();

        hlswatch.addMouseListener(this);
        hlswatch.addMouseMotionListener(this);

        setLayout(new BorderLayout());

        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(hlswatch, BorderLayout.CENTER);
        JPanel p2 = new JPanel();

        p2.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        p2.setLayout(new GridLayout(3,1));
        lsgrp = new ButtonGroup();
        JPanel p3 = new JPanel();

        p3.add(light = new JRadioButton("Lightness", false));
        light.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            hlswatch.setSat(!light.isSelected());
            hlswatch.repaint();
        }});
        lsgrp.add(light);
        p3.add(sat = new JRadioButton("Saturation", true));
        sat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            hlswatch.setSat(sat.isSelected());
            hlswatch.repaint();
        }});
        lsgrp.add(sat);
        p2.add(p3);

        p3 = new JPanel();
        p3.setLayout(new GridLayout(2,1));

        p3.add(slider1 = new JSlider());
        slider1.setMaximum(100);
        slider1.addChangeListener(this);
        slider1.setPaintTrack( false );
        p3.add(lswatch = new LSwatch());
        p2.add(p3);

        p3 = new JPanel();
        p3.setLayout(new GridLayout(2,1));

        p3.add(slider2 = new JSlider());
        slider2.setMaximum(100);
        slider2.addChangeListener(this);
        slider2.setPaintTrack( false );
        p3.add(sswatch = new SSwatch());
        p2.add(p3);

        p.add(p2, BorderLayout.SOUTH);

        add(p, BorderLayout.CENTER);

        p = new JPanel();
        p.setLayout(new BorderLayout());
        swatch = new Swatch();
        p.add(swatch, BorderLayout.CENTER);

        p2 = new JPanel();
        p2.setLayout(new GridLayout(3,6));
        for(int i=0; i<3; ++i)
        {
            for(int j=0; j<3; ++j)
            {
                p2.add(new JLabel(names[i+j*3], JLabel.RIGHT));
                p2.add(text[i+j*3] = new NumberField(3));
                text[i+j*3].addActionListener(this);
            }
        }
        p.add(p2, BorderLayout.SOUTH);

        add(p, BorderLayout.EAST);

        setSliders();
    }

    private Colour colour;
    boolean activeModel = false;
    boolean inChangeListener = false;

    /**
    * Listener interface for the changes to the scrollbars
    * @param e event signalled
    */
    public void stateChanged(ChangeEvent e)
    {
        if(inChangeListener) return;
        inChangeListener = true;
        try {
            colour = Colour.hls(
                colour.h_hls(),
                slider1.getValue()/100.0,
                slider2.getValue()/100.0);
            if(activeModel)
            {
                ColorSelectionModel csm = getColorSelectionModel();
                if(csm != null) csm.setSelectedColor(colour.color());
            }
            swatch.repaint();
            hlswatch.repaint();
            lswatch.repaint();
            sswatch.repaint();
            updateText();
        }finally{
            inChangeListener = false;
        }
    }

    /**
    * Listener interface for the changes to the text fields
    * @param e event signalled
    */
    public void actionPerformed(ActionEvent e)
    {
        int v1=0;
        Colour col = colour;

        if(e.getSource() == text[0])
        {
            try {
                v1 = Integer.parseInt(text[0].getText());
            } catch (NumberFormatException ex0) {v1 = 0;}
            if(v1 < 0) v1 = 0;
            if(v1 > 255) v1 = 255;
            col = Colour.rgb(v1/255.0, col.g(), col.b());
        }
        else if(e.getSource() == text[1])
        {
            try {
                v1 = Integer.parseInt(text[1].getText());
            } catch (NumberFormatException ex1) {v1 = 0;}
            if(v1 < 0) v1 = 0;
            if(v1 > 255) v1 = 255;
            col = Colour.rgb(col.r(), v1/255.0, col.b());
        }
        else if(e.getSource() == text[2])
        {
            try {
                v1 = Integer.parseInt(text[2].getText());
            } catch (NumberFormatException ex2) {v1 = 0;}
            if(v1 < 0) v1 = 0;
            if(v1 > 255) v1 = 255;
            col = Colour.rgb(col.r(), col.g(), v1/255.0);
        }
        else if(e.getSource() == text[3])
        {
            try {
                v1 = Integer.parseInt(text[3].getText());
            } catch (NumberFormatException ex3) {v1 = 0;}
            if(v1 < 0) v1 = 0;
            if(v1 > 360) v1 = 360;
            col = Colour.hls(v1, col.l_hls(), col.s_hls());
        }
        else if(e.getSource() == text[4])
        {
            try {
                v1 = Integer.parseInt(text[4].getText());
            } catch (NumberFormatException ex4) {v1 = 0;}
            if(v1 < 0) v1 = 0;
            if(v1 > 100) v1 = 100;
            col = Colour.hls(col.h_hls(), v1, col.s_hls());
        }
        else if(e.getSource() == text[5])
        {
            try {
                v1 = Integer.parseInt(text[5].getText());
            } catch (NumberFormatException ex5) {v1 = 0;}
            if(v1 < 0) v1 = 0;
            if(v1 > 100) v1 = 100;
            col = Colour.hls(col.h_hls(), col.l_hls(), v1);
        }
        else if(e.getSource() == text[6])
        {
            try {
                v1 = Integer.parseInt(text[6].getText());
            } catch (NumberFormatException ex6) {v1 = 0;}
            if(v1 < 0) v1 = 0;
            if(v1 > 360) v1 = 360;
            col = Colour.hsv(v1, col.s_hsv(), col.v_hsv());
        }
        else if(e.getSource() == text[7])
        {
            try {
                v1 = Integer.parseInt(text[7].getText());
            } catch (NumberFormatException ex7) {v1 = 0;}
            if(v1 < 0) v1 = 0;
            if(v1 > 100) v1 = 100;
            col = Colour.hsv(col.h_hsv(), v1, col.v_hsv());
        }
        else if(e.getSource() == text[8])
        {
            try {
                v1 = Integer.parseInt(text[8].getText());
            } catch (NumberFormatException ex8) {v1 = 0;}
            if(v1 < 0) v1 = 0;
            if(v1 > 100) v1 = 100;
            col = Colour.hsv(col.h_hsv(), col.s_hsv(), v1);
        }
        getColorSelectionModel().setSelectedColor(col.color());
    }


    private void updateText()
    {
            int i = (int)(colour.r()*255.0+0.5);
            if(i<0) i=0; if(i>255) i = 255;
            text[0].setText(""+i);
            i = (int)(colour.g()*255.0+0.5);
            if(i<0) i=0; if(i>255) i = 255;
            text[1].setText(""+i);
            i = (int)(colour.b()*255.0+0.5);
            if(i<0) i=0; if(i>255) i = 255;
            text[2].setText(""+i);

            i = (int)(colour.h_hls()+0.5);
            if(i<0) i+=260; if(i>360) i -= 360;
            text[3].setText(""+i);
            i = (int)(colour.l_hls()*100.0+0.5);
            if(i<0) i=0; if(i>100) i = 100;
            text[4].setText(""+i);
            i = (int)(colour.s_hls()*100.0+0.5);
            if(i<0) i=0; if(i>100) i = 100;
            text[5].setText(""+i);

            i = (int)(colour.h_hsv()+0.5);
            if(i<0) i+=260; if(i>360) i -= 360;
            text[6].setText(""+i);
            i = (int)(colour.s_hsv()*100.0+0.5);
            if(i<0) i=0; if(i>100) i = 100;
            text[7].setText(""+i);
            i = (int)(colour.v_hsv()*100.0+0.5);
            if(i<0) i=0; if(i>100) i = 100;
            text[8].setText(""+i);
    }

    private void setSliders()
    {
        boolean save = inChangeListener;
        inChangeListener = true;
        try {
            slider1.setValue((int)(colour.l_hls()*100 + 0.5));
            slider2.setValue((int)(colour.s_hls()*100 + 0.5));
        }finally{
            inChangeListener = save;
        }
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
        hlswatch.setBackground(c);
        repaint();
        setSliders();
        updateText();
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
                ColourSwatch x = new ColourSwatch();
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
    public String getDisplayName() {return "Colour swatch";}

    /**
    * Sets the chooser panel initial state - necessary interface routine
    */
    public void buildChooser(){
        Color c = colour.color();
        ColorSelectionModel m = getColorSelectionModel();
        m.setSelectedColor(c);
        activeModel = true;
        updateText();
    } //otherwise done in constructor;

    /**
    * Listener interface for the mouse cursor on the sample swatch
    * @param e event signalled
    */
    public void mouseClicked(MouseEvent e)
    {
        if(e.getSource() == hlswatch)
        {
            colour = hlswatch.getColour(e.getPoint());
            if(activeModel) buildChooser();
            setSliders();
            hlswatch.repaint();
        }
    }

    /**
    * Listener interface dummy
    * @param event signalled
    */
    public void mousePressed(MouseEvent e)
    {
        //TODO: implement this java.awt.event.MouseListener method;
    }

    /**
    * Listener interface dummy
    * @param event signalled
    */
    public void mouseReleased(MouseEvent e)
    {
        //TODO: implement this java.awt.event.MouseListener method;
    }

    /**
    * Listener interface dummy
    * @param event signalled
    */
    public void mouseEntered(MouseEvent e)
    {
        //TODO: implement this java.awt.event.MouseListener method;
    }

    /**
    * Listener interface dummy
    * @param event signalled
    */
    public void mouseExited(MouseEvent e)
    {
        //TODO: implement this java.awt.event.MouseListener method;
    }

    /**
    * Listener interface dummy
    * @param event signalled
    */
    public void mouseDragged(MouseEvent e)
    {
        if(e.getSource() == hlswatch)
        {
            colour = hlswatch.getColour(e.getPoint());
            if(activeModel) buildChooser();
            setSliders();
            hlswatch.repaint();
        }
    }

    /**
    * Listener interface dummy
    * @param event signalled
    */
    public void mouseMoved(MouseEvent e)
    {
        //TODO: implement this java.awt.event.MouseMotionListener method;
    }

}
