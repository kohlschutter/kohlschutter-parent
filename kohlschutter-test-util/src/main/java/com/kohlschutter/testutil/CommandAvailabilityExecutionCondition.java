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

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * A JUnit {@link ExecutionCondition} for {@link CommandAvailabilityRequirement}.
 * 
 * @author Christian Kohlschütter
 * @see CommandAvailabilityRequirement
 */
public final class CommandAvailabilityExecutionCondition implements ExecutionCondition {
  /**
   * Default constructor.
   */
  public CommandAvailabilityExecutionCondition() {
    super();
  }

  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
    String[] commands = null;

    String message = "";

    Optional<AnnotatedElement> element = context.getElement();
    if (element.isPresent()) {
      CommandAvailabilityRequirement requirement = element.get().getAnnotation(
          CommandAvailabilityRequirement.class);
      if (requirement != null) {
        commands = requirement.commands();
        message = requirement.message();
      }
    }

    if (commands == null || commands.length == 0) {
      return ConditionEvaluationResult.enabled("Unconditional execution");
    }

    for (String command : commands) {
      try {
        if (Runtime.getRuntime().exec(new String[] {"which", command}).waitFor() != 0) {
          return ConditionEvaluationResult.disabled(message);
        }
      } catch (InterruptedException | IOException e) {
        return ConditionEvaluationResult.disabled(message);
      }
    }
    return ConditionEvaluationResult.enabled("All commands are available");
  }
}
