package com.kohlschutter.util;

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
  
  public static void main(String[] args) {
    System.out.println(ProcessUtilVintage.getPid());
  }
}
