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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Marks tests to be executed only if the specified rules are applicable.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(ExecutionEnvironmentExecutionCondition.class)
public @interface ExecutionEnvironmentRequirement {

  /**
   * Determines execution for a given environment.
   */
  enum Rule {
    /**
     * Do not run test when executed from within the corresponding environment.
     */
    PROHIBITED,

    /**
     * Execution from within the corresponding environment is allowed but not required.
     */
    ALLOWED,

    /**
     * Execution from within the corresponding environment is required for the test to be run.
     */
    REQUIRED
  }

  /**
   * Controls whether the test should be run when executing from within Eclipse.
   *
   * @return The rule.
   */
  Rule eclipse() default Rule.ALLOWED;

  /**
   * Controls whether the test should be run when executing as user "root".
   *
   * @return The rule.
   */
  Rule root() default Rule.ALLOWED;

  /**
   * Controls whether the test should be run when executing in Windows.
   *
   * @return The rule.
   */
  Rule windows() default Rule.ALLOWED;

  /**
   * Controls whether the test should be run when running in "selftest" mode.
   *
   * @return The rule.
   */
  Rule selftest() default Rule.ALLOWED;

  /**
   * Controls whether the test should be run when running in an environment that does not perform
   * garbage collection (e.g., epsilon-GC).
   * 
   * @return The rule.
   */
  Rule epsilonGC() default Rule.ALLOWED;
}
