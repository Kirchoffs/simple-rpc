package org.syh.prj.rpc.simplerpc.core.common.event.listener;

import org.syh.prj.rpc.simplerpc.core.common.event.SimpleRpcListener;
import org.syh.prj.rpc.simplerpc.core.common.event.event.SimpleRpcNodeChangeEvent;
import org.syh.prj.rpc.simplerpc.core.common.utils.ChannelFutureWrapper;
import org.syh.prj.rpc.simplerpc.core.registry.URL;
import org.syh.prj.rpc.simplerpc.core.registry.zookeeper.ProviderNodeInfo;
import org.syh.prj.rpc.simplerpc.core.router.Selector;

import java.util.List;

import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.CONNECT_MAP;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.RPC_ROUTER;

public class SimpleRpcNodeChangeListener implements SimpleRpcListener<SimpleRpcNodeChangeEvent> {
    @Override
    public void callBack(Object t) {
        ProviderNodeInfo providerNodeInfo = ((ProviderNodeInfo) t);
        List<ChannelFutureWrapper> channelFutureWrappers =  CONNECT_MAP.get(providerNodeInfo.getServiceName());
        for (ChannelFutureWrapper channelFutureWrapper: channelFutureWrappers) {
            String address = channelFutureWrapper.getHost() + ":" + channelFutureWrapper.getPort();
            if (address.equals(providerNodeInfo.getAddress())) {
                channelFutureWrapper.setWeight(providerNodeInfo.getWeight());
                Selector selector = new Selector.SelectorBuilder().setProviderServiceName(providerNodeInfo.getServiceName()).build();
                RPC_ROUTER.refreshRouterArr(selector);
                break;
            }
        }
    }
}
