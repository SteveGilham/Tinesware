/****************************************************************
 **
 **  $Id: ImagePanelGadget.java,v 1.8 1997/08/06 23:27:06 cvs Exp $
 **
 **  $Source: /cvs/classes/dtai/gwt/ImagePanelGadget.java,v $
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
import java.awt.Image;

/**
 * ImagePanelGadget
 * @version 1.1
 * @author DTAI, Incorporated
 */
public class ImagePanelGadget extends PanelGadget {

    private PanelGadget mainPanel;
    private ImageGadget imageGadget;

    public ImagePanelGadget() {
        this(null);
    }

    /**
     * ImagePanelGadget
     * @param image - TBD
     */
    public ImagePanelGadget(Image image) {
        super(new GadgetCardLayout());

        imageGadget = new ImageGadget();
        imageGadget.setParent(this);
        setTiled(true);
        setImage(image);
    }

	/**
	 * getFinalBackground
	 * @param g - TBD
	 * @return Color
	 */
	protected Color getFinalBackground(GadgetGraphics g) {
	    return getFinalNormalBackground(g);
    }

	/**
	 * getFinalNormalBackground
	 * @param g - TBD
	 * @return Color
	 */
	protected Color getFinalNormalBackground(GadgetGraphics g) {
	    if (getImage() == null || normalBackground == null) {
        	return super.getFinalBackground(g);
	    } else {
	        return normalBackground;
	    }
    }

	/**
	 * getNormalBackground
	 * @param g - TBD
	 * @return Color
	 */
	protected final Color getNormalBackground(GadgetGraphics g) {
	    if (getImage() == null) {
	        return super.getNormalBackground(g);
	    } else {
	        return Gadget.transparent;
	    }
    }

    /**
     * setImage
     * @param image - TBD
     */
    public void setImage(Image image) {
        imageGadget.setImage(image);
    }

	/**
	 * getImage
	 * @return Image
	 */
	public Image getImage() {
		return imageGadget.getImage();
	}

	/**
	 * getImageGadget
	 * @return ImageGadget
	 */
	public ImageGadget getImageGadget() {
	    return imageGadget;
	}

	/**
	 * setBlackToForeground
	 * @param blackToForeground - TBD
	 */
	public void setBlackToForeground( boolean blackToForeground ) {
        imageGadget.setBlackToForeground(blackToForeground);
	}

	/**
	 * getBlackToForeground
	 * @return boolean
	 */
	public boolean getBlackToForeground() {
		return imageGadget.getBlackToForeground();
	}

    /**
	 * setTiled
	 * @param tiled - TBD
	 */
    public void setTiled(boolean tiled) {
        imageGadget.setTiled(tiled);
    }

	/**
	 * isTiled
	 * @return boolean
	 */
	public boolean isTiled() {
		return imageGadget.isTiled();
	}

    /**
     * setStretched
     * @param streched - TBD
     */
    public void setStretched(boolean stretched) {
        imageGadget.setStretched(stretched);
    }

	/**
	 * isStretched
	 * @return boolean
	 */
	public boolean isStretched() {
		return imageGadget.isStretched();
	}

	/**
	 * setShell
	 * @param shell - TBD
	 */
	protected void setShell( GadgetShell shell ) {
	    super.setShell(shell);
	    imageGadget.setShell(shell);
	}

	/**
	 * setBounds
	 * @param x - TBD
	 * @param y - TBD
	 * @param width - TBD
	 * @param height - TBD
	 * @param notifyParent - TBD
	 */
	public void setBounds(int x, int y, int width, int height, boolean notifyParent) {
	    super.setBounds(x, y, width, height, notifyParent);
	    imageGadget.setBounds(0,0,width,height,false);
	}

	/**
	 * paint
	 * @param g - TBD
	 */
	public void paint(GadgetGraphics g) {
	    imageGadget.setForeground(getForeground(g));
	    imageGadget.setBackground(getBackground(g));
	    imageGadget.setFont(getFont(g));
	    imageGadget.paint(g);
	}
}
