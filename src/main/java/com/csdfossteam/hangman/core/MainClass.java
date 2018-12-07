/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * print
 *
 * @author xrica_vabenee
 */
public class MainClass {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        SelectWordClass a = new SelectWordClass();
        MainGame m = new MainGame(a);
        File f = new File("/home/xrica_vabenee/NetBeansProjects/hangman_project/src/words.txt");
        a.countWordList();
        a.pickRandomWord();
        System.out.println(a.getLine());
        a.createDashes(true);
        a.printArrayList(a.getArrayList());

        while (!m.checkWord()) {
            m.inputLetter();
            a.printArrayList(a.getArrayList());
        }
    }

}
