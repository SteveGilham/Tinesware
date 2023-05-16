/****************************************************************
 **
 **  $Id: AppletManager.java,v 1.18 1997/12/10 16:46:22 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/AppletManager.java,v $
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

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AudioClip;
import java.awt.Frame;
import java.awt.Image;
import java.awt.TextComponent;
import java.awt.TextField;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;

/**
 * AppletManager - implements AppletContext and works with gwt.AppletWrapper
 * to allow any applet to easily run as an application.  The only thing it
 * can't do is access URL's.  Applet parameters are entered on the command
 * line with name as one word and value as the next, or through an HTML file.
 *
 * @version	1.1
 * @author	DTAI, Incorporated
 *
 * $Id: AppletManager.java,v 1.18 1997/12/10 16:46:22 cvs Exp $
 *
 * $Source: /cvs/classes/dtai/gwt/AppletManager.java,v $
 */

public class AppletManager
    extends Frame
    implements AppletContext, URLStreamHandlerFactory {

    private Object status;
    private Vector applets = new Vector();
    private Hashtable clips = new Hashtable();

    private static AppletManager defaultAppletContext;

	/**
	 * AppletManager
	 */
    public AppletManager() {

        defaultAppletContext = this;

        URL.setURLStreamHandlerFactory( this );
    }

	/**
	 * Convenience constructor for the AppletManager that starts
	 * the given applet in its own frame (AppletFrame).
	 *
	 * @param applet          the wrapper of an applet
	 * @param defaultWidth    default frame width
	 * @param defaultHeight   default frame height
	 * @param args            the command line arguments
	 */
    public AppletManager( Applet applet, int defaultWidth, int defaultHeight, String args[] ) {
        this(applet,-1,-1,defaultWidth,defaultHeight,args);
    }

	/**
	 * Convenience constructor for the AppletManager that starts
	 * the given applet in its own frame (AppletFrame).
	 *
	 * @param applet          the wrapper of an applet
	 * @param defaultX        default frame x
	 * @param defaultY        default frame y
	 * @param defaultWidth    default frame width
	 * @param defaultHeight   default frame height
	 * @param args            the command line arguments
	 */
    public AppletManager( Applet applet, int defaultX, int defaultY,
                          int defaultWidth, int defaultHeight, String args[] ) {

        this();

		AppletWrapper wrapper = new AppletWrapper( applet );
		wrapper.setArgs( args );
		wrapper.setAppletContext(this);

		AppletFrame frame = new AppletFrame( wrapper );

		frame.setDefaultLocation( defaultX, defaultY );
		frame.setDefaultSize( defaultWidth, defaultHeight );

		setStatusBar( frame.getStatusBar() );

		wrapper.init();
		frame.show();
		wrapper.start();
    }

	/**
	 * Return the default applet context.
	 *
	 * @return defaultAppletContext  the default applet context, or null if none
	 */
    public static AppletManager getDefaultAppletContext() {
        return defaultAppletContext;
    }

	/**
	 * Add an applet, via an AppletWrapper, and have it initialize.
	 *
	 * @param appletWrapper            the wrapper of an applet
	 */
    protected void add( AppletWrapper appletWrapper ) {

        applets.addElement( appletWrapper.getApplet() );
    }

	/**
	 * Remove an applet, via an AppletWrapper.
	 *
	 * @param appletWrapper            the wrapper of an applet
	 */
    protected void remove( AppletWrapper appletWrapper ) {

        applets.removeElement( appletWrapper.getApplet() );
        if ( applets.size() == 0 ) {
            System.exit(0);
        }
    }

	/**
	 * setStatusBar             Set the given TextField as the status bar.
	 *
	 * @param status            the TextField
	 */
    public void setStatusBar( TextField status ) {

        this.status = status;
    }

	/**
	 * setStatusBar             Set the given TextField as the status bar.
	 *
	 * @param status            the TextField
	 */
    public void setStatusBar( TextFieldGadget status ) {

        this.status = status;
    }

	/**
	 * returns the object in which "showStatus" is displayed
	 * @return a TextField or TextFieldGadget (must do instanceof to tell)
	 */
    public Object getStatusBar() {
        return status;
    }

/************ AppletContext methods *************/

	/**
     * Gets an audio clip.  (There doesn't seem to be a "Toolkit" for
     * audio clips in my JDK, so this always returns null.  You could
     * implement this differently, returning a dummy AudioClip object
     * for which the class could be defined at the bottom of this file.)
	 *
	 * @param url   URL of the AudioClip to load
	 * @return      the AudioClip object if it exists (in our case,
	 *              this is always null
	 */
    public final AudioClip getAudioClip( URL url ) {
        AudioClip clip = (AudioClip)clips.get(url);
        if (clip == null) {
            /*try {
                clip = new sun.applet.AppletAudioClip(url);
            } catch (NoSuchMethodError nsme) {
                nsme.printStackTrace();
            } catch (NoClassDefFoundError ncdfe) {
                ncdfe.printStackTrace();
            }*/
        }
        return clip;
    }

	/**
     * Gets an image. This usually involves downloading it
     * over the net. However, the environment may decide to
     * cache images. This method takes an array of URLs,
     * each of which will be tried until the image is found.
	 *
	 * @param url   URL of the Image to load
	 * @return      the Image object
	 */
    public final Image getImage( URL url ) {
        return Toolkit.getDefaultToolkit().getImage( url );
    }

	/**
     * Gets an applet by name.
     *
	 * @param name  the name of the applet
     * @return      null if the applet does not exist, and it never
     *              does since we never name the applet.
	 */
    public final Applet getApplet( String name ) { return null; }

	/**
     * Enumerates the applets in this context. Only applets
     * that are accessible will be returned. This list always
     * includes the applet itself.
	 *
	 * @return  the Enumeration -- contains ONLY the applet created with
	 *          this AppletManager
	 */
    public final Enumeration getApplets() { return applets.elements(); }

	/**
     * Shows a new document. This may be ignored by
     * the applet context (and in our case, it is, but we'll show the
     * user, in the status area, that the document was requested and
     * WOULD be loaded if in a browser).
	 *
	 * @param url   URL to load
	 */
    public void showDocument( URL url ) {
        showStatus( "AppletContext request to show URL "+url.toString() );
    }

	/**
     * Show a new document in a target window or frame.
     * This may be ignored by the applet context.  (Again, it is ignored,
     * but we'll show the request information to the user in the status area.)
     *
     * This method accepts the target strings:
     *   _self		show in current frame
     *   _parent	show in parent frame
     *   _top		show in top-most frame
     *   _blank		show in new unnamed top-level window
     *   <other>	show in new top-level window named <other>
	 *
	 * @param url       URL to load
	 * @param target    the target string
	 */
    public void showDocument( URL url, String target ) {
        showStatus("AppletContext request to show URL " +
                   url.toString()+" in target: " + target );
    }

	/**
     * Show a status string in the status area (the Text object at
     * the bottom of the window).
	 *
	 * @param text  the text to display
	 */
    public void showStatus( String text ) {
        Object status = this.status;
        if ( status != null ) {
            if (status instanceof TextField) {
                ((TextField)status).setText( text );
            } else {
                ((TextFieldGadget)status).setText( text );
            }
        } else {
            System.out.println(text);
        }
    }

/************ URLStreamHandlerFactory methods *************/

	/**
     * Creates a new URLStreamHandler instance with the specified
     * protocol.
     *
     * @param protocol  the protocol to use (ftp, http, nntp, etc.).
     *                  THIS PROTOCOL IS IGNORED BY THIS APPLET CONTEXT
     * @return URLStreamHandler
     */
    public URLStreamHandler createURLStreamHandler( String protocol ) {
        return new AmURLStreamHandler();
    }
}

