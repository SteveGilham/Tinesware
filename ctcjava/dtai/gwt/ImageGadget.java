/****************************************************************
 **
 **  $Id: ImageGadget.java,v 1.32 1998/03/10 20:44:57 kadel Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/ImageGadget.java,v $
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.MediaTracker;

/**
 * ImageGadget is a subclass of LabelGadget.  If the image is null, then the label
 * is displayed instead.  The label is initialized to the
 * empty string (""), but you might want to set it to something like "No Image".
 * @version 1.1
 * @author DTAI, Incorporated
 */

public class ImageGadget extends LabelGadget {

    private static boolean defaultWaitForImage = true;

    private Image origImage;
    private Image image;
    private Image disabledImage;
    private Image shadowImage;
	private boolean tiled = false;
	private boolean stretched = false;
    private Color curColor = null;
    private boolean blackToForeground = false;
    private boolean possiblyDisabled = false;
    private boolean disabledImageSet = false;
    private boolean waitForImage = defaultWaitForImage;
    private Color lastBg;

    public static void setDefaultWaitForImage(boolean defaultWaitForImage) {
        ImageGadget.defaultWaitForImage = defaultWaitForImage;
    }

    public static boolean getDefaultWaitForImage() {
        return defaultWaitForImage;
    }

/**
 * constructs a Button with no label.
 */

	public ImageGadget() {
	}

/**
 * Constructs an ImageGadget with the specified image.
 * @param image - the image to display
 */

	public ImageGadget( Image image ) {
	    setImage( image );
	}

/**
 * Gets the label of the button.
 * @see setLabel
 * @return Image
 */

	public Image getImage() {
		return origImage;
	}

/**
 * isPossiblyDisabled
 * @see setWrapLength
 * @return boolean
 */

	public boolean isPossiblyDisabled() {
		return possiblyDisabled;
	}

/**
 * setPossiblyDisabled
 * @param possiblyDisabled - TBD
 */

	public void setPossiblyDisabled( boolean possiblyDisabled ) {
	    synchronized(getTreeLock()) {
    	    if ( this.possiblyDisabled != possiblyDisabled ) {
        		this.possiblyDisabled = possiblyDisabled;
        		invalidate();
        	}
        }
	}

/**
 * getWaitForImage
 * @see setWrapLength
 * @return boolean
 */

	public boolean getWaitForImage() {
		return waitForImage;
	}

/**
 * setWaitForImage
 * @param waitForImage - TBD
 */

	public void setWaitForImage( boolean waitForImage ) {
	    if ( this.waitForImage != waitForImage ) {
    		this.waitForImage = waitForImage;
    	}
	}

    /**
	 * Set this to true if you want the black pixels to be converted to the
	 * current foreground color.
	 * @param blackToForeground - TBD
	 */

	public void setBlackToForeground( boolean blackToForeground ) {
	    this.blackToForeground = blackToForeground;
	}

	/**
	 * getBlackToForeground
	 * @return boolean
     */
	public boolean getBlackToForeground() {
	    return blackToForeground;
	}

	/**
	 * setTiled
	 * @param tiled - TBD
	 */
    public void setTiled(boolean tiled) {
	    if ( tiled != this.tiled ) {
    	    this.tiled = tiled;
    	    repaint();
    	}
	}

	/**
	 * isTiled
	 * @return boolean
	 */
	public boolean isTiled() {
	    return tiled;
	}

	/**
	 * setStretched
	 * @param stretched - TBD
	 */
	public void setStretched(boolean stretched) {
	    if ( stretched != this.stretched ) {
    	    this.stretched = stretched;
    	    repaint();
    	}
	}

	/**
	 * isStretched
	 * @return boolean
	 */
	public boolean isStretched() {
	    return stretched;
	}

/**
 * sets the button with the specified image.
 * @param image - the image to set the button with
 * @see getImage
 */

	public void setImage( Image image ) {
		synchronized(getTreeLock()) {
    	    if ( origImage != image ) {
        		origImage = image;
        		this.image = image;
        		curColor = null;
        		if ( ! tiled && ! stretched ) {
            		invalidate();
        		}
        		repaint();
            }
    	}
	}

	/**
	 * setDisabledImage
	 * @param disabledImage - TBD
	 */
	public void setDisabledImage( Image disabledImage ) {
		synchronized(getTreeLock()) {
    	    if ( this.disabledImage != disabledImage ) {
        		this.disabledImage = disabledImage;
        	    if ( disabledImage == null ) {
        	        disabledImageSet = false;
        	    }
        	    else {
            	    disabledImageSet = true;
            	}
        		if ( ! tiled && ! stretched ) {
            		invalidate();
        		}
        		if ( ! isFunctioning() ) {
            		repaint();
            	}
        	}
        }
	}

