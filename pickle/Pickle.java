/***************************************************************************
                                Pickle.java
                             -------------------
    copyright            : (C) 2002 by Mr. Tines
    email                : tines@ravnaandtines.com
 ***************************************************************************/

/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU Library General Public License (e.g. at                           *
 *   http://www.ravnaandtines.com/licences.txt) for more details.          *
 *                                                                         *
 ***************************************************************************/

import java.io.*;
import waba.io.*;
import java.util.*;
import waba.applet.Applet;

public class Pickle {

  private Pickle() {}

  public static void main(String []args)
  {
     // part 0 - fudge the issue
     Applet.currentApplet = new Applet();
     Applet.currentApplet.isApplication = true;

     try
     {
        // part 1 - reads the file
        RandomAccessFile is = new RandomAccessFile(args[0], "r");
        java.io.File f = new java.io.File(args[0]);

        long length = is.length();
        byte[] buffer = new byte[(int)length];
        is.readFully(buffer);

        // part 2 - open the catalog for the file
        String creator = "AngL";    // Angerona Lite creator ID !
        String name = f.getName();
        
        Catalog catC = new Catalog(name.replace('.','_')+"."+creator+".File",
          Catalog.CREATE);

        // part 3 - writes into the records of the pdb file
        catC.setAttributes(Catalog.DB_ATTR_BACKUP |
           Catalog.DB_ATTR_COPY_PREVENTION |
           Catalog.DB_ATTR_OK_TO_INSTALL_NEWER );

        int offset = 0;
        int chunk = 32767;
        while(length > 0)
        {
          if (chunk > length) chunk = (int)length;

          int pos = catC.addRecord(chunk);
          catC.writeBytes(buffer, offset, chunk);

          length -= chunk;
          offset += chunk;
        }
        catC.close();
     } catch (Exception e) {e.printStackTrace();}
  }
}
