package org.syh.prj.rpc.simplerpc.core.filter.server;

import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcInvocation;
import org.syh.prj.rpc.simplerpc.core.filter.ServerFilter;

import java.util.ArrayList;
import java.util.List;

public class ServerFilterChain {
    private List<ServerFilter> serverFilterList = new ArrayList<>();

    public void addServerFilter(ServerFilter serverFilter) {
        serverFilterList.add(serverFilter);
    }

    public void doFilter(RpcInvocation rpcInvocation) {
        for (ServerFilter serverFilter: serverFilterList) {
            serverFilter.doFilter(rpcInvocation);
        }
    }
}
