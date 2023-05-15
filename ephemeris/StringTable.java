//import package UK.co.demon.windsong.tines.?
/** 
*  Class StringTable
*
*  Central repository for all standard strings, easing local language
*  revisions. 
*
*  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1997
*  All rights reserved.  For full licence details see file Main.java
*
* @author Mr. Tines
* @version 1.0 31-Jul-1997
*
*/

public class StringTable
{
	// BasicFrame
	public static final String FILE = "File";
	public static final String HELP = "Help";
	public static final String EXIT = "Exit";
	public static final String ABOUT = "About";
	public static final String CLOSE = "Close";
	public static final String LICTEXT = "License text";


	// replace a %S with another string
	public static String substitute(String whole, String part)
	{
		int i = whole.indexOf("%S");
		int l = whole.length();
		if(i == -1) return whole;
		else if (i == 0) return part+whole.substring(2,l);
		else return whole.substring(0,i)+part+whole.substring(i+2,l);
	}
}

/* end of file StringTable.java */

