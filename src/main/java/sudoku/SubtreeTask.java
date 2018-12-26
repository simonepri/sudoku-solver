package sudoku;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.RecursiveTask;
import java.util.function.Consumer;

public class SubtreeTask extends RecursiveTask<BigInteger> {
  private static final long serialVersionUID = -3746632891759493367L;
  private static final long FILLABLE_COUNT_CUTOFF = 0;
  private static final BigInteger SEARCH_SPACE_CUTOFF = BigInteger.valueOf(500);
  private Board board;
  private Consumer<Board> onSolution;
  private BigInteger searchSpace;
  private boolean sequential;
  private StackElement move;
  private long fillables;

  public class StackElement {
    private final int row;
    private final int col;
    private final int val;

    public StackElement(int r, int c, int v) {
      row = r;
      col = c;
      val = v;
    }
  }

  public SubtreeTask(Board board) {
    this(board, 5);
  }

  public SubtreeTask(Board board, long fillables) {
    this(board, fillables, null, null);
  }

  public SubtreeTask(Board board, long fillables, StackElement move) {
    this(board, fillables, move, null);
  }

  public SubtreeTask(Board board, long fillables, StackElement move, BigInteger searchSpace) {
    this(board, fillables, move, searchSpace, null);
  }

  public SubtreeTask(Board board, long fillables, StackElement move, BigInteger searchSpace, Consumer<Board> onSolution) {
    this(board, fillables, move, searchSpace, onSolution, false);

  }

  public SubtreeTask(Board board, long fillables, StackElement move, BigInteger searchSpace, Consumer<Board> onSolution, boolean sequential) {
    this.board = board;
    this.move = move;
    this.fillables = fillables;
    this.searchSpace = searchSpace;
    this.onSolution = onSolution;
    this.sequential = sequential;
  }

  public BigInteger compute() {
    if (move != null) {
      board = board.copyBoard();
      board.setCell(move.row, move.col, move.val);
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

    Board.Cell start = board.getBestNextToFill();
    if (start == null) {
      return BigInteger.ZERO;
    }

    ArrayList<SubtreeTask> tasks = new ArrayList<>();
    // long fillablesCount = board.getFillables().parallel().count();
    // if (searchSpace == null) {
    //   searchSpace = board.getSearchSpace();
    // }
    if (fillables <= FILLABLE_COUNT_CUTOFF /*|| searchSpace.compareTo(SEARCH_SPACE_CUTOFF) <= 0*/ || sequential) {
      return SequentialSolver.enumerate(board);
    }
    BigInteger candidatesCount = BigInteger.valueOf(board.getCandidatesCount(start.row, start.col));
    if (candidatesCount.compareTo(BigInteger.ZERO) <= 0) {
      return BigInteger.ZERO;
    }
    if (candidatesCount.compareTo(BigInteger.ONE) == 0) {
      board.setCell(start.row, start.col, board.getCandidates(start.row, start.col).sum());
      fillables -= 1;
      return this.compute();
    }
    // BigInteger newSearchSpace = searchSpace.divide(candidatesCount);
    board.getCandidates(start.row, start.col).forEach(nval -> {
      tasks.add(new SubtreeTask(board, fillables - 1, new StackElement(start.row, start.col, nval)/*, newSearchSpace, onSolution*/));
    });
    BigInteger count = BigInteger.ZERO;
    if (tasks.size() > 1) {
      for (int i = 1; i < tasks.size(); i++) {
        tasks.get(i).sequential = false;
        tasks.get(i).fork();
      }
    }
    if (tasks.size() > 0) {
      count = count.add(tasks.get(0).compute());
    }
    for (int i = 1; i < tasks.size(); i++) {
      count = count.add(tasks.get(i).join());
    }
    return count;
  }
}
