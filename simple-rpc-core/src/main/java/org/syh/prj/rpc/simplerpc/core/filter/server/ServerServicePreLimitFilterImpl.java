package org.syh.prj.rpc.simplerpc.core.filter.server;

import java.util.concurrent.Semaphore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.syh.prj.rpc.simplerpc.core.common.annotations.SPI;
import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcInvocation;
import org.syh.prj.rpc.simplerpc.core.common.exception.MaxServiceLimitRequestException;
import org.syh.prj.rpc.simplerpc.core.filter.ServerFilter;
import org.syh.prj.rpc.simplerpc.core.server.MaxConnectionLimitHandler;
import org.syh.prj.rpc.simplerpc.core.server.ServerServiceSemaphoreWrapper;

import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonServerCache.SERVER_SERVICE_SEMAPHORE_MAP;

@SPI("pre")
public class ServerServicePreLimitFilterImpl implements ServerFilter {
    private final Logger logger = LogManager.getLogger(MaxConnectionLimitHandler.class);

    @Override
    public void doFilter(RpcInvocation rpcInvocation) {
        String serviceName = rpcInvocation.getTargetServiceName();
        ServerServiceSemaphoreWrapper serverServiceSemaphoreWrapper = SERVER_SERVICE_SEMAPHORE_MAP.get(serviceName);
        Semaphore semaphore = serverServiceSemaphoreWrapper.getSemaphore();
        boolean tryResult = semaphore.tryAcquire();
        if (!tryResult) {
            logger.warn("Semaphore failed");
            logger.error(
                "[ServerServiceBeforeLimitFilterImpl] {}'s max request is {}, reject now",
                rpcInvocation.getTargetServiceName(),
                serverServiceSemaphoreWrapper.getMaxNums()
            );
            MaxServiceLimitRequestException rpcException = new MaxServiceLimitRequestException(rpcInvocation);
            rpcInvocation.setException(rpcException);
            return;
        }
        logger.info("Semaphore acquired");
    }
}
