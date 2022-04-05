package com.skwangles;

import java.util.ArrayList;
import java.util.Objects;

//Alexander Stokes - 1578409 & Rowan Thorley -
//Alexander Developed this

//--Factor is a literal, term is something that could be part of a whole i.e. (a+b) could be a part of (a+b)/2
//State or FSM means a labelled item in a FSM table that has 1. a state, 2. two next states - n1, n2 - points to the next index in the whole model
public class REcompile {
    // static State[] output;
    int currstate = 0;//State number starts at 0,
    int nextChar = 0;
    ArrayList<Character> symbol = new ArrayList<>();
    ArrayList<Integer> next1 = new ArrayList<>();
    ArrayList<Integer> next2 = new ArrayList<>();
    char[] regexpattern;
    public static void main(String[] args) {
        String regex = args[0];//Reads in the regex pattern into an easily parse-able set.
       // output = new State[regex.length]; //Creates array of the max space the regex could be (100% concat) and has the state-number, symbol/branchindicator & next 2 states
        //" are removed automatically
        REcompile compile = new REcompile();
        compile.parse(regex);
    }

    public void parse(String regex){
        regexpattern = regex.toCharArray();
        addState('\0', 1,1);//Starting value
        expression();
        addState('\0',0,0);//Finishing state
    }


   public int expression()
    {
        int ret = term();
        if(isVocab(regexpattern[nextChar]) ||regexpattern[nextChar]=='(') expression();//Concatenate the following expression
        return ret;//Returns the pointer to the start of this particular branch
    }

    public int term()
    {
        //nextChar++ is used to 'consume' the operator
        //currstate++ is used to progress forward in the States table

        int heldState, savedNextState1,savedNextState2,prevState;//Prevents global versions being overwritten - Excess C notation

        prevState=currstate-1;//updates the prev state for this local
        savedNextState1 = factor();//Gets the next factor and returns the variable pointing to the start of the term

        if(regexpattern[nextChar]=='*'){
            addBranchState(currstate+1,savedNextState1);//Add branch state, pointing to the term, and to the item past it (t1 already is pointing to this branch)
            nextChar++;
            pointStateToCurrent(prevState);//point previous to the new branch state
            currstate++;//Advance the state counter - as a branchstate has been created
        }
        else if(regexpattern[nextChar]=='?'){
            addBranchState(currstate+1,savedNextState1);//Add branch state, pointing to the term, and to the item past it
            nextChar++;
            pointStateToCurrent(prevState);//point previous to the new branch state
            currstate++;
            pointStateToCurrent(savedNextState1);//Makes the 0 or 1'd literal point to the current (next open) state
        }
        else if(regexpattern[nextChar]=='+'){//a+ is equal to aa* - first create new literal then apply * to the new literal
            addState(symbol.get(savedNextState1), currstate +1, currstate +1 );//Creates identical literal as savedNextState
            prevState = savedNextState1;
            currstate++;
            addBranchState(currstate+1,currstate-1);//Add branch state, pointing to the term, and to the item past it (t1 already is pointing to this branch)
            nextChar++;
            pointStateToCurrent(prevState);//point previous to the new branch state
            currstate++;//Advance the state counter - as a branchstate has been created
        }
        else if(regexpattern[nextChar] == '|'){
            pointStateToCurrent(prevState);
            prevState=currstate-1;//Update the previous state
            nextChar++;//Consume the | character
            heldState=currstate;//Save the current state for after Term is run
            addBranchState(0,0);//place holder branch

            //Get the location of the next term's state -
            savedNextState2 = term();
            updateState(heldState,'\0',savedNextState1,savedNextState2);//Create branching state at the location of the 'heldState' pointing to the two different items
            pointStateToCurrent(prevState);//point
        }
        return savedNextState1;//Returns the factor location for the | state - used in nothing else
    }

   public int factor()//Consumes a literal - i.e. it can be assumed on return the nextchar is either an operator or an expression to concatenate
    {
        int heldState = currstate;

        if(isVocab(regexpattern[nextChar])){
            heldState = currstate;
            addState(regexpattern[nextChar],currstate+1,currstate+1);
            nextChar++;//Consume a factor
        }
        else if(regexpattern[nextChar]=='('){
            nextChar++;//Consume bracket
            heldState = expression();//Process the internals of the ()
            if(regexpattern[nextChar]==')')//If the very next char is NOT a close bracket, then the parsing fails
                nextChar++;//Consume the ')'
            else
                error();
        }
        else
            error();
        return heldState;
    }


    public boolean isVocab(char s){//Checks if the variable is a literal within the scope of the project - i.e. acceptable ascii
        if(s == '\\'){
            nextChar++;//Program will work with next value as literal
            //Checks that the char is within the ascii of ! until ~
            return true;
        }
        else if((int)s >= 33 && (int) s <= 126){//Checks that the char is within the ascii of ! until ~
            //is it an operator
            return !"*()[]?+.".contains("" + s);
        }
        return false;//Either is outside the ascii bounds OR is an unescaped special character
    }

    //
    //----------------------STATE FUNCTIONS-----------------------
    //

    public void pointStateToCurrent(int stateIndex){//repoint the input FSM to the current state, but does it differently based on the type of state
        if(Objects.equals(next1.get(stateIndex), next2.get(stateIndex)))
            next2.set(stateIndex, currstate);//If previous is Literal point both to next
        next1.set(stateIndex, currstate);//if previous is branching state - point 1 'next' to the currentstate
    }

    public void addState(char sym, int n1, int n2){
        symbol.add(sym);
        next1.add(n1);
        next2.add(n2);
        currstate++;
    }

    public void addBranchState(int n1, int n2){
        symbol.add('\0');
        next1.add(n1);
        next2.add(n2);
        currstate++;
    }

    public void updateState(int state, char sym, int n1, int n2){
        symbol.set(state, sym);
        next1.set(state, n1);
        next2.set(state, n2);
    }


    //
    //---------------------ERROR FUNCTION--------------------
    //
    public void error(){
        System.out.println("Error occurred in the Program");
        System.exit(1);
    }
}



// Priority - \ first, () next, * + and ?, concatenation, finally | or []

//State class holds all of the individual info of the States in a FSM
class State {
    String symbol = null;
    int n1 = -1;
    int n2 = -1;
    //Used to hold the State values as they are parsed for the compiler, can also be used by the search
    public State(String symbol, int n1, int n2) {
        this.symbol = symbol;
        this.n1 = n1;
        this.n2 = n2;
    }
}
