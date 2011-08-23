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
 * Represents an explodable wall on the playground.
 * @author Christian Lins
 */
class Wall extends Element implements Explodable
{

	public static final String IMAGE_FILENAME = "/resource/gfx/explodable_wall.png";
	private static Image image = null;

	private Playground playground;

	public Wall(short x, short y, Playground playground)
	{
		super(x, y);
		this.playground = playground;
		if(image == null) {
			image = loadImage(IMAGE_FILENAME);
			image = scaleImage(image, playground.getTileSize(), playground.getTileSize());
		}
	}

	public void explode() {
		System.out.println("Wall at " + gridX + "/" + gridY + " explodes!");
		this.playground.setElement(gridX, gridY, 0, null);
	}

	public Image getImage()
	{
		return Wall.image;
	}

}
