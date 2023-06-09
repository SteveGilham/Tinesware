/*
 * @(#)QSortAlgorithm.java	1.3   29 Feb 1996 James Gosling
 *
 * Copyright (c) 1994-1996 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL or COMMERCIAL purposes and
 * without fee is hereby granted. 
 * Please refer to the file http://www.javasoft.com/copy_trademarks.html
 * for further important copyright and trademark information and to
 * http://www.javasoft.com/licensing.html for further important
 * licensing information for the Java (tm) Technology.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * THIS SOFTWARE IS NOT DESIGNED OR INTENDED FOR USE OR RESALE AS ON-LINE
 * CONTROL EQUIPMENT IN HAZARDOUS ENVIRONMENTS REQUIRING FAIL-SAFE
 * PERFORMANCE, SUCH AS IN THE OPERATION OF NUCLEAR FACILITIES, AIRCRAFT
 * NAVIGATION OR COMMUNICATION SYSTEMS, AIR TRAFFIC CONTROL, DIRECT LIFE
 * SUPPORT MACHINES, OR WEAPONS SYSTEMS, IN WHICH THE FAILURE OF THE
 * SOFTWARE COULD LEAD DIRECTLY TO DEATH, PERSONAL INJURY, OR SEVERE
 * PHYSICAL OR ENVIRONMENTAL DAMAGE ("HIGH RISK ACTIVITIES").  SUN
 * SPECIFICALLY DISCLAIMS ANY EXPRESS OR IMPLIED WARRANTY OF FITNESS FOR
 * HIGH RISK ACTIVITIES.
 */

package com.ravnaandtines.util;
import java.util.Vector;
/**
 * A quick sort demonstration algorithm
 * <br>converted to string sorting -- Mr. Tines 4-May-98
 * <br>converted to more general sorting -- Mr. Tines 4-July-98
 *
 * @author James Gosling
 * @author Kevin A. Smith
 * @author Mr. Tines <tines@windsong.demon.co.uk>
 * @version 	QSort.java	1.5, 04 July 1998
 */
public class QSort
{
   /** This is a generic version of C.A.R Hoare's Quick Sort 
    * algorithm.  This will handle arrays that are already
    * sorted, and arrays with duplicate keys.<BR>
    *
    * If you think of a one dimensional array as going from
    * the lowest index on the left to the highest index on the right
    * then the parameters to this function are lowest index or
    * left and highest index or right.  The first time you call
    * this function it will be with the parameters 0, a.length - 1.
    *
    * @param a       a String array
    * @param lo0     left boundary of array partition
    * @param hi0     right boundary of array partition
    */
   static void sort(String a[], int lo0, int hi0)
   {
      int lo = lo0;
      int hi = hi0;
      String mid;

      if ( hi0 > lo0)
      {

         /* Arbitrarily establishing partition element as the midpoint of
          * the array.
          */
         mid = a[ ( lo0 + hi0 ) / 2 ].toUpperCase();

         // loop through the array until indices cross
         while( lo <= hi )
         {
            /* find the first element that is greater than or equal to 
             * the partition element starting from the left Index.
             */
            while( ( lo < hi0 ) && ( a[lo].toUpperCase().compareTo(mid) < 0 ) )
               ++lo;

            /* find an element that is smaller than or equal to
             * the partition element starting from the right Index.
             */
            while( ( hi > lo0 ) && ( a[hi].toUpperCase().compareTo(mid) > 0 ) )
               --hi;

            // if the indexes have not crossed, swap
            if( lo <= hi )
            {
               swap(a, lo, hi);
               ++lo;
               --hi;
            }
         }

         /* If the right index has not reached the left side of array
          * must now sort the left partition.
          */
         if( lo0 < hi )
            sort( a, lo0, hi );

         /* If the left index has not reached the right side of array
          * must now sort the right partition.
          */
         if( lo < hi0 )
            sort( a, lo, hi0 );

      }
   }

   private static void swap(String a[], int i, int j)
   {
      String T;
      T = a[i];
      a[i] = a[j];
      a[j] = T;

   }

   public static void sort(String a[])
   {
      sort(a, 0, a.length - 1);
   }

   static void sort(Vector a, int lo0, int hi0)
   {
      int lo = lo0;
      int hi = hi0;
      String mid;

      if ( hi0 > lo0)
      {

         /* Arbitrarily establishing partition element as the midpoint of
          * the array.
          */
         mid = ((String)a.elementAt( ( lo0 + hi0 ) / 2 )).toUpperCase();

         // loop through the array until indices cross
         while( lo <= hi )
         {
            /* find the first element that is greater than or equal to 
             * the partition element starting from the left Index.
             */
            while( ( lo < hi0 ) && ( ((String)a.elementAt(lo)).toUpperCase().compareTo(mid) < 0 ) )
               ++lo;

            /* find an element that is smaller than or equal to
             * the partition element starting from the right Index.
             */
            while( ( hi > lo0 ) && ( ((String)a.elementAt(hi)).toUpperCase().compareTo(mid) > 0 ) )
               --hi;

            // if the indexes have not crossed, swap
            if( lo <= hi )
            {
               swap(a, lo, hi);
               ++lo;
               --hi;
            }
         }

         /* If the right index has not reached the left side of array
          * must now sort the left partition.
          */
         if( lo0 < hi )
            sort( a, lo0, hi );

         /* If the left index has not reached the right side of array
          * must now sort the right partition.
          */
         if( lo < hi0 )
            sort( a, lo, hi0 );

      }
   }

   private static void swap(Vector a, int i, int j)
   {
      Object T;
      T = a.elementAt(i);
      a.setElementAt(a.elementAt(j),i);
      a.setElementAt(T,j);
   }

   public static void sort(Vector a)
   {
      sort(a, 0, a.size() - 1);
   }

   static void sortables(Vector a, int lo0, int hi0)
   {
      int lo = lo0;
      int hi = hi0;
      Sortable mid;

      if ( hi0 > lo0)
      {

         /* Arbitrarily establishing partition element as the midpoint of
          * the array.
          */
         mid = ((Sortable)a.elementAt( ( lo0 + hi0 ) / 2 ));

         // loop through the array until indices cross
         while( lo <= hi )
         {
            /* find the first element that is greater than or equal to 
             * the partition element starting from the left Index.
             */
            while( ( lo < hi0 ) && ( ((Sortable)a.elementAt(lo)).compareTo(mid) < 0 ) )
               ++lo;

            /* find an element that is smaller than or equal to
             * the partition element starting from the right Index.
             */
            while( ( hi > lo0 ) && ( ((Sortable)a.elementAt(hi)).compareTo(mid) > 0 ) )
               --hi;

            // if the indexes have not crossed, swap
            if( lo <= hi )
            {
               swap(a, lo, hi);
               ++lo;
               --hi;
            }
         }

         /* If the right index has not reached the left side of array
          * must now sort the left partition.
          */
         if( lo0 < hi )
            sortables( a, lo0, hi );

         /* If the left index has not reached the right side of array
          * must now sort the right partition.
          */
         if( lo < hi0 )
            sortables( a, lo, hi0 );

      }
   }

   public static void sortables(Vector a)
   {
      sortables(a, 0, a.size() - 1);
   }

}
