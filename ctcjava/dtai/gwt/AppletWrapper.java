/****************************************************************
 **
 **  $Id: AppletWrapper.java,v 1.22 1998/03/10 21:58:45 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/AppletWrapper.java,v $
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

package dtai.gwt;

import dtai.util.ShowUser;
import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StreamTokenizer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * AppletWrapper - implements AppletStub and works with gwt.AppletManager
 * to allow any applet to easily run as an application.  The only thing
 * it can't do is access URL's.  Applet parameters are entered on the
 * command line with name as one word and value as the next, or through
 * an HTML file.
 *
 * @version	1.1
 * @author	DTAI, Incorporated
 *
 * $Id: AppletWrapper.java,v 1.22 1998/03/10 21:58:45 kadel Exp $
 *
 * $Source: /cvs/classes/dtai/gwt/AppletWrapper.java,v $
 */
public class AppletWrapper implements AppletStub {

    private Applet applet;
    private AppletFrame frame;
    private AppletContext appletContext;

    private String args[];
    private Hashtable params;

    private int initial_x = -1;
    private int initial_y = -1;
    private int initial_width = 640;
    private int initial_height = 480;
    private String codebase = ".";

	/**
	 * Constructor for the main class, given an existing applet object.
	 *
	 * @param applet            the Applet embedded in this AppletContext
	 */
    public AppletWrapper( Applet applet ) {
        this.applet = applet;
    }

	/**
	 * Returns the applet.
	 *
	 * @return Applet
	 */
    public Applet getApplet() {
        return applet;
    }

	/**
	 * Set the AppletFrame.
	 *
	 * @param frame             the frame to be used
	 */
    public void setFrame( AppletFrame frame ) {
        this.frame = frame;
    }

	/**
	 * Set the command-line arguments.
	 *
	 * @param args[]              the command line arguments.  Contains possibly
	 *                            height and width, and any applet parameters
	 */
    public void setArgs( String args[] ) {
        this.args = args;
    }

	/**
	 * Set the default size, which might be overridden by
	 * command line arguments or an HTML file.
	 *
	 * @param default_width     the default width of the window
	 * @param default_height    the default height of the window
	 */
    protected void setDefaultSize( int default_width, int default_height ) {
        initial_width = default_width;
        initial_height = default_height;
    }

	/**
	 * Set the default location, which might be overridden by command line arguments
	 * or an HTML file.
	 *
	 * @param default_x         the default x of the window
	 * @param default_y         the default y of the window
	 */
    protected void setDefaultLocation( int default_x, int default_y ) {
        initial_x = default_x;
        initial_y = default_y;
    }

	/**
	 * Returns the initial x.
	 *
     * @return int
     */
    public int getInitialX() {
        return initial_x;
    }

	/**
	 * Returns the initial y.
	 *
	 * @return  int
	 */
    public int getInitialY() {
        return initial_y;
    }

	/**
	 * Returns the initial width.
	 * If initial_width was < 0, it is first set to 640,
	 * then returned.
	 *
     * @return int
     */
    public int getInitialWidth() {
        if ( initial_width < 0 ) {
            initial_width = 640;
        }
        return initial_width;
    }

	/**
	 * Returns the initial height.
	 * If initial_height was < 0, it is first set to 480,
	 *
     * @return int
     *
	 */
    public int getInitialHeight() {
        if ( initial_height < 0 ) {
            initial_height = 480;
        }
        return initial_height;
    }

	/**
	 * set the applet context.
	 *
	 * @param appletContext		value for this appletContext
	 */
    public void setAppletContext( AppletContext appletContext ) {
        this.appletContext = appletContext;
        if ( appletContext instanceof AppletManager ) {
            ((AppletManager)appletContext).add(this);
        }
    }

    /**
     * init
     */
    public void init() {

        if ( params != null ) {
            return;
        }

        applet.setStub(this);

        params = new Hashtable();

        parseArgs( args );

        applet.resize(initial_width,initial_height);

        applet.init();
    }

	/**
	 * Called by the applet context to start the applet.
	 */
    public void start() {
        applet.start();
    }

	/**
	 * Called by the applet context to stop the applet.
	 */
    public void stop() {
        applet.stop();
    }

	/**
	 * Called by the applet context to destroy the applet.
	 */
    public void destroy() {
        try {
            applet.destroy();
            if ( appletContext instanceof AppletManager ) {
                ((AppletManager)appletContext).remove(this);
            }
        } catch (RuntimeException e) {
            try {
                if (e.getClass() != Class.forName("java.lang.RuntimeException")) {
                    System.err.println("Applet destroy failed due to:");
                    e.printStackTrace();
                }
            } catch (Throwable t) {
            }
        }
    }

