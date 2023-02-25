package org.syh.prj.rpc.simplerpc.core.common.exception;

import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcInvocation;

public class SimpleRpcException extends RuntimeException {
    private RpcInvocation rpcInvocation;

    public SimpleRpcException(RpcInvocation rpcInvocation) {
        this.rpcInvocation = rpcInvocation;
    }

    public RpcInvocation getRpcInvocation() {
        return rpcInvocation;
    }

    public void setRpcInvocation(RpcInvocation rpcInvocation) {
        this.rpcInvocation = rpcInvocation;
    }
}
