package br.com.gerararquivos.trocararquivoswagger;

import br.com.gerararquivos.Main;
import org.apache.maven.surefire.shade.org.apache.maven.shared.utils.io.DirectoryScanner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class TrocarArquivoSwagger {
    public static void modificarArquivoOpenApiConfig(String workspace) throws IOException {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(new String[]{"**/OpenApiConfig.java"});
        String basedir = workspace + "/src/main/java/";
        scanner.setBasedir(basedir);
        scanner.setCaseSensitive(true);
        scanner.scan();
        String[] files = scanner.getIncludedFiles();
        if (files.length > 0) {
            String includedFile = scanner.getIncludedFiles()[0];
            File file = new File(basedir + includedFile);
            Scanner scan = new Scanner(new FileInputStream(file));
            String pacote = scan.nextLine();
            scan.close();
            FileWriter fileWriter = new FileWriter(file, false);
            fileWriter.write(pacote + "\n");
            scan = new Scanner(Main.class.getClassLoader().getResourceAsStream("OpenApiConfig.java"));
            while (scan.hasNextLine()) {
                fileWriter.write(scan.nextLine());
                fileWriter.write("\n");
            }
            fileWriter.close();
            scan.close();
        }
    }
}
