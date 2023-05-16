
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1998
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;

import dtai.gwt.*;
import com.ravnaandtines.util.*;


public class SignatureFolder extends TreeFolder
{
    private Signature sig;
    java.util.ResourceBundle res = java.util.ResourceBundle.getBundle("com.ravnaandtines.ctcjava.Res");
    private CJPubkey key;
    private Username name;
    private static java.awt.Image good = IconSelection.getIcon(IconSelection.TICK);
    private static java.awt.Image bad = IconSelection.getIcon(IconSelection.CROSS);

    public SignatureFolder(Signature sigIn, CJPubkey keyIn, Username nameIn)
    {
        sig = sigIn;
        key = keyIn;
        name = nameIn;
        setLabel(toString());
    }

    public void pubkey()
    {
        add(new PubKeyFolder(sig.toKey()));
    }

    public String toString()
    {
        if(sig.toKey().getUsername().isNull())
        {
            return res.getString("Signature_by_unknown") + sig.getName();
        }
        boolean x = key.checkSignature(name, sig);
        if(x) setImages(good, good);
        else setImages(bad,bad);

        return (x ? res.getString("Good_signature_made") : res.getString("Bad_signature_made_at")) +
            sig.getName();
    }

    public Signature toSig()
    {
        return sig;
    }

    /* Not quite the right place to stick up to ask
     *     if we want to fetch from a keyserver.
     *     public void setExpanded(boolean expanded)
     *     {
     *         if(expanded && sig.toKey().getUsername().isNull())
     *         {
     *             SMTPDialog send = new SMTPDialog(CJGlobals.mainFrame);
     *             send.setText("");
     *             send.show();
     *         }
     * 
     *         super.setExpanded(expanded);
     *     }
     */
}
