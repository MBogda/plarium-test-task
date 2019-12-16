package com.playrix.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FileReader {

    private int batchSize;
    private BufferedReader reader;
    private List<String> lines;

    public FileReader(int batchSize, Path filePath) throws IllegalArgumentException, IOException {
        if (batchSize <= 0) {
            throw new IllegalArgumentException("batchSize must be greater than 0.");
        }
        this.batchSize = batchSize;
        reader = Files.newBufferedReader(filePath);
        lines = new ArrayList<>(batchSize);
    }

    public List<String> fetchNext() throws IOException {
        lines.clear();
        for (int i = 0; i < batchSize; i++) {
            String line = reader.readLine();
            if (line == null) {
                reader.close();
                break;
            }
            lines.add(line);
        }
        return lines;
    }
}
