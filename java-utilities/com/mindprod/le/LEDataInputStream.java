package com.mindprod.le;
import java.io.*;

/*
 * LEDataInputStream.java
 *
 * Copyright (c) 1998
 * Roedy Green
 * Canadian Mind Products
 * 5317 Barker Avenue
 * Burnaby, BC Canada V5H 2N6
 * tel: (604) 435-3016
 * mailto:roedy@mindprod.com
 * http://mindprod.com
 *
 *
 * Version 1.0 1998 January 6
 *         1.1 1998 January 7 - officially implements DataInput
 *         1.2 1998 January 9 - add LERandomAccessFile
 *         1.3 1998 August 27 - fix bug, readFully instead of read.
 *         1.4 1998 November 10 - add address and phone.
 *
 * Version Tines1.5 March 16, 1999 - make the class actually implement
 *                                   (Filter)InputStream
 *
 * Very similar to DataInputStream except it reads little-endian instead of
 * big-endian binary data.
 * We can't extend DataInputStream directly since it has only final methods.
 * This forces us implement LEDataInputStream with a DataInputStream object,
 * and use wrapper methods.

 LEDataStream 1.4 : little-endian replacments for DataInputStream,
DataOutputStream and RandomAccessFile.  They work just like
DataInputStream, DataOutputStream and RandomAccessFile except they work
with little-endian binary data.  Normally Java binary I/O is done with
big-endian data, with the most significant byte of an integer or float
first.  Intel and Windows 95 tend to work with little endian data in
native files.  LEInputStream, LEOutputstream and LERandomAccessFile will
let you read and write such files.  Source code provided.  Version 1.3
corrects a bug.  read was used where it should have used readFully.  pMay
be freely distributed for any purpose but military.  Copyright 1998 by
Roedy Green of Canadian Mind Products.

 */


public
  class LEDataInputStream extends FilterInputStream implements DataInput
  {
  /**
    * constructor
    */
  public LEDataInputStream(InputStream inin)
  {
    super(inin);
    this.d = new DataInputStream(in);
    w = new byte[8];
  }

  // L I T T L E   E N D I A N   R E A D E R S
  // Little endian methods for multi-byte numeric types.
  // Big-endian do fine for single-byte types and strings.
  /**
    * like DataInputStream.readShort except little endian.
    */
  public final short readShort() throws IOException
  {
    d.readFully(w, 0, 2);
    return (short)(
                  (w[1]&0xff) << 8 |
                  (w[0]&0xff));
  }

  /**
    * like DataInputStream.readUnsignedShort except little endian.
    * Note, returns int even though it reads a short.
    */
  public final int readUnsignedShort() throws IOException
  {
    d.readFully(w, 0, 2);
    return (
           (w[1]&0xff) << 8 |
           (w[0]&0xff));
  }

  /**
    * like DataInputStream.readChar except little endian.
    */
  public final char readChar() throws IOException
  {
    d.readFully(w, 0, 2);
    return (char) (
                  (w[1]&0xff) << 8 |
                  (w[0]&0xff));
  }

  /**
    * like DataInputStream.readInt except little endian.
    */
  public final int readInt() throws IOException
  {
    d.readFully(w, 0, 4);
    return
      (w[3]&0xff) << 24 |
      (w[2]&0xff) << 16 |
      (w[1]&0xff) <<  8 |
      (w[0]&0xff);
  }

  /**
    * like DataInputStream.readLong except little endian.
    */
  public final long readLong() throws IOException
  {
    d.readFully(w, 0, 8);
    return
      (long)(w[7]&0xff) << 56 |
      (long)(w[6]&0xff) << 48 |
      (long)(w[5]&0xff) << 40 |
      (long)(w[4]&0xff) << 32 |
      (long)(w[3]&0xff) << 24 |
      (long)(w[2]&0xff) << 16 |
      (long)(w[1]&0xff) <<  8 |
      (long)(w[0]&0xff);
  }

  /**
    * like DataInputStream.readFloat except little endian.
    */
  public final float readFloat() throws IOException
  {
    return Float.intBitsToFloat(readInt());
  }

  /**
    * like DataInputStream.readDouble except little endian.
    */
  public final double readDouble() throws IOException
  {
    return Double.longBitsToDouble(readLong());
  }

  // p u r e l y   w r a p p e r   m e t h o d s
  // We can't simply inherit since dataInputStream is final.

  /* Watch out, may return fewer bytes than requested. */
  public final int read(byte b[], int off, int len) throws IOException
  {
    // For efficiency, we avoid one layer of wrapper
    return in.read(b, off, len);
  }

  public final void readFully(byte b[]) throws IOException
  {
    d.readFully(b, 0, b.length);
  }

  public final void readFully(byte b[], int off, int len) throws IOException
  {
    d.readFully(b, off, len);
  }

  public final int skipBytes(int n) throws IOException
  {
    return d.skipBytes(n);
  }

  public final long skip(long n) throws IOException
  {
    return d.skip(n);
  }

  /* only reads one byte */
  public final boolean readBoolean() throws IOException
  {
    return d.readBoolean();
  }

  public final byte readByte() throws IOException
  {
    return d.readByte();
  }

  public final int read() throws IOException
  {
    return d.read();
  }

  // note: returns an int, even though says Byte.
  public final int readUnsignedByte() throws IOException
  {
    return d.readUnsignedByte();
  }

  public final String readLine() throws IOException
  {
    return d.readLine();
  }

  public final String readUTF() throws IOException
  {
    return d.readUTF();
  }

// Note. This is a STATIC method!
  public final static String readUTF(DataInput in) throws IOException
  {
    return DataInputStream.readUTF(in);
  }

  public final  void close() throws IOException   {
    d.close();
  }

  public int available() throws IOException
  {
    return d.available();
  }
  public synchronized void mark(int readlimit)
  {
    d.mark(readlimit);
  }
  public synchronized void reset() throws IOException
  {
    d.reset();
  }
  public boolean markSupported()
  {
    return d.markSupported();
  }
// i n s t a n c e   v a r i a b l e s

  protected DataInputStream d; // to get at high level readFully methods of DataInputStream
  byte w[]; // work array for buffering input

  } // end class LEDataInputStream
