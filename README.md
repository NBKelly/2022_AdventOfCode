# ADVENT OF CODE 2021

This is the set of all my 2021 Advent of Code solutions. (At some point later they will be cleaned). They are all produced in java, using my (very awful) workflow tools and hacks - I significantly improved and rewrote these since last year.

#  Table of Contents
1. [Project Structure](#Project-Structure)
2. [Lore](#Lore)
3. [Problem Ratings](#Problem-Ratings)
4. [Solutions](#Solutions)
5. [Visualizations](#Visualizations)

## Project Structure
All of the solutions are available in the ```com/nbkelly/advent``` folder, and can be run using the ```run.sh``` script, like so:

1. Run with input file - ```./run.sh 01 input.txt```
2. Run with optional args, such as debug mode - ```./run.sh 01 input.txt --debug```

## Lore
Here's a brief summary of the 2021 advent of code **deep** lore.

| Problem | Plot |
| :-----: | :--- |
| Day 01  | You're minding your own business on a boat when Santa (now black) has his keys go flying over the edge. To find his keys, we start by performing a depth analysis of the ocean floor using a handy submarine. Because this measurement wasn't useful, we then need to smooth/aggregate that data.
| Day 02  | Today we learnt to pilot the submarine. The course is pre-programmed, so we just need to figure out where we are going. To make matters worse, the manual is in chinese, so we calculated the wrong number the first time.
| Day 03  | The submarine appears to be faulty (cheap chinese merchandise), so we run a diagnostic. We're concerned with the **power consumption** and the **life support rating** of the vessel.
| Day 04  | 1.5km below the surface, a giant squid has grappled our submarine (revenge for chinese fishery practices). Eric decides it wants to play bingo. We use the bingo program on the ship to print a set of boards and draws, and then game the system to see the best ways to win and lose.
| Day 05  | We've come accross a network of hydrothermal vents on the ocean floor. To get through safely, we must map them. Hey where did that octopus go? We never found out what he wanted?

## Problem Ratings
Here are my ratings for each problem, and what the time complexity of the solutions happens to be. If I use the letter N, it's line count (unless otherwise noted).

| Problem | Complexity (Part One) | Complexity (Part Two) | Comments |
| ------- |:---------------------:|:---------------------:|:-------- |
| Day 01  | *O(N)*                  | *O(N)*      | A good solution should read the input from a file one line at a time - this would allow processing of arbitrarily large files. <br/> Eric should have had the window size be larger, say 5 or 10, to force some of the programmers to ask "is there an easier way".
| Day 02  | *O(N)*		    | *O(N)*	  | This problem is pretty straightforward. It looks messy if you use the same data twice. I quite like this problem.
| Day 03  | *O(N.B)*		    | *O(N.B)*	  | *B = Bit-length* - If it wasn't for the absurd walls of text, this would be a pretty straightforward problem. Part one is simple consensus, part two is closer to finding a dominant taxa from a character table. There's a bad solution to part 2, and a nice solution. For real though, it's unbelievable how absurdly obfuscated the text is for this one.
| Day 04  | *O(log<sub>2</sub>(N).S.B)* **OR** *O(B + Nlog<sub>2</sub>N)*	    | *O(log<sub>2</sub>(N).S.B)* or *O(B + Nlog<sub>2</sub>N)* | *N,S,B = number of draws,size(num_cols) of the board, and number of boards* - This was a fun problem. Parsing may be hard for people not using python or java, specifically because eric decided to left pad numbers on the bingo boards (why couldn't they just leave spacing up to the user to pretty print?). Fuck you eric for not counting the diagonals. The second time complexity is for when you solve the bingo boards through condensing them into "I win at this index". You have to sort them still, so it ends up being *Nlog<sub>2</sub>N*
| Day 05  | *O(N.L)*		    | *O(N.L)*	| *number of lines, average line length* - Note that there is a better way to do this, which involves a linear sort on all of the line segments, so you can scan left to right picking out all the intersections. This problem produces some pretty pictures.

## Solutions

### Day 01: Sonar Sweep
#### Part One: Count how many times a list increments.

Simply read through the list, and note every time ```li(i) > li(i-1)```.

#### Part Two: Count how many times a sliding window (of size 3) for the list increments.

The same as above, but note every time ```li(i) > li(i-3)``` instead.

Whenever you move on the sliding window, you are adding the element at *i*, and removing the element at *i-3* from the sum of that window, so you can determine the value of that change by directly comparing those two elements.

More visually, we are making the comparison ```[i-3] + [i-] + [i-1] < [i-2] + [i-1] + [i]```. It is trivial to see that we can factor this out to ```[i-3] < [i]```.

### Day 02: Dive!
#### Part One: Manhattan distance

Simply keep a tally of ```loc``` and ```depth```. This is a simple switch statement on your input.

#### Part Two: Manhattan product

Just keep track of an aim variable too.

This is mostly the same procedure, you just move all your state changes into the handler for ```forward``` - ```up``` and ```down``` only change your aim.

### Day 03: Binary Diagnostic
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

The simple way to do this is simply to put all line segments into a hashmap (cell by cell), and mark any point that gets intersected in a hashset.
The answer is simply the size of that hashset.

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

Intersections highlighted orange.

<img src="/images/05_out_1i.png" alt="all" width="700">

Only the intersections.

<img src="/images/05_out_2i.png" alt="intersections only" width="700">
