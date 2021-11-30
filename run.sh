if [ $# -eq 2 ]
then
    userinput=$1
    filename=$2

    remainder=
elif [ $# -gt 2 ]
then
    userinput=$1
    filename=$2
    shift 2

    remainder=$*
else
    echo "no filename or problem number given"
    exit 1
    #echo -n "Enter problem number: "
    #read userinput
fi

PRE="Advent2021_"
if [[ $userinput =~ ^[0123456789][0123456789]$ ]]   # checks that the input is within the desired range
then
    javac com/nbkelly/advent/$PRE$userinput.java
    if [ $? -eq 0 ]; then	
	java com.nbkelly.advent/$PRE$userinput --file $filename $remainder
    fi
else
    echo "Invalid problem number"
fi
