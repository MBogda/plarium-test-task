package com.plarium.service;

import com.plarium.service.helpers.FilesSaver;
import com.plarium.service.helpers.TypeExtractor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Application.class})
public class ControllerTest {

    @Autowired
    private TypeExtractor typeExtractor;
    private FilesSaver filesSaver;
    private Controller controller;

    private Map<String, StringWriter> stringStringWriterMap;

    @Before
    public void before() throws IOException {
        filesSaver = Mockito.spy(new FilesSaver());
        controller = new Controller(typeExtractor, filesSaver);
        stringStringWriterMap = new HashMap<>();
        doCallRealMethod().when(filesSaver).saveTypedObjects(any());
        doCallRealMethod().when(filesSaver).setDate(any());
        doNothing().when(filesSaver).createDirectories(any());
        doAnswer(invocation -> {
                    Path path = invocation.getArgumentAt(0, Path.class);
                    return stringStringWriterMap.computeIfAbsent(
                            path.getName(path.getNameCount() - 2).toString(),
                            s -> new StringWriter());
                }).when(filesSaver).getFileWriter(any());
    }

    @Test
    public void index() {
        String result = controller.index();
        assertThat(result, containsString("It works!"));
        assertThat(result, containsString("/upload_json"));
    }

    @Test
    public void uploadJson_singleJson() {
        ResponseEntity<String> response = controller.uploadJson(List.of(Map.of("type", "log")));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));
        assertThat(response.getBody(), containsString("Success!"));
        assertThat(stringStringWriterMap.get("log").toString(), equalTo("{\"type\":\"log\"}\n"));
    }

    @Test
    public void uploadJson_singleJsonWithSeveralFields() {
        ResponseEntity<String> response = controller.uploadJson(List.of(
                Map.of("type", "log", "field2", "value2", "xx", "y")));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));
        assertThat(response.getBody(), containsString("Success!"));
        assertThat(stringStringWriterMap.get("log").toString(),
                anyOf(
                        equalTo("{\"type\":\"log\",\"field2\":\"value2\",\"xx\":\"y\"}\n"),
                        equalTo("{\"type\":\"log\",\"xx\":\"y\",\"field2\":\"value2\"}\n"),
                        equalTo("{\"field2\":\"value2\",\"type\":\"log\",\"xx\":\"y\"}\n"),
                        equalTo("{\"field2\":\"value2\",\"xx\":\"y\",\"type\":\"log\"}\n"),
                        equalTo("{\"xx\":\"y\",\"type\":\"log\",\"field2\":\"value2\"}\n"),
                        equalTo("{\"xx\":\"y\",\"field2\":\"value2\",\"type\":\"log\"}\n")
                ));
    }

    @Test
    public void uploadJson_severalJsonsWithSameType() {
        ResponseEntity<String> response = controller.uploadJson(List.of(
                Map.of("type", "log"),
                Map.of("type", "log"),
                Map.of("type", "log"),
                Map.of("type", "log")));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));
        assertThat(response.getBody(), containsString("Success!"));
        assertThat(stringStringWriterMap.get("log").toString(),
                equalTo("{\"type\":\"log\"}\n{\"type\":\"log\"}\n{\"type\":\"log\"}\n{\"type\":\"log\"}\n"));
    }

    @Test
    public void uploadJson_severalJsonsWithDifferentTypes() {
        ResponseEntity<String> response = controller.uploadJson(List.of(
                Map.of("type", "log", "num", "0"),
                Map.of("type", "type2", "num", "1"),
                Map.of("type", "log", "num", "2"),
                Map.of("type", "single_type", "num", "3"),
                Map.of("type", "type2", "num", "4"),
                Map.of("type", "log", "num", "5")
        ));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));
        assertThat(response.getBody(), containsString("Success!"));
        assertThat(stringStringWriterMap.get("single_type").toString(),
                anyOf(
                        equalTo("{\"type\":\"single_type\",\"num\":\"3\"}\n"),
                        equalTo("{\"num\":\"3\",\"type\":\"single_type\"}\n")
                ));
        assertThat(stringStringWriterMap.get("type2").toString(),
                anyOf(
                        equalTo("{\"type\":\"type2\",\"num\":\"1\"}\n{\"type\":\"type2\",\"num\":\"4\"}\n"),
                        equalTo("{\"type\":\"type2\",\"num\":\"1\"}\n{\"num\":\"4\",\"type\":\"type2\"}\n"),
                        equalTo("{\"num\":\"1\",\"type\":\"type2\"}\n{\"type\":\"type2\",\"num\":\"4\"}\n"),
                        equalTo("{\"num\":\"1\",\"type\":\"type2\"}\n{\"num\":\"4\",\"type\":\"type2\"}\n")
                ));
        assertThat(stringStringWriterMap.get("log").toString(),
                anyOf(
                        equalTo("{\"type\":\"log\",\"num\":\"0\"}\n{\"type\":\"log\",\"num\":\"2\"}\n"
                                + "{\"type\":\"log\",\"num\":\"5\"}\n"),
                        equalTo("{\"type\":\"log\",\"num\":\"0\"}\n{\"type\":\"log\",\"num\":\"2\"}\n"
                                + "{\"num\":\"5\",\"type\":\"log\"}\n"),
                        equalTo("{\"type\":\"log\",\"num\":\"0\"}\n{\"num\":\"2\",\"type\":\"log\"}\n"
                                + "{\"type\":\"log\",\"num\":\"5\"}\n"),
                        equalTo("{\"type\":\"log\",\"num\":\"0\"}\n{\"num\":\"2\",\"type\":\"log\"}\n"
                                + "{\"num\":\"5\",\"type\":\"log\"}\n"),
                        equalTo("{\"num\":\"0\",\"type\":\"log\"}\n{\"type\":\"log\",\"num\":\"2\"}\n"
                                + "{\"type\":\"log\",\"num\":\"5\"}\n"),
                        equalTo("{\"num\":\"0\",\"type\":\"log\"}\n{\"type\":\"log\",\"num\":\"2\"}\n"
                                + "{\"num\":\"5\",\"type\":\"log\"}\n"),
                        equalTo("{\"num\":\"0\",\"type\":\"log\"}\n{\"num\":\"2\",\"type\":\"log\"}\n"
                                + "{\"type\":\"log\",\"num\":\"5\"}\n"),
                        equalTo("{\"num\":\"0\",\"type\":\"log\"}\n{\"num\":\"2\",\"type\":\"log\"}\n"
                                + "{\"num\":\"5\",\"type\":\"log\"}\n")
                ));
    }

    @Test
    public void uploadJson_severalRequests() {
        ResponseEntity<String> response = controller.uploadJson(List.of(Map.of("type", "log")));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));
        assertThat(response.getBody(), containsString("Success!"));
        response = controller.uploadJson(List.of(Map.of("type", "log")));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));
        assertThat(response.getBody(), containsString("Success!"));
        assertThat(stringStringWriterMap.get("log").toString(),
                equalTo("{\"type\":\"log\"}\n{\"type\":\"log\"}\n"));
    }

    @Test
    public void uploadJson_JsonWithoutType() {
        ResponseEntity<String> response = controller.uploadJson(List.of(Map.of("field", "log")));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(), containsString("JSON object at index 0 must contain \"type\" key."));
    }

    @Test
    public void uploadJson_JsonWithoutTypeAtSecondIndex() {
        ResponseEntity<String> response = controller.uploadJson(List.of(
                Map.of("type", "log"),
                Map.of("field", "log"),
                Map.of("type", "log")));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(), containsString("JSON object at index 1 must contain \"type\" key."));
    }

    @Test
    public void uploadJson_ErrorDuringWriting() throws IOException {
        doThrow(new IOException("IOEception")).when(filesSaver).saveTypedObjects(any());
        ResponseEntity<String> response = controller.uploadJson(List.of(Map.of("type", "log")));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(response.getBody(), containsString("Fail :( Please try again."));
    }
}
