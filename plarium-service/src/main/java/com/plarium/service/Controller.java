package com.plarium.service;

import com.plarium.service.helpers.FilesSaver;
import com.plarium.service.helpers.TypeExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Единственный контроллер веб-сервиса.
 */
@RestController
public class Controller {

    private TypeExtractor typeExtractor;
    private FilesSaver filesSaver;

    @Autowired
    public Controller(TypeExtractor typeExtractor, FilesSaver filesSaver) {
        this.typeExtractor = typeExtractor;
        this.filesSaver = filesSaver;
    }

    /**
     * Страница, успешно отвечающая на GET-запрос.
     * @return Сообщение со статусом 200 OK.
     */
    @ResponseBody
    @GetMapping(Constants.INDEX_ENTRY_POINT)
    public String index() {
        return "It works! Upload your json on " + Constants.UPLOAD_JSON_ENTRY_POINT + " using HTTP POST request.";
    }

    /**
     * Страница для загрузки данных POST запросом в формате JSON array.
     * @param jsonArray десериализованные загружаемые данные.
     * @return 201 Created в случае успешной записи переданных объектов;
     *         400 Bad Request в случае ошибки в переданных данных;
     *         500 Internal Server Error в случае внутренних ошибок (например, нет доступа к папке записи). todo
     */
    @PostMapping(Constants.UPLOAD_JSON_ENTRY_POINT)
    public ResponseEntity<String> uploadJson(@RequestBody List<Map<String, String>> jsonArray) {
        Map<String, Collection<Map<String, String>>> objectsByType;
        try {
            objectsByType = typeExtractor.extractTypes(jsonArray);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        filesSaver.setDate(new Date());
        try {
            filesSaver.saveTypedObjects(objectsByType);
            return new ResponseEntity<>("Success!", HttpStatus.CREATED);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Fail :( Please try again.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
