package com.plarium.client;

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
import java.util.stream.Collectors;

public class ConsoleJsonLoader {

    private static Logger logger = Logger.getLogger(ConsoleJsonLoader.class.getName());

    public static void main(String[] args) {
        if (args.length < 2) {
            usage();
        }
        Path pathToListenTo = Path.of(args[0]);
        int batchSize = Integer.parseInt(args[1]);
        if (!Files.exists(pathToListenTo)) {
            notExist(pathToListenTo);
        } else if (!Files.isReadable(pathToListenTo)) {
            notReadable(pathToListenTo);
        } else if (!Files.isDirectory(pathToListenTo)) {
            notDirectory(pathToListenTo);
        }

        PathListener pathListener = new PathListener(pathToListenTo);
        EventHandler eventHandler = new EventHandler(pathToListenTo, batchSize);
        try {
            // todo: process existing files, not only new
            pathListener.listenDirectory(watchEvent -> {
                try {
                    if (eventHandler.initEventHandling(watchEvent)) {
                        List<String> jsonBatch;
                        do {
                            jsonBatch = eventHandler.getNextBatch();
                            List<String> filteredBatch = jsonBatch.stream()
                                    .filter(s -> !s.isBlank())
                                    .filter(s -> true)     // todo: check type existence and json format
                                    .collect(Collectors.toList())
                            ;
                            PlariumHttpClient.sendBatch(filteredBatch);
                        } while (jsonBatch.size() >= batchSize);    // todo: think and refactor
                    }
                } catch (IOException | InterruptedException e) {    // todo: think about exceptions handling
                    logger.log(Level.SEVERE, "Error during processing file.", e);
                }
            });
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error during listening directory " + pathToListenTo, e);
        }
    }

    private static void usage() {
        logger.severe("No argument is provided.\nUsage: java ConsoleJsonLoader path_to_listen_to");    // todo: move to separate file
        // todo: not hardcode class name
        System.exit(-1);
    }

    private static void notDirectory(Path pathToListenTo) {
        logger.severe("Provided path " + pathToListenTo + " is not a directory.\nExit.");
        System.exit(-1);
    }

    private static void notReadable(Path pathToListenTo) {
        logger.severe("Provided path " + pathToListenTo + " is not readable.\nExit.");
        System.exit(-1);
    }

    private static void notExist(Path pathToListenTo) {
        logger.severe("Provided path " + pathToListenTo + " is not exist.\nExit.");
        System.exit(-1);
    }
}
