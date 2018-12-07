/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Stream;

/**
 *
 * @author xrica_vabenee
 */
public class SelectWordClass {

    private String st;
    private File f;
    private BufferedReader br;
    private int listLength;
    private Random rand;
    private int select;
    private String line;
    private ArrayList<Character> a;

    public SelectWordClass() throws FileNotFoundException {
        st = new String();
        f = new File("/home/xrica_vabenee/NetBeansProjects/hangman_project/src/words.txt");
        br = new BufferedReader(new FileReader(f));
        listLength = 0;
        rand = new Random();
        line = new String();
        a = new ArrayList<Character>();
    }

    public void countWordList() throws IOException {
        while ((st = br.readLine()) != null) {
            //System.out.println(st);
            listLength++;
        }
    }

    public void pickRandomWord() throws IOException {
        select = rand.nextInt(listLength);
        // System.out.println(select);
        Stream<String> lines = Files.lines(Paths.get("/home/xrica_vabenee/NetBeansProjects/hangman_project/src/words.txt"));
        line = lines.skip(select).findFirst().get().toLowerCase();
       //System.out.println("The word which was selected is: "+line);

    }

    public String getLine() {
        return line;
    }

    public void printArrayList(ArrayList<Character> inputArray) {
        for (int i = 0; i < inputArray.size(); i++) {
            System.out.print(inputArray.get(i) + " ");
        }
    }

    public ArrayList<Character> getArrayList() {
        return a;
    }

    public void createDashes(boolean helpfulVersion) {
        if (helpfulVersion) {
            char firstLetter = line.charAt(0);
            a.add(firstLetter);
           // System.out.print(firstLetter);

            for (int i = 1; i < line.length() - 2; i++) {
                if (line.charAt(i) == firstLetter) {
                    a.add(firstLetter);
                } else {
                    a.add('_');
                }
            }
        } else {
            for (int i = 0; i < line.length() - 2; i++) {
                a.add('_');
            }
        }
    }

}
