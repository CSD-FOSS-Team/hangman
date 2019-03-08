/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csdfossteam.hangman.core;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Stream;

/**
 *
 * @author xrica_vabenee, nasioutz
 */
public class WordDictionary implements Serializable{

    private String st;
    private String file_path;
    private int listLength;
    private String current;
    private ArrayList<Character> currentHidden;

    public WordDictionary(String dict_path) throws IOException
    {
        file_path = dict_path;
        listLength = 0;
        current = new String();
        currentHidden = new ArrayList<Character>();

        countWordList();
    }

    private void countWordList() throws IOException 
    {
        File f = new File(file_path);

        BufferedReader br = new BufferedReader(new FileReader(f));
        
        while ((st = br.readLine()) != null) {
            listLength++;
        }
    }
    
    public void changeFile(String dict_file) throws IOException
    {
        file_path = Paths.get(new java.io.File( "." ).getCanonicalPath(), "data","dictionaries",dict_file).toString();
        
        countWordList();   
    }

    public void pickRandomWord() throws IOException
    {
        Random rand = new Random();
        int select = rand.nextInt(listLength);
        Stream<String> lines = Files.lines(Paths.get(file_path), Charset.forName("UTF-8"));

        current = lines.skip(select).findFirst().get().toLowerCase();

    }

    public String getCurrentString()
    {
        return current;
    }

    public ArrayList<Character> getCurrentHidden()
    {
        return currentHidden;
    }
    
    public String getCurrentHiddenString()
    {
        return getArrayListToString(currentHidden);
    }

    public void createDashes(boolean helpfulVersion)
    {
        if (helpfulVersion)
        {
            char firstLetter = current.charAt(0);
            currentHidden.add(firstLetter);

            for (int i = 1; i < current.length() - 1; i++)
            {
                if (current.charAt(i) == firstLetter)
                {
                    currentHidden.add(firstLetter);
                } 
                else
                {
                    currentHidden.add('_');
                }
            }

            currentHidden.add(current.charAt(current.length()-1));
         
        }
        else 
        {
            for (int i = 0; i < current.length() - 2; i++) 
            {
                currentHidden.add('_');
            }
        }
    }

    public static void printArrayList(ArrayList<Character> inputArray)
    {
        for (int i = 0; i < inputArray.size(); i++)
        {
            System.out.print(inputArray.get(i) + " ");
        }
    }

    public static String getArrayListToString(ArrayList<Character> inputArray)
    {
        StringBuilder sb = new StringBuilder();
        for (Character s : inputArray)
        {
            sb.append(s);
            sb.append(" ");
        }
        return sb.toString();
    }

    public static File[] getFiles(String dirName,String file_type)
    {
        File dir = new File(dirName);

        return dir.listFiles (new FilenameFilter() {
            public boolean accept(File dir, String filename)
            { return filename.endsWith(file_type); }
        } );
    }


    public static File[] getDictionaries() throws Exception
    {
        String dirName = Paths.get(new java.io.File( "." ).getCanonicalPath(), "data","dictionaries").toString();
        return getFiles(dirName,".txt");
    }

 
}
