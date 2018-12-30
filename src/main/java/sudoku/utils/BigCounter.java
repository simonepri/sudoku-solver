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

  /**
   * Add a value to the counter.
   * @param value a value to add to the counter.
   */
  public void add(long value) {
    if (value > 0) {
      if (modcount <= 0) {
        modcount += value;
        return;
      }
      long left = Long.MAX_VALUE - modcount;
      if (left >= value) {
        modcount += value;
      } else {
        count = count.add(BigInteger.valueOf(Long.MAX_VALUE));
        modcount = value - left;
      }
    } else if (value < 0) {
      if (modcount >= 0) {
        modcount += value;
        return;
      }
      long left = Long.MIN_VALUE - modcount;
      if (left <= value) {
        modcount += value;
      } else {
        count = count.add(BigInteger.valueOf(Long.MIN_VALUE));
        modcount = value - left;
      }
    }
  }
  
  /**
   * Sum another counter to this counter.
   * @param other a value to add to the counter.
   */
  public void add(BigCounter other) {
    if (other.count == BigInteger.ONE) {
      inc();
    } else if (other.count != BigInteger.ZERO) {
      count = count.add(other.count);
    }
    add(other.modcount);
  }

  /**
   * Add a value to the counter.
   * @param value a value to add to the counter.
   */
  public void add(BigInteger value) {
    if (value == BigInteger.ZERO) {
      return;
    } else if (value == BigInteger.ONE) {
      inc();
    } else {
      count = count.add(value);
    }
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
