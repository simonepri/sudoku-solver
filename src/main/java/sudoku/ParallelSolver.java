package sudoku;

import java.math.BigInteger;
import java.util.function.Consumer;

import sudoku.utils.BigCounter;

public class ParallelSolver {
  /**
   * Default Constructor.
   */
  private ParallelSolver() {}

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
    return BigInteger.ONE;
  }
}
