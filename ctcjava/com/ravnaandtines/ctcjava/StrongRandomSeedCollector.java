
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware

package com.ravnaandtines.ctcjava;
import java.awt.event.*;
import javax.swing.*;

public class StrongRandomSeedCollector implements MouseMotionListener {

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

    
    private JDialog shell = null;
    private JLabel label1 = null;

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
    private java.awt.Frame owner;

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
            Object[] args = { new Integer(required) };
            label1.setText(java.text.MessageFormat.format(
               GlobalData.getResourceString("Please_move_mouse"), args));
            label1.setHorizontalAlignment(JLabel.CENTER);
            label1.setVerticalAlignment(JLabel.CENTER);
            label1.invalidate();
        }
        else
        {
            shell.setVisible(false);
            shell.dispose();
            shell = null;
            return;
        }
        java.awt.Point xy = e.getPoint();
        randomFrom(xy.x, mx);
        randomFrom(xy.y, my);
    }

    public StrongRandomSeedCollector(java.awt.Frame frame)
    {
        owner = frame;
        
        mx = new CJRanSample();
        my = new CJRanSample();
        
        bits = 0x00;
        nbits = ncache = 0;
    }

    private synchronized void jbInit()
    {
        if(null == shell) {
            shell = new JDialog(owner, GlobalData.getResourceString("Collecting_random"), true);
            shell.getContentPane().setLayout(new java.awt.BorderLayout());
            label1 = new JLabel();
            shell.getContentPane().add(label1, java.awt.BorderLayout.NORTH);
            shell.addMouseMotionListener(this);
        }
    }

    public void ensure(int bits)
    {
        if(8*ncache + nbits < bits)
        {
            jbInit();
            required = bits - (8*ncache + nbits);
            java.awt.Point parentAt = owner.getLocation();
            java.awt.Dimension d = owner.getSize();

            label1.setText(GlobalData.getResourceString("Collecting_mouse"));
            label1.setHorizontalAlignment(JLabel.CENTER);
            label1.setVerticalAlignment(JLabel.CENTER);
            shell.pack();

            java.awt.Dimension d2 = shell.getSize();
            if(d2.width < d.width) d2.width = d.width;
            if(d2.height < d.height) d2.height = d.height;
            shell.setSize(d2);

            shell.setLocation(parentAt.x+(d.width-d2.width)/2,
            parentAt.y+(d.height-d2.height)/2);
            shell.setVisible(true);
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


