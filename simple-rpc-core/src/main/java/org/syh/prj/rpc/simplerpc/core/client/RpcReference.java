package org.syh.prj.rpc.simplerpc.core.client;

import org.syh.prj.rpc.simplerpc.core.proxy.ProxyFactory;

public class RpcReference {
    public ProxyFactory proxyFactory;

    public RpcReference(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    public <T> T get(Class<T> tClass) throws Throwable {
        return proxyFactory.getProxy(tClass);
    }
}
