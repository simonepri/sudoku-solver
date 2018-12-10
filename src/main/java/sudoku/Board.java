package sudoku;

import java.util.BitSet;
import java.util.stream.IntStream;

final class Board {
  static final int EMPTY_CELL = 0;

  private final int[][] board;
  private final int regionSize;
  private final int gridSize;
  private BitSet[] rowUsed;
  private BitSet[] colUsed;
  private BitSet[] boxUsed;

  /**
   * Default Constructor.
   * @param board the sudoku board, empty cells are represented by 0.
   */
  public Board(int[][] board) {
    if (board == null) {
      throw new IllegalArgumentException("The board size is too small");
    }
    if (board.length > 128) {
      throw new IllegalArgumentException("The board size is too large");
    }
    regionSize = (int)(Math.sqrt(board.length));
    gridSize = (regionSize * regionSize);
    if (board.length != gridSize) {
      throw new IllegalArgumentException("The board size must be a perfect square");
    }

    this.board = new int[gridSize][gridSize];
    rowUsed = new BitSet[gridSize];
    colUsed = new BitSet[gridSize];
    boxUsed = new BitSet[gridSize];
    for (int i = 0; i < gridSize; i++) {
      rowUsed[i] = new BitSet(gridSize + 1);
      colUsed[i] = new BitSet(gridSize + 1);
      boxUsed[i] = new BitSet(gridSize + 1);
    }

    for (int row = 0; row < gridSize; row++) {
      if (board[row].length != gridSize) {
        throw new IllegalArgumentException("The board must be a square");
      }

      for (int col = 0; col < gridSize; col++) {
        int val = board[row][col];
        if (val == EMPTY_CELL) {
          this.board[row][col] = val;
        } else {
          setCell(row, col, board[row][col]);
        }
      }
    }
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

    int box = getBoxIndex(row, col);
    if (!isCandidateRaw(row, col, box, val)) {
      throw new IllegalArgumentException("Value already used");
    }

    if (oldval != EMPTY_CELL) {
      rowUsed[row].clear(oldval);
      colUsed[col].clear(oldval);
      boxUsed[box].clear(oldval);
    }
    board[row][col] = val;
    rowUsed[row].set(val);
    colUsed[col].set(val);
    boxUsed[box].set(val);
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
    int box = getBoxIndex(row, col);
    return isCandidateRaw(row, col, box, val);
  }

  /**
   * Get a stream of possible legal values for a particular empty cell.
   * @param row a row of the board.
   * @param col a column of the board.
   */
  public IntStream getCandidates(int row, int col) {
    if (!isValidCell(row, col)) {
      throw new IllegalArgumentException("The cell specified is out of the board");
    }
    if (board[row][col] != EMPTY_CELL) {
      return IntStream.empty();
    }
    int box = getBoxIndex(row, col);
    return IntStream.rangeClosed(1, gridSize).filter(val -> isCandidateRaw(row, col, box, val));
  }

  /**
   * Check whether the given cell belongs to the board.
   * @param row a row of the board.
   * @param col a column of the board.
   */
  protected boolean isValidCell(int row, int col) {
    return row >= 0 && row < gridSize && col >= 0 && col < gridSize;
  }

  /**
   * Check whether the given value can ve used in the board.
   * @param row a row of the board.
   * @param col a column of the board.
   */
  protected boolean isValidValue(int val) {
    return val >= 1 && val <= gridSize;
  }

  /**
   * Compute the box index for the given cell. (left to right, top to bottom)
   * @param row a row of the board.
   * @param col a column of the board.
   */
  protected int getBoxIndex(int row, int col) {
    return (((row / regionSize) * regionSize) + (col / regionSize));
  }

  /**
   * Raw version of Board#isCandidate that does not compute the box value.
   * @param row a row of the board.
   * @param col a column of the board.
   * @param box the box of the cell provided.
   * @param val the value to place at the position.
   * @throws IllegalArgumentException if the action cannot be taken.
   */
  private boolean isCandidateRaw(int row, int col, int box, int val) {
    return !(rowUsed[row].get(val) || colUsed[col].get(val) || boxUsed[box].get(val));
  }

  /**
   * Return a string containing the sudoku board with _ to sign an empty cell.
   */
  @Override
  public String toString() {
    final int bufSize = ((gridSize + 1) * gridSize);
    final StringBuilder buffer = new StringBuilder(bufSize);
    for (int row = 0; row < gridSize; row++) {
      for (int col = 0; col < gridSize; col++) {
        int val = board[row][col];
        buffer.append(val == EMPTY_CELL ? '_' : (char)(val + '0'));
      }
      buffer.append('\n');
    }
    return buffer.toString();
  }
}
