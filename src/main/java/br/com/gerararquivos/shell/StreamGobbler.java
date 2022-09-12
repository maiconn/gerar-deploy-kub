package br.com.gerararquivos.shell;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

public class StreamGobbler implements Runnable {
    private InputStream inputStream;
    private InputStream errorInputStream;
    private Consumer<String> consumer;

    public StreamGobbler(InputStream inputStream, InputStream errorInputStream, Consumer<String> consumer) {
        this.inputStream = inputStream;
        this.errorInputStream = errorInputStream;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        new BufferedReader(new InputStreamReader(inputStream)).lines()
                .forEach(consumer);
        new BufferedReader(new InputStreamReader(errorInputStream)).lines()
                .forEach(consumer);
    }
}
