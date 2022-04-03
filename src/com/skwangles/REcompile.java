package com.skwangles;
//Alexander Stokes - 1578409 & Rowan Thorley -
//Alexander Developed this
public class REcompile {
    static char escape = '\0';
   static State[] output;
    static int currentStateNum = 0;//State number starts at 0,

    public static void main(String[] args) {
        char[] regex = args[0].toCharArray();//Reads in the regex pattern into an easily parse-able set.
        output = new State[regex.length]; //Creates array of the max space the regex could be (100% concat) and has the state-number, symbol/branchindicator & next 2 states
        //" are removed automatically


    }
}



// Priority - \ first, () next, * + and ?, concatenation, finally | or []

/*



Overview:     Implement a regexp pattern searcher using the FSM, deque and compiler techniques outlined in lectures. Your solution must consist of two programs: one called REcompile.java and the other called REsearch.java. The first of these must accept a regexp pattern as a command-line argument (enclosed within quotes—see "Note" below), and produce as standard output a description of the corresponding FSM, such that each line of output includes four things: the state-number, the symbol to be matched or branch-state indicator (or other type of indicator if you find an alternative useful), and two numbers indicating the two possible next states. The second program must accept, as standard input, the output of the first program, then it must execute a search for matching patterns within the text of a file whose name is given as a command-line argument. Each line of the text file that contains a substring that matches the regexp is output just once, regardless of the number of times the pattern might be satisfied in that line. (Note also we are just interested in searching plain text files.)

Regexp specification:     For this assignment, a wellformed regexp is specified as follows:

any symbol that does not have a special meaning (as given below) is a literal that matches itself
- . is a wildcard symbol that matches any literal
- * indicates closure (zero or more occurrences) on the preceding regexp
- + indicates that the preceding regexp can occur one or more times
- ? indicates that the preceding regexp can occur zero or one time
- | is an infix alternation operator such that if r and e are regexps, then r|e is a regexp that matches one of either r or e
- ( and ) may enclose a regexp to raise its precedence in the usual manner; such that if e is a regexp, then (e) is a regexp and is equivalent to e. e cannot be empty.
- \ is an escape character that matches nothing but indicates the symbol immediately following the backslash loses any special meaning and is to be interpretted as a literal symbol
- square brackets "[" and "]" enclose a list of symbols of which one and only one must match (i.e. a shorthand for multi-symbol alternation); all special symbols lose their special meaning within the brackets, and if the closing square bracket is to be a literal then it must be first in the enclosed list; and the list cannot be empty.
adjacent regexps are concatenated to form a single regexp
operator precedence is as follows (from high to low):
escaped characters (i.e. symbols preceded by \)
parentheses (i.e. the most deeply nested regexps have the highest precedence)
repetition/option operators (i.e. *, + and ?)
concatenation
alternation (i.e. | and [ ])
You must implement your own parser/compiler, and your own FSM (simulating two-state and branching machines) similar to how it was shown to you in lectures, and you must implement your own dequeue to support the search. Note that you have some freedom in how you represent states within your search engine, and are not limited to the three-array method I used in lecture. For example, for the square-bracketted list of literals, it may be useful to represent the list as a String and use the contains() method to test for a match within REsearch.

Note that you should make sure you have a good grammar before you start programming, so take time to write out the phrase structure rules that convince you that your program will accept all and only the regular expressions you deem legal. Anything not explicitly covered by this specification may be handled any way you want. For example, you can decide if a** is a legal expression or not. And it is okay to preprocess the expression before trying to compile it, if such preprocessing simplifies what you are trying to do. For example, you could decide to replace any ** with just * if you want ** to be legal. Or, you could replace a square bracketed list of alternative literals with an equivalent expression that uses | symbols for alternation.

Observe also that REsearch can be developed in parallel with REcompile simply by working out the states of a valid FSM by hand and testing REsearch with that.

Note:     Operating system shells typically parse command-line arguments as regular expressions, and some of the special characters defined for this assignment are also special characters for the command-line interpreter of various operating systems. This can make it hard to pass your regexp into the argument vector of your program. You can get around most problems by simply enclosing your regexp command-line argument within double-quote characters, which is what you should do for this assignment. To get a double-quote character into your regexp, you have to escape it by putting a backslash in front of it, and then the backslash is removed by the time the string gets into your program's command-line argument vector (but I will not be testing with any kind of quote character). There is only one other situation where Linux shells remove a backslash character from a quoted string, and that is when it precedes another backslash. For this assignment, it is the string that gets into your program that is the regexp—which may entail some extra backslashes in the argument. (N.b. Windows command prompt shell has a slightly different regexp grammar than Linux uses, so if you develop on a windows box, make sure you make the necessary adjustments for it to run under linux.)
 */