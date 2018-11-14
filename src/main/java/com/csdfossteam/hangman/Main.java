
package com.csdfossteam.hangman;

import com.csdfossteam.hangman.face.cli.HangmanScreen;
import com.csdfossteam.hangman.face.cli.base.ViewsFile;


public final class Main {

    private Main() {}

    public static void main(String[] args) throws Exception {

        System.out.println("hangman");

        new HangmanScreen(ViewsFile.fromResoure("com/csdfossteam/hangman/face/cli/" + "classic")).start();

    }

}
