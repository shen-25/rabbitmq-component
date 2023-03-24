package com.zengshen.rabbit.common.serializer;

public interface Serializer {
    byte[] serializerRaw(Object data);

    String serialize(Object data);

    <T> T deserialize(String content);

    <T> T deserialize( byte[] content);
}
