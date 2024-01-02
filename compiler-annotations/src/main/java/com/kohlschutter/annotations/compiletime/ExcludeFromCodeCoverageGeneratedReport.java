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
package com.kohlschutter.annotations.compiletime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a type or method that should be excluded from code-coverage calculations.
 *
 * Implementation note: Jacoco recognizes all annotations that contain the string "Generated",
 * therefore this class should not be renamed.
 *
 * @author Christian Kohlschütter
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.CONSTRUCTOR})
public @interface ExcludeFromCodeCoverageGeneratedReport {
  /**
   * A human-readable explanation why it was excluded.
   *
   * As a convention use the following values for certain scenarios:
   *
   * <ul>
   * <li>{@code unreachable} for clearly unreachable code (e.g., a private constructor in a static
   * helper class) and methods that are assumed but not guaranteed to never be called (e.g.,
   * implementations of an abstract/interface method that are expected to never be called due to
   * implementation specifics)</li>
   * <li>{@code exception unreachable} for methods that catch an exception that is never thrown
   * (e.g., {@link CloneNotSupportedException})</li>
   * <li>{@code jacoco bug} for scenarios where JaCoCo is clearly wrong
   * </ul>
   *
   * @return The reason.
   */
  String reason() default "";
}
