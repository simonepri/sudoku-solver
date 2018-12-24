package sudoku;

import java.math.BigInteger;
import java.util.Stack;
import java.util.function.Consumer;
import sudoku.utils.BigCounter;

public class SubtreeTask extends RecursiveTask<BigInteger> {
    public SubtreeTask(Board board) {
        return this(board, null);
    }
    public SubtreeTask(Board board, Consumer<Board> onSolution) {
        this.board = board;
        this.onSolution = onSolution;
    }

    public BigInteger compute() {
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
      
      
          Board.Cell start = board.getBestNextToFill();

          SubtreeTask tasks[] = board.getCandidates(start.row, start.col)
               .map(nval -> new SubtreeTask(makeBoard(board, start.row, start.col, nval)));
        BigInteger count = BigInteger.ZERO;
        if (tasks.length > 1) {
            for (int i = 1; i < tasks.length; i++) {
                tasks[i].fork();
            }
        }
               if (tasks.length > 0) {
                count.add(tasks.compute());
        }
        for (int i = 1; i < tasks.length; i++) {
            count.add(tasks[i].join());
        }
        return count;
    }
}
