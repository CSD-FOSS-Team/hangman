/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication1;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author xrica_vabenee
 */
public class MainGame {

    private ArrayList<Character> hiddenWord;
    private Scanner scan;
    private SelectWordClass s;
    private Life life;

    public MainGame(SelectWordClass a) throws FileNotFoundException {
        hiddenWord = new ArrayList<Character>();
        s = a;
        scan = new Scanner(System.in);
        hiddenWord = s.getArrayList();

        life = new Life(6);
    }

    public boolean checkChar(String c) {
        if(c.isEmpty()){
            return false;
        }
        char charAt0 = c.charAt(0);
        if (Character.isLetter(charAt0) && c.length() == 1) {
            return true;
        } else {
            return false;
        }
    }

    public void inputLetter() {
        System.out.print("give letter: ");
        String c = scan.nextLine();
        c = c.toLowerCase();
        boolean key=false;
        if (checkChar(c)) {
            for (int i = 0; i < hiddenWord.size(); i++) {
                if (c.charAt(0) == s.getLine().charAt(i)) {
                    hiddenWord.add(i, c.charAt(0));
                    hiddenWord.remove(i + 1);
                    key=true;
                }
            }
            if(!key) life.reduce();
            life.printLife();
        }

    }

    
    //return true otan prepei na teleiosei to game
    public boolean checkWord() {
        if(life.getCurrent()<=0)
        {
            return true;
        }
        for (int i = 0; i < hiddenWord.size(); i++) {
            if (hiddenWord.get(i) != s.getLine().charAt(i)) {
                return false;
            }

        }
        return true;
    }

}
