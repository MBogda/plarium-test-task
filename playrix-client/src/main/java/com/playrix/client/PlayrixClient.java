package com.playrix.client;

/*
 * todo не забыть
 * 1. Демонизация
 * 1. Скрипты для запуска/остановки демона
 * 1. ArgumentsParser class
 * 1. Logging
 */

/*
 * todo особенности
 * 1. В читаемом файле хранится 1 json на строке.
 * 1.
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayrixClient {

    private static Logger logger = Logger.getLogger(PlayrixClient.class.getName());

    public static void main(String[] args) {
        if (args.length < 2) {
            usage();
        }
        Path pathToListenTo = Path.of(args[0]);
        int batchSize = Integer.parseInt(args[1]);
        if (!Files.isDirectory(pathToListenTo)) {
            notDirectory(pathToListenTo);
        }
        PathListener pathListener = new PathListener(pathToListenTo);
        EventHandler eventHandler = new EventHandler(pathToListenTo, batchSize);
        try {
            // todo: process existing files, not only new
            pathListener.listenDirectory(watchEvent -> {
                try {
                    eventHandler.initEventHandling(watchEvent);
                    List<String> jsonBatch;
                    do {
                        jsonBatch = eventHandler.getNextBatch();
                        for (String json : jsonBatch) {
                            // todo: check type and json format, filter empty strings
                        }
                        PlayrixHttpClient.sendBatch(jsonBatch);
                    } while (jsonBatch.size() >= batchSize);    // todo: think and refactor
                } catch (IOException | InterruptedException e) {    // todo: think about exceptions handling
                    logger.log(Level.SEVERE, "Error during processing file.", e);
                }
            });
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error during listening directory " + pathToListenTo, e);
        }
    }

    private static void usage() {
        logger.severe("No argument is provided.\nUsage: java PlayrixClient path_to_listen_to");    // todo: move to separate file
        // todo: not hardcode class name
        System.exit(-1);
    }

    private static void notDirectory(Path pathToListenTo) {
        logger.severe("Provided path " + pathToListenTo + " is not a directory.\nExit.");
        System.exit(-1);
    }
}
