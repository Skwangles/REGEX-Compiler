
#REcompile notes:
- Recompile output is: State_Number next1 next2 character(nothing printed if branch state)  
- Program prints out "__" or "", based on variables which define what the wildcard/branchstate output values are
- [] cannot have 0 or less items i.e. [] is not accepted
- [a] is accepted - i.e. is translated as (\a), i.e. another way to define a literal
- **, ??, ++, || are not accepted
- Wildcard is represented *internally* by \t before it is printed as __ in console (can change based on variable)
- Branch is represented *internally* by \0 before it is printed as an empty string/char
- As per the specification empty brackets fail ()
- Error calls System.exit(1) on failure and writes 'This regex can NOT be parsed' to standard out

S->E\
E->C\
E->C|E\
C->T\
C->TC\
T->F\
T->F*\
T->F+\
T->F?\
F->v\
F->(E)

#REsearch notes:
- Branch Character considered empty (\0)
- Wildcard Character (\t)
- I suck at reading the specs, so my search initially output every match it found and   
interpreted the entire file as a string,  
- I kept the functionality by allowing you to enable or disable it with command line arguments  
as discussed in execution
- My search looks for the regex on each line separately, i.e. it doesn't look for matches
that span multiple lines, as I assumed this is what was meant in the assignment specs
- The default search outputs the *entire* line where atleast 1 match is found. e.g. line is "abc", regex is "a"..."abc" will be printed to console as per our interpretation of the specs

#Execution notes:
The following shell command is used to perform a search for the specified regular  
expression: $regex, in the file specified by name: $filename

java REcompile.java $regex | java REsearch.java $filename

By default, as per the assignment specifications, the program will only output
each line of the file which contained a match.

The following arguments can be specified after - to allow further functionality

-a  
Prints every match on every line

-d  
Prints Debug info including the FSM, line numbers and where the match  
was found on each line

An example would to execute a search outputting all matches with debug info:
java REcompile.java $regex | java REsearch.java $filename -ad

- The order of arguments doesn't matter  
- Arguments must be specified using '-'   
- Arguments are non-compulsory  
- Arguments can come before the filename, it doesn't matter

