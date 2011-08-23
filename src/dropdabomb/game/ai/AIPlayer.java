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
package dropdabomb.game.ai;

import dropdabomb.game.Bomb;
import dropdabomb.game.Element;
import dropdabomb.game.Explodable;
import dropdabomb.game.Extra;
import dropdabomb.game.Game;
import dropdabomb.game.Player;
import dropdabomb.game.Playground;
import dropdabomb.Vector2D;
import java.util.Random;
import java.util.Vector;

/**
 * An AI-controlled player. The AI uses a modified A* algorithm for
 * path finding.
 * @author Kai Ritterbusch
 * @author Christian Lins
 */
public class AIPlayer extends Player {

	private transient Vector currentPath = new Vector();
	private transient boolean isDead = false;
	private transient Playground playground = null;

	public AIPlayer(Game g, Playground playground) {
		super(g, "KI-Knecht", playground.getTileSize());

		this.nickname += hashCode();

		if (g == null || playground == null) {
			throw new IllegalArgumentException();
		}

		this.game = g;
		this.playground = playground;

		Thread thread = new AIPlayerThread(this, g);
		thread.start();
	}

	public void explode() {
		super.explode();
		die();
	}

	/**
	 * Search for explodable Elements in Element[]
	 * @param elements
	 * @return
	 */
	private int containsExplodable(Element[] elements) {
		if(elements == null) {
			return 0;
		}

		int explodables = 0;

		for (int n = 0; n < elements.length; n++) {
			Element e = elements[n];
			if (e instanceof Explodable && !(e instanceof Extra)) {
				explodables++;
			}
		}

		return explodables;
	}

