package sudoku;

// checkstyle-disable-next-line AvoidStarImport
// checkstyle-disable-next-line AvoidStarImport
import static org.junit.Assert.*;

import org.junit.Test;

public class AppTest {
  @Test public void testAppHasAGreeting() {
    App classUnderTest = new App();
    assertNotNull("app should have a greeting", classUnderTest.getGreeting());
  }
}
