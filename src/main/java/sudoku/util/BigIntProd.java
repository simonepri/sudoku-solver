package sudoku.util;

import java.math.BigInteger;

public class BigIntProd implements Comparable<BigIntProd> {
  private BigInteger bigValue = BigInteger.ONE;
  private long modMul = 1L;

  /**
   * Default Constructor.
   */
  public BigIntProd() {}

  /**
   * Default Constructor.
   * @param initial an initial value.
   */
  public BigIntProd(long initial) {
    modMul = initial;
  }

  /**
   * Default Constructor.
   * @param initial an initial value.
   */
  public BigIntProd(BigInteger initial) {
    bigValue = initial;
  }

  /**
   * Clone Constructor.
   * @param other an other instance of this class.
   */
  public BigIntProd(BigIntProd other) {
    bigValue = other.bigValue;
    modMul = other.modMul;
  }

  /**
   * Get the value.
   */
  public BigInteger get() {
    if (modMul != 1L) {
      bigValue = bigValue.multiply(BigInteger.valueOf(modMul));
      modMul = 1L;
    }
    return bigValue;
  }

  /**
   * Moltiply for a value.
   * @param value a value to multiply for.
   */
  public BigIntProd multiply(long value) {
    if (multiplyOverflows(modMul, value)) {
      bigValue = bigValue.multiply(BigInteger.valueOf(modMul));
      modMul = 1L;
    }
    modMul *= value;
    return this;
  }

  /**
   * Moltiply for value.
   * @param value a value to multiply for.
   */
  public BigIntProd multiply(BigInteger value) {
    bigValue = bigValue.multiply(value);
    return this;
  }

  /**
   * Divide for value.
   * @param other another instance of this class to divide for.
   */
  public BigIntProd multiply(BigIntProd other) {
    multiply(other.modMul);
    multiply(other.bigValue);
    return this;
  }

  /**
   * Return the signum function on the value stored.
   */
  public int signum() {
    return Long.signum(modMul) * bigValue.signum();
  }

  /**
   * Check if the product of two values overflows.
   * @param a first value.
   * @param b second value.
   */
  private static boolean multiplyOverflows(final long x, final long y) {
    long r = x * y;
    long ax = Math.abs(x);
    long ay = Math.abs(y);
    return ((ax | ay) >>> 31 != 0)
      && (((y != 0) && (r / y != x)) || (x == Long.MIN_VALUE && y == -1));
  }

  @Override
  public String toString() {
    return get().toString();
  }

  @Override
  public int compareTo(BigIntProd c) {
    return get().compareTo(c.get());
  }

  @Override
  public boolean equals(Object obj) {
    return get().equals(obj);
  }

  @Override
  public int hashCode() {
    return get().hashCode();
  }
}
