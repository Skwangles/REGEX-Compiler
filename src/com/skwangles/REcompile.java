package com.skwangles;

import java.util.ArrayList;
import java.util.Objects;

//Alexander Stokes - 1578409 & Rowan Thorley -
//Alexander Developed this

//--Factor is a literal, term is something that could be part of a whole i.e. (a+b) could be a part of (a+b)/2
//State or FSM means a labelled item in a FSM table that has 1. a state, 2. two next states - n1, n2 - points to the next index in the whole model
public class REcompile {
    int currstate = 0;//State number starts at 0,
    int nextChar = 0;
    char[] regexpattern;//Holds all the chars to be parsed/compiled
    ArrayList<State> FSMlist = new ArrayList<>();

    String specialChars = "*()[]?+|";//All special chars, '.' is not included, as it is a wildcard
    String wildcardPrintout = "__";
    String branchStatePrintout = "";

    public static void main(String[] args) {
        String regex = args[0];//Reads in the regex pattern into an easily parse-able set.
        REcompile compile = new REcompile();
        compile.parse(regex);
    }

    public void parse(String regex){
        regexpattern = changeSquareToSlash(regex).toCharArray();

        addState('\0', 1,1);//Starting state
        expression();
        if(nextChar < regexpattern.length) error();//Whole pattern was not parsed
        addState('\0',0,0);//Finishing state pointing back to start - Can be remove or pointed to -1

        for (int i = 0; i < FSMlist.size(); i++){
            State state = FSMlist.get(i);
            System.out.println(i + " " + state.n1 + " " + state.n2 + " " + (state.symbol == '\0' ? (state.symbol == '.' ? wildcardPrintout : branchStatePrintout) : state.symbol));
        }
    }

    public String changeSquareToSlash(String regex){//replaces the [] in a regex with the (a|b|c) version
        int fromIndex = 0;
        while(regex.indexOf("[", fromIndex) != -1){
            int startOfSub = regex.indexOf("[", fromIndex);
            if(regex.charAt(startOfSub-1) == '\\')
            {
                fromIndex = startOfSub+1;
                continue;
            }
            //If the [ is interpreted as a literal, skip it and advance the 'search from' index

            int endOfSub = regex.indexOf("]", fromIndex+2);//Start search from at least 2 past the [ to exclude the first literal (Could be a ])
            if(endOfSub == -1 || endOfSub - startOfSub <= 1){//There must be a ] otherwise there are unmatched brackets and there must be atleast 2 characters to be valid
                error();
            }
            char[] sub = regex.substring(startOfSub+1, endOfSub).toCharArray();
            StringBuilder cut = new StringBuilder("(\\" + sub[0]);
            for(int i = 1; i < sub.length; i++){
                cut.append("|\\").append(sub[i]);//Add each char with a | and \ to make sure it is read as a literal and is OR'ed.
            }
            cut.append(")");
            fromIndex = endOfSub;//Narrow the search
            regex = regex.substring(0, startOfSub) + cut + regex.substring(endOfSub + 1);//Read formatted string back to input
        }
        return regex;
    }




    public int expression()
    {
      int prevState = currstate-1;
        int startofExpression = concatenation();

        if(nextChar >= regexpattern.length) return startofExpression;//Return if finished

        if(regexpattern[nextChar] == '|'){
            nextChar++;//Consume |
            pointStateToCurrent(prevState);//Point previous to the branch state about to be created
            int heldBranch = currstate;
            addBranchState(0,0);//placeholder
            int nextExpressionStart = expression();
            if(nextExpressionStart == -1) nextExpressionStart = currstate;

            repointAllToCurrent(heldBranch, startofExpression);
            updateState(heldBranch, '\0', startofExpression, nextExpressionStart);
            //pointStateToCurrent(heldBranch-1);//Point state just BEFORE the branch to the exitstate
            startofExpression = heldBranch;//Branch is now the start of this expression
        }
        return startofExpression;//Returns the pointer to the start of this particular expression
    }

    public int concatenation(){
        int startOfConcat = term();

        if(nextChar >= regexpattern.length) return startOfConcat;//Return if finished

        if(!specialChars.contains("" + regexpattern[nextChar]) ||regexpattern[nextChar]=='(') concatenation();//Look ahead, without advancing any possible \

        return startOfConcat;
    }
    public int term()
    {
        //nextChar++ is used to 'consume' the operator
        //currstate++ is used to progress forward in the States table
        int prevState=currstate-1;
        int startOfTermNumber = factor();

        if(nextChar >= regexpattern.length) return startOfTermNumber;

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
        if(nextChar >= regexpattern.length) return startOfFactor;
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
        else
            error();
        return startOfFactor;
    }



    public boolean isVocab(char s){//Checks if the variable is a literal within the scope of the project - i.e. acceptable ascii
        if(s == '\\'){
            nextChar++;//Program will work with next value as literal
            return true;
        }
        return !specialChars.contains("" + s);
    }

    //
    //----------------------STATE FUNCTIONS-----------------------
    //
    public void repointAllToCurrent(int oldTarget, int searchStart){//Repoint all values pointing to specific state to another - does not apply to values that occur before the search start
        for(int i = searchStart-1; i < oldTarget; i++){ //Only repoint things that have occurred before the oldstate
            if(FSMlist.get(i).n1 == oldTarget){
                FSMlist.get(i).n1 = currstate;
            }
            if(FSMlist.get(i).n2 == oldTarget){
                FSMlist.get(i).n2 = currstate;
            }
        }

    }


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
        System.out.println("This regex string can NOT be parsed");
        System.exit(1);
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
