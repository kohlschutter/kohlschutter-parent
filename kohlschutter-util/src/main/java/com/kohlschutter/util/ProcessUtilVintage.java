package com.kohlschutter.util;

import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class ProcessUtilVintage {
  private static final Pattern PAT_PID_IN_MX_BEAN_NAME = Pattern.compile("^([0-9]+)\\@");

  private ProcessUtilVintage() {
    throw new IllegalStateException("No instances");
  }

  /**
   * Returns the PID of the current process.
   * 
   * Workaround for Java 8 where <code>ProcessHandle.current().pid()</code> is not available.
   * Nevertheless, this should still work in newer versions.
   * 
   * @return The PID
   * @throws IllegalStateException if the PID could not be determined.
   */
  public static long getPid() {
    Class<?> managementClass;
    Class<?> mxBeanClass;
    try {
      managementClass = Class.forName("java.lang.management.ManagementFactory");
      mxBeanClass = Class.forName("java.lang.management.RuntimeMXBean");
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException("Unable to determine current process PID", e);
    }
    try {
      Object mxBean = managementClass.getMethod("getRuntimeMXBean").invoke(null);

      String name = mxBeanClass.getMethod("getName").invoke(mxBean).toString();
      Matcher m;
      if ((m = PAT_PID_IN_MX_BEAN_NAME.matcher(name)).find()) {
        long pid = Long.parseLong(m.group(1));
        return pid;
      }
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
        | NoSuchMethodException | SecurityException e) {
      throw new IllegalStateException("Unable to determine current process PID", e);
    }
    throw new IllegalStateException("Unable to determine current process PID");
  }
}
