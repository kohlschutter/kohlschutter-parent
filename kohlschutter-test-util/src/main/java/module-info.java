/**
 * Testing-related helper classes.
 */
module com.kohlschutter.testutil {
  exports com.kohlschutter.testutil;

  requires static com.kohlschutter.annotations.compiletime;
  requires static org.eclipse.jdt.annotation;
  requires transitive org.junit.jupiter.api;
  requires transitive com.kohlschutter.util;
  requires java.logging;
}
