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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Marks tests to be executed only if ForkVM is supported.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(ForkedVMExecutionCondition.class)
public @interface ForkedVMRequirement {
  /**
   * Requires the availability ({@code true}) or absence ({@code false}) of support for
   * {@link ForkedVM}.
   *
   * @return {@code true} if ForkedVM support should be available, {@code false} if ForkedVM support
   *         should not be available.
   */
  boolean forkSupported();

  /**
   * The error message to show if the requirement does not hold.
   *
   * @return The error message.
   */
  String message() default "";
}
