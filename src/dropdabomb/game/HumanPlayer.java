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

import dropdabomb.BombermanMIDlet;
import javax.microedition.lcdui.Alert;

/**
 *
 * @author Christian Lins
 */
public class HumanPlayer extends Player {

	private BombermanMIDlet midlet;

	public HumanPlayer(BombermanMIDlet midlet, Game game, Playground playground) {
		super(game, "KI-Knecht", playground.getTileSize());

		this.midlet = midlet;
	}

	public void explode() {
		super.explode();

		Alert alert = new Alert("Looser");
		alert.setString("You lost the game!");
		alert.setTimeout(4000);

		midlet.exitGame(alert);
	}
}
