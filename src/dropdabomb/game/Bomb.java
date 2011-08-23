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
 * The BOMB!
 * @author Kai Ritterbusch
 * @author Christian Lins
 */
public class Bomb extends Element implements Explodable {

	private static Image[] images = null;

	private Player player; // Player who dropped this Bomb
	private int stage = 0;
	private Game game;

	public Bomb(Game game, short x, short y, Player player, short tileSize) {
		super(x, y);
		this.game = game;
		this.player = player;

		if(images == null) {
			images = new Image[6];
			for(short n = 1; n <= 6; n++) {
				images[n - 1] = loadImage("/resource/gfx/bomb/bomb" + n + ".png");
				images[n - 1] = scaleImage(images[n - 1], tileSize, tileSize);
			}
		}

		game.getBombTimerThread().addBomb(this);
	}

	/**
	 * Bomb explodes.
	 */
	public void explode() {
		try {
			System.out.println(this + " explodes!");
			player.bombs.removeElement(this);
			player.game.getPlayground().setElement(gridX, gridY, 0, null);
			game.getBombTimerThread().removeBomb(this);
			player.game.notifyExplosion(gridX, gridY, player.bombDistance);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Image getImage() {
		return images[stage];
	}

	/**
	 * Updates Playground
	 * @return
	 */
	int tick() {
		player.game.forcePlaygroundUpdate();
		return ++stage;
	}

}
