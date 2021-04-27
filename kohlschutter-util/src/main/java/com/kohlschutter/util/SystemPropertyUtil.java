package com.kohlschutter.util;

public final class SystemPropertyUtil {
  private SystemPropertyUtil() {
    throw new IllegalStateException("No instances");
  }

  /**
   * Retrieves an integer value stored as a system property.
   *
   * @param key The name of the system property.
   * @param defaultValue The default value, if no value has been set.
   * @return The value
   * @throws IllegalArgumentException if the default value could not be parsed
   */
  public static int getIntSystemProperty(String key, int defaultValue) {
    String v = System.getProperty(key, null);
    if (v == null || v.trim().isEmpty()) { // NOPMD
      return defaultValue;
    }
    try {
      if ("true".equalsIgnoreCase(v)) {
        return 1;
      } else if ("false".equalsIgnoreCase(v)) {
        return 0;
      }
      return Integer.parseInt(v);
    } catch (Exception e) {
      throw new IllegalArgumentException("Illegal value for system property " + key + ": " + v, e);
    }
  }
}
