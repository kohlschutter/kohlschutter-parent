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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Wrapper for a lazy-initialized object.
 *
 * @param <V> The object type.
 * @author Christian Kohlschütter
 */
@SuppressWarnings("PMD.ShortClassName")
public interface Lazy<V> extends Supplier<V>, Consumer<V> {
  /**
   * A {@link Lazy} implementation that can be set via {@link #complete(Object)}.
   *
   * @param <V> The object type.
   * @author Christian Kohlschütter
   */
  class BaseImpl<V> implements Lazy<V> {
    final CompletableFuture<V> future = new CompletableFuture<V>();
    final AtomicBoolean supplied = new AtomicBoolean();

    private BaseImpl() {
    }

    @Override
    public V get() {
      @SuppressWarnings("null")
      V val = future.getNow(null);
      future.complete(val);
      return val;
    }

    @Override
    public boolean complete(V value) {
      supplied.set(true); // NOTE: We do not compareAndSet here to allow #set from within
                          // supplier.get
      return future.complete(value);
    }

    @SuppressWarnings("null")
    @Override
    public String toString() {
      return super.toString() + "[supplied=" + supplied + "; value=" + future.getNow(null) + "]";
    }
  }

  /**
   * A {@link Lazy} implementation for values supplied by a {@link Supplier}.
   *
   * @param <V> The object type.
   * @author Christian Kohlschütter
   */
  final class FromSupplier<V> extends BaseImpl<V> {
    private final Supplier<V> supplier;

    private FromSupplier(Supplier<V> supplier) {
      super();
      this.supplier = supplier;
    }

    @Override
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
  }

  /**
   * A {@link Lazy} implementation for values supplied immediately.
   *
   * @param <V> The object type.
   * @author Christian Kohlschütter
   */
  final class WithSupplied<V> implements Lazy<V> {
    private final V supplied;

    private WithSupplied(V supplied) {
      this.supplied = supplied;
    }

    @Override
    public V get() {
      return supplied;
    }

    @Override
    public boolean complete(V value) {
      return false;
    }
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
  static <V> Lazy<V> of(Supplier<V> supplier) {
    return new FromSupplier<>(supplier);
  }

  /**
   * Creates a lazy-load wrapper, only allowing setting the value via {@link #complete(Object)}.
   *
   * @param <V> The object type.
   * @return The wrapper instance.
   */
  @SuppressWarnings("PMD.ShortMethodName")
  static <V> Lazy<V> of() {
    return new BaseImpl<>();
  }

  /**
   * Creates a {@link Lazy} wrapper, using the given value, which is regarded as instantly supplied.
   *
   * @param <V> The object type.
   * @param supplied The supplied object.
   * @return The wrapper instance.
   */
  static <V> Lazy<V> ofSupplied(V supplied) {
    return new WithSupplied<>(supplied);
  }

  /**
   * Returns the object. If this is the first call, the object is retrieved from the configured
   * supplier, and this instance transitions to a completed state.
   *
   * @return The object.
   */
  @Override
  V get();

  /**
   * If not already completed, sets the value returned by {@link #get()} and related methods to the
   * given value, side-stepping the value that would be retrieved through other means. If already
   * completed, nothing is changed and {@code false} is returned.
   *
   * @param value the result value
   * @return {@code true} if this invocation caused this instance to transition to a completed
   *         state, else {@code false}
   */
  boolean complete(V value);

  /**
   * Calls {@link #complete(Object)}, disregarding the return value.
   */
  @Override
  default void accept(V value) {
    complete(value);
  }
}
