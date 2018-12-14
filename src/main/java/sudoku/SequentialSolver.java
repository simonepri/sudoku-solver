package sudoku;

import java.math.BigInteger;
import java.util.Stack;
import java.util.function.Consumer;
import org.apache.commons.math3.util.Pair;

import sudoku.utils.BigCounter;

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
    Stack<Pair<Board.Cell, Integer>> stack = new Stack<>();

    Board.Cell start = board.getNextToFill();
    stack.push(new Pair<>(start, Board.EMPTY_CELL));
    board.getCandidates(start.row, start.col).forEach(nval -> stack.push(new Pair<>(start, nval)));

    while (!stack.isEmpty()) {
      Pair<Board.Cell, Integer> curr = stack.pop();
      Board.Cell cell = curr.getFirst();
      int val = curr.getSecond();

      board.setCell(cell.row, cell.col, val);

      if (val == Board.EMPTY_CELL) {
        continue;
      }

      if (board.isFull()) {
        if (onSolution != null) {
          onSolution.accept(board);
        }
        count.inc();
        continue;
      }

      Board.Cell ncell = board.getNextToFill();
      stack.push(new Pair<>(ncell, Board.EMPTY_CELL));
      board.getCandidates(ncell.row, ncell.col).forEach(nval -> stack.push(new Pair<>(ncell, nval)));
    }

    return count.get();
  }
}
