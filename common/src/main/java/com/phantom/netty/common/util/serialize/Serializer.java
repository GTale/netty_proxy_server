package com.phantom.netty.common.util.serialize;

/**
 * @author: phantom
 * @Date: 2018/11/28 11:24
 * @Description:
 */
public interface Serializer {

//    Serializer DEFAULT = new JSONSerializer();
    Serializer DEFAULT = new KryoSerializer();

    byte getSerializerAlgorithm();

    byte[] serialize(Object object);

    <T> T deserialize(Class<T> clazz, byte[] bytes);
}
