/**
 *
 *  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 2005
 *  All rights reserved.
 *
 */

package com.ravnaandtines.ctcjava;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.zip.*;
import java.util.*;

public enum IconSelection
{
    STOP("/org/tango_project/icons22x22/actions/process-stop.png"),
    COPY("/org/tango_project/icons22x22/actions/edit-copy.png"),
    CUT("/org/tango_project/icons22x22/actions/edit-cut.png"),
    FILENEW("/org/tango_project/icons22x22/actions/document-new.png"),
    FILEOPEN("/org/tango_project/icons22x22/actions/document-open.png"),
    FILESAVE("/org/tango_project/icons22x22/actions/document-save.png"),
    HELP("/org/tango_project/icons22x22/apps/help-browser.png"),
    PASTE("/org/tango_project/icons22x22/actions/edit-paste.png"),
    PRINT("/org/tango_project/icons22x22/actions/document-print.png"),
    MAILIN("/org/tango_project/icons22x22/apps/internet-mail.png"),
    MAILOUT("/org/tango_project/icons22x22/actions/mail-message-new.png"),
    LOCK("/org/tango_project/icons22x22/status/locked.png"),
    UNLOCK("/org/tango_project/icons22x22/status/unlocked.png"),
    PREFS("/org/tango_project/icons22x22/categories/preferences-desktop.png"),
    FILESAVEAS("/org/tango_project/icons22x22/actions/document-save-as.png"),
    FOLDERCLOSED("/org/tango_project/icons22x22/places/folder.png"),
    FOLDEROPEN("/org/tango_project/icons22x22/status/folder-open.png"),
    LEAF("/org/tango_project/icons22x22/mimetypes/text-x-generic.png"),  
    READONLY("/org/tango_project/icons22x22/emblems/emblem-readonly.png"),  
    
    KEY("/org/javalobby/icons/20x20png/Key.png"),
    TICK("/org/javalobby/icons/20x20png/Check.png"), 
    DOUBLETICK("/org/javalobby/icons/20x20png/CheckAll.png"), 
    CROSS("/org/javalobby/icons/20x20png/Delete.png"), 
    USER("/org/javalobby/icons/20x20png/User.png"), 
    BINARY("/org/javalobby/icons/20x20png/Data.png"), 
    TEXT("/org/javalobby/icons/20x20png/DocumentDraw.png"), 
    
    ICON("/com/ravnaandtines/tinelogo.png");
    
    private Image theImage = null;
    private String imageFile;
    IconSelection(String f)
    {
        imageFile = f;
    }
    
    public synchronized Image getImage()
    {
        if(null == theImage)
        {
            try {
                java.io.InputStream in = IconSelection.class.getResourceAsStream(imageFile);
                theImage  = javax.imageio.ImageIO.read(in);
            } catch (java.io.IOException io) {
            } catch (IllegalArgumentException io) {                
            }                        
        }
        return theImage;
    }  
}
