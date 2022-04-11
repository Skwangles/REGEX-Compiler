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

    String specialChars = "*()[]?+|.";//All special chars
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
            System.out.println(i + " " + state.n1 + " " + state.n2 + " " + (state.symbol == '\0' ?  branchStatePrintout : (state.symbol == '\t' ? wildcardPrintout : state.symbol)));
        }
    }

    public boolean isBracketSlashed(int b, String regex){
        int evenOrOdd = 0;
        while(b > 0 && regex.charAt(b-1) == '\\'){
            evenOrOdd++;
            b--;
        }
        return (evenOrOdd %2 == 1 ? true : false);//If odd, there is a \ for the [, if even - all the slashes cancel each other
    }

    public String changeSquareToSlash(String regex){//replaces the [] in a regex with the (a|b|c) version
        int fromIndex = 0;
        while(regex.indexOf("[", fromIndex) != -1){
            int firstBracket = regex.indexOf("[", fromIndex);
            if(isBracketSlashed(firstBracket, regex))
            {
                fromIndex = firstBracket+1;
                continue;
            }
            //If the [ is interpreted as a literal, skip it and advance the 'search from' index

            int closingBracket = regex.indexOf("]", fromIndex+2);//Start search from at least 2 past the [ to exclude the first literal (Could be a ])
            if(closingBracket == -1 || closingBracket - firstBracket <= 1){//There must be a ] otherwise there are unmatched brackets and there must be atleast 2 characters to be valid
                error();
            }
            char[] sub = regex.substring(firstBracket+1, closingBracket).toCharArray();
            StringBuilder cut = new StringBuilder("(\\" + sub[0]);//Add the first values, regardless of it if is the ] or not
            for(int i = 1; i < sub.length; i++){
                cut.append("|\\").append(sub[i]);//Add each char with a | and \ to make sure it is read as a literal and is OR'ed.
            }
            cut.append(")");
            fromIndex = closingBracket;//Narrow the search
            regex = regex.substring(0, firstBracket) + cut + regex.substring(closingBracket + 1);//Read formatted string back to input
        }
        return regex;
    }

    //
    //-----------------PARSE FUNCTIONS-----------
    //
    public int expression()
    {
      int prevState = currstate-1;
        int startofExpression = concatenation();

        if(nextChar >= regexpattern.length) return startofExpression;//Return if finished

        if(regexpattern[nextChar] == '|'){
            nextChar++;//Consume |
            if(nextChar >= regexpattern.length || (!isVocab(regexpattern[nextChar]) && regexpattern[nextChar] != '.' && regexpattern[nextChar] != '(')) error(); //Make sure a full | expression is possible
            pointStateToCurrent(prevState);//Point previous to the branch state about to be created
            int heldBranch = currstate;
            addBranchState(0,0);//placeholder
            int nextExpressionStart = expression();
            repointAllToCurrent(heldBranch, prevState+1);//Repoint all items pointing to held (excluding prevstate)
            updateState(heldBranch, '\0', startofExpression, nextExpressionStart);
            //pointStateToCurrent(heldBranch-1);//Point state just BEFORE the branch to the exitstate
            startofExpression = heldBranch;//Branch is now the start of this expression
        }
        return startofExpression;//Returns the pointer to the start of this particular expression
    }

    public int concatenation(){
        int startOfConcat = term();

        if(nextChar >= regexpattern.length) return startOfConcat;//Return if finished

        if(!specialChars.contains("" + regexpattern[nextChar]) ||regexpattern[nextChar]=='(' || regexpattern[nextChar] == '.') concatenation();//Look ahead, without advancing any possible \

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
            addBranchState(startOfTermNumber, currstate+1);//Add branch state, pointing to the term, and to the item past it (t1 already is pointing to this branch)
            nextChar++;
            startOfTermNumber = currstate-1;//Branch is now the start of the Term
        }
        else if(regexpattern[nextChar]=='?'){
            pointStateToCurrent(prevState);//point previous to the new branch state
            addBranchState(startOfTermNumber, currstate+1);//Add branch state, pointing to the term, and to the item past it
            nextChar++;
            repointAllToCurrent(currstate-1, prevState+1);//Repoint any items EXCLUDING the prevstate, to the new exit
        }
        else if(regexpattern[nextChar]=='+'){//a+ is equal to aa* - first create new literal then apply * to the new literal
            int createdLiteralStateNum = currstate;//Location of the created Literal
            addState(FSMlist.get(startOfTermNumber).symbol, currstate +1, currstate +1 );//Creates identical literal as savedNextState
            pointStateToCurrent(startOfTermNumber);//point previous literal to the new branch state
            addBranchState(createdLiteralStateNum, currstate+1);//Add branch state, pointing to the term, and to the item past it (startOfTerm already is pointing to this branch)
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
        else if(regexpattern[nextChar] == '.'){
            startOfFactor = currstate;
            addState('\t',currstate+1,currstate+1);//Add wildcard
            nextChar++;//Consume a factor
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
        for(int i = searchStart; i < oldTarget; i++){ //Only repoint things that have occurred before the oldstate
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
            FSMlist.get(stateIndex).n1 = currstate;
        }

        FSMlist.get(stateIndex).n2 = currstate;
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
