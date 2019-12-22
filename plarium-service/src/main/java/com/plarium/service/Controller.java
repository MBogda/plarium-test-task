package com.plarium.service;

import com.plarium.service.helpers.FilesSaver;
import com.plarium.service.helpers.TypeExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/*
 * todo: Особенности
 * 1. Объект записывается в преобразованном виде.
 * 1. ...
 */

/*
 * todo: Не забыть
 * 1. Дока
 * 1. Тесты
 * 1. Readme.md
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

    @ResponseBody
    @GetMapping(Constants.INDEX_ENTRY_POINT)
    public String index() {
        return "It works! Upload your json on " + Constants.UPLOAD_JSON_ENTRY_POINT + " using HTTP POST request.";
    }

    @PostMapping(Constants.UPLOAD_JSON_ENTRY_POINT)
    public String uploadJson(@RequestBody List<Map<String, String>> jsonArray) {
        var objectsByType = typeExtractor.extractTypes(jsonArray);  // todo: not server error, but bad request
        filesSaver.setDate(new Date());
        try {
            filesSaver.saveTypedObjects(objectsByType);
            return "Success!";
        } catch (IOException e) {
            e.printStackTrace();
//            throw e;
//            throw new HttpServerErrorException.InternalServerError("Fail :( Please try again", "", HttpHeaders.EMPTY, null, null);
            return "Fail :(";   // todo: status code
//            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
