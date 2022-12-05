package org.syh.prj.rpc.simplerpc.core.proxy.jdk;

import org.syh.prj.rpc.simplerpc.core.proxy.ProxyFactory;

import java.lang.reflect.Proxy;

public class JDKProxyFactory implements ProxyFactory {
    @Override
    public <T> T getProxy(final Class clazz) {
        return (T) Proxy.newProxyInstance(
            clazz.getClassLoader(),
            new Class[] { clazz },
            new JDKClientInvocationHandler(clazz)
        );
    }
}
