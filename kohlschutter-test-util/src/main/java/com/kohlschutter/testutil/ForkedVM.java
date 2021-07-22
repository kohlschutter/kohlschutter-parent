package com.kohlschutter.testutil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kohlschutter.util.ProcessUtil;
import com.kohlschutter.util.SystemPropertyUtil;

/**
 * Simplifies forking a Java VM based on the configuration of the currently running VM.
 * 
 * This is mostly designed to be used during unit testing when running a second VM is required.
 * 
 * @author Christian Kohlschütter
 */
public class ForkedVM {
  private static final Set<String> HAS_PARAMETER = new HashSet<>(Arrays.asList("--add-opens", "-p",
      "-cp", "--module-path", "--upgrade-module-path", "-classpath", "--class-path",
      "--patch-module", "--add-reads", "--add-exports", "--add-opens", "--add-modules", "-d",
      "--describe-module", "--limit-modules", "-jar"));

  private static final boolean SUPPORTED;

  static {
    if (!SystemPropertyUtil.getBooleanSystemProperty("com.kohlschutter.ForkedVM.enabled", true)) {
      SUPPORTED = false;
    } else if (ProcessUtil.getJavaCommand() == null || ProcessUtil
        .getJavaCommandArguments() == null) {
      SUPPORTED = false;
    } else {
      SUPPORTED = true;
    }
  }

  private List<String> cmd;

  private final String overrideMainClass;
  private final String[] overrideArgs;

  private boolean haveJavaMainClass = false;

  private boolean haveArguments = false;

  protected ForkedVM() {
    this(null, (String[]) null);
  }

  public ForkedVM(String mainClass, String... args) {
    this.overrideMainClass = mainClass;
    this.overrideArgs = args == null ? null : args.clone();
  }

  public Process fork() throws IOException, UnsupportedOperationException {
    cmd = new ArrayList<>();
    parse();
    if (!haveJavaMainClass) {
      onJavaMainClass(null);
    }
    if (!haveArguments) {
      onArguments(Collections.emptyList());
    }

    Process p = new ProcessBuilder(cmd) //
        .redirectOutput(Redirect.PIPE) //
        .redirectError(Redirect.PIPE) //
        .start();

    return p;
  }

  /**
   * Checks if launching a new Java VM based on the current one is supported.
   * 
   * @return {@code true} if supported.
   */
  public static boolean isSupported() {
    return SUPPORTED;
  }

  private void parse() throws IOException, UnsupportedOperationException {
    String command = ProcessUtil.getJavaCommand();
    if (command == null) {
      throw new UnsupportedOperationException("Could not get VM command");
    }
    String[] commandArgs = ProcessUtil.getJavaCommandArguments();
    if (commandArgs == null || commandArgs.length == 0) {
      throw new UnsupportedOperationException("Could not get VM command arguments");
    }

    onJavaExecutable(command);

    List<String> args = new ArrayList<>(Arrays.asList(commandArgs));

    while (!args.isEmpty()) {
      String arg = unescapeJavaArg(args.remove(0));

      if (!parseArg(args, arg)) {
        break;
      }
    }
  }

  @SuppressWarnings("PMD.CognitiveComplexity")
  private boolean parseArg(List<String> args, String arg) throws FileNotFoundException,
      IOException {
    if (HAS_PARAMETER.contains(arg)) {
      onJavaOption(arg, unescapeJavaArg(args.remove(0)));
    } else if (!arg.startsWith("-")) {
      if (arg.startsWith("@") && arg.length() > 1) {
        addExtraArgsFromFile(args, new File(arg.substring(1)));
        return true;
      } else {
        onJavaMainClass(arg);
        onArguments(args);
        return false;
      }
    } else if (arg.startsWith("-javaagent")) {
      if (!onJavaAgent(arg) && arg.contains("jacoco")) {
        parseJacocoJavaAgent(arg);
      }
    } else if (arg.startsWith("-XX:StartFlightRecording=") || arg.startsWith(
        "-XX:StartFlightRecording:")) {
      if (!onStartFlightRecording(arg) && arg.contains("filename=")) {
        parseStartFlightRecording(arg);
      }
    } else {
      onJavaOption(arg);
    }

    return true;
  }

