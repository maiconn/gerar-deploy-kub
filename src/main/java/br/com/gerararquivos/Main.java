package br.com.gerararquivos;

import br.com.gerararquivos.trocararquivoswagger.TrocarArquivoSwagger;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    private static final String LOG_LEVEL = System.getProperty("log.level");

    public static void main(String[] args) throws IOException {
        println(List.of(args));
        if ("0".equals(args[0])) { //BACK
            TrocarArquivoSwagger.modificarArquivoOpenApiConfig(args[1]);
        } else if ("1".equals(args[0])) { //FRONT
            String workspace = args[1];
            String[] partes = args[2].split("/");
            String repositorio = partes[partes.length - 1].split("\\.", 2)[0].toLowerCase();
            String usuario = partes[partes.length - 2].toLowerCase();
            String appPath = usuario + "/" + repositorio;
            copiarArquivoEnv(workspace, appPath);
            modificarArquivoPackage(workspace, appPath);
            System.exit(0);
        }
        System.exit(0);
    }

    private static void copiarArquivoEnv(String workspace, String publicURL) throws IOException {
        System.out.println("criando arquivo .env");
        String fileContent = readFileToString("./react/.env");
        fileContent = fileContent.replace("{{URL}}", publicURL);

        File destino = new File(workspace + "/.env");
        if (destino.exists()) {
            destino.delete();
        }
        destino.createNewFile();

        BufferedWriter writer = new BufferedWriter(new FileWriter(destino));
        writer.append(fileContent);
        writer.close();
    }

    private static void modificarArquivoPackage(String workspace, String publicURL) throws IOException {
        System.out.println("criando arquivo package.json");

        String content = new String(Files.readAllBytes(Paths.get(workspace + "/package.json")));

        JsonObject convertedObject = new Gson().fromJson(content, JsonObject.class);
        convertedObject.addProperty("homepage", publicURL);


        File destino = new File(workspace + "/package.json");
        if (destino.exists()) {
            destino.delete();
        }
        destino.createNewFile();

        BufferedWriter writer = new BufferedWriter(new FileWriter(destino));
        writer.append(convertedObject.toString());
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