/**
 * A URL stream handler for all protocols, used to return our
 * dummy implementation of URLConnection to open up a local
 * file when called upon.
 *
 * @version	1.1
 * @author	DTAI, Incorporated
 */
class AmURLStreamHandler extends URLStreamHandler {

    /**
     * URLConnection
     * @param url
     * @return URLConnection
     */
    protected final URLConnection openConnection( URL url ) throws IOException {
        if ( url.getProtocol().equals( "file" ) ) {
            return new AmFileURLConnection( url );
        }
        else if ( url.getProtocol().equals( "http" ) ) {
            return new AmHttpURLConnection( url );
        }
        else {
            throw new IOException(
                "Cannot open URL connection for protocol "+url.getProtocol()+
                "\nURL is "+url );
        }
    }
}

/**
 * Our dummy implementation of URLConnection used to open
 * up a local file when called upon with a given URL of ANY protocol type.  This
 * allows the applet to easily use the "getInputStream()" function.
 *
 * @version	1.1
 * @author	DTAI, Incorporated
 */
class AmFileURLConnection extends URLConnection {

    boolean connected = false;
    InputStream instream;

    /**
     * Constructor for the AmURLConnection
     * @param url
     */
    protected AmFileURLConnection( URL url ) {
        super( url );
    }

    /**
     * Open the local file
     */
    public void connect() throws IOException {
        if ( ! connected ) {
            String filename = url.getFile();
            if ( filename.charAt(1) == '|' ) {
                StringBuffer buf = new StringBuffer( filename );
                buf.setCharAt( 1, ':' );
                filename = buf.toString();
            }
            else if ( filename.charAt(2) == '|' ) {
                StringBuffer buf = new StringBuffer( filename );
                buf.setCharAt( 2, ':' );
                filename = buf.toString();
            }
            instream = new FileInputStream( filename );
        }
    }

