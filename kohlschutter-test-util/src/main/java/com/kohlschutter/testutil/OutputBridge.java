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

  public enum ProcessStream {
    STDOUT, STDERR
  }

  public OutputBridge(Process process, ProcessStream output) {
    this(process, output, (byte[]) null);
  }

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

  public final boolean hasOutput() {
    return numBytesRead() > 0;
  }

  public final int numBytesRead() {
    return numRead;
  }

  @Override
  public void close() {
    closed = true;
  }
}
