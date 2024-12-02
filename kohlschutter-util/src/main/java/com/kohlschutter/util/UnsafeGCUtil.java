package com.kohlschutter.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

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
      Runtime rt = Runtime.getRuntime();
      try {
        StringBuilder sb = new StringBuilder("bigboombaddaboom");
        int count = 0;
        while (!Boolean.TRUE.equals(done.get())) {
          sb.append(sb);
          // Only ask for garbage collection every 4th append; makes things a bit faster
          // done
          if (++count == 4) {
            rt.gc();
            count = 0;
          }
        }
      } catch (Throwable e) { // NOPMD
        // mostly OutOfMemoryError, but could also be a RutimeException from the supplier, etc.
        oomeConsumer.accept(e);
        return;
      }
      oomeConsumer.accept(null); // no error, or thrown elsewhere
    });
    t.setDaemon(true);
    t.start();
  }
}
