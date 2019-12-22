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
    private PathListener pathListener;
    private FileReader fileReader;

    private Path parentDirectory;
    private int batchSize;
    private Path processingFile;

    public EventHandler(Path parentDirectory, int batchSize) {
        this.parentDirectory = parentDirectory;
        this.batchSize = batchSize;
        this.pathListener = new PathListener(parentDirectory);
    }

    public void handle() {
        try {
            Files.list(parentDirectory).forEach(this::handleExistingFile);
            // todo: remove gap between handling existing files and starting new events processing by threads
            pathListener.listenDirectory(this::handleWatchEvent, StandardWatchEventKinds.ENTRY_CREATE);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error during listening directory " + parentDirectory, e);
            System.exit(-1);
        }
    }

    private void handleExistingFile(Path child) {
        try {
            processingFile = child;
            if (initSuccessful()) {
                readAndSend();
            }
        } catch (IOException | InterruptedException e) {
            logger.log(Level.SEVERE, "Error during processing file " + processingFile, e);
        }
    }

    private void handleWatchEvent(WatchEvent<?> watchEvent) {
        try {
            if (watchEventInitSuccessful(watchEvent)) {
                readAndSend();
            }
        } catch (IOException | InterruptedException e) {
            logger.log(Level.SEVERE, "Error during processing file " + processingFile, e);
        }
    }

    private boolean watchEventInitSuccessful(WatchEvent<?> watchEvent) throws IOException {
        if (watchEvent.kind() == StandardWatchEventKinds.OVERFLOW) {
            logger.severe("OVERFLOW event is received!");
            return false;
        }

        @SuppressWarnings("unchecked")
        WatchEvent<Path> pathWatchEvent = (WatchEvent<Path>) watchEvent;
        Path newFileName = pathWatchEvent.context();
        processingFile = parentDirectory.resolve(newFileName);

        return initSuccessful();
    }

    private boolean initSuccessful() throws IOException {
        if (!Files.isReadable(processingFile)) {
            logger.info("File " + processingFile + " is detected, but it's not readable - ignored.");
            return false;
        } else if (!Files.isRegularFile(processingFile)) {
            logger.info("File " + processingFile + " is detected, but it's not a regular file - ignored.");
            return false;
        }
        logger.info("File " + processingFile + " is detected, start processing.");
        fileReader = new FileReader(batchSize, processingFile);
        return true;
    }

    private void readAndSend() throws IOException, InterruptedException {
        List<String> jsonBatch;
        do {
            jsonBatch = getNextBatch();
            List<String> filteredBatch = jsonBatch.stream()
                    .filter(Verifier::verifyFormat)
                    .collect(Collectors.toList());
            PlariumHttpClient.sendBatch(filteredBatch);
        } while (jsonBatch.size() >= batchSize);
    }

    private List<String> getNextBatch() throws IOException {
        // todo? think more about reading stop (get rid of extra empty lists)
        List<String> jsons = fileReader.fetchNext();
        if (jsons.size() < batchSize) {
            Files.delete(processingFile);
            logger.info("Finish processing file " + processingFile + " and delete it.");
        }
        return jsons;
    }
}
