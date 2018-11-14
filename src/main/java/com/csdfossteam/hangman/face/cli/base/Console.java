
package com.csdfossteam.hangman.face.cli.base;

import java.io.PrintStream;
import java.util.Scanner;


/**
 * A basic console operations wrapper
 *
 * @author Akritas Akritidis
 */
public abstract class Console {

    public static final Console create() {

        if (System.console() != null) {
            return new ClearTextConsole(true, "\033[H\033[2J");
        } else {
            return new ClearTextConsole(false, "\n\n\n\n\n\n\n\n\n");
        }
    }

    protected final boolean colors;

    protected final Scanner scanner;
    protected final PrintStream printer;

    private Console(boolean colors) {
        this.colors = colors;

        scanner = new Scanner(System.in);
        printer = System.out;

    }

    public String input() {
        return scanner.nextLine();
    }

    public void output(String text) {
        // TODO strip color when colors is false

        clear();
        printer.print(text);
    }

    protected abstract void clear();

    private static final class ClearTextConsole extends Console {

        private final String clearText;

        public ClearTextConsole(boolean colors, String clearText) {
            super(colors);
            this.clearText = clearText;
        }

        @Override
        protected void clear() {
            printer.print(clearText);
        }

    }
}
