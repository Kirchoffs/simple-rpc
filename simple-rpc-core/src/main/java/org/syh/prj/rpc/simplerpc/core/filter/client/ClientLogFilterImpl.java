package org.syh.prj.rpc.simplerpc.core.filter.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcInvocation;
import org.syh.prj.rpc.simplerpc.core.common.utils.ChannelFutureWrapper;
import org.syh.prj.rpc.simplerpc.core.filter.ClientFilter;
import org.syh.prj.rpc.simplerpc.core.filter.server.ServerLogFilterImpl;

import java.util.List;

import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.CLIENT_CONFIG;

public class ClientLogFilterImpl implements ClientFilter {
    private Logger logger = LogManager.getLogger(ServerLogFilterImpl.class);

    @Override
    public void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation) {
        rpcInvocation.getAttachments().put("c_app_name", CLIENT_CONFIG.getApplicationName());
        logger.info(
            "{} invokes {}",
            rpcInvocation.getAttachments().get("c_app_name"),
            rpcInvocation.getTargetServiceName()
        );
    }
}
