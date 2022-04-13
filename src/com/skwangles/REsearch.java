package com.skwangles;

//Alexander Stokes - 1578409 & Rowan Thorley - 1560315
//Rowan Developed this

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class REsearch {
    Scanner scanner;
    File fileObject;
    ArrayList<SearchState> FSMlist;
    String wildcardPrintout = "__";
    char wildCardChar = '\t';
    char branchStateChar = '\0';

    public static void main(String[] args) {//WRITE ANY PROGRAM CODE IN THE 'searchFile' FUNCTION!!!
        REsearch res = new REsearch();
        res.programRun(args[0]);//Pass file name to function
    }

    public void programRun(String filename){
        FSMlist = new ArrayList<>();
        try {
            fileObject = new File(filename);
            scanner = new Scanner(fileObject);
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred with the file read in.");
            e.printStackTrace();
        }
        //--------------------^^ CONFIG ^^---------------------------
        populateStatesList();//reads the values from System.in into the states list as objects
        //
        //--------PROGRAM CODE START HERE------
        //
        while (scanner.hasNextLine()) {
            scanner.nextLine();
            //Can call searching here, or do whatever with this
        }



        //-----------END OF PROGRAM----------
        scanner.close();
    }













    //
    //--------STATE READ IN CODE---------
    //#region
    public void addState(char sym, int n1, int n2){
        FSMlist.add(new SearchState(sym, n1,n2));
    }

    public void addBranchState(int n1, int n2){
        FSMlist.add(new SearchState('\0', n1,n2));
    }

    public void populateStatesList(){
        try {
            Scanner stateIn = new Scanner(System.in);
            while(stateIn.hasNextLine()){
                String readIn = stateIn.nextLine();
                String[] newState = readIn.split("\s");
                if(newState.length < 4){ //This is a branch state
                    addBranchState(Integer.parseInt(newState[1]), Integer.parseInt(newState[2]));
                }else {
                    addState((Objects.equals(newState[3], wildcardPrintout) ? wildCardChar : newState[3].toCharArray()[0]), Integer.parseInt(newState[1]), Integer.parseInt(newState[2]));
                    //Check for wildcard, otherwise add character with the 2 states
                }
            }
        }
        catch (Exception e){
            System.out.println("Error occurred in the state reading");
            System.exit(1);//Error quit
        }
    }
//#endregion

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

    public String toString(){
        return "[" + (symbol == '\0' ? "BRANCH" : (symbol == '\t' ? "WILDCARD" : symbol)) + ", " + n1 + ", " + n2 + "]";
    }
}