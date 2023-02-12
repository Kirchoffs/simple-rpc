package org.syh.prj.rpc.simplerpc.core.proxy;

import org.syh.prj.rpc.simplerpc.core.client.RpcReferenceWrapper;
import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.RESP_MAP;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.SEND_QUEUE;

public class CustomizedInvocationHandler implements InvocationHandler {
    private final static Object OBJECT = new Object();

    private RpcReferenceWrapper rpcReferenceWrapper;

    public CustomizedInvocationHandler(RpcReferenceWrapper rpcReferenceWrapper) {
        this.rpcReferenceWrapper = rpcReferenceWrapper;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcInvocation rpcInvocation = new RpcInvocation();
        rpcInvocation.setArgs(args);
        rpcInvocation.setTargetMethod(method.getName());
        rpcInvocation.setTargetServiceName(rpcReferenceWrapper.getAimClass().getName());
        rpcInvocation.setUuid(UUID.randomUUID().toString());
        rpcInvocation.setAttachments(rpcReferenceWrapper.getAttachments());

        RESP_MAP.put(rpcInvocation.getUuid(), OBJECT);
        SEND_QUEUE.add(rpcInvocation);

        long beginTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - beginTime < 3 * 1000) {
            Object object = RESP_MAP.get(rpcInvocation.getUuid());
            if (object instanceof RpcInvocation) {
                return ((RpcInvocation) object).getResponse();
            }
        }

        throw new TimeoutException("request timeout!");
    }
}
