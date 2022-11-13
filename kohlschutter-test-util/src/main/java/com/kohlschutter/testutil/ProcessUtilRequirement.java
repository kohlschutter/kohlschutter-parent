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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;

import com.kohlschutter.util.ProcessUtil;

/**
 * Annotation to control the availability of a specific test depending on the availability of
 * particular features provided by {@link ProcessUtil}.
 *
 * @author Christian Kohlschütter
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(ProcessUtilCondition.class)
public @interface ProcessUtilRequirement {
  /**
   * If set to {@code true}, the annotated test will only run if
   * {@link ProcessUtil#getJavaCommandArguments()} returns some non-empty value.
   *
   * @return {@code true} if required.
   */
  boolean canGetJavaCommandArguments();
}
