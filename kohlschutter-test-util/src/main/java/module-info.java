/**
 * Testing-related helper classes.
 */
module com.kohlschutter.testutil {
  exports com.kohlschutter.testutil;

  requires transitive org.junit.jupiter.api;
  requires transitive com.kohlschutter.util;
  requires java.logging;
  requires org.eclipse.jdt.annotation;
}
