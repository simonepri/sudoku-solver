package sudoku;

import java.util.ArrayList;
import java.util.concurrent.RecursiveTask;
import java.util.function.Consumer;

import sudoku.utils.BigCounter;

public class SubtreeTask extends RecursiveTask<BigCounter> {
  private static final long FILLABLES_COUNT_CUTOFF = 50;
  private Board board;
  private Consumer<Board> onSolution;
  private StackElement move;

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

  public SubtreeTask(Board board, Consumer<Board> onSolution) {
    this(board, onSolution, null);
  }

  public SubtreeTask(Board board, Consumer<Board> onSolution, StackElement move) {
    this.board = new Board(board);
    this.move = move;
    this.onSolution = onSolution;
  }

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

    if (board.getFillablesCount() <= FILLABLES_COUNT_CUTOFF) {
      return new BigCounter(SequentialSolver.enumerate(board, onSolution));
    }

    ArrayList<SubtreeTask> tasks = new ArrayList<>();
    Board.Cell start = board.getBestNextToFill();
    board.getCandidates(start.row, start.col).forEach(nval -> {
      tasks.add(new SubtreeTask(board, onSolution, new StackElement(start.row, start.col, nval)));
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
