package sudoku;

import java.util.BitSet;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

final class Board {
  final class Cell {
    public final int row;
    public final int col;

    private Cell(int row, int col) {
      this.row = row;
      this.col = col;
    }

    @Override
    public String toString() {
      return "(" + row + "," + col + ")";
    }
  }

  private static final int MAX_BOARD_LENGTH = 9; // Math.floor(Math.sqrt(Byte.MAX_VALUE))
  public static final int EMPTY_CELL = 0;

  private static int[] BITSET_COUNT = null;
  private static int BITSET_SIZE = 0;

  private final int[][] board;
  private final int boxLength;
  private final int boardLength;
  private final int cellCount;
  private int clueCount;

  private int nextFreeRow;
  private int[] nextFreeOnRow;
  private int nextBestFreeRow;
  private int[] nextBestFreeOnRow;

  private int[] rowUsed;
  private int[] colUsed;
  private int[] boxUsed;

  /**
   * Default Constructor.
   * @param board the sudoku board, empty cells are represented by 0.
   */
  public Board(int[][] board) {
    if (board == null) {
      throw new IllegalArgumentException("The board size is too small");
    }
    if (board.length > MAX_BOARD_LENGTH) {
      throw new IllegalArgumentException("The board size is too large");
    }

    boxLength = (int) (Math.sqrt(board.length));
    boardLength = (boxLength * boxLength);
    cellCount = boardLength * boardLength;
    clueCount = 0;

    if (board.length != boardLength) {
      throw new IllegalArgumentException("The board size must be a perfect square");
    }

    rowUsed = new int[boardLength];
    colUsed = new int[boardLength];
    boxUsed = new int[boardLength];
    buildCountBitSetLookupTable(boardLength + 1);

    nextFreeRow = 0;
    nextFreeOnRow = new int[boardLength];
    nextBestFreeRow = 0;
    nextBestFreeOnRow = new int[boardLength];

    this.board = new int[boardLength][boardLength];
    for (int row = 0; row < boardLength; row++) {
      if (board[row].length != boardLength) {
        throw new IllegalArgumentException("The board must be a square");
      }

      for (int col = 0; col < boardLength; col++) {
        setCell(row, col, board[row][col]);
      }
    }
  }
  
  /**
   * @return a copy of this board
   */
  public Board copyBoard() {
    return new Board(board);
  }
  /**
   * Get the value of a board's cell.
   * @param row a row of the board.
   * @param col a column of the board.
   */
  public int getCell(int row, int col) {
    if (!isValidCell(row, col)) {
      throw new IllegalArgumentException("The cell specified is out of the board");
    }

    return board[row][col];
  }

  /**
   * Set the value of a board's cell.
   * @param row a row of the board.
   * @param col a column of the board.
   * @param val the value to place at the position.
   * @throws IllegalArgumentException if the action cannot be taken.
   */
  public void setCell(int row, int col, int val) {
    if (!isValidCell(row, col)) {
      throw new IllegalArgumentException("The cell specified is out of the board");
    }
    if (!isValidValue(val)) {
      throw new IllegalArgumentException("The value specified is invalid");
    }

    int oldval = board[row][col];
    if (oldval == val) {
      return;
    }

    int box = getBoxIndexRaw(row, col);
    if (!isCandidateRaw(row, col, box, val)) {
      throw new IllegalArgumentException("Value already used");
    }

    if (oldval != EMPTY_CELL) {
      int unsetbit = ~(1 << oldval);
      rowUsed[row] &= unsetbit;
      colUsed[col] &= unsetbit;
      boxUsed[box] &= unsetbit;
      clueCount--;
    }

    if (val != EMPTY_CELL) {
      int setbit = (1 << val);
      rowUsed[row] |= setbit;
      colUsed[col] |= setbit;
      boxUsed[box] |= setbit;
      clueCount++;
    }

    board[row][col] = val;

    if (oldval == EMPTY_CELL) {
      updateNextToFillOnSet(row, col, box);
      updateNextBestToFillOnSet(row, col, box);
    } else if (val == EMPTY_CELL) {
      updateNextToFillOnUnset(row, col, box);
      updateNextBestToFillOnUnset(row, col, box);
    }
  }

