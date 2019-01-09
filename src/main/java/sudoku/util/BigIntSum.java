package sudoku.util;

import java.math.BigInteger;

public class BigIntSum implements Comparable<BigIntSum> {
  private BigInteger bigValue = BigInteger.ZERO;
  private long modSum = 0L;

  /**
   * Default Constructor.
   */
  public BigIntSum() {}

  /**
   * Default Constructor.
   * @param initial an initial value.
   */
  public BigIntSum(long initial) {
    modSum = initial;
  }

  /**
   * Default Constructor.
   * @param initial an initial value.
   */
  public BigIntSum(BigInteger initial) {
    bigValue = initial;
  }

  /**
   * Clone Constructor.
   * @param other an other instance of this class.
   */
  public BigIntSum(BigIntSum other) {
    bigValue = other.bigValue;
    modSum = other.modSum;
  }

  /**
   * Get the value.
   */
  public BigInteger get() {
    if (modSum != 0L) {
      bigValue = bigValue.add(BigInteger.valueOf(modSum));
      modSum = 0L;
    }
    return bigValue;
  }

  /**
   * Increment.
   */
  public BigIntSum inc() {
    if (modSum == Long.MAX_VALUE) {
      bigValue = bigValue.add(BigInteger.valueOf(modSum));
      modSum = 0L;
    }
    modSum++;
    return this;
  }

  /**
   * Decrement.
   */
  public BigIntSum dec() {
    if (modSum == Long.MIN_VALUE) {
      bigValue = bigValue.add(BigInteger.valueOf(modSum));
      modSum = 0L;
    }
    modSum--;
    return this;
  }

  /**
   * Add a value.
   * @param value a value to add.
   */
  public BigIntSum add(long value) {
    if (additionOverflows(modSum, value)) {
      bigValue = bigValue.add(BigInteger.valueOf(modSum));
      modSum = 0L;
    }
    modSum += value;
    return this;
  }

  /**
   * Add a value.
   * @param value a value to add.
   */
  public BigIntSum add(BigInteger value) {
    if (value == BigInteger.ONE) {
      return inc();
    }
    bigValue = bigValue.add(value);
    return this;
  }

  /**
   * Add a value.
   * @param other another instance of this class to add.
   */
  public BigIntSum add(BigIntSum other) {
    add(other.bigValue);
    add(other.modSum);
    return this;
  }

  /**
   * Return the signum function on the value stored.
   */
  public int signum() {
    return get().signum();
  }

  /**
   * Check if the sum of two values overflows.
   * @param a first value.
   * @param b second value.
   */
  private static boolean additionOverflows(final long x, final long y) {
    long r = x + y;
    return ((x ^ r) & (y ^ r)) < 0;
  }

  @Override
  public String toString() {
    return get().toString();
  }

  @Override
  public int compareTo(BigIntSum c) {
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
