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
package com.kohlschutter.testutil;

import org.opentest4j.TestAbortedException;

/**
 * A {@link TestAbortedException} that should not be regarded a test failure.
 *
 * @author Christian Kohlschütter
 */
public final class TestAbortedNotAnIssueException extends TestAbortedException {
  private static final long serialVersionUID = 1L;

  /**
   * Creates a new {@link TestAbortedNotAnIssueException}.
   */
  public TestAbortedNotAnIssueException() {
    super();
  }

  /**
   * Creates a new {@link TestAbortedNotAnIssueException}.
   *
   * @param message The message.
   * @param cause The cause.
   */
  public TestAbortedNotAnIssueException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Creates a new {@link TestAbortedNotAnIssueException}.
   *
   * @param message The message.
   */
  public TestAbortedNotAnIssueException(String message) {
    super(message);
  }
}
