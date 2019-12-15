package com.playrix.service;

import com.playrix.service.helpers.FilesSaver;
import com.playrix.service.helpers.TypeExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.playrix.service.Constants.DATE_PATTERN;

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

    @Autowired
    public Controller(TypeExtractor typeExtractor) {
        this.typeExtractor = typeExtractor;
    }

    @ResponseBody
    @GetMapping("/")
    public String index() {
//    public ResponseEntity<String> index() {
        return "It works!";

//        return "It works! Upload your json on " + "" + " using HTTP POST request.";     // todo?

//        return new ResponseEntity<String>("It works!", HttpStatus.OK);
    }

    @PostMapping("/upload_json")
    public String uploadJson(@RequestBody List<Map<String, String>> jsonArray) {
        var objectsByType = typeExtractor.extractTypes(jsonArray);
        FilesSaver filesSaver = new FilesSaver(DATE_PATTERN, new Date());
        boolean state = filesSaver.saveTypedObjects(objectsByType);
        return state ? "Success!" : "Fail :(";
    }
}
