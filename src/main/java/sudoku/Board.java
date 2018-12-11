package sudoku;

import java.util.BitSet;
import java.util.stream.IntStream;

final class Board {
  private static final int MAX_BOARD_LENGTH = 9; // Math.floor(Math.sqrt(Byte.MAX_VALUE))
  public static final int EMPTY_CELL = 0;

  private final int[][] board;
  private final int boxLength;
  private final int boardLength;

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
    if (board.length > MAX_BOARD_LENGTH) {
      throw new IllegalArgumentException("The board size is too large");
    }

    boxLength = (int) (Math.sqrt(board.length));
    boardLength = (boxLength * boxLength);

    if (board.length != boardLength) {
      throw new IllegalArgumentException("The board size must be a perfect square");
    }

    this.board = new int[boardLength][boardLength];
    rowUsed = new BitSet[boardLength];
    colUsed = new BitSet[boardLength];
    boxUsed = new BitSet[boardLength];
    for (int i = 0; i < boardLength; i++) {
      rowUsed[i] = new BitSet(boardLength + 1);
      colUsed[i] = new BitSet(boardLength + 1);
      boxUsed[i] = new BitSet(boardLength + 1);
    }

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
    if (val != EMPTY_CELL) {
      rowUsed[row].set(val);
      colUsed[col].set(val);
      boxUsed[box].set(val);
    }

    board[row][col] = val;
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
    int box = getBoxIndex(row, col);
    return IntStream.rangeClosed(1, boardLength).filter(val -> isCandidateRaw(row, col, box, val));
  }

  /**
   * Check whether the given cell belongs to the board.
   * @param row a row of the board.
   * @param col a column of the board.
   */
  protected boolean isValidCell(int row, int col) {
    return row >= 0 && row < boardLength && col >= 0 && col < boardLength;
  }

  /**
   * Check whether the given value can ve used in the board.
   * @param row a row of the board.
   * @param col a column of the board.
   */
  protected boolean isValidValue(int val) {
    return val >= 0 && val <= boardLength;
  }

  /**
   * Compute the box index for the given cell. (left to right, top to bottom)
   * @param row a row of the board.
   * @param col a column of the board.
   */
  protected int getBoxIndex(int row, int col) {
    return (((row / boxLength) * boxLength) + (col / boxLength));
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
