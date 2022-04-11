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
F->(E)\

REcompile notes:\
-recompile output is:\
-statenum next1 next2 character(nothing printed if branch state)\
-Program prints out "__" or "", based on variables which define what the wildcard/branchstate output values are\
-[] cannot have 0 or less items i.e. [] is not accepted\
-[a] is accepted - i.e. is translated as (\a), i.e. another way to define a literal
-**, ??, ++, || are not accepted\
-There is an edge case that if '\' is backslashed just before [], it will interpret it as if it were not backslashed
-Wildcard is represented by \t before it is printed as __ in console (can change based on variable)
-