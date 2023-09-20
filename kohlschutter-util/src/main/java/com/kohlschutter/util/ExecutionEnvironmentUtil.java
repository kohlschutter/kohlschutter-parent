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
package com.kohlschutter.util;

import java.util.Locale;

/**
 * Utility class to check which environment we run in.
 *
 * @author Christian Kohlschütter
 */
public final class ExecutionEnvironmentUtil {
  private static final boolean IN_ECLIPSE = //
      (System.getProperty("java.class.path", "").contains("/target-eclipse/") || //
          System.getProperty("jdk.module.path", "").contains("/target-eclipse/") //
      );

  private static final boolean WINDOWS = //
      System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("windows");

  private ExecutionEnvironmentUtil() {
    throw new IllegalStateException("No instances");
  }

  /**
   * Checks whether the code is being run from within Eclipse.
   *
   * @return {@code true} if knowingly so.
   */
  public static boolean isInEclipse() {
    return IN_ECLIPSE;
  }

  /**
   * Checks whether the code is being run from Windows.
   *
   * @return {@code true} if knowingly so.
   */
  public static boolean isWindows() {
    return WINDOWS;
  }
}
