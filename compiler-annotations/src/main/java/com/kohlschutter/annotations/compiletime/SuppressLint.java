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
package com.kohlschutter.annotations.compiletime;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marker class indicating that some warnings of Facebook's "infer" linter should be suppressed.
 * <p>
 * References:
 * <ul>
 * <li><a href="https://fbinfer.com/docs/next/all-issue-types/">Issue types</a></li>
 * </ul>
 *
 * @author Christian Kohlschütter
 */
@Retention(RetentionPolicy.CLASS)
public @interface SuppressLint {
  /**
   * The warnings that should be suppressed.
   *
   * @return A list of warnings.
   */
  String[] value() default {};
}
