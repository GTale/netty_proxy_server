package com.phantom.netty.common.util.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoSerializer implements Serializer {

    private static final ThreadLocal<Kryo> kryoLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setReferences(true);//默认值为true,强调作用
        kryo.setRegistrationRequired(false);//默认值为false,强调作用
        return kryo;
    });


    @Override
    public byte getSerializerAlgorithm() {
        return 2;
    }

    @Override
    public byte[] serialize(Object object) {
        Kryo kryo = kryoLocal.get();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        kryo.writeClassAndObject(output, object);
        output.close();
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        Kryo kryo = kryoLocal.get();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(byteArrayInputStream);// <1>
        input.close();
        return (T) kryo.readClassAndObject(input);//<2>
    }
}
