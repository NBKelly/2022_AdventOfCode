# ADVENT OF CODE 2021

This is the set of all my 2021 Advent of Code solutions. (At some point later they will be cleaned). They are all produced in java, using my (very awful) workflow tools and hacks - I significantly improved and rewrote these since last year.

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
Here's a brief summary of the 2021 advent of code **deep** lore.

| Problem | Plot |
| :-----: | :--- |
| Day 01  | You're minding your own business on a boat when Santa (now black) has his keys go flying over the edge. To find his keys, we start by performing a depth analysis of the ocean floor using a handy submarine. Because this measurement wasn't useful, we then need to smooth/aggregate that data.
| Day 02  | Today we learnt to pilot the submarine. The course is pre-programmed, so we just need to figure out where we are going. To make matters worse, the manual is in chinese, so we calculated the wrong number the first time.
| Day 03  | The submarine appears to be faulty (cheap chinese merchandise), so we run a diagnostic. We're concerned with the **power consumption** and the **life support rating** of the vessel.
| Day 04  | 1.5km below the surface, a giant squid has grappled our submarine (revenge for chinese fishery practices). Eric decides it wants to play bingo. We use the bingo program on the ship to print a set of boards and draws, and then game the system to see the best ways to win and lose.
| Day 05  | We've come accross a network of hydrothermal vents on the ocean floor. To get through safely, we must map them. Hey where did that octopus go? We never found out what he wanted?
| Day 06  | Still heading down, we see some lanternfish. We attempt to model their growth rate.
| Day 07  | On the way down, a whale attacks. Fortunately, a swarm of crabs (in their own crab submersibles) is ready to assist. We need to compute the optimal way to align the crabmarines so they can blow the floor out of the ocean, and let us into an underwater cave system. Any by most optimal, Eric means we have to make sure they save on fuel - "crabs" are notorius cheapskates (what did Eric mean by this?)
| Day 08  | We made in into the cave (at the cheapest price possible for the crabs), by the width of a hair. We know there's an exit somewhere deeper, but the seven-segment displays in our submarine are faulty. Time to debug them!
| Day 09  | The caves are actually lava tubes, and are filling with smoke. We need to model the smoke to find a safe way through.
| Day 10  | Our submarine can't find us the best way out of the cave because the entire computer is fucked. Today, we check syntax for some reason.
| Day 11  | We've encountered a cavern full of **glow in the dark** octopi. They flash in a fashion similar to fireflies (they sychronise), and we need to compute this pattern to navigate through the pattern without disturbing them.
| Day 12  | The pathfinding routines on the submarine are bad, so we have to manually find "the" path out - but the only way to do that is to find all paths out of the cave.
| Day 13  | We want to thermally image some more volcanic vents, but the thermal camera has not been activated! In order to activate it, we need the activation code. In order to find that, we need to fold a piece of transparent paper a bunch of times to create the code.
| Day 14  | The pressure is too high for the submarine, so we need to produce some polymer reinforcements. Like everything in this submarine, we have to do it manually.
| Day 15  | We're almost at the cave exit, but found that the narrow area we're in is covered in some sort of sea bugs. We don't want hurt them, so we need to plot the path that has us interact with the smallest amount of them.
| Day 16  | We made it to open water (at last - did we ever even use the thermal camera?) - now we have to manually decode a radio transmission from the elves (in *Fourier Uncoded Chiral Kinematic-Yaw Orthograph Unresolution*  format).
| Day 17  | The elf message was worthless. We need to shoot a probe into an ocean trench.
| Day 18  | Some snailfish (in the trench) claim to have seen the keys, but will only tell us which direction they went if we solve their math homework. (hint: direction = down)

## Problem Ratings
Here are my ratings for each problem, and what the time complexity of the solutions happens to be. If I use the letter N, it's line count (unless otherwise noted).

