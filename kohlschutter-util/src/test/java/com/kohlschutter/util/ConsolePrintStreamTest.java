package com.kohlschutter.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.UUID;

import org.junit.jupiter.api.Test;

public class ConsolePrintStreamTest {

  @Test
  public void testNewlineMarker() throws Exception {
    synchronized (System.class) {
      PrintStream outOld = System.out;
      try {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bos, true, Charset.defaultCharset().name()));
        try (ConsolePrintStream cpw = ConsolePrintStream.wrapSystemOut()) {
          assertFalse(cpw.hasNewlineSinceMark());
          cpw.println();
          assertTrue(cpw.hasNewlineSinceMark());
          cpw.markPosition();
          assertFalse(cpw.hasNewlineSinceMark());
          cpw.print('1');
          assertFalse(cpw.hasNewlineSinceMark());
          cpw.print('\r');
          assertFalse(cpw.hasNewlineSinceMark());
          cpw.println("Hello World");
          assertTrue(cpw.hasNewlineSinceMark());
          cpw.markPosition();
          assertFalse(cpw.hasNewlineSinceMark());
          cpw.print(new char[] {'\n'});
          assertTrue(cpw.hasNewlineSinceMark());
        }
      } finally {
        System.setOut(outOld);
      }
    }
  }

  @Test
  public void testClearLine() throws Exception {
    try (ConsolePrintStream cpw = ConsolePrintStream.wrapSystemOut()) {
      cpw.updateln("Hello");
      cpw.update("World");
      cpw.updateln("Java");
      for (int i = 0; i < 5; i++) {
        String s = UUID.randomUUID().toString();
        cpw.update(s.substring(0, s.length() - i));

        if (i == 3) {
          System.out.println("Some interrupting text");
        }

        Thread.sleep(100);
      }
      cpw.println();
    }
  }
}
