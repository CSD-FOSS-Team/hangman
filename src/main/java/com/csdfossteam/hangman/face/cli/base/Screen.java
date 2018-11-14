
package com.csdfossteam.hangman.face.cli.base;

/**
 * A handler of multiple views and input.
 *
 * @author Akritas Akritidis
 */
public class Screen {

    private final Console console;

    private View view;
    private ViewValues values;
    private Input input;

    private boolean alive;

    public Screen(Console console) {
        this.console = console;
    }

    /**
     * Starts the interface loop using the view, view values and the input state.
     * <p>
     * To stop the loop the {@link #stop()} method must be called.
     */
    public final void start() {
        alive = true;

        while (alive) {

            onPreOutput();
            console.output(view.text(values));

            onPreInput();
            final String text = console.input();

            onPreHandle();
            input.handle(text);
        }

    }

    public void onPreOutput() {

    }

    public void onPreInput() {

    }

    public void onPreHandle() {

    }

    /**
     * Stops the interface loop, if one has been started.
     */
    public final void stop() {
        alive = false;
    }

    // etters

    public final View getView() {
        return view;
    }

    public final ViewValues getValues() {
        return values;
    }

    public final Input getInput() {
        return input;
    }

    public final void setView(View view) {
        this.view = view;
    }

    public final void setValues(ViewValues values) {
        this.values = values;
    }

    public final void setInput(Input input) {
        this.input = input;
    }

}
