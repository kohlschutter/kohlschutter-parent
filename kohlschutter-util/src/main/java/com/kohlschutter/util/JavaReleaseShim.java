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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The implementations in this class may be overridden for older Java versions.
 * 
 * see the corresponding class in src/main/java8, for example.
 */
final class JavaReleaseShim {
  private JavaReleaseShim() {
    throw new IllegalStateException("No instances");
  }

  static byte[] readAllBytes(InputStream in) throws IOException {
    return in.readAllBytes();
  }

  static long getPid() {
    return ProcessHandle.current().pid();
  }

  static String getJavaCommand() {
    return ProcessHandle.current().info().command().orElse(null);
  }

  static String[] getJavaCommandArguments() {
    return ProcessHandle.current().info().arguments().orElse(null);
  }

  static long transferAllBytes(InputStream in, OutputStream out) throws IOException {
    return in.transferTo(out);
  }
}
