# ADVENT OF CODE 2022

This is the set of all my 2022 Advent of Code solutions. (At some point later they will be cleaned). They are all produced in java, using my (very awful) workflow tools and hacks - I significantly improved and rewrote these since last year.

#  Table of Contents
1. [Project Structure](#Project-Structure)
2. [Lore](#Lore)
3. [Problem Ratings](#Problem-Ratings)
4. [Solutions](#Solutions)
5. [Visualizations](#Visualizations)
6. [Bonus Puzzles](#Bonus)

## Project Structure
All of the solutions are available in the ```com/nbkelly/advent``` folder, and can be run using the ```run.sh``` script, like so:

1. Run with input file - ```./run.sh 01 input.txt```
2. Run with optional args, such as debug mode and pictorial output (where supported) - ```./run.sh 01 input.txt -d [1-5] --out-file```

## Lore
Here's a brief summary of the 2022 advent of code **deep** lore.

| Problem | Plot |
| :-----: | :--- |


## Problem Ratings
Here are my ratings for each problem, and what the time complexity of the solutions happens to be. If I use the letter N, it's line count (unless otherwise noted).

| Problem | Complexity (Part One) | Complexity (Part Two) | Comments |
| ------- |:---------------------:|:---------------------:|:-------- |


## Solutions

###  Table of Solutions
###  Table of Solutions
1. [Day 01: Sonar Sweep](#Day-01-Sonar-Sweep)
2. [Day 02: Dive!](#Day-02-Dive)
3. [Day 03: Binary Diagnostic](#Day-03-Binary-Diagnostic)
4. [Day 04: Giant Squid](#Day-04-Giant-Squid)
5. [Day 05: Hydrothermal Venture](#Day-05-Hydrothermal-Venture)

### Day 01: Sonar Sweep
#### Summary
Count the number of times a number is greater than the last number.

For part two, count the number of times a group of 3 number window is greater than the last 3 number window. (1-2-3 / 2-3-4 / 3-4-5).

#### Part One: Count how many times a list increments.

Simply read through the list, and note every time ```li(i) > li(i-1)```.

#### Part Two: Count how many times a sliding window (of size 3) for the list increments.

The same as above, but note every time ```li(i) > li(i-3)``` instead.

Whenever you move on the sliding window, you are adding the element at *i*, and removing the element at *i-3* from the sum of that window, so you can determine the value of that change by directly comparing those two elements.

More visually, we are making the comparison ```[i-3] + [i-] + [i-1] < [i-2] + [i-1] + [i]```. It is trivial to see that we can factor this out to ```[i-3] < [i]```.
