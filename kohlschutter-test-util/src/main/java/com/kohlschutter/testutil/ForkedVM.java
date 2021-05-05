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
