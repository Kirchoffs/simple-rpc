package org.syh.prj.rpc.simplerpc.core.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcInvocation;
import org.syh.prj.rpc.simplerpc.core.common.utils.ChannelFutureWrapper;
import org.syh.prj.rpc.simplerpc.core.common.utils.CommonUtils;
import org.syh.prj.rpc.simplerpc.core.registry.URL;
import org.syh.prj.rpc.simplerpc.core.registry.zookeeper.ProviderNodeInfo;
import org.syh.prj.rpc.simplerpc.core.router.Selector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.CLIENT_FILTER_CHAIN;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.CONNECT_MAP;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.RPC_ROUTER;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.SERVER_ADDRESS;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.SERVICE_ROUTER_MAP;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.URL_MAP;

public class ConnectionHandler {
    private static Bootstrap bootstrap;

    public static void setBootstrap(Bootstrap bootstrap) {
        ConnectionHandler.bootstrap = bootstrap;
    }

    public static void connect(String providerServiceName, String providerIp) throws InterruptedException {
        if (bootstrap == null) {
            throw new RuntimeException("bootstrap can not be null");
        }

        if (!providerIp.contains(":")) {
            return;
        }
        String[] providerAddress = providerIp.split(":");
        String ip = providerAddress[0];
        Integer port = Integer.parseInt(providerAddress[1]);

        ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();
        String providerNodeInfoDataStr = URL_MAP.get(providerServiceName).get(providerIp);
        ProviderNodeInfo providerNodeInfo = URL.buildProviderNodeInfoFromDataStr(providerNodeInfoDataStr);
        ChannelFutureWrapper channelFutureWrapper = new ChannelFutureWrapper();
        channelFutureWrapper.setChannelFuture(channelFuture);
        channelFutureWrapper.setHost(ip);
        channelFutureWrapper.setPort(port);
        channelFutureWrapper.setWeight(providerNodeInfo.getWeight());
        channelFutureWrapper.setGroup(providerNodeInfo.getGroup());
        SERVER_ADDRESS.add(providerIp);
        CONNECT_MAP.computeIfAbsent(providerServiceName, param -> new ArrayList<>()).add(channelFutureWrapper);
        Selector selector = new Selector.SelectorBuilder().setProviderServiceName(providerServiceName).build();
        RPC_ROUTER.refreshRouterArr(selector);
    }

    public static ChannelFuture createChannelFuture(String ip, Integer port) throws InterruptedException {
        ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();
        return channelFuture;
    }

    public static void disConnect(String providerServiceName, String providerIp) {
        SERVER_ADDRESS.remove(providerIp);
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(providerServiceName);
        if (CommonUtils.isNotEmptyList(channelFutureWrappers)) {
            Iterator<ChannelFutureWrapper> iterator = channelFutureWrappers.iterator();
            while (iterator.hasNext()) {
                ChannelFutureWrapper channelFutureWrapper = iterator.next();
                if (providerIp.equals(channelFutureWrapper.getHost() + ":" + channelFutureWrapper.getPort())) {
                    iterator.remove();
                }
            }
        }
    }

    public static ChannelFuture getChannelFuture(RpcInvocation rpcInvocation) {
        String providerServiceName = rpcInvocation.getTargetServiceName();
        ChannelFutureWrapper[] channelFutureWrappers = SERVICE_ROUTER_MAP.get(providerServiceName);
        if (channelFutureWrappers == null || channelFutureWrappers.length == 0) {
            throw new RuntimeException("no provider exists for " + providerServiceName);
        }

        List<ChannelFutureWrapper> candidatesChannelFutureWrappers = Arrays.stream(channelFutureWrappers).collect(Collectors.toList());
        CLIENT_FILTER_CHAIN.doFilter(candidatesChannelFutureWrappers, rpcInvocation);
        Selector selector = new Selector.SelectorBuilder()
                .setProviderServiceName(providerServiceName)
                .setChannelFutureWrappers(candidatesChannelFutureWrappers)
                .build();
        ChannelFuture channelFuture = RPC_ROUTER.select(selector).getChannelFuture();
        return channelFuture;
    }
}
