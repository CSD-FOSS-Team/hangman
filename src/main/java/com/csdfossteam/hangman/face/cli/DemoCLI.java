/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csdfossteam.hangman.face.cli;

import com.csdfossteam.hangman.core.*;
import com.csdfossteam.hangman.net.HangmanLANClient;
import com.csdfossteam.hangman.net.HangmanLANServer;

import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.UUID;

/**
 *
 * @author nasioutz
 */
public class DemoCLI 
{
    private Scanner scan;
    private String select;
    private boolean configured;
    private int remotePlayers;

    
    public DemoCLI()
    {
        scan = new Scanner(System.in);
    }


    public Hashtable<String,Object> config () throws Exception {

        configured = false;
        Hashtable<String,Object> configuration = GameEngine.defaultConfig();
        HangmanLANServer localServer = null;
        HangmanLANClient localClient = null;

        do
        {

            do {
                clearConsole();
                System.out.println("\n---------------");
                System.out.println("1)Start Game");
                System.out.println("2)Join Game");
                System.out.println("3)Configure Game");
                System.out.println("4)Exit");
                System.out.print("\nMake a selection: ");
                select = scan.nextLine();
            } while (!isValidChoice(select,1,4));


            if (Integer.parseInt(select)==1)
            {
                return configuration;
            }
            else if (Integer.parseInt(select)==2)
            {
                String ip = null;
                int port = -1;
                boolean error = false;

                do {

                clearConsole();
                System.out.println("\n---------------");
                System.out.print("\nEnter Host Address [Format: \"IP:PORT\"]: ");


                try
                {select = scan.next();
                ip = select.split(":")[0];
                port = Integer.parseInt(select.split(":")[1]);}
                catch (Exception e1)
                {System.out.println("ERROR: "+e1);
                 error = true;}

                try
                {localClient = new HangmanLANClient(ip, port);}
                catch (Exception e2)
                {System.out.println("ERROR: "+e2);
                 error = true;}


                } while ((localClient == null) || error);



                System.out.println("\n---------------");
                System.out.println("Enter your Player Name: ");
                select = scan.nextLine();
                localClient.sendToServer(select);

                configuration.put("isClient",true);

                System.out.println("\n---------------");
                System.out.println("Keep Calm and Wait for you Host!");

                configuration.put("localNetwork",localClient);

                return configuration;



            }
            else if (Integer.parseInt(select)==3)
            {
                do {


                    do {

                        clearConsole();
                        System.out.println("\n---------------");
                        System.out.println("1)Add Player (selecting this will reset default players)");
                        System.out.println("2)Select Dictionary");
                        System.out.println("3)Back");
                        System.out.print("\nMake a selection: ");
                        select = scan.nextLine();

                    } while (!isValidChoice(select, 1, 4));

                    if (Integer.parseInt(select) == 1)
                    {
                        ArrayList<Player> list = new ArrayList<>();
                        do {

                            do {
                                System.out.println("\n---------------");
                                System.out.println("1)Local: ");
                                System.out.println("2)Remote: ");
                                System.out.println("3)Back");
                                System.out.print("\nMake a selection: ");
                                select = scan.nextLine();

                            } while (!isValidChoice(select, 1, 4));

                            if (Integer.parseInt(select) == 1)
                            {
                                System.out.println("\n---------------");
                                System.out.print("\nPlayer Name: ");
                                select = scan.nextLine();
                                list.add(new Player(select));
                            }
                            else if (Integer.parseInt(select) == 2)
                            {



                                if (localServer == null)
                                {
                                    localServer = new HangmanLANServer(Handler.defaultPort);
                                }

                                System.out.println("\n---------------");
                                System.out.println("Host IP: " + localServer.getServerIP());
                                System.out.println("Host Port: " + localServer.getServerPort());

                                System.out.println("Waiting for a Player to Show Up.");
                                localServer.findClient();
                                String name = localServer.receiveFromClient(localServer.getClientNumber() - 1);
                                list.add(new Player(name, localServer.getClientNumber() - 1));
                                System.out.println("\nPlayer " + name + " added to list. yay!");
                                configuration.put("isHost", true);
                                configuration.put("localNetwork",localServer);
                            }
                            else
                                break;

                            configuration.put("playerList", list);

                        } while (true);
                    }
                    else if (Integer.parseInt(select) == 2)
                    {
                        File[] dict_list = WordDictionary.getDictionaries();
                        do {
                            clearConsole();
                            System.out.println("\n---------------");
                            System.out.println("Available Dictionaries");
                            for (int i = 0; i < dict_list.length; i++) {
                                System.out.println((i + 1) + ")" + dict_list[i].getName().toUpperCase());
                            }
                            System.out.print("\nMake a selection: ");
                            select = scan.nextLine();
                        } while (!isValidChoice(select, 1, dict_list.length));

                        configuration.put("dict_path", WordDictionary.getDictionaries()[Integer.parseInt(select) - 1].toString());

                    }
                    else
                    {
                        if (configuration.containsKey("exit"))
                            configuration.computeIfPresent("exit", (k, v) -> false);
                        else
                            configuration.put("exit", false);
                        break;
                    }


                } while(true);
            }
            else if (Integer.parseInt(select)==4)
            {
                if (configuration.containsKey("exit"))
                    configuration.computeIfPresent("exit",(k,v)->true);
                else
                    configuration.put("exit",true);
                return configuration;
            }


        } while (!configured);
        return configuration;
    }

    public void init(Hashtable<String,Object> config,Hashtable<String,Object> state)
    {
        update(state);
    }

    public void input(inputString input)
    {
        System.out.println("--------------");
        System.out.print("give letter: ");
        String c = scan.nextLine();
        input.set(c);
    }

    public void update(Hashtable<String, Object> gameStatus)
    {
        System.out.println("\n---------------");
        System.out.println("Current Life: "+((Life) gameStatus.get("lifes")).getCurrentString());
        System.out.println("---------------");
        System.out.println(((WordDictionary)gameStatus.get("hiddenWord")).getCurrentHiddenString());
    }


    public static boolean isNumeric(String strNum) {
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

    public static boolean isValidChoice(String select,int range1,int range2)
    {
        return DemoCLI.isNumeric(select) && Integer.parseInt(select) >= range1 && Integer.parseInt(select) <= range2;
    }

    public final static void clearConsole()
    {
        try
        {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows"))
            {
                Runtime.getRuntime().exec("cls");
            }
            else
            {
                Runtime.getRuntime().exec("clear");
            }
        }
        catch (final Exception e)
        {
            //  Handle any exceptions.
        }
    }



}
