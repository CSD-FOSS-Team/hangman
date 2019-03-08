/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csdfossteam.hangman.core;

import com.csdfossteam.hangman.net.HangmanLANServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.BindException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;

/**
 * Game Engine Class
 * Handles the words, lifes and letters of the player(s)
 *
 * @author xrica_vabenee, nasioutz
 */
public class GameEngine {

    private WordDictionary words;
    private int winnerIndex;
    Hashtable<String,Object> gameConfig;
    Hashtable<String,Object> gameState;
    ArrayList<Player> playerList;
    private int playerIndex;


    /**
     * Simple Contructor for GameEngine
     */
    public GameEngine()
    {
        gameState = new Hashtable<String,Object>();
        playerList = new ArrayList<>();
        playerIndex = 0;
        winnerIndex = -1;
    }

    /**
     * Add Players to Player List from a Similar List
     * @param plList
     */
    public void addPlayersList(ArrayList<Player> plList )
    {
        for(int i=0; i<plList.size(); i++)
        {
            playerList.add(plList.get(i));
        }
    }



    /**
     * Take the configuration and create what's needed.
     * Currently:
     * <b>Making a dashed word from the WordDictionary</b>
     *
     * @param config
     * @throws IOException
     */
    public Hashtable<String,Object> init(Hashtable<String,Object> config) throws IOException
    {
        gameState.compute("play",(k,v) -> !(boolean) config.get("exit"));
        if (play()){
        addPlayersList((ArrayList<Player>) config.get("playerList"));
        for (Player p : playerList) p.reset();
        words = new WordDictionary((String)config.get("dict_path"));
        words.pickRandomWord();
        words.createDashes(true);
        gameConfig = config;
        gameState.put("hiddenWord",words);
        gameState.put("test-bool",true);
        gameState.put("playerList", playerList);
        gameState.put("playerIndex", playerIndex);
        gameState.put("winnerIndex", winnerIndex);
        }
        return gameState;
    }

    /**
     * Moves the playerIndex to the next player in cyclical fashion
     */
    private void changePlayerIndex()
    {
        if( (int) gameState.get("playerIndex") == ((ArrayList<Player>) gameConfig.get("playerList")).size() -1 || playerIndex<0)
        {
            gameState.computeIfPresent("playerIndex",(k,v) -> 0);
        }else
        {
            gameState.computeIfPresent("playerIndex",(k,v) -> (int) gameState.get("playerIndex") + 1 );
        }

    }

    /**
     * Checks if character is valid
     * @param c
     * @return boolean
     */
    public boolean checkChar(String c)
    {
        if(c.isEmpty())
        {
            return false;
        }
        char charAt0 = c.charAt(0);
        if (Character.isLetter(charAt0) && c.length() == 1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Registers the String input from the user and modifies game status
     * @param c
     */
    public void inputLetter(String c) {try{

        c = c.toLowerCase();
        boolean key=false;
        if (checkChar(c)) 
        { 
            for (int i = 0; i < words.getCurrentHidden().size(); i++)
            {
                if (c.charAt(0) == words.getCurrentString().charAt(i))
                {
                    words.getCurrentHidden().add(i, c.charAt(0));
                    words.getCurrentHidden().remove(i + 1);
                    key=true;
                }
            }
            
            if(!key && !((ArrayList<Player>)gameState.get("playerList")).get((int)gameState.get("playerIndex")).hasLetter(c.charAt(0)))
            {
                ((ArrayList<Player>)gameState.get("playerList")).get((int)gameState.get("playerIndex")).reduceLifes(c.charAt(0));
                changePlayerIndex();
            }
        }

        updateGameStatus();

    } catch (NullPointerException e) {updateGameStatus();}}



    /**
     * Check whether the Game should continue
     */
    public boolean play()
    {
        return (boolean) gameState.get("play");
    }

    /**
     *  Forcefully Terminate the Game Status
     */
    public void terminatePlay()
    {
        gameState.computeIfPresent("play",(k,v) -> false);
    }

    /**
     * Updates game parameters
     * @return boolean
     */
    public void updateGameStatus()
    {
        gameState.computeIfPresent("play",(k,v) -> !checkLosser() && !checkWord() && !(boolean) gameConfig.get("exit"));
    }

    public boolean checkLosser()
    {
        for (Player player : (ArrayList<Player>) gameState.get("playerList"))
        {
            if (player.getLifes().getCurrent() <= 0)
            {
                winnerIndex = 0;
                return true;
            }

        }
        return false;
    }

    /**
     * Confirm if the word is completed
     * @return boolean
     */
    public boolean checkWord() {

        for (int i = 0; i < words.getCurrentHidden().size(); i++) {
            if (words.getCurrentHidden().get(i) != words.getCurrentString().charAt(i)) {
                return false;
            }

        }
        gameState.computeIfPresent("winnerIndex", (k,v) -> gameState.get("playerIndex"));
        return true;
    }

    /**
     * Return a Hashtable containing all info needed to be communicated to UI and Sockets
     * @return Hashtable<String,Object>
     */
    public Hashtable<String,Object> gameState()
    {
        return gameState;
    }

    /**
     * Returns information on the whether the current player is a remote (LAN) player
     * @return (int) -1 if player is local, her "remote index" if remote (which corresponds to the client thread index hopefully)
     */
    public int remotePlayer()
    {
        return ((ArrayList<Player>)gameState.get("playerList")).get((int)gameState.get("playerIndex")).getRemoteIndex();
    }


    /**
     * Returns a default configuration for quick game
     *
     * <b>reference for what the UI classes need to implement<b/>
     *
     * @return Hashtable<String,Object>
     * @throws IOException
     */
    public static Hashtable<String,Object> defaultConfig() throws Exception {

        Hashtable<String,Object> configuration = new Hashtable<String,Object>();
        configuration.put("dict_path",WordDictionary.getDictionaries()[0].toString());
        configuration.put("exit",false);
        ArrayList<Player> list = new ArrayList<>();
        list.add(new Player("player1"));
        configuration.put("playerList",list);
        try{configuration.put("localNetwork",new HangmanLANServer(Handler.defaultPort));}
        catch(BindException be) {configuration.put("localNetwork",new HangmanLANServer(Handler.defaultPort));}
        configuration.put("isClient",false);

        return configuration;
    }


    }
