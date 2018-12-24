package sudoku;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.RecursiveTask;
import java.util.function.Consumer;

public class SubtreeTask extends RecursiveTask<BigInteger> {
    private static final long serialVersionUID = -3746632891759493367L;
    private Board board;
    private Consumer<Board> onSolution;

    public SubtreeTask(Board board) {
        this(board, null);
    }

    public SubtreeTask(Board board, Consumer<Board> onSolution) {
        this.board = board;
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

        ArrayList<SubtreeTask> tasks = new ArrayList<>();
        board.getCandidates(start.row, start.col).forEach(nval -> {
            Board candidateBoard = board.copyBoard();
            candidateBoard.setCell(start.row, start.col, nval);
            tasks.add(new SubtreeTask(candidateBoard, onSolution));
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
