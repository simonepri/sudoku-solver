package sudoku;

// checkstyle-disable-next-line AvoidStarImport
import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class AppTest {
  @Test public void testAppHasAGreeting() {
    App classUnderTest = new App();
    assertThat(classUnderTest.getGreeting()).isEqualTo("app should have a greeting");
  }
}
