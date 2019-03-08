/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csdfossteam.hangman.core;

import java.io.IOException;
import java.util.Hashtable;

import com.csdfossteam.hangman.face.cli.DemoCLI;
import com.csdfossteam.hangman.face.gui.HangmanGUI;
import com.csdfossteam.hangman.net.HangmanLANClient;
import com.csdfossteam.hangman.net.HangmanLANServer;
import javafx.scene.control.Alert;


/**
 * print
 *
 * @author xrica_vabenee, nasioutz
 */
public class Handler {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */

    public static int defaultPort = 7654;

    private DemoCLI cli;
    private HangmanGUI gui;

    private Thread gameEngineThread;
    private inputString inputBuffer;
    private Hashtable<String,Object> gameConfig;
    private Hashtable<String,Object> gameState;
    private GameEngine game;
    private HangmanLANServer localServer;
    private HangmanLANClient localClient;

    /**
     * Game Handler Constructor
     */
    public Handler () throws IOException {
        gameEngineThread = Thread.currentThread();
        inputBuffer = new inputString();
        game = new GameEngine();
        localClient = null;
        localServer = null;
        cli = new DemoCLI();
        gui = HangmanGUI.startGUIThread();
    }

    /**
     * Main Method that Initiates Play
     * @throws IOException
     * @throws InterruptedException
     */
    public void start() throws Exception {


        do {

            /*---- SETTINGS PART ----*/
            //Start the Settings Window
            gameConfig = gui.config();

            /*------ GAME PART ------*/
            //Choose the appropriate loop for each game configuration
            if ((boolean)gameConfig.get("isClient"))
                runClientLoop();
            else
                runHostLoop();

        }
        while (!(boolean) gameConfig.get("exit")) ;

        gui.terminate();

    }

    /**
     * Executes a loop running both for local and remote initiations, update, and inputs
     * Default server is always innitiated as empty to avoid null pointer exceptions when running through remove client list
     * @throws IOException
     * @throws InterruptedException
     */
    private void runHostLoop() throws IOException, InterruptedException {
        //Initialize game parameters that are outside the constructor?
        gameState = game.init(gameConfig);

        //Transfer local network object
        localServer = (HangmanLANServer) gameConfig.getOrDefault("localNetwork",null);
        gameConfig.remove("localNetwork");

        try {
            //Start Game Window GUI
            if (game.play()) {
                gui.initGame(gameConfig, gameState);
                for (int i = 0; i < localServer.getClientNumber(); i++) {
                    localServer.sendToClient(i, "init");
                    localServer.sendObjectToClient(i, gameConfig);
                    localServer.sendObjectToClient(i, gameState);
                }
            } //cli.init(gameConfig,gameState);

            while (game.play()) {

                //Get input from Interface
                if (game.remotePlayer() >= 0) {

                    localServer.sendToClient(game.remotePlayer(), "turn");
                    inputBuffer.set(localServer.receiveFromClient(game.remotePlayer()));

                } else
                    gui.input(inputBuffer); //cli.input(inputBuffer);

                //Transfer user input to game engine
                game.inputLetter(inputBuffer.get());

                //Update User Interface

                for (int i = 0; i < localServer.getClientNumber(); i++) {
                    localServer.sendToClient(i, "update");
                    localServer.sendObjectToClient(i, gameState);
                }
                gui.update(gameState); //cli.update(game.gameState());
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
            gui.endOnMainThread();
            gui.runAlert(Alert.AlertType.WARNING,"Connection Error","Problem Connecting to the Client(s)","The Game was terminated and you're returning to the main screen\n\nDetails: "+e,"exit-white.png");
        }

        localServer.freeClients();
    }

    /**
     * Implements a client-type loop that only initiates the gui, updates it and sends input to host
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws java.lang.InterruptedException
     */
    private void runClientLoop() {


        localClient = (HangmanLANClient) gameConfig.get("localNetwork");
        gameConfig.remove("localNetwork");

        String signal;
        try{

            do {
                gui.waitSplashScreen(true);
                signal = localClient.receiveFromServer();
                gui.waitSplashScreen(false);
                if (signal.equals("init")) {
                    gameConfig = (Hashtable) localClient.receiveObjectFromServer();
                    gameState = (Hashtable) localClient.receiveObjectFromServer();
                    gui.initGame(gameConfig, gameState);

                }
            }while (!signal.equals("init")) ;

            while ((boolean) gameState.get("play") && !gui.terminationRequested()) {
                signal = localClient.receiveFromServer();
                if (signal.equals("update")) {
                    gameState = (Hashtable) localClient.receiveObjectFromServer();
                    gui.update(gameState);
                } else if (signal.equals("turn")) {
                    gui.input(inputBuffer);
                    localClient.sendToServer(inputBuffer.get());
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
            gui.runAlert(Alert.AlertType.WARNING,"Connection Error","Problem Connecting to the Host","The Game was terminated and you're returning to the main screen\n\nDetails: "+e,"exit-white.png");
        }

        localClient.freeClient();

    }
}
