package com.kohlschutter.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class IOUtil {
  private IOUtil() {
    throw new IllegalStateException("No instances");
  }

  /**
   * Reads all bytes from the given InputStream.
   * 
   * With Java 9 and above, {@code InputStream#readAllBytes()} is used. On older Java versions, the
   * call is implemented like {@link #readAllBytesNaively(InputStream)}.
   * 
   * @param in The input stream.
   * @return The bytes.
   * @throws IOException on error.
   */
  public static byte[] readAllBytes(InputStream in) throws IOException {
    return JavaReleaseShim.readAllBytes(in);
  }

  /**
   * Reads all bytes from the given InputStream — naively — by reading into a temporary byte-array
   * buffer, which is then converted to a byte array.
   * 
   * @param in The input stream.
   * @return The bytes.
   * @throws IOException on error.
   */
  public static byte[] readAllBytesNaively(InputStream in) throws IOException {
    byte[] buf = new byte[4096];
    int read;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    while ((read = in.read(buf)) != -1) {
      bos.write(buf, 0, read);
    }
    return bos.toByteArray();
  }
}
