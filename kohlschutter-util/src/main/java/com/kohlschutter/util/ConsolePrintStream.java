package com.kohlschutter.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * A {@code System.out} wrapper that can knows a little bit about the system console.
 * 
 * @author Christian Kohlschütter
 */
public class ConsolePrintStream extends PrintStream {
  private static final byte[] NEWLINE_BYTES = System.lineSeparator().getBytes(Charset
      .defaultCharset());
  private static final int NEWLINE = '\n';
  private static final boolean NO_CONSOLE = System.console() == null;

  private final PrintStream out;
  private final ConsoleFilterOut cfo;
  private boolean closed = false;

  private ConsolePrintStream() throws UnsupportedEncodingException {
    this(new ConsoleFilterOut(System.out));
  }

  private ConsolePrintStream(ConsoleFilterOut cfo) throws UnsupportedEncodingException {
    super(cfo, false, Charset.defaultCharset().name());
    this.cfo = cfo;
    this.out = System.out;
    System.setOut(this);
  }

  public static ConsolePrintStream wrapSystemOut() {
    synchronized (System.class) {
      PrintStream out = System.out;
      if (out instanceof ConsolePrintStream) {
        return (ConsolePrintStream) out;
      } else {
        try {
          return new ConsolePrintStream();
        } catch (UnsupportedEncodingException e) {
          throw new IllegalStateException(e);
        }
      }
    }
  }

  private static final class ConsoleFilterOut extends FilterOutputStream {
    private int numBytes = 0;
    private int lastNewline = 0;
    private int markedPosition = 0;
    private int lastUpdate = 0;
    private int lastByte = 0;

    private ConsoleFilterOut(PrintStream out) {
      super(out);
    }

    @Override
    public synchronized void write(int b) throws IOException {
      boolean newline = (b == NEWLINE);
      if (!newline && lastUpdate == numBytes && numBytes > 0) {
        lastUpdate = 0;
        write(NEWLINE_BYTES);
      }

      super.write(b);
      lastByte = b;

      int count = ++numBytes;
      if (newline) {
        lastNewline = count;
      }
    }

    @Override
    public synchronized void write(byte[] b, int off, int len) throws IOException {
      if (len == 0) {
        return;
      }
      if (b[off] != NEWLINE && lastUpdate == numBytes && numBytes > 0) {
        lastUpdate = 0;
        write(NEWLINE_BYTES);
      }

      super.write(b, off, len);
      lastByte = b[off + len - 1];
      int countPre = numBytes;
      numBytes += len;

      for (int i = len - 1; i >= off; i--) {
        if (b[i] == NEWLINE) {
          lastNewline = (countPre + (i - off + 1));
        }
      }
    }

    synchronized void markPosition() {
      markedPosition = numBytes;
    }

    synchronized boolean hasNewlineSinceMark() {
      return lastNewline > markedPosition;
    }

    synchronized boolean hasOutputSinceMark() {
      return numBytes > markedPosition;
    }
  }

  /**
   * Marks the current position in the stream (which by default is the beginning of the stream).
   *
   * @see #hasOutputSinceMark()
   * @see #hasNewlineSinceMark()
   * @see #update(String)
   */
  public void markPosition() {
    cfo.markPosition();
  }

  /**
   * Checks if a newline character was written since the last call to {@link #markPosition()}.
   * 
   * @return {@code true} if a newline character was found.
   */
  public boolean hasNewlineSinceMark() {
    return cfo.hasNewlineSinceMark();
  }

  /**
   * Checks if any output was written since the last call to {@link #markPosition()}.
   * 
   * @return {@code true} if a newline character was found.
   */
  public boolean hasOutputSinceMark() {
    return cfo.hasOutputSinceMark();
  }

  /**
   * Prints a string, not advancing to a newline unless necessary, and clearing any previous output
   * written with {@link #update(String)} via backspace characters.
   * 
   * This allows for succinct progress updates that don't pollute the console output.
   * 
   * @param s The string to print.
   */
  public void update(String s) {
    synchronized (cfo) {
      cfo.lastUpdate = 0;
      if (cfo.lastByte != NEWLINE) {
        if (NO_CONSOLE || hasNewlineSinceMark()) {
          println();
        } else {
          clearLine();
        }
      }
      print(s);
      flush();
      markPosition();
      cfo.lastUpdate = cfo.numBytes;
    }
  }

  /**
   * Like {@link #update(String)}, but adding a newline after the output.
   * 
   * @param s The string to print.
   */
  public void updateln(String s) {
    synchronized (cfo) {
      update(s);
      println();
    }
  }

  private void clearLine() {
    synchronized (cfo) {
      flush();

      int numBytesSinceNewline = (cfo.numBytes - cfo.lastNewline);
      if (numBytesSinceNewline == 0) {
        return;
      }
      StringBuilder sb = new StringBuilder(numBytesSinceNewline * 3 + 1);

      // overwrite anything written so far in the last line without ANSI escape sequences
      for (int i = 0; i < numBytesSinceNewline; i++) {
        sb.append("\b \b");
      }

      // triggers a newline in Eclipse when "Interpret ASCII control characters" is off
      sb.append('\r');

      out.print(sb);
      out.flush();
      cfo.lastNewline = cfo.numBytes;
    }
  }

  /**
   * Flushes all pending data to the previous System.out, restoring (but not closing) it.
   */
  @Override
  public void close() {
    synchronized (cfo) {
      flush();
      if (closed) {
        return;
      }
      closed = true;
      System.setOut(out);
    }
  }
}
