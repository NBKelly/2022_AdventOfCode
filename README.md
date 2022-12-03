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
| Day 01  | Fat elves are counting calories and planning who to steal treats from. |
| Day 02  | One of those fat elves gives us a rock paper scissors cheat table, so we can cheat while we wait. |
| Day 03  | One of those fat elves loaded up the rucksacks wrong, and now we have to count them and find the badges |

## Problem Ratings
Here are my ratings for each problem, and what the time complexity of the solutions happens to be. If I use the letter N, it's line count (unless otherwise noted).

| Problem | Complexity (Part One) | Complexity (Part Two) | Comments |
| ------- |:---------------------:|:---------------------:|:-------- |
| Day 01  | O(N) | O(N) | Both single and triple maximums can be done in a linear search |
| Day 02  | O(N) | O(N) | You can just make a 3x3 lookup table for both sides on paper and hardcode it - and you should, it's the best solution |
| Day 03  | O(N log N) | O(N log N) | Just use sets and retain functions. |

## Solutions

I'm only writing out the ones that were worthwhile.

###  Table of Solutions
