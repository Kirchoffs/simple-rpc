package org.syh.prj.rpc.simplerpc.core.proxy.javassit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.syh.prj.rpc.simplerpc.core.client.Client;
import org.syh.prj.rpc.simplerpc.core.client.RpcReferenceWrapper;
import org.syh.prj.rpc.simplerpc.core.proxy.CustomizedInvocationHandler;
import org.syh.prj.rpc.simplerpc.core.proxy.ProxyFactory;

public class JavassitProxyFactory implements ProxyFactory {
    private final Logger logger = LogManager.getLogger(Client.class);

    @Override
    public <T> T getProxy(RpcReferenceWrapper<T> rpcReferenceWrapper) throws Throwable {
        logger.info("Javassist proxy work in progress");

        return (T) ProxyGenerator.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                rpcReferenceWrapper.getAimClass(),
                new CustomizedInvocationHandler(rpcReferenceWrapper)
        );
    }

}
