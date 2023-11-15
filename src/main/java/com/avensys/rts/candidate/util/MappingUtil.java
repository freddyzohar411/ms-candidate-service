package com.avensys.rts.candidate.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class MappingUtil {
	/**
	 * This method is used to convert Object to Class
	 * Use to convert API client Httpresponse back to DTO class
	 * @param <T>
	 * @param body
	 * @param mappedDTO
	 * @return
	 */
	public static <T> T mapClientBodyToClass(Object body, Class<T> mappedDTO) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.convertValue(body, mappedDTO);
    }
	/**
	 * This method is used to convert JSONString to JsonNode
	 * @param jsonString
	 * @return
	 */
	public static JsonNode convertJSONStringToJsonNode(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            return objectMapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
	/**
	 * This method is used to convert JsonNode to JSONString
	 * @param jsonNode
	 * @return
	 */
	public static String convertJsonNodeToJSONString(JsonNode jsonNode) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            return objectMapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
	

}
