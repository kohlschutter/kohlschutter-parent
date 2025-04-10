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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.kohlschutter.annotations.compiletime.ExcludeFromCodeCoverageGeneratedReport;

/**
 * Some I/O-related helper methods.
 *
 * @author Christian Kohlschütter
 */
public final class IOUtil {
  @ExcludeFromCodeCoverageGeneratedReport(reason = "unreachable")
  private IOUtil() {
    throw new IllegalStateException("No instances");
  }

  /**
   * Reads all bytes from the given {@link InputStream}.
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
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    transferAllBytesNaively(in, bos);
    return bos.toByteArray();
  }

  /**
   * Transfers all remaining bytes from the given {@link InputStream} to the given
   * {@link OutputStream}.
   *
   * With Java 9 and above, {@code InputStream#transferTo()} is used. On older Java versions, the
   * call is implemented like {@link #transferAllBytesNaively(InputStream, OutputStream)}.
   *
   * @param in The source.
   * @param out The target.
   * @return The number of bytes transferred.
   * @throws IOException on error.
   */
  public static long transferAllBytes(InputStream in, OutputStream out) throws IOException {
    return JavaReleaseShim.transferAllBytes(in, out);
  }

  /**
   * Reads all bytes from the given {@link InputStream} — naively — by reading into a temporary
   * byte-array buffer, which is then written to the given {@link OutputStream}.
   *
   * @param in The source.
   * @param out The target.
   * @return The number of bytes transferred.
   * @throws IOException on error.
   */
  public static long transferAllBytesNaively(InputStream in, OutputStream out) throws IOException {
    long total = 0;
    byte[] buf = new byte[4096];
    int read;
    while ((read = in.read(buf)) != -1) {
      total += read;
      out.write(buf, 0, read);
    }
    return total;
  }

  /**
   * Deletes a file.
   *
   * If the file could not be deleted (but exists), an {@link IOException} is thrown.
   *
   * @param f The file to delete.
   * @throws IOException on error.
   */
  public static void delete(File f) throws IOException {
    if (!f.delete() && f.exists()) {
      throw new IOException("Could not delete file: " + f);
    }
  }
}
