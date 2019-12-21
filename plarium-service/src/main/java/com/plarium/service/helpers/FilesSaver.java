package com.plarium.service.helpers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plarium.service.Constants;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class FilesSaver {

    String date;    // todo: date as parameter, not member
    ObjectMapper objectMapper;

    public FilesSaver(String pattern, Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        this.date = dateFormat.format(date);
        this.objectMapper = new ObjectMapper();
    }

    // todo: not boolean, but exceptions
    public boolean saveTypedObjects(Map<String, Collection<Map<String, String>>> objectsByType) {
        if (!initFoldersStructure(objectsByType)) {
            return false;
        }
        for (var entry : objectsByType.entrySet()) {
            String type = entry.getKey();
            Path file = createFilePath(type);
            try {
                Writer writer = Files.newBufferedWriter(file, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
                JsonGenerator jsonGenerator = objectMapper.getFactory().createGenerator(writer);
                for (var jsonObject : entry.getValue()) {
                    // todo? if I need generator?
                    // todo: remove extra space
                    objectMapper.writeValue(jsonGenerator, jsonObject);
                    jsonGenerator.writeRaw(System.lineSeparator());
                }
                jsonGenerator.close();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
//                    throw e;    // todo
            }
        }
        return true;
    }

    private boolean initFoldersStructure(Map<String, Collection<Map<String, String>>> objectsByType) {
        for (String type : objectsByType.keySet()) {
            Path folder = createFilePath(type).getParent();
            if (!Files.exists(folder)) {
                try {
                    Files.createDirectories(folder);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

    private Path createFilePath(String type) {
        return Path.of(Constants.ROOT_FOLDER, type, date);
    }
}
