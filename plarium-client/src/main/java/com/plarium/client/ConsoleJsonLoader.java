package com.plarium.client;

/*
 * todo не забыть
 * 1. Демонизация
 * 1. Скрипты для запуска/остановки демона
 */

/*
 * todo особенности
 * 1. В читаемом файле хранится 1 json на строке.
 * 1.
 */

import com.plarium.client.arguments.ArgumentParser;
import com.plarium.client.arguments.Arguments;

import java.nio.file.Files;
import java.nio.file.Path;

public class ConsoleJsonLoader {

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

        new EventHandler(pathToListenTo, arguments.getBatchSize()).handle();
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
