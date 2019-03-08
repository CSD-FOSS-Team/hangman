/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csdfossteam.hangman.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;


/**
 *
 * @author dimosthenis Paradeisis, 3051
 */
public class valueWordMaker 
{
    private ArrayList<String> wordList;
    int[] letters;
    double[] letterP;
    int letterSum; 
    
    public valueWordMaker()
    {
        wordList = new ArrayList<>();
        letters = new int[26];
        letterP = new double[26];
        letterSum = 0;
    }
    
    /**
     * This class put in a arrayList the words with n letters and over.
     * @param f
     * @throws IOException 
     */
    public void listOfWordMaker(File f, int n) throws IOException
    {
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        String line;
        while((line = br.readLine()) != null)
        {
            String newWord = line.toLowerCase();
            
            if(newWord.length()>=n)
            wordList.add(newWord);
        }
                
    }
    
    /**
     * This class finds the probability of eatch other latter.
     */
    public void lattersMakerP()
    {
        for(int i=0; i<wordList.size(); i++)
        {
            String word01 = wordList.get(i);
            for(int j=0; j<word01.length(); j++)
            {
                int l =(int) (word01.charAt(j))-(int)('a');
                if(l>=0 && l<=26)
                {
                    letters[l]++;
                    letterSum++;
                }

            }
        }
        
        for(int i=0; i<26; i++)
        {
            letterP[i] = letters[i]*1.0/letterSum;
            //System.out.println(letterP[i]);
        }
    }
    
    /**
     * This class find the probability to find player a latter of word at first round.
     * @param word
     * @return 
     */
    public double probabilityForFirstLatter(String word)
    {
        HashSet<Integer> ar = new HashSet<>();
        for(int i=0; i<word.length();i++)
        {
            int l = (int) word.charAt(i) - (int) ('a');
            if(l>=0 && l<=26)
            {
                ar.add(l);
            }
        }
        double apotel=1;
        for(Integer let : ar)
        {
            apotel*=1-letterP[let];
        }
        apotel = 1-apotel;
        return apotel;
    }
    
    /**
     * Write the value of word. The value of word is the average of how many used the word and the propability of method probabilityForFirstLatter
     */
    public void printValueWords()
    {
        for(int i=0;i<wordList.size();i++)
        {
            double prob = probabilityForFirstLatter(wordList.get(i))/2  + (1-i/wordList.size())/2;
            System.out.println( /* wordList.get(i)  + "   " */+ prob );
        }
    }
  
    

}