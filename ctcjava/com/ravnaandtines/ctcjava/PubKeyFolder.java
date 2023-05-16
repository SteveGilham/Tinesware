
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1998
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;

import dtai.gwt.*;
import com.ravnaandtines.util.*;

public class PubKeyFolder extends TreeFolder implements Sortable{
    private CJPubkey key;
    java.util.ResourceBundle res = java.util.ResourceBundle.getBundle("com.ravnaandtines.ctcjava.Res");
    boolean hasBeenExpanded;
    FolderItem data = null;
    FolderItem revoke = null;
    private Username name = null;
    private static java.awt.Image keyImg = IconSelection.getIcon(IconSelection.KEY);

    public PubKeyFolder(CJPubkey keyIn)
    {
        key = keyIn;
        setLabel(toString());
        hasBeenExpanded = false;
        setImages(keyImg, keyImg);
    }

    public PubKeyFolder(CJPubkey keyIn, Username nameIn)
    {
        key = keyIn;
        name = nameIn;
        setLabel(toString());
        hasBeenExpanded = false;
        setImages(keyImg, keyImg);
    }

    public String toString()
    {
        if(name != null) return name.getName();
        return key.getName();
    }

    public int compareTo(Sortable b)
    {
        return toString().toUpperCase().compareTo(
        ((PubKeyFolder)b).toString().toUpperCase());
    }

    public CJPubkey toKey()
    {
        return key;
    }

    public void setExpanded(boolean expanded)
    {
        if(!hasBeenExpanded)
        {
            data = new FolderItem(key.data());
            this.add(data);
            if(key.isRevoked())
            {
                revoke = new FolderItem(res.getString("NOTE:_This_key_has"));
                this.add(revoke);
            }

            for(Signature s = key.getSignature(); !s.isNull();
                s = s.nextSignature())
            {
                SignatureFolder f = new SignatureFolder(s, key, null);
                this.add(f);
                f.pubkey();
            }

            for(CJPubkey k = key.getSubkey(); (k!=null) && !k.isNull();
                k = k.getSubkey())
            {
                PubKeyFolder f = new PubKeyFolder(k);
                this.add(f);
            }

            for(Username u = key.getUsername(); !u.isNull();
                u = u.nextUsername())
            {
                UsernameFolder f = new UsernameFolder(u, key);
                this.add(f);
                for(Signature s = u.getSignature(); !s.isNull();
                    s = s.nextSignature())
                {
                    SignatureFolder f2 = new SignatureFolder(s, key, u);
                    f.add(f2);
                    f2.pubkey();
                }
            }

            hasBeenExpanded = true;
        }
        super.setExpanded(expanded);
    }

    public void able()
    {
        if(!hasBeenExpanded) return;
        data.setLabel(key.data());
    }

    public void reset()
    {
        if(!hasBeenExpanded) return;
        removeAll();
        hasBeenExpanded = false;
        data = null;
        revoke = null;
    }
}
