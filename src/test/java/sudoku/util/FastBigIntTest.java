package sudoku.util;

// checkstyle-disable-next-line AvoidStarImport
import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class FastBigIntTest {
  @Test
  public void testInc() {
    assertThat(
      new FastBigInt(0)
        .inc()
        .get().toString()
    ).isEqualTo("1");
  }

  @Test
  public void testDec() {
    assertThat(
      new FastBigInt(0)
        .dec()
        .get().toString()
    ).isEqualTo("-1");
  }

  @Test
  public void testAdd() {
    assertThat(
      new FastBigInt(0)
        .add(42)
        .get().toString()
    ).isEqualTo("42");
    assertThat(
      new FastBigInt(0)
        .add(-42)
        .get().toString()
    ).isEqualTo("-42");
  }

  @Test
  public void testSub() {
    assertThat(
      new FastBigInt(0)
        .subtract(42)
        .get().toString()
    ).isEqualTo("-42");
    assertThat(
      new FastBigInt(0)
        .subtract(-42)
        .get().toString()
    ).isEqualTo("42");
  }

  @Test
  public void testMul() {
    assertThat(
      new FastBigInt(42)
        .multiply(42)
        .get().toString()
    ).isEqualTo("1764");
    assertThat(
      new FastBigInt(42)
        .multiply(-42)
        .get().toString()
    ).isEqualTo("-1764");
    assertThat(
      new FastBigInt(Long.MAX_VALUE)
        .multiply(Long.MAX_VALUE)
        .get().toString()
    ).isEqualTo("85070591730234615847396907784232501249");
  }

  @Test
  public void testDiv() {
    assertThat(
      new FastBigInt(42)
        .divide(42)
        .get().toString()
    ).isEqualTo("1");
    assertThat(
      new FastBigInt(42)
        .divide(-42)
        .get().toString()
    ).isEqualTo("-1");
    assertThat(
      new FastBigInt(Long.MIN_VALUE)
        .multiply(Long.MAX_VALUE)
        .divide(Long.MIN_VALUE)
        .get().toString()
    ).isEqualTo(Long.toString(Long.MAX_VALUE));
  }

  @Test
  public void testSignum() {
    assertThat(
      new FastBigInt(42).signum()
    ).isEqualTo(1);
    assertThat(
      new FastBigInt(-42).signum()
    ).isEqualTo(-1);
    assertThat(
      new FastBigInt(0).signum()
    ).isEqualTo(0);
  }
}
