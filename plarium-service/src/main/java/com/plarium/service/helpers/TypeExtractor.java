package com.plarium.service.helpers;

import com.plarium.service.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class TypeExtractor {

    @Autowired
    public TypeExtractor() {}

    public Map<String, Collection<Map<String, String>>> extractTypes(Collection<Map<String, String>> jsonArray) {
        Map<String, Collection<Map<String, String>>> objectsByType = new HashMap<>();
        for (Map<String, String> jsonObject : jsonArray) {
            String type = jsonObject.get(Constants.TYPE_KEY);
            if (type == null) {
                // todo? error objects list + todo? object index in array
                throw new IllegalArgumentException("JSON object must contain \"" + Constants.TYPE_KEY + "\" key");
            }
            Collection<Map<String, String>> sameTypeObjects = objectsByType
                    .computeIfAbsent(type, k -> new ArrayList<>());
            sameTypeObjects.add(jsonObject);
        }
        return objectsByType;
    }
}
