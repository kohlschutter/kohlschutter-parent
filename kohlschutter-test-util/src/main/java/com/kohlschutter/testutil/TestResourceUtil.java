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
package com.kohlschutter.testutil;

import java.net.URL;

/**
 * Utility class for retrieving resources from the test classpath.
 *
 * @author Christian Kohlschütter
 */
public final class TestResourceUtil {
  private TestResourceUtil() {
    throw new IllegalStateException("No instances");
  }

  /**
   * Returns the URL for a required resource, by path relative to the given class.
   *
   * @param classRef The class the path is relative to.
   * @param name The relative pathname.
   * @return The URL.
   * @throws IllegalStateException if the resource could not be retrieved.
   */
  public static URL getRequiredResource(Class<?> classRef, String name) {
    URL url = classRef.getResource(name);
    if (url == null) {
      throw new IllegalStateException("Missing expected test resource: " + name);
    }
    return url;
  }
}
