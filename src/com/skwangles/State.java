package com.skwangles;

public class State {
    int statenum;
    int symbol;
    int n1;
    int n2;
    //Used to hold the State values as they are parsed for the compiler, can also be used by the search
    public State(int statenum, char symbol, int n1, int n2) {
        this.statenum = statenum;
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

    public int getStatenum() {
        return statenum;
    }

    public int getSymbol() {
        return symbol;
    }
}
