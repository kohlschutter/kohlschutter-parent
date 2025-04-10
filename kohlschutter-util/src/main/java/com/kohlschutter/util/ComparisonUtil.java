/*
 * kohlschutter-parent
 *
 * Copyright 2009-2025 Christian Kohlschütter
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
 * Helper methods for comparisons with {@link Comparable}.
 *
 * @author Christian Kohlschütter
 */
public final class ComparisonUtil {
  private ComparisonUtil() {
    throw new IllegalStateException("No instances");
  }

  /**
   * Reverses the sign of a comparison result.
   *
   * In addition to just negating the value, this method takes care of dealing with
   * {@link Integer#MIN_VALUE}, which will be turned into {@link Integer#MAX_VALUE}.
   *
   * @param c The comparison result.
   * @return The reversed result.
   */
  public static int reverseComparisonResult(int c) {
    if (c == Integer.MIN_VALUE) {
      return Integer.MAX_VALUE;
    } else {
      return -c;
    }
  }
}
