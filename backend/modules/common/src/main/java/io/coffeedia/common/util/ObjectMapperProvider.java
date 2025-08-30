package io.coffeedia.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperProvider {

    private ObjectMapperProvider() {
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static ObjectMapper getInstance() {
        return OBJECT_MAPPER;
    }
}
