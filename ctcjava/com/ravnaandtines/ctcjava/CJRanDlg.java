
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware

package com.ravnaandtines.ctcjava;
import java.awt.*;
import java.awt.event.*;
import java.util.ResourceBundle;

public class CJRanDlg extends Dialog implements MouseMotionListener{

    private class CJRanSample
    {
        int last;
        int min;
        int max;

        CJRanSample()
        {
            last = -1;
            min=0;
            max=10;
        }
    }

    Panel bodyPanel = new Panel();
    static ResourceBundle res = null;
    BorderLayout borderLayout1 = new BorderLayout();
    BorderLayout borderLayout2 = new BorderLayout();
    Label label1 = new Label();

    private static final int BITSPERSAMPLE  = 3;
    private static final int DISCARD = 3;
    private static final int MINCHANGE = (1<<(DISCARD + 1));
    private static final int MARGIN = (1<<BITSPERSAMPLE);
    private static final int CACHESIZE  = 1024;

    private int required;
    private int bits;
    private int nbits;
    private byte[] cache = new byte [CACHESIZE];
    private int ncache;
    private int ocache;
    private CJRanSample mx, my;
    Frame owner;

    public void mouseDragged(MouseEvent e)
    {
        sample(e);
    }
    public void mouseMoved(MouseEvent e)
    {
        sample(e);
    }

    private void sample(MouseEvent e)
    {
        if(required > 0)
        {
            Object[] args = {
                    new Integer(required) };
            label1.setText(java.text.MessageFormat.format(
               res.getString("Please_move_mouse"), args));
            label1.setAlignment(Label.CENTER);
            label1.invalidate();
        }
        else
        {
            OK_actionPerformed();
            return;
        }
        java.awt.Point xy = e.getPoint();
        randomFrom(xy.x, mx);
        randomFrom(xy.y, my);
    }

    public CJRanDlg(Frame frame, String title, boolean modal)
    {
        super(frame, title, modal);
        if(null == res) res =
            ResourceBundle.getBundle("com.ravnaandtines.ctcjava.Res");
        owner = frame;
        enableEvents(java.awt.AWTEvent.MOUSE_EVENT_MASK);
        mx = new CJRanSample();
        my = new CJRanSample();

        bits = 0x00;
        nbits = ncache = 0;

        try
        {
            jbInit();
            add(bodyPanel);
            pack();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    public CJRanDlg(Frame frame)
    {
        this(frame, "", false);
    }


    public CJRanDlg(Frame frame, boolean modal)
    {
        this(frame, "", modal);
    }


    public CJRanDlg(Frame frame, String title)
    {
        this(frame, title, false);
    }

    void jbInit() throws Exception
    {
        setLayout(borderLayout1);
        add(bodyPanel);
        bodyPanel.setLayout(borderLayout2);
        label1.setText(res.getString("Collecting_mouse"));
        label1.setAlignment(Label.CENTER);
        bodyPanel.add(label1, BorderLayout.NORTH);
        bodyPanel.addMouseMotionListener(this);
    }

    void OK_actionPerformed()
    {
        if(required <= 0)
        {
            setVisible(false);
        }
    }

    public void ensure(int bits)
    {
        if(8*ncache + nbits < bits)
        {
            required = bits - (8*ncache + nbits);
            java.awt.Point parentAt = owner.getLocation();
            java.awt.Dimension d = owner.getSize();

            label1.setText(res.getString("Collecting_mouse"));
            label1.setAlignment(Label.CENTER);
            pack();

            java.awt.Dimension d2 = getSize();
            if(d2.width < d.width) d2.width = d.width;
            if(d2.height < d.height) d2.height = d.height;
            setSize(d2);

            setLocation(parentAt.x+(d.width-d2.width)/2,
            parentAt.y+(d.height-d2.height)/2);
            show();
        }
    }

    public void getRaw(byte [] data, int length)
    {
        int size = ncache < length ? ncache : length;
        ncache -= size;
        System.arraycopy(cache, ncache, data, 0, size);
    }

    private void randomFrom(int value, CJRanSample aSample)
    {
        if(value >= aSample.min && aSample.last >= aSample.min)
        {
            if(value <= aSample.max)
            {
                int diff = Math.abs(value - aSample.last);
                int count = 0;
                int mask = 0x1;
                while( (diff >= MINCHANGE) && (count++ < BITSPERSAMPLE))
                {
                    storeBit(value & mask);
                    mask <<= 1;
                    diff /= 2;
                }
            }
            else
            {
                aSample.max = value;
            }
        }
        aSample.last = value;
    }

    private void storeBit(int bit)
    {
        bits <<= 1;
        if(bit != 0) ++bits;
        if(++nbits >= 8)
        {
            nbits = 0;
            if(ncache < CACHESIZE)
            {
                cache[ncache++] ^= (byte) bits;
                if(CACHESIZE == ncache) ocache = CACHESIZE;
            }
            else
            {
                cache[--ocache] ^= (byte) bits;
                if(0 == ocache) ocache = CACHESIZE;
            }
        }
        if(required >= 0) --required;
    }
}


