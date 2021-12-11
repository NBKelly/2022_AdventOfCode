#!/bin/bash

if [ $# -eq 1 ]
then
    userinput=$1
else
    echo -n "Enter problem number: "
    read userinput
fi


if [[ $userinput =~ ^([0123456789][0123456789])$ ]]   # checks that the input is within the desired range
then
    YEAR="2019"
    echo $YEAR
    file="com/nbkelly/advent/Advent"$YEAR"_"$userinput".java"
    echo $file
    if test -f "$file"; then
	emacs -nw $file
    else
	echo -n "Create file Advent"$YEAR"_"$userinput?": "
	read userinput2
	if [ -z "$userinput2" ]
	then	    
	    echo $file
	    ../21_Drafter/drafter.sh -n Advent$YEAR"_"$userinput -p com.nbkelly.advent -l com/nbkelly/advent -ap com.nbkelly.drafter -ad com/nbkelly/drafter --overwrite-aux -d 0 --insert-params config/params.txt --insert-commands config/commands.txt --additional-imports config/import.txt --insert-in-post config/post.txt --insert-in-solution config/insert.txt --insert-block config/block.txt

#	    ../21_Drafter/simple.sh --classname com.nbkelly.advent.Advent$userinput --auxiliary-package-name com.nbkelly.drafter 
	    emacs -nw $file
	else
	    echo "Cancelled"
	fi
    fi     
else
    echo "Invalid problem number"
fi

#../21_Drafter/simple.sh
