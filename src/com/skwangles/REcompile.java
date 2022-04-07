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
    ArrayList<State> FSMlist = new ArrayList<>();
    char[] regexpattern;
    public static void main(String[] args) {
        String regex = args[0];//Reads in the regex pattern into an easily parse-able set.
        REcompile compile = new REcompile();
        compile.parse(regex);
    }

    public void parse(String regex){
        regexpattern = regex.toCharArray();
        addState('\0', 1,1);//Starting value
        expression();
        addState('\0',0,0);//Finishing state pointing back to start

        for (int i = 0; i < FSMlist.size(); i++){
            State state = FSMlist.get(i);
            System.out.println(i + " " + (state.symbol == '\0' ? "~" : state.symbol) + " " + state.n1 + " " + state.n2);
        }
    }


    public int expression()
    {
        int heldState = currstate;//Start of the term
        int prevState = heldState-1;
        int concatRet = expressionConcat();

        if(nextChar >= regexpattern.length || concatRet == -1) return -1;//Return if finished

        if(regexpattern[nextChar] == '|'){
            pointStateToCurrent(prevState);//Point the value just before the concat
            prevState=currstate-1;//get the character just before the |
            nextChar++;//Consume the | character
            int branchheldState=currstate;//Save the current state so the below branch state can be modified later
            addBranchState(0,0);//placeholder branch
            int secondTermInDisjunction = term();//Get the location of the next term's state
            updateState(branchheldState,'\0',heldState,secondTermInDisjunction);//Create branching state at the location of the 'heldState' pointing to the two different items
            heldState = branchheldState;
        }
        if(expressionConcat() != -1){
            pointStateToCurrent(prevState);
        }
        return heldState;//Returns the pointer to the start of this particular expression
    }

    public int expressionConcat(){
        int startOfExpression = term();

        if(nextChar >= regexpattern.length || startOfExpression == -1) return -1;//Return if finished

        if(isVocab(regexpattern[nextChar]) ||regexpattern[nextChar]=='(') expressionConcat();//Concatenate the following expression
        return startOfExpression;
    }
    public int term()
    {
        //nextChar++ is used to 'consume' the operator
        //currstate++ is used to progress forward in the States table

        if(nextChar >= regexpattern.length) return -1;//Return -1 if finished
        int prevState=currstate-1;
        int startOfTermNumber = factor();

        if(nextChar >= regexpattern.length || startOfTermNumber == -1) return -1;

        if(regexpattern[nextChar]=='*'){
            pointStateToCurrent(prevState);//repoint to branch state
            addBranchState(currstate+1,startOfTermNumber);//Add branch state, pointing to the term, and to the item past it (t1 already is pointing to this branch)
            nextChar++;
        }
        else if(regexpattern[nextChar]=='?'){
            pointStateToCurrent(prevState);//point previous to the new branch state
            addBranchState(currstate+1,startOfTermNumber);//Add branch state, pointing to the term, and to the item past it
            nextChar++;
            pointStateToCurrent(startOfTermNumber);//Makes the 0 or 1'd literal point to the current (next open) state
        }
        else if(regexpattern[nextChar]=='+'){//a+ is equal to aa* - first create new literal then apply * to the new literal
            addState(FSMlist.get(startOfTermNumber).symbol, currstate +1, currstate +1 );//Creates identical literal as savedNextState
            prevState = startOfTermNumber;
            pointStateToCurrent(prevState);//point previous to the new branch state
            addBranchState(currstate+1,currstate-1);//Add branch state, pointing to the term, and to the item past it (t1 already is pointing to this branch)
            nextChar++;
        }
        return startOfTermNumber;//Returns the factor location for the | state - used in nothing else
    }

    public int factor()//Consumes a literal - i.e. it can be assumed on return the nextchar is either an operator or an expression to concatenate
    {
        int startOfFactor = currstate;
        if(isVocab(regexpattern[nextChar])){
            startOfFactor = currstate;
            addState(regexpattern[nextChar],currstate+1,currstate+1);
            nextChar++;//Consume a factor
        }
        else if(regexpattern[nextChar]=='('){
            nextChar++;//Consume bracket
            startOfFactor = expression();//Process the internals of the ()
            if(regexpattern[nextChar]==')')//If the very next char is NOT a close bracket, then the parsing fails
                nextChar++;//Consume the ')'
            else
                error();
        }
        else if(regexpattern[nextChar] == '['){
            nextChar++;
            //parse disjunction here - first make sure that concant and | occur in the right oder
            if(regexpattern[nextChar] == ']'){
                nextChar++;
            }
            else
                error();//Unmatched brackets
        }
        else
            error();
        return startOfFactor;
    }



    public boolean isVocab(char s){//Checks if the variable is a literal within the scope of the project - i.e. acceptable ascii
        if(s == '\\'){
            nextChar++;//Program will work with next value as literal
            //Checks that the char is within the ascii of ! until ~
            return true;
        }
        else if((int)s >= 33 && (int) s <= 126){//Checks that the char is within the ascii of ! until ~
            //is it an operator
            return !"*()[]?+|".contains("" + s);//All special chars, '.' is not included, as it is a wildcard
        }
        else{
            error();//Was an unusable char
        }
        return false;//Either is outside the ascii bounds OR is an unescaped special character
    }

    //
    //----------------------STATE FUNCTIONS-----------------------
    //

    public void pointStateToCurrent(int stateIndex){//repoint the input FSM to the current state, but does it differently based on the type of state
        if(Objects.equals(FSMlist.get(stateIndex).n1, FSMlist.get(stateIndex).n2)) {
            FSMlist.get(stateIndex).n2 = currstate;
        }

        FSMlist.get(stateIndex).n1 = currstate;
    }

    public void addState(char sym, int n1, int n2){

        FSMlist.add(new State(sym, n1,n2));
        currstate++;
    }

    public void addBranchState(int n1, int n2){
        FSMlist.add(new State('\0', n1,n2));
        currstate++;
    }

    public void updateState(int state, char sym, int n1, int n2){
        FSMlist.set(state, new State(sym, n1, n2));
    }


    //
    //---------------------ERROR FUNCTION--------------------
    //
    public void error(){
        System.out.println("Error occurred in the Program");
      //  System.exit(1);
    }
}



// Priority - \ first, () next, * + and ?, concatenation, finally | or []

//State class holds all of the individual info of the States in a FSM
class State {
    char symbol;
    int n1;
    int n2;
    //Used to hold the State values as they are parsed for the compiler, can also be used by the search
    public State(char symbol, int n1, int n2) {
        this.symbol = symbol;
        this.n1 = n1;
        this.n2 = n2;
    }
}
