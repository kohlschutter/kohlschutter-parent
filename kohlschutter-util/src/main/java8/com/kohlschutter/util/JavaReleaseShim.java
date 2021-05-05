package com.kohlschutter.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

final class JavaReleaseShim {
  private JavaReleaseShim() {
    throw new IllegalStateException("No instances");
  }

  static byte[] readAllBytes(InputStream in) throws IOException {
    return IOUtil.readAllBytesNaively(in);
  }

  static long getPid() {
    return ProcessUtilVintage.getPid();
  }

  static String getCommandline() {
    return null;
  }
}
