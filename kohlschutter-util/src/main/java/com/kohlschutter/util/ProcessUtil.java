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
