package org.syh.prj.rpc.simplerpc.core.proxy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.syh.prj.rpc.simplerpc.core.client.Client;
import org.syh.prj.rpc.simplerpc.core.client.RpcReferenceWrapper;
import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.RESP_MAP;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.SEND_QUEUE;
import static org.syh.prj.rpc.simplerpc.core.common.constants.RpcConstants.DEFAULT_TIMEOUT;

public class CustomizedInvocationHandler implements InvocationHandler {
    private final Logger logger = LogManager.getLogger(CustomizedInvocationHandler.class);

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
        for (int i = 0; i < rpcReferenceWrapper.getRetry() + 1; i++) {
            SEND_QUEUE.add(rpcInvocation);

            long beginTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - beginTime < DEFAULT_TIMEOUT) {
                Object object = RESP_MAP.get(rpcInvocation.getUuid());

                if (object instanceof RpcInvocation) {
                    RESP_MAP.remove(rpcInvocation.getUuid());
                    RpcInvocation rpcInvocationResp = (RpcInvocation) object;
                    if (rpcInvocationResp.getException() != null) {
                        rpcInvocationResp.getException().printStackTrace();
                    }

                    return rpcInvocationResp.getResponse();
                }
            }
        }
        RESP_MAP.remove(rpcInvocation.getUuid());

        throw new TimeoutException(String.format("request timeout! requested %d time(s)", rpcReferenceWrapper.getRetry() + 1));
    }
}
