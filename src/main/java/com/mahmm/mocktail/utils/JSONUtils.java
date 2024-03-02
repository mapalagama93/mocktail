package com.mahmm.mocktail.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtils {

    public static ObjectMapper mapper = new ObjectMapper();

    public static String toStr(Object t) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(t);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    public static <T> T toObj(String s, TypeReference<T> type) {
        try {
            return mapper.readValue(s, type);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

}
