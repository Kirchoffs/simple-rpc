package org.syh.prj.rpc.simplerpc.core.proxy.javassit;

import org.syh.prj.rpc.simplerpc.core.proxy.CustomizedInvocationHandler;
import org.syh.prj.rpc.simplerpc.core.proxy.ProxyFactory;

public class JavassitProxyFactory implements ProxyFactory {
    @Override
    public <T> T getProxy(Class clazz) throws Throwable {
        return (T) ProxyGenerator.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                clazz,
                new CustomizedInvocationHandler(clazz)
        );
    }

}
