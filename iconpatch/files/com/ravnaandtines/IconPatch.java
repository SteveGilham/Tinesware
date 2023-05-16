/*
Copyright © 2003 Mr. Tines <tines@ravnaandtines.com>

This program may be freely redistributed or amended, 
but must retain the attribution above, and amended
versions must be clearly marked as such.

THERE IS NO WARRANTY.
*/

package com.ravnaandtines;

import java.io.*;

class ResourceHeader
{
    byte[] name = new byte[4];
    byte[] id = new byte[2];
    byte[] offset = new byte[4];

    public void write(OutputStream out) throws IOException
    {
       out.write(name);
       out.write(id);
       out.write(offset);
    }

    public ResourceHeader(InputStream in) throws IOException
    {
        in.read(name);
        in.read(id);
        in.read(offset);
    }
    public ResourceHeader(String label, int type, int at)
    {
        name[0] = (byte)(label.charAt(0) & 0xFF);
        name[1] = (byte)(label.charAt(1) & 0xFF);
        name[2] = (byte)(label.charAt(2) & 0xFF);
        name[3] = (byte)(label.charAt(3) & 0xFF);
        id[0] = (byte)((type>>8)&0xFF);
        id[1] = (byte)(type&0xFF);
        setOffset(at);
    }
    public String getName()
    {
        StringBuffer s = new StringBuffer(4);
        s.append((char)(name[0] & 0xFF));
        s.append((char)(name[1] & 0xFF));
        s.append((char)(name[2] & 0xFF));
        s.append((char)(name[3] & 0xFF));
        return s.toString();
    }
    public int getID()
    {
        return ((id[0]&0xFF)<<8) | (id[1] & 0xFF);
    }
    public int getOffset()
    {
        return ((offset[0]&0xFF)<<24) |
               ((offset[1]&0xFF)<<16) |
               ((offset[2]&0xFF)<<8)  | (offset[3] & 0xFF);
    }
    public int length = 0;
    public byte[] value = null;
    public void setOffset(int i)
    {
       offset[3] = (byte)(i&0xFF);
       offset[2] = (byte)((i>>>8)&0xFF);
       offset[1] = (byte)((i>>>16)&0xFF);
       offset[0] = (byte)((i>>>24)&0xFF);
    }
}

public class IconPatch
{

  private IconPatch()
  {
  }

