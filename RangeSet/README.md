# RangeSet

A text range set implementation.

Provides APIs for:

1. Addition of a text range to the set being tracked.
2. Deletion of a text range from the set being tracked.
3. Query on whether a specific string is inside the set of ranges being tracked.

## Implementation 

Implemented using two range sets one that tracks all allowed/included ranges and one that tracks all excluded ranges. The tracked ranges are checked and resized as needed when ever a new range is added or deleted.  


For the within check first the given text string is checked in all allowed/include ranges and only if found then a second check of exclude list is done before confirming.  


The two separate tracking sets allows an easier to understand implementation.


## Build 

```
$ javac *.java 
```

## Run and test 

```
$ java Main 
```

## Using Docker

```
$ javac *.java 
# docker build -t rangetest .
```

```
# docker images | grep rangetest
rangetest           latest              e41606141bdb        24 seconds ago      102.7 MB
```

```
# docker run rangetest
...
INFO : Adding a new range AaA, BaB ...
INFO : [AaA, BaB] is added as a new range
true
false
INFO : Adding a new range C, F ...
INFO : [C, F] is added as a new range
INFO : Adding a new range I, K ...
INFO : [I, K] is added as a new range
RANGE: A B C D E F G H I J K L M
FOUND:     * * * *     * * *
...
...
...
```

## Tests 

### The test cases uses single char sets for easy visual verification

```
INFO : Adding a new range C, F ...
INFO : [C, F] is added as a new range
INFO : Adding a new range I, K ...
INFO : [I, K] is added as a new range
RANGE: A B C D E F G H I J K L M
FOUND:     * * * *     * * *
```

### The implementation uses logging to show internal execution logic.

```
INFO : Adding a new range C, F ...
INFO : [C, F] is added as a new range
INFO : Adding a new range I, K ...
INFO : [I, K] is added as a new range
```

```
INFO : [C, H] is added as a new range
INFO : Adding a new range F, K ...
INFO : [F, K] is partially contained in existing range [C, H], will resize this range to include the new range
INFO : Updated range [C, H] -> [C, K]
```

```
INFO : [C, K] is added as a new range
INFO : Deleting a new range F, H ...
INFO : [F, H] is added as a new range
INFO : Adding a new range C, K ...
INFO : [C, K] contained in existing range [C, K]
```

## TODO

- Add optional compaction of the ranges while adding/delete a new range
- Add interval range set notation support to print the range set using mathematical set notation
- Add thread safety test cases
- Add a describe method that will print out the current state of range set 
