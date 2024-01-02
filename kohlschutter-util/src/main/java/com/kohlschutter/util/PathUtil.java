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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class PathUtil {
  private PathUtil() {
    throw new IllegalStateException("No instances");
  }

  public static String getFilename(Path p) {
    Path filename = p.getFileName();
    if (filename == null) {
      throw new IllegalStateException();
    }
    return filename.toString();
  }

  public static Path resolveSiblingAppendingSuffix(Path path, String suffix) {
    return path.resolveSibling(getFilename(path) + suffix);
  }

  public static void createAncestorDirectories(Path generatedCssPath) throws IOException {
    Path parent = generatedCssPath.getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }
  }

  public static Path relativizeSibling(Path base, Path path) {
    Path parent = base.getParent();
    if (parent == null) {
      throw new IllegalStateException();
    }
    return parent.relativize(path);
  }

  /**
   * Tries to resolve a path into a real path as much as possible.
   * <p>
   * If only a parent's real path exists, we'll use that one.
   *
   * @param p The path to canonicalize.
   * @return A somewhat real path.
   */
  public static Path partialRealpath(Path p) {
    try {
      return p.toRealPath();
    } catch (IOException e) {
      Path newPath = Paths.get("/");
      boolean exists = true;
      for (int i = 0, n = p.getNameCount(); i < n; i++) {
        newPath = newPath.resolve(p.getName(i));
        if (exists && Files.exists(newPath)) {
          try {
            newPath = newPath.toRealPath();
          } catch (IOException e1) {
            // try really hard
          }
        } else {
          exists = false;
        }
      }

      return newPath;
    }
  }
}
