<h1 align="center">
  <b>sudoku-solver</b>
</h1>

<p align="center">
  🔢 Sudoku Solutions Enumerator (Sequential and Parallel)
  <br/>

  <sub>
    Coded by <a href="#authors">Simone Primarosa</a> and <a href="#authors">Q. Matteo Chen</a>.
  </sub>
</p>

## Introduction to Sudoku
<img src="media/sudoku.gif" width="250" align="right" alt="Example Sudoku Board" />

Sudoku is a popular puzzle game usually played on a 9x9 board of numbers between
1 and 9.

The goal of the game is to fill the board with numbers. However, each row can
only contain one of each of the numbers between 1 and 9. Similarly, each column
and 3x3 sub-board can only contain one of each of the numbers between 1 and 9.
This makes for an engaging and challenging puzzle game.

A well-formed Sudoku puzzle is one that has a unique solution.
A Sudoku puzzle, more in general, can have more than one solution and our goal
is to enumerate them all, but this task is not always feasible.
Indeed, if we were given an empty Sudoku table, we would have to enumerate
[6670903752021072936960 solutions][ref:sudoku-board-num], and this would take
thousands of years.

### Definitions
In the following sections we will use some letters or words to refer to specific
aspects of the Sudoku problem. The following table summarizes the most important.

Term | Description
-----|------------
S | A perfect square indicating the number of columns, rows, and boxes of a Sudoku board.
N | The total number of cells of a board given as N = S * S.
B | The size of a single box of the matrix as B = √S
Board | An instance of Sudoku represented with a S x S matrix.
Row | A row of a board's matrix that can only contain one of each of the numbers in [1, S].
Column | A column of a board's matrix that can only contain one of each of the numbers in [1, S].
Box | A particular B x B sub-matrix of a board's matrix that can only contain one of each of the numbers in [1, S].
Cell | A single entry of a board's matrix either empty or with a legal assignment.
Empty Cell | A cell whose assignment has still to be found.
Cell's Candidates | A list of values in [1, S] which can be legally placed in a particular cell.
Search Space | The productory of the candidates of all the empty cells.
Solution | An assignment of values for all the empty cells of a board that satisfies the constraints.

## Solving Algorithm
A common algorithm to solve Sudoku boards is called
[backtracking][ref:backtracking]. This algorithm is essentially a
[depth-first search][ref:dfs] in the tree of all possible guesses in the empty
cells of the Sudoku board.

### Sequential Backtracking
The sequential algorithm, that can be found in
[`src/main/java/sudoku/SequentialSolver.java`][source:sequential], is
implemented iteratively and can be summarized by the following pseudo-code.

```python
def sequential_solutions_counter(board):
  stack = []

  if board.is_full(): return 1
  (row, col) = board.get_empty_cell()
  stack.push((row, col, EMPTY_CELL_VALUE))
  for val in board.get_candidates(row, col): stack.push((row, col, val))

  count = 0
  while len(stack) > 0:
    (row, col, val) = stack.pop()
    board.set_cell(row, col, val)
    if val == EMPTY_CELL_VALUE: continue

    if board.is_full(): count += 1; continue
    (row, col) = board.get_empty_cell()
    stack.push((row, col, 0))
    for val in board.get_candidates(row, col): stack.push((row, col, val))

  return count
```

It's important to notice that the strategy used to pick the empty cell by the
`get_empty_cell` can lead to [significant reduction][ref:look-ahead] of the
total search space and thus in the time needed to enumerate all the solutions.

Another notable thing to consider is that the time complexity and space
complexity of all the operations on the board inside the while loop
(`is_full`, `set_cell`, `get_empty_cell`, `get_candidates`)
can significantly impact the overall performance of the backtracking and thus
has to be kept as efficient as possible.

More details about the computational complexity of the operations and the idea
behind their implementation can be found in the
[implementation details](#implementation-details) section.

### Parallel Backtracking
The parallel algorithm, that can be found in
[`src/main/java/sudoku/ParallelSolver.java`][source:parallel], is
implemented by parallelizing the recursive guesses of each empty cell and can be
summarized by the following pseudo-code.

```python
def parallel_solutions_counter(board, move):
  if move is not null:
    (row, col, val) = move
    board = board.clone()
    board.set_cell(row, col, val)

  if board.is_full(): return 1

  space = board.get_search_space_size()
  if space == 0: return 0
  if space <= SEQUENTIAL_CUTOFF:
    return sequential_solutions_counter(board)

  count = 0
  (row, col) = board.get_empty_cell()
  parallel for val in board.get_candidates(row, col):
    count += parallel_solutions_counter(board, (row, col, val))

  return count
```

The same consideration given for the
[sequential backtracking](#sequential-backtracking) also holds for the parallel
version.
In addition to those, it's worth mentioning that two new methods have been
introduced (`get_search_space_size` and `clone`).

The presence of those new methods and the creation of a thread for each branch
of the backtracking are the major cause of overhead.
The sequential algorithm is indeed used as sub-routine to speedup the
computation, when the remaining search space is below a certain threshold
empirically found.

More details about the computational complexity of the operations and the idea
behind their implementation can be found in the
[implementation details](#implementation-details) section.

### Implementation details

#### Check if the board is completed in constant time
The naive approach is to loop through the whole board and check whether or
not there is an empty cell.

A better approach is to keep track of the number of the filled cells of the
board (also called clues) and compare it against the total number of cells of
the board.

The number of clues is initially set to `0` and gets increased or decreased
appropriately after each `set_cell` operation.

So, at the small cost of a constant additional work inside the `set_cell` and an
overall additional constant memory usage, we reduced the time complexity for the
`is_full` method from `O(N)` to `O(1)`.

#### Check if a value is legal for a cell in constant time
The naive approach is to iterate on the row, column, and box of the given
cell searching for the specific value not to be present in any of them.

But if we somehow keep track of the used values on each row, column, and box of
the board we can avoid looping through all the cells like the naive approach
does. To do so it's sufficient to keep a bit-set of `S` bits for each
row, column, and box.
Then each time a specific cell's value `v` is set, we also set the bit at
position `v - 1` of the 3 bit-sets for the particular row, column, and box of the
given cell.
Finally to check whether a value `v` is valid or not we just check if the bit at
position `v - 1` is not set in any of the 3 bit-sets for the particular row, column,
and box of the given cell.

So at the cost of a constant additional work inside the `set_cell` and an
overall additional memory usage of `O(S)`, we reduced the time complexity for
the `is_valid_calindate` method from `O(S)` to `O(1)` and thus reduced the
complexity of the `get_candidates` method from `O(S^2)` to `O(S)`.

#### Count the number of candidates of a cell in constant time
Given the way we implemented the `is_valid_calindate` method we can easily
count the valid candidates for a given cell by iterating on all the possible
values counting the ones valid.

Lets say that for the cell we want to count the candidates, the state of the
3 bit-sets is the following.

value    | 9 | 8 | 7 | 6 | 5 | 4 | 3 | 2 | 1
---------|---|---|---|---|---|---|---|---|---
row      | 1 | 0 | 1 | 1 | 0 | 0 | 0 | 0 | 0
column   | 0 | 1 | 0 | 0 | 0 | 0 | 0 | 0 | 0
box      | 0 | 0 | 0 | 0 | 0 | 1 | 1 | 0 | 0

If we compute the bitwise or operation of the 3 bit-sets we obtain a new bit-set
that has a 1 on every invalid candidate as showed below.

value    | 9 | 8 | 7 | 6 | 5 | 4 | 3 | 2 | 1
---------|---|---|---|---|---|---|---|---|---
invalid  | 1 | 1 | 1 | 1 | 0 | 1 | 1 | 0 | 0

At this point, counting the number of candidates has been reduced to the problem
of counting the zeros of a bit-set.
If we use integers to represent our bit-sets then we could use the integer given
by the binary representation of the bit-set to accesses a pre-computed table in
constant time that gives us the answer of how many zeros or ones that particular
number has.

The pre-computed table has to be built only once and can be shared by all the
boards instantiated.

This tricky optimization allows us to get the number of candidates in `O(1)` but
requires an additional memory usage of `O(2^S)` shared among all the boards.

#### Get the next empty cell in constant time
The naive approach is to iterate over the board looking for the first empty cell.

A better strategy involves the use of an array of `S` elements that contains
the column index of the next empty cell of each row.
If we use such additional array and we also keep track of which is the first row
having an empty cell then we can easily get the empty cell in constant time.

To maintain the array update on each set operation we update the next empty cell
for the row where the value has been set and if the row has no more empty cells
we update the variable that tells us which is the first row that contains an
empty cell.

So at the cost of an additional work of `O(S)` inside the `set_cell` and an
overall additional memory usage of `O(S)`, we reduced the time complexity for
the `get_empty_cell` method from `O(N)` to `O(1)`.

#### Get the cell with the lowest number of candidates in constant time
As we mentioned in the sections above, the strategy used to pick the empty cell
can impact significantly on the size of the search space.
Indeed, the more is the number of legal candidates for a cell the lower is the
probability that our guess for that cell will result correct.

Thus is intuitively better to always try to guess values for cells that has
the lowest number of candidates.

To do this without affecting the current complexity of the `get_empty_cell`
method we used an array of `S` elements that contains the
column index of the cell with the lowest number of candidates of each row.
If we use such additional array and we also keep track of which is the row
that has the cell with the lower number of candidates then we can easily get
the empty cell in constant time.

To maintain the array update on each set operation we do three things.
1) For each row try to update the column index of the cell with the lowest
number of candidates with the the column where the value has been set and
eventually update the variable that stores where is the row index of the best empty cel.
2) For the row where the value has been set try to update the column index with
all the columns of that row and eventually update the variable that stores where
is the row index of the best empty cel.
3) For the rows of the box where the value has been set try to update the column
index with all the columns of that box and eventually update the variable that
stores where is the row index of the best empty cel.