  /**
   * Check whether a certain value can be legally placed in a cell.
   * @param row a row of the board.
   * @param col a column of the board.
   * @param val the value to place at the position.
   * @throws IllegalArgumentException if the action cannot be taken.
   */
  public boolean isCandidate(int row, int col, int val) {
    if (!isValidCell(row, col)) {
      throw new IllegalArgumentException("The cell specified is out of the board");
    }
    if (!isValidValue(val)) {
      return false;
    }

    int box = getBoxIndexRaw(row, col);
    return isCandidateRaw(row, col, box, val);
  }

  /**
   * Get a stream of possible legal values to set for a particular cell.
   * @param row a row of the board.
   * @param col a column of the board.
   * @throws IllegalArgumentException if the action cannot be taken.
   */
  public IntStream getCandidates(int row, int col) {
    if (!isValidCell(row, col)) {
      throw new IllegalArgumentException("The cell specified is out of the board");
    }

    int box = getBoxIndexRaw(row, col);
    return IntStream.rangeClosed(1, boardLength).filter(val -> isCandidateRaw(row, col, box, val));
  }

  /**
   * Get the number of possible legal values to set for a particular cell.
   * @param row a row of the board.
   * @param col a column of the board.
   * @throws IllegalArgumentException if the action cannot be taken.
   */
  public int getCandidatesCount(int row, int col) {
    if (!isValidCell(row, col)) {
      throw new IllegalArgumentException("The cell specified is out of the board");
    }

    return boardLength - getUsedCountRaw(row, col);
  }

  /**
   * Get a stream of empty cells of the board. (left to right, top to bottom)
   */
  public Stream<Cell> getFillables() {
    return IntStream.range(0, boardLength)
        .mapToObj(i -> IntStream.range(0, boardLength).mapToObj(j -> new Cell(i, j)))
        .flatMap(Function.identity())
        .filter(cell -> board[cell.row][cell.col] == EMPTY_CELL);
  }

  /**
   * Get the next cell that is empty. (left to right, top to bottom)
   */
  public Cell getNextToFill() {
    if (nextFreeRow >= boardLength) {
      return null;
    }
    return new Cell(nextFreeRow, nextFreeOnRow[nextFreeRow]);
  }

  /**
   * Get the next cell that is empty and has the least number of candidates.
   * (left to right, top to bottom)
   */
  public Cell getBestNextToFill() {
    if (nextFreeRow >= boardLength) {
      return null;
    }
    return new Cell(nextBestFreeRow, nextBestFreeOnRow[nextBestFreeRow]);
  }

  /**
   * Get the width and height of the table.
   */
  public int getBorderLength() {
    return boardLength;
  }

  /**
   * Get the width and height of a table's box.
   */
  public int getBoxLength() {
    return boxLength;
  }

  /**
   * Get the number of cells of the boards.
   */
  public int getSize() {
    return cellCount;
  }

  /**
   * Check wether the board is complete.
   */
  public boolean isFull() {
    return clueCount == cellCount;
  }

  /**
   * Check whether the given cell belongs to the board.
   * @param row a row of the board.
   * @param col a column of the board.
   */
  public boolean isValidCell(int row, int col) {
    return row >= 0 && row < boardLength && col >= 0 && col < boardLength;
  }

  /**
   * Check whether the given value can ve used in the board.
   * @param row a row of the board.
   * @param col a column of the board.
   */
  public boolean isValidValue(int val) {
    return val >= 0 && val <= boardLength;
  }

  /**
   * Compute the box index for the given cell. (left to right, top to bottom)
   * @param row a row of the board.
   * @param col a column of the board.
   * @throws IllegalArgumentException if the action cannot be taken.
   */
  public int getBoxIndex(int row, int col) {
    if (!isValidCell(row, col)) {
      throw new IllegalArgumentException("The cell specified is out of the board");
    }

    return getBoxIndexRaw(row, col);
  }

