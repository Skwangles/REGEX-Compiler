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
    int mark = 0;
    int point = 0;

    public static void main(String[] args) {
        REsearch res = new REsearch();
        res.init(args[0]); //Pass file name to function
        res.search(); //Search
    }

    public void init(String filename) {
        FSMlist = new ArrayList<>();
        getStates(); //Populates FSMlist with System.in

        //Read File as string
        try {
            Path filepath = Path.of(filename);
            search_text = Files.readString(filepath);
            System.out.println("Searching in: " + search_text);
        }
        catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public void search() {
        boolean found = false;
        boolean end = false;
        searchQueue.add(scan); //Add scan in center of deque

        //For each starting character of the search text
        while (mark < search_text.length() && !found && !end) {

            searchQueue.addFirst(1); //Add Start state to stack

            while (searchQueue.peek() != scan) {  //For everything on top of the scan
                //System.out.println(searchQueue);
                int index = searchQueue.pop();
                if (index == -1) { //Final State is -1
                    found = true;
                    end = true;
                }
                else if (!visited.contains(index)) {
                    visited.add(index);
                    addNext(index); //Add next states for the index
                }
            }

            //Match Couldn't be found at mark, reset and increment mark
            if (searchQueue.peekLast() == scan) {
                //System.out.println("Moving on");
                visited.clear();
                point = 0;
                mark++;
            }
            //Otherwise there is a possible next state
            //Move the next states to current states & increase point
            else {
                //System.out.println("Possible next!");
                move_scan();
                point++;
            }
        }

        //Reached end of search
        if (end && !found) {
            end = true;
            System.out.println("Reached end of input");
        }
        else if (found) {
            System.out.println("Found match at: " + mark);
        }
    }

    private void addNext(int index) {
        //If the symbol at index is the symbol required at the mark
        System.out.println("COMPARE: " + search_text.charAt(mark + point) + " " + FSMlist.get(index).symbol);
        if (search_text.charAt(mark + point) == FSMlist.get(index).symbol || FSMlist.get(index).symbol == branchChar) {
            searchQueue.addLast(FSMlist.get(index).n1);
            searchQueue.addLast(FSMlist.get(index).n2);
            //if (!FSMlist.get(index).nextEqual()) searchQueue.addLast(FSMlist.get(index).n2); //Add the second possible state if its different
        }

        System.out.println(searchQueue);
    }

    private void move_scan() { //Move the scan to the bottom
        searchQueue.addLast(searchQueue.removeFirst());
        System.out.println(searchQueue);
    }

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
            FSMlist.forEach(System.out::println);   //Prints the contents as a string
        }

        catch (Exception e) {
            System.out.println("Error occurred in the state reading");
            System.exit(1);
        }

    }

}

//State class holds all of the individual info of the States in a FSM
class SearchState {
    char symbol;
    int n1;
    int n2;

    //Used to hold the State values as they are parsed for the compiler, can also be used by the search
    public SearchState(char symbol, int n1, int n2) {
        this.symbol = symbol;
        this.n1 = n1;
        this.n2 = n2;
    }

    public boolean nextEqual() {
        return n1 == n2;
    }

    public String toString() {
        //return "[" + symbol + ", " + n1 + ", " + n2 + "]";
        return "[" + (symbol == '\0' ? "BRANCH" : (symbol == '\t' ? "WILDCARD" : symbol)) + ", " + n1 + ", " + n2 + "]";
    }
}