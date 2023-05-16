
//Title:        CTC2.0 for Java
//Version:
//Copyright:    Copyright (c) 1997
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.util;

import java.awt.Frame;
import dtai.gwt.Gadget;
import java.util.*;

public class MessageBox {
    public static final int YES = 0;
    static ResourceBundle res = ResourceBundle.getBundle("com.ravnaandtines.util.Res");
    public static final int NO = 1;
    public static final int YES_NO = 2;
    private static String[] yn = {
        res.getString("Yes"), res.getString("No")        };

    public static final int YES_NO_CANCEL = 3;
    private static String[] ync = {
        res.getString("Yes"),
        res.getString("No"), res.getString("Cancel")        };

    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;
    private static String[] ok = {
        res.getString("OK")        };

    public static final int OK_CANCEL = 7;
    private static String[] oc = {
        res.getString("OK"), res.getString("Cancel")        };

    Gadget innerC = null;

    int type = ERROR;

    Frame f = null;
    String title = null;
    String message = null;
    int result = -1;

    StickUp box = null;

    public MessageBox()
    {
    }

    public void setFrame(Frame parent)
    {
        f = parent;
    }

    public void setTitle(String t)
    {
        title = t;
    }

    public void setMessage(String m)
    {
        message = m;
    }

    public String getTitle()
    {
        return title;
    }

    public String getMessage()
    {
        return message;
    }

    public void setType(int t)
    {
        if(t>=YES_NO && t <=ERROR) type = t;
        else if (OK_CANCEL == t) type = t;
    }

    public void addInnerComponent(Gadget c)
    {
        innerC = c;
    }


    public void show()
    {
        int boxtype = StickUp.ERROR;
        String [] label = ok;
        switch(type)
        {
        case YES_NO:
            label = yn;
            boxtype = StickUp.QUERY;
            break;
        case YES_NO_CANCEL:
            label = ync;
            boxtype = StickUp.QUERY;
        case OK_CANCEL:
            label = oc;
            boxtype = StickUp.INFO;
            break;
        }
        box = new StickUp(f, boxtype, message, label);
        if(null != title) box.setTitle(title);
        if(null != innerC) box.addInnerComponent(innerC);

        box.show();

        result = box.getResult();
    }

    public int getResult()
    {
        return result;
    }
}


