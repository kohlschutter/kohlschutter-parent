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

import java.util.concurrent.ExecutionException;

import com.kohlschutter.annotations.compiletime.ExcludeFromCodeCoverageGeneratedReport;

/**
 * Helper methods for exception handling.
 *
 * @author Christian Kohlschütter
 */
public final class ExceptionUtil {
  @ExcludeFromCodeCoverageGeneratedReport(reason = "unreachable")
  private ExceptionUtil() {
    throw new IllegalStateException("No instances");
  }

  /**
   * Unwraps the exception thrown as the cause of an {@link ExecutionException}, if possible.
   *
   * If the cause is not an exception (but some other throwable), the {@link ExecutionException}
   * itself is returned.
   *
   * @param ex The {@link ExecutionException}.
   * @return The {@link ExecutionException}, or its cause {@link Exception}.
   */
  public static Exception unwrapExecutionException(ExecutionException ex) {
    Throwable cause = ex.getCause();
    if (cause instanceof Exception) {
      return ((Exception) cause);
    } else {
      return ex;
    }
  }
}
