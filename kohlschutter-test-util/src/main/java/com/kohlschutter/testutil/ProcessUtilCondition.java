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

import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.kohlschutter.util.ProcessUtil;

/**
 * Checks if the requirements specified by {@link ProcessUtilRequirement} are met.
 *
 * @author Christian Kohlschütter
 */
public final class ProcessUtilCondition implements ExecutionCondition {
  /**
   * Default constructor.
   */
  public ProcessUtilCondition() {
    super();
  }

  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
    Optional<AnnotatedElement> element = context.getElement();
    if (element.isPresent()) {
      @SuppressWarnings("null")
      ProcessUtilRequirement requirement = element.get().getAnnotation(
          ProcessUtilRequirement.class);
      if (requirement != null) {
        if (requirement.canGetJavaCommandArguments()) {
          String[] args = ProcessUtil.getJavaCommandArguments();
          if (args == null || args.length == 0) {
            return ConditionEvaluationResult.disabled("Cannot get Java commandline arguments");
          }
        }
      }
    }

    return ConditionEvaluationResult.enabled("Everything looks good");
  }
}
