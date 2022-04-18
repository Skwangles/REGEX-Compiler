#!/bin/bash

filename="test.txt"
regex="a|bc"
java REcompile.java $regex | java REsearch.java $filename