package org.syh.prj.rpc.simplerpc.core.dispatcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.syh.prj.rpc.simplerpc.core.common.exception.SimpleRpcException;
import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcInvocation;
import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcProtocol;
import org.syh.prj.rpc.simplerpc.core.server.ServerChannelReadData;

import java.lang.reflect.Method;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonServerCache.PROVIDER_CLASS_MAP;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonServerCache.SERVER_PRE_FILTER_CHAIN;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonServerCache.SERVER_POST_FILTER_CHAIN;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonServerCache.SERVER_SERIALIZE_FACTORY;

public class ServerChannelDispatcher {
    private BlockingQueue<ServerChannelReadData> RPC_DATA_QUEUE;

    private ExecutorService executorService;

    private final Logger logger = LogManager.getLogger(ServerChannelDispatcher.class);

    public ServerChannelDispatcher() {}

    public void init(int queueSize, int bizThreadNums) {
        RPC_DATA_QUEUE = new ArrayBlockingQueue<>(queueSize);
        executorService = new ThreadPoolExecutor(
            bizThreadNums,
            bizThreadNums,
        0L,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(512)
        );
    }

    public void add(ServerChannelReadData serverChannelReadData) {
        RPC_DATA_QUEUE.add(serverChannelReadData);
    }

    class ServerJobCoreHandler implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    ServerChannelReadData serverChannelReadData = RPC_DATA_QUEUE.take();
                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                logger.info("Start processing");
                                RpcProtocol rpcProtocol = serverChannelReadData.getRpcProtocol();
                                RpcInvocation rpcInvocation =
                                    SERVER_SERIALIZE_FACTORY.deserialize(rpcProtocol.getContent(),
                                    RpcInvocation.class
                                );
                                try {
                                    logger.info("Start pre filtering");
                                    SERVER_PRE_FILTER_CHAIN.doFilter(rpcInvocation);
                                } catch (Exception e) {
                                    if (e instanceof SimpleRpcException) {
                                        SimpleRpcException rpcException = (SimpleRpcException) e;
                                        RpcInvocation rpcInvocationFromException = rpcException.getRpcInvocation();
                                        byte[] body = SERVER_SERIALIZE_FACTORY.serialize(rpcInvocationFromException);
                                        RpcProtocol rpcProtocolFromException = new RpcProtocol(body);
                                        serverChannelReadData.getChannelHandlerContext().writeAndFlush(rpcProtocolFromException);
                                        return;
                                    }
                                }
                                Object aimObject = PROVIDER_CLASS_MAP.get(rpcInvocation.getTargetServiceName());
                                Method[] methods = aimObject.getClass().getDeclaredMethods();
                                Object result = null;
                                for (Method method : methods) {
                                    if (method.getName().equals(rpcInvocation.getTargetMethod())) {
                                        if (method.getReturnType().equals(Void.TYPE)) {
                                            try {
                                                method.invoke(aimObject, rpcInvocation.getArgs());
                                            } catch (Exception e) {
                                                rpcInvocation.setException(e);
                                            }
                                        } else {
                                            try {
                                                result = method.invoke(aimObject, rpcInvocation.getArgs());
                                            } catch (Exception e) {
                                                rpcInvocation.setException(e);
                                            }
                                        }
                                        break;
                                    }
                                }
                                rpcInvocation.setResponse(result);
                                logger.info("result: {}", result);
                                logger.info("Start post filtering");
                                SERVER_POST_FILTER_CHAIN.doFilter(rpcInvocation);
                                RpcProtocol respRpcProtocol = new RpcProtocol(
                                    SERVER_SERIALIZE_FACTORY.serialize(rpcInvocation)
                                );
                                serverChannelReadData.getChannelHandlerContext().writeAndFlush(respRpcProtocol);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void startDataConsume() {
        Thread thread = new Thread(new ServerJobCoreHandler());
        thread.start();
    }
}
