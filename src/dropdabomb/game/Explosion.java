/*
 *  Drop Da Bomb
 *  Copyright (C) 2008-2011 Christian Lins <christian@lins.me>
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
 *
 * @author Christian Lins
 */
public class Explosion extends Element {

	private static Image[] images = null;

	private int stage = 0;

	public Explosion(int x, int y, short tileSize) {
		super((short)x, (short)y);

		if(images == null) {
			images = new Image[5];
			for(short n = 1; n <= 5; n++) {
				images[n - 1] = loadImage("/resource/gfx/explosion/expl" + n + ".png");
				images[n - 1] = scaleImage(images[n - 1], tileSize, tileSize);
			}
		}
	}

	public Image getImage() {
		return images[Math.min(stage, 4)];
	}

	public int nextStage() {
		return ++stage;
	}

}
