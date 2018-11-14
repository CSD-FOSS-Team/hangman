
package com.csdfossteam.hangman.face.cli;

import java.io.IOException;
import java.util.HashMap;

import com.csdfossteam.hangman.face.cli.base.Console;
import com.csdfossteam.hangman.face.cli.base.Input;
import com.csdfossteam.hangman.face.cli.base.Screen;
import com.csdfossteam.hangman.face.cli.base.ViewValues;
import com.csdfossteam.hangman.face.cli.base.ViewsFile;


/**
 * A cli interface for the hangman game
 *
 * @author Akritas Akritidis
 */
public class HangmanScreen extends Screen {

    private final ViewsFile views;

    private ViewValues values;
    private HashMap<String, Input> inputs;

    public HangmanScreen(ViewsFile views) throws IOException {
        super(Console.create());
        this.views = views;

        values = new ViewValues();
        values.set("version", "0.1");
        setValues(values);

        inputs = new HashMap<>();

        inputs.put("Menu", new Input() //
                .set(1, "n", this::menuNewGame) //
                .set(2, "s", this::menuSettings) //
                .set(0, "e", this::menuExit) //
                .setDefault(this::unknown));

        inputs.put("Settings", new Input() //
                .set(0, "b", this::settingsBack) //
                .setDefault(this::unknown));

        inputs.put("GameSetup", new Input() //
                .set(0, "b", this::gameSetupBack) //
                .setDefault(this::unknown));

        setViewInput("Menu");
    }

    public void setViewInput(String name) {
        super.setView(views.get(name));
        super.setInput(inputs.get(name));
    }

    // ==

    public void menuNewGame() {
        setViewInput("GameSetup");
    }

    public void menuSettings() {
        setViewInput("Settings");
    }

    public void menuExit() {
        stop();
    }

    // ==

    public void settingsBack() {
        setViewInput("Menu");
    }

    // ==

    public void gameSetupBack() {
        setViewInput("Menu");
    }

    // ==

    public void unknown(String text) {

    }

}
