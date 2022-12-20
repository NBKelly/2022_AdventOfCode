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
| Day 03  | One of those fat elves loaded up the rucksacks wrong, and now we have to count them and find the badges | Day 04  | We need to clean camp krusty before we leave. The elves try to find out the best way to get out of doing anything |
| Day 05  | The elves need to move a bunch of supplies, and want to do the crane operators job for him |
| Day 06  | We finally leave. The elves hand us the shittiest radio they have, so we have to start fixing it. |
| Day 07  | Now we need to format the C:// drive on the radio so we can update it. |
| Day 08  | The elves want to build a treehouse, so we plan out ideal locations. |
| Day 09  | When crossing a rope bridge, it breaks. We get out our computer and calculate the trajectories for the dangling rope.
| Day 10  | We survived, but our radio is broken. We need to replace the CRT on it.
| Day 11  | Some monkeys stole our items and we have to watch them throw them around for 50 hours.
| Day 12  | We can't contact the elves, so we need to find the highest nearby spot. Note that climbing up one meter is rough, but jumping down 26 is A-OK. Thanks eric.
| Day 13  | We managed to get a signal - a *distress signal*. Unfortunately, it's borked. We need to sort the incoming packets to make sense of it.
| Day 14  | The signal lead us to - and behind - a giant waterfall. We find that the cave is crumbling, and sand is pouring in. We decide to model what that will look like.
| Day 15  | We come to a large network of underground tunnels, and decide to set out scanners out to track down exactly where the distress signal is coming from.
| Day 16  | We find the original of the signal! A bunch of elephants have caused it! They are lost in the tunnels, and the tunnels are flooding with magma. We cooperate with exactly one elephant to close a bunch of suspiciously convenient flood management valves to buy escape time.
| Day 17  | We find an alternative exit to the cave, but then we find that it, too, is crumbling. Tetrominos are falling into a chamber, and we have to model how if we are to escape.
| Day 18  | We finally make it outside. It was a volcano all along! Bits of lava are being ejected out towards a nearby lake, and they could even make obsidian! We need to determine the scatter/impact pattern of the lava globules into the water (ie the visible surface area)
| Day 19  | The lava did indeed form obsidian. We decide to use it to make some geode cracking robots - but first we need clay finding robots, obsidian finding robots, and ore finding robots - we have one factory that can spit out one robot per minute, and the elephants are only willing to wait 24 minutes.
| Day 20  | It's time to meet with the elves again (where did the elephants go?). We need to decrypt a file with the location of the star fruit grove.
| Day 21  | 

## Problem Ratings
Here are my ratings for each problem, and what the time complexity of the solutions happens to be. If I use the letter N, it's line count (unless otherwise noted).

| Problem | Complexity (Part One) | Complexity (Part Two) | Comments |
| ------- |:---------------------:|:---------------------:|:-------- |
| Day 01  | O(N) | O(N) | Both single and triple maximums can be done in a linear search |
| Day 02  | O(N) | O(N) | You can just make a 3x3 lookup table for both sides on paper and hardcode it - and you should, it's the best solution |
| Day 03  | O(N log N) | O(N log N) | Just use sets and retain functions. |
| Day 04  | O(1) | O(1) | Calculating if two intervals intersect or engulf is super easy. |
| Day 05  | O(k) | O(k) | K = size of stack manipulated. Just pop n times from a deque, or take n from a dequeue. Nothing more to it than that.

## Solutions

I'm only writing out the ones that were worthwhile.

### Day 04: Camp Krusty Cleanup

An easy (and reusable) thing to do is create a helper function for this later, like:
```Java
class Range {
  int open, close
  public Range(int open, int close) {
    this.open = Math.min(open, close);
    this.close = Math.max(open, close);
  }
  
  /* these intervals are inclusive-inclusive */
  public int size() { return close + 1 - open; }
  
  public Range intersection(Range target) {
    if (close < target.open || open > target.close)
      return null;
    
    return new Range(Math.max(open, target.open),
                     Math.min(close, target.close));
  }
```

1) One range engulfs the other if the output range has the size of one of the input ranges.
2) The ranges intersect if you get even a single result

###  Table of Solutions
