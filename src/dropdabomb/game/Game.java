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

import dropdabomb.BombermanMIDlet;
import dropdabomb.game.ai.AIPlayer;
import dropdabomb.game.ai.AIPlayerThread;
import javax.microedition.lcdui.Alert;

/**
 * Represents a game that can be created and started by the users.
 * @author Kai Ritterbusch
 * @author Christian Lins
 */
public class Game {
	private AIPlayerThread aiThread0;
	private AIPlayerThread aiThread1;
	private AIPlayerThread aiThread2;
	private Player[] players = new Player[4];
	private short    playerIDHelper = 0;
	private Playground playground;
	private boolean running = false;
	private boolean stopped = false;
	private boolean started = false;
	private BombTimerThread bombTimerThread;
	private ExplosionThread explosionThread;
	private BombermanMIDlet midlet;

	public Game(BombermanMIDlet midlet) {
		this.playground = new Playground(midlet, this);
		this.midlet = midlet;

		// Create one human player and three AIs
		Player player0 = new HumanPlayer(midlet, this, playground);
		AIPlayer player1 = new AIPlayer(this, playground);
		AIPlayer player2 = new AIPlayer(this, playground);
		AIPlayer player3 = new AIPlayer(this, playground);

		// and add them to the game
		addPlayer(player0);
		addPlayer(player1);
		addPlayer(player2);
		addPlayer(player3);

		// make the AIs alive
		aiThread0 = new AIPlayerThread(player1, this);
		aiThread1 = new AIPlayerThread(player2, this);
		aiThread2 = new AIPlayerThread(player3, this);

		// arise AI
		aiThread0.start();
		aiThread1.start();
		aiThread2.start();
	}

	/**
	 * Adds a player to the playground
	 * @param player
	 * @return
	 */
	public final boolean addPlayer(Player player) {
		if(playerIDHelper < 4) {
			player.setID(++playerIDHelper);

			// Adds player to playground view, set starting position
			int x = 0;
			int y = 0;
			if (player.getID() == 2) {
				x = Playground.COLS - 1;
				y = Playground.ROWS - 1;
			} else if (player.getID() == 3) {
				y = Playground.ROWS - 1;
			} else if (player.getID() == 4) {
				x = Playground.COLS - 1;
			}
			this.playground.setElement(x, y, player.getID(), player);
			player.setPosition((short)x, (short)y);

			players[player.getID() - 1] = player;
			player.setGame(this);
			System.out.println("Player " + player.getID() + " added to Playground (" + player.getNickname() + ")");
			return true;
		} else {
			return false;
		}
	}

	BombTimerThread getBombTimerThread() {
		return this.bombTimerThread;
	}

	/**
	 * Forces the server to update the Playground, e.g. when an AI player has moved.
	 */
	public void forcePlaygroundUpdate() {
		this.playground.repaint();
	}

	/**
	 * Return the playground
	 * @return playground
	 */
	public Playground getPlayground() {
		return this.playground;
	}

	/**
	 * Returns list of all players
	 * @return list of players
	 */
	public Player[] getPlayers() {
		return this.players;
	}

	/**
	 * Removes player from playerlist and playground.
	 * @param player
	 */
	public void removePlayer(Player player) {
		this.players[player.getID() - 1] = null;
		this.playground.setElement(player.getX(), player.getY(), player.getID(), null);

		// Is this the right place to check for victory condition?
		if(numPlayers() == 1 && players[0] != null) {
			Alert alert = new Alert("Winner");
			alert.setString("You won the game!");
			alert.setTimeout(4000);

			midlet.exitGame(alert);
		}
	}

	/**
	 * Moves a player in the game's playground if possible.
	 * @param player
	 * @param dx
	 * @param dy
	 * @return
	 */
	public boolean movePlayer(Player player, short dx, short dy) {
		if (dx == dy && (dx > 1 || dy > 1)) { // No jumps over edges
			return false;
		}

		// Check if we can move in that direction
		short nx = (short)(player.getX() + dx);
		short ny = (short)(player.getY() + dy);

		if (nx < 0 || ny < 0
				|| Playground.COLS <= nx
				|| Playground.ROWS <= ny) {
			return false;
		}

		Element el = this.playground.getElement(nx, ny)[0];
		if (el == null) { // or Extra
			// Set old position in Playground to null...
			this.playground.setElement(player.getX(), player.getY(), player.getID(), null);
			// ...and set new position
			player.move(dx, dy);
			this.playground.setElement(player.getX(), player.getY(), player.getID(), player);

			return true;
		} else if (el instanceof Extra) {
			if (el instanceof ExtraBomb) {
				player.raiseBombCount();
			} else {
				player.raiseBombDistance();
			}
			this.playground.setElement(el.getX(), el.getY(), 0, null);
			// Set old position in Playground to null...
			this.playground.setElement(player.getX(), player.getY(), player.getID(), null);
			player.setPosition(nx, ny);
			this.playground.setElement(player.getX(), player.getY(), player.getID(), player);

			return true;
		} else {
			return false;
		}
	}

	private int numPlayers() {
		int num = 0;
		for(int n = 0; n < 4; n++) {
			if(players[n] != null) {
				num++;
			}
		}
		return num;
	}

	private void destroyElements(Element[] el) {
		if(el == null) {
			return;
		}

		for(int n = 0; n < 5; n++) {
			if(el[n] instanceof Explodable) {
				((Explodable)el[n]).explode();
			}
		}
	}

	public void notifyExplosion(short x, short y, short blast) {
		// Destroy all objects within the bomb blast radius
		destroyElements(this.playground.getElement(x, y));
		playground.setElement(x, y, 0, new Explosion(x, y, playground.getTileSize()));

		for(short b = 1; b <= blast; b++) {
			destroyElements(this.playground.getElement(x, y + b)); // up
			playground.setElement(x, y + b, 0,
					new Explosion(x, y + b, playground.getTileSize()));

			destroyElements(this.playground.getElement(x, y - b)); // down
			playground.setElement(x, y - b, 0,
					new Explosion(x, y - b, playground.getTileSize()));

			destroyElements(this.playground.getElement(x - b, y)); // left
			playground.setElement(x - b, y, 0,
					new Explosion(x - b, y, playground.getTileSize()));

			destroyElements(this.playground.getElement(x + b, y)); // right
			playground.setElement(x + b, y, 0,
					new Explosion(x + b, y, playground.getTileSize()));
		}
	}

	/**
	 * Set the Playground
	 * @param playground
	 */
	public void setPlayground(Playground playground) {
		this.playground = playground;
	}

	/**
	 * Checks if game is Running
	 * @return true if is running
	 */
	public boolean isRunning() {
		return this.running;
	}

	public boolean isStopped() {
		return this.stopped;
	}

	public boolean isStarted() {
		return this.started;
	}

	public void startGame() {
		this.started = true;

		if(this.bombTimerThread == null) {
			this.bombTimerThread = new BombTimerThread(this);
			this.bombTimerThread.start();
		}

		if(this.explosionThread == null) {
			this.explosionThread = new ExplosionThread(this);
			this.explosionThread.start();
		}

		this.running = true;
	}

	public void pauseGame() {
		this.running = false;
	}

	public void stopGame() throws InterruptedException {
		this.running = false;
		this.stopped = true;

		this.bombTimerThread = null;
		this.explosionThread = null;
	}
}