  /**
   * Check whether a certain value can be legally placed in a cell.
   * @param row a row of the board.
   * @param col a column of the board.
   * @param box the box of the cell provided.
   * @param val the value to place at the position.
   */
  private boolean isCandidateRaw(int row, int col, int box, int val) {
    int nthbit = 1 << val;
    return ((rowUsed[row] & nthbit) == 0)
        && ((colUsed[col] & nthbit) == 0)
        && ((boxUsed[box] & nthbit) == 0);
  }

  /**
   * Get the number of possible legal values to set for a particular cell.
   * @param row a row of the board.
   * @param col a column of the board.
   */
  private int getUsedCountRaw(int row, int col) {
    return BITSET_COUNT[rowUsed[row] | colUsed[col] | boxUsed[getBoxIndexRaw(row, col)]];
  }

  /**
   * Get the number of possible legal values to set for a particular cell.
   * @param row a row of the board.
   * @param col a column of the board.
   * @param box the box of the cell provided.
   */
  private int getUsedCountRaw(int row, int col, int box) {
    return BITSET_COUNT[rowUsed[row] | colUsed[col] | boxUsed[box]];
  }

  /**
   * Compute the box index for the given cell. (left to right, top to bottom)
   * @param row a row of the board.
   * @param col a column of the board.
   */
  private int getBoxIndexRaw(int row, int col) {
    return (((row / boxLength) * boxLength) + (col / boxLength));
  }

  /**
   * Helper function to update the next free cell when a cell is setted.
   * @param row a row of the board.
   * @param col a column of the board.
   * @param box the box of the cell provided.
   */
  private void updateNextToFillOnSet(int row, int col, int box) {
    if (col != nextFreeOnRow[row]) {
      return;
    }

    // Update the next free cell on the given row.
    int ncol = nextFreeOnRow[row] + 1;
    while (ncol < boardLength && board[row][ncol] != EMPTY_CELL) {
      ncol++;
    }
    nextFreeOnRow[row] = ncol;

    // Update the overall next cell.
    if (row == nextFreeRow) {
      int nrow = nextFreeRow;
      while (nrow < boardLength && nextFreeOnRow[nrow] == boardLength) {
        nrow++;
      }
      nextFreeRow = nrow;
    }
  }

  /**
   * Helper function to update the next free cell when a cell is cleared.
   * @param row a row of the board.
   * @param col a column of the board.
   * @param box the box of the cell provided.
   */
  private void updateNextToFillOnUnset(int row, int col, int box) {
    // Update the next free cell on the given row.
    if (col < nextFreeOnRow[row]) {
      nextFreeOnRow[row] = col;
    }

    // Update the overall next cell.
    if (row < nextFreeRow) {
      nextFreeRow = row;
    }
  }

