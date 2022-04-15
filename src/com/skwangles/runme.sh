#!/bin/bash

filename="test.txt"
regex="(j|f)"

java REcompile.java $regex | java REsearch.java $filename