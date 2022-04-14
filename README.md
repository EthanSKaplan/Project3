# Problem Statement

Programming Assignment 3
Due Apr 13 by 11:59pm Points 100 Submitting a text entry box, a website url, or a file upload
COP 4520 Spring 2022

Programming Assignment 3

Note 1:

Please, submit your work via Webcourses.

Submissions by e-mail will not be accepted.

Due date: Wednesday, April 13th by 11:59 PM

Late submissions are not accepted.

Note 2:

This assignment is individual.

You can use a programming language of your choice for this assignment.

If you do not have a preference for a programming language, I would recommend C++.

## Problem 1: The Birthday Presents Party (50 points)

The Minotaur’s birthday party was a success. The Minotaur received a lot of presents from his guests. The next day he decided to sort all of his presents and start writing “Thank you” cards. Every present had a tag with a unique number that was associated with the guest who gave it. Initially all of the presents were thrown into a large bag with no particular order. The Minotaur wanted to take the presents from this unordered bag and create a chain of presents hooked to each other with special links (similar to storing elements in a linked-list). In this chain (linked-list) all of the presents had to be ordered according to their tag numbers in increasing order. The Minotaur asked 4 of his servants to help him with creating the chain of presents and writing the cards to his guests. Each servant would do one of three actions in no particular order:

Take a present from the unordered bag and add it to the chain in the correct location by hooking it to the predecessor’s link. The servant also had to make sure that the newly added present is also linked with the next present in the chain.
Write a “Thank you” card to a guest and remove the present from the chain. To do so, a servant had to unlink the gift from its predecessor and make sure to connect the predecessor’s link with the next gift in the chain.
Per the Minotaur’s request, check whether a gift with a particular tag was present in the chain or not; without adding or removing a new gift, a servant would scan through the chain and check whether a gift with a particular tag is already added to the ordered chain of gifts or not.
As the Minotaur was impatient to get this task done quickly, he instructed his servants not to wait until all of the presents from the unordered bag are placed in the chain of linked and ordered presents. Instead, every servant was asked to alternate adding gifts to the ordered chain and writing “Thank you” cards. The servants were asked not to stop or even take a break until the task of writing cards to all of the Minotaur’s guests was complete.

After spending an entire day on this task the bag of unordered presents and the chain of ordered presents were both finally empty!

Unfortunately, the servants realized at the end of the day that they had more presents than “Thank you” notes. What could have gone wrong?

Can we help the Minotaur and his servants improve their strategy for writing “Thank you” notes?

Design and implement a concurrent linked-list that can help the Minotaur’s 4 servants with this task. In your test, simulate this concurrent “Thank you” card writing scenario by dedicating 1 thread per servant and assuming that the Minotaur received 500,000 presents from his guests.

## Problem 2: Atmospheric Temperature Reading Module (50 points)

You are tasked with the design of the module responsible for measuring the atmospheric temperature of the next generation Mars Rover, equipped with a multi-core CPU and 8 temperature sensors. The sensors are responsible for collecting temperature readings at regular intervals and storing them in shared memory space. The atmospheric temperature module has to compile a report at the end of every hour, comprising the top 5 highest temperatures recorded for that hour, the top 5 lowest temperatures recorded for that hour, and the 10-minute interval of time when the largest temperature difference was observed. The data storage and retrieval of the shared memory region must be carefully handled, as we do not want to delay a sensor and miss the interval of time when it is supposed to conduct temperature reading. 

Design and implement a solution using 8 threads that will offer a solution for this task. Assume that the temperature readings are taken every 1 minute. In your solution, simulate the operation of the temperature reading sensor by generating a random number from -100F to 70F at every reading. In your report, discuss the efficiency, correctness, and progress guarantee of your program.

 

Grading policy:

General program design and correctness: 50%

Efficiency: 30%

Documentation including statements and proof of correctness, efficiency, and experimental evaluation: 20%

 

Submission:

You will submit a link to your GitHub page. Your repositories must be private until the next morning. You must still push your code before the deadline, because Github will record this time. No code pushes will be accepted after the deadline. However, we wont start grading until the next morning.

If we cannot access your repositories, or if you provide an invalid link, you will receive a 0 - please double check your submission once it has been made.

Late submissions will receive a 0 as per the syllabus.

This GitHub management does two good things:  

1.  Removes the temptation to look at other's work, because they will all be private until after the deadline.

2.  Makes your life easier, as you don't have to send us invites

Also, we cannot accept any late work as all the repos will be potentially public shortly after the deadline.

Happy coding!

# Project3
Project 3 for COP4520

## Problem 1

To run:
```
javac Threads.java
java Threads
```

> Unfortunately, the servants realized at the end of the day that they had more presents than “Thank you” notes. What could have gone wrong?

The servants seem to have missed some presents while removing presents from the chain.

This is because the servants were told not to wait until all of the presents from the unordered bag were placed in the chain of linked and ordered presents before removing them to write thank you cards. So, to fix this problem, we need to implement make them wait this time.

As per the instructions, this is hardcoded with 4 threads and an input of 500,000. I have a commented out line that sets the input to 20 in case the grader would like to see some output.

### My Solution

1. Simulate unordered bag with shuffled ArrayList
2. Use LockFreeList class from 9.8 to store presents.
3. Use a synchonized counter so two threads don't accidentally add the same present.

