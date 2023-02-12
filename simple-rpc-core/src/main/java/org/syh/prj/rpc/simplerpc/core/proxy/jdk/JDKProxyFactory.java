package org.syh.prj.rpc.simplerpc.core.proxy.jdk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.syh.prj.rpc.simplerpc.core.client.Client;
import org.syh.prj.rpc.simplerpc.core.client.RpcReferenceWrapper;
import org.syh.prj.rpc.simplerpc.core.proxy.CustomizedInvocationHandler;
import org.syh.prj.rpc.simplerpc.core.proxy.ProxyFactory;

import java.lang.reflect.Proxy;

public class JDKProxyFactory implements ProxyFactory {
    private final Logger logger = LogManager.getLogger(Client.class);

    @Override
    public <T> T getProxy(RpcReferenceWrapper<T> rpcReferenceWrapper) {
        logger.info("JDK proxy work in progress");

        return (T) Proxy.newProxyInstance(
            rpcReferenceWrapper.getAimClass().getClassLoader(),
            new Class[] { rpcReferenceWrapper.getAimClass() },
            new CustomizedInvocationHandler(rpcReferenceWrapper)
        );
    }
}