    /**
     * validateImage
     * @param g - TBD
     * @param fg - TBD
     * @param bg - TBD
     */
    private void validateImage(GadgetGraphics g, Color fg, Color bg) {
        if ( ( fg != curColor ) && ( ! fg.equals( curColor ) ) ) {
            curColor = fg;
            disabledImage = null;
            shadowImage = null;
            image =
                createImage( new FilteredImageSource(
                    origImage.getSource(),
                    new DisplayImageFilter( fg,
                        getFinalBackground(g), true ) ) );
        }
        else if ( lastBg != bg ) {
            disabledImage = null;
            shadowImage = null;
            lastBg = bg;
        }
    }

    /**
     * paints the foreground
     * @param g - TBD
     * @param x - TBD
     * @param y - TBD
     * @param width - TBD
     * @param height - TBD
     */
    protected void paintForeground(
            GadgetGraphics g, int x, int y, int width, int height) {
		Color fg = getForeground(g);
		Color bg = getFinalBackground(g);
        Color blackTo = null;
        if ( blackToForeground ) {
            blackTo = fg;
            validateImage(g,fg,bg);
        }

        int imageWidth = width;
        int imageHeight = height;

        if ( possiblyDisabled && ! disabledImageSet && blackToForeground ) {
            imageWidth -= 1;
            imageHeight -= 1;
        }

	    if ( image != null ) {
            if ( ! stretched || tiled ) {
                imageWidth = image.getWidth(this);
                imageHeight = image.getHeight(this);
            }
	        prepareImage( image, imageWidth, imageHeight, this );
	        int status = checkImage( image, imageWidth, imageHeight, this );
	        if ( ( status & ImageObserver.ERROR ) != 0 ) {
    	        image = null;
    	        setLabel( "Error loading image" );
    	        return;
    	    } else if ( ( status & ImageObserver.ALLBITS ) == 0 ) {
                if (waitForImage) {
                    synchronized(getTreeLock()) {
                        GadgetShell shell = getShell();
                        if (shell != null) {
                            MediaTracker mt = new MediaTracker( shell );
                            mt.addImage( image, 0 );
                            try {
                                dtai.util.Debug.println("waiting for image...");
                                mt.waitForAll();
                                dtai.util.Debug.println("...got image");
                            } catch (InterruptedException ie) {
                            }
                        }
                    }
                }
    	    }
	    }

	    if ( image != null ) {
    		if (tiled) {
                imageWidth = image.getWidth(this);
                imageHeight = image.getHeight(this);
                if (imageHeight > 0 && imageWidth > 0) {
        			for ( int i = 0; i < height; i += imageHeight ) {
        				for ( int j = 0; j < width; j += imageWidth ) {
        					g.drawImage( image, j+x, i+y, this );
        				}
        			}
        		}
    		} else {
    			g.drawImage( image, x, y, imageWidth, imageHeight, this );
    		}
		}
		else {
		    resetForegroundSize( g );
		    super.paintForeground( g, x, y, width, height );
		}
	}

