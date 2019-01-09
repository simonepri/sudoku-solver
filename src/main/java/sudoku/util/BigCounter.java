package sudoku.util;

import java.math.BigInteger;

public class BigCounter implements Comparable<BigCounter> {
  private BigInteger bigValue = BigInteger.ZERO;
  private long modSum = 0L;

  /**
   * Default Constructor.
   */
  public BigCounter() {}

  /**
   * Default Constructor.
   * @param initial an initial value.
   */
  public BigCounter(long initial) {
    modSum = initial;
  }

  /**
   * Default Constructor.
   * @param initial an initial value.
   */
  public BigCounter(BigInteger initial) {
    bigValue = initial;
  }

  /**
   * Increment.
   */
  public BigCounter inc() {
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
  public BigCounter dec() {
    if (modSum == Long.MIN_VALUE) {
      bigValue = bigValue.add(BigInteger.valueOf(modSum));
      modSum = 0L;
    }
    modSum--;
    return this;
  }

  /**
   * Get the value.
   */
  public BigInteger get() {
    applyModSum();
    return bigValue;
  }

  /**
   * Add a value.
   * @param value a value to add.
   */
  public BigCounter add(long value) {
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
  public BigCounter add(BigInteger value) {
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
  public BigCounter add(BigCounter other) {
    add(other.bigValue);
    add(other.modSum);
    return this;
  }

  /**
   * Subtract a value.
   * @param value a value to subtract.
   */
  public BigCounter sub(long value) {
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
  public BigCounter sub(BigInteger value) {
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
  public BigCounter sub(BigCounter other) {
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
  public int compareTo(BigCounter c) {
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
