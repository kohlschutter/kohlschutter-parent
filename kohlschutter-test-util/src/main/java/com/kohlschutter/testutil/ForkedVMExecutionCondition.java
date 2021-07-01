/*
 * junixsocket
 *
 * Copyright 2009-2021 Christian Kohlschütter
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

public class ForkedVMExecutionCondition implements ExecutionCondition {
  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
    Boolean forkSupported = null;

    String message = "";

    Optional<AnnotatedElement> element = context.getElement();
    if (element.isPresent()) {
      ForkedVMRequirement requirement = element.get().getAnnotation(
          ForkedVMRequirement.class);
      if (requirement != null) {
        forkSupported = requirement.forkSupported();
      }
    }

    if (forkSupported == null) {
      return ConditionEvaluationResult.enabled("Unconditional execution");
    }
    
    if (forkSupported.booleanValue() != ForkedVM.isSupported()) {
      return ConditionEvaluationResult.disabled(message);
    } else {
      return ConditionEvaluationResult.enabled("ForkedVM support status as expected");
    }
  }
}
