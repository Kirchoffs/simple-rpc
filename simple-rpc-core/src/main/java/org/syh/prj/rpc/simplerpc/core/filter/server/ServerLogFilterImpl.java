package org.syh.prj.rpc.simplerpc.core.filter.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcInvocation;
import org.syh.prj.rpc.simplerpc.core.filter.ServerFilter;

public class ServerLogFilterImpl implements ServerFilter {
    private Logger logger = LogManager.getLogger(ServerLogFilterImpl.class);

    @Override
    public void doFilter(RpcInvocation rpcInvocation) {
        logger.info(
            "{} invokes {}#{}",
            rpcInvocation.getAttachments().get("c_app_name"),
            rpcInvocation.getTargetServiceName(),
            rpcInvocation.getTargetMethod()
        );
    }
}
