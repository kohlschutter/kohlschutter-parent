package com.kohlschutter.testutil;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.opentest4j.TestAbortedException;

/**
 * A JUnit-compatible set of "soft assertions".
 * 
 * Soft assertions can be checked after aggregation.
 * 
 * @author Christian Kohlschütter
 */
public final class SoftAssertions implements Supplier<String> {
  private final List<AssertionError> errors = new ArrayList<>();
  private final Supplier<String> supplier = new Supplier<String>() {

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
        sb.append('\n');
      }
      return sb.toString();
    }
  };

  /**
   * Default constructor.
   */
  public SoftAssertions() {
    super();
  }

  /**
   * Adds a failed assertion, using the given message, to the list of errors.
   * 
   * @param message The message.
   */
  public void fail(String message) {
    fail(new AssertionError(message));
  }

  /**
   * Adds a failed assertion, using the given message and throwable, to the list of errors.
   * 
   * @param message The message.
   * @param t The throwable.
   */
  public void fail(String message, Throwable t) {
    fail(new AssertionError(message, t));
  }

  /**
   * Adds a failed assertion to the list of errors.
   * 
   * @param error The assertion error.
   */
  public void fail(AssertionError error) {
    errors.add(error);
  }

  /**
   * Checks if there were no errors.
   * 
   * @return {@code true} if no errors/failed assumptions were encountered.
   */
  public boolean checkPass() {
    return errors.isEmpty();
  }

  /**
   * Provides a concise error message.
   * 
   * @return The error message (or an empty string).
   */
  public Supplier<String> conciseErrorMessageSupplier() {
    return supplier;
  }

  /**
   * Adds all encountered assertion errors as suppressed errors to the given throwable.
   * 
   * @param <T> The type of the throwable.
   * @param t The throwable to augment with suppressed throwables.
   * @return The augmented throwable.
   */
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

  /**
   * JUnit-assume that the assertions pass.
   */
  public void assumePass() {
    try {
      assumeTrue(checkPass(), this);
    } catch (TestAbortedException e) {
      throw addAssertionThrowablesAsSuppressed(e);
    }
  }
}
