/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csdfossteam.hangman;


import com.csdfossteam.hangman.core.Handler;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Paths;


/**
 * HangMan main Class
 * @author nasioutz
 */
public class HangMan
{

    public static Object hangman;

    public static void main(String[] args) throws Exception {
        hangman = new Handler();
        
        ((Handler) hangman).start();
    }
}

