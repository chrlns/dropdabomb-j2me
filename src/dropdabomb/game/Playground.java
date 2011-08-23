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
import java.util.Random;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

/**
 * Playground canvas.
 * @author Christian Lins
 */
public class Playground extends Canvas implements CommandListener
{
	public static final short ROWS = 11;
	public static final short COLS = 11;

	private static final Element[] SOLID_WALL =
		new Element[] {new SolidWall(-1, -1, 0), null, null, null, null};
	private static Command CMD_STOP = new Command("Stop", Command.STOP, 1);

	private short fieldSize;
	private short offsetX, offsetY;
	private Element[][][] elements;
	private Game game;
	private BombermanMIDlet midlet;

	public Playground(BombermanMIDlet midlet, Game game)
	{
		try {
			// Set up this canvas to listen to command events
			setCommandListener(this);

			// Add the Exit command
			addCommand(CMD_STOP);

			fieldSize = (short)Math.min(getWidth() / COLS, getHeight() / ROWS);
			offsetX = (short)((getWidth() - fieldSize * COLS) / 2);
			offsetY = (short)((getHeight() - fieldSize * ROWS) / 2);;

			this.elements = new Element[COLS][ROWS][5];
			initPlayground(fieldSize);

			this.game = game;
			this.midlet = midlet;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get Element at x,y. The parameters are int to avoid unecessary casts.
	 * @param x
	 * @param y
	 * @return Element[] at the specific position. Never null.
	 */
	public Element[] getElement(int x, int y) {
		if((x >= 0 && x < COLS) && (y >= 0 && y < ROWS))
		{
			return this.elements[x][y];
		} else {
			return SOLID_WALL; // null would be misleading -> empty
		}
	}

	/**
	 * Sets Element e at x,y on layer layer
	 * @param x
	 * @param y
	 * @param layer
	 * @param e
	 */
	public void setElement(int x, int y, int layer, Element e) {
		if(x < ROWS && y < COLS && x >= 0 && y >= 0) {
			if(!(this.elements[x][y][layer] instanceof SolidWall)) {
				this.elements[x][y][layer] = e;
			}
		}
	}

	public short getTileSize() {
		return this.fieldSize;
	}

	private void initPlayground(short fieldSize)
	{
		Random rnd = new Random();

		// Initialize the playground
		for (short x = 0; x < COLS; x++) {
			for (short y = 0; y < ROWS; y++) {
				// Solid borders
				/*if ((x == 0) || (x == COLS - 1) || (y == 0) || (y == ROWS - 1)) {
					elements[x][y][0] = new SolidWall(x, y, fieldSize); // A solid wall
				} // Solid walls within
				else */if ((y % 2 == 1) && (x % 2 == 1)) {
					elements[x][y][0] = new SolidWall(x, y, fieldSize); // Solid wall
				} // Player starting points
				else if ((x == 0 && (y == 0 || y == 1)) || (x == 1 && y == 0) || // top left
					(x == COLS - 1 && (y == 0 || y == 1)) || (x == COLS - 2 && y == 0) || // top right
					(x == 0 && (y == ROWS - 1 || y == ROWS - 2)) || (x == 1 && y == ROWS - 1) || // lower left
					(x == COLS - 1 && (y == ROWS - 1 || y == ROWS - 2))
					|| (x == COLS - 2 && y == ROWS - 1) // lower right
					) {
					// Make no walls
					continue;
				} else if (rnd.nextFloat() >= 0.2) { // 20% of the area should be empty
					elements[x][y][0] = new Wall(x, y, this); // Exploadable wall

					// Extras are placed later when a Wall explodes.
				}
			}
		}
	}

	public void paint(Graphics g)
	{
		g.setColor(0, 0, 0);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(10, 155, 10);
		g.fillRect(offsetX, offsetY, 
				getWidth() - 2 * offsetX, getWidth() - 2 * offsetX);

		// Draw the grid lines
		g.setColor(0, 0, 0);
		for(int x = 0; x <= this.fieldSize * COLS; x += this.fieldSize) {
			g.drawLine(offsetX + x, offsetY + 0, offsetX + x, offsetY + this.fieldSize * ROWS);
		}
		for(int y = 0; y <= this.fieldSize * ROWS; y += this.fieldSize) {
			g.drawLine(offsetX + 0, offsetY + y, offsetX + this.fieldSize * COLS, offsetY + y);
		}

		// Draw the elements
		for(int x = 0; x < COLS; x++) {
			for(int y = 0; y < ROWS; y++) {
				drawElement(g, this.elements[x][y][0], x, y); // Walls/Extras/Bomb/Explosion
				drawElement(g, this.elements[x][y][1], x, y); // Player 1
				drawElement(g, this.elements[x][y][2], x, y); // Player 2
				drawElement(g, this.elements[x][y][3], x, y); // Player 3
				drawElement(g, this.elements[x][y][4], x, y); // Player 4
			}
		}
	}

	private void drawElement(Graphics graphics, Element el, int x, int y) {
		if (el != null) {
			graphics.drawImage(
				el.getImage(),
				offsetX + x * fieldSize, offsetY + y * fieldSize,
				Graphics.TOP | Graphics.LEFT);
		}
	}

	/**
	 * Called when a key is pressed.
	 * @param keyCode
	 */
	protected void keyPressed(int keyCode)
	{
		if(keyCode == getKeyCode(Canvas.UP)) {
			keyCode = Canvas.KEY_NUM2;
		} else if(keyCode == getKeyCode(Canvas.DOWN)) {
			keyCode = Canvas.KEY_NUM8;
		} else if(keyCode == getKeyCode(Canvas.LEFT)) {
			keyCode = Canvas.KEY_NUM4;
		} else if(keyCode == getKeyCode(Canvas.RIGHT)) {
			keyCode = Canvas.KEY_NUM6;
		} else if(keyCode == getKeyCode(Canvas.FIRE)) {
			keyCode = Canvas.KEY_NUM5;
		}

		switch (keyCode) {
			case Canvas.KEY_NUM2: // Up
				this.game.movePlayer(this.game.getPlayers()[0], (short)0, (short)-1);
				break;
			case Canvas.KEY_NUM8: // Down
				this.game.movePlayer(this.game.getPlayers()[0], (short)0, (short)1);
				break;
			case Canvas.KEY_NUM4: // Left
				this.game.movePlayer(this.game.getPlayers()[0], (short)-1, (short)0);
				break;
			case Canvas.KEY_NUM6: // Right
				this.game.movePlayer(this.game.getPlayers()[0], (short)1, (short)0);
				break;
			case Canvas.KEY_NUM5: // Bomb
				this.game.getPlayers()[0].placeBomb();
				break;
			default:
				System.out.println("Unhandled keycode: " + keyCode);
		}
		repaint();
	}

	/**
	 * Called when a key is released.
	 */
	protected void keyReleased(int keyCode)
	{
		System.out.println("Released: " + keyCode);
	}

	/**
	 * Called when a key is repeated (held down).
	 */
	protected void keyRepeated(int keyCode)
	{
		System.out.println("Repeated: " + keyCode);
	}

	/**
	 * Called when the pointer is dragged.
	 */
	protected void pointerDragged(int x, int y)
	{
	}

	/**
	 * Called when the pointer is pressed.
	 */
	protected void pointerPressed(int x, int y)
	{
	}

	/**
	 * Called when the pointer is released.
	 */
	protected void pointerReleased(int x, int y)
	{
	}

	/**
	 * Called when action should be handled
	 */
	public void commandAction(Command command, Displayable displayable)
	{
		if(command.equals(CMD_STOP)) {
			this.midlet.exitGame(null);
			Display.getDisplay(midlet).setCurrent(this.midlet.getMainForm());
		}
	}
}
