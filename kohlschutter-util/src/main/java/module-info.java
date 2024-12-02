/**
 * Some shared utility code.
 */
module com.kohlschutter.util {
  requires static com.kohlschutter.annotations.compiletime;

  exports com.kohlschutter.util;

  requires org.eclipse.jdt.annotation;
  requires static java.management;
}
