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
Board | An instance of Sudoku represented with a S x S matrix.
Row | A row of a board's matrix that can only contain one of each of the numbers in [1, S].
Column | A column of a board's matrix that can only contain one of each of the numbers in [1, S].
Box | A particular √S x √S sub-matrix of a board's matrix that can only contain one of each of the numbers in [1, S].
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
    board.set_cell(row, col, val);

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
TODO
<!-- Keep the count of empty and total cells. -->

#### Check if a value is legal for a cell in constant time
TODO
<!-- Keep an bit-set of size S for each column row and box. -->
<!-- Update the bit-set after during set operation. -->
<!-- Use the bit-set to understand if a specific value has already been used. -->

#### Count the number of candidates of a cell in constant time
TODO
<!-- Count the number of ones of the or of the bit-sets of the used values. -->
<!-- Precompute the number of ones for all the possible int value of the bit-set. -->

#### Get the next empty cell in constant time
TODO
<!-- Keep an array of S elements that contains the column index of the next empty cell of each row. -->
<!-- On set, update the next empty cell for the row where the value has been set. -->

#### Get the cell with the lowest number of candidates in constant time
TODO
<!-- Keep an array of S elements that contains the column index of the cell with the lowest number of candidates of each row. -->
<!-- On set, for each row try to update the column index of the cell with the lowest number of candidates with the the column where the value has been set. -->
<!-- On set, for the row where the value has been set try to update the column index with all the columns of that row. -->
<!-- On set, for the rows of the box where the value has been set try to update the column index with all the columns of that box. -->

#### Count using BigInteger in constant amortized time
TODO
<!-- Counter modulo Long.MAX_VALUE. -->

#### Parallelize branches using the fork/join framework

<!-- Thread "halving". -->
<!-- Work stealing thread pool. -->

#### Parallelize board copy
<!-- Pass the "delta" rather than a modified board -->
The Board object has to be modified after filling a cell, so each work item has to have it's local instance of a Board.

Duplicating a Board is an expensive operation so instead of doing it eagerly in the constructor of a RecursiveTask, we make a copy in the compute method.

In the constructor of our RecursiveTask we only pass the change that we want to try.

In this way we offload an expensive computation on the forks, decreasing the Span.

#### Compute the search space
TODO
<!-- Multiply at groups of log(Long.MAX_VALUE)/log(9). -->
<!-- Update on set with by dividend. -->

#### Choose of the appropriate sequential cut-off
TODO

#### Addition using BigInteger in constant amortized time
TODO
<!-- Adder modulo Long.MAX_VALUE. -->

## Experiments

### Testing environment
TODO
<!-- CPU model and other hardware info. -->

### Test cases
TODO
<!-- Table or Graphs showing the number of empty cells and the search space of each test. -->

### Execution times
TODO
<!-- Table or Graphs showing the execution times of each test. -->
<!-- Which instances does require more time? -->
<!-- Is there a correlation between the fill factor, the search space and execution time? -->

### Speedups obtained
TODO
<!-- Table or Graphs showing the speedups of each test. -->
<!-- Is the speedup always greater than 1? Why? -->

## Usage
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

<img src="media/run-cli.gif" height="420" align="center" alt="Sudoku solution enumerator CLI" />

## Benchmarking suite
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

<img src="media/bench-cli.gif" height="420" align="center" alt="Sudoku solution enumerator benchmarking CLI" />

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
