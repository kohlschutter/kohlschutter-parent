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

import java.lang.management.ManagementFactory;
import java.util.Locale;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.eclipse.jdt.annotation.Nullable;

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

  private static final boolean SELFTEST = //
      !System.getProperty("com.kohlschutter.selftest", "").isEmpty();

  private static final Lazy<@Nullable String> VM_FLAGS = Lazy.of(
      ExecutionEnvironmentUtil::obtainVMFlags);

  private static final Lazy<Boolean> EPSILON_GC = Lazy.of(ExecutionEnvironmentUtil::initEpsilonGC);

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

  /**
   * Checks whether the code is being run in "selftest" mode.
   * <p>
   * This is checked by the System property {@code com.kohlschutter.selftest}, which should be set
   * to the classname of the Selftest class (if a selftest was started before initializing this
   * class — the setting is cached).
   *
   * @return {@code true} if knowingly so.
   */
  public static boolean isSelftest() {
    return SELFTEST;
  }

  /**
   * Checks whether the code is being in in an environment that does not perform garbage collection
   * (e.g., Epsilon GC).
   *
   * @return {@code true} if knowingly so.
   */
  public static boolean isEpsilonGC() {
    return EPSILON_GC.get();
  }

  private static boolean initEpsilonGC() {
    String vmFlags = VM_FLAGS.get();
    return (vmFlags != null && vmFlags.contains("+UseEpsilonGC"));
  }

  private static @Nullable String obtainVMFlags() {
    try {
      MBeanServer server = ManagementFactory.getPlatformMBeanServer();
      ObjectName on = new ObjectName("com.sun.management:type=DiagnosticCommand");
      String result = (String) server.invoke(on, "vmFlags", new Object[] {null}, new String[] {
          String[].class.getName()});
      return result;
    } catch (Exception e) {
      // ignore
    }
    return null;
  }
}
