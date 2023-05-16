
//Title:        CTC2.0 for Java
//Version:      
//Copyright:    Copyright (c) 1998
//Author:       Mr. TInes
//Company:      Ravna & Tines
//Description:  Free World Freeware


package com.ravnaandtines.ctcjava;
import java.io.*;

public class POP3RetrievedMessage
{
    private StringBuffer text = new StringBuffer();

    // fairly simplistic reader to string jacket
    public POP3RetrievedMessage(BufferedReader r) throws IOException
    {
        String line;
        while(true)
        {
            line = r.readLine();
            if(null == line || line.equals("."))
            {
                break;
            }
            text.append(line+System.getProperty("line.separator"));
        }

    }

    public String toString()
    {
        return text.toString();
    }

    public StringBuffer getBuffer()
    {
        return text;
    }
} 
