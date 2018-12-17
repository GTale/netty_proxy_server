package com.phantom.netty.common.util.serialize;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author: phantom
 * @Date: 2018/11/28 11:34
 * @Description:
 */
@Getter
public enum SerializerAlgorithm {
    /**
     * json 序列化标识
     */
    KRYO((byte)2, KryoSerializer.class);

    private Byte value;
    private Class<? extends Serializer> clazz;


    SerializerAlgorithm(Byte value, Class<? extends Serializer> clazz) {
        this.value = value;
        this.clazz = clazz;
    }


    public static SerializerAlgorithm getAlgorithm(Byte value) {
        return Arrays.stream(SerializerAlgorithm.values()).filter(x -> Objects.equals(x.getValue(), value)).findFirst().orElse(null);
    }
}
