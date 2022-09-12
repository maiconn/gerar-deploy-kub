package br.com.gerararquivos.shell;

import java.io.IOException;
import java.util.concurrent.Executors;

public class ExecutarDeployKub {
    public static void executarDeployKub(String image, String workspace) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("sudo", "sh", "/usr/local/bin/upkub/pull-minikube.sh", image, workspace);
        Process process = builder.start();
        StreamGobbler streamGobbler =
                new StreamGobbler(process.getInputStream(), process.getErrorStream(), System.out::println);
        Executors.newSingleThreadExecutor().submit(streamGobbler);
        int exitCode = process.waitFor();
        System.exit(exitCode);
    }
}
