package com.plarium.client;

/*
 * todo не забыть
 * 1. Демонизация
 * 1. Скрипты для запуска/остановки демона
 */

/*
 * todo особенности
 * 1. В читаемом файле хранится 1 json на строке.
 * 1. Не заиспользовал потоки, т.к. не было требования многопоточной обработки данных (+ не успел).
 * 1. Не добавил тесты, т.к. не было требования + нужны доп. либы + не успел (если б успел, то доабвил бы).
 */

import com.plarium.client.arguments.Arguments;
import com.plarium.client.arguments.ArgumentsParser;
import com.plarium.client.arguments.ArgumentsVerifier;

public class ConsoleJsonLoader {

    public static void main(String[] args) {
        Arguments arguments = new ArgumentsParser().parse(args);

        new ArgumentsVerifier(arguments).verity();

        new EventHandler(arguments).handle();
    }
}
