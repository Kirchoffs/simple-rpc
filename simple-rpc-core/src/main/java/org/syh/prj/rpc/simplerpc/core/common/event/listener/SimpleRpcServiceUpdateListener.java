package org.syh.prj.rpc.simplerpc.core.common.event.listener;

import io.netty.channel.ChannelFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.syh.prj.rpc.simplerpc.core.client.ConnectionHandler;
import org.syh.prj.rpc.simplerpc.core.common.event.SimpleRpcListener;
import org.syh.prj.rpc.simplerpc.core.common.event.event.SimpleRpcServiceUpdateEvent;
import org.syh.prj.rpc.simplerpc.core.common.event.data.URLChangeWrapper;
import org.syh.prj.rpc.simplerpc.core.common.utils.ChannelFutureWrapper;
import org.syh.prj.rpc.simplerpc.core.common.utils.CommonUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.CONNECT_MAP;

public class SimpleRpcServiceUpdateListener implements SimpleRpcListener<SimpleRpcServiceUpdateEvent> {
    private Logger logger = LogManager.getLogger(SimpleRpcServiceUpdateListener.class);

    @Override
    public void callBack(Object t) {
        URLChangeWrapper urlChangeWrapper = (URLChangeWrapper) t;

        // Servers known by client
        List<ChannelFutureWrapper> curChannelFutureWrappers = CONNECT_MAP.get(urlChangeWrapper.getServiceName());

        if (CommonUtils.isEmptyList(curChannelFutureWrappers)) {
            logger.error("[SimpleRpcServiceUpdateListener] channelFutureWrappers is empty");
        } else {
            List<String> updatedProviderUrls = urlChangeWrapper.getProviderUrls();

            Set<String> finalUrl = new HashSet<>();
            List<ChannelFutureWrapper> finalChannelFutureWrappers = new ArrayList<>();

            // Add previous servers, some servers may be dropped.
            for (ChannelFutureWrapper curChannelFutureWrapper: curChannelFutureWrappers) {
                String curProviderUrl = curChannelFutureWrapper.getHost() + ":" + curChannelFutureWrapper.getPort();
                if (updatedProviderUrls.contains(curProviderUrl)) {
                    finalChannelFutureWrappers.add(curChannelFutureWrapper);
                    finalUrl.add(curProviderUrl);
                }
            }

            // Add new servers
            List<ChannelFutureWrapper> newChannelFutureWrappers = new ArrayList<>();
            for (String newProviderUrl: updatedProviderUrls) {
                if (!finalUrl.contains(newProviderUrl)) {
                    String host = newProviderUrl.split(":")[0];
                    Integer port = Integer.valueOf(newProviderUrl.split(":")[1]);

                    ChannelFutureWrapper newChannelFutureWrapper = new ChannelFutureWrapper();
                    newChannelFutureWrapper.setPort(port);
                    newChannelFutureWrapper.setHost(host);

                    ChannelFuture newChannelFuture;
                    try {
                        newChannelFuture = ConnectionHandler.createChannelFuture(host, port);
                        newChannelFutureWrapper.setChannelFuture(newChannelFuture);

                        newChannelFutureWrappers.add(newChannelFutureWrapper);
                        finalUrl.add(newProviderUrl);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            finalChannelFutureWrappers.addAll(newChannelFutureWrappers);
            CONNECT_MAP.put(urlChangeWrapper.getServiceName(), finalChannelFutureWrappers);
        }
    }
}
