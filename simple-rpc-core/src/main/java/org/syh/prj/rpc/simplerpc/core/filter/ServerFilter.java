package org.syh.prj.rpc.simplerpc.core.filter;

import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcInvocation;

public interface ServerFilter extends Filter {
    void doFilter(RpcInvocation rpcInvocation);
}