| Problem | Complexity (Part One) | Complexity (Part Two) | Comments |
| ------- |:---------------------:|:---------------------:|:-------- |
| Day 01  | *O(N)*                  | *O(N)*      | A good solution should read the input from a file one line at a time - this would allow processing of arbitrarily large files. <br/> Eric should have had the window size be larger, say 5 or 10, to force some of the programmers to ask "is there an easier way".
| Day 02  | *O(N)*		    | *O(N)*	  | This problem is pretty straightforward. It looks messy if you use the same data twice. I quite like this problem.
| Day 03  | *O(N.B)*		    | *O(N.B)*	  | *B = Bit-length* - If it wasn't for the absurd walls of text, this would be a pretty straightforward problem. Part one is simple consensus, part two is closer to finding a dominant taxa from a character table. There's a bad solution to part 2, and a nice solution. For real though, it's unbelievable how absurdly obfuscated the text is for this one.
| Day 04  | *O(log<sub>2</sub>(N).S.B)* **OR** *O(B + Nlog<sub>2</sub>N)*	    | *O(log<sub>2</sub>(N).S.B)* or *O(B + Nlog<sub>2</sub>N)* | *N,S,B = number of draws,size(num_cols) of the board, and number of boards* - This was a fun problem. Parsing may be hard for people not using python or java, specifically because eric decided to left pad numbers on the bingo boards (why couldn't they just leave spacing up to the user to pretty print?). Fuck you eric for not counting the diagonals. The second time complexity is for when you solve the bingo boards through condensing them into "I win at this index". You have to sort them still, so it ends up being *Nlog<sub>2</sub>N*
| Day 05  | *O(N.L)*		    | *O(N.L)*	| *number of lines, average line length* - Note that there is a better way to do this, which involves a linear sort on all of the line segments, so you can scan left to right picking out all the intersections. This problem produces some pretty pictures.
| Day 06  | *O(N)*		    | *O(N)*	| *N = number of days* - This one was quite easy, but the way it's written may catch people off guard for part 2 (a brute force solution will slow to a crawl). I like it. **your puzzle input: FUCK YOU**
| Day 07  | *O(N)*		    | *O(N)*	| This is one of the easiest days so far. In terms of programming, there's effectively nothing you need to do. You need to know how to take an average, find the median, and sum a series of integers. Recognizing the problem is slightly harder, but still not too hard.
| Day 08  | *O(N)*		    | *O(N)*	| Eric needs to take lessons in technical writing. This entire page is a mess. The first part of the problem is braindead easy, but figuring out what the fuck eric wants from you is a challenge. The second part is actually a fun puzzle, but it's still a challenge finding out what the fuck eric is asking you.
| Day 09  | *O(N<sup>2</sup>)*	    | *O(N<sup>2</sup>)* | This problem was alright, but it's a little lame. The only possible difficulty you can have is reading the question wrong (which a lot of people did).
| Day 10  | *O(N)*		    | *O(N)*		 | This problem should have been on day three or four. It's something completely trivial to do. It's also drastically overexplained, meaning you have to search for the important parts of the question (the numbers assigned to the digits).
| Day 11  | *O(N)*		    | *O(N)*		 | *N = number of rounds*. This is a fun problem, and it produces some pretty graphics. There's no overexplaining, and no hidden information. 9/10, but only because part 2 was too easy and the image was too small.
| Day 12  | *O(N<sup>N-1</sup>)*    | *O(N<sup>N</sup>)* | *N = number of links*. This was neat. You can do it very quickly with a DFS, but for a larger set you would have to use dynamic programming. Given the restrictions of this puzzle (Big nodes cannot link to other big nodes), I think an n^2 solution may be possible, given that you first factor out every big node and then apply DP.
| Day 13  | *O(N.K)*		    | *O(N.K)*		 | *Number of folds, number of points* This was fun, but still quite easy. 2021 is the most "inclusive" year yet. The good news is you have a program which can generate images based on any arbitrary fold set, which is neat.
| Day 14  | *O(N.K)*		    | *O(N.K)*		 | *Number of rules, number of iterations* This day was a little boring. The solution is obvious, and genuinely the hardest part of this is counting the most/least common letter. Fuck you eric.
| Day 15  | *O(N<sup>2</sup>)*	    | *O(N<sup>2</sup>)* | There's no good heuristic you can use, because cost dominates distance. Eric gave an extremely shit description of how the cave extends, so a lot of people wasted time trying to figure out just what the fuck he was saying. This puzzle sucks ass compared to 2018 day 15, which had pathfinding, battling dudes, turns, and everything else. No soul.
| Day 16  | *O(N)*		    | *O(N)*		 | This problem was easy, reading it was a fucking nightmare.
| Day 17  | I Don't Care	    | I Don't Care	 | This problem is really barely even worth doing. The inputs are so easy that the optimal strategy is brute force. The bounds are easy to see, and a brute force solution should finish in less than a second. The second optimal strategy is a partially correct solution, which should also finish in barely a second, and will be correct for every input eric gives. Part one is literally a one-liner, which is only correct in the specific circumstance eric gives.
| Day 18  | *O(N.K)*		    | *O((N.K)<sup>2</sup>)* | N = line count, k = reduction complexity. This problem was actually quite fun, even if the instructions were garbage. At least it was difficult.


## Solutions

###  Table of Solutions
1. [Day 01: Sonar Sweep](#Day-01-Sonar-Sweep)
2. [Day 02: Dive!](#Day-02-Dive)
3. [Day 03: Binary Diagnostic](#Day-03-Binary-Diagnostic)
4. [Day 04: Giant Squid](#Day-04-Giant-Squid)
5. [Day 05: Hydrothermal Venture](#Day-05-Hydrothermal-Venture)
6. [Day 06: Lanternfish](#Day-06-Lanternfish)
7. [Day 07: The Treachery of Whales](#Day-07-The-Treachery-of-Whales)
8. [Day 08: Seven Segment Search](#Day-08-Seven-Segment-Search)
9. [Day 09: Smoke Basin](#Day-09-Smoke-Basin)
10. [Day 10: Syntax Scoring](#Day-10-Syntax-Scoring)
11. [Day 11: Dumbo Octopus](#Day-11-Dumbo-Octopus)
12. [Day 12: Passage Pathing](#Day-12-Passage-Pathing)
13. [Day 13: Transparent Origami](#Day-13-Transparent-Origami)
14. [Day 14: Extended Polymerization](#Day-14-Extended-Polymerization)
15. [Day 15: Chiton](#Day-15-Chiton)
16. [Day 16: Packet Decoder](#Day-16-Packet-Decoder)
17. [Day 17: Trick Shot](#Day-17-Trick-Shot)
18. [Day 18: Snailfish](#Day-18-Snailfish)
19. [Day 19](#Day-19-)
20. [Day 20](#Day-20-)
21. [Day 21](#Day-21-)
22. [Day 22](#Day-22-)
23. [Day 23](#Day-23-)
24. [Day 24](#Day-24-)
25. [Day 25](#Day-25-)







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

### Day 02: Dive!
#### Summary
Follow a set of commands to move the submarine in straight lines. For part two, follow a set of commands to move the submarine in diagonal lines.

#### Part One: Manhattan distance

Simply keep a tally of ```loc``` and ```depth```. This is a simple switch statement on your input.

#### Part Two: Manhattan product

Just keep track of an aim variable too.

This is mostly the same procedure, you just move all your state changes into the handler for ```forward``` - ```up``` and ```down``` only change your aim.

### Day 03: Binary Diagnostic
#### Summary
Find the consensus between two strings, and then build a character table from those strings.

#### Part One

1. Find the consensus between a set of strings.
2. Invert your answer ```[1->0], [0->1]```
3. Parse the answer, and inverted answer, using radix 2
4. Return the product of these numbers

#### Part Two

This one is a little more interesting. I think the most convenient approach is to build a tree.

```Java
private class Tree {
    private int weight = 0;
    private Tree zero = null;
    private Tree one = null;

    public Tree() {}

    public void add(String s) {
        weight++;
	    
        if(s.length() == 0)
            return;

        var child = s.substring(1);
        if(s.charAt(0) == '0') {
            if(zero == null)
                zero = new Tree();
            zero.add(child);
        }
	else  {
            if(one == null)
                one = new Tree();
            one.add(child);		
        }
    }
    
    public String traverse(boolean common, boolean ones) {
        if(one == null && zero == null) return "";
        if(one == null) return "0" + zero.traverse(common, ones);
        if(zero == null) return "1" + one.traverse(common, ones);

        if(one.weight == zero.weight && ones) return "1" + one.traverse(common, ones);
        if(one.weight == zero.weight && !ones) return "0" + zero.traverse(common, ones);

        if((one.weight > zero.weight) ^ !common)
            return "1" + one.traverse(common, ones);
        
        return "0" + zero.traverse(common, ones);
    }
}    

```



### Day 4: Giant Squid

#### Summary
Find the winning and the losing bingo boards.

#### Solution

I'm not writing much here, except that the most effecient way to find each terminating round is through the use of a binary search.

Additionally, the easiest way to check rows/colums is to just make sets once, and simply check if row/column is a subset of the current draw. This multiplies your space usage by 2, because you're storing each bingo card twice.

Here's what a binary search for part two looks like. It's worth noting that the condition for our search is the first element in the collection where every board is complete.

```Java
int left = 0;
int right = draws_list.size();
	
Bingo last_match = null;
int last_matching_round = 0;

while(left < right) {
    int mid = (left + right) / 2;
    int round = mid;

    var draws = new HashSet<Integer>();
    draws.addAll(draws_list.subList(0, round+1));

    //see if any board satisfies - we want to find a state where every board is correct
    var match = boards.parallelStream().filter(board->!board.check(draws))
        .findAny();

    if(match.isPresent()) {
        last_match = match.get();
        last_matching_round = round +1;
        left = mid+1;
    }
    else
        right = mid;	    
}

var draws = new HashSet<Integer>();
draws.addAll(draws_list.subList(0, last_matching_round+1));
return draws_list.get(last_matching_round) * last_match.score(draws);
```

#### Alternative Method
A more effective method exists for solving this problem. 

* Replace each number in your bingo board with it's index in the list of draws (if it exists)
* Each row or column can be reduced to the highest number in that row or column - that's the index of the draw which completes it
* Each board can then be reduced to the lowest reduced row/column - this is the first index which completes the board (and the round it completes) - we can call this it's condensation
* The set of boards can then be sorted based on their condensations. The board with the lowest condensation is the first winner, and the board with the highest condensation is the last winner.

To find the condensations:

```Java
public Integer condensation(TreeMap<Integer, Integer> grades) {
    Integer best = null;

    row: for(var row: rows) {
        int highest = 0;
        for(var token : row) {
            var key = grades.get(token);
            if(key != null)
                highest = Math.max(key, highest);
            else
                continue row;
        }

        if(best == null)
            best = highest;
        else
            best = Math.min(best, highest);
    }

    col: for(var col: cols) {
        int highest = 0;
	for(var token : col) {
            var key = grades.get(token);
            if(key != null)
                highest = Math.max(key, highest);
            else
                continue col;
        }

        if(best == null)
            best = highest;
        else
            best = Math.min(best, highest);
        }


    return best;
}
```

Once that's done, the actual solution looks like this:

```Java
public void solve_fast(ArrayList<Bingo> boards, ArrayList<Integer> draws) {
    TreeMap<Integer, Bingo> condensations = new TreeMap<>();

    TreeMap<Integer, Integer> grades = new TreeMap<Integer, Integer>();
    for(int i = 0; i < draws.size(); i++)
        grades.put(draws.get(i), i);
	
    for(var board : boards) {
        var con = board.condensation(grades);
        condensations.put(con, board);
    }

    //get the first item/round
    var first_pair = condensations.firstEntry();
    var first_index = first_pair.getKey();
    var first_draw = draws.get(first_index);	
    var first_valid_draws = new HashSet<Integer>(draws.subList(0, first_index+1));
    var first_score = first_pair.getValue().score(first_valid_draws) * first_draw;
    DEBUGF(2, "Part One (CONDENSATION): %d%n", first_score);

    //get the last item/round
    var last_pair = condensations.lastEntry();
    var last_index = last_pair.getKey();
    var last_draw = draws.get(last_index);
    var last_valid_draws = new HashSet<Integer>(draws.subList(0, last_index+1));
    var last_score = last_pair.getValue().score(last_valid_draws) * last_draw;
    DEBUGF(2, "Part Two (CONDENSATION): %d%n", last_score);
}

```




### Day 05: Hydrothermal Venture

#### Summary
Find the set of all intersections between a group of orthogonal/octogonal lines.

#### Solution.

The simple way to do this is simply to put all line segments into a hashmap (cell by cell), and mark any point that gets intersected in a hashset.
The answer is simply the size of that hashset.

The more complicated answer involves first finding the set of all colinear lines, isolating the overlapping portions, and then merging those overlapping portions which overlap.

Once that's done, simply use any reasonable algorithm to check if/when lines intersect, and check them against the line segments you already have. To avoid needing to track significantly large numbers of points, you can break the solution space up into smaller chunks and divide/conquer it.





### Day 06: Lanternfish

#### Summary
Count the population of a group of ~~rabbits~~ fish over time, given an initial population state.

#### Solution
Don't count each fish individually, batch them instead. This is linear time, you just have to ensure you don't have your data overflow. A better solution may use BigIntegers.

```Java
long[] timers = new long[9];
for(int i = 0; i < ints.length; i++)
    timers[Integer.parseInt(ints[i])]++;
	
for(int day = 1; day <= 256; day++) {
    long[] next = new long[9];
    for(int i = 1; i < 9; i++)
        next[i-1] += timers[i];
	    
    next[6] += timers[0];
    next[8] += timers[0];
	    
    timers = next;	    

    if(day == 80) {
        long sum = 0;
        for(int i = 0; i < 9; i++)
            sum += timers[i];
        DEBUGF(1, "PART ONE: "); println(sum);
    }
}
	
long sum = 0;
for(int i = 0; i < 9; i++)
    sum += timers[i];
DEBUGF(1, "PART TWO: "); println(sum);
```


### Day 07: The Treachery of Whales

#### Summary
Find the median point and average points of a set of numbers.

#### Part One
Find the median value, then compute the distance for that index. This is ```ints(ints.size()/2)```.

For why this is the median, first consider a set of crabs at positions 1, 5, 7.

If the target location is 5, then crabs at position 1 and 7 will not need to move. If you offset the position in either direction, you will find that despite disp(1) and disp(7) having the same sum, 5 has to move as well. So it's trivial to see that 5 is optimal.

This example scales up for any odd-numbered set of crabs. Consider a slightly larger example:

```
set: 1, 2, 3, 8, 9, 10, 11

00  01  02  03  04  05  06  07  08  09  10  11 
    01---------------------------
                                 -----------11
        02-----------------------
	                         -------10
            03-------------------
	                         ---09
                                08
```

It's plain to see that each pair of non-median numbers requires a span with a distance of (max-min) to cover it, and that shifting the meeting point away from the median increases the required distance in all cases.

#### Part Two
First, recall the formula for a sum of n integers: ```1 + 2 + ... + n == n (n+1)```. If you want a better visual proof, look in the code, or write the series down twice, once backwards, and add the two together.

```
 1   2   3  ... n-2  n-1  n   +
 n  n-1 n-2 ...  3    2   1   =
n+1 n+1 n+1 ... n+1  n+1 n+1 

= n(n+1)
```
That's the formula for twice the sum - so the real sum is ```n(n+1)/2```.

Then find the average of the set, and compute your distance from that number.

### Day 08: Seven Segment Search

#### Summary
Your seven segment displays are faulty. A seven segment display uses 7 lights to display the numbers 0-9, like so:

```
.1111.
2....3
2....3
.4444.
5....6
5....6
.7777.
```

Where each number represents one segment. Somehow, the segments have become scrambled. Given a list of segment combinations, each representing one displayed number, decode an output message.

#### Part One
The numbers 1, 4, 7 and 8 each have a unique number of segments required to produce them. (2, 4, 3, 7). Simply count the number of times the output (the four numbers on the right) contains these numbers.

#### Part Two

For this one, you have to actually work out what all the numbers are and decode all of the outputs.

To start with, we know four numbers for free already. Using that, the process I used is as follows:

* We can make an intermediate product (let's call it **X**) with ```|8| - |4|.```
* |2| = k in unresolved, *where* ```size(k) == 5``` and ```size(k - X) == 2```
* **mid** = |2| - |1| - **X**
* |0| = |8| - **mid**
* |6| = |8| - (|2| ^ |1|)
* |9| = k in unresolved, *where* ```size(k) == 6```
* |3| = k in unresolved, *where* |2|^|1| in k
* |5| = |unresolved|


### Day 09: Smoke Basin

#### Summary
The cave is filling with smoke, which flows into every tile with a height less than 9. Find all local minima of the graph (points lower than all neighbors), and then find all basins (areas surrounded by 9's/boundaries) to determine the largest basins.

#### Part One
Simply select tiles which are surrounded by tiles with a bigger number.

#### Part Two
Floor fill from all of the tiles found in Part One, selecting every tile which is not 9. Sort the areas, the select the tree largest and multiply them together.

### Day 10: Syntax Scoring

#### Summary
The navigation system has syntax errors. We need to identify lines with mismatched parentheses, and then complete lines with incomplete parentheses.

Just use a stack. You know what a stack is, right?

#### Part One
Add opening parenthesis to a stack, pop when you see a closing parenthesis and compare.

#### Part Two
Count whatever is left in the stack after your line is cleared.


### Day 11: Dumbo Octopus

#### Summary
We've encountered some octopi with glow in the dark. These octopi have two states: charging (energy 1-9) and discharging (energy 0). An octopus cannot gain energy while discharging, and charges all adjacent octopi while discharging. A discharge can cause a cascade of discharges. Between every state, the energy level of all octopuses increases by 1 - and those with energy >9 discharge (are set to 0).

We need to measure the discharge rate of these octopi, and then find out when they sychronize.

#### Part One
For each iteration:
* Maintain a set of complete (popped) nodes
* Maintain a queue of active (popped) nodes
* for each node in the space, if the energy is 9, add it to active
* while active > 0
* let current node = active.pollFirst();
* get every neighbor: iterate each neighbor
  * if they have value 9, add their neighbors to active and them to complete
* add current node to complete : continue
* set every node with value 9 to 0 - these are the popped nodes
* iterate every other node

Simply repeat 100x.

#### Part Two
Simply run until popped = matrix size.


### Day 12: Passage Pathing

#### Summary
Find *all* paths through a graph, where small nodes may only be visited once (or one small node may be visited exactly twice, for part two).

I'm going to post a chunk of code here which should roughly illustrate the solution. It does a little more than the solution requires (it handles loops where there are two adjacent big nodes, for example), but is still lean enough to not be significantly obfuscated.

```Java
private class History {
    /* big nodes visited since last small node */
    TreeSet<String> sinceLastProgress = new TreeSet<String>();

    /* set of all visited small nodes */
    TreeSet<String> small_visited = new TreeSet<String>();

    /* whatever small node has been revisted (if any) */
    String revisited = null;

    /* last visited state */
    String last = null;

    public History() {}

    private History(String new_state, History last, boolean is_repeat) {
        if(is_repeat)
            revisited = new_state;
        else
            revisited = last.revisited;

        /* keep all last small visited */
        small_visited.addAll(last.small_visited);

        /* if it's big, then copy sinceLastProgress */
        if(isSmall(new_state))
            small_visited.add(new_state);
        else {
            sinceLastProgress.addAll(last.sinceLastProgress);
            sinceLastProgress.add(new_state);
        }

        this.last = new_state;
    }

    public History extend(String state, boolean allow_repeat) {
        if(isSmall(state)) {
            if(small_visited.contains(state)) {
                /* if we can repeat, return a repeat */
                if(allow_repeat && this.revisited == null
                   && !isStart(state))
                    return new History(state, this, true);
                return null;
            }

            return new History(state, this, false);
        }

        /* see if we're stuck in a loop */
        if(sinceLastProgress.contains(state))
            return null;

        return new History(state, this, false);
    }
}

```

Given this history definition, finding each part of the problem can be done like so:

```Java
LinkedList<History> active = new LinkedList<History>();
active.add(new History().extend("start", allow_repeat));

BigInteger res = BigInteger.ZERO;
while(active.size() > 0) {
    var current = active.pollFirst();

    inner: for(var state : states.get(current.last)) {
        if(isEnd(state)) {
            res = res.add(BigInteger.ONE);
            continue inner;
        }

        var next = current.extend(state, allow_repeat);

        /* dfs stores the least in memory - we still need to visit every state */
        if(next != null)
            active.push(next);
    }
}

return res;
```

### Day 13: Transparent Origami

#### Summary
We have a transparent piece of paper which needs to be folded several times so that the points line up to make an image.

#### Part One

Don't use an array. Use a set. Put all points in a set, then:

```Java
public HashSet<IntPair> foldX(HashSet<IntPair> set, int x) {
    HashSet<IntPair> res = new HashSet<IntPair>();

    for(var pair : set) {
        if(pair.X < x)
            res.add(pair);
        else {
            var diff = pair.X - x;
            res.add(new IntPair(x - diff, pair.Y));
        }
    }

    return res;
}

public HashSet<IntPair> foldY(HashSet<IntPair> set, int y) {
    HashSet<IntPair> res = new HashSet<IntPair>();

    for(var pair : set) {
        if(pair.Y < y)
            res.add(pair);
        else {
            var diff = pair.Y - y;
            res.add(new IntPair(pair.X, y - diff));
        }
    }

    return res;
}
```

#### Part Two

All you need to do is be proficient in reading block-text.<sup>Is that a U or a V?<sup>fuck you eric</sup></sup>

```
.........................................
.█..█.█..█.█..█...██..██...██....██.████.
.█..█.█.█..█..█....█.█..█.█..█....█....█.
.████.██...█..█....█.█....█..█....█...█..
.█..█.█.█..█..█....█.█.██.████....█..█...
.█..█.█.█..█..█.█..█.█..█.█..█.█..█.█....
.█..█.█..█..██...██...███.█..█..██..████.
.........................................
```

### Day 14: Extended Polymerization

#### Summary
Recursively expand a string based on a set of rules, which define how each pair of characters should expand. Then, count the most/least common characters.

#### Part One
You can do part one the bad way (actually use string replacement), but you'll regret it quite quickly.

The 'smart' way:
* Create a counter which stores <Pair,Integer>
* Insert every pair from the input string into this counter
* Run your input through expand(counter, rules) 10 times
* Count the occurances of the first letter of each pair, plus the last letter of the input

Expand:
* create a new_counter
* for each pair XY in the counter, see if a rule V matches it
* if so, add to new count (XV, count) and (VY, count)
* otherwise add to new count (XY, count)

#### Part Two
Just run it 30 more times.

### Day 15: Chiton

#### Summary
Find a path from point A to B with minimum cost.

#### Part One
Define functions to get neighbors, index a point on the grid, and check if a point is pathable.

Then perform a search from point A to B using the pathfinding algorithm of your choice.

```Java
var p1_cost = map.cost_orthogonal(sx, sy, fx, fy,
				  c -> Character.isDigit(c),
				  x -> Map.OrthogonalNeighbors(x),
				  x -> (x - '0'),
				  x -> get_char(lines, x.X, x.Y, 0));	
```

#### Part Two
The space you're searching is bigger. My technique for indexing it:

```
var p2_cost = map.cost_orthogonal(sx, sy, fx, fy,
				  c -> Character.isDigit(c),
				  x -> Map.OrthogonalNeighbors(x),
				  x -> (x - '0'),
				  x -> get_char(lines, x.X, x.Y, 4));
	
```

with get_char defined as:

```Java
public Character get_char(ArrayList<String> lines, int x, int y,
			      int rot_lim) {	
    if(x < 0 || y < 0)
        return null;
	
    //calculate displacement
    var rot_x =  x / lines.get(0).length();
    var rot_y =  y / lines.size();

    if(rot_x > rot_lim || rot_y > rot_lim)
        return null;	
	
    if(rot_x == 0 && rot_y == 0)
        return lines.get(y).charAt(x);	
	
    var index_x = x % lines.get(0).length();
    var index_y = y % lines.size();

    return modulate(lines.get(index_y).charAt(index_x), rot_x + rot_y);
}
    
public Character modulate(Character c, int mod) {
    if(c == null)
        return null;
    var ch = c + mod;
    while(ch > '9')
        ch = (char)((int)ch - 9);

    return (char)ch;
}
```

### Day 16: Packet Decoder

#### Summary
Decode a packet, and find the summ of all version headers, and the resultant value of the operation.

The basic format of a packet:

```
+-----+-----+-----
| VVV | TTT | ?
+-----+-----+-----
```

* V = **VERSION** - protocol version
* T = **TYPE ID** - the typeID field of the packet, used to define packet behaviour.


**LITERAL TYPE**
```
TYPE = 4 : 100
+-----+-----+-------++ ------+
| VVV | 100 | IAAAA ?? IAAAA |
+-----+-----+-------++-------+
```

The payload of a **LITERAL** packet is of arbitrary length, divided into 5 bit blocks. The first bit of each block, **INDICATOR**, defines whether this is the final (terminal) segment. The next four bits of the block, **AAAA**, are in order segments of the payload (a number or value).

* If **INDICATOR** is 1, there are more blocks as part of the payload. (non-terminal).
* if **INDICATOR** is 0, the payload terminates with this block. (terminal).

**OPERATOR TYPE**
```
+-----+-----+---+-----
| VVV |!100 | I | ?
+-----+-----+---+-----
```
An **OPERATOR** has a **TYPE ID** that is not 4/100. It has a single **INDICATOR** bit, that defines it as a **TYPE A** or **TYPE B** operator.

**TYPE A OPERATOR**
```
+-----+-----+---+-----------------+--------
| VVV |!100 | 0 | LLLLLLLLLLLLLLL | ?????
+-----+-----+---+-----------------+--------
```

A **TYPE A OPERATOR** has an **INDICATOR** of 0, and has a **LENGTH** field that is 15 bits long, and defines **the total LENGTH in bits of the contained subpackets**, represented by **??**.

**TYPE B OPERATOR**
```
+-----+-----+---+-------------+--------
| VVV |!100 | 1 | LLLLLLLLLLL | ?????
+-----+-----+---+-------------+--------
```

A **TYPE B OPERATOR** has an **INDICATOR** of 1, and has a **LENGTH** field that is 11 bits long, and defines **the COUNT of sub-packets contained in this packet**, represented by **??**.

**TYPE DEFINITIONS**

```
+---+-----+------+----------------------------------------------------+
| 0 | 000 | SUM  | Sum of all contains packets		              |
| 1 | 001 | PROD | Product of all contained packets		      |
| 2 | 010 | MIN  | Minimum value from contained packets		      |
| 3 | 011 | MAX	 | Maximum value from contained packets		      |
| 4 | 100 | LIT	 | A literal number/value			      |
| 5 | 101 | GT	 | Is the first packet greater than the second (1, 0) |
| 6 | 110 | LT	 | Is the first packet smaller than the second (1, 0) |
| 7 | 111 | EQ	 | Is the first packet the same as the second (1, 0)  |
+---+-----+------+----------------------------------------------------+
```

#### Part One
Decode all the packets. Count the version numbers of each packet.

#### Part Two
Compute the value of the packet.


### Day 17: Trick Shot

#### Summary

Find the highest value a projectile can have on a trajectory that hits a box, and find the total number of trajectories that hit a box. I'll redo this one in a non-shit way after christmas I guess.

#### Part One
brute force it. The inputs and problem are so garbage that it's genuinely not worth doing it right. The continual subpar and lazy puzzles are really sapping my will to bother with AOC this year. Eric has really phoned it in.

#### Part Two
Brute force it. Eric gives you zero reason not to.


### Day 18: Snailfish

#### Summary

Find the sum of a series of **snailfish numbers**, and then find the pair of **snailfish numbers** that produce the highest sum from the series.

A snailfish number is a ~~tree~~ string of numbers, that can be defined as:
```
literal   := (0|1|2|3|4|5|6|7|8|9)+
unit      := pair|literal
pair      := [unit,unit]
snailfish := pair
```

An few examples would be:
```
[1,1]
[[1,2],3]
[[[1,[2,[3,4]]],[5,6]],7]
```

Because depth is important, this one labels depth:
```
[0,[1,[2,[3,[4,4]]]]]
```

In order to sum two snailfish numbers, the following steps are taken:

```
pre-reduction_sum := [left,right]
sum = reduce(pre_reduction_sum)
```

But how is a snailfish number reduced?

The following steps are taken:
* start:
* If possible, explode the leftmost pair with depth == 4, then GOTO start.
* If possible, split the leftmost number with value > 9, then GOTO start.
* end

So what do the reduce and split operations look like?

To reduce a pair:
* Note the two values of the pair (**left**, **right**)
* Replace the pair with the literal 0
* Find the rightmost number that precedes the pair (if it exists), and add **left** to it.
* Find the leftmost number that follows the pair (if it exists), and add **right** to it.

```
reduce([1,[2,[3,[4,[5,6]]]]])   == [1,[2,[3,[9,0]]]]
reduce([1,[2,[3,[4,[5,6]]]],7]) == [1,[2,[3,[9,0]]],13]
```

To split a pair:
* Replace the value X with the pair ```[X/2, X/2 + (X%2)]```.

```
split([13,2])  == [[6,7],2]
split([3,[26,[12,5]]]) == [3,[[13,13],[12,5]]]
```

### Part One

Sum all of the pairs in the input in order, then find the magnitude.

Magnitude(pair) = 3.**left** + 2.**right**. Solve it recursively.

### Part Two
Find the maximum value for the sum of all pairs of numbers, (A,B), or (B,A).
Note that addition is not commutative.

This part of the question is really a joke.

<!---
start vis
--->



## Visualizations

Where I can, I will try to produce visualizations for the puzzles.

### Day 01: Sonar Sweep 
The seabed that we are scanning (The elf dropped the keys into marianas trench).

<img src="/images/01_out.png" alt="drawing" width="400">

### Day 02: Dive
Visualizations for part one, part two, and both overlaid.

<img src="/images/02_out_1.png" alt="drawing" width="400">

The graphs for part two and three have been scaled (vertically) down by 100x.

<img src="/images/02_out_2.png" alt="drawing" width="400">

For the graph with both overlaid, the path for part one has been scaled up 20x to be visible.

<img src="/images/02_out_3.png" alt="drawing" width="400">

### Day 03: Binary Diagnostic
There's really nothing exciting to look at for today. I produced some images anyway, but I don't want to embed them.

[ONE](/images/03_out_1.png) [TWO](/images/03_out_2.png) [SEVEN](/images/03_out_3.png) [THREE](/images/03_out_4.png)

### Day 04: Giant Squid
Again, there's nothing too exciting to see. Here's what the bingo boards look like when the first and last winner have been drawn.

For the samples:

<img src="/images/04_out_1s.png" alt="winner" width="604">

<img src="/images/04_out_2s.png" alt="loser" width="604">

For the actual input:

[WINNING](/images/04_out_1i.png) [LOSING](/images/04_out_2i.png)

### Day 05: Hydrothermal Venture

This one actually has some pretty pictures. Dissapoining that no art was hidden in it. My personal headcannon is that we're looking down at some lateral hydrothermal vents in a massive underwater tube/trench.

_Intersections highlighted orange._

<img src="/images/05_out_1i.png" alt="all" width="700">

_Only the intersections._

<img src="/images/05_out_2i.png" alt="intersections only" width="700">

_My image got nabbed for this, so I'm nabbing it back._

<img src="/images/05_art.png" alt="no refunds" width="700">


### Day 09: Smoke Basin

Finally a day where a visualization doesn't feel like a waste of time.

<img src="/images/09_out.png" alt="spooky pools" width="700">


### Day 11: Dumbo Octopus

The puzzle:

![puzzle](videos/day_11_output.webm)

Some fun:

![eric](output_eric_loop_fast.webm)

### Day 13: Transparent Origami

<img src="/images/six_letter.jpg" alt="jogger">

data/13_reward_02.txt
```
......................................................................................................................................................................................
............................................................████████████████████████████████....................................██████████████████████████████████████................
...........................................................██████████████████████████████████......................................██████████████████████████████████████.............
.........................................................████████████████████████████████████...........███.........................████████████████████████████████████████..........
........................................................███████████████████████████████████..............█████..........██...........█████████████████████████████████████████........
......................................................█████████████████████████████...████.................██████.........██.........█████████████████████████████████████████........
.....................................................████████......................███████...................███████........██........███████████████████████████████████████████████.
..........................................█████████████████████████████████████████████████....................█████████████████......████████████████████████████....................
............................█████████████████████████████████████████████████████████████████████..................███████████████....█████████████████████...........................
...............██████████████████████████████████████████████████████████████████████████████████████████.................█████████..██.█████████████.................................
......█████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████....█████......................................
.█████████████████████████████████████████..........████████████████████████████████████████████████████████████████████████████████████.....██.......................................
.██████████████████████████████████████████████████......███████████████████████████████████████████████████████████████████████████████.......███....█████████████████...............
.█████████████████████████████████████████████████████...█████████████████████████████████████████████████████████████████████████████████....████████████████████████████████......█.
.██████████████████████████████████████████████████████████████....████.....████████████████████████████████████████████████████████████████████████████████████████████.........███..
.███████████████████████...........██████████████████████████......███..████████████████████████████████████████████████████████████████████████████.....█████..............█████.....
.███████████████████.████.............██████████████████████.......████.████████████████████████████████████████████████████████████████████████████████............█████████.........
.███████████████████████████████..............███████████████.....███████████████████████████████████████████████████████████████████████████████████████████..███████████............
.██████████████████████████████████............██████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████.............
.███████████████████████████████████████....████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████..............
.█████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████............
.████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████.........
.██████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████.......
.█████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████....
.████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████.
.██████████████████...███████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████.
.███████████████.....████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████.
.███████████████████████████████████████████████████████████████████████████████████████████████████.████████████████████████████████████████████████████████████████████████████████.
.████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████.
.████████████████████████████████████████████████████████████████████████████████████.███████████████████████████████████████████████████████████████████████████████████████████████.
.███████████████████████████████████████████████████████████████████.██...██████████████..████████████████████████████████.██████████████████████████████████████████████████████████.
...█████████████████████████████████████████████████████████████████...████████████████.......█████████████.█.███████████..██████████████████████████████████████████████████████████.
......████████████████████████████████████████████████████████████████.....███..██............██.██...███.....█████████..██.█████████████████████████████████████████████████████████.
.........███████████████████████████████████████████████████████████............................█.......███.........██.....██████████████████████████████████████████████████████████.
.............█████████████████████████████████████████████████████████...............████................................████████████████████████████████████████████████████████████.
..................██████████████████████████████████████████████████...██...........█████.............................███████████████████████████████████████████████████████████████.
..........███████.......██████████████████████████████████████████████..................................................█████████████████████████████████████████████████████████████.
......███████..................██████████████████████████████████████.....................................................███████████████████████████████████████████████████████████.
....███████...........................████████████████████████████████....................................................███████████████████████████████████████████████████████████.
...██████...........................████████████████████████████████████................███████.........................█████████████████████████████████████████████████████████████.
..███████.........................█████████████████████████████████████████.............███████████..................████████████████████████████████████████████████████████████████.
.████████.......................███████████████████████████████████████████████...................................███████████████████████████████████████████████████████████████████.
.█████████...................██████████████████████████████████████████████████████...........................████████████████████████████████████████████..███.......................
.███████████.............███████████████████████████████████████████████████████████████.................█████████████████████████████████████████████████..████......................
.████████████████...████████████████████████████████████████████████████████████████████████.......███████████████████████████████████████████████████████..████......................
.████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████...████......................
.███████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████...█████......................
..████████████████████████████████████████████████████████████████████████████████████████████████████████████████...████████████████████████████████████.██████......................
....███████████████████████████████████████████████████████████████████████████████████....█████████████............███████████████████████████████..█████████........................
.......██████████████████████████████████████████████████████████████████████████████████...........................████████████████████████████..........███████.....................
.....█████████████████████████████████████████████████████████████████████████████████████████████..................█████████████████████████████............██████...................
...█████████████████████████████████████████████████████████████████████████████████████████......................███████████████████████████████..............█████..................
..█████████████████████████████████████████████████████████████████████████████████████████████.................█████████████████████████████████................███..................
.██████████████████████████████████████████████████████████████████████████████████████████████████..........██████████████████████████████████..................████.................
.███████████████████████████████████████████████████████████████████████████████████████.......█████████..██████████████████████████████████████................██████................
.█████████████████████████████████████████████████████████████████████████████████████████..........██████████████████████████████████████████████..............██████................
.███████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████............███████...............
.██████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████.............████████..............
.█████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████████.........██████████.............
.████████████████████████████████████████████████....███████████████████████████████████████████████████████████████████████████████████████████████████.......████████████...........
......................................................................................................................................................................................
```

data/13_reward_03.txt

```
......................................................................................................................................................................................
.████████████████████████████████████████████████████████████████████████████████████████████████████......................................██████████████████████████████████████████.
.████████████.....................████████████████████████████████████████████████████████████...................███████████████████████....█████████████████████████████████████████.
.█████..........................................████████████████████████████████████████...............█████████████████████████████████....█████████████████████████████████████████.
.███....████████████████████████████.....................██████████████████████████............█████████████████████████████████████████....█████████████████████████████████████████.
.██....██████████████████████████████████████████.......................................████████████████████████████████████████████████.....██████..........█████████........███████.
.██....███████████████████████████████████████████████████..............████████████████████████████████████████████████████████████████.....███..........██████████.........████████.
.██....██████████████████████████████████████████████████████████████..........................................█████████████████████████...............██████████.........███████████.
.██....████████████████████████████████████████████████████████.........................................................████████████████............██████████.........██████████████.
.██.....█████████████████████████████████████████████████.....................................................................██████████.........██████████..........████████████████.
.██.....██████████████████████████████████████████████............................................................................███████.........██████..........██████████..███████.
.██......█████████████████████████████████████████......................................................................................█████..................██████████.....███████.
.███.....██████████████████████████████████████............................................████.........................█..................█████............██████████........███████.
.███......██████████████████████████████████..............................................██████.......................███.....................████......██████████..........████████.
.███.......██████████████████████████████................................................████████.....................██████.....................████.......████..........███████████.
.████.......███████████████████████████.................................................██████████...................████████.......................████...............██████████████.
.████........████████████████████████..................................................███████████...................██████████..............██......█████..........█████████████████.
.█████........█████████████████████...................................................█████████████.................███████████..............█.........█████......█████████...███████.
.██████........██████████████████........................................█████........████████████.....................█████████.............██.........██████......████......███████.
.███████........████████████████........................................██████........██████.█████.....................██████████............███.........███████..............███████.
.████████.......██████████████..........................................██████.........█████.██████....................█████████████..........███.......█████████...........█████████.
.████████......██████████████..........................................███████........█████.█████████....................██████████..........██████..........██████......████████████.
.████████......█████████████...........................................████████......██████.███████████.................███████.█████..........██████.........██████.....████████████.
.█████████.....█████████████..........................................██████████....██████..███████████.................███████..█████.........████████.........██████....███.███████.
.████████......█████████████..........................................█████████....██████...████████████...............████████...██████.......█████████.....████.█████.......███████.
.████████.....█████████████...........................................████████.█████████████████████████..............█████████..███████........███████.......████.█████......███████.
.████████.....█████████████..........................................█████████.....████......████████████.............█████████....██████.███....███████......███████████.....███████.
.████████.....█████████████..█████...............................█████████████.....████......█████████████...........███████████.....█████...███.███████.......███████████...████████.
.███████......█████████████..█████..............................██████████████....████........████████████...........████████████....██████........██████.......███████████...███████.
.███████.......████████████..█████............................██████.█████████...████████....█████████████...........████████████.....██████......████████.......████.██████...██████.
.███████........███████████..█████............................████...█████████..███████.......█████████████..........███████████.......██████......███████........███..█████...██████.
.███████...........████████.██████..................................█████████████████..........█████████████........███████████..........█████.....████████........██...█████...█████.
.████████..........████████.██████.................................████████████████.............█████████████......████████████...........█████.....████████......████...████...█████.
.████████........██████████.██████................................████████████████...............██████████████...█████████████............████.....█████████.....████.....██...█████.
.████████......██████████████████..........█████.................████████.██████..................██████████████.████████...██..............████....█████████.....████...........████.
.████████....████████████████████....█....███████..............█████████..████.....................█████████████████████....██...............████....███████.......███...........████.
.███████....█████████████████████..██.....████████............█████████...███.......................██████████████████......█.................████...██████..████..████.........█████.
.█████.....█████████████████████...███...█████████............████████...............................████████████████.......█....███████.......███....█████.█████..████...██..███████.
.███.....███████████████████████..████..█████████.............███████.......██████████.................████████████......██...█████████████.....███..██████.█████..████.......███████.
.██.....████████████████████████.██████.████████..............██████.....█████████████████...██.........█████████......██...██████......██████...███.██████.█████..███........███████.
.█.....████████████████████████.██████.██████████............███████...████████.......██████....█........█████............████............█████..██████████.█████..███.......████████.
......█████████████████████████.██████.███████████..........████████.███████..............████...........................███................█████.█████████.█████..███....███████████.
.....█████████████████████████.██████.██████████████.........████████████...................███..........................██.................█████████████████████.████...████████████.
.....█████████████████████████████████.███████████████......████████████████..........................................................███████████████████████.███.███...█████████████.
.....██████████████████████████████████.███████████████.....██████████████████████..................................................█████████████████████████.██.████...███...███████.
.....██████████████████████████████████████████████████....█████████████████████████...............................................███████████████████████████.█████...██.....███████.
..............██████████████████████████████████████████...█████████████████████████................................................███████████████████████████████...........███████.
.................████████████████████████████████████████..████████████████████████....................................................██████████████████████████...........█████████.
.█................████████████████████████████████████████.██████████████████████.....................................................................██████████..........███████████.
.█....█.............███████████████████████████████████████.████████████████...........................................................................█████████.......██████████████.
.....███████..........██████████████████████████████████████.█████████..................████████████████████████........................................█████████......██████████████.
.....█████.....██.........███████████████████████████████████.█████████...............████████████████████████████████████.............................██████████......████...███████.
.....██████......█.██.....█████████████████████████████████████████████..............██████████████████████████████████████...........................████████████............███████.
.....█..████.......█...█..██████████████████████████████████████████████............███████████████████████████████████████........................███████████████............███████.
.....████████.......█.....██████.███████████████████████████████████████............██████████████████████████████████████......................██████████████████..........█████████.
......█████████.....██....█████....██████████████████████████████████████...........█████████████████████████████████████...................██████████████████████.......████████████.
......█.███████████████....███████..████████████████████████████████████████.........████████████████████████████████████..............█████████████████████.█████......█████████████.
.██....██████████.....██....████.█.....███████████████████████████████████████████....█..█████████████████████████████..........███████████████████████████...████......█████.███████.
.███████████████.......███...██...█......███████████████████████████████████████████████████........███████████.......███████████████████████████████████......████.....██....███████.
.███.....███████.........██████...█...........█████████████████████████████████████████████████████████████████████████████████████████████████████████.........███...........███████.
.██....███████.██.........████.....█..............█████████████████████████████████████████████████████████████████████████████████████...█████████.............██............███████.
.██...█.███████████........██......█................████████████████████████......██████████████████████████████████████████████████.....██████............................██████████.
.██....██████████████......███...█.█...............██████████████████████................█████████████.████████████....██████............................█████.............██████████.
.███...█████████████.███████████.█.█...............█████████████████████.......................███████████████........████.........................███...███████████████...██████████.
.█████...███..████.....█.███████.█.█..............███████████████████████████........................██████..................████████..........███......███████████████...██..███████.
.██████.......██........█████████..██............████████████████████████████████.......................██████............████████..........███...████████████████████..█.....███████.
.████████████████........█..███.█..█.............███████████████████████████████████.......................██..██.....██████████.............█.....███████████████████........███████.
.█████.██████████.........██...................████████████████..........██████████████......................██..█...████████............███.............███████████████.....████████.
.████████████████..........█.................██████████████...................████████████....................█...█...████...........██████████..........████████...█████...█████████.
.███...███████████..........█..............██████████████........................████████████.................██..██.............██....██████.......███..█████..............█████████.
.███......███████████....█..██............██████████████...........................██████████████.............██...█....███████████.........█...████████..██...██████████████████████.
.████.........██..█████████..█..........███████████████..............................█.█████████████..........███..██....████████████.███████████████████...█████████████████████████.
.█████..........███..█.................████████████████...............................█..██████████████.......█████.██...████████████████████████████████████████████████████████████.
......................................................................................................................................................................................
```

## Bonus

### 2018

Puzzles from the year 2018.

### Day 15
The two requirements to beating this are being able to follow directions and implementing a map searching algorithm effectively.

#### Map Search
First, we can easily define a map based on any set of input strings by assigning each token to (token, x, y). For this specific puzzle, it helps to not define the elves and goblins as part of the map, but just assign them as empty space and keep track of them seperately.

It will help to define a single function to get the neighbors of any pair.

Then to get a path, consider first a Metric:
```
num_moves;
location(x,y);
dist; //manhattan
score;

compareTo(metric) = (left, right) ->
		  (left.score, right.score,
		  left.moves, right.moves,
		  left.dist, right.dist,
		  left.location.y, right.location.y,
		  left.location.x, right.location.x);


init :: score = num_moves + dist;
```

So the metric sorts first on score, then the number of moves (we want to minimize this), then the distance remaining, then it sorts on `reading order` (from the puzzle).

Then to find out the distance, consider a function

```
dist(from(x, y), to(x, y), f_pathable(ch -> bool), f_neighbors(ch -> bool):

    if(from == to)
        return 0;

    if(!f_pathable(get(from)) || !(f_pathable(get(to))))
        return null;

    TreeSet<Metric> metrics = new TreeSet<>();
    metrics.add(new Metric(dist=0, from, to));

    HashMap<IntPair, Integer> best = new HashMap<IntPair, Integer>();
    
    while(metrics.size() > 0) {
        var metric = metrics.pollFirst();
	var old_score = best.containsKey(metric.location);
	if(old_score <= null && old_score <= metric.score)
	    continue;

        best.put(metric.location, metric.score);
	
        if(metric.location.equals(to))
	    return metric.num_moves;

        var pathable_neighbors = f_neighbors(metric.location).filter(f_pathable);

        for(var neighbor : pathable_neighbors)
	    metrics.add(new Metric(metric.moves + 1, neighbor, to));
    }

    return null;
```
		  
So now we can find distances in the map, so long as we pass the arguments ```f_pathable == !'#'``` and ```f_neighbors(loc) = adjacent_neighbors && not_a_unit(loc)```. Note that it may be worthwhile to have not_a_unit ignore the source/target.

Now all that you need to do is follow the directions in the puzzle, or:

* construct a map
* define all of the units

```while(actions_possible)```

* sort all alive units by reading order
* construct an occupancy grid for the units
* while active units in turn is not empty:
* select the active unit
* check the unit is alive
* find the set of all targets for the unit - if any are adjacent, skip straight to attack
* find the optimal position to move to
* find the optimal first move for that position
* **attack**: pick the adjacent target with the lowest health
* break ties by reading order
* remove health from that unit equal to attack
* if that unit is dead, remove it from the occupancy grid

If a round finishes without a valid move being taken, the combat is over, and you can count hp.

#### Part Two
Just perform a binary search. It will take at most 6-7 runs to find the optimal answer.

### 2021_51

This is just a generator that takes as input ascii art, and gives as output puzzles for day 13 2021.
