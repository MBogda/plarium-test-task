package com.plarium.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class EventHandler {

    private Logger logger = Logger.getLogger(PathListener.class.getName());
    private FileReader fileReader;
    private PathListener pathListener;

    private Path parentDirectory;
    private int batchSize;
    private Path createdFile;

    public EventHandler(Path parentDirectory, int batchSize) {
        this.parentDirectory = parentDirectory;
        this.batchSize = batchSize;
        this.pathListener = new PathListener(parentDirectory);
    }

    public void handle() {
        try {
            // todo: process existing files, not only new
            pathListener.listenDirectory(watchEvent -> {
                try {
                    if (initEventHandling(watchEvent)) {
                        readAndSend();
                    }
                } catch (IOException | InterruptedException e) {    // todo: think about exceptions handling
                    logger.log(Level.SEVERE, "Error during processing file.", e);
                }
            }, StandardWatchEventKinds.ENTRY_CREATE);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error during listening directory " + parentDirectory, e);
            System.exit(-1);
        }
    }

    public boolean initEventHandling(WatchEvent<?> watchEvent) throws IOException {
        if (watchEvent.kind() == StandardWatchEventKinds.OVERFLOW) {
            logger.severe("OVERFLOW event is received!");
            return false;
        }

        @SuppressWarnings("unchecked")
        WatchEvent<Path> pathWatchEvent = (WatchEvent<Path>) watchEvent;
        Path newFileName = pathWatchEvent.context();
        createdFile = parentDirectory.resolve(newFileName);

        if (!Files.isReadable(createdFile)) {
            logger.info("File " + createdFile + " is created, but it's not readable - ignored.");
            return false;
        } else if (!Files.isRegularFile(createdFile)) {
            logger.info("File " + createdFile + " is created, but it's not a regular file - ignored.");
            return false;
        }
        logger.info("File " + createdFile + " is created - start processing.");
        fileReader = new FileReader(batchSize, createdFile);
        return true;
    }

    public List<String> getNextBatch() throws IOException {
        List<String> jsons = fileReader.fetchNext();
        if (jsons.size() < batchSize) {
            Files.delete(createdFile);
            logger.info("Finish processing file " + createdFile + " Delete it.");
        }
        return jsons;
    }

    public void readAndSend() throws IOException, InterruptedException {
        List<String> jsonBatch;
        do {
            jsonBatch = getNextBatch();
            List<String> filteredBatch = jsonBatch.stream()
                    .filter(Verifier::verifyFormat)
                    .collect(Collectors.toList());
            PlariumHttpClient.sendBatch(filteredBatch);
        } while (jsonBatch.size() >= batchSize);
    }
}
