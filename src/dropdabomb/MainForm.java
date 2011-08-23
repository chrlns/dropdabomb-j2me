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
package dropdabomb;

import dropdabomb.game.Element;
import dropdabomb.game.Game;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 *
 * @author Christian Lins
 */
public class MainForm extends Canvas implements CommandListener {

	public static final Command CMD_EXIT = new Command("Exit", Command.EXIT, 0);
	public static final Command CMD_START = new Command("Start", Command.OK, 1);
	public static final Command CMD_HELP = new Command("Help", Command.HELP, 0);
	public static final Command CMD_ABOUT = new Command("About", Command.HELP, 0);
	public static final String IMAGE_FILENAME = "/resource/gfx/bomb.png";

	private static Image image = null;

	private Game game;
	private BombermanMIDlet midlet;

	public MainForm(BombermanMIDlet midlet) {
		if(image == null) {
			image = Element.loadImage(IMAGE_FILENAME);
			image = Element.scaleImage(image, 240, 240);
		}
		this.midlet = midlet;

		addCommand(CMD_EXIT);
		addCommand(CMD_START);
		addCommand(CMD_HELP);
		addCommand(CMD_ABOUT);

		setCommandListener(this);
	}

	public void paint(Graphics g) {
		g.setColor(10, 155, 10);
		g.fillRect(0, 0, getWidth(), getHeight());

		g.setColor(0, 0, 0);
		g.drawString("Drop Da Bomb!", getWidth() / 2, 10,
				Graphics.HCENTER | Graphics.TOP);

		g.drawImage(image, getWidth() / 2, getHeight() / 2,
				Graphics.HCENTER | Graphics.VCENTER);
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public void commandAction(Command cmd, Displayable disp) {
		if(cmd.equals(CMD_ABOUT)) {
			Display.getDisplay(midlet).setCurrent(new AboutForm(midlet));
		} else if(cmd.equals(CMD_EXIT)) {
			this.midlet.notifyDestroyed();
			this.midlet.destroyApp(false);
		} else if(cmd.equals(CMD_HELP)) {
			Display.getDisplay(midlet).setCurrent(new HelpForm(midlet));
		} else if(cmd.equals(CMD_START)) {
			this.game.startGame();
			Display.getDisplay(midlet).setCurrent(game.getPlayground());
		}
	}
}
