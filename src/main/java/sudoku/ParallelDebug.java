package sudoku;


public class ParallelDebug {

  public class ParallelSolverBenchTest {
    public void testBench_1a() {
      Board sudoku = new Board(
          new int[][] { { 8, 0, 0, 2, 0, 3, 0, 0, 6 }, { 0, 5, 0, 7, 0, 0, 0, 0, 9 }, { 0, 0, 4, 0, 0, 0, 1, 0, 0 },
              { 7, 9, 0, 0, 5, 0, 0, 0, 4 }, { 0, 0, 0, 8, 0, 6, 0, 0, 0 }, { 1, 0, 0, 0, 7, 0, 0, 9, 5 },
              { 0, 0, 3, 0, 0, 0, 2, 0, 0 }, { 5, 0, 0, 0, 0, 7, 0, 6, 0 }, { 4, 0, 0, 6, 0, 9, 0, 0, 1 } });
      new SubtreeTask(sudoku).compute().toString();
    }

    public void testBench_1b() {
      Board sudoku = new Board(
          new int[][] { { 8, 0, 0, 2, 0, 0, 0, 0, 6 }, { 0, 5, 0, 0, 0, 0, 0, 0, 9 }, { 0, 0, 4, 0, 0, 0, 1, 0, 0 },
              { 0, 9, 0, 0, 5, 0, 0, 0, 4 }, { 0, 0, 0, 0, 0, 6, 0, 0, 0 }, { 1, 0, 0, 0, 7, 0, 0, 0, 5 },
              { 0, 0, 3, 0, 0, 0, 2, 0, 0 }, { 5, 0, 0, 0, 0, 7, 0, 6, 0 }, { 4, 0, 0, 6, 0, 0, 0, 0, 1 } });
      new SubtreeTask(sudoku).compute().toString();
    }

    public void testBench_1c() {
      Board sudoku = new Board(
          new int[][] { { 0, 0, 0, 2, 0, 0, 0, 0, 6 }, { 0, 5, 0, 0, 0, 0, 0, 0, 9 }, { 0, 0, 4, 0, 0, 0, 1, 0, 0 },
              { 0, 9, 0, 0, 5, 0, 0, 0, 4 }, { 0, 0, 0, 0, 0, 6, 0, 0, 0 }, { 1, 0, 0, 0, 7, 0, 0, 0, 5 },
              { 0, 0, 3, 0, 0, 0, 2, 0, 0 }, { 5, 0, 0, 0, 0, 0, 0, 6, 0 }, { 4, 0, 0, 6, 0, 0, 0, 0, 1 } });
      new SubtreeTask(sudoku).compute().toString();
    }

    public void testBench_1d() {
      Board sudoku = new Board(
          new int[][] { { 0, 0, 0, 2, 0, 0, 0, 0, 6 }, { 0, 5, 0, 0, 0, 0, 0, 0, 9 }, { 0, 0, 4, 0, 0, 0, 1, 0, 0 },
              { 0, 9, 0, 0, 0, 0, 0, 0, 4 }, { 0, 0, 0, 0, 0, 6, 0, 0, 0 }, { 1, 0, 0, 0, 7, 0, 0, 0, 5 },
              { 0, 0, 3, 0, 0, 0, 2, 0, 0 }, { 5, 0, 0, 0, 0, 0, 0, 6, 0 }, { 4, 0, 0, 6, 0, 0, 0, 0, 1 } });
      new SubtreeTask(sudoku).compute().toString();
    }

    public void testBench_1e() {
      Board sudoku = new Board(
          new int[][] { { 0, 0, 0, 2, 0, 0, 0, 0, 6 }, { 0, 0, 0, 0, 0, 0, 0, 0, 9 }, { 0, 0, 4, 0, 0, 0, 1, 0, 0 },
              { 0, 9, 0, 0, 0, 0, 0, 0, 4 }, { 0, 0, 0, 0, 0, 6, 0, 0, 0 }, { 1, 0, 0, 0, 7, 0, 0, 0, 5 },
              { 0, 0, 3, 0, 0, 0, 2, 0, 0 }, { 5, 0, 0, 0, 0, 0, 0, 6, 0 }, { 4, 0, 0, 6, 0, 0, 0, 0, 1 } });
      new SubtreeTask(sudoku).compute().toString();
    }

