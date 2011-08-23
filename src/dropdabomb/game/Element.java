/*
 *  Drop Da Bomb
 *  Copyright (C) 2008-2011 Christian Lins <christian@lins.me>
 *  Copyright (C) 2008 Kai Ritterbusch <kai.ritterbusch@googlemail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dropdabomb.game;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.Image;

/**
 * One tile of the Playground that can be painted through the
 * ElementPainter class on client side.
 * There are various derived classes for the different tile
 * types: @see{Bomb}, @see{Wall}, @see{Player}.
 * This class could be seen as model component on a MVC-architecture.
 * @author Christian Lins
 */
public abstract class Element {

	/**
	 * Changes size of bitmap. Taken from code snippets at forums.nokia.com
	 * @param sourceImage source image.
	 * @param newWidth width of new image.
	 * @param newHeight height of new image.
	 * @return scaled image.
	 */
	public static Image scaleImage(Image sourceImage, int newWidth, int newHeight) {
		// Remember image size.
		int oldWidth = sourceImage.getWidth();
		int oldHeight = sourceImage.getHeight();

		// Create buffer for input image.
		int[] inputData = new int[oldWidth * oldHeight];
		// Fill it with image data.
		sourceImage.getRGB(inputData, 0, oldWidth, 0, 0, oldWidth, oldHeight);

		// Create buffer for output image.
		int[] outputData = new int[newWidth * newHeight];

		int YD = (oldHeight / newHeight - 1) * oldWidth;
		int YR = oldHeight % newHeight;
		int XD = oldWidth / newWidth;
		int XR = oldWidth % newWidth;

		// New image buffer offset.
		int outOffset = 0;
		// Source image buffer offset.
		int inOffset = 0;

		for (int y = newHeight, YE = 0; y > 0; y--) {
			for (int x = newWidth, XE = 0; x > 0; x--) {
				// Copying pixel from old image to new.
				outputData[outOffset++] = inputData[inOffset];
				inOffset += XD;
				// Calculations for "smooth" scaling in x dimension.
				XE += XR;
				if (XE >= newWidth) {
					XE -= newWidth;
					inOffset++;
				}
			}
			inOffset += YD;
			// Calculations for "smooth" scaling in y dimension.
			YE += YR;
			if (YE >= newHeight) {
				YE -= newHeight;
				inOffset += oldWidth;
			}
		}
		// Create image from output buffer.
		return Image.createRGBImage(outputData, newWidth, newHeight, true);
	}

	public static Image loadImage(final String path) {
		InputStream in = path.getClass().getResourceAsStream(path);
		try {
			if (in != null) {
				return Image.createImage(in);
			} else {
				return null;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		} finally {
			try {
				in.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	protected short gridX;
	protected short gridY;

	public Element(short x, short y) {
		this.gridX = x;
		this.gridY = y;
	}

	public abstract Image getImage();

	public short getX() {
		return this.gridX;
	}

	public short getY() {
		return this.gridY;
	}

	public void setPosition(short x, short y) {
		this.gridX = x;
		this.gridY = y;
	}
}
