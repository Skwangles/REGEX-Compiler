#!/bin/bash

filename="test.txt"
regex="a"
java REcompile.java $regex | java REsearch.java $filename -da
