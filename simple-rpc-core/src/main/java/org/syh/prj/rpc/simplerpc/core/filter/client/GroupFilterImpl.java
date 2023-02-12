package org.syh.prj.rpc.simplerpc.core.filter.client;

import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcInvocation;
import org.syh.prj.rpc.simplerpc.core.common.utils.ChannelFutureWrapper;
import org.syh.prj.rpc.simplerpc.core.common.utils.CommonUtils;
import org.syh.prj.rpc.simplerpc.core.filter.ClientFilter;

import java.util.List;

public class GroupFilterImpl implements ClientFilter {
    @Override
    public void doFilter(List<ChannelFutureWrapper> src, RpcInvocation rpcInvocation) {
        String group = String.valueOf(rpcInvocation.getAttachments().get("group"));

        for (ChannelFutureWrapper channelFutureWrapper: src) {
            if (!channelFutureWrapper.getGroup().equals(group)) {
                src.remove(channelFutureWrapper);
            }
        }

        if (CommonUtils.isEmptyList(src)) {
            throw new RuntimeException("no matched provider for group " + group);
        }
    }
}
