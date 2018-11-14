
package com.csdfossteam.hangman.face.cli.base;

import java.util.ArrayList;
import java.util.regex.Pattern;


/**
 * A handler of console based input.
 *
 * @author Akritas Akritidis
 */
public class Input {

    /**
     * Simple input text handler.
     */
    @FunctionalInterface
    public static interface InputHandler {

        void handle(String input);
    }

    /**
     * Ignoring text handler.
     */
    @FunctionalInterface
    public static interface SimpleHandler extends InputHandler {

        void handle();

        @Override
        default void handle(String input) {
            handle();
        }
    }

    private static final class Case {

        public final Pattern pattern;
        public final InputHandler handler;

        public Case(Pattern pattern, InputHandler handler) {
            this.pattern = pattern;
            this.handler = handler;
        }

    }

    private final ArrayList<Case> cases;

    private InputHandler defaultHandler;

    public Input() {
        cases = new ArrayList<>();
    }

    /**
     * Handle a given text based on the cases that have been set so far.
     */
    public void handle(String text) {

        for (final Case i : cases) {

            if (i.pattern.matcher(text).matches()) {
                i.handler.handle(text);
                return;
            }
        }

        if (defaultHandler != null) {
            defaultHandler.handle(text);
        }
    }

    /**
     * Set a case which matches either the given id or the given key with case
     * Insensitivity.
     */
    public final Input set(int id, String key, SimpleHandler handler) {
        return set(id, key, (InputHandler) handler);
    }

    /**
     * Set a case which matches either the given id or the given key with case
     * Insensitivity.
     */
    public final Input set(int id, String key, InputHandler handler) {

        final Pattern pattern = Pattern.compile("(" + id + "|" + Pattern.quote(key) + ")", Pattern.CASE_INSENSITIVE);

        cases.add(new Case(pattern, handler));
        return this;
    }

    /**
     * Set a case which matches only if no other case is matched.
     */
    public final Input setDefault(InputHandler handler) {

        defaultHandler = handler;
        return this;
    }

}
