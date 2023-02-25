package org.syh.prj.rpc.simplerpc.core.common.exception;

import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcInvocation;

public class MaxServiceLimitRequestException extends SimpleRpcException {
    public MaxServiceLimitRequestException(RpcInvocation rpcInvocation) {
        super(rpcInvocation);
    }
}
