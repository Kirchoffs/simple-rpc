package org.syh.prj.rpc.simplerpc.core.serialize.jackson;

import com.caucho.hessian.io.Hessian2Input;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.syh.prj.rpc.simplerpc.core.serialize.SerializeFactory;

import java.io.ByteArrayInputStream;

public class JacksonSerializeFactory implements SerializeFactory {
    public static ThreadLocal<ObjectMapper> objectMapperFactory = ThreadLocal.withInitial(() -> new ObjectMapper());

    @Override
    public <T> byte[] serialize(T t) {
        byte[] data = null;

        try {
            data = objectMapperFactory.get().writeValueAsBytes(t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return  data;
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        if (data == null) {
            return null;
        }

        Object result = null;

        try {
            result = objectMapperFactory.get().readValue(data, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return (T) result;
    }
}
