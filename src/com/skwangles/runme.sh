#!/bin/bash

filename="bee_movie.txt"
regex="abcdefg"


java REcompile.java $regex | java REsearch.java $filename