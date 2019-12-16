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
    private FileReader fileReader;
    private Path createdFile;

    public EventHandler(Path parentDirectory, int batchSize) {
        this.parentDirectory = parentDirectory;
        this.batchSize = batchSize;
    }

    public void initEventHandling(WatchEvent<?> watchEvent) throws IOException {
        if (watchEvent.kind() == StandardWatchEventKinds.OVERFLOW) {
            logger.severe("OVERFLOW event is received!");
            return;
        }

        @SuppressWarnings("unchecked")
        WatchEvent<Path> pathWatchEvent = (WatchEvent<Path>) watchEvent;
        Path newFileName = pathWatchEvent.context();
        createdFile = parentDirectory.resolve(newFileName);

        if (!Files.isReadable(createdFile)) {
            logger.info("File " + createdFile + " is created, but it's not readable - ignored.");
            return;
        } else if (!Files.isRegularFile(createdFile)) {
            logger.info("File " + createdFile + " is created, but it's not a regular file - ignored.");
            return;
        }
        logger.info("File " + createdFile + " is created - start processing.");
        fileReader = new FileReader(batchSize, createdFile);
    }

    public List<String> getNextBatch() throws IOException {
        List<String> jsons = fileReader.fetchNext();
        if (jsons.size() < batchSize) {
            Files.delete(createdFile);
            logger.info("Finish processing file " + createdFile + " Delete it.");
        }
        return jsons;
    }
}
