package com.plarium.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

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

    public List<String> fetchNext(Predicate<String> filterPredicate) throws IOException {
        lines.clear();
        while (lines.size() < batchSize) {
            String line = reader.readLine();
            if (line == null) {
                reader.close();
                break;
            }
            if (filterPredicate.test(line)) {
                lines.add(line);
            }
        }
        return lines;
    }
}
