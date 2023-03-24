package com.zengshen.rabbit.common.serializer.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.zengshen.rabbit.common.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Type;

@Slf4j
public class JacksonSerializer implements Serializer {
    private static final ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.disable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    private final JavaType type;

    public JacksonSerializer(JavaType type) {
        this.type = type;
    }
    public JacksonSerializer(Type type) {
        this.type = mapper.getTypeFactory().constructType(type);
    }
    public static JacksonSerializer createParametricType(Type type) {
        return new JacksonSerializer(mapper.getTypeFactory().constructType(type));
    }


    @Override
    public byte[] serializerRaw(Object data) {
        try {
            return mapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            log.info("序列化出错了");
            e.printStackTrace();
        }
        return  null;
    }

    @Override
    public String serialize(Object data) {
        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.info("序列化出错了");
            e.printStackTrace();
        }
        return  null;
    }

    @Override
    public <T> T deserialize(String content) {
        try {
            return mapper.readValue(content, type);
        } catch (JsonProcessingException e) {
            log.info("反序列化出错了");
            e.printStackTrace();
        }
        return  null;
    }

    @Override
    public <T> T deserialize(byte[] content) {
        try {
            return mapper.readValue(content, type);
        } catch (IOException e) {
            log.info("反序列化出错了");
            e.printStackTrace();
        }
        return  null;
    }
}
