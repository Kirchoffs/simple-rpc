package org.syh.prj.rpc.simplerpc.core.filter.client;

import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcInvocation;
import org.syh.prj.rpc.simplerpc.core.common.utils.ChannelFutureWrapper;
import org.syh.prj.rpc.simplerpc.core.filter.ClientFilter;

import java.util.ArrayList;
import java.util.List;

public class ClientFilterChain {
    private static List<ClientFilter> clientFilterList = new ArrayList<>();

    public void addClientFilter(ClientFilter iClientFilter) {
        clientFilterList.add(iClientFilter);
    }

    public void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation) {
        for (ClientFilter clientFilter : clientFilterList) {
            clientFilter.doFilter(src, rpcInvocation);
        }
    }
}
