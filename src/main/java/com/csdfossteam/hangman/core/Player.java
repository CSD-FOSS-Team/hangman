/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csdfossteam.hangman.core;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A Simple class implementing the player entity
 *
 * Note: Made serializable for socket handling
 * @author User
 */
public class Player implements Serializable {


    public static final long serialVersionUID = -2710817129268002120L;

    private String name;
    private String remoteTag;
    private Life lifes;
    private ArrayList<Character> wrongLetters;
    private String letter;
    private int remoteIndex;


    public Player(String nm, int remote){
        this(nm);
        remoteIndex = remote;
        remoteTag = "Remote";
    }
    public Player(String nm){
        name = nm;
        lifes=new Life();
        wrongLetters=new ArrayList<>();
        remoteIndex = -1;
        remoteTag = "Local";
    }
    
    public Life getLifes(){
        return lifes;
    }

    public String getName() {return name;}
    
    public ArrayList<Character> getLetters()
    {
        return wrongLetters;
    }

    public boolean hasLetter(char letter)
    {
        return wrongLetters.contains(letter);
    }

    public void reset()
    {
        lifes = new Life();
        wrongLetters = new ArrayList<>();
    }

    public void reduceLifes(char letter)
    {
        lifes.reduce();
        if (!hasLetter(letter))
        {wrongLetters.add(letter);}
    }

    public int getRemoteIndex()
    {
        return remoteIndex;
    }

    public void setRemoveIndex(int i) {remoteIndex=i;}

    public String getRemoteTag() {return remoteTag;}

    public static ArrayList<Player> refreshNetworkIndexes(ArrayList<Player> pList)
    {
        int last_player_index = 0;
        for (int i=0 ; i < pList.size(); i++)
        {
            if (pList.get(i).getRemoteIndex() >= 0 && Math.abs(pList.get(i).getRemoteIndex() - last_player_index) > 1)
            {
                for (int j=i; j < pList.size(); j++)
                {
                    pList.get(j).setRemoveIndex(pList.get(j).getRemoteIndex() - 1);
                }
            }
        }

        return pList;
    }

}
