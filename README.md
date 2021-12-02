# ADVENT OF CODE 2020

This is the set of all my 2021 Advent of Code solutions. (At some point later they will be cleaned). They are all produced in java, using my (very awful) workflow tools and hacks - I significantly improved and rewrote these since last year.

#  Table of Contents
1. [Project Structure]
2. [Lore]
3. [Problem Ratings]
4. [Solutions]

## Project Structure
All of the solutions are available in the ```com/nbkelly/advent``` folder, and can be run using the ```run.sh``` script, like so:

1. Run with input file - ```./run.sh 01 input.txt```
2. Run with optional args, such as debug mode - ```./run.sh 01 input.txt --debug```

## Lore
Here's a brief summary of the 2021 advent of code deep lore.

| Problem | Plot |
| :-----: | :--- |
| Day 01  | You're minding your own business on a boat when santas keys go flying over the edge. To find his keys, we start by performing a depth analysis of the ocean floor. Because this measurement wasn't useful, we then need to smooth the data using an aggregation technique.


## Problem Ratings
Here are my ratings for each problem, and what the time complexity of the solutions happens to be. If I use the letter N, it's line count (unless otherwise noted).

| Problem | Complexity (Part One) | Complexity (Part Two) | Comments |
| ------- |:---------------------:|:---------------------:|:-------- |
| Day 01  | *O(N)*                  | *O(N)*      |

## Solutions

### Day 01: Sonar Sweep
#### Part One: Count how many times a list increments.

Simply read through the list, and note every time ```li(i) > li(i-1)```.

#### Part Two: Count how many times a sliding window (of size 3) for the list increments.

The same as above, but note every time ```li(i) > li(i-3)``` instead.

Whenever you move on the sliding window, you are adding the element at *i*, and removing the element at *i-3* from the sum of that window, so you can determine the value of that change by directly comparing those two elements.