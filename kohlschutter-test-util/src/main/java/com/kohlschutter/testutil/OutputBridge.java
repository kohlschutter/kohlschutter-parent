/*
 * kohlschutter-parent
 *
 * Copyright 2009-2024 Christian Kohlschütter
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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Forwards output (stdout/stderr) from another process.
 *
 * @author Christian Kohlschütter
 */
public class OutputBridge implements Runnable, Closeable {
  private final byte[] prefix;
  private final InputStream in;
  private final OutputStream out;
  private int numRead = 0;
  private boolean closed = false;

  private ByteArrayOutputStream bos;

  /**
   * Specifies valid output streams for a process.
   *
   * @author Christian Kohlschütter
   */
  public enum ProcessStream {
    /** Standard output (stdout). */
    STDOUT,

    /** Error output (stderr). */
    STDERR
  }

  /**
   * Creates a new {@link OutputBridge} for the output from the given process.
   *
   * @param process The process to bridge output from.
   * @param output The stream to bridge.
   */
  public OutputBridge(Process process, ProcessStream output) {
    this(process, output, (byte[]) null);
  }

  /**
   * Creates a new {@link OutputBridge} for the output from the given process, optionally prefixing
   * each output line with the given prefix.
   *
   * @param process The process to bridge output from.
   * @param output The stream to bridge.
   * @param prefix The prefix, or {@code null}.
   */
  public OutputBridge(Process process, ProcessStream output, String prefix) {
    this(process, output, (prefix == null || prefix.isEmpty() ? null : prefix.getBytes(Charset
        .defaultCharset())));
  }

  private OutputBridge(Process process, ProcessStream output, byte[] prefix) {
    this.prefix = prefix;
    switch (output) {
      case STDOUT:
        this.in = new BufferedInputStream(process.getInputStream());
        this.out = System.out;
        break;
      case STDERR:
        this.in = new BufferedInputStream(process.getErrorStream());
        this.out = System.err;
        break;
      default:
        throw new IllegalArgumentException();
    }
  }

  @SuppressWarnings({"NoFinalizer" /* checkstyle */, "PMD.EmptyFinalizer"})
  @Deprecated
  @Override
  protected final void finalize() {
  }

  @Override
  public final void run() {
    bos = new ByteArrayOutputStream();
    if (prefix != null) {
      try {
        bos.write(prefix);
      } catch (IOException e) {
        // not thrown
      }
    }
    try {
      int x;
      while (!closed && (x = in.read()) != -1) {
        ++numRead;
        bos.write(x);
        if (x == '\n') {
          flush();
        }
      }
      flush();
      bos.close();
    } catch (IOException e) {
      // ignored
    }
  }

  private void flush() throws IOException {
    if (bos.size() == (prefix == null ? 0 : prefix.length)) {
      return;
    }
    bos.writeTo(out);
    bos.reset();

    if (prefix != null) {
      bos.write(prefix);
    }
  }

  /**
   * Checks if there was some output.
   *
   * @return {@code true} if there was some output.
   */
  public final boolean hasOutput() {
    return numBytesRead() > 0;
  }

  /**
   * Returns the number of bytes received.
   *
   * @return The number of bytes received.
   */
  public final int numBytesRead() {
    return numRead;
  }

  @Override
  public void close() {
    closed = true;
  }
}
