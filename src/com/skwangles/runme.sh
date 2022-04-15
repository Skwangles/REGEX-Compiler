#!/bin/bash

filename="test.txt"
regex="z"

java REcompile.java $regex | java REsearch.java $filename