  public static void main(String[] args)
  {
      if(args.length != 3)
      {
          System.out.println("Usage: IconPatch prcfile.prc bigicon.bin smallicon.bin");
          System.exit(0);
      }
      try {
      FileInputStream in = new FileInputStream(args[0]);
      FileInputStream big = new FileInputStream(args[1]);
      FileInputStream small = new FileInputStream(args[2]);
      FileOutputStream out = new FileOutputStream(
        args[0]+".patch.prc", false);
      File source = new File(args[0]);
      long total = source.length();
      System.out.println("File length = "+total);

      source = new File(args[1]);
      long bigLength = source.length();
      source = new File(args[2]);
      long smallLength = source.length();

      byte[] start = new byte[0x4C];
      in.read(start);

      byte[] numrec = new byte[2];
      in.read(numrec);
      int records = ((numrec[0]&0xFF)<<8) | (numrec[1] & 0xFF);
      System.out.println("record count = "+records);
      ResourceHeader[] recordList = new ResourceHeader[records+2];

      int bigRec = -1;
      int smallRec = -1;

      int i = 0;
      int first = 0;
      for(i=0; i<records;++i)
      {
          ResourceHeader r = new ResourceHeader(in);
          recordList[i]=r;
          if(0 == i)
            first = r.getOffset();
          else
            recordList[i-1].length = r.getOffset() -
             recordList[i-1].getOffset();
          if(!r.getName().equals("tAIB"))
            continue;
          if(r.getID() == 1000)
            bigRec = i;
          if(r.getID() == 1001)
            smallRec = i;
      }
      recordList[records-1].length = (int)(total - recordList[records-1].getOffset());
      for(i=0; i<records; ++i)
      {
          ResourceHeader r = recordList[i];
          System.out.println("Name = "+r.getName()+" id = "
           +r.getID()+" offset = "+r.getOffset()+" length = "+r.length);
      }
      int current = start.length+numrec.length+10*records;
      System.out.println("Bytes so far = "+current);
      System.out.println("big record = "+bigRec);
      System.out.println("small record = "+smallRec);

      int increase = 0;
      if (bigRec<0) increase = 10;
      if (smallRec<0) increase += 10;
      int appinfo = ((start[0x34]&0xFF)<<24) |
               ((start[0x35]&0xFF)<<16) |
               ((start[0x36]&0xFF)<<8)  | (start[0x37] & 0xFF);
      int sortinfo = ((start[0x38]&0xFF)<<24) |
               ((start[0x39]&0xFF)<<16) |
               ((start[0x3A]&0xFF)<<8)  | (start[0x3B] & 0xFF);

      System.out.println("Increase by "+increase+" a = "+appinfo
       +" s = "+sortinfo);

      if(increase > 0)
      {
        if(appinfo != 0)
        {
           i = appinfo+increase;
           start[0x37] = (byte)(i&0xFF);
           start[0x36] = (byte)((i>>>8)&0xFF);
           start[0x35] = (byte)((i>>>16)&0xFF);
           start[0x34] = (byte)((i>>>24)&0xFF);
        }
        if(sortinfo != 0)
        {
           i = sortinfo+increase;
           start[0x3B] = (byte)(i&0xFF);
           start[0x3A] = (byte)((i>>>8)&0xFF);
           start[0x39] = (byte)((i>>>16)&0xFF);
           start[0x38] = (byte)((i>>>24)&0xFF);
        }
      }
      out.write(start);
      if(increase > 0)
      {
        i = records + (increase/10);
        numrec[1] = (byte)(i&0xFF);
        numrec[0] = (byte)((i>>>8)&0xFF);
        System.out.println("record count "+i);
      }
      out.write(numrec);

      byte[] blank = new byte[first-current];
      in.read(blank);

      int offset = first+increase;
      for(i=0; i<records; ++i)
      {
          ResourceHeader r = recordList[i];
          r.value = new byte[r.length];
          in.read(r.value);
          r.setOffset(offset);
          if(i == bigRec)
          {
             r.length = (int) bigLength;
             r.value = new byte[r.length];
             big.read(r.value);
          }
          if(i == smallRec)
          {
             r.length = (int) smallLength;
             r.value = new byte[r.length];
             small.read(r.value);
          }
          offset += r.length;
      }
      if(bigRec < 0)
      {
        bigRec = records;
        records++;
        recordList[bigRec] = new ResourceHeader("tAIB", 1000, offset);
        ResourceHeader r = recordList[bigRec];
        r.length = (int) bigLength;
        r.value = new byte[r.length];
        big.read(r.value);
        offset+=bigLength;
      }
      if(smallRec < 0)
      {
        smallRec = records;
        records++;
        recordList[smallRec] = new ResourceHeader("tAIB", 1001, offset);
        ResourceHeader r = recordList[smallRec];
        r.length = (int) smallLength;
        r.value = new byte[r.length];
        small.read(r.value);
        offset+=smallLength;
      }

      for(i=0; i<records; ++i)
      {
         recordList[i].write(out);
          ResourceHeader r = recordList[i];
          System.out.println("Name = "+r.getName()+" id = "
           +r.getID()+" offset = "+r.getOffset()+" length = "+r.length);
      }
      out.write(blank);
      for(i=0; i<records; ++i)
      {
         out.write(recordList[i].value);
      }
      } catch (Exception catchall) {
          catchall.printStackTrace(System.out);
      }
  }
}
