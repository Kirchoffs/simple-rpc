package org.syh.prj.rpc.simplerpc.core.proxy;

import org.syh.prj.rpc.simplerpc.core.client.RpcReferenceWrapper;

public interface ProxyFactory {
    <T> T getProxy(RpcReferenceWrapper<T> rpcReferenceWrapper) throws Throwable;
}
