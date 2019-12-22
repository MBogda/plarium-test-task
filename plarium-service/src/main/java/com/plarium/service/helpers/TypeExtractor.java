package com.plarium.service.helpers;

import com.plarium.service.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TypeExtractor {

    @Autowired
    public TypeExtractor() {}

    public Map<String, Collection<Map<String, String>>> extractTypes(List<Map<String, String>> jsonArray)
            throws IllegalArgumentException {
        Map<String, Collection<Map<String, String>>> objectsByType = new HashMap<>();
        for (int i = 0; i < jsonArray.size(); ++i) {
            Map<String, String> jsonObject = jsonArray.get(i);
            String type = jsonObject.get(Constants.TYPE_KEY);
            if (type == null) {
                throw new IllegalArgumentException("JSON object at index " + i + " must contain \""
                        + Constants.TYPE_KEY + "\" key.");
            }
            Collection<Map<String, String>> sameTypeObjects = objectsByType
                    .computeIfAbsent(type, k -> new ArrayList<>());
            sameTypeObjects.add(jsonObject);
        }
        return objectsByType;
    }
}
