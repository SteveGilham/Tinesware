
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1998
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;

import dtai.gwt.*;
import com.ravnaandtines.util.*;

public class UsernameFolder extends TreeFolder
{
    private Username name;
    java.util.ResourceBundle res = java.util.ResourceBundle.getBundle("com.ravnaandtines.ctcjava.Res");
    private CJPubkey key;
    private static java.awt.Image user = IconSelection.getIcon(IconSelection.USER);

    public UsernameFolder(Username nameIn, CJPubkey keyIn)
    {
        name = nameIn;
        key = keyIn;
        setLabel(toString());
        setImages(user,user);
    }

    public String toString()
    {
        return res.getString("UserID:")+name.getName();
    }

    public Username toName()
    {
        return name;
    }
}
