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
package com.kohlschutter.testutil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

/**
 * Helper class to control logging of certain messages. For testing only.
 *
 * @author Christian Kohlschütter
 */
public final class LoggerUtil {
  private LoggerUtil() {
    throw new IllegalStateException("No instances");
  }

  /**
   * Overrides the default logging configuration with the logging.properties file specified at the
   * given path, relative to the given class.
   * <p>
   * The override is not taken into account when the system property
   * {@code java.util.logging.config.file} was set.
   *
   * @param classRef The class reference.
   * @param loggingPropertiesFilePath The relative path (e.g., {@code logging.properties}).
   */
  public static void overrideDefaultConfiguration(Class<?> classRef,
      String loggingPropertiesFilePath) {
    String logConfig = System.getProperty("java.util.logging.config.file", "");
    if (!logConfig.isEmpty()) {
      // do not override this override
      return;
    }

    try (InputStream in = classRef.getResource(loggingPropertiesFilePath).openStream()) {
      LogManager.getLogManager().readConfiguration(in);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Reverts the logging configuration to the default.
   */
  public static void revertToDefaultConfiguration() {
    try {
      LogManager.getLogManager().readConfiguration();
    } catch (FileNotFoundException e) {
      try {
        overrideDefaultConfiguration(LoggerUtil.class, "logging.properties");
      } catch (SecurityException e1) {
        throw new IllegalStateException(e); // throw original exception cause
      }
    } catch (SecurityException | IOException e) {
      throw new IllegalStateException(e);
    }
  }
}
