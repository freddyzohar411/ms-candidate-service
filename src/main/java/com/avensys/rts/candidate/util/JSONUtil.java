package com.avensys.rts.candidate.util;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JSONUtil {

    public static String mergeJsonObjects(String... jsonObjects) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode mergedNode = objectMapper.createObjectNode();

            for (String jsonObject : jsonObjects) {
                JsonNode jsonNode = objectMapper.readTree(jsonObject);
                mergeJsonNode(mergedNode, jsonNode);
            }

            return objectMapper.writeValueAsString(mergedNode);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JsonNode mergeJsonNodes(List<JsonNode> jsonNodes) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode mergedNode = mapper.createObjectNode();

        for (JsonNode node : jsonNodes) {
            if (node.isObject()) {
                mergedNode.setAll((ObjectNode) node);
            } else {
                throw new IllegalArgumentException("Only JSON objects can be merged.");
            }
        }
        return mergedNode;
    }

    private static void mergeJsonNode(ObjectNode target, JsonNode source) {
        for (Iterator<Map.Entry<String, JsonNode>> it = source.fields(); it.hasNext();) {
            Map.Entry<String, JsonNode> entry = it.next();
            String fieldName = entry.getKey();
            JsonNode sourceValue = entry.getValue();

            JsonNode targetValue = target.get(fieldName);
            if (targetValue != null && targetValue.isObject() && sourceValue.isObject()) {
                // Recursive merge for nested JSON objects
                mergeJsonNode((ObjectNode) targetValue, sourceValue);
            } else {
                // Non-nested merge or overwrite
                target.set(fieldName, sourceValue);
            }
        }
    }

    public static void writeJsonToFile(String filePath, String fileName, JsonNode jsonNode)
            throws IOException, IOException {
        // Ensure the file path ends with a separator
        if (!filePath.endsWith(File.separator)) {
            filePath += File.separator;
        }

        // Create the file object
        File file = new File(filePath + fileName);

        // Initialize ObjectMapper instance
        ObjectMapper mapper = new ObjectMapper();

        // Writing to a file
        mapper.writeValue(file, jsonNode);
    }

    public static String writeToJsonString(Object obj) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }


    public static JsonNode convertObjectToJsonNode(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.valueToTree(obj);
    }


}