    /**
     * paintDisabledForeground
     * @param g - TBD
     * @param x - TBD
     * @param y - TBD
     * @param width - TBD
     * @param height - TBD
     */
    protected void paintDisabledForeground( GadgetGraphics g,
            int x, int y, int width, int height ) {
        int imageWidth = width;
        int imageHeight = height;
        if ( possiblyDisabled && ! disabledImageSet && blackToForeground ) {
            imageWidth -= 1;
            imageHeight -= 1;
        }

		Color fg = getForeground(g);
		Color bg = getFinalBackground(g);

        Color blackTo = null;
        if ( blackToForeground ) {
            blackTo = fg;
            validateImage(g,fg,bg);
        }

        Color disabledForeground = blackTo;
        boolean paintSolid = false;
        Image imageToFilter = image;
        if ( possiblyDisabled && ! disabledImageSet && blackToForeground ) {
            disabledForeground = getDisabledTop(fg,bg);
            paintSolid = true;
            imageToFilter = origImage;
        }

	    if ( disabledImage != null ) {
            if ( ! stretched || tiled ) {
                imageWidth = disabledImage.getWidth(this);
                imageHeight = disabledImage.getHeight(this);
            }
	        prepareImage( disabledImage, imageWidth, imageHeight, this );
	        int status = checkImage( disabledImage, imageWidth, imageHeight, this );
	        if ( ( status & ImageObserver.ERROR ) != 0 ) {
    	        disabledImage = null;
    	        return;
    	    } else if ( ( status & ImageObserver.ALLBITS ) == 0 ) {
                if (waitForImage) {
                    synchronized(getTreeLock()) {
                        GadgetShell shell = getShell();
                        if (shell != null) {
                            MediaTracker mt = new MediaTracker( shell );
                            mt.addImage( disabledImage, 0 );
                            try {
                                dtai.util.Debug.println("waiting for disabled image...");
                                mt.waitForAll();
                                dtai.util.Debug.println("...got disabled image");
                            } catch (InterruptedException ie) {
                            }
                        }
                    }
                }
    	    }
	    }

	    if ( ( disabledImage == null ) &&
	         ( image != null ) ) {
            disabledImage =
                createImage( new FilteredImageSource(
                    imageToFilter.getSource(),
                    new DisplayImageFilter( disabledForeground,
                        getFinalBackground(g), paintSolid ) ) );
        }

        if ( possiblyDisabled && ! disabledImageSet && blackToForeground ) {
    	    if ( shadowImage != null ) {
                if ( ! stretched || tiled ) {
                    imageWidth = shadowImage.getWidth(this);
                    imageHeight = shadowImage.getHeight(this);
                }
    	        prepareImage( shadowImage, imageWidth, imageHeight, this );
    	        int status = checkImage( shadowImage, imageWidth, imageHeight, this );
    	        if ( ( status & ImageObserver.ERROR ) != 0 ) {
        	        shadowImage = null;
        	        return;
        	    } else if ( ( status & ImageObserver.ALLBITS ) == 0 ) {
                    if (waitForImage) {
                        synchronized(getTreeLock()) {
                            GadgetShell shell = getShell();
                            if (shell != null) {
                                MediaTracker mt = new MediaTracker( shell );
                                mt.addImage( shadowImage, 0 );
                                try {
                                    dtai.util.Debug.println("waiting for shadow image...");
                                    mt.waitForAll();
                                    dtai.util.Debug.println("...got shadow image");
                                } catch (InterruptedException ie) {
                                }
                            }
                        }
                    }
        	    }
    	    }

    	    if ( ( shadowImage == null ) &&
    	         ( image != null ) ) {
                shadowImage =
                    createImage( new FilteredImageSource(
                        origImage.getSource(),
                        new DisplayImageFilter( getDisabledBottom(fg,bg),
                            getFinalBackground(g), true ) ) );
            }
        }
        else {
            shadowImage = null;
        }

	    if ( disabledImage != null ) {
    	    if ( shadowImage != null ) {
        		if (tiled) {
                    imageWidth = shadowImage.getWidth(this);
                    imageHeight = shadowImage.getHeight(this);
                    if (imageHeight > 0 && imageWidth > 0) {
            			for ( int i = 0; i < height; i += imageHeight ) {
            				for ( int j = 0; j < width; j += imageWidth ) {
                    			g.drawImage( shadowImage, i+x+1, j+y+1, this );
            				}
            			}
            		}
        		} else {
        			g.drawImage( shadowImage, x+1, y+1, imageWidth, imageHeight, this );
        		}
    			g.drawImage( shadowImage, x+1, y+1, imageWidth, imageHeight, this );
    		}
    		if (tiled) {
                imageWidth = disabledImage.getWidth(this);
                imageHeight = disabledImage.getHeight(this);
                if (imageHeight > 0 && imageWidth > 0) {
        			for ( int i = 0; i < height; i += imageHeight ) {
        				for ( int j = 0; j < width; j += imageWidth ) {
        					g.drawImage( disabledImage, j+x, i+y, this );
        				}
        			}
        		}
    		} else {
    			g.drawImage( disabledImage, x, y, imageWidth, imageHeight, this );
    		}
		}
		else {
		    super.paintDisabledForeground( g, x, y, width, height );

		}
	}

	/**
	 * resets the size of the foreground
	 * @param g - TBD
	 */
	protected void resetForegroundSize( GadgetGraphics g ) {
		if ( stretched || tiled ) {
    		fgWidth = width;
    		fgHeight = height;
		}
		else {
    	    if ( image != null ) {
        		int newWidth = image.getWidth( this );
        		int newHeight = image.getHeight( this );

        		if ( newWidth != -1 ) {
        		    fgWidth = newWidth;
        		}
        		if ( newHeight != -1 ) {
        		    fgHeight = newHeight;
        		}
                if ( possiblyDisabled && ! disabledImageSet && blackToForeground ) {
        		    fgHeight++;
        		    fgWidth++;
        		}
        	}
        	else {
        	    super.resetForegroundSize( g );
        	}
        }
	}
    /**
     * Repaints the component when the image has changed.
     * @return true if image has changed; false otherwise.
     */
    public boolean imageUpdate(Image img, int flags, int x, int y, int w, int h) {
		if ( ( ( flags & (WIDTH|HEIGHT|FRAMEBITS|ALLBITS) ) != 0 ) &&
    		 ( img != disabledForeground ) ) {
    		if ( !stretched && !tiled ) {
    		    fgWidth = w;
    		    fgHeight = h;
    		}
		}
		return ( super.imageUpdate( img, flags, x, y, w, h ) );
	}
}
