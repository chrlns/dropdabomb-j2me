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
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

/**
 * Main MIDlet of the bomberman app.
 * @author Christian Lins
 */
public class BombermanMIDlet extends MIDlet {

	private Game game = null;
	private MainForm mainForm = null;

	public BombermanMIDlet() {
		this.mainForm = new MainForm(this);
	}

	public void exitGame(Alert alert) {
		try {
			this.game.stopGame();
		} catch(Exception ex) {
			System.out.println(ex.getMessage());
		}

		this.game = null;
		startApp();

		if(alert != null) {
			Display.getDisplay(this).setCurrent(alert, mainForm);
		}
	}

	public MainForm getMainForm() {
		return this.mainForm;
	}

	/**
	 * MIDlets state changes from paused -> started.
	 */
    public void startApp() {
		if(this.game == null) {
			this.game = new Game(this);
			this.mainForm.setGame(game);
		}

		if(this.game.isStarted()) {
			Display.getDisplay(this).setCurrent(game.getPlayground());
		} else {
			Display.getDisplay(this).setCurrent(mainForm);
		}
    }

    public void pauseApp() {
		if(game != null) {
			game.pauseGame(); // will stop AI threads as well
		}
    }

    public void destroyApp(boolean unconditional) {
		if(game != null) {
			try {
				this.game.stopGame();
			} catch(Exception ex) {
				System.out.println("Exception: " + ex.getMessage());
			}
		}
    }
}
