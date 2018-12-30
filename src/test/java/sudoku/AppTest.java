package sudoku;

// checkstyle-disable-next-line AvoidStarImport
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class AppTest {
  @Test
  public void testNoArgs() {
    App.Args args = new App.Args();
    App main = new App(args);

    List<String> out = new LinkedList<>();
    main.run(out::add);
    assertThat(out.toString()).contains("Usage");
  }

  @Test
  public void testUsage() {
    App.Args args = new App.Args();
    args.help = true;
    App main = new App(args);

    List<String> out = new LinkedList<>();
    main.run(out::add);
    assertThat(out.toString()).contains("Usage");
  }

  @Test
  public void testInvalidFile() {
    App.Args args = new App.Args();
    args.filenames = Arrays.asList("src/test/fixtures/invalid.txt");
    App main = new App(args);

    List<String> out = new LinkedList<>();
    main.run(out::add);
    assertThat(out.toString()).contains("Invalid character found");
  }

  @Test
  public void testValidFileSequential() {
    App.Args args = new App.Args();
    args.sequential = true;
    args.filenames = Arrays.asList("src/test/fixtures/valid.txt");
    App main = new App(args);

    List<String> out = new LinkedList<>();
    main.run(out::add);
    assertThat(out.toString()).contains("276");
  }

  @Test
  public void testValidFileParallel() {
    App.Args args = new App.Args();
    args.filenames = Arrays.asList("src/test/fixtures/valid.txt");
    App main = new App(args);

    List<String> out = new LinkedList<>();
    main.run(out::add);
    assertThat(out.toString()).contains("276");
  }
}
