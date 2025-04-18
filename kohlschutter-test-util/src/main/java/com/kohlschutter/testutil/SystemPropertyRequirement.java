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
 * Marks tests to be executed only if the specified properties are set to a specific value (or not
 * set).
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(SystemPropertyExecutionCondition.class)
public @interface SystemPropertyRequirement {
  /**
   * The name of the system property to check.
   *
   * @return The name of the system property.
   */
  String property();

  /**
   * The expected value of the system property.
   *
   * @return The expected value of the system property.
   */
  String value();

  /**
   * The error message to show if the requirement does not hold.
   *
   * @return The error message.
   */
  String message() default "";
}
