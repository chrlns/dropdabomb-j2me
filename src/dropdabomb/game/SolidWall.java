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

import javax.microedition.lcdui.Image;

/**
 * A solid, not exploadable wall.
 * @author Christian Lins
 */
class SolidWall extends Element
{

	public static final String IMAGE_FILENAME = "/resource/gfx/solid_wall.png";
	private static Image image = null;

	public SolidWall(int x, int y, int tileSize)
	{
		super((short)x, (short)y);
		if(image == null && tileSize > 0) {
			image = loadImage(IMAGE_FILENAME);
			image = scaleImage(image, tileSize, tileSize);
		}
	}

	/**
	 * @return Returns the filename that is used on client side to
	 * paint this wall.
	 */
	public Image getImage()
	{
		return SolidWall.image;
	}

}
