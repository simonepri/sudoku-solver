package sudoku.util;

import java.math.BigInteger;

public class FastBigInt implements Comparable<FastBigInt> {
  private BigInteger bigValue = BigInteger.ZERO;
  private long modSum = 0L;

  /**
   * Default Constructor.
   */
  public FastBigInt() {}

  /**
   * Default Constructor.
   * @param initial an initial value.
   */
  public FastBigInt(long initial) {
    modSum = initial;
  }

  /**
   * Default Constructor.
   * @param initial an initial value.
   */
  public FastBigInt(BigInteger initial) {
    bigValue = initial;
  }

  /**
   * Get the value.
   */
  public BigInteger get() {
    applyModSum();
    return bigValue;
  }

  /**
   * Increment.
   */
  public FastBigInt inc() {
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
  public FastBigInt dec() {
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
  public FastBigInt add(long value) {
    if (additionOverflows(modSum, value)) {
      applyModSum();
    }
    modSum += value;
    return this;
  }

  /**
   * Add a value.
   * @param value a value to add.
   */
  public FastBigInt add(BigInteger value) {
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
  public FastBigInt add(FastBigInt other) {
    add(other.bigValue);
    add(other.modSum);
    return this;
  }

  /**
   * Subtract a value.
   * @param value a value to subtract.
   */
  public FastBigInt sub(long value) {
    if (subtractionOverflows(modSum, value)) {
      applyModSum();
    }
    modSum -= value;
    return this;
  }

  /**
   * Add a value.
   * @param value a value to subtract.
   */
  public FastBigInt sub(BigInteger value) {
    if (value == BigInteger.ONE) {
      return dec();
    }
    bigValue = bigValue.subtract(value);
    return this;
  }

  /**
   * Add a value.
   * @param other another instance of this class to subtract.
   */
  public FastBigInt sub(FastBigInt other) {
    sub(other.bigValue);
    sub(other.modSum);
    return this;
  }

  private void applyModSum() {
    if (modSum == 0L) {
      return;
    }
    bigValue = bigValue.add(BigInteger.valueOf(modSum));
    modSum = 0L;
  }

  private static boolean additionOverflows(final long a, final long b) {
    return (b > 0L && a > Long.MAX_VALUE - b)
        || (b < 0L && a < Long.MIN_VALUE - b);
  }

  private static boolean subtractionOverflows(final long a, final long b) {
    return (b == Long.MIN_VALUE && a > -1)
        || (b > 0L && a > Long.MAX_VALUE - b)
        || (b < 0L && a < Long.MIN_VALUE - b);
  }


  @Override
  public String toString() {
    return get().toString();
  }

  @Override
  public int compareTo(FastBigInt c) {
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