	/**
	 * Parse the command line arguments.  Get the initial width and height of
	 * the window if specified (-width [value] -height [value]), and the
	 * applet parameters (name value pairs).
	 *
	 * @param args[]              the command line arguments.  Contains possibly
	 *                            height and width, and any applet parameters
	 */
    public void parseArgs( String args[] ) {
        for ( int idx = 0; idx < args.length; idx++ ) {
            try {
                if ( args[idx].equals( "-x" ) ) {
                    initial_x = Integer.parseInt( args[++idx] );
                }
                else if ( args[idx].equals( "-y" ) ) {
                    initial_y = Integer.parseInt( args[++idx] );
                }
                else if ( args[idx].equals( "-width" ) ) {
                    initial_width = Integer.parseInt( args[++idx] );
                }
                else if ( args[idx].equals( "-height" ) ) {
                    initial_height = Integer.parseInt( args[++idx] );
                }
                else if ( args[idx].equals( "-codebase" ) ) {
                    codebase = args[++idx];
                }
                else if ( args[idx].equals( "-debug" ) ) {
                    dtai.util.Debug.on();
                    if (idx+1 < args.length) {
                        try {
                            int level = Integer.parseInt(args[idx+1]);
                            if (level > 0 || args[idx+1].trim() == "0") {
                                idx++;
                                dtai.util.Debug.on(level);
                            }
                        } catch (NumberFormatException nfe) {
                        }
                    }
                }
                else if ( args[idx].equals( "-f" ) ) {
                    parseArgsFromHTML( args[++idx] );
                }
                else {
                    if (idx < args.length-1) {
                        params.put( args[idx], args[++idx] );
                    }
                }
            }
            catch ( NumberFormatException nfe ) {
                System.err.println("Warning: command line argument "+args[idx]+
                                   " is not a valid number." );
            }
        }
    }

    private final static int CODEBASE = 1;
    private final static int WIDTH = 2;
    private final static int HEIGHT = 3;
    private final static int X = 4;
    private final static int Y = 5;
    private final static int NAME = 6;
    private final static int VALUE = 7;

	/**
	 * Parse the command line arguments from an HTML file, as would be done by
	 * the appletviewer.  This looks for width and height tags as well as applet
	 * parameters.  It parses only the first APPLET tag.  It also assumes that the
	 * first APPLET tag is for the applet it is using, rather than try to load the
	 * class specified in the APPLET tag.
	 *
	 * @param filename          the HTML file
	 */
    public void parseArgsFromHTML( String filename ) {
        try {
            InputStream file = new FileInputStream(filename);
            StreamTokenizer toks = new StreamTokenizer(file);
            toks.lowerCaseMode(true);

            boolean inApplet = false;
            boolean inAppletTag = false;
            boolean inParamTag = false;
            boolean inTag = false;
            boolean justGotSlash = false;
            int waitfor = 0;
            String name = null;

            while (toks.nextToken() != StreamTokenizer.TT_EOF) {
                if ( ( waitfor > 0 ) &&
                     ( toks.ttype == '=' ) ) {
                    continue;
                }
                if ( ! inTag ) {
                    if ( toks.ttype == '<' ) {
                        inTag = true;
                        name = null;
                    }
                }
                else {
                    if ( toks.ttype == '>' ) {
                        inTag = false;
                        inAppletTag = false;
                        inParamTag = false;
                    }
                    else {
                        if ( ! inApplet ) {
                            if ( ( toks.ttype == StreamTokenizer.TT_WORD ) &&
                                 ( ! justGotSlash ) &&
                                 ( toks.sval.equals("applet") ) ) {
                                inApplet = true;
                                inAppletTag = true;
                            }
                        }
                        else {
                            if ( ( toks.ttype == StreamTokenizer.TT_WORD ) ||
                                 ( toks.ttype == StreamTokenizer.TT_NUMBER ) ||
                                 ( toks.ttype == '\"' ) ) {
                                if ( ( justGotSlash ) &&
                                     ( toks.sval.equals("applet") ) ) {
                                    break;
                                }
                                else if ( inAppletTag ) {
                                    if ( waitfor == CODEBASE ) {
                                        codebase = toks.sval;
                                    }
                                    else if ( waitfor == WIDTH ) {
                                        if ( toks.ttype == StreamTokenizer.TT_NUMBER ) {
                                            initial_width = (int)toks.nval;
                                        }
                                        else if ( toks.ttype == '\"' ) {
                                            try {
                                                initial_width = Integer.parseInt(toks.sval);
                                            }
                                            catch ( NumberFormatException nfe ) {
                                            }
                                        }
                                    }
                                    else if ( waitfor == HEIGHT ) {
                                        if ( toks.ttype == StreamTokenizer.TT_NUMBER ) {
                                            initial_height = (int)toks.nval;
                                        }
                                        else if ( toks.ttype == '\"' ) {
                                            try {
                                                initial_height = Integer.parseInt(toks.sval);
                                            }
                                            catch ( NumberFormatException nfe ) {
                                            }
                                        }
                                    }
                                    else if ( waitfor == X ) {
                                        if ( toks.ttype == StreamTokenizer.TT_NUMBER ) {
                                            initial_x = (int)toks.nval;
                                        }
                                        else if ( toks.ttype == '\"' ) {
                                            try {
                                                initial_x = Integer.parseInt(toks.sval);
                                            }
                                            catch ( NumberFormatException nfe ) {
                                            }
                                        }
                                    }
                                    else if ( waitfor == Y ) {
                                        if ( toks.ttype == StreamTokenizer.TT_NUMBER ) {
                                            initial_y = (int)toks.nval;
                                        }
                                        else if ( toks.ttype == '\"' ) {
                                            try {
                                                initial_y = Integer.parseInt(toks.sval);
                                            }
                                            catch ( NumberFormatException nfe ) {
                                            }
                                        }
                                    }
                                    else if ( toks.sval.equals("codebase") ) {
                                        waitfor = CODEBASE;
                                        continue;
                                    }
                                    else if ( toks.sval.equals("width") ) {
                                        waitfor = WIDTH;
                                        continue;
                                    }
                                    else if ( toks.sval.equals("height") ) {
                                        waitfor = HEIGHT;
                                        continue;
                                    }
                                    else if ( toks.sval.equals("x") ) {
                                        waitfor = X;
                                        continue;
                                    }
                                    else if ( toks.sval.equals("y") ) {
                                        waitfor = Y;
                                        continue;
                                    }
                                }
                                else if ( inParamTag ) {
                                    if ( waitfor == NAME ) {
                                        name = toks.sval;
                                    }
                                    else if ( waitfor == VALUE ) {
                                        if ( name != null ) {
                                            if ( toks.ttype == StreamTokenizer.TT_NUMBER ) {
                                                params.put( name, Integer.toString( (int)toks.nval ) );
                                            }
                                            else {
                                                params.put( name, toks.sval );
                                            }
                                        }
                                    }
                                    else if ( toks.sval.equals("name") ) {
                                        waitfor = NAME;
                                        continue;
                                    }
                                    else if ( toks.sval.equals("value") ) {
                                        waitfor = VALUE;
                                        continue;
                                    }
                                }
                                else if ( toks.sval != null &&
                                          toks.sval.equals("param") ) {
                                    inParamTag = true;
                                }
                            }
                        }
                    }
                }
                if ( toks.ttype == '/' ) {
                    justGotSlash = true;
                }
                else {
                    justGotSlash = false;
                }
                if ( toks.ttype != '=' ) {
                    waitfor = 0;
                }
            }
        }
        catch ( IOException ioe ) {
        }
    }

/************ AppletStub methods *************/

