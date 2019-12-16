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

public class PlayrixClient {
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
                Path createdFile = eventHandler.processEvent0(watchEvent);
                eventHandler.processEvent(createdFile);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void usage() {
        System.err.println("No argument is provided.\nUsage: java PlayrixClient path_to_listen_to");    // todo: move to separate file
        // todo: not hardcode class name
        System.exit(-1);
    }

    private static void notDirectory(Path pathToListenTo) {
        System.err.println("Provided path " + pathToListenTo + " is not a directory.\nExit.");
        System.exit(-1);
    }
}
