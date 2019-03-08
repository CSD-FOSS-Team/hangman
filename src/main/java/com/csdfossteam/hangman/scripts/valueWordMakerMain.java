/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csdfossteam.hangman.core;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author user
 */
public class valueWordMakerMain {
      
    public static void main(String[] args) throws IOException 
    {
        File file = new File("C:\\Users\\user\\Documents\\fossHangman\\hangman-master\\data\\dictionaries\\words02.txt");
        valueWordMaker valMaker = new valueWordMaker();
        valMaker.listOfWordMaker(file,7);
        valMaker.lattersMakerP();
        valMaker.printValueWords();
    }
}
