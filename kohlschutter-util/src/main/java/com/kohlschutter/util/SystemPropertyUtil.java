/*
 * kohlschutter-parent
 *
 * Copyright 2009-2024 Christian Kohlschütter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kohlschutter.util;

/**
 * Helper methods to simplify working with system properties.
 *
 * @author Christian Kohlschütter
 */
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

  /**
   * Retrieves a boolean value stored as a system property.
   *
   * @param key The name of the system property.
   * @param defaultValue The default value, if no value has been set.
   * @return The value
   * @throws IllegalArgumentException if the default value could not be parsed
   */
  public static boolean getBooleanSystemProperty(String key, boolean defaultValue) {
    String v = System.getProperty(key, null);
    if (v == null || v.trim().isEmpty()) { // NOPMD
      return defaultValue;
    }
    try {
      if ("1".equals(v)) {
        return true;
      } else if ("yes".equalsIgnoreCase(v)) {
        return true;
      } else if ("y".equalsIgnoreCase(v)) {
        return true;
      } else if ("on".equalsIgnoreCase(v)) {
        return true;
      }
      return Boolean.parseBoolean(v);
    } catch (Exception e) {
      throw new IllegalArgumentException("Illegal value for system property " + key + ": " + v, e);
    }
  }
}
