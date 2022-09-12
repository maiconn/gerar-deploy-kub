package br.com.gerararquivos.shell;

import java.io.IOException;
import java.util.concurrent.Executors;

public class ExecutarSh {
    public static void executarDeployKub(String image, String workspace) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("sudo", "sh", "/usr/local/bin/upkub/pull-minikube.sh", image, workspace);
        Process process = builder.start();
        StreamGobbler streamGobbler =
                new StreamGobbler(process.getInputStream(), process.getErrorStream(), System.out::println);
        Executors.newSingleThreadExecutor().submit(streamGobbler);
        int exitCode = process.waitFor();
        System.out.printf("%s %s ", image, exitCode == 0 ? "" : "não " + "publicado");
        System.exit(exitCode);
    }

    public static void executarMvnCleanPackage(String workspace) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("sudo", "cd", workspace);
        builder.command("sudo", "/apache-maven-3.8.6/bin/mvn", "clean", "package");
        Process process = builder.start();
        StreamGobbler streamGobbler =
                new StreamGobbler(process.getInputStream(), process.getErrorStream(), System.out::println);
        Executors.newSingleThreadExecutor().submit(streamGobbler);
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }
}
