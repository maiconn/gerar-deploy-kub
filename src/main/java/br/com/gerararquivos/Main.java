package br.com.gerararquivos;

import java.io.*;
import java.net.URL;
import java.util.List;

public class Main {
    private static final String ARQUIVO_COMPLETO_TEMPLATE = "exemploCompleto.yaml";
    private static final String ARQUIVO_DOCKERFILE = "Dockerfile";

    public static void main(String[] args) throws IOException {
        System.out.println(List.of(args));
        String workspace = args[0];
        String jobName = args[1];
        String image = args[2];
        String port = args[3];
        copiarDockerfile(workspace);
        createArquivoCompleto(workspace, jobName, image, port);
    }

    private static void createArquivoCompleto(String workspace, String jobName, String image, String port) throws IOException {
        System.out.println("criando arquivo de deploy completo");
        URL resource = Main.class.getClassLoader().getResource(ARQUIVO_COMPLETO_TEMPLATE);
        if (resource == null) {
            throw new IllegalArgumentException(ARQUIVO_COMPLETO_TEMPLATE + " file not found!");
        } else {
            String fileContent;

            byte[] data;
            try (InputStream in = Main.class.getClassLoader().getResourceAsStream(ARQUIVO_COMPLETO_TEMPLATE)) {
                data = in.readAllBytes();
            }
            fileContent = new String(data);
            fileContent = fileContent.replace("{{jobName}}", jobName);
            fileContent = fileContent.replace("{{image}}", image + ":latest");
            fileContent = fileContent.replace("{{port}}", port);

            File destino = new File(workspace + "/k8s/complete-deployment.yaml");
            if (destino.exists()) {
                destino.delete();
            }
            if (!destino.getParentFile().exists()) {
                destino.getParentFile().mkdirs();
            }
            destino.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(destino));
            writer.append(fileContent);
            writer.close();
        }
    }

    public static void copiarDockerfile(String workspace) throws IOException {
        System.out.println("criando arquivo dockerfile");
        URL resource = Main.class.getClassLoader().getResource(ARQUIVO_DOCKERFILE);
        if (resource == null) {
            throw new IllegalArgumentException(ARQUIVO_DOCKERFILE + " file not found!");
        } else {
            String fileContent;

            byte[] data;
            try (InputStream in = Main.class.getClassLoader().getResourceAsStream(ARQUIVO_DOCKERFILE)) {
                data = in.readAllBytes();
            }
            fileContent = new String(data);

            File destino = new File(workspace + "/Dockerfile");
            if (destino.exists()) {
                destino.delete();
            }
            destino.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(destino));
            writer.append(fileContent);
            writer.close();
        }
    }

}
