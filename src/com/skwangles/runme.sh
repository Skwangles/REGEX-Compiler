#!/bin/bash

filename="test.txt"
regex="z|abcd"

java REcompile.java $regex | java REsearch.java $filename