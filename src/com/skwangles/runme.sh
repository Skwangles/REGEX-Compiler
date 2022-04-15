#!/bin/bash

filename="test.txt"
regex="a|b"
all="true" #0 - Print first occurence, #1 - Print all occurences
java REcompile.java $regex | java REsearch.java $filename $all