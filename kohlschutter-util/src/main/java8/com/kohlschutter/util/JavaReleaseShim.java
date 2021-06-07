package com.kohlschutter.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

  static String getJavaCommand() {
    return null;
  }

  static String[] getJavaCommandArguments() {
    return null;
  }

  static long transferAllBytes(InputStream in, OutputStream out) throws IOException {
    return IOUtil.transferAllBytesNaively(in, out);
  }
}
