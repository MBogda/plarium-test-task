package com.plarium.service;

import com.plarium.service.helpers.FilesSaver;
import com.plarium.service.helpers.TypeExtractor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Application.class})
public class ControllerTest {

    @Autowired
    private TypeExtractor typeExtractor;
    private FilesSaver filesSaver;
    private Controller controller;

    private StringWriter stringWriter;

    @Before
    public void before() {
        filesSaver = Mockito.spy(new FilesSaver());
        controller = new Controller(typeExtractor, filesSaver);
        try {
            doCallRealMethod().when(filesSaver).saveTypedObjects(any());
            doCallRealMethod().when(filesSaver).setDate(any());
            doReturn(null).when(filesSaver).createDirectories(any());
            stringWriter = new StringWriter(20);
            doReturn(stringWriter).when(filesSaver).getFileWriter(any());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void index() {
        String result = controller.index();
        assertThat(result, containsString("It works!"));
        assertThat(result, containsString("/upload_json"));
    }

    @Test
    public void uploadJson() {
        String result = controller.uploadJson(List.of(Map.of("type", "log")));
        assertThat(result, containsString("Success!"));
        assertThat(stringWriter.toString(), equalTo("{\"type\":\"log\"}\n"));
    }
}
