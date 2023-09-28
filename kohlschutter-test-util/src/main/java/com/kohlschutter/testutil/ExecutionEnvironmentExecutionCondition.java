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
import java.util.function.Supplier;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.kohlschutter.testutil.ExecutionEnvironmentRequirement.Rule;
import com.kohlschutter.util.ExecutionEnvironmentUtil;

/**
 * A JUnit {@link ExecutionCondition} for {@link ExecutionEnvironmentRequirement}.
 *
 * @author Christian Kohlschütter
 * @see AvailabilityRequirement
 */
public final class ExecutionEnvironmentExecutionCondition implements ExecutionCondition {

  /**
   * Default constructor.
   */
  public ExecutionEnvironmentExecutionCondition() {
    super();
  }

  private static ConditionEvaluationResult unconditionalExecution() {
    return ConditionEvaluationResult.enabled("Unconditional execution");
  }

  private static ConditionEvaluationResult notSatisfied(String ruleName, Rule ruleValue) {
    return ConditionEvaluationResult.disabled("Does not satisfy rule: " + ruleName + "="
        + ruleValue);
  }

  private static ConditionEvaluationResult checkRule(String ruleName, Rule rule,
      Supplier<Boolean> positiveCheck) {
    switch (rule) {
      case ALLOWED:
        return null;
      case REQUIRED:
        if (!positiveCheck.get()) {
          return notSatisfied(ruleName, rule);
        }
        break;
      case PROHIBITED:
        if (positiveCheck.get()) {
          return notSatisfied(ruleName, rule);
        }
        break;
      default:
        throw new IllegalStateException(ruleName);
    }
    return null;
  }

  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
    Optional<AnnotatedElement> element = context.getElement();
    @SuppressWarnings("null")
    ExecutionEnvironmentRequirement requirement = element.isPresent() ? element.get().getAnnotation(
        ExecutionEnvironmentRequirement.class) : null;
    if (requirement == null) {
      return unconditionalExecution();
    }

    ConditionEvaluationResult res;

    res = checkRule("eclipse", requirement.eclipse(), ExecutionEnvironmentUtil::isInEclipse);
    if (res != null) {
      return res;
    }

    res = checkRule("root", requirement.root(), () -> "root".equals(System.getProperty("user.name",
        "")));
    if (res != null) {
      return res;
    }

    res = checkRule("windows", requirement.windows(), ExecutionEnvironmentUtil::isWindows);
    if (res != null) {
      return res;
    }

    return ConditionEvaluationResult.enabled("Criteria satisfied");
  }
}