    /**
     * Returns true if the applet is active.
     *
     * @return boolean result  always true
     */
    public boolean isActive() { return true; }

    /**
     * Gets the document URL.
     *
	 * @return URL     a "file:" URL for the current directory
     */
    public URL getDocumentBase() {
        URL url = null;
        try {
            File dummy = new File( "dummy.html" );
            String path = dummy.getAbsolutePath();
            if ( ! File.separator.equals( "/" ) ) {
                StringBuffer buffer = new StringBuffer();
                if ( path.charAt(0) != File.separator.charAt(0) ) {
                    buffer.append( "/" );
                }
                StringTokenizer st = new StringTokenizer( path, File.separator );
                while ( st.hasMoreTokens() ) {
                    buffer.append( st.nextToken() + "/" );
                }
                if ( File.separator.equals( "\\" ) &&
                     ( buffer.charAt(2) == ':' ) ) {
                    buffer.setCharAt( 2, '|' );
                }
                else {
                }
                path = buffer.toString();
                path = path.substring( 0, path.length()-1 );
            }
            url = new URL( "file", "", -1, path );
        }
        catch ( MalformedURLException mue ) {
			dtai.util.ShowUser.error(mue);
        }
        return url;
    }

    /**
     * Gets the codebase URL.
     *
	 * @return URL     in this case, the same value as getDocumentBase()
     */
    public final URL getCodeBase() {
        try {
            if ( codebase.charAt(codebase.length()-1) == '/' ) {
                return new URL( getDocumentBase(), codebase );
            }
            else {
                return new URL( getDocumentBase(), codebase+'/' );
            }
        }
        catch ( MalformedURLException mue ) {
            return null;
        }
    }

    /**
     * Gets a parameter of the applet.
     *
	 * @param name  the name of the parameter
	 * @return String      the value, or null if not defined
	 */
    public final String getParameter( String name ) {
        return (String)params.get( name );
    }

	/**
	 * Gets a handler to the applet's context.
	 *
	 * @return AppletContext    this object
	 */
    public final AppletContext getAppletContext() { return appletContext; }

	/**
	 * Called when the applet wants to be resized.  This causes the
	 * Frame (window) to be resized to accomodate the new Applet size.
	 *
	 * @param width     the new width of the applet
	 * @param height    the new height of the applet
	 */
    public void appletResize( int width, int height ) {

        if ( frame != null ) {
            frame.appletResize(width,height);
        }
    }

}
