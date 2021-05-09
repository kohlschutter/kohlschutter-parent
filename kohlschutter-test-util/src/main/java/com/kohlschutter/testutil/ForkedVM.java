package com.kohlschutter.testutil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kohlschutter.util.ProcessUtil;

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

  private List<String> cmd;

  private final String overrideMainClass;
  private final String[] overrideArgs;

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

    Process p = new ProcessBuilder(cmd) //
        .redirectOutput(Redirect.PIPE) //
        .redirectError(Redirect.PIPE) //
        .start();

    return p;
  }

  private void parse() throws IOException, UnsupportedOperationException {
    String commandline = ProcessUtil.getCommandline();
    if (commandline == null) {
      throw new UnsupportedOperationException("Could not get VM commandline");
    }

    List<String> args = new ArrayList<>(Arrays.asList(commandline.split("[ ]+")));
    onJavaExecutable(args.remove(0));
    while (!args.isEmpty()) {
      String arg = unescapeJavaArg(args.remove(0));

      if (HAS_PARAMETER.contains(arg)) {
        onJavaOption(arg, unescapeJavaArg(args.remove(0)));
      } else if (!arg.startsWith("-")) {
        if (arg.startsWith("@") && arg.length() > 1) {
          List<String> extraArgs = new ArrayList<>();
          File f = new File(arg.substring(1));
          try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f),
              Charset.defaultCharset()))) {
            String arg0;
            while ((arg0 = in.readLine()) != null) {
              extraArgs.add(arg0);
            }
          }
          args.addAll(0, extraArgs);
          continue;
        }

        onJavaMainClass(arg);
        onArguments(args);
        break;
      } else if (arg.startsWith("-javaagent")) {
        if (!onJavaAgent(arg)) {
          if (arg.contains("jacoco")) {
            Pattern patDestFile = Pattern.compile("^(.+?[=,]destfile=)([^,=]+)(.*?)$");
            Matcher m = patDestFile.matcher(arg);
            if (m.find()) {
              // This is how Maven calls jacoco. We can create a separate coverage file, which we
              // then aggregate later.
              File currentJacocoExec = new File(m.group(2));
              File newJacocoExec = new File(currentJacocoExec.getParentFile(), "jacoco-forked-"
                  + UUID.randomUUID() + ".exec");
              System.err.println("[INFO] (ForkedVM) Writing code coverage for forked process to "
                  + newJacocoExec);

              StringBuilder sb = new StringBuilder();
              sb.append(m.group(1));
              sb.append(newJacocoExec.toString());
              sb.append(m.group(3));

              onJavaOption(sb.toString());
            } else {
              // Eclipse calls jacoco using a TCP port, and it looks like access that port from
              // multiple clients isn't supported (yet?), so let's emit a warning and see what
              // happens.
              System.err.println(
                  "[WARNING] (ForkedVM) Code coverage may be incomplete for code only called from the forked VM");

              onJavaOption(arg);
            }
          }
        }
      } else {
        onJavaOption(arg);
      }
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

  protected void onJavaOption(String option, String arg) {
    cmd.add(option);
    cmd.add(arg);
  }

  protected void onJavaMainClass(String arg) {
    if (overrideMainClass != null) {
      arg = overrideMainClass;
    }
    cmd.add(arg);
  }

  protected void onArguments(List<String> args) {
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
