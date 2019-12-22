package com.plarium.service.helpers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plarium.service.Constants;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

@Component
public class FilesSaver {

    private SimpleDateFormat dateFormat;
    private ObjectMapper objectMapper;
    private String date;

    public FilesSaver() {
        dateFormat = new SimpleDateFormat(Constants.DATE_PATTERN);
        this.objectMapper = new ObjectMapper();
        this.date = dateFormat.format(new Date());
    }

    public void setDate(Date date) {
        this.date = dateFormat.format(date);
    }

    public void saveTypedObjects(Map<String, Collection<Map<String, String>>> objectsByType) throws IOException {
        initFoldersStructure(objectsByType);
        writeToFiles(objectsByType);
    }

    private void initFoldersStructure(Map<String, Collection<Map<String, String>>> objectsByType) throws IOException {
        for (String type : objectsByType.keySet()) {
            Path folder = createFilePath(type).getParent();
            if (!Files.exists(folder)) {
                createDirectories(folder);
            }
        }
    }

    private void writeToFiles(Map<String, Collection<Map<String, String>>> objectsByType) throws IOException {
        for (var entry : objectsByType.entrySet()) {
            String type = entry.getKey();
            Path file = createFilePath(type);
            Writer writer = getFileWriter(file);
            JsonGenerator jsonGenerator = objectMapper.getFactory().createGenerator(writer)
                    .setRootValueSeparator(new SerializedString(System.lineSeparator()));
            for (var jsonObject : entry.getValue()) {
                objectMapper.writeValue(jsonGenerator, jsonObject);
            }
            jsonGenerator.writeRaw(System.lineSeparator());
            jsonGenerator.close();
            writer.close();
        }
    }

    private Path createFilePath(String type) {
        return Path.of(Constants.ROOT_FOLDER, type, date);
    }

    public void createDirectories(Path folder) throws IOException {
        Files.createDirectories(folder);
    }

    public Writer getFileWriter(Path file) throws IOException {
        return Files.newBufferedWriter(file, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
    }
}
