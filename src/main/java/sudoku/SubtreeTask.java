package sudoku;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.RecursiveTask;
import java.util.function.Consumer;

public class SubtreeTask extends RecursiveTask<BigInteger> {
    private static final long serialVersionUID = -3746632891759493367L;
    private static final long FILLABLE_COUNT_CUTOFF = 50;
    private static final BigInteger SEARCH_SPACE_CUTOFF = BigInteger.valueOf(50000000);
    private Board board;
    private Consumer<Board> onSolution;
    private BigInteger searchSpace;

    public SubtreeTask(Board board) {
        this(board, board.getSearchSpace(), null);
    }
    public SubtreeTask(Board board, BigInteger searchSpace) {
        this(board, searchSpace, null);
    }

    public SubtreeTask(Board board, BigInteger searchSpace, Consumer<Board> onSolution) {
        this.board = board;
        this.searchSpace = searchSpace;
        this.onSolution = onSolution;
    }

    public BigInteger compute() {

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
        long fillablesCount = board.getFillables().count();

        if (fillablesCount <= FILLABLE_COUNT_CUTOFF) {
            return SequentialSolver.enumerate(board);
        }
        if (searchSpace.compareTo(SEARCH_SPACE_CUTOFF) <= 0) {
            return SequentialSolver.enumerate(board);
        }
        BigInteger candidatesCount = BigInteger.valueOf(board.getCandidates(start.row, start.col).count());

        if (candidatesCount.compareTo(BigInteger.ZERO) <= 0) {
            return BigInteger.ZERO;
        }
        BigInteger newSearchSpace = searchSpace.divide(candidatesCount);
        board.getCandidates(start.row, start.col).forEach(nval -> {
            Board candidateBoard = board.copyBoard();
            candidateBoard.setCell(start.row, start.col, nval);
            tasks.add(new SubtreeTask(candidateBoard, newSearchSpace, onSolution));
        });
        BigInteger count = BigInteger.ZERO;
        if (tasks.size() > 1) {
            for (int i = 1; i < tasks.size(); i++) {
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
