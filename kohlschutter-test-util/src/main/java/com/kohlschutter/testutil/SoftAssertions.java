package com.kohlschutter.testutil;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.opentest4j.TestAbortedException;

public final class SoftAssertions implements Supplier<String> {
  private final List<AssertionError> errors = new ArrayList<>();
  private final Supplier<String> conciseErrorMessageSupplier = new Supplier<String>() {

    @Override
    public String get() {
      StringBuilder sb = new StringBuilder();
      boolean first = true;
      for (AssertionError err : errors) {
        if (first) {
          first = false;
        } else {
          sb.append("; ");
        }
        sb.append(err.getMessage());
        sb.append("\n");
      }
      return sb.toString();
    }
  };

  public void fail(String message) {
    fail(new AssertionError(message));
  }

  public void fail(String message, Throwable t) {
    fail(new AssertionError(message, t));
  }

  public void fail(AssertionError error) {
    errors.add(error);
  }

  public boolean checkPass() {
    return errors.isEmpty();
  }

  public Supplier<String> conciseErrorMessageSupplier() {
    return conciseErrorMessageSupplier;
  }
  
  public <T extends Throwable> T addAssertionThrowablesAsSuppressed(T t) {
    for (AssertionError err : errors) {
      t.addSuppressed(err);
    }
    return t;
  }

  @Override
  public String get() {
    return conciseErrorMessageSupplier().get();
  }

  public void assumePass() {
    try {
      assumeTrue(checkPass(), this);
    } catch (TestAbortedException e) {
      throw addAssertionThrowablesAsSuppressed(e);
    }
  }
}
