/**
 * When adding this dependency to your module-info, please use:
 * <p>
 * <code> requires static transitive
 * com.kohlschutter.annotations.compiletime; <br>
 * requires static transitive org.eclipse.jdt.annotation;
 * </code>
 */
module com.kohlschutter.annotations.compiletime {
  exports com.kohlschutter.annotations.compiletime;

  requires transitive org.eclipse.jdt.annotation;
}
