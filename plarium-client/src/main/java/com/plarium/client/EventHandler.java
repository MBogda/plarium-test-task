package com.plarium.client;

import com.plarium.client.arguments.Arguments;

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
    private PathListener pathListener;
    private PlariumHttpClient plariumHttpClient;
    private FileReader fileReader;

    private Path pathToListenTo;
    private int batchSize;
    private Path processingFile;

    public EventHandler(Arguments arguments) {
        this.pathToListenTo = Path.of(arguments.getPathToListenTo());
        this.batchSize = arguments.getBatchSize();
        this.pathListener = new PathListener(pathToListenTo);
        this.plariumHttpClient = new PlariumHttpClient(arguments.getServiceUrl(), arguments.getUploadPath(),
                arguments.getRetriesCount(), arguments.getTimeoutInSeconds());
    }

    public void handle() {
        try {
            logger.info("Scanning path " + pathToListenTo + " for existing files.");
            Files.list(pathToListenTo).forEach(this::handleExistingFile);
            // todo: remove gap between handling existing files and starting new events processing by threads
            pathListener.listenDirectory(this::handleWatchEvent, StandardWatchEventKinds.ENTRY_CREATE);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error during listening directory " + pathToListenTo, e);
            System.exit(-1);
        }
    }

    private void handleExistingFile(Path child) {
        try {
            processingFile = child;
            if (initSuccessful()) {
                readAndSend();
            }
        } catch (IOException | InterruptedException | IllegalStateException e) {
            logger.log(Level.SEVERE, "Error during processing file " + processingFile, e);
        }
    }

    private void handleWatchEvent(WatchEvent<?> watchEvent) {
        try {
            if (watchEventInitSuccessful(watchEvent)) {
                readAndSend();
            }
        } catch (IOException | InterruptedException | IllegalStateException e) {
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
        processingFile = pathToListenTo.resolve(newFileName);

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

    private void readAndSend() throws IOException, InterruptedException, IllegalStateException {
        List<String> jsonBatch;
        while (true) {
            jsonBatch = fileReader.fetchNext(JsonVerifier::verifyFormat);
            if (!jsonBatch.isEmpty()) {
                plariumHttpClient.sendBatch(jsonBatch);
            }
            if (jsonBatch.size() < batchSize) {
                Files.delete(processingFile);
                logger.info("Finish processing file " + processingFile + " and delete it.");
                break;
            }
        }
    }
}
