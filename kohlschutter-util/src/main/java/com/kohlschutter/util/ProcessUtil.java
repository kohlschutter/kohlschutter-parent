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
 * Helper methods to access the current Java process.
 *
 * @author Christian Kohlschütter
 */
public final class ProcessUtil {
  private ProcessUtil() {
    throw new IllegalStateException("No instances");
  }

  /**
   * Returns the PID of the current process.
   *
   * @return The PID
   * @throws IllegalStateException if the PID could not be determined.
   */
  public static long getPid() {
    return JavaReleaseShim.getPid();
  }

  /**
   * Returns the java command used to invoke this VM, or {@code null} if unable to comply.
   *
   * @return The command, or {@code null}.
   */
  public static String getJavaCommand() {
    return JavaReleaseShim.getJavaCommand();
  }

  /**
   * Returns the java arguments (without the command) used to invoke this VM, or {@code null} if
   * unable to comply.
   *
   * @return The command, or {@code null}.
   */
  public static String[] getJavaCommandArguments() {
    return JavaReleaseShim.getJavaCommandArguments();
  }
}
