package br.com.gerararquivos;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class Main {
    private static final String ARQUIVO_DEPLOY_TEMPLATE = "exemploDeployment.yaml";
    private static final String ARQUIVO_SERVICE_TEMPLATE = "exemploService.yaml";
    private static final String ARQUIVO_INGRESS_TEMPLATE = "exemploIngress.yaml";
    private static final String ARQUIVO_DOCKERFILE = "Dockerfile";

    public static void main(String[] args) throws URISyntaxException, IOException {
        System.out.println(List.of(args));
        String workspace = args[0];
        String jobName = args[1];
        String image = args[2];
        String port = args[3];
        copiarDockerfile(workspace);
        createArquivoDeploy(workspace, jobName, image);
        createArquivoService(workspace, jobName, port);
        createArquivoIngress(workspace, jobName, port);
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

    public static void createArquivoDeploy(String workspace, String jobName, String image) throws URISyntaxException, IOException {
        System.out.println("criando arquivo de deploy");
        URL resource = Main.class.getClassLoader().getResource(ARQUIVO_DEPLOY_TEMPLATE);
        if (resource == null) {
            throw new IllegalArgumentException(ARQUIVO_DEPLOY_TEMPLATE + " file not found!");
        } else {
            String fileContent;

            byte[] data;
            try (InputStream in = Main.class.getClassLoader().getResourceAsStream(ARQUIVO_DEPLOY_TEMPLATE)) {
                data = in.readAllBytes();
            }
            fileContent = new String(data);
            fileContent = fileContent.replace("{{jobName}}", jobName);
            fileContent = fileContent.replace("{{image}}", image + ":latest");

            File destino = new File(workspace + "/k8s/deployment.yaml");
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

    public static void createArquivoService(String workspace, String jobName, String port) throws URISyntaxException, IOException {
        System.out.println("criando arquivo de service");
        URL resource = Main.class.getClassLoader().getResource(ARQUIVO_SERVICE_TEMPLATE);
        if (resource == null) {
            throw new IllegalArgumentException(ARQUIVO_SERVICE_TEMPLATE + " file not found!");
        } else {
            String fileContent;

            byte[] data;
            try (InputStream in = Main.class.getClassLoader().getResourceAsStream(ARQUIVO_SERVICE_TEMPLATE)) {
                data = in.readAllBytes();
            }
            fileContent = new String(data);
            fileContent = fileContent.replace("{{jobName}}", jobName);
            fileContent = fileContent.replace("{{port}}", port);

            File destino = new File(workspace + "/k8s/service.yaml");
            if (destino.exists()) {
                destino.delete();
            }
            destino.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(destino));
            writer.append(fileContent);
            writer.close();
        }
    }

    public static void createArquivoIngress(String workspace, String jobName, String port) throws URISyntaxException, IOException {
        System.out.println("criando arquivo de ingress");
        URL resource = Main.class.getClassLoader().getResource(ARQUIVO_INGRESS_TEMPLATE);
        if (resource == null) {
            throw new IllegalArgumentException(ARQUIVO_INGRESS_TEMPLATE + " file not found!");
        } else {
            String fileContent;

            byte[] data;
            try (InputStream in = Main.class.getClassLoader().getResourceAsStream(ARQUIVO_INGRESS_TEMPLATE)) {
                data = in.readAllBytes();
            }
            fileContent = new String(data);
            fileContent = fileContent.replace("{{jobName}}", jobName);
            fileContent = fileContent.replace("{{port}}", port);

            File destino = new File(workspace + "/k8s/ingress.yaml");
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
