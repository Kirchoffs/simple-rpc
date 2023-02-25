package org.syh.prj.rpc.simplerpc.core.filter.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.syh.prj.rpc.simplerpc.core.common.annotations.SPI;
import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcInvocation;
import org.syh.prj.rpc.simplerpc.core.filter.ServerFilter;
import org.syh.prj.rpc.simplerpc.core.server.ServerServiceSemaphoreWrapper;

import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonServerCache.SERVER_SERVICE_SEMAPHORE_MAP;

@SPI("post")
public class ServerServicePostLimitFilterImpl implements ServerFilter {
    private final Logger logger = LogManager.getLogger(ServerServicePostLimitFilterImpl.class);

    @Override
    public void doFilter(RpcInvocation rpcInvocation) {
        String serviceName = rpcInvocation.getTargetServiceName();
        ServerServiceSemaphoreWrapper serverServiceSemaphoreWrapper = SERVER_SERVICE_SEMAPHORE_MAP.get(serviceName);
        serverServiceSemaphoreWrapper.getSemaphore().release();
        logger.info("Semaphore released");
    }
}
