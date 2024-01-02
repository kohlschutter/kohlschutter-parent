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

import java.util.Objects;

import org.opentest4j.TestAbortedException;

/**
 * A {@link TestAbortedException} that has a very important message that should be shown in an
 * "import messages" section.
 *
 * Depending on the "with issues" status, the test abortion may be counted as "test passed" or "test
 * passed with issues".
 *
 * @author Christian Kohlschütter
 */
public final class TestAbortedWithImportantMessageException extends TestAbortedException {
  private static final long serialVersionUID = 1L;

  /**
   * The message type.
   */
  public enum MessageType {
    /**
     * Test aborted, result would count as "with issues".
     */
    TEST_ABORTED_WITH_ISSUES, //

    /**
     * Test aborted but consider test passed.
     *
     * A message is included, which should be shown along with test module and class information.
     */
    TEST_ABORTED_INFORMATIONAL, //

    /**
     * Test aborted but consider test passed.
     *
     * A message is included, which should be shown without further information about test module
     * and class.
     */
    TEST_ABORTED_SHORT_INFORMATIONAL,

    /**
     * Test aborted, result would count as "with issues".
     *
     * A short message is included, which should be shown without further information about test
     * module and class.
     */
    TEST_ABORTED_SHORT_WITH_ISSUES;

    /**
     * Checks if this message type is considered "with issues".
     *
     * @return {@code true} if "with issues."
     */
    public boolean isWithIssues() {
      return this == TEST_ABORTED_WITH_ISSUES || this == TEST_ABORTED_SHORT_WITH_ISSUES;
    }

    /**
     * Checks if this message type requires that test information is included in the report.
     *
     * @return {@code true} if test information (module, class, method) should be included.
     */
    public boolean isIncludeTestInfo() {
      return this != TEST_ABORTED_SHORT_INFORMATIONAL && this != TEST_ABORTED_SHORT_WITH_ISSUES;
    }
  }

  /**
   * The message type.
   */
  private final MessageType type;

  /**
   * The summary message (could be identical to message).
   */
  private final String summaryMessage;

  /**
   * Creates a new {@link TestAbortedWithImportantMessageException} instance.
   *
   * @param message The message.
   * @param type The message type.
   * @param cause The optional cause.
   */
  public TestAbortedWithImportantMessageException(MessageType type, String message,
      Throwable cause) {
    this(type, message, message, cause);
  }

  /**
   * Creates a new {@link TestAbortedWithImportantMessageException} instance.
   *
   * @param message The message.
   * @param type The message type.
   * @param summaryMessage A message to show in a selftest summary, or {@code null}.
   * @param cause The optional cause.
   */
  public TestAbortedWithImportantMessageException(MessageType type, String message,
      String summaryMessage, Throwable cause) {
    super(message, cause);
    this.type = Objects.requireNonNull(type);
    this.summaryMessage = summaryMessage == null ? message : summaryMessage;
  }

  /**
   * Creates a new {@link TestAbortedWithImportantMessageException} instance.
   *
   * @param message The message.
   * @param type The message type.
   */
  public TestAbortedWithImportantMessageException(MessageType type, String message) {
    this(type, message, message);
  }

  /**
   * Creates a new {@link TestAbortedWithImportantMessageException} instance.
   *
   * @param type The message type.
   * @param message The message to show in the stack trace
   * @param summaryMessage A message to show in a selftest summary, or {@code null}.
   */
  public TestAbortedWithImportantMessageException(MessageType type, String message,
      String summaryMessage) {
    super(message);
    this.type = Objects.requireNonNull(type);
    this.summaryMessage = summaryMessage == null ? message : summaryMessage;
  }

  /**
   * Returns the message type.
   *
   * @return The type.
   */
  public MessageType messageType() {
    return type;
  }

  /**
   * Returns the summary message.
   *
   * @return The type.
   */
  public String getSummaryMessage() {
    return summaryMessage;
  }
}
