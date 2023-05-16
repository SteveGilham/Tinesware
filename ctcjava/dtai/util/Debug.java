/****************************************************************
 **
 **  $Id: Debug.java,v 1.5 1997/09/30 03:15:56 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/util/Debug.java,v $
 **
 ****************************************************************
 **
 **  Gadget Windowing Toolkit (GWT) Java Class Library
 **  Copyright (C) 1997  DTAI, Incorporated (http://www.dtai.com)
 **
 **  This library is free software; you can redistribute it and/or
 **  modify it under the terms of the GNU Library General Public
 **  License as published by the Free Software Foundation; either
 **  version 2 of the License, or (at your option) any later version.
 **
 **  This library is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 **  Library General Public License for more details.
 **
 **  You should have received a copy of the GNU Library General Public
 **  License along with this library (file "COPYING.LIB"); if not,
 **  write to the Free Software Foundation, Inc.,
 **  59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 **
 ****************************************************************/

package dtai.util;

/**
 * Debug class
 * @version 1.1
 * @author DTAI, Incorporated
 */
public class Debug {

	private static int level = 0;

    /**
     * turns debug on with default level of 100
     */
    public static void on() {
		on(1);
    }

    /**
     * turns debug level on and sets with specified debug level
     * @param level  the specified debug level
     */
    public static void on(int level) {
        Debug.level = level;
        println( "Debug level "+level );
    }

    /**
     * turns debug off
     */
    public static void off() {
        println( "Debug off" );
        level = 0;
    }

    /**
     * quietOn
     */
    public static void quietOn() {
		quietOn(100);
    }

    /**
     * quietOn
     * @param level  the level
     */
    public static void quietOn(int level) {
		Debug.level = level;
    }

    /**
     * quietOff
     */
    public static void quietOff() {
		level = 0;
    }

    /**
     * isOn
     * @return boolean
     */
    public static boolean isOn() {
        return level > 0;
    }

    /**
     * gets the debug level
     * @return   the level
     */
    public static int getLevel() {
        return level;
    }

    /**
     * prints a string
     * @param string   the string to be printed
     */
    public static void print( String string ) {
		print(1,string);
    }

    /**
     * prints a string if the debug level is greater than or equal to the level
     * @param level  the level
     * @param string   the string to pe printed
     */
    public static void print( int level, String string ) {
        if ( Debug.level >= level ) {
            System.err.print( string );
        }
    }

    /**
     * println
     */
    public static void println() {
		println(1);
    }

    /**
     * println
     * @param level    the level
     */
    public static void println(int level) {
        if ( Debug.level >= level ) {
            System.err.println();
        }
    }

    /**
     * println
     * @param string   the string to be printed
     */
    public static void println( String string ) {
		println(1,string);
    }

    /**
     * println
     * @param level  the level
     * @param string  the string to be printed
     */
    public static void println( int level, String string ) {
        if ( Debug.level >= level ) {
            System.err.println( string );
        }
    }
}