So at the cost of an additional work of `O(S)` inside the `set_cell` and an
overall additional memory usage of `O(S)`, we have potentially reduced the
search space by many orders of magnitude.

#### Optimized addition and multiplication with BigInteger
In Java BigInteger objects are immutable and thus every time an operation is
executed on them a new object is instantiated.

We implemented two modified versions of the BigInteger class, namely
BigIntSum and BigIntProd, that are mutable BitInteger which allow us to do sums
and products with less overhead in the average case.

#### Parallelize branches using the fork/join framework

We choose the ForkJoin framework for Java because it is easy to reason with and with nice theoretical guarantees.

A recursive task can model fairly well a backtracking solver. Each backtracking choice can be tested concurrently forking on each choice.

To minimize the overhead of task creation we employ to common strategies in the fork join realm:

- Reusing the same task rather than on a fork to simulate the first choice.
- Choosing a sequential cutoff

#### Parallelize board copy
<!-- Pass the "delta" rather than a modified board -->
The Board object has to be modified after filling a cell, so each work item has to have it's local instance of a Board.

Duplicating a Board is an expensive operation so instead of doing it eagerly in the constructor of a RecursiveTask, we make a copy in the compute method.

In the constructor of our RecursiveTask we only pass the change that we want to try.

In this way we offload an expensive computation on the forks, decreasing the Span.

#### Compute the search space

We can estimate the search space multiplying the number of candidates of each empty cell.
Since it is an expensive task we tried to speed it up.
<!-- Multiply at groups of log(Long.MAX_VALUE)/log(9). -->
<!-- Update on set with by dividend. -->

#### Choose of the appropriate sequential cut-off

We experimented with different ways to determine the cutoff:

- recursion depth
- still empty cells
- estimated search space

We measured limited differences when choosing one of these parameters, provided that we optimize the value of the cutoff accordingly.

This result is readily explained by the correlation graph in section **Speedups obtained**, that is the search space and empty cell have almost perfect linear correlation. Thus one can be used to estimate the other.

Concretely the optimal sequential cutoff can be found looking at processor utilization patterns: it should be full during most of the computation and all the cpu should complete their task at the same time.
The minimum sequential cutoff also has to consider the task creation overhead.

## Experiments

### Testing environment

To get a scalable and homogeneous environment and reproducible results, we leveraged Google cloud infrastructure

| os    | core | cpu                            |
|-------|------|--------------------------------|
| Linux | 2    | Intel(R) Xeon(R) CPU @ 2.30GHz |
| Linux | 4    | Intel(R) Xeon(R) CPU @ 2.30GHz |
| Linux | 8    | Intel(R) Xeon(R) CPU @ 2.30GHz |
| Linux | 12   | Intel(R) Xeon(R) CPU @ 2.30GHz |
| Linux | 16   | Intel(R) Xeon(R) CPU @ 2.30GHz |
| Linux | 24   | Intel(R) Xeon(R) CPU @ 2.30GHz |
| Linux | 32   | Intel(R) Xeon(R) CPU @ 2.30GHz |
| Linux | 40   | Intel(R) Xeon(R) CPU @ 2.30GHz |
| Linux | 48   | Intel(R) Xeon(R) CPU @ 2.30GHz |
| Linux | 56   | Intel(R) Xeon(R) CPU @ 2.30GHz |
| Linux | 64   | Intel(R) Xeon(R) CPU @ 2.30GHz |


### Test cases

There are a handful of test cases differing mainly on the number of solutions.

| test name | total cells | empty cells | search space                                     | solutions | Filling factor |
|-----------|-------------|-------------|--------------------------------------------------|-----------|----------------|
| 1a        | 81          | 53          | 43129799915034095124480000                       | 1         | 34.57%         |
| 1b        | 81          | 59          | 1947751863256350720000000000000000000            | 4715      | 27.16%         |
| 1c        | 81          | 61          | 13980445502865408000000000000000000000000        | 132271    | 24.69%         |
| 1d        | 81          | 62          | 477847258398720000000000000000000000000000       | 587264    | 23.46%         |
| 1e        | 81          | 63          | 23409163772243214336000000000000000000000000     | 3151964   | 22.22%         |
| 1f        | 81          | 64          | 1179821854121058002534400000000000000000000000   | 16269895  | 20.99%         |
| 2a        | 81          | 58          | 24563768857859261988864000000000                 | 1         | 28.40%         |
| 2b        | 81          | 60          | 261718015484414301673881600000000000             | 276       | 25.93%         |
| 2b        | 81          | 62          | 5546527766851092480000000000000000000000         | 32128     | 23.46%         |
| 2d        | 81          | 64          | 54366191037898352756785152000000000000000000     | 1014785   | 20.99%         |
| 2e        | 81          | 65          | 4281337544234495279596830720000000000000000000   | 7388360   | 19.75%         |
| 2f        | 81          | 66          | 509895408914038847535316992000000000000000000000 | 48794239  | 18.52%         |

