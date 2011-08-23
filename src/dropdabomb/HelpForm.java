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

import dropdabomb.game.Game;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Spacer;

/**
 * Help screen. Shows some useful tips for the game.
 * @author Christian Lins
 */
public class HelpForm extends Form implements CommandListener {

	public static final Command CMD_BACK = new Command("Back", Command.BACK, 0);

	private BombermanMIDlet midlet;

	public HelpForm(BombermanMIDlet midlet) {
		super("Help");

		this.midlet = midlet;

		setCommandListener(this);
		addCommand(CMD_BACK);

		// Add help text
		append("You are the yellow player in the upper left corner.");
		append(new Spacer(getWidth(), 1));
		append("Move the player with the number pad: ");
		append("'4' for left, '6' for right, '2' for up, '8' for down.");
		append(new Spacer(getWidth(), 1));
		append("Press '5' to place a bomb.");
		append(new Spacer(getWidth(), 1));
		append("After placing a bomb, run and hide!");
	}

	public void commandAction(Command cmd, Displayable disp) {
		if(cmd.equals(CMD_BACK)) {
			Display.getDisplay(midlet).setCurrent(midlet.getMainForm());
		}
	}
}
