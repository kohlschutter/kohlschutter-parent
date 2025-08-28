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

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.kohlschutter.annotations.compiletime.SuppressFBWarnings;

/**
 * Helper class related to Garbage Collection, mostly for testing purposes.
 *
 * @author Christian Kohlschütter
 * @see ExecutionEnvironmentUtil#isEpsilonGC()
 */
public final class UnsafeGCUtil {
  private UnsafeGCUtil() {
  }

  /**
   * Adds significant pressure to the garbage collector by exponentially building up a very large
   * string via {@link StringBuilder} from a daemon thread; OutOfMemoryError etc. may occur, which
   * is logged via {@link Throwable#printStackTrace()}.
   * <p>
   * The call immediately returns, and the operation continues in a daemon thread until {@code done}
   * yields {@code true}.
   * <p>
   * Note that this call may affect other code running in the JVM, due to the possibility of
   * {@link OutOfMemoryError} being thrown elsewhere.
   *
   * @param done When this supplier returns {@code true},
   */
  public static void addBrutalGCPressure(Supplier<Boolean> done) {
    addBrutalGCPressure(done, Throwable::printStackTrace);
  }

  /**
   * Calls {@link Runtime#gc()}.
   */
  @SuppressWarnings("PMD.DoNotCallGarbageCollectionExplicitly")
  @SuppressFBWarnings("DM_GC")
  public static void askforExplitGC() {
    Runtime.getRuntime().gc();
  }

  /**
   * Adds significant pressure to the garbage collector by exponentially building up a very large
   * string via {@link StringBuilder} from a daemon thread; OutOfMemoryError etc. may occur, which
   * is handled via the given consumer.
   * <p>
   * The call immediately returns, and the operation continues in a daemon thread until {@code done}
   * yields {@code true}.
   * <p>
   * Note that this call may affect other code running in the JVM, due to the possibility of
   * {@link OutOfMemoryError} being thrown elsewhere.
   *
   * @param done When this supplier returns {@code true},
   * @param oomeConsumer Consumes a potential {@link OutOfMemoryError} or other throwable, or
   *          {@code null} if none occurred or thrown elsewhere.
   */
  public static void addBrutalGCPressure(Supplier<Boolean> done, Consumer<Throwable> oomeConsumer) {
    Thread t;
    t = new Thread(() -> {
      try {
        StringBuilder sb = new StringBuilder("bigboombaddaboom");
        int count = 0;
        while (!Boolean.TRUE.equals(done.get())) {
          try {
            sb.append(sb);
          } catch (OutOfMemoryError e) {
            sb = new StringBuilder("bigboombaddaboom");
          }
          // Only ask for garbage collection every 4th append; makes things a bit faster
          // done
          if (++count == 4) {
            askforExplitGC();
            count = 0;
          }
        }
        Objects.requireNonNull(sb);
      } catch (Throwable e) { // NOPMD
        // mostly OutOfMemoryError, but could also be a RutimeException from the supplier, etc.
        oomeConsumer.accept(e);
        return;
      }
      try {
        oomeConsumer.accept(null); // no error, or thrown elsewhere
      } catch (Exception e) {
        // ignore
      }
    });
    t.setDaemon(true);
    t.start();
  }
}