<!-- Table or Graphs showing the number of empty cells and the search space of each test. -->

### Execution times

See Appendix
<!-- Table or Graphs showing the execution times of each test. -->
<!-- Which instances does require more time? -->
<!-- Is there a correlation between the fill factor, the search space and execution time? -->

### Speedups obtained
Speedup values grouped by test case and core count.
There seem to be an optimum core count for maximum speedup. It grows almost linearly with relation to core count.

![Speedup for test series 1](data/speedup1.svg)

![Speedup for test series 2](data/speedup2.svg)

Unfortunately due to inefficiencies introduced by the concurrent algorithm, the speedup obtained is sometimes slightly smaller than 1. This happens only on smaller test cases or with a very low core count.

To understand the root cause of the values obtained, we computed a correlation matrix over all the data we gathered.

While the sequential time depends linearly on the number of solution and unrelated to the number of cores, the factors that make up the parallel time and the speed up are more varied.

![Correlation Matrix for all data](data/correlation.svg)

The correlation matrix is muddled by the core count, thus we plotted several correlation matrices grouping the data by core count.

Here is an example of the correlation matrix for the test data obtained on the 64 core machine.
![Correlation Matrix for 64 cores](data/correlation64.svg)

We can see clearly that the speedup is correlated greatly with the search space and the empty cells, rather than only with the number of solutions. This is expected because search space and empty cells are almost linearly dependent and **search space determines how much branching we can have on our parallel solver**.

This trend can also be found looking at the speed up plot.

The speedup also is not very strongly correlated with the number of solutions, while the parallel time is linearly dependent of the number of solutions.

We can conclude speedup comes from being able to explore the whole search space faster and could be improved a little tweaking the ForkJoin parameters.

<!-- Table or Graphs showing the speedups of each test. -->
<!-- Is the speedup always greater than 1? Why? -->

## Usage
<img src="media/run-cli.png" width="350" align="right" alt="Sudoku solution enumerator CLI" />

The project is provided with a [CLI][bin:run-cli] that allows you
to run the solver on your machine with ease.

If you want to run it locally, you need to run the following commands.
```bash
git clone https://github.com/simonepri/sudoku-solver.git
cd sudoku-solver

./sudoku
```

> NB: This will also trigger the build process so be sure to have the
[Java JDK][download:jjdk] installed on your machine prior to launch it.

## Benchmarking suite
<img src="media/bench-cli.png" width="350" align="right" alt="Sudoku solution enumerator benchmarking CLI" />

The project is provided with a [CLI][bin:bench-cli] that allows you
to reproduce the tests results on your machine.

If you want to run it locally, you need to run the following commands.
```bash
git clone https://github.com/simonepri/sudoku-solver.git
cd sudoku-solver

./bench
```

> NB: This will also trigger the build process so be sure to have the
[`Java JDK`][download:jjdk] installed on your machine prior to launch it.

> TIP: You can stop a test by hitting `CTRL+C` or `Command+C`.

## Development
Clone the repository to your local machine then cd into the directory created by
the cloning operation.

```bash
git clone https://github.com/simonepri/sudoku-solver.git
cd sudoku-solver
```

The source code for the sudoku solver can be found in
[`src/main/java/sudoku`][source:main], while the source code for the unit tests
and the benchmarking suite can be found in [`src/test/java/sudoku`][source:test]
and [`src/benchmark`][source:benchmark] respectively.

Build the project, run the unit tests and run the CLI.
```bash
# On Linux and Darwin
./gradlew build
./gradlew test
./gradlew run

# On Windows
./gradlew.bat build
./gradlew.bat test
./gradlew.bat run
```

> NB: You will need the [`Java JDK`][download:jjdk] installed on your machine to
build the project.

## Authors
- **Simone Primarosa** - *Github* ([@simonepri][github:simonepri]) • *Twitter* ([@simoneprimarosa][twitter:simoneprimarosa])
- **Q. Matteo Chen** - *Github* ([@chq-matteo][github:chq-matteo]) • *Twitter* ([@chqmatteo][twitter:chqmatteo])

## License
This project is licensed under the MIT License - see the [license][license] file for details.

<!-- Links -->
[license]: https://github.com/simonepri/sudoku-solver/tree/master/license
[source]: https://github.com/simonepri/sudoku-solver/tree/master/src/main/java/sudoku
[bin:bench-cli]: https://github.com/simonepri/sudoku-solver/tree/master/bench
[bin:run-cli]: https://github.com/simonepri/sudoku-solver/tree/master/sudoku

[source:main]: https://github.com/simonepri/sudoku-solver/tree/master/src/main/java/sudoku
[source:test]: https://github.com/simonepri/sudoku-solver/tree/master/src/test/java/sudoku
[source:benchmark]: https://github.com/simonepri/sudoku-solver/tree/master/src/benchmark
[source:sequential]: https://github.com/simonepri/sudoku-solver/tree/master/src/main/java/sudoku/SequentialSolver.java
[source:parallel]: https://github.com/simonepri/sudoku-solver/tree/master/src/main/java/sudoku/ParallelSolver.java

[github:simonepri]: https://github.com/simonepri
[twitter:simoneprimarosa]: http://twitter.com/intent/user?screen_name=simoneprimarosa
[github:chq-matteo]: https://github.com/chq-matteo
[twitter:chqmatteo]: http://twitter.com/intent/user?screen_name=chqmatteo

[download:git]: https://git-scm.com/downloads
[download:jjdk]: https://www.oracle.com/technetwork/pt/java/javase/downloads/index.html

[ref:sudoku-board-num]: http://www.afjarvis.staff.shef.ac.uk/sudoku
[ref:backtracking]: https://en.wikipedia.org/wiki/backtracking
[ref:dfs]: https://en.wikipedia.org/wiki/depth-first_search
[ref:look-ahead]: https://en.wikipedia.org/wiki/look-ahead_(backtracking)

## Appendix

### Execution Times

