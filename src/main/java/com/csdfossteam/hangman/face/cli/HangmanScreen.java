
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
 * Note: DEPRICATED
 * This class does not work with current implementation. Update Required
 *
 * @author Akritas Akritidis
 */
public class HangmanScreen extends Screen {

    private enum State {
        Menu, Settings, GameSetup
    }

    private final ViewsFile views;

    private ViewValues values;
    private HashMap<State, Input> inputs;

    public HangmanScreen(ViewsFile views) throws IOException {
        super(Console.create());
        this.views = views;

        values = new ViewValues();
        values.set("version", "0.1");
        setValues(values);

        inputs = new HashMap<>();

        inputs.put(State.Menu, new Input() //
                .set(1, "n", this::menuNewGame) //
                .set(2, "s", this::menuSettings) //
                .set(0, "e", this::menuExit) //
                .setDefault(this::unknown));

        inputs.put(State.Settings, new Input() //
                .set(0, "b", this::settingsBack) //
                .setDefault(this::unknown));

        inputs.put(State.GameSetup, new Input() //
                .set(0, "b", this::gameSetupBack) //
                .setDefault(this::unknown));

        setViewInput(State.Menu);
    }

    public void setViewInput(State state) {

        setView(views.get(state.name()));
        setInput(inputs.get(state));
    }

    // ==

    @Override
    public void onPreHandle() {

        values.unset("message");
    }

    // ==

    public void menuNewGame() {
        setViewInput(State.GameSetup);
    }

    public void menuSettings() {
        setViewInput(State.Settings);
    }

    public void menuExit() {
        stop();
    }

    // ==

    public void settingsBack() {
        setViewInput(State.Menu);
    }

    // ==

    public void gameSetupBack() {
        setViewInput(State.Menu);
    }

    // ==

    public void unknown(String text) {
        values.set("message", "Unknown input '" + text + "'");
    }

}
