package org.syh.prj.rpc.simplerpc.core.filter.client;

import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcInvocation;
import org.syh.prj.rpc.simplerpc.core.common.utils.ChannelFutureWrapper;
import org.syh.prj.rpc.simplerpc.core.common.utils.CommonUtils;
import org.syh.prj.rpc.simplerpc.core.filter.ClientFilter;

import java.util.Iterator;
import java.util.List;

public class DirectInvokeFilterImpl implements ClientFilter {
    @Override
    public void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation) {
        String url = (String) rpcInvocation.getAttachments().get("url");
        if (CommonUtils.isEmpty(url)) {
            return;
        }

        Iterator<ChannelFutureWrapper> channelFutureWrapperIterator = src.iterator();
        while (channelFutureWrapperIterator.hasNext()) {
            ChannelFutureWrapper channelFutureWrapper = channelFutureWrapperIterator.next();
            if (!(String.format("%s:%d", channelFutureWrapper.getHost(), channelFutureWrapper.getPort())).equals(url)) {
                channelFutureWrapperIterator.remove();
            }
        }

        if (CommonUtils.isEmptyList(src)) {
            throw new RuntimeException("no matched provider for url " + url);
        }
    }
}