  private void addExtraArgsFromFile(List<String> args, File f) throws FileNotFoundException,
      IOException {
    List<String> extraArgs = new ArrayList<>();
    try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f),
        Charset.defaultCharset()))) {
      String arg0;
      while ((arg0 = in.readLine()) != null) {
        extraArgs.add(arg0);
      }
    }
    args.addAll(0, extraArgs);
  }

  private File replacePath(String arg, Pattern patDestFile, String prefix, String suffix) {
    Matcher m = patDestFile.matcher(arg);
    if (m.find()) {
      // Create a separate file for the ForkedVM
      File oldFile = new File(m.group(2));
      File newFile = new File(oldFile.getParentFile(), prefix + UUID.randomUUID() + suffix);

      StringBuilder sb = new StringBuilder();
      sb.append(m.group(1));
      sb.append(newFile.toString());
      sb.append(m.group(3));

      onJavaOption(sb.toString());
      return newFile;
    } else {
      // Eclipse calls jacoco using a TCP port, and it looks like access that port from
      // multiple clients isn't supported (yet?), so let's emit a warning and see what
      // happens.
      System.err.println(
          "[WARNING] (ForkedVM) Code coverage may be incomplete for code only called from the forked VM");

      onJavaOption(arg);
      return null;
    }
  }

  private void parseJacocoJavaAgent(String arg) {
    Pattern patDestFile = Pattern.compile("^(.+?[=,]destfile=)([^,=]+)(.*?)$");
    File newFile;
    if ((newFile = replacePath(arg, patDestFile, "jacoco-forked-", ".exec")) != null) {
      // This is how Maven calls jacoco. We can create a separate coverage file, which we
      // then aggregate later.
      System.err.println("[INFO] (ForkedVM) Writing code coverage for forked process to "
          + newFile);
    } else {
      // Eclipse calls jacoco using a TCP port, and it looks like access that port from
      // multiple clients isn't supported (yet?), so let's emit a warning and see what
      // happens.
      System.err.println(
          "[WARNING] (ForkedVM) Code coverage may be incomplete for code only called from the forked VM");

      onJavaOption(arg);
    }
  }

  private void parseStartFlightRecording(String arg) {
    Pattern patDestFile = Pattern.compile("^(.+?[=,]filename=)([^,=]+)(.*?)$");
    File newFile;
    if ((newFile = replacePath(arg, patDestFile, "jfr-forked-", ".jfr")) != null) {
      System.err.println("[INFO] (ForkedVM) Writing flight recording data to " + newFile);
    } else {
      onJavaOption(arg);
    }
  }

  protected void onJavaExecutable(String executable) {
    cmd.add(executable);
  }

  protected void onJavaOption(String option) {
    cmd.add(option);
  }

  /**
   * Called for a {@code -javaagent} option.
   * 
   * @param option The option.
   * @return {@code true} if handled by this method. If {@code false}, some fallback options may be
   *         applied by {@link ForkedVM}.
   */
  protected boolean onJavaAgent(String option) {
    // ignored by default
    return false;
  }

  /**
   * Called for a {@code -XX:StartFlightRecording=} option.
   * 
   * @param option The option.
   * @return {@code true} if handled by this method. If {@code false}, some fallback options may be
   *         applied by {@link ForkedVM}.
   */
  protected boolean onStartFlightRecording(String option) {
    // ignored by default
    return false;
  }

  protected void onJavaOption(String option, String arg) {
    if ("-jar".equals(option)) {
      option = "-cp";
    }
    cmd.add(option);
    cmd.add(arg);
  }

  protected void onJavaMainClass(String arg) {
    haveJavaMainClass = true;
    if (overrideMainClass != null) {
      arg = overrideMainClass;
    }
    if (arg != null) {
      cmd.add(arg);
    }
  }

  protected void onArguments(List<String> args) {
    haveArguments = true;
    if (overrideArgs != null) {
      args = Arrays.asList(overrideArgs);
    }
    cmd.addAll(args);
  }

  private static String unescapeJavaArg(String arg) {
    if (arg.length() > 1 && arg.endsWith("\"")) {
      if (arg.startsWith("\"")) {
        arg = arg.substring(1, arg.length() - 1);
      } else if (arg.contains("=\"")) {
        arg = arg.replace("=\"", "=");
        arg = arg.substring(0, arg.length() - 1);
      }
    }
    return arg;
  }
}
