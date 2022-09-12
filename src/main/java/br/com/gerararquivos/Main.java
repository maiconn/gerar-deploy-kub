package br.com.gerararquivos;

import br.com.gerararquivos.database.SQLiteJDBCDriverConnection;
import br.com.gerararquivos.shell.ExecutarDeployKub;

import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

public class Main {
    private static final String ARQUIVO_COMPLETO_TEMPLATE = "exemploCompleto.yaml";
    private static final String ARQUIVO_DOCKERFILE = "Dockerfile";

    public static void main(String[] args) throws IOException, SQLException, InterruptedException {
        System.out.println(List.of(args));
        String workspace = args[0];
        String gitUrl = args[1];
        String javaOpts = args[2];

        String[] partes = gitUrl.split("/");
        String usuario = partes[partes.length - 2].toLowerCase();
        String repositorio = partes[partes.length - 1].split("\\.", 2)[0].toLowerCase();
        String appPath = usuario + "/" + repositorio;
        String image = (usuario + "-" + repositorio).replace("_", "-");
        Integer port = SQLiteJDBCDriverConnection.getPorta(appPath);

        System.out.println("publicando " + appPath);
        copiarDockerfile(workspace, javaOpts, appPath);
        createArquivoCompleto(workspace, image, port.toString(), usuario, repositorio);
        ExecutarDeployKub.executarDeployKub(image, workspace);
    }

    private static void createArquivoCompleto(String workspace, String image, String port, String usuario, String repo) throws IOException {
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
            fileContent = fileContent.replace("{{usuario}}", usuario);
            fileContent = fileContent.replace("{{repo}}", repo);
            fileContent = fileContent.replace("{{image}}", image);
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

    public static void copiarDockerfile(String workspace, String javaOpts, String appPath) throws IOException {
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
            String url = "-Dspring.datasource.url=jdbc:oracle:thin:@10.0.20.80:1521/xe -Doracle.jdbc.timezoneAsRegion=false";
            String port = "-Dserver.port=8080";
            String profile = "-Dspring.profiles.active=hml";
            String appName = "-Dspring.application.name=" + appPath;
            String forwardHeader = "-Dserver.use-forward-headers=true -Dserver.forward-headers-strategy=framework -Dspringdoc.swagger-ui.path=/";
            fileContent = fileContent.replace("{{javaOpts}}", javaOpts + " "
                    + url + " "
                    + port + " "
                    + profile + " "
                    + appName + " "
                    + forwardHeader);

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
