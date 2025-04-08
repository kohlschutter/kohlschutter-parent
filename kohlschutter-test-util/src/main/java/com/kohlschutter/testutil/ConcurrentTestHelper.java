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
package com.kohlschutter.testutil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Simplifies running some test code concurrently.
 * 
 * @author Christian Kohlschütter
 */
public final class ConcurrentTestHelper implements AutoCloseable {
  private volatile boolean keepRunning = true;
  private final ExecutorService jobs = Executors.newCachedThreadPool();
  private final List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());

  /**
   * Some job that can be submitted to this helper.
   * 
   * @author Christian Kohlschütter
   */
  @FunctionalInterface
  public interface CTHRunnableWithException {
    /**
     * Runs the job.
     * 
     * @param cth The helper this will be run on.
     * @throws Exception on error; these will be collected and thrown upon
     *           {@link ConcurrentTestHelper#close()}.
     */
    void run(ConcurrentTestHelper cth) throws Exception;
  }

  /**
   * Instantiates a new {@link ConcurrentTestHelper}; ideally from a try-with-resources block.
   */
  public ConcurrentTestHelper() {
  }

  /**
   * Submits a job to be executed once.
   * 
   * @param op The job.
   * @return The future.
   */
  public Future<?> submitOneOffJob(CTHRunnableWithException op) {
    return jobs.submit(() -> {
      try {
        op.run(ConcurrentTestHelper.this);
      } catch (Exception e) {
        e.printStackTrace();
        if (isKeepRunning()) {
          exceptions.add(e);
        }
      }
    });
  }

  /**
   * Submits a job to be executed in a loop, which is run until this instance is shut down.
   * 
   * @param op The job.
   * @return The future.
   */
  public Future<?> submitLoopingJob(CTHRunnableWithException op) {
    return jobs.submit(() -> {
      while (isKeepRunning()) {
        try {
          op.run(ConcurrentTestHelper.this);
        } catch (Exception e) {
          e.printStackTrace();
          if (isKeepRunning()) {
            exceptions.add(e);
          }
        }
      }
    });
  }

  /**
   * Returns {@code true} until shut down.
   * 
   * @return The state.
   */
  public boolean isKeepRunning() {
    return keepRunning;
  }

  /**
   * Stops the execution of jobs, but doesn't wait until they're finished.
   */
  public void stop() {
    keepRunning = false;
    jobs.shutdown();
  }

  /**
   * Stops the execution of jobs, and waits until they're finished or the given timeout expires.
   * 
   * @param timeout The timeout.
   * @param unit The timeout unit.
   * @throws InterruptedException if the timeout expired.
   */
  public void stopAndWait(long timeout, TimeUnit unit) throws InterruptedException {
    stop();
    jobs.awaitTermination(timeout, unit);
  }

  /**
   * Closes this instance; usually via try-with-resources. Gives running jobs some time to finish.
   */
  @Override
  public void close() throws Exception {
    try {
      stopAndWait(10, TimeUnit.SECONDS);
    } catch (RuntimeException | InterruptedException e) {
      for (Exception suppressed : exceptions) {
        e.addSuppressed(suppressed);
      }
      throw e;
    }

    if (!exceptions.isEmpty()) {
      Exception e = exceptions.remove(0);
      for (Exception suppressed : exceptions) {
        e.addSuppressed(suppressed);
      }
      throw e;
    }
  }

  /**
   * Awaits termination of this instance.
   * 
   * @param timeout The timeout.
   * @param unit The timeout unit.
   * @throws InterruptedException if the timeout expired.
   */
  public void awaitTermination(int timeout, TimeUnit unit) throws InterruptedException {
    jobs.awaitTermination(timeout, unit);
  }
}