    /**
     * Return the open stream to the local file (open if necessary).
	 *
	 * @return InputStream		the open stream
     */
    public InputStream getInputStream() throws IOException {
        if ( ! connected ) {
            connect();
        }
        if ( instream == null ) {
            throw new IOException();
        }
        return instream;
    }

    /**
     * Return the content type, if known, or null.
	 *
	 * @return the MIME content type, either "image/jpg" or "image/gif"
     */
    public String getContentType() {
        if ( url.getFile().toLowerCase().endsWith(".gif") ) {
            return "image/gif";
        }
        else if ( url.getFile().toLowerCase().endsWith(".jpg") ) {
            return "image/jpg";
        }
        else {
            return null;
        }
    }
}

/**
 * Our dummy implementation of URLConnection used to open up
 * a TCP/IP HTTP connection when called upon with a given URL
 * of ANY protocol type. This allows the applet to easily use
 * the "getInputStream()" function, and helps handle image
 * loading.
 *
 * @version	1.1
 * @author	DTAI, Incorporated
 */
class AmHttpURLConnection extends URLConnection {

    boolean connected = false;
    InputStream instream;

    /**
     * Constructor for the AmURLConnection
     */
    protected AmHttpURLConnection( URL url ) {
        super( url );
    }

    /**
     * Open the local file
     */
    public void connect() throws IOException {
        if ( ! connected ) {
            int port = url.getPort();
            if ( port < 0 ) {
                port = 80;
            }
            Socket socket = new Socket( url.getHost(), port );
            instream = socket.getInputStream();
            String request = "GET "+url+"\n";
            int len = request.length();
            byte bytes[] = new byte[len];
            request.getBytes( 0, len, bytes, 0 );
            socket.getOutputStream().write( bytes );
        }
    }

    /**
     * Return the open stream to the local file (open if necessary).
	 *
	 * @return instream		the open stream
     */
    public InputStream getInputStream() throws IOException {
        if ( ! connected ) {
            connect();
        }
        if ( instream == null ) {
            throw new IOException();
        }
        return instream;
    }

    /**
     * Return the content type, if known, or null.
	 *
	 * @return		String
     */
    public String getContentType() {
        if ( url.getFile().toLowerCase().endsWith(".gif") ) {
            return "image/gif";
        }
        else if ( url.getFile().toLowerCase().endsWith(".jpg") ) {
            return "image/jpg";
        }
        else {
            return null;
        }
    }
}
