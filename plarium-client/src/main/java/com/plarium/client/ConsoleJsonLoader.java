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

import com.plarium.client.arguments.ArgumentParser;
import com.plarium.client.arguments.Arguments;

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
        ArgumentParser argumentParser = new ArgumentParser();
        Arguments arguments = argumentParser.parse(args);
        Path pathToListenTo = Path.of(arguments.getPathToListenTo());
        if (!Files.exists(pathToListenTo)) {
            notExist(pathToListenTo);
        } else if (!Files.isReadable(pathToListenTo)) {
            notReadable(pathToListenTo);
        } else if (!Files.isDirectory(pathToListenTo)) {
            notDirectory(pathToListenTo);
        }

        PathListener pathListener = new PathListener(pathToListenTo);
        EventHandler eventHandler = new EventHandler(pathToListenTo, arguments.getBatchSize());
        try {
            // todo: process existing files, not only new
            pathListener.listenDirectory(watchEvent -> {
                try {
                    if (eventHandler.initEventHandling(watchEvent)) {
                        List<String> jsonBatch;
                        do {
                            jsonBatch = eventHandler.getNextBatch();
                            List<String> filteredBatch = jsonBatch.stream()
                                    .filter(Verifier::verifyFormat)
                                    .collect(Collectors.toList());
                            PlariumHttpClient.sendBatch(filteredBatch);
                        } while (jsonBatch.size() >= arguments.getBatchSize());    // todo: think and refactor
                    }
                } catch (IOException | InterruptedException e) {    // todo: think about exceptions handling
                    logger.log(Level.SEVERE, "Error during processing file.", e);
                }
            });
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error during listening directory " + pathToListenTo, e);
        }
    }

    private static void notDirectory(Path pathToListenTo) {
        System.out.println("Provided path " + pathToListenTo + " is not a directory.\nExit.");
        System.exit(-1);
    }

    private static void notReadable(Path pathToListenTo) {
        System.out.println("Provided path " + pathToListenTo + " is not readable.\nExit.");
        System.exit(-1);
    }

    private static void notExist(Path pathToListenTo) {
        System.out.println("Provided path " + pathToListenTo + " is not exist.\nExit.");
        System.exit(-1);
    }
}