    public void testBench_2a() {
      Board sudoku = new Board(
          new int[][] { { 0, 0, 9, 3, 0, 0, 0, 0, 1 }, { 0, 6, 0, 0, 7, 8, 0, 0, 0 }, { 4, 0, 0, 0, 0, 0, 2, 0, 0 },
              { 0, 0, 0, 4, 0, 0, 0, 0, 9 }, { 0, 0, 3, 0, 5, 0, 7, 0, 0 }, { 1, 0, 0, 0, 0, 6, 0, 0, 0 },
              { 0, 0, 6, 0, 0, 0, 0, 0, 8 }, { 0, 0, 0, 9, 1, 0, 0, 3, 0 }, { 2, 0, 0, 0, 0, 7, 4, 0, 0 } });
      new SubtreeTask(sudoku).compute().toString();
    }

    public void testBench_2b() {
      Board sudoku = new Board(
          new int[][] { { 0, 0, 9, 3, 0, 0, 0, 0, 1 }, { 0, 6, 0, 0, 0, 8, 0, 0, 0 }, { 4, 0, 0, 0, 0, 0, 2, 0, 0 },
              { 0, 0, 0, 4, 0, 0, 0, 0, 9 }, { 0, 0, 3, 0, 5, 0, 7, 0, 0 }, { 1, 0, 0, 0, 0, 6, 0, 0, 0 },
              { 0, 0, 6, 0, 0, 0, 0, 0, 8 }, { 0, 0, 0, 9, 1, 0, 0, 3, 0 }, { 2, 0, 0, 0, 0, 0, 4, 0, 0 } });
      new SubtreeTask(sudoku).compute().toString();
    }

    public void testBench_2c() {
      Board sudoku = new Board(
          new int[][] { { 0, 0, 9, 0, 0, 0, 0, 0, 1 }, { 0, 6, 0, 0, 0, 8, 0, 0, 0 }, { 4, 0, 0, 0, 0, 0, 2, 0, 0 },
              { 0, 0, 0, 4, 0, 0, 0, 0, 9 }, { 0, 0, 3, 0, 5, 0, 7, 0, 0 }, { 1, 0, 0, 0, 0, 6, 0, 0, 0 },
              { 0, 0, 6, 0, 0, 0, 0, 0, 8 }, { 0, 0, 0, 0, 1, 0, 0, 3, 0 }, { 2, 0, 0, 0, 0, 0, 4, 0, 0 } });
      new SubtreeTask(sudoku).compute().toString();
    }

    public void testBench_2d() {
      Board sudoku = new Board(
          new int[][] { { 0, 0, 9, 0, 0, 0, 0, 0, 1 }, { 0, 6, 0, 0, 0, 8, 0, 0, 0 }, { 4, 0, 0, 0, 0, 0, 2, 0, 0 },
              { 0, 0, 0, 0, 0, 0, 0, 0, 9 }, { 0, 0, 3, 0, 0, 0, 7, 0, 0 }, { 1, 0, 0, 0, 0, 6, 0, 0, 0 },
              { 0, 0, 6, 0, 0, 0, 0, 0, 8 }, { 0, 0, 0, 0, 1, 0, 0, 3, 0 }, { 2, 0, 0, 0, 0, 0, 4, 0, 0 } });
      new SubtreeTask(sudoku).compute().toString();
    }

    public void testBench_2e() {
      Board sudoku = new Board(
          new int[][] { { 0, 0, 9, 0, 0, 0, 0, 0, 0 }, { 0, 6, 0, 0, 0, 8, 0, 0, 0 }, { 4, 0, 0, 0, 0, 0, 2, 0, 0 },
              { 0, 0, 0, 0, 0, 0, 0, 0, 9 }, { 0, 0, 3, 0, 0, 0, 7, 0, 0 }, { 1, 0, 0, 0, 0, 6, 0, 0, 0 },
              { 0, 0, 6, 0, 0, 0, 0, 0, 8 }, { 0, 0, 0, 0, 1, 0, 0, 3, 0 }, { 2, 0, 0, 0, 0, 0, 4, 0, 0 } });
      new SubtreeTask(sudoku).compute().toString();
    }
  }

  public void compute() {

    ParallelSolverBenchTest test = new ParallelSolverBenchTest();
    test.testBench_1d();
    test.testBench_1e();
    test.testBench_1c();
    test.testBench_1b();
    test.testBench_2a();
    test.testBench_2b();
  }

  public static void main(String[] args) {
    new ParallelDebug().compute();
  }
}
