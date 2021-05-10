package com.kohlschutter.util;

import java.util.concurrent.ExecutionException;

import com.kohlschutter.annotations.compiletime.ExcludeFromCodeCoverageGeneratedReport;

/**
 * Helper methods for exception handling.
 * 
 * @author Christian Kohlschütter
 */
public final class ExceptionUtil {
  @ExcludeFromCodeCoverageGeneratedReport
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
