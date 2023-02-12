package org.syh.prj.rpc.simplerpc.core.filter;

import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcInvocation;
import org.syh.prj.rpc.simplerpc.core.common.utils.ChannelFutureWrapper;

import java.util.List;

public interface ClientFilter extends Filter {
    void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation);
}
