<h1 align="center">
  <b>sudoku-solver</b>
</h1>
<p align="center">
  <!-- CI - TravisCI -->
  <a href="https://travis-ci.com/simonepri/sudoku-solver">
    <img src="https://img.shields.io/travis/com/simonepri/sudoku-solver/master.svg?label=MacOS%20%26%20Linux" alt="Mac/Linux Build Status" />
  </a>
  <!-- CI - AppVeyor -->
  <a href="https://ci.appveyor.com/project/simonepri/sudoku-solver">
    <img src="https://img.shields.io/appveyor/ci/simonepri/sudoku-solver/master.svg?label=Windows" alt="Windows Build status" />
  </a>
  <!-- Coverage - Codecov -->
  <a href="https://codecov.io/gh/simonepri/sudoku-solver">
    <img src="https://img.shields.io/codecov/c/github/simonepri/sudoku-solver/master.svg" alt="Codecov Coverage report" />
  </a>
  <!-- License - MIT -->
  <a href="https://github.com/simonepri/sudoku-solver/tree/master/license">
    <img src="https://img.shields.io/github/license/simonepri/sudoku-solver.svg" alt="Project license" />
  </a>

  <br/>
</p>
<p align="center">
  🔢 Sudoku Solutions Enumerator (Sequential and Parallel)
  <br/>

  <sub>
    Coded by <a href="#authors">Simone Primarosa</a> and <a href="#authors">Q. Matteo Chen</a>.
  </sub>
</p>

## Introduction to Sudoku
<img src="media/sudoku.png" width="250" align="right" alt="Example Sudoku Board" />

Sudoku is a popular puzzle game usually played on a 9x9 board of numbers between
1 and 9.

The goal of the game is to fill the board with numbers. However, each row can
only contain one of each of the numbers between 1 and 9. Similarly, each column
and 3x3 sub-board can only contain one of each of the numbers between 1 and 9.
This makes for an engaging and challenging puzzle game.

A well-formed Sudoku puzzle is one that has a unique solution.
A Sudoku puzzle, more in general, can have more than one solution and our goal
is to enumerate them all, but this task is not always feasible.
Indeed, if we were given an empty Sudoku table we would have to enumerate
[6670903752021072936960 solutions][ref:sudoku-board-num] and this would take thousands of
years.

## Solving Algorithm

A common algorithm to solve Sudoku boards is called backtracking. This algorithm
is essentially a depth first search in the tree of all possible guesses in the
empty cells of the Sudoku board.

### Sequential Backtracking
TODO

### Parallel Backtracking
TODO

## Results
TODO

## Usage
If you want to run it locally, you need to run the following commands.

```bash
git clone https://github.com/simonepri/sudoku-solver.git
cd sudoku-solver

gradlew assemble

gradlew run --args="your cli arguments"
```

## Authors
- **Simone Primarosa** - *Github* ([@simonepri][github:simonepri]) • *Twitter* ([@simoneprimarosa][twitter:simoneprimarosa])
- **Q. Matteo Chen** - *Github* ([@chq-matteo][github:chq-matteo]) • *Twitter* ([@chqmatteo][twitter:chqmatteo])

## License
This project is licensed under the MIT License - see the [license][license] file for details.

<!-- Links -->
[license]: https://github.com/simonepri/sudoku-solver/tree/master/license

[github:simonepri]: https://github.com/simonepri
[twitter:simoneprimarosa]: http://twitter.com/intent/user?screen_name=simoneprimarosa
[github:chq-matteo]: https://github.com/chq-matteo
[twitter:chqmatteo]: http://twitter.com/intent/user?screen_name=chqmatteo

[ref:sudoku-board-num]: http://www.afjarvis.staff.shef.ac.uk/sudoku/
