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

/**
 * Timer controlling exploding bombs.
 * @author Christian Lins
 */
class BombTimerThread extends Thread {

	public static final int BOMB_TIME = 4000 / 6; // 4 sec / 6 steps

	private Vector bombs = new Vector();
	private Game game;

	public BombTimerThread(Game game) {
		this.game = game;
	}

	public void addBomb(Bomb bomb) {
		bombs.addElement(bomb);
	}

	public void removeBomb(Bomb bomb) {
		bombs.removeElement(bomb);
	}

	/**
	 * Is called when bomb has reached last Image status
	 */
	public void run() {
		while(!game.isStopped()) {
			try {
				if(game.isRunning()) {
					for(int n = 0; n < this.bombs.size(); n++) {
						Bomb bomb = (Bomb)this.bombs.elementAt(n);
						if(bomb.tick() >= 6) {
							bomb.explode();
						}
					}
				}
				sleep(BOMB_TIME);
			} catch(Exception ex) {
				System.out.println("Exception: " + ex.getMessage());
			}
		}
		System.out.println("BombTimerThread stopped.");
	}

}
