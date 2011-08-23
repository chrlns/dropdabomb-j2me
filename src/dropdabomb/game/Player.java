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

import java.util.Vector;
import javax.microedition.lcdui.Image;

/**
 * Human player.
 * @author Kai Ritterbusch
 * @author Christian Lins
 */
public class Player extends Element implements Explodable {

	public static final short UP	= 1;
	public static final short DOWN	= 2;
	public static final short LEFT	= 3;
	public static final short RIGHT	= 4;
	public static final short NONE	= 5;
	public static final short EXPLODING = 6;

	protected Vector bombs = new Vector();
	protected Game game;
	protected String nickname;
	protected short id;
	private Image[] images = new Image[1];
	private short   lastMoveDirection = DOWN;
	private short	tileSize;

	// For extras
	protected short bombDistance = 1;
	protected short bombCount = 1;

	public Player(Game game, String nickname, short tileSize) {
		super((short)0, (short)0);

		this.game = game;
		this.nickname = nickname;
		this.tileSize = tileSize;
	}

	public void explode() {
		this.game.removePlayer(this);
	}

	/**
	 * Returns Image filename for player
	 * @return filename
	 */
	private String getImageFilename() {
		String imgPath = "/resource/gfx/player" + getID() + "/";
		String addition = "";

		switch (lastMoveDirection) {
			case UP: {
				addition = "1";
				break;
			}
			case DOWN: {
				addition = "6";
				break;
			}
			case LEFT: {
				addition = "11";
				break;
			}
			case RIGHT: {
				addition = "16";
				break;
			}
		}

		imgPath = imgPath + addition + ".png";

		return imgPath;
	}

	public Image getImage() {
		return this.images[0];
	}

	/**
	 * Nickname of the player
	 * @return
	 */
	public String getNickname() {
		return this.nickname;
	}

	/**
	 * Returns ID of the player
	 * @return
	 */
	public int getID() {
		return id;
	}

	/**
	 * Raise if BombDistance-Extra is collected.
	 */
	public void raiseBombDistance() {
		this.bombDistance++;
	}

	/**
	 * raise Bombcount
	 */
	public void raiseBombCount() {
		this.bombCount++;
	}

	/**
	 * Moves player
	 * @param dx
	 * @param dy
	 */
	void move(int dx, int dy) {
		this.gridX += dx;
		this.gridY += dy;

		if (dx < 0) {
			lastMoveDirection = LEFT;
		} else if (dx > 0) {
			lastMoveDirection = RIGHT;
		} else if (dy < 0) {
			lastMoveDirection = UP;
		} else if (dy > 0) {
			lastMoveDirection = DOWN;
		}
	}

	/**
	 * place a Bomb
	 */
	protected void placeBomb() {
		if (bombs.size() >= this.bombCount) {
			return;
		}
		System.out.println("Spieler " + nickname + " legt Bombe bei " + gridX + "/" + gridY);

		Bomb bomb = new Bomb(game, gridX, gridY, this, tileSize);
		this.bombs.addElement(bomb);

		this.game.getPlayground().setElement(gridX, gridY, 0, bomb);
	}

	/**
	 * Set active game of the player
	 * @param game
	 */
	public void setGame(Game game) {
		this.game = game;
	}

	/**
	 * Set ID of the player
	 * @param id
	 */
	public void setID(short id) {
		this.id = id;

		// Load the images
		this.images[0] = loadImage(getImageFilename());
		this.images[0] = scaleImage(this.images[0], tileSize, tileSize);
	}
}
