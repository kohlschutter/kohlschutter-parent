package com.kohlschutter.util;

import java.io.IOException;
import java.io.InputStream;

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
}
