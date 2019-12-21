package com.plarium.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Application.class})
public class ControllerTest {

    @Autowired
    private Controller controller;

    @Test
    public void index() {
        String result = controller.index();
        assertThat(result, containsString("It works!"));
        assertThat(result, containsString("/upload_json"));
    }

    @Test
    public void uploadJson() {
        // todo: FileSaver as dependency (and so, mocking)
//        String result = controller.uploadJson(List.of(Map.of("type", "log")));
    }
}
