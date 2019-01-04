package sudoku;

// checkstyle-disable-next-line AvoidStarImport
import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class ParallelSolverBenchTest {
  @Test
  public void testBench_1a() {
    Board sudoku = new Board(new int[][]{
      {8, 0, 0, 2, 0, 3, 0, 0, 6},
      {0, 5, 0, 7, 0, 0, 0, 0, 9},
      {0, 0, 4, 0, 0, 0, 1, 0, 0},
      {7, 9, 0, 0, 5, 0, 0, 0, 4},
      {0, 0, 0, 8, 0, 6, 0, 0, 0},
      {1, 0, 0, 0, 7, 0, 0, 9, 5},
      {0, 0, 3, 0, 0, 0, 2, 0, 0},
      {5, 0, 0, 0, 0, 7, 0, 6, 0},
      {4, 0, 0, 6, 0, 9, 0, 0, 1}
    });
    assertThat((sudoku).getSearchSpace().toString())
      .isEqualTo("43129799915034095124480000");
    assertThat(ParallelSolver.enumerate(sudoku).toString())
      .isEqualTo("1");
  }

  @Test
  public void testBench_1b() {
    Board sudoku = new Board(new int[][]{
      {8, 0, 0, 2, 0, 0, 0, 0, 6},
      {0, 5, 0, 0, 0, 0, 0, 0, 9},
      {0, 0, 4, 0, 0, 0, 1, 0, 0},
      {0, 9, 0, 0, 5, 0, 0, 0, 4},
      {0, 0, 0, 0, 0, 6, 0, 0, 0},
      {1, 0, 0, 0, 7, 0, 0, 0, 5},
      {0, 0, 3, 0, 0, 0, 2, 0, 0},
      {5, 0, 0, 0, 0, 7, 0, 6, 0},
      {4, 0, 0, 6, 0, 0, 0, 0, 1}
    });
    assertThat((sudoku).getSearchSpace().toString())
      .isEqualTo("1947751863256350720000000000000000000");
    assertThat(ParallelSolver.enumerate(sudoku).toString())
      .isEqualTo("4715");
  }

  @Test
  public void testBench_1c() {
    Board sudoku = new Board(new int[][]{
      {0, 0, 0, 2, 0, 0, 0, 0, 6},
      {0, 5, 0, 0, 0, 0, 0, 0, 9},
      {0, 0, 4, 0, 0, 0, 1, 0, 0},
      {0, 9, 0, 0, 5, 0, 0, 0, 4},
      {0, 0, 0, 0, 0, 6, 0, 0, 0},
      {1, 0, 0, 0, 7, 0, 0, 0, 5},
      {0, 0, 3, 0, 0, 0, 2, 0, 0},
      {5, 0, 0, 0, 0, 0, 0, 6, 0},
      {4, 0, 0, 6, 0, 0, 0, 0, 1}
    });
    assertThat((sudoku).getSearchSpace().toString())
      .isEqualTo("13980445502865408000000000000000000000000");
    assertThat(ParallelSolver.enumerate(sudoku).toString())
      .isEqualTo("132271");
  }

  @Test
  public void testBench_1d() {
    Board sudoku = new Board(new int[][]{
      {0, 0, 0, 2, 0, 0, 0, 0, 6},
      {0, 5, 0, 0, 0, 0, 0, 0, 9},
      {0, 0, 4, 0, 0, 0, 1, 0, 0},
      {0, 9, 0, 0, 0, 0, 0, 0, 4},
      {0, 0, 0, 0, 0, 6, 0, 0, 0},
      {1, 0, 0, 0, 7, 0, 0, 0, 5},
      {0, 0, 3, 0, 0, 0, 2, 0, 0},
      {5, 0, 0, 0, 0, 0, 0, 6, 0},
      {4, 0, 0, 6, 0, 0, 0, 0, 1}
    });
    assertThat((sudoku).getSearchSpace().toString())
      .isEqualTo("477847258398720000000000000000000000000000");
    assertThat(ParallelSolver.enumerate(sudoku).toString())
      .isEqualTo("587264");
  }

  @Test
  public void testBench_1e() {
    Board sudoku = new Board(new int[][]{
      {0, 0, 0, 2, 0, 0, 0, 0, 6},
      {0, 0, 0, 0, 0, 0, 0, 0, 9},
      {0, 0, 4, 0, 0, 0, 1, 0, 0},
      {0, 9, 0, 0, 0, 0, 0, 0, 4},
      {0, 0, 0, 0, 0, 6, 0, 0, 0},
      {1, 0, 0, 0, 7, 0, 0, 0, 5},
      {0, 0, 3, 0, 0, 0, 2, 0, 0},
      {5, 0, 0, 0, 0, 0, 0, 6, 0},
      {4, 0, 0, 6, 0, 0, 0, 0, 1}
    });
    assertThat((sudoku).getSearchSpace().toString())
      .isEqualTo("23409163772243214336000000000000000000000000");
    assertThat(ParallelSolver.enumerate(sudoku).toString())
      .isEqualTo("3151964");
  }

  @Test
  public void testBench_1f() {
    Board sudoku = new Board(new int[][]{
      {0, 0, 0, 2, 0, 0, 0, 0, 6},
      {0, 0, 0, 0, 0, 0, 0, 0, 9},
      {0, 0, 4, 0, 0, 0, 1, 0, 0},
      {0, 9, 0, 0, 0, 0, 0, 0, 4},
      {0, 0, 0, 0, 0, 0, 0, 0, 0},
      {1, 0, 0, 0, 7, 0, 0, 0, 5},
      {0, 0, 3, 0, 0, 0, 2, 0, 0},
      {5, 0, 0, 0, 0, 0, 0, 6, 0},
      {4, 0, 0, 6, 0, 0, 0, 0, 1}
    });
    assertThat((sudoku).getSearchSpace().toString())
      .isEqualTo("1179821854121058002534400000000000000000000000");
    assertThat(ParallelSolver.enumerate(sudoku).toString())
      .isEqualTo("16269895");
  }

  @Test
  public void testBench_2a() {
    Board sudoku = new Board(new int[][]{
      {0, 0, 9, 3, 0, 0, 0, 0, 1},
      {0, 6, 0, 0, 7, 8, 0, 0, 0},
      {4, 0, 0, 0, 0, 0, 2, 0, 0},
      {0, 0, 0, 4, 0, 0, 0, 0, 9},
      {0, 0, 3, 0, 5, 0, 7, 0, 0},
      {1, 0, 0, 0, 0, 6, 0, 0, 0},
      {0, 0, 6, 0, 0, 0, 0, 0, 8},
      {0, 0, 0, 9, 1, 0, 0, 3, 0},
      {2, 0, 0, 0, 0, 7, 4, 0, 0}
    });
    assertThat((sudoku).getSearchSpace().toString())
      .isEqualTo("24563768857859261988864000000000");
    assertThat(ParallelSolver.enumerate(sudoku).toString())
      .isEqualTo("1");
  }

  @Test
  public void testBench_2b() {
    Board sudoku = new Board(new int[][]{
      {0, 0, 9, 3, 0, 0, 0, 0, 1},
      {0, 6, 0, 0, 0, 8, 0, 0, 0},
      {4, 0, 0, 0, 0, 0, 2, 0, 0},
      {0, 0, 0, 4, 0, 0, 0, 0, 9},
      {0, 0, 3, 0, 5, 0, 7, 0, 0},
      {1, 0, 0, 0, 0, 6, 0, 0, 0},
      {0, 0, 6, 0, 0, 0, 0, 0, 8},
      {0, 0, 0, 9, 1, 0, 0, 3, 0},
      {2, 0, 0, 0, 0, 0, 4, 0, 0}
    });
    assertThat((sudoku).getSearchSpace().toString())
      .isEqualTo("261718015484414301673881600000000000");
    assertThat(ParallelSolver.enumerate(sudoku).toString())
      .isEqualTo("276");
  }

  @Test
  public void testBench_2c() {
    Board sudoku = new Board(new int[][]{
      {0, 0, 9, 0, 0, 0, 0, 0, 1},
      {0, 6, 0, 0, 0, 8, 0, 0, 0},
      {4, 0, 0, 0, 0, 0, 2, 0, 0},
      {0, 0, 0, 4, 0, 0, 0, 0, 9},
      {0, 0, 3, 0, 5, 0, 7, 0, 0},
      {1, 0, 0, 0, 0, 6, 0, 0, 0},
      {0, 0, 6, 0, 0, 0, 0, 0, 8},
      {0, 0, 0, 0, 1, 0, 0, 3, 0},
      {2, 0, 0, 0, 0, 0, 4, 0, 0}
    });
    assertThat((sudoku).getSearchSpace().toString())
      .isEqualTo("5546527766851092480000000000000000000000");
    assertThat(ParallelSolver.enumerate(sudoku).toString())
      .isEqualTo("32128");
  }

  @Test
  public void testBench_2d() {
    Board sudoku = new Board(new int[][]{
      {0, 0, 9, 0, 0, 0, 0, 0, 1},
      {0, 6, 0, 0, 0, 8, 0, 0, 0},
      {4, 0, 0, 0, 0, 0, 2, 0, 0},
      {0, 0, 0, 0, 0, 0, 0, 0, 9},
      {0, 0, 3, 0, 0, 0, 7, 0, 0},
      {1, 0, 0, 0, 0, 6, 0, 0, 0},
      {0, 0, 6, 0, 0, 0, 0, 0, 8},
      {0, 0, 0, 0, 1, 0, 0, 3, 0},
      {2, 0, 0, 0, 0, 0, 4, 0, 0}
    });
    assertThat((sudoku).getSearchSpace().toString())
      .isEqualTo("54366191037898352756785152000000000000000000");
    assertThat(ParallelSolver.enumerate(sudoku).toString())
      .isEqualTo("1014785");
  }

  @Test
  public void testBench_2e() {
    Board sudoku = new Board(new int[][]{
      {0, 0, 9, 0, 0, 0, 0, 0, 0},
      {0, 6, 0, 0, 0, 8, 0, 0, 0},
      {4, 0, 0, 0, 0, 0, 2, 0, 0},
      {0, 0, 0, 0, 0, 0, 0, 0, 9},
      {0, 0, 3, 0, 0, 0, 7, 0, 0},
      {1, 0, 0, 0, 0, 6, 0, 0, 0},
      {0, 0, 6, 0, 0, 0, 0, 0, 8},
      {0, 0, 0, 0, 1, 0, 0, 3, 0},
      {2, 0, 0, 0, 0, 0, 4, 0, 0}
    });
    assertThat((sudoku).getSearchSpace().toString())
      .isEqualTo("4281337544234495279596830720000000000000000000");
    assertThat(ParallelSolver.enumerate(sudoku).toString())
      .isEqualTo("7388360");
  }

  @Test
  public void testBench_2f() {
    Board sudoku = new Board(new int[][]{
      {0, 0, 9, 0, 0, 0, 0, 0, 0},
      {0, 6, 0, 0, 0, 8, 0, 0, 0},
      {4, 0, 0, 0, 0, 0, 2, 0, 0},
      {0, 0, 0, 0, 0, 0, 0, 0, 9},
      {0, 0, 3, 0, 0, 0, 7, 0, 0},
      {1, 0, 0, 0, 0, 6, 0, 0, 0},
      {0, 0, 6, 0, 0, 0, 0, 0, 8},
      {0, 0, 0, 0, 1, 0, 0, 3, 0},
      {2, 0, 0, 0, 0, 0, 0, 0, 0}
    });
    assertThat((sudoku).getSearchSpace().toString())
      .isEqualTo("509895408914038847535316992000000000000000000000");
    assertThat(ParallelSolver.enumerate(sudoku).toString())
      .isEqualTo("48794239");
  }
}
