package com.ravnaandtines.util;

import java.util.*;

public class Res extends java.util.ListResourceBundle
{
    static final Object[][] contents = {
        { "Yes", "Yes"},
        { "No", "No"},
        { "Cancel", "Cancel"},
        { "OK", "OK"},
        { "INFORMATION", "INFORMATION"},
        { "QUERY", "QUERY"},
        { "ERROR", "ERROR"},
        { "WARNING", "WARNING"}
    };

    public Object[][] getContents()
    {
        return contents;
    }
}
