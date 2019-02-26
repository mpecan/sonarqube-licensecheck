package at.porscheinformatik.sonarqube.licensecheck.gradle;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;

class GradleInvoker {

    private static final Logger LOGGER = LoggerFactory.getLogger(GradleInvoker.class);

    private static final String BUILD_GRADLE = "build.gradle";
    private String gradleExec;

    private final File projectRoot;

    private String userSettings;

    // todo: use gradle tooling api if possible
    GradleInvoker(String projectRoot) {
        this.projectRoot = new File(projectRoot);
        File buildGradle = new File(projectRoot, BUILD_GRADLE);

        if (!buildGradle.exists()) {
            throw new RuntimeException("no build.gradle found");
        }

        gradleExec = resolveGradleExecutable(this.projectRoot);

        userSettings = null;
        CommandLine cmd = getCommandLineArgs();
        if (cmd != null && cmd.hasOption("I")) {
            userSettings = cmd.getOptionValue("I");
        }
    }

    String invoke(String... gradleTasks) throws IOException, GradleInvokerException {

        String[] command = resolveFullGradleCommand(gradleTasks);

        ProcessBuilder processBuilder = new ProcessBuilder();
        Process process = processBuilder.command(command).directory(projectRoot).start();
        Thread timeoutThread = new Thread(() -> {
            try {
                Thread.sleep(3 * 60 * 1000L);
                process.destroyForcibly();
                LOGGER.error("Had to interrupt gradle process, as it was stuck.");
            } catch (InterruptedException e) {
                LOGGER.info("Gradle Timeout thread has been interrupted, gradle execution successful");
            }
        });
        timeoutThread.start();
        InputStream errorStream = process.getErrorStream();
        InputStream inputStream = process.getInputStream();
        redirectOutput(errorStream, LOGGER::error);
        redirectOutput(inputStream, LOGGER::debug);

        while (process.isAlive()) {
            // Waiting for gradle to finish
        }
        timeoutThread.interrupt();
        if (process.exitValue() != 0) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Failed execution of gradle command {}", Arrays.toString(command));
            }
            throw new GradleInvokerException("Failed execution of gradle command ");
        }

        return "";
    }

    private String resolveGradleExecutable(File projectRoot) {
        File gradlew = new File(projectRoot, "gradlew");

        if (gradlew.exists()) {
            if (LOGGER.isInfoEnabled()) {

                resolveGradleVersion(gradlew.getAbsolutePath());
            }
            return gradlew.getAbsolutePath();
        } else {
            String gradle = "gradle";
            resolveGradleVersion(gradle);
            return gradle;
        }
    }

    private void resolveGradleVersion(String exec) {
        ProcessBuilder processBuilder = new ProcessBuilder(exec, "--version");
        Process process = null;
        try {
            process = processBuilder.start();
            redirectOutput(process.getInputStream(), LOGGER::info);
        } catch (IOException e) {
            LOGGER.error("Error resolving gradle version: ", e);
        }
    }

    private String[] resolveFullGradleCommand(String[] gradleCommands) {
        String[] baseCommands;
        if (userSettings == null) {
            baseCommands = new String[]{gradleExec, "-i"};
        } else {
            String[] split = userSettings.split(" ");
            baseCommands = new String[split.length + 2];
            baseCommands[0] = gradleExec;
            baseCommands[1] = "-i";
            baseCommands[2] = split[0];
            baseCommands[3] = split[1];
        }
        return Stream
            .of(baseCommands, gradleCommands)
            .flatMap(Stream::of)
            .toArray(String[]::new);
    }

    private void redirectOutput(InputStream stream, Consumer<String> stringConsumer) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        Thread thread = new Thread(() -> {
            String line;
            while (true) {
                try {
                    if ((line = reader.readLine()) == null) break;
                    stringConsumer.accept(line);
                } catch (IOException e) {
                    LOGGER.error("Error redirecting stream.", e);
                }
            }
        });
        thread.start();


    }

    public static class GradleInvokerException extends Exception {
        GradleInvokerException(String message) {
            super(message);
        }
    }

    private static CommandLine getCommandLineArgs() {
        CommandLine cmd = null;
        try {
            String commandArgs = System.getProperty("sun.java.command");
            CommandLineParser parser = new DefaultParser();
            Options options = new Options();
            options.addOption("I", "--init-script", true, "Specifies an initialization script.");
            cmd = parser.parse(options, commandArgs.split(" "));
        } catch (Exception e) {
            // ignore unparsable command line args
        }
        return cmd;
    }

}
