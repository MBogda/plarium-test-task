package com.plarium.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс с точкой входа в программу.
 */
@SpringBootApplication
public class Application {

    /**
     * Точка входа в программу, запускает веб-сервис.
     * @param args аргументы командной строки.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
