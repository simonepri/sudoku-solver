package sudoku.utils;

import java.math.BigInteger;

public class BigCounter implements Comparable<BigCounter> {
  private BigInteger count = BigInteger.ZERO;
  private long modcount = 0L;

  /**
   * Default Constructor.
   */
  public BigCounter() {}

  /**
   * Default Constructor.
   * @param initial an initial value for the counter.
   */
  public BigCounter(long initial) {
    modcount = initial;
  }

  /**
   * Default Constructor.
   * @param initial an initial value for the counter.
   */
  public BigCounter(BigInteger initial) {
    count = initial;
  }

  /**
   * Increment the counters.
   */
  public void inc() {
    if (modcount == Long.MAX_VALUE) {
      count = count.add(BigInteger.valueOf(modcount));
      modcount = 1L;
    } else {
      modcount++;
    }
  }

  /**
   * Decrement the counters.
   */
  public void dec() {
    if (modcount == Long.MIN_VALUE) {
      count = count.add(BigInteger.valueOf(modcount));
      modcount = -1L;
    } else {
      modcount--;
    }
  }

  /**
   * Get the value of the counter.
   */
  public BigInteger get() {
    if (modcount != 0L) {
      count = count.add(BigInteger.valueOf(modcount));
      modcount = 0L;
    }
    return count;
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
