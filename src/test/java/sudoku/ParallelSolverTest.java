package sudoku;

// checkstyle-disable-next-line AvoidStarImport
import static org.assertj.core.api.Assertions.*;

import java.math.BigInteger;

import org.junit.Test;

import sudoku.utils.BigCounter;

public class ParallelSolverTest {
  @Test
  public void testEnumerate1() {
    Board sudoku = new Board(new int[][]{
      {3, 1, 0, 0},
      {0, 2, 0, 0},
      {0, 0, 2, 0},
      {0, 0, 1, 3}
    });

    BigCounter c0 = new BigCounter(0);
    BigInteger c1 = ParallelSolver.enumerate(sudoku, sol -> {
      assertThat(sol.toString()).isEqualTo(
          "3142\n"
          + "4231\n"
          + "1324\n"
          + "2413\n"
      );
      synchronized (c0) {
        c0.inc();
      }
    });
    assertThat(c0.toString()).isEqualTo(c1.toString());
  }

  @Test
  public void testEnumerate2() {
    Board sudoku = new Board(new int[][]{
      {0, 2, 9, 4, 8, 6, 0, 1, 3},
      {1, 3, 0, 7, 9, 0, 4, 6, 0},
      {0, 6, 4, 0, 3, 1, 7, 9, 2},
      {3, 9, 1, 6, 4, 7, 2, 0, 5},
      {4, 5, 0, 2, 1, 3, 9, 7, 6},
      {0, 7, 2, 9, 0, 8, 0, 4, 1},
      {9, 0, 7, 8, 2, 0, 6, 3, 4},
      {5, 8, 6, 0, 7, 4, 1, 2, 9},
      {2, 4, 3, 1, 6, 0, 8, 5, 7}
    });

    BigCounter c0 = new BigCounter(0);
    BigInteger c1 = ParallelSolver.enumerate(sudoku, sol -> {
      assertThat(sol.toString()).isEqualTo(
          "729486513\n"
          + "135792468\n"
          + "864531792\n"
          + "391647285\n"
          + "458213976\n"
          + "672958341\n"
          + "917825634\n"
          + "586374129\n"
          + "243169857\n"
      );
      synchronized (c0) {
        c0.inc();
      }
    });
    assertThat(c0.toString()).isEqualTo(c1.toString());
  }

  @Test
  public void testEnumerate3() {
    Board sudoku = new Board(new int[][]{
      {3, 1, 4, 2},
      {4, 2, 3, 1},
      {1, 3, 2, 4},
      {2, 4, 1, 3}
    });

    BigCounter c0 = new BigCounter(0);
    BigInteger c1 = ParallelSolver.enumerate(sudoku, sol -> {
      assertThat(sol.toString()).isEqualTo(
          "3142\n"
          + "4231\n"
          + "1324\n"
          + "2413\n"
      );
      synchronized (c0) {
        c0.inc();
      }
    });
    assertThat(c0.toString()).isEqualTo(c1.toString());
  }
}
