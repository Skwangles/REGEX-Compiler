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
-[] cannot have 1 or less items i.e. [a] and [] are not accepted\
-**, ??, ++, || are not accepted\
-