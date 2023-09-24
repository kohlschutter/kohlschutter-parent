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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * A {@code System.out} wrapper that knows a little bit about the system console.
 *
 * @author Christian Kohlschütter
 */
public final class ConsolePrintStream extends PrintStream {
  private static final byte[] NEWLINE_BYTES = System.lineSeparator().getBytes(Charset
      .defaultCharset());
  private static final int NEWLINE = '\n';
  private static final boolean NO_CONSOLE; // if true, then we don't update lines
  private static final boolean CLEAR_LINE_FIRST;

  private final PrintStream printStream;
  private final ConsoleFilterOut cfo;
  private boolean closed = false;

  static {
    String forceConsole = System.getProperty("com.kohlschutter.util.console", "");
    if (!forceConsole.isEmpty()) {
      NO_CONSOLE = !Boolean.valueOf(forceConsole);
    } else if (System.console() != null) {
      if ("z/OS".equals(System.getProperty("os.name"))) {
        NO_CONSOLE = true;
      } else {
        NO_CONSOLE = false;
      }
    } else {
      String serviceName = System.getenv("XPC_SERVICE_NAME");
      if (serviceName != null && serviceName.endsWith(".eclipse")) {
        // running in Eclipse, assume console is on
        NO_CONSOLE = false;
      } else {
        NO_CONSOLE = true;
      }
    }

    boolean isWindows = "\\".equals(System.getProperty("file.separator", ""));

    String clf = System.getProperty("com.kohlschutter.util.console.clear-line-first", "");
    if (!clf.isEmpty()) {
      CLEAR_LINE_FIRST = Boolean.valueOf(clf);
    } else {
      CLEAR_LINE_FIRST = (isWindows);
    }
  }

  private ConsolePrintStream(PrintStream out) throws UnsupportedEncodingException {
    this(new ConsoleFilterOut(out));
  }

  private ConsolePrintStream(ConsoleFilterOut cfo) throws UnsupportedEncodingException {
    super(cfo, false, Charset.defaultCharset().name());
    this.cfo = cfo;
    this.printStream = cfo.getOutputStream();
  }

  /**
   * Wraps {@code System.out} as a {@link ConsolePrintStream}.
   *
   * {@code System.out} is automatically set to this stream.
   *
   * @return The wrapped stream.
   */
  public static synchronized ConsolePrintStream wrapSystemOut() {
    PrintStream out = System.out;
    ConsolePrintStream cps = wrapPrintStream(out);

    System.setOut(cps);

    return cps;
  }

  /**
   * Wraps the given PrintStream as a {@link ConsolePrintStream}.
   *
   * {@code System.out} is NOT automatically set to this stream, so you may need to do this
   * yourself.
   *
   * @param out The {@link PrintStream} that should be wrapped.
   *
   * @return The wrapped stream.
   */
  public static ConsolePrintStream wrapPrintStream(PrintStream out) {
    if (out instanceof ConsolePrintStream) {
      return (ConsolePrintStream) out;
    } else {
      try {
        return new ConsolePrintStream(out);
      } catch (UnsupportedEncodingException e) {
        throw new IllegalStateException(e);
      }
    }
  }

  private static final class ConsoleFilterOut extends FilterOutputStream {
    private int numBytes = 0;
    private int lastNewline = 0;
    private int markedPosition = 0;
    private int lastUpdate = 0;
    private int lastByte = 0;

    ConsoleFilterOut(PrintStream out) {
      super(out);
    }

    PrintStream getOutputStream() {
      return (PrintStream) out;
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
      flush();
      cfo.lastUpdate = 0;
      if (cfo.lastByte != NEWLINE) {
        if (NO_CONSOLE || hasNewlineSinceMark()) {
          println();
          print(s);
        } else {
          if (CLEAR_LINE_FIRST) {
            clearLine();
            print(s);
          } else {
            int numBytesSinceNewline = (cfo.numBytes - cfo.lastNewline);
            int toClear = numBytesSinceNewline - s.length();

            print('\r');
            cfo.lastNewline = cfo.numBytes;
            print(s);
            clearToEndOfLine(toClear);
          }
        }
      } else {
        print(s);
      }
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

      printStream.print(sb);
      printStream.flush();
      cfo.lastNewline = cfo.numBytes;
    }
  }

  private void clearToEndOfLine(int toClear) {
    if (toClear <= 0) {
      return;
    }
    synchronized (cfo) {
      StringBuilder sb = new StringBuilder(toClear * 2);
      for (int i = 0; i < toClear; i++) {
        sb.append(' ');
      }
      for (int i = 0; i < toClear; i++) {
        sb.append('\b');
      }

      printStream.print(sb);
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
      System.setOut(printStream);
    }
  }

  /**
   * Returns {@code true} if a system console is attached (which enables {@link #update(String)},
   * for example), {@code false} when piping to another file or process, for example.
   *
   * The behavior can be overridden by setting the system property
   * {@code com.kohlschutter.util.console} to either {@code true} or {@code false}.
   *
   * @return {@code true} if a system console is attached.
   */
  public static boolean hasConsole() {
    return !NO_CONSOLE;
  }

  /**
   * Returns {@code true} if {@link #update(String)} clears the line first before writing the new
   * content instead of printing a carriage return, followed by the new text, and then clearing the
   * remaining characters.
   *
   * The behavior can be overridden by setting the system property
   * {@code com.kohlschutter.util.console.clear-line-first} to either {@code true} or {@code false}.
   *
   * @return {@code true} if "clear-first" is enabled.
   */
  public static boolean isClearLineFirst() {
    return CLEAR_LINE_FIRST;
  }
}
