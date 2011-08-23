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

/**
 *
 * @author Christian Lins
 */
public class ExplosionThread extends Thread {

	private Game game;

	public ExplosionThread(Game game) {
		this.game = game;
	}

	public void run() {
		while(!game.isStopped()) {
			try {
				Playground pg = this.game.getPlayground();
				for(int x = 0; x < Playground.COLS; x++) {
					for(int y = 0; y < Playground.ROWS; y++) {
						Element[] el = pg.getElement(x, y);
						if(el[0] instanceof Explosion) {
							Explosion ex = (Explosion)el[0];
							if(ex.nextStage() > 5) {
								el[0] = null;
							}
						}
					}
				}
				pg.repaint();
				sleep(100);
			} catch(Exception ex) {
				System.out.println("Exception catched: " + ex.getMessage());
			}
		}
		System.out.println("ExplosionThread stopped.");
	}
}
