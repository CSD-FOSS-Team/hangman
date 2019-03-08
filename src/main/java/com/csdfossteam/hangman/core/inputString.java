package com.csdfossteam.hangman.core;

/**
 * A Simple wrapper class for a String
 */
public class inputString
{
    private String[] array;
    public inputString()
    {
        array = new String[1];
    }
    public inputString(String s)
    {
        array = new String[1];
        array[0] = s;
    }

    public inputString(inputString inSr)
    {
        array = new String[1];
        array[0] = inSr.get();
    }

    public void set(String s)
    {
        array[0] = s;
    }
    public String get()
    {
        return array[0];
    }
}
