
package dtai.gwt;

import java.awt.Color;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.ImageConsumer;

public class Sprite extends Image {

    private static final int white = Color.white.getRGB();
    private int pixels[];
    private int imageWidth;
    private int offsetY, offsetX;
    private int width, height;
    private Color color;

    private static final SpriteProducer spriteProducer = new SpriteProducer();

	public Sprite(int pixels[], int imageWidth) {
	    this(pixels, imageWidth, pixels.length/2, pixels.length/2, 0, 0, Color.black);
	}

	public Sprite(int pixels[], int imageWidth, int width, int height, int offsetX, int offsetY, Color color) {
	    this.pixels = pixels;
	    this.imageWidth =  imageWidth;
	    this.width = width;
	    this.height = height;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.color = color;
	}

    public final int[] getPixels() {
		int newArray[] = new int[width * height];
		int xx = offsetY * imageWidth + offsetX;
		int k = 0;
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				newArray[k] = pixels[xx + i];
				k++;
			}
            xx += imageWidth;
		}
		return newArray;
    }

    public final boolean drawImage(GadgetGraphics g, int x, int y) {
        return drawPixels(g, x, y, width, height, color);
    }

    public final boolean drawImage(GadgetGraphics g, int x, int y,
                             int width, int height) {
        return drawPixels(g, x, y, width, height, color);
    }

    public final boolean drawImage(GadgetGraphics g,  int x, int y,
                             Color bgcolor ) {
        return drawPixels(g, x, y, width, height, bgcolor);
    }

    public final boolean drawImage(GadgetGraphics g, int x, int y,
                             int width, int height,
                             Color bgcolor) {
        return drawPixels(g, x, y, width, height, bgcolor);
    }

	private boolean drawPixels(GadgetGraphics g, int x, int y, int width, int height,
                       Color bgcolor) {
		g.setColor(bgcolor);

		int xx = offsetY * imageWidth + offsetX;

		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
                if (pixels[xx + i] == white) {
					int ii = i;
					while (i + 1 < width && pixels[xx + i + 1] == white)
						i++;
                    g.drawLine(ii+x, j+y, i+x, j+y);
                }
            }
            xx += imageWidth;
        }
	    return true;
    }

    public int getWidth(ImageObserver o) {
	    return width;
	}

	public int getHeight(ImageObserver o) {
	    return height;
	}

	public Graphics getGraphics() {
	    return null;
	}

	public ImageProducer getSource() {
	   return spriteProducer;
	}

	public void flush() {
	    pixels = null;
	}

	public Object getProperty(String name, ImageObserver o) {
	    return Image.UndefinedProperty;
	}

}

class SpriteProducer implements ImageProducer {

    public void addConsumer(ImageConsumer ic) {
    }

    public boolean isConsumer(ImageConsumer ic) {
        return true;
    }

    public void removeConsumer(ImageConsumer ic) {
    }

    public void startProduction(ImageConsumer ic) {
    }

    public void requestTopDownLeftRightResend(ImageConsumer ic) {
    }

}
