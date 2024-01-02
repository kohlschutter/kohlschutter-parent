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
