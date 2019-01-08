package sudoku;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;


public class App {
  public static class Args {
    @Parameter(names = "--help", help = true)
    public boolean help = false;

    @Parameter(names = {"-p"}, description = "Print all the solutions")
    public boolean print = false;

    @Parameter(names = {"-s"}, description = "Disable parallelism")
    public boolean sequential = false;

    @Parameter(names = {"-i"}, description = "Enable benchmarking mode with iterations")
    public int iterations = 0;

    @Parameter(names = {"-t"}, description = "Enable benchmarking mode with time")
    public int time = 0;

    @Parameter(names = {"-cf"}, description = "Sequential cutoff")
    public BigDecimal cutoff = null;

    @Parameter(description = "<filename>[ <filename>]*")
    public List<String> filenames = new ArrayList<>();
  }

  private final boolean help;
  private final boolean print;
  private final boolean sequential;
  private final int iterations;
  private final int time;
  private final BigDecimal cutoff;
  private final List<String> filenames;

  /**
   * Default Constructor.
   * @param args the arguments for the app.
   */
  public App(Args args) {
    help = args.help;
    print = args.print;
    iterations = args.iterations;
    time = args.time;
    cutoff = args.cutoff;
    sequential = args.sequential;
    filenames = new ArrayList<>(args.filenames);
  }

  /**
   * Read a file containing a sudoku description and convert it to an int matrix.
   * @param filename a path to a file or a filname in the cwd.
   */
  private int[][] parse(String filename) throws IOException {
    List<int[]> matrix = new LinkedList<int[]>();

    Scanner scanner = new Scanner(new File(filename));
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      List<Integer> row = new LinkedList<>();
      for (char c : line.toCharArray()) {
        if (c == '.') {
          row.add(0);
        } else if ('0' <= c && c <= '9') {
          row.add(c - '0');
        } else {
          throw new IllegalArgumentException(
            "Invalid character found: " + c + " in " + filename + "\n"
          );
        }
      }
      matrix.add(
          row.stream().mapToInt(Integer::intValue).toArray()
      );
    }
    scanner.close();

    return matrix.toArray(new int[0][]);
  }

  /**
   * Enumerate all the legal solutions of the given board.
   * @param board the board for which to enumerate all the solutions.
   */
  public BigInteger enumerate(Board board) {
    return enumerate(board, null);
  }

  /**
   * Enumerate all the legal solutions of the given board.
   * @param board the board for which to enumerate all the solutions.
   * @param onSolution callback called each time a solution is found.
   */
  public BigInteger enumerate(Board board, Consumer<Board> onSolution) {
    if (sequential) {
      return SequentialSolver.enumerate(board, onSolution);
    }
    if (cutoff != null) {
      ParallelSolver.setSequentialCutoff(cutoff.toBigInteger());
    }
    return ParallelSolver.enumerate(board, onSolution);
  }

  /**
   * Run the app.
   * @param out the app will call this when needs to send a message.
   */
  public int run(Consumer<String> out) {
    if (help || filenames.size() == 0) {
      out.accept(
          "Usage: sudoku [options] <filenames>[,<filenames>]*\n"
          + "  Options:\n"
          + "    --help               Print usage\n"
          + "    -p                   Print all the solutions\n"
          + "    -s                   Disable parallelism\n"
          + "    -cf <integer>        Change default sequential cutoff\n"
          + "    -i <integer>         Enable benchmarking mode with iterations\n"
          + "    -t <integer>         Enable benchmarking mode with time\n"
      );
      return 0;
    }

    try {
      for (String filename : filenames) {
        Board board = new Board(parse(filename));
        if (iterations > 0 || time > 0) {
          int executions = 0;
          // Benchmarking mode
          long timeSum = 0;
          long timeSumSquared = 0;
          long timeMin = -1;
          long timeMax = -1;
          while (true) {
            long start = System.nanoTime();
            BigInteger sc = enumerate(board);
            long end = System.nanoTime();
            long etime = (end - start) / 1000;
            timeSum += etime;
            timeSumSquared += etime * etime;
            if (timeMin == -1 || timeMin > etime) {
              timeMin = etime;
            }
            if (timeMax == -1 || timeMax < etime) {
              timeMax = etime;
            }
            executions++;
            if (iterations > 0 && executions >= iterations) {
              break;
            }
            if (time > 0 && timeSum + timeSum / executions >= time) {
              break;
            }
          }
          long timeAvg = Math.round(timeSum / (double)executions);
          long timeDev = Math.round(Math.sqrt(
              timeSumSquared / (double)executions - Math.pow(timeSum / (double)executions, 2)
          ));
          out.accept(executions + "," + timeAvg + "," + timeDev + "," + timeMin + "," + timeMax);
        } else if (print) {
          // Print mode
          enumerate(board, b -> out.accept(b.toString() + "\n"));
        } else {
          // Normal mode
          BigInteger sp = board.getSearchSpace();
          out.accept("Search space: " + sp + "\n");
          double ff = 100.0 - (board.getFillablesCount() * 100.0) / board.getSize();
          out.accept("Fill factor: " + String.format("%.2f", ff) + "%\n");
          BigInteger sc = enumerate(board);
          out.accept("Legal solutions: " + sc + "\n");
        }
      }
    } catch (NoSuchFileException e) {
      out.accept("No such file: " + e.getFile() + "\n");
      return 1;
    } catch (IllegalArgumentException e) {
      out.accept(e.getMessage() + "\n");
      return 2;
    } catch (Exception e) {
      out.accept("An unexpected error occurred: " + e.getMessage() + "\n");
      return -1;
    }

    return 0;
  }

  /**
   * Entry point of the app.
   * @param argv the arguments.
   */
  public static void main(String[] argv) {
    Args args = new Args();
    JCommander jct = JCommander.newBuilder().addObject(args).build();
    jct.parse(argv);

    App main = new App(args);
    System.exit(main.run(System.out::print));
  }
}
