package com.kohlschutter.testutil;

/**
 * Some assertion-related helper methods.
 * 
 * @author Christian Kohlschütter
 */
public final class AssertUtil {
  private AssertUtil() {
    throw new IllegalStateException("No instances");
  }

  /**
   * Ignore the given value. Can be used to silence compiler warnings etc.
   * 
   * @param o The value to ignore.
   */
  public static void ignoreValue(Object o) {
  }
}
