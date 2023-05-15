/***************************************************************************
                                Revive.java
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

public class Revive {

  private Revive() {}

  public static void main(String []args)
  {
     // part 0 - fudge the issue
     Applet.currentApplet = new Applet();
     Applet.currentApplet.isApplication = true;

     try
     {
        // part 1 - finds the file
        java.io.File f = new java.io.File(args[0]);
        FileOutputStream os = new FileOutputStream(f);

        // part 2 - open the catalog for the file
        String creator = "AngL";    // Angerona Lite creator ID !
        String name = f.getName();

        Catalog catC = new Catalog(name.replace('.','_')+"."+creator+".File",
          Catalog.READ_ONLY);

        // part 3 - reads from records of the pdb file
        int chunk = 32767;
        byte[] buffer = new byte[chunk];
        int recs = catC.getRecordCount();

        for(int i=0; i<recs; ++i)
        {
          catC.setRecordPos(i);
          int length = catC.getRecordSize();
          int offset = 0;
          int left = length;

          while(left > 0)
          {
            int got = catC.readBytes(buffer, offset, left);
            if(got < 0) {i = recs; break;}
            offset += got;
            left -= got;
          }
          os.write(buffer, 0, length);
        }
        catC.close(); //dsC.close();
     } catch (Exception e) {e.printStackTrace();}
  }



} 