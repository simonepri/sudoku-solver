package sudoku.util;

import java.math.BigInteger;

public class FastBigInt implements Comparable<FastBigInt> {
  private BigInteger bigValue = BigInteger.ZERO;
  private long modSum = 0L;
  private int modMul = 1;
  private int modDiv = 1;

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
    applyModMulDiv();
    applyModSum();
    return bigValue;
  }

  /**
   * Increment.
   */
  public FastBigInt inc() {
    applyModMulDiv();
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
    applyModMulDiv();
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
    applyModMulDiv();
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
    applyModMulDiv();
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
    applyModMulDiv();
    add(other.bigValue);
    add(other.modSum);
    return this;
  }

  /**
   * Subtract a value.
   * @param value a value to subtract.
   */
  public FastBigInt subtract(long value) {
    applyModMulDiv();
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
  public FastBigInt subtract(BigInteger value) {
    applyModMulDiv();
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
  public FastBigInt subtract(FastBigInt other) {
    applyModMulDiv();
    subtract(other.bigValue);
    subtract(other.modSum);
    return this;
  }

  /**
   * Multiply for a value.
   * @param value a value to multiply for.
   */
  public FastBigInt multiply(int value) {
    applyModSum();
    if (multiplyOverflows(modMul, value)) {
      applyModMul();
    }
    modMul *= value;
    return this;
  }

  /**
   * Multiply for value.
   * @param value a value to multiply for.
   */
  public FastBigInt multiply(BigInteger value) {
    applyModSum();
    bigValue = bigValue.multiply(value);
    return this;
  }

  /**
   * Divide for value.
   * @param other another instance of this class to divide for.
   */
  public FastBigInt multiply(FastBigInt other) {
    applyModSum();
    multiply(other.modDiv);
    multiply(other.bigValue);
    divide(other.modMul);
    return this;
  }

  /**
   * Divide for a value.
   * @param value to value to divide for.
   */
  public FastBigInt divide(int value) {
    if (value == 0) {
      throw new ArithmeticException("FastBigInt: division by zero");
    }
    applyModSum();
    if (multiplyOverflows(modDiv, value)) {
      applyModDiv();
    }
    modDiv *= value;
    return this;
  }

  /**
   * Divide for value.
   * @param value a value to divide for.
   */
  public FastBigInt divide(BigInteger value) {
    if (value == BigInteger.ZERO) {
      throw new ArithmeticException("FastBigInt: division by zero");
    }
    applyModSum();
    bigValue = bigValue.divide(value);
    return this;
  }

  /**
   * Divide for value.
   * @param other another instance of this class to divide for.
   */
  public FastBigInt divide(FastBigInt other) {
    applyModSum();
    multiply(other.modDiv);
    divide(other.bigValue);
    divide(other.modMul);
    return this;
  }

  private void applyModSum() {
    if (modSum == 0L) {
      return;
    }
    bigValue = bigValue.add(BigInteger.valueOf(modSum));
    modSum = 0L;
  }

  private void applyModMul() {
    if (modMul == 1) {
      return;
    }
    bigValue = bigValue.multiply(BigInteger.valueOf(modMul));
    modMul = 1;
  }

  private void applyModDiv() {
    if (modDiv == 1) {
      return;
    }
    bigValue = bigValue.divide(BigInteger.valueOf(modDiv));
    modDiv = 1;
  }

  private void applyModMulDiv() {
    if (modMul == modDiv) {
      if (modMul == 1) {
        return;
      }
      int comDiv = gcd(modMul, modDiv);
      modMul /= comDiv;
      modDiv /= comDiv;
    }
    applyModMul();
    applyModDiv();
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

  private static boolean multiplyOverflows(final int a, final int b) {
    if (b == 0) {
      return false;
    }
    long result = a * b;
    return a == result / b;
  }

  private static int gcd(int a, int b) {
    int r;
    while (b != 0) {
      r = a % b;
      a = b;
      b = r;
    }
    return a;
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
