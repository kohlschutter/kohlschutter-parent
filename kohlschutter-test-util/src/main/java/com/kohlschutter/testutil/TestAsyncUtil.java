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
package com.kohlschutter.testutil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Helper class to run code asynchronously during unit tests.
 *
 * @author Christian Kohlschütter
 */
public final class TestAsyncUtil {
  private TestAsyncUtil() {
    throw new IllegalStateException("No instances");
  }

  /**
   * Run the following code asynchronously (fire and forget).
   *
   * @param run The code to run.
   */
  public static void runAsync(Runnable run) {
    trackFuture(CompletableFuture.runAsync(run));
  }

  /**
   * Run the following code asynchronously (fire and forget), using the given executor.
   *
   * @param executor The executor to use.
   * @param run The code to run.
   */
  public static void runAsync(Executor executor, Runnable run) {
    executor.execute(run);
  }

  /**
   * Run the following code asynchronously, returning a {@link Future}.
   *
   * @param run The code to run.
   * @return The future.
   */
  public static Future<Void> supplyAsync(Runnable run) {
    CompletableFuture<Void> future = CompletableFuture.runAsync(run);
    trackFuture(future);
    return future;
  }

  /**
   * Run the following code, provided as a supplier. asynchronously, returning a {@link Future}.
   *
   * @param run The supplier to call when run asynchronously.
   * @param <U> The return type.
   * @return The future.
   */
  public static <U> Future<U> supplyAsync(Supplier<U> run) {
    CompletableFuture<U> future = CompletableFuture.supplyAsync(run);
    trackFuture(future);
    return future;
  }

  /**
   * Run the following code asynchronously (fire and forget), after the given delay.
   *
   * @param delay The delay value.
   * @param unit The time unit of the delay.
   * @param run The code to run.
   */
  public static void runAsyncDelayed(long delay, TimeUnit unit, Runnable run) {
    runAsyncDelayed(Executors.newSingleThreadScheduledExecutor(), delay, unit, run);
  }

  /**
   * Run the following code asynchronously (fire and forget), after the given delay, using the given
   * {@link ScheduledExecutorService}.
   *
   * @param ses The service to use.
   * @param delay The delay value.
   * @param unit The time unit of the delay.
   * @param run The code to run.
   */
  public static void runAsyncDelayed(ScheduledExecutorService ses, long delay, TimeUnit unit,
      Runnable run) {
    trackFuture(ses.schedule(run, delay, unit));
  }

  /**
   * Tracks the given future.
   *
   * @param future The future.
   */
  public static void trackFuture(Future<?> future) {
    // Currently does nothing
  }
}
