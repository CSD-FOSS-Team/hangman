/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication1;

/**
 *
 * @author xrica_vabenee
 */
public class Life {

    private int max;
    private int current;

    public Life(int max) {
        this.max = max;
        current = max;
    }

    public void reduce() {
        current--;
    }

    public int getCurrent() {
        return current;
    }

    public void printLife() {
        System.out.println("Lives: " + current);

    }

}
