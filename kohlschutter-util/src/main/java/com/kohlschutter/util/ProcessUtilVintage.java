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

import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class ProcessUtilVintage {
  private static final Pattern PAT_PID_IN_MX_BEAN_NAME = Pattern.compile("^([0-9]+)\\@");

  private ProcessUtilVintage() {
    throw new IllegalStateException("No instances");
  }

  private static int getAndroidPid() throws ClassNotFoundException, IllegalAccessException,
      InvocationTargetException, NoSuchMethodException, SecurityException {
    Class<?> androidClass = Class.forName("android.os.Process");
    return (Integer) (androidClass.getMethod("myPid").invoke(null));
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
      try {
        return getAndroidPid();
      } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException
          | NoSuchMethodException | SecurityException e2) {
        IllegalStateException ise = new IllegalStateException(
            "Unable to determine current process PID", e);
        ise.addSuppressed(e2);
        throw ise;
      }
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
