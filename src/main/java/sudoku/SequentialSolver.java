package sudoku;

import java.math.BigInteger;
import java.util.Stack;
import java.util.function.Consumer;

import sudoku.util.BigCounter;

public class SequentialSolver {
  /**
   * Default Constructor.
   */
  private SequentialSolver() {}

  /**
   * Enumerate all the legal solutions of the given board.
   * @param board the board for which to enumerate all the solutions.
   */
  public static BigInteger enumerate(Board board) {
    return enumerate(board, null);
  }

  /**
   * Enumerate all the legal solutions of the given board.
   * @param board the board for which to enumerate all the solutions.
   * @param onSolution callback called each time a solution is found.
   */
  public static BigInteger enumerate(Board board, Consumer<Board> onSolution) {
    class StackElement {
      private final int row;
      private final int col;
      private final int val;

      public StackElement(int r, int c, int v) {
        row = r;
        col = c;
        val = v;
      }
    }

    if (board == null) {
      return BigInteger.ZERO;
    }

    if (board.isFull()) {
      if (onSolution != null) {
        onSolution.accept(board);
      }
      return BigInteger.ONE;
    }

    BigCounter count = new BigCounter();
    Stack<StackElement> stack = new Stack<>();

    Board.Cell start = board.getBestNextToFill();
    stack.push(new StackElement(start.row, start.col, Board.EMPTY_CELL));
    board.getCandidates(start.row, start.col)
         .forEach(nval -> stack.push(new StackElement(start.row, start.col, nval)));

    while (!stack.isEmpty()) {
      StackElement curr = stack.pop();

      board.setCell(curr.row, curr.col, curr.val);

      if (curr.val == Board.EMPTY_CELL) {
        continue;
      }

      if (board.isFull()) {
        if (onSolution != null) {
          onSolution.accept(board);
        }
        count.inc();
        continue;
      }

      Board.Cell ncell = board.getBestNextToFill();

      stack.push(new StackElement(ncell.row, ncell.col, Board.EMPTY_CELL));
      board.getCandidates(ncell.row, ncell.col)
           .forEach(nval -> stack.push(new StackElement(ncell.row, ncell.col, nval)));
    }

    return count.get();
  }
}
