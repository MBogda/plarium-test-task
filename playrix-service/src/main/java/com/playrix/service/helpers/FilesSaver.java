package com.playrix.service.helpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import static com.playrix.service.Constants.ROOT_FOLDER;

public class FilesSaver {

    String date;    // todo: date as parameter, not member

    public FilesSaver(String pattern, Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        this.date = dateFormat.format(date);
    }

    public boolean saveTypedObjects(Map<String, Collection<Map<String, String>>> objectsByType) {
        if (!initFoldersStructure(objectsByType)) {
            return false;
        }
        for (var entry : objectsByType.entrySet()) {
            String type = entry.getKey();
            for (var jsonObject : entry.getValue()) {
                Path file = createFilePath(type);
                try {
                    // todo: write to buffer
                    // todo: convert map to json, not using toString
                    Files.writeString(file, jsonObject.toString() + "\n", StandardOpenOption.APPEND,
                            StandardOpenOption.CREATE);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
//                    throw e;    // todo
                }
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
        return Path.of(ROOT_FOLDER, type, date);
    }
}
