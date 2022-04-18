package com.skwangles;

//Alexander Stokes - 1578409 & Rowan Thorley - 1560315
//Rowan Developed this

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import java.util.ArrayDeque;

public class REsearch {
    ArrayList<SearchState> FSMlist = new ArrayList<SearchState>();
    ArrayList<Integer> visited = new ArrayList<Integer>();
    //ArrayDeque<Integer> searchQueue = new ArrayDeque<>();
    Deque searchQueue = new Deque();

    String search_text;
    String wildcardPrintout = "__";
    char wildCardChar = '\t';
    char branchChar = '\0';

    int scan = -2;
    int start_state = 0;
    int end_state = -1;
    int mark, point, line;

    boolean debug = false;
    boolean find_all = false;

    public static void main(String[] args) {
        REsearch res = new REsearch();
        String filename = "";

        //Check arguments
        for (String arg : args) {
            if (arg.charAt(0) == '-') {
                for (int i = 1; i < arg.length(); i++) {
                    switch (arg.charAt(i)) {
                        case 'a':
                            res.find_all = true;
                            break;
                        case 'd':
                            res.debug = true;
                            break;
                        default:
                            System.out.println("Unknown Argument: " + arg.charAt(i));
                    }
                }
            }
            else filename = arg;
        }

        ArrayList<String> fileLines = new ArrayList<String>();
        if (filename != "") {
            try { //Read in file lines to array
                Scanner scanner = new Scanner(new File(filename));
                while (scanner.hasNextLine()) {
                    fileLines.add(scanner.nextLine());
                }

                res.getStates(); //Populates FSMlist with System.in;

                for (int i = 0; i < fileLines.size(); i++){
                    res.search_text = fileLines.get(i);
                    res.line = i;
                    res.search();
                }
            }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

    public void search() {
        boolean found = false;
        mark = 0;
        point = 0;
        searchQueue.clear();
        searchQueue.addFirst(scan);   //Add scan to deque
        searchQueue.addFirst(0); //Add Start state to stack

        //For each starting character of the search text
        while (mark < search_text.length() && !found) {

            //For each possible current state
            int index = searchQueue.removeFirst(); //Pop the top state

            if (index == scan) {
                //If there are no current states but there are next states
                if (searchQueue.size() > 0) point++;

                //If there are no next or current states, reset and increase mark
                else {
                    point = 0;
                    mark++;
                    searchQueue.addFirst(0);
                }
                visited.clear();
                searchQueue.addLast(scan); //Add the scan at the end
            }

            //If we've got to the final state and theres no next states
            else if (index == end_state) {
                if (debug) System.out.println("Found a match '" + search_text.substring(mark, mark + point) +
                        "' on line " + line + ", " + search_text + " at index " + mark + " to " + (mark + point));
                else System.out.println(search_text);
                if (!find_all) found = true;
            }

            //Otherwise, if the state hasn't already been checked, see if theres any possible next states
            else if (!visited.contains(index) && index >= 0) {
                visited.add(index); //State has been visited
                addNext(index);     //Add next states for the index
            }
        }
    }

    private void addNext(int index) {
        //If Branch state, add states as current states
        if (FSMlist.get(index).symbol == branchChar) {
            searchQueue.addFirst(FSMlist.get(index).n1);
            if (!FSMlist.get(index).nextEqual()) searchQueue.addFirst(FSMlist.get(index).n2);
        }
        else {
            try {
                //Otherwise if the correct symbol or wildcard is found add as next states
                if (search_text.charAt(mark + point) == FSMlist.get(index).symbol || FSMlist.get(index).symbol == wildCardChar) {
                    searchQueue.addLast(FSMlist.get(index).n1);
                    if (!FSMlist.get(index).nextEqual()) searchQueue.addLast(FSMlist.get(index).n2);
                }
            }
            catch (java.lang.StringIndexOutOfBoundsException e) { } //Pointer went out of bounds
        }
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
                else addState((Objects.equals(newState[3], wildcardPrintout) ? wildCardChar : newState[3].toCharArray()[0]),
                        Integer.parseInt(newState[1]), Integer.parseInt(newState[2]));
            }

            if (debug) {
                System.out.println("FSM: Branch (B), Wildcard (W)");
                FSMlist.forEach(System.out::println);   //Prints the contents as a string
                System.out.println("");
            }
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

    public boolean nextEqual() { return n1 == n2; }
    public String toString() { return "[" + (symbol == '\0' ? "B" : (symbol == '\t' ? "W" : symbol)) + ", " + n1 + ", " + n2 + "]"; }
}

//Basically just a linked list with all the operations
//I originally implemented with the built in ArrayDeque to test
//then built this keeping the original method names
/*
addFirst() - add
addLast() - push
removeFirst()- pop
removeFirst() - remove
peekFirst()
peekLast()
 */
class Node
{
    int data;
    Node prev, next;

    static Node getnode(int data)
    {
        Node newNode = new Node();
        newNode.data = data;
        newNode.prev = newNode.next = null;
        return newNode;
    }
};

class Deque {
    Node top;
    Node bottom;
    int size;

    Deque()
    {
        top = bottom = null;
        size = 0;
    }

    boolean isEmpty() { return (top == null); }

    int size() { return size; }

    void addFirst(int data) {
        Node newNode = Node.getnode(data);

        if (newNode != null) {
            if (top == null) top = bottom = newNode;
            else {
                newNode.next = top;
                top.prev = newNode;
                top = newNode;
            }
            size++;
        }
    }

    void addLast(int data) {
        Node newNode = Node.getnode(data);

        if (newNode != null) {
            if (bottom == null) top = bottom = newNode;

            else {
                newNode.prev = bottom;
                bottom.next = newNode;
                bottom = newNode;
            }
            size++;
        }
    }

    int removeFirst() {
        Node temp = top;
        if (!isEmpty()) {
            top = top.next;
            if (top == null) bottom = null;
            else top.prev = null;

            size--;
        }
        return temp.data;
    }

    int removeLast() {
        Node temp = bottom;
        if (!isEmpty()) {
            bottom = bottom.prev;

            if (bottom == null) top = null;
            else bottom.next = null;

            size--;
        }
        return temp.data;
    }

    int peekFirst()
    {
        if (isEmpty()) return -1;
        return top.data;
    }

    int peekLast()
    {
        if (isEmpty()) return -1;
        return bottom.data;
    }

    public void clear()
    {
        bottom = null;
        while (top != null) {
            Node temp = top;
            top = top.next;
        }
        size = 0;
    }
}



