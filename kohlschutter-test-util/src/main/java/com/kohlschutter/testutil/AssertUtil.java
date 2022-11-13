/*
 * kohlschutter-parent
 *
 * Copyright 2009-2022 Christian Kohlschütter
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
package com.kohlschutter.testutil;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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

  /**
   * Asserts that a given set contains a subset of elements.
   *
   * @param <T> The element type.
   * @param set The set to check.
   * @param elements The expected elements.
   */
  public static <T> void assertSetContains(Set<T> set, Collection<T> elements) {
    assertSetContains(set, elements, (String) null);
  }

  /**
   * Asserts that a given set contains a subset of elements.
   *
   * @param <T> The element type.
   * @param set The set to check.
   * @param message The error message if the assertion fails.
   * @param elements The expected elements.
   */
  public static <T> void assertSetContains(Set<T> set, Collection<T> elements, String message) {
    Set<T> actual = new HashSet<>(set);
    Set<T> expected = new HashSet<>(elements);
    actual.retainAll(expected);
    assertEquals(expected, actual);
  }
}
