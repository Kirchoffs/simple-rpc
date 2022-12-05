package org.syh.prj.rpc.simplerpc.core.proxy;

public interface ProxyFactory {
    <T> T getProxy(final Class clazz) throws Throwable;
}
