package org.syh.prj.rpc.simplerpc.core.filter.server;

import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcInvocation;
import org.syh.prj.rpc.simplerpc.core.common.utils.CommonUtils;
import org.syh.prj.rpc.simplerpc.core.filter.ServerFilter;
import org.syh.prj.rpc.simplerpc.core.server.ServiceWrapper;

import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonServerCache.PROVIDER_SERVICE_WRAPPER_MAP;

public class ServerTokenFilterImpl implements ServerFilter {
    @Override
    public void doFilter(RpcInvocation rpcInvocation) {
        String token = String.valueOf(rpcInvocation.getAttachments().get("serviceToken"));
        ServiceWrapper serviceWrapper = PROVIDER_SERVICE_WRAPPER_MAP.get(rpcInvocation.getTargetServiceName());
        String expectedToken = serviceWrapper.getServiceToken();

        if (CommonUtils.isEmpty(expectedToken)) {
            return;
        }

        if (expectedToken.equals(token)) {
            return;
        }

        throw new RuntimeException("token is not valid");
    }
}
