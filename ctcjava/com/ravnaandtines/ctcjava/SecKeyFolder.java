
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1998
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;

import dtai.gwt.*;
import com.ravnaandtines.util.*;

public class SecKeyFolder extends TreeFolder implements Sortable
{
    private CJSeckey key;
    private Username name;
    private static java.awt.Image lockImg = IconSelection.getIcon(IconSelection.LOCK);
    private static java.awt.Image unlockImg = IconSelection.getIcon(IconSelection.UNLOCK);

    public SecKeyFolder(CJSeckey keyIn)
    {
        key = keyIn;
        setLabel(toString());
        PubKeyFolder f = new PubKeyFolder(key.getPubkey());
        setImage();
    }

    public SecKeyFolder(CJSeckey keyIn, Username nameIn)
    {
        key = keyIn;
        name = nameIn;
        setLabel(toString());
        setImage();
    }

    public void pubkey()
    {
        PubKeyFolder f = new PubKeyFolder(key.getPubkey());
        this.add(f);
    }

    public String toString()
    {
        if(name != null) return name.getName();
        return key.getPubkey().getName();
    }

    public int compareTo(Sortable b)
    {
        return toString().toUpperCase().compareTo(
        ((SecKeyFolder)b).toString().toUpperCase());
    }

    public CJSeckey toKey()
    {
        return key;
    }

    public void setImage()
    {
        if(key.isLocked())setImages(lockImg, lockImg);
        else setImages(unlockImg, unlockImg);
    }
}
