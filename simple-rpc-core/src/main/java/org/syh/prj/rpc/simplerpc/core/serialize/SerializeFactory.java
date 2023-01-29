package org.syh.prj.rpc.simplerpc.core.serialize;

public interface SerializeFactory {
    <T> byte[] serialize(T t);
    <T> T deserialize(byte[] data, Class<T> clazz);
}
