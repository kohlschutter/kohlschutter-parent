/*
 * kohlschutter-parent
 *
 * Copyright 2009-2024 Christian Kohlschütter
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
package com.kohlschutter.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Some helper methods for reflection.
 *
 * @author Christian Kohlschütter
 */
public final class ReflectionUtil {
  private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_BOXED = new HashMap<>();

  static {
    PRIMITIVE_TO_BOXED.put(boolean.class, Boolean.class);
    PRIMITIVE_TO_BOXED.put(byte.class, Byte.class);
    PRIMITIVE_TO_BOXED.put(char.class, Character.class);
    PRIMITIVE_TO_BOXED.put(double.class, Double.class);
    PRIMITIVE_TO_BOXED.put(float.class, Float.class);
    PRIMITIVE_TO_BOXED.put(int.class, Integer.class);
    PRIMITIVE_TO_BOXED.put(long.class, Long.class);
    PRIMITIVE_TO_BOXED.put(short.class, Short.class);
  }

  private ReflectionUtil() {
    throw new IllegalStateException("No instances");
  }

  private static final class ExceptionPlaceholder extends Exception {
    private static final long serialVersionUID = 1L;
  }

  /**
   * Tries to resolve an exception/throwable class name. On failure, placeholder exception classname
   * is returned.
   *
   * @param className the className of a {@link Throwable} to look up.
   * @return The corresponding class, or an internal placeholder class.
   */
  @SuppressWarnings("unchecked")
  public static Class<? extends Throwable> throwableByNameForAssertion(String className) {
    try {
      Class<?> klazz = Class.forName(className);
      if (Throwable.class.isAssignableFrom(klazz)) {
        return (Class<? extends Throwable>) klazz;
      } else {
        return ExceptionPlaceholder.class;
      }
    } catch (ClassNotFoundException e) {
      return ExceptionPlaceholder.class;
    }
  }

  private static boolean isIgnoredClass(String className) {
    return "true".equals(System.getProperty("com.kohlschutter.reflection.ignore-class." + className,
        ""));
  }

  /**
   * Tries to retrieve a singleton instance by calling the given static method on the class
   * identified by the given name. Returns {@code null} if there was an error.
   * <p>
   * By setting the system property of the form
   * {@code com.kohlschutter.reflection.ignore-class.com.example.ClassName=true}, you can prevent
   * resolution, and ensure that {@code null} is always returned for that class.
   *
   * @param <T> The desired type.
   * @param desiredType The desired type's class.
   * @param className The name of the class to retrieve the singleton.
   * @param methodName The method name to call (must be static).
   * @return The instance, or {@code null} if the instance could not be retrieved.
   */
  @SuppressWarnings("exports")
  public static <T> @Nullable T singletonIfPossible(Class<T> desiredType, String className,
      String methodName) {
    if (isIgnoredClass(className)) {
      return null;
    }
    try {
      Class<?> klazz = Class.forName(className);
      if (!desiredType.isAssignableFrom(klazz)) {
        return null; // NOPMD.ReturnEmptyCollectionRatherThanNull
      }
      Method m = klazz.getMethod(methodName);
      if ((m.getModifiers() & Modifier.STATIC) == 0) {
        return null; // NOPMD.ReturnEmptyCollectionRatherThanNull
      }
      return desiredType.cast(m.invoke(null));
    } catch (ClassNotFoundException | NoSuchMethodException | SecurityException
        | IllegalAccessException | InvocationTargetException | ClassCastException e) {
      return null; // NOPMD.ReturnEmptyCollectionRatherThanNull
    }
  }

  /**
   * Tries to instantiate a class given by name, and optionally values for the constructor.
   * <p>
   * By setting the system property of the form
   * {@code com.kohlschutter.reflection.ignore-class.com.example.ClassName=true}, you can prevent
   * resolution, and ensure that {@code null} is always returned for that class.
   *
   * @param <T> The desired type.
   * @param desiredType The desired type's class.
   * @param className The name of the class to instantiate.
   * @param args The arguments.
   * @return The instance, or {@code null} if the instance could not be retrieved.
   */
  @SuppressWarnings({"PMD.CognitiveComplexity", "PMD.ExceptionAsFlowControl", "exports"})
  public static <T> @Nullable T instantiateIfPossible(Class<T> desiredType, String className,
      Object... args) {
    if (isIgnoredClass(className)) {
      return null;
    }
    try {
      Class<?> klazz = Class.forName(className);
      if (!desiredType.isAssignableFrom(klazz)) {
        return null;
      }
      Constructor<?> constructor = null;
      for (Constructor<?> constr : klazz.getConstructors()) {
        if (constr.getParameterCount() != args.length) {
          continue;
        }
        constructor = constr; // Assume we found it
        if (args.length == 0) {
          break;
        }

        Parameter[] params = constr.getParameters();
        int i = 0;

        for (Parameter param : params) {
          Class<?> paramClass = param.getType();
          Object arg = args[i++];

          if (arg == null) {
            if (paramClass.isPrimitive()) {
              constructor = null; // cannot be cast
              break;
            } else {
              // check other parameters
              continue;
            }
          }
          Class<?> argClass = arg.getClass();

          if (paramClass.isAssignableFrom(argClass)) {
            // check other parameters
            continue;
          } else if (paramClass.isPrimitive()) {
            Class<?> boxedClass = PRIMITIVE_TO_BOXED.get(paramClass);
            if (boxedClass != null) {
              if (boxedClass.isAssignableFrom(argClass)) {
                continue;
              } else {
                constructor = null; // cannot be cast
                break;
              }
            } else {
              constructor = null; // unexpected; cannot be cast
              break;
            }
          } else {
            constructor = null; // cannot be cast
            break;
          }
        }
        if (constructor != null) {
          break;
        }
      }
      if (constructor == null) {
        throw new NoSuchMethodException("Could not find constructor for " + className);
      }
      return desiredType.cast(constructor.newInstance(args));
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException | NoSuchMethodException | SecurityException
        | ClassNotFoundException | ClassCastException e) {
      // e.printStackTrace();
      return null; // NOPMD.ReturnEmptyCollectionRatherThanNull
    }
  }
}