	/**
	 * Check if Element[] contains another element
	 * @param elements
	 * @param c
	 * @return true if Element[] contains c
	 */
	private boolean contains(Element[] elements, Element element) {
		if(elements == null) {
			return false;
		}

		for (int n = 0; n < elements.length; n++) {
			if (element.equals(elements[n])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method is called when AIPlayer dies.
	 * Sets this.isDead to true and removes player from gamelist
	 */
	public void die() {
		this.isDead = true;

		// Remove player from game
		this.game.removePlayer(this);
	}

	/**
	 * Returns living status of the Player
	 * @return
	 */
	public boolean isDead() {
		return this.isDead;
	}

	/**
	 * Determines if the given point is a possible target zone, that means
	 * has Explodable neighbours.
	 * @param pnt
	 * @return
	 */
	private boolean isTargetZone(Vector2D pnt) {
		// Determine all possible neighbours...
		Element[] n1 = this.playground.getElement((short)(pnt.X + 1), pnt.Y);
		Element[] n2 = this.playground.getElement((short)(pnt.X - 1), pnt.Y);
		Element[] n3 = this.playground.getElement(pnt.X, (short)(pnt.Y + 1));
		Element[] n4 = this.playground.getElement(pnt.X, (short)(pnt.Y - 1));

		int numExpln1 = containsExplodable(n1);
		int numExpln2 = containsExplodable(n2);
		int numExpln3 = containsExplodable(n3);
		int numExpln4 = containsExplodable(n4);
		int sumExpl = numExpln1 + numExpln2 + numExpln3 + numExpln4;

		if (sumExpl == 1) {
			if (contains(n1, this) || contains(n2, this) || contains(n3, this) || contains(n4, this)) {
				return false;
			} else {
				return true;
			}
		} else if (sumExpl > 1) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isExtra(Vector2D pnt) {
		Element[] el = this.playground.getElement(pnt.X, pnt.Y);
		if (el == null) {
			return false;
		}
		return el[0] instanceof Extra;
	}

	/**
	 * Calculates a path to a possible bombing spot.
	 */
	private Vector calculateTargetPath() {
		Random rand = new Random();

		// The A* Algorithm
		Vector openNodes = new Vector(); // Not yet visited nodes
		Vector closedNodes = new Vector(); // Already visited nodes

		// Initialize with starting point (add = push = at end of list)
		// Starting point (gridX, gridY) is the current player position
		openNodes.addElement(new short[]{gridX, gridY, -255, -255});
		while (openNodes.size() > 0) {
			short[] node = (short[])openNodes.firstElement();
			openNodes.removeElementAt(0); // pop()
			Vector2D pnt = new Vector2D(node[0], node[1]);

			if (checkForBomb(pnt) != null) {
				// A bomb in the path is a really bad idea or we must know
				// when this bomb explodes...
				// And placing a bomb next to a ticking other is a even worse idea...
				continue;
			} else if (isTargetZone(pnt) || // Are the neighbours of point explodable?
					isExtra(pnt) || // Or is it an extra we can collect?
					closedNodes.size() > 15) {
				// Backtrace the path
				Vector path = new Vector();
				path.insertElementAt(node, 0); // Add at front
				while (closedNodes.size() > 1) {
					path.insertElementAt(closedNodes.firstElement(), 0);
					closedNodes.removeElementAt(0);
				}

				return path;
			} else {
				short r1 = rand.nextInt(2) == 1 ? (short)1 : (short)-1;

				// Find all possible neighbours of node
				Element[] n1a = this.playground.getElement(node[0] + r1, node[1]);
				Element n1 = n1a == null ? null : n1a[0];
				Element[] n2a = this.playground.getElement(node[0] - r1, node[1]);
				Element n2 = n2a == null ? null : n2a[0];
				Element[] n3a = this.playground.getElement(node[0], node[1] + r1);
				Element n3 = n3a == null ? null : n3a[0];
				Element[] n4a = this.playground.getElement(node[0], node[1] - r1);
				Element n4 = n4a == null ? null : n4a[0];

				boolean saveNode = false;

				if ((n1 == null || n1 instanceof Extra) && node[2] != node[0] + r1) {
					openNodes.insertElementAt(new short[]{(short)(node[0] + r1), node[1], node[0], node[1]}, 0);
					saveNode = true;
				}
				if ((n2 == null || n2 instanceof Extra) && node[2] != node[0] - r1) {
					openNodes.insertElementAt(new short[]{(short)(node[0] - r1), node[1], node[0], node[1]}, 0);
					saveNode = true;
				}
				if ((n3 == null || n3 instanceof Extra) && node[3] != node[1] + r1) {
					openNodes.insertElementAt(new short[]{node[0], (short)(node[1] + r1), node[0], node[1]}, 0);
					saveNode = true;
				}
				if ((n4 == null || n4 instanceof Extra) && node[3] != node[1] - r1) {
					openNodes.insertElementAt(new short[]{node[0], (short)(node[1] - r1), node[0], node[1]}, 0);
					saveNode = true;
				}

				if (saveNode) {
					closedNodes.insertElementAt(node, 0);
				}
			}
		}
		return null;
	}

	/**
	 * Calculate escaperoute after placing a bomb. The route is calculated
	 * from the current player position to a safe point.
	 * @param bomb
	 * @return
	 */
	private Vector calculateHidePath() {
		short x = gridX;
		short y = gridY;

		// the A* Algorithmus
		Vector openNodes = new Vector(); // Not yet travelled nodes
		Vector closedNodes = new Vector(); // Travelled nodes

		// Starting point (x, y) is the bomb
		openNodes.addElement(new short[]{x, y, -255, -255});
		while (openNodes.size() > 0) {
			short[] node = (short[])openNodes.firstElement();
			openNodes.removeElementAt(0); // pop
			//if ((node[0] != x && node[1] != y) || closedNodes.size() > 15) // Is this point save?
			if(checkForBomb(new Vector2D(node[0], node[1])) == null || closedNodes.size() > 15) {
				// check path
				Vector path = new Vector();
				path.insertElementAt(node, 0);
				while (closedNodes.size() > 1) {
					path.insertElementAt(closedNodes.firstElement(), 0);
					closedNodes.removeElementAt(0);
				}
				return path;
			} else {
				// Get all neighbours from node
				Element n1 = playground.getElement(node[0] + 1, node[1])[0];
				Element n2 = playground.getElement(node[0] - 1, node[1])[0];
				Element n3 = playground.getElement(node[0], node[1] + 1)[0];
				Element n4 = playground.getElement(node[0], node[1] - 1)[0];

				boolean saveNode = false;

				if ((n1 == null || n1 instanceof Extra) && node[2] != node[0] + 1) {
					openNodes.insertElementAt(new short[]
						{(short)(node[0] + 1), node[1], node[0], node[1]}, 0);
					saveNode = true;
				}
				if ((n2 == null || n2 instanceof Extra) && node[2] != node[0] - 1) {
					openNodes.insertElementAt(new short[]
						{(short)(node[0] - 1), node[1], node[0], node[1]}, 0);
					saveNode = true;
				}
				if ((n3 == null || n3 instanceof Extra) && node[3] != node[1] + 1) {
					openNodes.insertElementAt(new short[]
						{node[0], (short)(node[1] + 1), node[0], node[1]}, 0);
					saveNode = true;
				}
				if ((n4 == null || n4 instanceof Extra) && node[3] != node[1] - 1) {
					openNodes.insertElementAt(new short[]
						{node[0], (short)(node[1] - 1), node[0], node[1]}, 0);
					saveNode = true;
				}

				if (saveNode) {
					closedNodes.insertElementAt(node, 0);
				}
			}
		}

		return null;
	}

	/**
	 * Checks if bomb is near the player.
	 * @param bomb
	 * @return null if no Bomb is found
	 */
	private Element checkForBomb(Vector2D playerPos) {
		int matrixX = playerPos.X; // this.gridX;
		int matrixY = playerPos.Y; // this.gridY;

		// Player is on the bomb
		Element[] zeroElements = this.playground.getElement(matrixX, matrixY);
		if (zeroElements != null && zeroElements[0] instanceof Bomb) {
			return zeroElements[0];
		}

		// Player is near the bomb
		for (int i = 0; i < 4; i++) {
			if (this.playground.getElement(matrixX + i, matrixY) != null
					&& this.playground.getElement(matrixX + i, matrixY)[0] instanceof Bomb) {
				return this.playground.getElement(matrixX + i, matrixY)[0];
			} else if (this.playground.getElement(matrixX - i, matrixY) != null
					&& this.playground.getElement(matrixX - i, matrixY)[0] instanceof Bomb) {
				return this.playground.getElement(matrixX - i, matrixY)[0];
			} else if (this.playground.getElement(matrixX, matrixY + i) != null
					&& this.playground.getElement(matrixX, matrixY + i)[0] instanceof Bomb) {
				return this.playground.getElement(matrixX, matrixY + i)[0];
			} else if (this.playground.getElement(matrixX, matrixY - i) != null
					&& this.playground.getElement(matrixX, matrixY - i)[0] instanceof Bomb) {
				return this.playground.getElement(matrixX, matrixY - i)[0];
			}
		}
		return null;
	}

	/**
	 * Moves Player
	 * @param dx
	 * @param dy
	 * @return true if playermoved
	 */
	private boolean wannaMove(int dx, int dy) {
		System.out.println(this.nickname + " laeuft in Richtung " + dx + "/" + dy);

		boolean moved = this.game.movePlayer(this, (short)dx, (short)dy);
		if (moved) {
			this.game.forcePlaygroundUpdate();
		}

		return moved;
	}

	/**
	 * One small step for AI...
	 * Moving of the Player
	 */
	public void tick() {
		if (!this.game.isRunning()) {
			return;
		}

		if (currentPath != null && currentPath.size() > 0) { // walk if path exists
			short[] node = (short[])currentPath.firstElement();
			currentPath.removeElementAt(0);
			if (!wannaMove(node[0] - gridX, node[1] - gridY)) { // Move expects relative direction
				currentPath = new Vector(); // Delete path because it must be invalid
			}
		} else if (bombs.size() < super.bombCount) { // You can put a bomb
			if (isTargetZone(new Vector2D(gridX, gridY))) {
				placeBomb();
				currentPath = calculateHidePath();
				if (currentPath == null) {
					currentPath = new Vector();
					placeBomb(); // Suicide
				}
			} else {
				currentPath = calculateTargetPath();
				if (currentPath == null) {
					currentPath = new Vector();
				}
			}
		} else {
			Element bomb = checkForBomb(new Vector2D(gridX, gridY));
			if (bomb != null) {
				currentPath = calculateHidePath();
				if (currentPath == null) {
					currentPath = new Vector();
				}
			}
		}
	}
}