  /**
   * Helper function to update the next bext free cell when a cell is setted.
   * @param row a row of the board.
   * @param col a column of the board.
   * @param box the box of the cell provided.
   */
  private void updateNextBestToFillOnSet(int row, int col, int box) {
    // Update the best cell on the given row.
    if (col == nextBestFreeOnRow[row]) {
      int nbrCol = nextFreeOnRow[row];
      if (nbrCol < boardLength) {
        int nbrUse = getUsedCountRaw(row, nbrCol);
        for (int ntrCol = nbrCol + 1; ntrCol < boardLength; ntrCol++) {
          if (board[row][ntrCol] != EMPTY_CELL) {
            continue;
          }
          int ntrUse = getUsedCountRaw(row, ntrCol);
          if (ntrUse > nbrUse || ntrUse == nbrUse && ntrCol < nbrCol) {
            nbrCol = ntrCol;
            nbrUse = ntrUse;
          }
        }
      }
      nextBestFreeOnRow[row] = nbrCol;
    }

    if (nextFreeRow == boardLength) {
      nextBestFreeRow = boardLength;
      return;
    }

    // Update the new best on all the rows of the given box.
    int srow = row / boxLength * boxLength;
    int erow = srow + boxLength;
    int scol = col / boxLength * boxLength;
    int ecol = scol + boxLength;
    for (int r = Math.max(srow, nextFreeRow); r < erow; r++) {
      if (r == row) {
        continue;
      }
      int nbrCol = nextBestFreeOnRow[r];
      if (nbrCol == boardLength) {
        continue;
      }
      int nbrUse = getUsedCountRaw(r, nbrCol);
      for (int ntrCol = Math.max(scol, nextFreeOnRow[r]); ntrCol < ecol; ntrCol++) {
        if (board[r][ntrCol] != EMPTY_CELL || ntrCol == nbrCol) {
          continue;
        }
        int ntrUse = getUsedCountRaw(r, ntrCol, box);
        if (ntrUse > nbrUse || ntrUse == nbrUse && ntrCol < nbrCol) {
          nbrCol = ntrCol;
          nbrUse = ntrUse;
        }
      }
      nextBestFreeOnRow[r] = nbrCol;
    }

    int nextBestFreeRowUse = -1;
    // Update the new best on all the rows at the given col and the overall best cell.
    for (int r = nextFreeRow; r < boardLength; r++) {
      int nbrCol = nextBestFreeOnRow[r];
      if (nbrCol == boardLength) {
        continue;
      }
      int nbrUse = getUsedCountRaw(r, nbrCol);
      if (nbrCol != col && board[r][col] == EMPTY_CELL && r != row) {
        int ntrUse = getUsedCountRaw(r, col);
        if (ntrUse > nbrUse || ntrUse == nbrUse && col < nbrCol) {
          nextBestFreeOnRow[r] = col;
          nbrUse = ntrUse;
        }
      }

      if (nbrUse > nextBestFreeRowUse) {
        nextBestFreeRow = r;
        nextBestFreeRowUse = nbrUse;
      }
    }
  }

  /**
   * Helper function to update the next bext free cell when a cell is cleared.
   * @param row a row of the board.
   * @param col a column of the board.
   * @param box the box of the cell provided.
   */
  private void updateNextBestToFillOnUnset(int row, int col, int box) {
    int uc = getUsedCountRaw(row, col, box);

    // Update the best cell on the given row.
    int brcol = nextBestFreeOnRow[row];
    if (brcol == boardLength) {
      nextBestFreeOnRow[row] = col;
    } else {
      int bruc = getUsedCountRaw(row, brcol);
      if (bruc < uc) {
        nextBestFreeOnRow[row] = col;
      } else {
        return;
      }
    }

    // Update the overall best cell.
    if (nextBestFreeRow == boardLength) {
      nextBestFreeRow = row;
    } else {
      int borow = nextBestFreeRow;
      int bocol = nextBestFreeOnRow[borow];
      int bouc = getUsedCountRaw(borow, bocol);
      if (bouc < uc) {
        nextBestFreeRow = row;
      }
    }
  }

  /**
   * Build a lookup table to be able to count set bits of a bitset faster.
   * @param size the number of bits of the bitset.
   */
  private static void buildCountBitSetLookupTable(int size) {
    if (BITSET_COUNT == null || BITSET_SIZE < size) {
      BITSET_SIZE = size;
      int space = 1 << BITSET_SIZE;
      BITSET_COUNT = new int[space];
      for (int i = 1; i < space; i++) {
        BITSET_COUNT[i] = (i & 1) + BITSET_COUNT[i >> 1];
      }
    }
  }

  /**
   * Return a string containing the sudoku board with _ to sign an empty cell.
   */
  @Override
  public String toString() {
    final int bufSize = ((boardLength + 1) * boardLength);
    final StringBuilder buffer = new StringBuilder(bufSize);
    for (int row = 0; row < boardLength; row++) {
      for (int col = 0; col < boardLength; col++) {
        int val = board[row][col];
        buffer.append(val == EMPTY_CELL ? '_' : (char) (val + '0'));
      }
      buffer.append('\n');
    }
    return buffer.toString();
  }
}
