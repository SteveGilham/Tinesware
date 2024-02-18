//package UK.co.demon.windsong.tines.util
//import package UK.co.demon.windsong.tines.?
/** 
*  Interface Paged 
*
*  Supports text to be supplied in parts (perhaps by being
*  generated on the fly)
*
*  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1997
*  All rights reserved.  For full licence details see file Main.java
*
* @author Mr. Tines
* @version 1.0 24-Jul-1997
*
*/

public interface Paged
{
   /**
   * return a page
   * @param i int page number
   */
   public String getPage(int i);
   /**
   * supports the idiom page=0; page<maxPages(); ++page ;
   */
   public int maxPages(); // 
}

/* end of file ClassName.java */

