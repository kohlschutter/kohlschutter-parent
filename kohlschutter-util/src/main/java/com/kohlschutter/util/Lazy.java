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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import com.kohlschutter.annotations.compiletime.SuppressFBWarnings;

/**
 * Wrapper for a lazy-initialized object.
 *
 * @param <V> The object type.
 * @author Christian Kohlschütter
 */
@SuppressWarnings("PMD.ShortClassName")
public final class Lazy<V> {
  private final CompletableFuture<V> future = new CompletableFuture<V>();
  private final AtomicBoolean supplied = new AtomicBoolean();
  private final Supplier<V> supplier;

  private Lazy(Supplier<V> supplier) {
    this.supplier = supplier;
  }

  /**
   * Creates a lazy-load wrapper, using the given supplier to supply the object upon the first call
   * to {@link #get()}.
   *
   * @param <V> The object type.
   * @param supplier The object supplier.
   * @return The wrapper instance.
   */
  @SuppressWarnings("PMD.ShortMethodName")
  public static <V> Lazy<V> of(Supplier<V> supplier) {
    return new Lazy<>(supplier);
  }

  /**
   * Returns the object. If this is the first call, the object is retrieved from the configured
   * supplier, and transitions this instance to a completed satate.
   *
   * @return The object.
   */
  public V get() {
    if (!future.isDone() && supplied.compareAndSet(false, true)) {
      future.complete(supplier.get());
    }
    try {
      return future.get();
    } catch (InterruptedException | ExecutionException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * If not already completed, sets the value returned by {@link #get()} and related methods to the
   * given value, side-stepping the value that would be retrieved from the supplier.
   *
   * @param value the result value
   * @return {@code true} if this invocation caused this instance to transition to a completed
   *         state, else {@code false}
   */
  public boolean complete(V value) {
    supplied.set(true); // NOTE: We do not compareAndSet here to allow #set from within supplier.get
    return future.complete(value);
  }

  @SuppressWarnings("null")
  @Override
  @SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
  public String toString() {
    return super.toString() + "[supplied=" + supplied + "; value=" + future.getNow(null) + "]";
  }
}
