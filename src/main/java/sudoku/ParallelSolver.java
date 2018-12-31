package sudoku;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.RecursiveTask;
import java.util.function.Consumer;

import sudoku.utils.BigCounter;

public class ParallelSolver {
  private static final BigInteger SEARCH_SPACE_CUTOFF = BigInteger.valueOf((long)1e13);

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
    return new SubtreeTask(board, onSolution).compute().get();
  }

  public static class SubtreeTask extends RecursiveTask<BigCounter> {
    private Board board;
    private Consumer<Board> onSolution;
    private StackElement move;
    private BigInteger space;

    private class StackElement {
      private final int row;
      private final int col;
      private final int val;

      public StackElement(int r, int c, int v) {
        row = r;
        col = c;
        val = v;
      }
    }

    /**
     * Entry point of computation for the backtracking.
     * @param board the board for which to enumerate all the solutions.
     * @param onSolution callback called each time a solution is found.
     */
    public SubtreeTask(Board board, Consumer<Board> onSolution) {
      this(board, onSolution, null);
    }

    /**
     * Single branch of computation for the backtracking.
     * @param board the board for which to enumerate all the solutions.
     * @param onSolution callback called each time a solution is found.
     * @param move the next move to apply.
     */
    public SubtreeTask(Board board, Consumer<Board> onSolution, StackElement move) {
      this.board = new Board(board);
      this.move = move;
      this.onSolution = onSolution;
    }

    @Override
    public BigCounter compute() {
      if (move != null) {
        board = new Board(board);
        board.setCell(move.row, move.col, move.val);
      }

      if (board.isFull()) {
        if (onSolution != null) {
          onSolution.accept(board);
        }
        return new BigCounter(1);
      }

      BigInteger space = board.getSearchSpace();
      if (space == BigInteger.ZERO) {
        return new BigCounter(0);
      }
      if (space.compareTo(SEARCH_SPACE_CUTOFF) <= 0) {
        return new BigCounter(SequentialSolver.enumerate(board, onSolution));
      }

      ArrayList<SubtreeTask> tasks = new ArrayList<>();
      Board.Cell start = board.getBestNextToFill();
      board.getCandidates(start.row, start.col).forEach(nval -> {
        StackElement nmove = new StackElement(start.row, start.col, nval);
        tasks.add(new SubtreeTask(board, onSolution, nmove));
      });

      BigCounter count = new BigCounter(0);
      if (tasks.size() > 0) {
        for (int i = 1; i < tasks.size(); i++) {
          tasks.get(i).fork();
        }
        count.add(tasks.get(0).compute());
        for (int i = 1; i < tasks.size(); i++) {
          count.add(tasks.get(i).join());
        }
      }
      return count;
    }
  }
}
