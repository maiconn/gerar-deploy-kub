package br.com.gerararquivos;

import br.com.gerararquivos.database.SQLiteJDBCDriverConnection;
import br.com.gerararquivos.shell.ExecutarSh;
import br.com.gerararquivos.trocararquivoswagger.TrocarArquivoSwagger;

import java.io.*;
import java.sql.SQLException;
import java.util.List;

public class Main {
    private static final String ARQUIVO_COMPLETO_TEMPLATE = "exemploCompleto.yaml";
    private static final String ARQUIVO_DOCKERFILE = "Dockerfile";
    private static final String LOG_LEVEL = System.getProperty("log.level");

    public static void main(String[] args) throws IOException, SQLException, InterruptedException {
        println(List.of(args));
        if ("0".equals(args[0])) {
            TrocarArquivoSwagger.modificarArquivoOpenApiConfig(args[1]);
            return;
        }
        String workspace = args[0];
        String gitUrl = args[1];
        String javaOpts = args[2];

        String[] partes = gitUrl.split("/");
        String usuario = partes[partes.length - 2].toLowerCase();
        String repositorio = partes[partes.length - 1].split("\\.", 2)[0].toLowerCase();
        String appPath = usuario + "/" + repositorio;
        String image = (usuario + "-" + repositorio).replace("_", "-");
        Integer port = SQLiteJDBCDriverConnection.getPorta(appPath);

        copiarDockerfile(workspace, javaOpts, appPath);
        createArquivoKubernetesCompleto(workspace, image, port.toString(), usuario, repositorio);
        ExecutarSh.executarDeployKub(image, workspace);
        System.out.println();
        System.out.println();
        System.out.println("================================");
        System.out.print("Publicado em ");
        System.out.println("http://vemser-dbc.dbccompany.com.br:39000/" + usuario + "/" + repositorio);
        System.out.println("================================");
        System.out.println("");
        System.exit(0);
    }

    private static void createArquivoKubernetesCompleto(String workspace, String image, String port, String usuario, String repo) throws IOException {
        System.out.println("criando arquivo de deploy completo");

        String fileContent = readFileToString(ARQUIVO_COMPLETO_TEMPLATE);
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


    public static void copiarDockerfile(String workspace, String javaOpts, String appPath) throws IOException {
        System.out.println("criando arquivo dockerfile");
        String fileContent = readFileToString(ARQUIVO_DOCKERFILE);
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

    private static String readFileToString(String file) throws IOException {
        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(file)) {
            return new String(inputStream.readAllBytes());
        }
    }

    public static void println(Object msg) {
        if ("DEBUG".equals(LOG_LEVEL)) {
            System.out.println(msg);
        }
    }
}