package sudoku.util;

// checkstyle-disable-next-line AvoidStarImport
import static org.assertj.core.api.Assertions.*;

import java.math.BigInteger;

import org.junit.Test;

public class BigIntProdTest {
  @Test
  public void testMul() {
    assertThat(
      new BigIntProd(1)
        .multiply(Long.MIN_VALUE)
        .multiply(Long.MIN_VALUE)
        .get().toString()
    ).isEqualTo(
      BigInteger.valueOf(1)
        .multiply(BigInteger.valueOf(Long.MIN_VALUE))
        .multiply(BigInteger.valueOf(Long.MIN_VALUE))
        .toString()
    );
    assertThat(
      new BigIntProd(1)
        .multiply(0)
        .get().toString()
    ).isEqualTo(
      BigInteger.valueOf(1)
        .multiply(BigInteger.valueOf(0))
        .toString()
    );
  }

  @Test
  public void testSignum() {
    assertThat(
      new BigIntProd(42).signum()
    ).isEqualTo(
      BigInteger.valueOf(42).signum()
    );

    assertThat(
      new BigIntProd(-42).signum()
    ).isEqualTo(
      BigInteger.valueOf(-42).signum()
    );

    assertThat(
      new BigIntProd(0).signum()
    ).isEqualTo(
      BigInteger.valueOf(0).signum()
    );
  }
}
