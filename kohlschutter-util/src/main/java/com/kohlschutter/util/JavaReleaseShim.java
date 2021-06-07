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
