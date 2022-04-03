package com.skwangles;

import java.util.ArrayList;

//Alexander Stokes - 1578409 & Rowan Thorley -
//Alexander Developed this
public class REcompile {
    static char escape = '\0';
  // static State[] output;
    static int currstate = 0;//State number starts at 0,
    static int j = 0;
    ArrayList symbol = new ArrayList();
    ArrayList next1 = new ArrayList();
    ArrayList next2 = new ArrayList();
    ArrayList stateList = new ArrayList();
    public static void main(String[] args) {
        String regex = args[0];//Reads in the regex pattern into an easily parse-able set.
       // output = new State[regex.length]; //Creates array of the max space the regex could be (100% concat) and has the state-number, symbol/branchindicator & next 2 states
        //" are removed automatically
        REcompile compile = new REcompile();
        compile.parse(regex);


    }

    public void parse(String regex){
        int initial = expression();
        if( p[j] ) error(); // In C, zero is false, not zero is true
        addState("Start",0,0);//Starting state
    }



    int expression()
    {
        int r;

        r=term();
        if(isvocab(p[j])||p[j]=='[') expression();
        return(r);
    }

    int term()
    {
        int r, t1,t2,f;

        f=state-1; r=t1=factor();
        if(p[j]=='*'){
            set_state(state,' ',state+1,t1);
            j++; r=state; state++;
        }
        if(p[j]=='+'){
            if(next1[f]==next2[f])
                next2[f]=state;
            next1[f]=state;
            f=state-1;
            j++;r=state;state++;
            t2=term();
            set_state(r,' ',t1,t2);
            if(next1[f]==next2[f])
                next2[f]=state;
            next1[f]=state;
        }
        return(r);
    }

    int factor()
    {
        int r;

        if(isvocab(p[j])){
            set_state(state,p[j],state+1,state+1);
            j++;r=state; state++;
        }
        else
        if(p[j]=='['){
            j++; r=expression();
            if(p[j]==']')
                j++;
            else
                error();
        }
        else
            error();
        return(r);
    }


    public void addState(String symbol, int next1, int next2){
        State state = new State(symbol, next1, next2);
        stateList.add(state);
    }

    public void addBranchingState(int next1, int next2){
        State state = new State(null, next1, next2);
        stateList.add(state);
    }
}


// Priority - \ first, () next, * + and ?, concatenation, finally | or []

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

    public int getN1() {
        return n1;
    }

    public int getN2() {
        return n2;
    }

    public int getSymbol() {
        return symbol;
    }
}
