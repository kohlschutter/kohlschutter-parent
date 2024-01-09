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
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Some {@link Path}-related helper methods.
 *
 * @author Christian Kohlschütter
 */
public final class PathUtil {
  private PathUtil() {
    throw new IllegalStateException("No instances");
  }

  /**
   * Gets the file name of the given path as a string.
   *
   * @param p The path.
   * @return The filename
   * @throws IllegalStateException if the filename is null.
   */
  public static String getFilename(Path p) {
    Path filename = p.getFileName();
    if (filename == null) {
      throw new IllegalStateException();
    }
    return filename.toString();
  }

  /**
   * Resolves a sibling path that is identical to the given path, but a suffix string is appended,
   * keeping existing "file suffixes" intact.
   *
   * @param path The original path.
   * @param suffix The extra suffix.
   * @return The new sibling path with the additional suffix.
   */
  public static Path resolveSiblingAppendingSuffix(Path path, String suffix) {
    return path.resolveSibling(getFilename(path) + suffix);
  }

  /**
   * Creates ancestor directories for the given path.
   *
   * @param path The path to create ancestor directories for.
   * @throws IOException on error.
   */
  public static void createAncestorDirectories(Path path) throws IOException {
    Path parent = path.getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }
  }

  /**
   * Relativize a sibling path, using the base's parent directory.
   *
   * @param base The base path.
   * @param path The other path.
   * @return The relativized path.
   */
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
      Path newPath = p.getRoot();
      boolean exists = true;
      for (int i = 0, n = p.getNameCount(); i < n; i++) {
        if (i == 0 && newPath == null) {
          newPath = p.getName(i);
        } else {
          newPath = newPath.resolve(p.getName(i));
        }
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

  /**
   * Converts the given URL to a Path, if possible.
   *
   * @param url The URL to convert to.
   * @return The Path, or {@code null} if not convertible.
   */
  public static Path toPathIfPossible(URL url) {
    if (url == null) {
      return null;
    }
    try {
      return Paths.get(url.toURI());
    } catch (URISyntaxException e) {
      return null;
    }
  }
}
