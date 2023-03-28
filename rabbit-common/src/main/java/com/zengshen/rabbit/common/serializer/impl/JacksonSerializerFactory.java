package com.zengshen.rabbit.common.serializer.impl;

import com.zengshen.rabbit.api.Message;
import com.zengshen.rabbit.common.serializer.Serializer;
import com.zengshen.rabbit.common.serializer.SerializerFactory;

/**
 * @author word
 */
public class JacksonSerializerFactory implements SerializerFactory {

    public static final SerializerFactory INSTANCE = new JacksonSerializerFactory();
    @Override

    public Serializer create() {
        return JacksonSerializer.createParametricType(Message.class);
    }

    public static void main(String[] args) {
        JacksonSerializerFactory serializerFactory = new JacksonSerializerFactory();
        Message message = new Message();

        Serializer serializer = serializerFactory.create();
        byte[] bytes = serializer.serializerRaw(message);
        System.out.println(bytes);
        Object deserialize = serializer.deserialize(bytes);
        System.out.println(deserialize);
    }
}
