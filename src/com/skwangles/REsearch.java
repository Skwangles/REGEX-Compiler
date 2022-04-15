package com.skwangles;

//Alexander Stokes - 1578409 & Rowan Thorley - 1560315
//Rowan Developed this

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import java.util.Deque;
import java.util.ArrayDeque;

public class REsearch {
    ArrayList<SearchState> FSMlist;
    ArrayList<Integer> visited = new ArrayList<Integer>();
    Deque<Integer> searchQueue = new ArrayDeque<>();

    String wildcardPrintout = "__";
    String search_text;
    char wildCardChar = '\t';
    char branchChar = '\0';

    int scan = -2;
    int end_state = -1;
    int mark = 0;
    int point = 0;

    public static void main(String[] args) {
        REsearch res = new REsearch();
        res.init(args[0]); //Initialize: Read in file to search and populate FSM list
        res.search(); //Search
    }

    public void init(String filename) {
        FSMlist = new ArrayList<>();
        getStates(); //Populates FSMlist with System.in

        try { //Read in file as string
            Path filepath = Path.of(filename);
            search_text = Files.readString(filepath);
            System.out.println("Searching in: " + search_text);
        }
        catch (java.io.IOException e) { e.printStackTrace(); }
    }

    public void search() {
        boolean found = false;
        searchQueue.add(scan); //Add scan in center of deque

        //For each starting character of the search text
        while (mark < search_text.length() && !found) {

            searchQueue.addFirst(1); //Add Start state to stack

            //For each possible current state
            while (searchQueue.peek() != scan) {

                int index = searchQueue.pop();          //Pop the top state
                if (index == end_state) found = true;   //If its the end state, the string has been found

                //Otherwise, if the state hasn;t already been checked, see if theres any possible next states
                else if (!visited.contains(index)) {
                    visited.add(index); //State has been visited
                    addNext(index);     //Add next states for the index
                }
            }

            //No possible next states, reset and increment mark
            if (searchQueue.peekLast() == scan && !found) {
                visited.clear();
                point = 0;
                mark++;
            }
            //Otherwise there is a possible next state
            //Make the next states the possible current states & increase point
            else {
                move_scan();
                point++;
            }
        }

        //Reached end of search
        if (found)  {
            System.out.println("Found a match '" + search_text.substring(mark, mark + point - 1) + "' at " + mark);
        }
        else {
            System.out.println("Reached end of input :/");
        }
    }

    private void addNext(int index) {

        //If Branch state, add states as current states
        if (FSMlist.get(index).symbol == branchChar) {
            searchQueue.addFirst(FSMlist.get(index).n1);
            searchQueue.addFirst(FSMlist.get(index).n2);
        }
        //Other wise if the correct symbol or wildcard is found add as next states
        if (search_text.charAt(mark + point) == FSMlist.get(index).symbol || FSMlist.get(index).symbol == wildCardChar) {
            searchQueue.addLast(FSMlist.get(index).n1);
            searchQueue.addLast(FSMlist.get(index).n2);
        }

    }

    //Move the scan to the bottom
    private void move_scan() { searchQueue.addLast(searchQueue.removeFirst()); }

    //--------STATE READ IN CODE---------
    public void addState(char sym, int n1, int n2) { FSMlist.add(new SearchState(sym, n1,n2)); }

    public void addBranchState(int n1, int n2) { FSMlist.add(new SearchState('\0', n1,n2)); }

    public void getStates() {
        try {
            Scanner stateIn = new Scanner(System.in);
            while(stateIn.hasNextLine()){               //For each line of input

                String readIn = stateIn.nextLine();     //Read the line
                String[] newState = readIn.split(" ");  //Split at whitespace
                //If Branch State
                if(newState.length < 4) addBranchState(Integer.parseInt(newState[1]), Integer.parseInt(newState[2]));
                //Check for wildcard, otherwise add character with the 2 states
                else addState((Objects.equals(newState[3], wildcardPrintout) ? wildCardChar : newState[3].toCharArray()[0]), Integer.parseInt(newState[1]), Integer.parseInt(newState[2]));

            }
            System.out.println("FSM: Branch (B), Wildcard (W)");
            FSMlist.forEach(System.out::println);   //Prints the contents as a string
            System.out.println("");
        }

        catch (Exception e) {
            System.out.println("Error occurred in the state reading");
            System.exit(1);
        }

    }

}

class SearchState { //State class holds all of the individual info of the States in the FSM
    char symbol;
    int n1;
    int n2;

    //Used to hold the State values as they are parsed for the compiler, can also be used by the search
    public SearchState(char symbol, int n1, int n2) {
        this.symbol = symbol;
        this.n1 = n1;
        this.n2 = n2;
    }

    public String toString() { return "[" + (symbol == '\0' ? "B" : (symbol == '\t' ? "W" : symbol)) + ", " + n1 + ", " + n2 + "]"; }
}