First, all 4 threads are going to work together to get the unorderd presents into the chain.
Once that's done, all 4 threads will switch to writing thank you cards.

I'm using a synchonized counter that doesn't use locks that I modified from [Baeldung](https://www.baeldung.com/java-atomic-variables) to keep track of what present we're at when enqueueing and dequeueing. I actually have two counters; one for the enqueueing and one for the dequeueing. This is so I don't have to stop the threads manually and restart them to start the dequeue process. 

### Timing the function
Input | Milliseconds
-- | --
50 | 58
500 | 298
5000 | 1078
50000 | 7735
500000 | 239939

Here's some example output for n = 500,000.
```
Servant 2 is adding presents. (enqueueing)
Servant 3 is adding presents. (enqueueing)
Servant 1 is adding presents. (enqueueing)
Servant 4 is adding presents. (enqueueing)
Servant 4 has begun writing thank you notes. (dequeueing)
Servant 3 has begun writing thank you notes. (dequeueing)
Servant 1 has begun writing thank you notes. (dequeueing)
Servant 2 has begun writing thank you notes. (dequeueing)
Writing thank you cards for 500000 presents took 245738ms.
```

## Problem 2

The atmospheric temperature module has to create an hourly report of three things.
1. The top 5 highest temperatures recorded for that hour.
2. The top 5 lowest temperatures recorded for that hour.
3. The 10-minute interval of time when the largest temperature difference was observed. 

We are given 8 threads and must write to shared memory space.

We simulate the data by generating a random number in the range [-100, 70] every minute. Minutes in our simulation have nothing to do with time. They will just represent a frequency of 60 readings/report.

### Hourly report
Every minute, each thread 
1. Gets a random temperature, 
2. Records the temp in the shared memory.

Every hour, 
1. The threads finish together,
2. Math is done to figure out the highs, lows, and 10-minute interval,
3. The report is compiled and printed from that information.

8 threads recording 1/minute means 8*60 readings/hour. For a reasonably small input such as 480, I think the best solution is to just record all the temperatures into an array, sort the array, and grab the top 5, bottom 5. 

But how do we efficiently calculate the 10 minute interval with the highest temperature change?

### My solution to interval issue
**The code for this solution is in LargestTemperatureRange.java**

To get the 10-minute range, observe that 10 minutes is 8*10 readings. So the question is, what sub-sequence of length 80 in the array had the largest temperature difference?

In an array of 480, there are 401 places to start a sub sequence of 80 (iterate through the array and take the 79 ints after it). So you have 401 hypothetical subarrays. On any given subarray, the way to get the largest temp difference is iterate through the list and get the `diff = max - min`. We can compare new diffs to old diffs as we iterate through sub-sequences so at the end we're left with the largest one.

But after you get diff[0] from ss[0] (the first sub-sequence), to calculate ss[1] you have to iterate over 79 of the same numbers. Is there any way to not have to redo that work?

One way is to save the index of max and min from ss[0]. If you do that, then you can simply check if they're also in ss[1]. Now, ss[1][-1] doesn't have to be compared with all of ss[0], just max[0] and min[0] to see if it beats either of those. If it does, calculate a new diff. If it doesn't, move on.

But what if max[0] and min[0] aren't in ss[1]? Now those are lost to ss[1] which means we have to start over.

Since we can't use them for comparison anymore (they necessarily won't be in any future ss's), we can discard them and replace them with min[1] and max[1]. Then we just continue.

With this approach, we should cut down the work by a factor of 80, because once a very large or very small number enters a ss, it'll take the length of that ss to leave it. And only then will we have to calculate new values for max and min using the brute force iterative method.

## Efficiency and Correctness
I'm very pleased with this solution. Here's an example output.

```
REPORT FOR HOUR 0:
============================
The top 5 highest temperatures were:
69 69 68 68 67 

The top 5 lowest temperatures were:
-109 -109 -109 -108 -108 

The largest 10-minute temperature difference occured between minutes 6 and 24
The temperature went from -109 to 69 for a difference of -178F
============================


REPORT FOR HOUR 1:
============================
The top 5 highest temperatures were:
69 69 69 68 67 

The top 5 lowest temperatures were:
-109 -109 -108 -108 -108 

The largest 10-minute temperature difference occured between minutes 0 and 6
The temperature went from -109 to 69 for a difference of -178F
============================


REPORT FOR HOUR 2:
============================
The top 5 highest temperatures were:
69 69 69 69 69 

The top 5 lowest temperatures were:
-109 -109 -109 -108 -108 

The largest 10-minute temperature difference occured between minutes 9 and 22
The temperature went from 69 to -109 for a difference of 178F
============================


REPORT FOR HOUR 3:
============================
The top 5 highest temperatures were:
69 69 69 69 68 

The top 5 lowest temperatures were:
-109 -109 -109 -109 -108 

The largest 10-minute temperature difference occured between minutes 2 and 29
The temperature went from -109 to 69 for a difference of -178F
============================


REPORT FOR HOUR 4:
============================
The top 5 highest temperatures were:
69 68 68 68 68 

The top 5 lowest temperatures were:
-109 -109 -109 -109 -109 

The largest 10-minute temperature difference occured between minutes 2 and 40
The temperature went from -109 to 69 for a difference of -178F
============================


Recording the temperature for 5 hours took 153ms.
```

Even with all the print statements and report generating, this only took 153ms. I think my efficient solution to the problem of getting the intervals really helped speed this code up.