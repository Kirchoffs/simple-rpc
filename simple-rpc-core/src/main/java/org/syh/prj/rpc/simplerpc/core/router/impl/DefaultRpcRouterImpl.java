package org.syh.prj.rpc.simplerpc.core.router.impl;

import org.syh.prj.rpc.simplerpc.core.common.utils.ChannelFutureWrapper;
import org.syh.prj.rpc.simplerpc.core.registry.URL;
import org.syh.prj.rpc.simplerpc.core.router.Selector;
import org.syh.prj.rpc.simplerpc.core.router.SimpleRpcRouter;

import java.util.List;

import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.CHANNEL_FUTURE_POLLING_REF;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.CONNECT_MAP;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.SERVICE_ROUTER_MAP;

public class DefaultRpcRouterImpl implements SimpleRpcRouter {
    @Override
    public void refreshRouterArr(Selector selector) {
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(selector.getProviderServiceName());
        ChannelFutureWrapper[] finalChannelFutureWrappers = new ChannelFutureWrapper[channelFutureWrappers.size()];
        for (int j = 0; j < channelFutureWrappers.size(); j++) {
            finalChannelFutureWrappers[j] = channelFutureWrappers.get(j);
        }
        SERVICE_ROUTER_MAP.put(selector.getProviderServiceName(), finalChannelFutureWrappers);
    }

    @Override
    public ChannelFutureWrapper select(Selector selector) {
        return CHANNEL_FUTURE_POLLING_REF.getChannelFutureWrapper(selector.getProviderServiceName());
    }
}
