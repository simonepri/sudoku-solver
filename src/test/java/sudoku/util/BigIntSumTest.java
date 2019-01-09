package sudoku.util;

// checkstyle-disable-next-line AvoidStarImport
import static org.assertj.core.api.Assertions.*;

import java.math.BigInteger;

import org.junit.Test;

public class BigIntSumTest {
  @Test
  public void testInc() {
    assertThat(
        new BigIntSum(0)
          .inc()
          .get().toString()
    ).isEqualTo(
        BigInteger.valueOf(0)
          .add(BigInteger.valueOf(1))
          .toString()
    );

    assertThat(
        new BigIntSum(Long.MAX_VALUE)
          .inc()
          .get().toString()
    ).isEqualTo(
        BigInteger.valueOf(Long.MAX_VALUE)
          .add(BigInteger.valueOf(1))
          .toString()
    );
  }

  @Test
  public void testDec() {
    assertThat(
        new BigIntSum(0)
          .dec()
          .get().toString()
    ).isEqualTo(
        BigInteger.valueOf(0)
          .subtract(BigInteger.valueOf(1))
          .toString()
    );

    assertThat(
        new BigIntSum(Long.MIN_VALUE)
          .dec()
          .get().toString()
    ).isEqualTo(
        BigInteger.valueOf(Long.MIN_VALUE)
          .subtract(BigInteger.valueOf(1))
          .toString()
    );
  }

  @Test
  public void testAdd() {
    assertThat(
        new BigIntSum(0)
          .add(Long.MAX_VALUE)
          .add(1)
          .get().toString()
    ).isEqualTo(
        BigInteger.valueOf(0)
          .add(BigInteger.valueOf(Long.MAX_VALUE))
          .add(BigInteger.valueOf(1))
          .toString()
    );
  }

  @Test
  public void testSignum() {
    assertThat(
        new BigIntSum(42).signum()
    ).isEqualTo(
        BigInteger.valueOf(42).signum()
    );

    assertThat(
        new BigIntSum(-42).signum()
    ).isEqualTo(
        BigInteger.valueOf(-42).signum()
    );

    assertThat(
        new BigIntSum(0).signum()
    ).isEqualTo(
        BigInteger.valueOf(0).signum()
    );
  }
}