| core | cpu                            | test name | sequential iterations | sequential time | sequential stdev | sequential min | sequential max | parallel iterations | parallel time | parallel stdev | parallel min | parallel max | speedup |
|------|--------------------------------|-----------|-----------------------|-----------------|------------------|----------------|----------------|---------------------|---------------|----------------|--------------|--------------|---------|
| 2    | Intel(R) Xeon(R) CPU @ 2.30GHz | 1a        | 3                     | 0:00:00.010     | 0:00:00.006      | 0:00:00.005    | 0:00:00.018    | 3                   | 0:00:00.013   | 0:00:00.005    | 0:00:00.007  | 0:00:00.020  | 0.73    |
| 4    | Intel(R) Xeon(R) CPU @ 2.30GHz | 1a        | 3                     | 0:00:00.007     | 0:00:00.004      | 0:00:00.003    | 0:00:00.013    | 3                   | 0:00:00.007   | 0:00:00.004    | 0:00:00.003  | 0:00:00.014  | 0.96    |
| 8    | Intel(R) Xeon(R) CPU @ 2.30GHz | 1a        | 3                     | 0:00:00.007     | 0:00:00.004      | 0:00:00.003    | 0:00:00.014    | 3                   | 0:00:00.007   | 0:00:00.005    | 0:00:00.002  | 0:00:00.015  | 1.05    |
| 12   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1a        | 3                     | 0:00:00.007     | 0:00:00.004      | 0:00:00.003    | 0:00:00.013    | 3                   | 0:00:00.007   | 0:00:00.005    | 0:00:00.002  | 0:00:00.015  | 0.98    |
| 16   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1a        | 3                     | 0:00:00.005     | 0:00:00.004      | 0:00:00.001    | 0:00:00.011    | 3                   | 0:00:00.007   | 0:00:00.007    | 0:00:00.001  | 0:00:00.018  | 0.68    |
| 24   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1a        | 3                     | 0:00:00.006     | 0:00:00.004      | 0:00:00.001    | 0:00:00.012    | 3                   | 0:00:00.005   | 0:00:00.005    | 0:00:00.001  | 0:00:00.013  | 1.01    |
| 32   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1a        | 3                     | 0:00:00.007     | 0:00:00.006      | 0:00:00.001    | 0:00:00.016    | 3                   | 0:00:00.006   | 0:00:00.006    | 0:00:00.001  | 0:00:00.016  | 1.02    |
| 40   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1a        | 3                     | 0:00:00.005     | 0:00:00.004      | 0:00:00.001    | 0:00:00.012    | 3                   | 0:00:00.006   | 0:00:00.005    | 0:00:00.001  | 0:00:00.014  | 0.91    |
| 48   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1a        | 3                     | 0:00:00.005     | 0:00:00.004      | 0:00:00.001    | 0:00:00.011    | 3                   | 0:00:00.006   | 0:00:00.005    | 0:00:00.001  | 0:00:00.014  | 0.87    |
| 56   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1a        | 3                     | 0:00:00.005     | 0:00:00.004      | 0:00:00.001    | 0:00:00.012    | 3                   | 0:00:00.006   | 0:00:00.005    | 0:00:00.001  | 0:00:00.014  | 0.86    |
| 64   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1a        | 3                     | 0:00:00.006     | 0:00:00.006      | 0:00:00.001    | 0:00:00.015    | 3                   | 0:00:00.005   | 0:00:00.005    | 0:00:00.001  | 0:00:00.013  | 1.06    |
| 2    | Intel(R) Xeon(R) CPU @ 2.30GHz | 1b        | 3                     | 0:00:00.192     | 0:00:00.066      | 0:00:00.114    | 0:00:00.277    | 3                   | 0:00:00.198   | 0:00:00.098    | 0:00:00.098  | 0:00:00.331  | 0.97    |
| 4    | Intel(R) Xeon(R) CPU @ 2.30GHz | 1b        | 3                     | 0:00:00.159     | 0:00:00.033      | 0:00:00.127    | 0:00:00.205    | 3                   | 0:00:00.107   | 0:00:00.075    | 0:00:00.050  | 0:00:00.214  | 1.48    |
| 8    | Intel(R) Xeon(R) CPU @ 2.30GHz | 1b        | 3                     | 0:00:00.148     | 0:00:00.023      | 0:00:00.124    | 0:00:00.180    | 3                   | 0:00:00.079   | 0:00:00.069    | 0:00:00.029  | 0:00:00.177  | 1.86    |
| 12   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1b        | 3                     | 0:00:00.163     | 0:00:00.029      | 0:00:00.123    | 0:00:00.192    | 3                   | 0:00:00.080   | 0:00:00.071    | 0:00:00.024  | 0:00:00.182  | 2.02    |
| 16   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1b        | 3                     | 0:00:00.158     | 0:00:00.027      | 0:00:00.122    | 0:00:00.189    | 3                   | 0:00:00.078   | 0:00:00.083    | 0:00:00.018  | 0:00:00.197  | 2.01    |
| 24   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1b        | 3                     | 0:00:00.157     | 0:00:00.032      | 0:00:00.111    | 0:00:00.182    | 3                   | 0:00:00.073   | 0:00:00.066    | 0:00:00.016  | 0:00:00.166  | 2.14    |
| 32   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1b        | 3                     | 0:00:00.166     | 0:00:00.034      | 0:00:00.117    | 0:00:00.193    | 3                   | 0:00:00.096   | 0:00:00.113    | 0:00:00.012  | 0:00:00.256  | 1.73    |
| 40   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1b        | 3                     | 0:00:00.162     | 0:00:00.020      | 0:00:00.134    | 0:00:00.177    | 3                   | 0:00:00.071   | 0:00:00.078    | 0:00:00.010  | 0:00:00.181  | 2.28    |
| 48   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1b        | 3                     | 0:00:00.172     | 0:00:00.020      | 0:00:00.145    | 0:00:00.193    | 3                   | 0:00:00.075   | 0:00:00.083    | 0:00:00.012  | 0:00:00.193  | 2.30    |
| 56   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1b        | 3                     | 0:00:00.159     | 0:00:00.018      | 0:00:00.132    | 0:00:00.175    | 3                   | 0:00:00.120   | 0:00:00.092    | 0:00:00.018  | 0:00:00.242  | 1.32    |
| 64   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1b        | 3                     | 0:00:00.170     | 0:00:00.019      | 0:00:00.144    | 0:00:00.187    | 3                   | 0:00:00.127   | 0:00:00.108    | 0:00:00.012  | 0:00:00.272  | 1.34    |
| 2    | Intel(R) Xeon(R) CPU @ 2.30GHz | 1c        | 3                     | 0:00:01.927     | 0:00:00.155      | 0:00:01.711    | 0:00:02.068    | 3                   | 0:00:02.000   | 0:00:00.274    | 0:00:01.708  | 0:00:02.367  | 0.96    |
| 4    | Intel(R) Xeon(R) CPU @ 2.30GHz | 1c        | 3                     | 0:00:01.929     | 0:00:00.140      | 0:00:01.732    | 0:00:02.045    | 3                   | 0:00:00.932   | 0:00:00.130    | 0:00:00.830  | 0:00:01.117  | 2.07    |
| 8    | Intel(R) Xeon(R) CPU @ 2.30GHz | 1c        | 3                     | 0:00:01.913     | 0:00:00.179      | 0:00:01.660    | 0:00:02.040    | 3                   | 0:00:00.509   | 0:00:00.098    | 0:00:00.435  | 0:00:00.648  | 3.75    |
| 12   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1c        | 3                     | 0:00:01.960     | 0:00:00.192      | 0:00:01.688    | 0:00:02.097    | 3                   | 0:00:00.403   | 0:00:00.107    | 0:00:00.325  | 0:00:00.555  | 4.86    |
| 16   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1c        | 3                     | 0:00:02.117     | 0:00:00.163      | 0:00:01.885    | 0:00:02.242    | 3                   | 0:00:00.316   | 0:00:00.113    | 0:00:00.234  | 0:00:00.477  | 6.68    |
| 24   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1c        | 3                     | 0:00:02.078     | 0:00:00.015      | 0:00:02.057    | 0:00:02.095    | 3                   | 0:00:00.225   | 0:00:00.091    | 0:00:00.157  | 0:00:00.355  | 9.20    |
| 32   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1c        | 3                     | 0:00:02.144     | 0:00:00.162      | 0:00:01.935    | 0:00:02.331    | 3                   | 0:00:00.232   | 0:00:00.123    | 0:00:00.134  | 0:00:00.407  | 9.21    |
| 40   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1c        | 3                     | 0:00:01.978     | 0:00:00.112      | 0:00:01.820    | 0:00:02.075    | 3                   | 0:00:00.191   | 0:00:00.096    | 0:00:00.112  | 0:00:00.327  | 10.35   |
| 48   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1c        | 3                     | 0:00:01.973     | 0:00:00.158      | 0:00:01.751    | 0:00:02.109    | 3                   | 0:00:00.189   | 0:00:00.111    | 0:00:00.103  | 0:00:00.347  | 10.41   |
| 56   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1c        | 3                     | 0:00:01.923     | 0:00:00.099      | 0:00:01.807    | 0:00:02.050    | 3                   | 0:00:00.212   | 0:00:00.174    | 0:00:00.073  | 0:00:00.458  | 9.06    |
| 64   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1c        | 3                     | 0:00:02.008     | 0:00:00.212      | 0:00:01.831    | 0:00:02.306    | 3                   | 0:00:00.189   | 0:00:00.142    | 0:00:00.083  | 0:00:00.391  | 10.60   |
| 2    | Intel(R) Xeon(R) CPU @ 2.30GHz | 1d        | 3                     | 0:00:06.030     | 0:00:00.325      | 0:00:05.579    | 0:00:06.337    | 3                   | 0:00:05.759   | 0:00:00.359    | 0:00:05.457  | 0:00:06.265  | 1.05    |
| 4    | Intel(R) Xeon(R) CPU @ 2.30GHz | 1d        | 3                     | 0:00:06.078     | 0:00:00.344      | 0:00:05.686    | 0:00:06.524    | 3                   | 0:00:02.651   | 0:00:00.334    | 0:00:02.376  | 0:00:03.123  | 2.29    |
| 8    | Intel(R) Xeon(R) CPU @ 2.30GHz | 1d        | 3                     | 0:00:06.028     | 0:00:00.314      | 0:00:05.611    | 0:00:06.370    | 3                   | 0:00:01.340   | 0:00:00.082    | 0:00:01.225  | 0:00:01.407  | 4.50    |
| 12   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1d        | 3                     | 0:00:06.152     | 0:00:00.480      | 0:00:05.531    | 0:00:06.701    | 3                   | 0:00:01.066   | 0:00:00.096    | 0:00:00.949  | 0:00:01.187  | 5.77    |
| 16   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1d        | 3                     | 0:00:06.443     | 0:00:00.168      | 0:00:06.210    | 0:00:06.604    | 3                   | 0:00:00.790   | 0:00:00.120    | 0:00:00.702  | 0:00:00.959  | 8.15    |
| 24   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1d        | 3                     | 0:00:06.450     | 0:00:00.275      | 0:00:06.081    | 0:00:06.743    | 3                   | 0:00:00.572   | 0:00:00.128    | 0:00:00.455  | 0:00:00.751  | 11.26   |
| 32   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1d        | 3                     | 0:00:06.214     | 0:00:00.507      | 0:00:05.574    | 0:00:06.815    | 3                   | 0:00:00.503   | 0:00:00.189    | 0:00:00.365  | 0:00:00.772  | 12.33   |
| 40   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1d        | 3                     | 0:00:06.476     | 0:00:00.114      | 0:00:06.364    | 0:00:06.633    | 3                   | 0:00:00.401   | 0:00:00.160    | 0:00:00.272  | 0:00:00.627  | 16.15   |
| 48   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1d        | 3                     | 0:00:06.225     | 0:00:00.406      | 0:00:05.698    | 0:00:06.687    | 3                   | 0:00:00.418   | 0:00:00.204    | 0:00:00.268  | 0:00:00.707  | 14.86   |
| 56   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1d        | 3                     | 0:00:05.774     | 0:00:00.205      | 0:00:05.489    | 0:00:05.968    | 3                   | 0:00:00.406   | 0:00:00.225    | 0:00:00.235  | 0:00:00.725  | 14.20   |
| 64   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1d        | 3                     | 0:00:05.764     | 0:00:00.196      | 0:00:05.573    | 0:00:06.034    | 3                   | 0:00:00.408   | 0:00:00.214    | 0:00:00.253  | 0:00:00.712  | 14.11   |
| 2    | Intel(R) Xeon(R) CPU @ 2.30GHz | 1e        | 3                     | 0:00:27.341     | 0:00:01.001      | 0:00:26.446    | 0:00:28.740    | 3                   | 0:00:26.403   | 0:00:00.511    | 0:00:25.790  | 0:00:27.043  | 1.03    |
| 4    | Intel(R) Xeon(R) CPU @ 2.30GHz | 1e        | 3                     | 0:00:28.559     | 0:00:01.707      | 0:00:27.240    | 0:00:30.970    | 3                   | 0:00:12.851   | 0:00:00.271    | 0:00:12.578  | 0:00:13.221  | 2.22    |
| 8    | Intel(R) Xeon(R) CPU @ 2.30GHz | 1e        | 3                     | 0:00:28.929     | 0:00:02.068      | 0:00:26.446    | 0:00:31.511    | 3                   | 0:00:05.919   | 0:00:00.029    | 0:00:05.878  | 0:00:05.944  | 4.89    |
| 12   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1e        | 3                     | 0:00:28.447     | 0:00:01.215      | 0:00:26.797    | 0:00:29.689    | 3                   | 0:00:04.501   | 0:00:00.180    | 0:00:04.366  | 0:00:04.756  | 6.32    |
| 16   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1e        | 3                     | 0:00:28.503     | 0:00:01.412      | 0:00:27.319    | 0:00:30.488    | 3                   | 0:00:03.323   | 0:00:00.280    | 0:00:03.086  | 0:00:03.717  | 8.58    |
| 24   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1e        | 3                     | 0:00:29.995     | 0:00:01.819      | 0:00:28.420    | 0:00:32.544    | 3                   | 0:00:02.226   | 0:00:00.146    | 0:00:02.084  | 0:00:02.427  | 13.47   |
| 32   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1e        | 3                     | 0:00:28.369     | 0:00:01.309      | 0:00:26.975    | 0:00:30.122    | 3                   | 0:00:01.792   | 0:00:00.138    | 0:00:01.674  | 0:00:01.986  | 15.83   |
| 40   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1e        | 3                     | 0:00:28.528     | 0:00:00.909      | 0:00:27.745    | 0:00:29.804    | 3                   | 0:00:01.431   | 0:00:00.166    | 0:00:01.292  | 0:00:01.665  | 19.93   |
| 48   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1e        | 3                     | 0:00:28.518     | 0:00:01.713      | 0:00:27.246    | 0:00:30.940    | 3                   | 0:00:01.306   | 0:00:00.158    | 0:00:01.186  | 0:00:01.529  | 21.83   |
| 56   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1e        | 3                     | 0:00:29.344     | 0:00:01.076      | 0:00:27.984    | 0:00:30.618    | 3                   | 0:00:01.158   | 0:00:00.186    | 0:00:00.966  | 0:00:01.410  | 25.32   |
| 64   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1e        | 3                     | 0:00:25.646     | 0:00:00.861      | 0:00:24.820    | 0:00:26.834    | 3                   | 0:00:01.200   | 0:00:00.231    | 0:00:01.033  | 0:00:01.528  | 21.36   |
| 2    | Intel(R) Xeon(R) CPU @ 2.30GHz | 1f        | 3                     | 0:02:09.095     | 0:00:08.684      | 0:02:00.916    | 0:02:21.119    | 3                   | 0:02:02.599   | 0:00:00.523    | 0:02:02.031  | 0:02:03.294  | 1.05    |
| 4    | Intel(R) Xeon(R) CPU @ 2.30GHz | 1f        | 3                     | 0:02:06.766     | 0:00:06.700      | 0:01:59.974    | 0:02:15.885    | 3                   | 0:00:55.655   | 0:00:00.638    | 0:00:54.813  | 0:00:56.357  | 2.28    |
| 8    | Intel(R) Xeon(R) CPU @ 2.30GHz | 1f        | 3                     | 0:02:08.963     | 0:00:08.030      | 0:01:59.664    | 0:02:19.260    | 3                   | 0:00:26.177   | 0:00:00.484    | 0:00:25.721  | 0:00:26.848  | 4.93    |
| 12   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1f        | 3                     | 0:02:16.136     | 0:00:17.238      | 0:01:57.489    | 0:02:39.060    | 3                   | 0:00:18.636   | 0:00:00.973    | 0:00:17.863  | 0:00:20.009  | 7.30    |
| 16   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1f        | 3                     | 0:02:19.556     | 0:00:05.163      | 0:02:12.256    | 0:02:23.345    | 3                   | 0:00:14.876   | 0:00:00.589    | 0:00:14.438  | 0:00:15.710  | 9.38    |
| 24   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1f        | 3                     | 0:02:18.082     | 0:00:05.404      | 0:02:11.055    | 0:02:24.199    | 3                   | 0:00:10.088   | 0:00:00.355    | 0:00:09.767  | 0:00:10.583  | 13.69   |
| 32   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1f        | 3                     | 0:02:24.521     | 0:00:05.104      | 0:02:17.547    | 0:02:29.622    | 3                   | 0:00:06.864   | 0:00:00.491    | 0:00:06.461  | 0:00:07.555  | 21.05   |
| 40   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1f        | 3                     | 0:02:22.114     | 0:00:08.578      | 0:02:10.796    | 0:02:31.556    | 3                   | 0:00:06.441   | 0:00:00.859    | 0:00:05.805  | 0:00:07.656  | 22.06   |
| 48   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1f        | 3                     | 0:02:15.985     | 0:00:09.813      | 0:02:02.533    | 0:02:25.665    | 3                   | 0:00:05.464   | 0:00:00.846    | 0:00:04.850  | 0:00:06.661  | 24.89   |
| 56   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1f        | 3                     | 0:02:17.410     | 0:00:08.787      | 0:02:07.398    | 0:02:28.792    | 3                   | 0:00:04.575   | 0:00:00.303    | 0:00:04.346  | 0:00:05.003  | 30.03   |
| 64   | Intel(R) Xeon(R) CPU @ 2.30GHz | 1f        | 3                     | 0:02:10.882     | 0:00:08.510      | 0:02:02.990    | 0:02:22.698    | 3                   | 0:00:04.897   | 0:00:01.016    | 0:00:04.158  | 0:00:06.334  | 26.72   |
| 2    | Intel(R) Xeon(R) CPU @ 2.30GHz | 2a        | 3                     | 0:00:00.013     | 0:00:00.009      | 0:00:00.006    | 0:00:00.026    | 3                   | 0:00:00.018   | 0:00:00.012    | 0:00:00.004  | 0:00:00.035  | 0.71    |
| 4    | Intel(R) Xeon(R) CPU @ 2.30GHz | 2a        | 3                     | 0:00:00.010     | 0:00:00.008      | 0:00:00.003    | 0:00:00.022    | 3                   | 0:00:00.013   | 0:00:00.009    | 0:00:00.003  | 0:00:00.026  | 0.77    |
| 8    | Intel(R) Xeon(R) CPU @ 2.30GHz | 2a        | 3                     | 0:00:00.007     | 0:00:00.006      | 0:00:00.002    | 0:00:00.017    | 3                   | 0:00:00.011   | 0:00:00.009    | 0:00:00.004  | 0:00:00.024  | 0.68    |
| 12   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2a        | 3                     | 0:00:00.007     | 0:00:00.007      | 0:00:00.002    | 0:00:00.017    | 3                   | 0:00:00.011   | 0:00:00.009    | 0:00:00.004  | 0:00:00.024  | 0.69    |
| 16   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2a        | 3                     | 0:00:00.008     | 0:00:00.007      | 0:00:00.001    | 0:00:00.018    | 3                   | 0:00:00.008   | 0:00:00.007    | 0:00:00.002  | 0:00:00.019  | 1.00    |
| 24   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2a        | 3                     | 0:00:00.005     | 0:00:00.006      | 0:00:00.001    | 0:00:00.014    | 3                   | 0:00:00.008   | 0:00:00.007    | 0:00:00.002  | 0:00:00.018  | 0.71    |
| 32   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2a        | 3                     | 0:00:00.007     | 0:00:00.008      | 0:00:00.001    | 0:00:00.019    | 3                   | 0:00:00.010   | 0:00:00.009    | 0:00:00.003  | 0:00:00.024  | 0.74    |
| 40   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2a        | 3                     | 0:00:00.005     | 0:00:00.006      | 0:00:00.001    | 0:00:00.014    | 3                   | 0:00:00.007   | 0:00:00.006    | 0:00:00.002  | 0:00:00.017  | 0.73    |
| 48   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2a        | 3                     | 0:00:00.005     | 0:00:00.005      | 0:00:00.001    | 0:00:00.013    | 3                   | 0:00:00.007   | 0:00:00.006    | 0:00:00.003  | 0:00:00.016  | 0.71    |
| 56   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2a        | 3                     | 0:00:00.006     | 0:00:00.006      | 0:00:00.001    | 0:00:00.016    | 3                   | 0:00:00.008   | 0:00:00.007    | 0:00:00.003  | 0:00:00.019  | 0.77    |
| 64   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2a        | 3                     | 0:00:00.006     | 0:00:00.006      | 0:00:00.001    | 0:00:00.015    | 3                   | 0:00:00.010   | 0:00:00.008    | 0:00:00.004  | 0:00:00.021  | 0.63    |
| 2    | Intel(R) Xeon(R) CPU @ 2.30GHz | 2b        | 3                     | 0:00:00.068     | 0:00:00.029      | 0:00:00.029    | 0:00:00.102    | 3                   | 0:00:00.060   | 0:00:00.032    | 0:00:00.028  | 0:00:00.104  | 1.13    |
| 4    | Intel(R) Xeon(R) CPU @ 2.30GHz | 2b        | 3                     | 0:00:00.045     | 0:00:00.022      | 0:00:00.020    | 0:00:00.075    | 3                   | 0:00:00.047   | 0:00:00.028    | 0:00:00.023  | 0:00:00.087  | 0.95    |
| 8    | Intel(R) Xeon(R) CPU @ 2.30GHz | 2b        | 3                     | 0:00:00.039     | 0:00:00.014      | 0:00:00.022    | 0:00:00.058    | 3                   | 0:00:00.040   | 0:00:00.024    | 0:00:00.018  | 0:00:00.074  | 0.98    |
| 12   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2b        | 3                     | 0:00:00.039     | 0:00:00.014      | 0:00:00.023    | 0:00:00.057    | 3                   | 0:00:00.038   | 0:00:00.023    | 0:00:00.015  | 0:00:00.071  | 1.01    |
| 16   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2b        | 3                     | 0:00:00.044     | 0:00:00.018      | 0:00:00.022    | 0:00:00.068    | 3                   | 0:00:00.043   | 0:00:00.027    | 0:00:00.019  | 0:00:00.081  | 1.03    |
| 24   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2b        | 3                     | 0:00:00.037     | 0:00:00.011      | 0:00:00.022    | 0:00:00.051    | 3                   | 0:00:00.033   | 0:00:00.016    | 0:00:00.016  | 0:00:00.056  | 1.12    |
| 32   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2b        | 3                     | 0:00:00.038     | 0:00:00.013      | 0:00:00.022    | 0:00:00.056    | 3                   | 0:00:00.057   | 0:00:00.045    | 0:00:00.022  | 0:00:00.121  | 0.67    |
| 40   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2b        | 3                     | 0:00:00.035     | 0:00:00.012      | 0:00:00.020    | 0:00:00.051    | 3                   | 0:00:00.028   | 0:00:00.016    | 0:00:00.012  | 0:00:00.051  | 1.24    |
| 48   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2b        | 3                     | 0:00:00.033     | 0:00:00.010      | 0:00:00.020    | 0:00:00.045    | 3                   | 0:00:00.028   | 0:00:00.014    | 0:00:00.013  | 0:00:00.048  | 1.15    |
| 56   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2b        | 3                     | 0:00:00.038     | 0:00:00.012      | 0:00:00.023    | 0:00:00.052    | 3                   | 0:00:00.032   | 0:00:00.016    | 0:00:00.013  | 0:00:00.054  | 1.17    |
| 64   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2b        | 3                     | 0:00:00.040     | 0:00:00.014      | 0:00:00.023    | 0:00:00.058    | 3                   | 0:00:00.037   | 0:00:00.023    | 0:00:00.018  | 0:00:00.069  | 1.08    |
| 2    | Intel(R) Xeon(R) CPU @ 2.30GHz | 2c        | 3                     | 0:00:00.738     | 0:00:00.099      | 0:00:00.624    | 0:00:00.867    | 3                   | 0:00:00.738   | 0:00:00.157    | 0:00:00.619  | 0:00:00.961  | 1.00    |
| 4    | Intel(R) Xeon(R) CPU @ 2.30GHz | 2c        | 3                     | 0:00:00.706     | 0:00:00.072      | 0:00:00.629    | 0:00:00.803    | 3                   | 0:00:00.402   | 0:00:00.126    | 0:00:00.304  | 0:00:00.581  | 1.75    |
| 8    | Intel(R) Xeon(R) CPU @ 2.30GHz | 2c        | 3                     | 0:00:00.763     | 0:00:00.077      | 0:00:00.683    | 0:00:00.868    | 3                   | 0:00:00.259   | 0:00:00.122    | 0:00:00.167  | 0:00:00.432  | 2.94    |
| 12   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2c        | 3                     | 0:00:00.759     | 0:00:00.073      | 0:00:00.685    | 0:00:00.860    | 3                   | 0:00:00.200   | 0:00:00.105    | 0:00:00.121  | 0:00:00.349  | 3.79    |
| 16   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2c        | 3                     | 0:00:00.754     | 0:00:00.062      | 0:00:00.705    | 0:00:00.842    | 3                   | 0:00:00.159   | 0:00:00.091    | 0:00:00.086  | 0:00:00.287  | 4.74    |
| 24   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2c        | 3                     | 0:00:00.715     | 0:00:00.057      | 0:00:00.670    | 0:00:00.797    | 3                   | 0:00:00.133   | 0:00:00.087    | 0:00:00.069  | 0:00:00.257  | 5.35    |
| 32   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2c        | 3                     | 0:00:00.783     | 0:00:00.065      | 0:00:00.731    | 0:00:00.875    | 3                   | 0:00:00.143   | 0:00:00.118    | 0:00:00.053  | 0:00:00.310  | 5.46    |
| 40   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2c        | 3                     | 0:00:00.717     | 0:00:00.084      | 0:00:00.655    | 0:00:00.837    | 3                   | 0:00:00.117   | 0:00:00.095    | 0:00:00.048  | 0:00:00.251  | 6.13    |
| 48   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2c        | 3                     | 0:00:00.651     | 0:00:00.078      | 0:00:00.579    | 0:00:00.761    | 3                   | 0:00:00.097   | 0:00:00.079    | 0:00:00.041  | 0:00:00.209  | 6.69    |
| 56   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2c        | 3                     | 0:00:00.779     | 0:00:00.108      | 0:00:00.681    | 0:00:00.930    | 3                   | 0:00:00.116   | 0:00:00.096    | 0:00:00.043  | 0:00:00.253  | 6.71    |
| 64   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2c        | 3                     | 0:00:00.754     | 0:00:00.085      | 0:00:00.684    | 0:00:00.875    | 3                   | 0:00:00.132   | 0:00:00.121    | 0:00:00.043  | 0:00:00.304  | 5.69    |
| 2    | Intel(R) Xeon(R) CPU @ 2.30GHz | 2d        | 3                     | 0:00:08.561     | 0:00:00.675      | 0:00:07.711    | 0:00:09.364    | 3                   | 0:00:07.541   | 0:00:00.199    | 0:00:07.299  | 0:00:07.788  | 1.14    |
| 4    | Intel(R) Xeon(R) CPU @ 2.30GHz | 2d        | 3                     | 0:00:08.730     | 0:00:00.975      | 0:00:07.770    | 0:00:10.069    | 3                   | 0:00:03.901   | 0:00:00.121    | 0:00:03.730  | 0:00:04.004  | 2.24    |
| 8    | Intel(R) Xeon(R) CPU @ 2.30GHz | 2d        | 3                     | 0:00:08.382     | 0:00:00.600      | 0:00:07.932    | 0:00:09.232    | 3                   | 0:00:02.061   | 0:00:00.198    | 0:00:01.812  | 0:00:02.298  | 4.07    |
| 12   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2d        | 3                     | 0:00:09.128     | 0:00:01.101      | 0:00:08.164    | 0:00:10.670    | 3                   | 0:00:01.426   | 0:00:00.086    | 0:00:01.322  | 0:00:01.534  | 6.40    |
| 16   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2d        | 3                     | 0:00:09.028     | 0:00:00.727      | 0:00:08.196    | 0:00:09.968    | 3                   | 0:00:01.144   | 0:00:00.089    | 0:00:01.033  | 0:00:01.252  | 7.89    |
| 24   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2d        | 3                     | 0:00:08.363     | 0:00:00.534      | 0:00:07.884    | 0:00:09.109    | 3                   | 0:00:00.811   | 0:00:00.144    | 0:00:00.677  | 0:00:01.011  | 10.31   |
| 32   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2d        | 3                     | 0:00:08.962     | 0:00:00.662      | 0:00:08.424    | 0:00:09.896    | 3                   | 0:00:00.697   | 0:00:00.238    | 0:00:00.478  | 0:00:01.029  | 12.85   |
| 40   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2d        | 3                     | 0:00:09.142     | 0:00:00.737      | 0:00:08.569    | 0:00:10.182    | 3                   | 0:00:00.559   | 0:00:00.159    | 0:00:00.439  | 0:00:00.784  | 16.33   |
| 48   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2d        | 3                     | 0:00:07.717     | 0:00:00.495      | 0:00:07.363    | 0:00:08.417    | 3                   | 0:00:00.477   | 0:00:00.128    | 0:00:00.380  | 0:00:00.658  | 16.18   |
| 56   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2d        | 3                     | 0:00:08.254     | 0:00:00.369      | 0:00:07.772    | 0:00:08.670    | 3                   | 0:00:00.511   | 0:00:00.227    | 0:00:00.315  | 0:00:00.830  | 16.15   |
| 64   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2d        | 3                     | 0:00:07.832     | 0:00:00.532      | 0:00:07.143    | 0:00:08.439    | 3                   | 0:00:00.502   | 0:00:00.192    | 0:00:00.317  | 0:00:00.767  | 15.59   |
| 2    | Intel(R) Xeon(R) CPU @ 2.30GHz | 2e        | 3                     | 0:01:09.814     | 0:00:03.882      | 0:01:04.359    | 0:01:13.081    | 3                   | 0:01:02.382   | 0:00:00.320    | 0:01:01.934  | 0:01:02.663  | 1.12    |
| 4    | Intel(R) Xeon(R) CPU @ 2.30GHz | 2e        | 3                     | 0:01:04.833     | 0:00:02.170      | 0:01:03.202    | 0:01:07.900    | 3                   | 0:00:29.028   | 0:00:00.316    | 0:00:28.681  | 0:00:29.447  | 2.23    |
| 8    | Intel(R) Xeon(R) CPU @ 2.30GHz | 2e        | 3                     | 0:01:09.553     | 0:00:05.054      | 0:01:03.092    | 0:01:15.432    | 3                   | 0:00:13.771   | 0:00:00.349    | 0:00:13.462  | 0:00:14.260  | 5.05    |
| 12   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2e        | 3                     | 0:01:08.832     | 0:00:02.494      | 0:01:06.513    | 0:01:12.294    | 3                   | 0:00:10.277   | 0:00:00.407    | 0:00:09.907  | 0:00:10.845  | 6.70    |
| 16   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2e        | 3                     | 0:01:11.220     | 0:00:02.106      | 0:01:08.820    | 0:01:13.948    | 3                   | 0:00:07.272   | 0:00:00.280    | 0:00:07.060  | 0:00:07.669  | 9.79    |
| 24   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2e        | 3                     | 0:01:08.451     | 0:00:01.357      | 0:01:06.533    | 0:01:09.494    | 3                   | 0:00:05.305   | 0:00:00.290    | 0:00:05.088  | 0:00:05.715  | 12.90   |
| 32   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2e        | 3                     | 0:01:16.641     | 0:00:04.622      | 0:01:12.776    | 0:01:23.139    | 3                   | 0:00:04.074   | 0:00:00.255    | 0:00:03.851  | 0:00:04.432  | 18.81   |
| 40   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2e        | 3                     | 0:01:11.200     | 0:00:02.887      | 0:01:07.694    | 0:01:14.766    | 3                   | 0:00:03.101   | 0:00:00.268    | 0:00:02.823  | 0:00:03.464  | 22.96   |
| 48   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2e        | 3                     | 0:01:01.140     | 0:00:03.694      | 0:00:58.274    | 0:01:06.356    | 3                   | 0:00:02.694   | 0:00:00.203    | 0:00:02.470  | 0:00:02.962  | 22.69   |
| 56   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2e        | 3                     | 0:01:10.805     | 0:00:04.593      | 0:01:07.259    | 0:01:17.292    | 3                   | 0:00:02.294   | 0:00:00.110    | 0:00:02.143  | 0:00:02.403  | 30.86   |
| 64   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2e        | 3                     | 0:01:06.339     | 0:00:04.449      | 0:01:02.825    | 0:01:12.616    | 3                   | 0:00:02.256   | 0:00:00.402    | 0:00:01.896  | 0:00:02.818  | 29.40   |
| 2    | Intel(R) Xeon(R) CPU @ 2.30GHz | 2f        | 3                     | 0:07:33.454     | 0:00:18.896      | 0:07:12.685    | 0:07:58.402    | 3                   | 0:07:15.648   | 0:00:00.515    | 0:07:15.216  | 0:07:16.373  | 1.04    |
| 4    | Intel(R) Xeon(R) CPU @ 2.30GHz | 2f        | 3                     | 0:07:22.852     | 0:00:19.627      | 0:07:04.271    | 0:07:50.001    | 3                   | 0:03:05.526   | 0:00:01.698    | 0:03:03.134  | 0:03:06.914  | 2.39    |
| 8    | Intel(R) Xeon(R) CPU @ 2.30GHz | 2f        | 3                     | 0:07:48.620     | 0:00:42.313      | 0:07:01.269    | 0:08:43.983    | 3                   | 0:01:33.616   | 0:00:00.656    | 0:01:32.692  | 0:01:34.156  | 5.00    |
| 12   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2f        | 3                     | 0:07:26.369     | 0:00:22.437      | 0:07:08.999    | 0:07:58.052    | 3                   | 0:01:03.369   | 0:00:00.343    | 0:01:03.010  | 0:01:03.831  | 7.04    |
| 16   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2f        | 3                     | 0:07:35.669     | 0:00:15.664      | 0:07:24.439    | 0:07:57.821    | 3                   | 0:00:50.355   | 0:00:00.208    | 0:00:50.066  | 0:00:50.552  | 9.05    |
| 24   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2f        | 3                     | 0:07:48.053     | 0:00:16.953      | 0:07:35.039    | 0:08:11.999    | 3                   | 0:00:33.666   | 0:00:00.325    | 0:00:33.286  | 0:00:34.081  | 13.90   |
| 32   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2f        | 3                     | 0:08:07.084     | 0:00:21.703      | 0:07:36.394    | 0:08:22.773    | 3                   | 0:00:25.600   | 0:00:00.299    | 0:00:25.182  | 0:00:25.867  | 19.03   |
| 40   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2f        | 3                     | 0:07:45.641     | 0:00:18.700      | 0:07:19.214    | 0:07:59.741    | 3                   | 0:00:19.381   | 0:00:00.165    | 0:00:19.174  | 0:00:19.581  | 24.02   |
| 48   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2f        | 3                     | 0:07:47.549     | 0:00:09.280      | 0:07:36.547    | 0:07:59.247    | 3                   | 0:00:16.811   | 0:00:00.117    | 0:00:16.646  | 0:00:16.914  | 27.81   |
| 56   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2f        | 3                     | 0:07:21.440     | 0:00:12.515      | 0:07:09.211    | 0:07:38.636    | 3                   | 0:00:14.239   | 0:00:00.205    | 0:00:13.951  | 0:00:14.412  | 31.00   |
| 64   | Intel(R) Xeon(R) CPU @ 2.30GHz | 2f        | 3                     | 0:07:15.563     | 0:00:07.730      | 0:07:06.944    | 0:07:25.697    | 3                   | 0:00:13.866   | 0:00:00.476    | 0:00:13.464  | 0:00:14.535  | 31.41   |
