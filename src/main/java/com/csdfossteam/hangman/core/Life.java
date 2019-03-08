/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csdfossteam.hangman.core;

import java.io.Serializable;

/**
 * A Simple Class Implementing the players Lifes
 *
 * Note: Made serializable for socket handling
 * @author xrica_vabenee
 */
public class Life implements Serializable {

    public static int MAX = 6;
    private int max;
    private int current;

    public Life() {
        this.max = MAX;
        current = max;
    }

    public void reduce() {
        current--;
    }

    public int getCurrent() {
        return current;
    }
    
    public String getCurrentString()
    
    {
        return String.valueOf(current);
    }

    public void printLife() {
        System.out.println("Lives: " + current);

    }

}
