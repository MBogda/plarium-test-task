package com.plarium.service;

/**
 * Класс с константами приложения.
 */
public class Constants {
    /**
     * Адрес страницы с "индексом" (просто страница, успешно отвечающая на GET запрос).
     */
    public static final String INDEX_ENTRY_POINT = "/";
    /**
     * Адрес страницы для POST запроса загрузки JSONа.
     */
    public static final String UPLOAD_JSON_ENTRY_POINT = "/upload_json";
    /**
     * Поле "тип" в получаемых JSON объектах.
     */
    public static final String TYPE_KEY = "type";
    /**
     * Директория, куда сохраняются полученные JSON объекты.
     */
    public static final String ROOT_FOLDER = "/root";
    /**
     * Формат записи даты для создаваемой директории, содержащей текущую дату.
     */
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    private Constants() {}
}
