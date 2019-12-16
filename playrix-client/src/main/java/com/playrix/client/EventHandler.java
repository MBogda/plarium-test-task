package com.playrix.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventHandler {

    private Logger logger = Logger.getLogger(PathListener.class.getName());
    private Path parentDirectory;
    private int batchSize;

    public EventHandler(Path parentDirectory, int batchSize) {
        this.parentDirectory = parentDirectory;
        this.batchSize = batchSize;
    }

    public Path processEvent0(WatchEvent<?> watchEvent) {
        if (watchEvent.kind() == StandardWatchEventKinds.OVERFLOW) {
            logger.severe("OVERFLOW event is received!");
            return null;
        }

        @SuppressWarnings("unchecked")
        WatchEvent<Path> pathWatchEvent = (WatchEvent<Path>) watchEvent;
        Path newFileName = pathWatchEvent.context();
        Path createdFile = parentDirectory.resolve(newFileName);

        if (!Files.isReadable(createdFile)) {
            logger.info("File " + createdFile + " is created, but it's not readable - ignored.");
            return null;
        } else if (!Files.isRegularFile(createdFile)) {
            logger.info("File " + createdFile + " is created, but it's not a regular file - ignored.");
            return null;
        }
        logger.info("File " + createdFile + " is created - start processing.");
        return createdFile;
    }

    public void processEvent(Path createdFile) {
        if (createdFile == null) return;
        try {
            FileReader fileReader = new FileReader(batchSize, createdFile);
            while (true) {
                List<String> jsons = fileReader.fetchNext();
                if (jsons.size() < batchSize) {
                    break;
                }
                // TODO: return jsons
                // todo: check type and json format
                // todo: send to service
                for (String json : jsons) {
                    System.out.println(json);
                }
            }
            Files.delete(createdFile);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error during processing file " + createdFile, e);
        } finally {
            logger.info("Finish processing file " + createdFile);
        }
    }
}
