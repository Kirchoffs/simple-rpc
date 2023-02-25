package org.syh.prj.rpc.simplerpc.core.common.cache;

import org.syh.prj.rpc.simplerpc.core.common.config.ServerConfig;
import org.syh.prj.rpc.simplerpc.core.dispatcher.ServerChannelDispatcher;
import org.syh.prj.rpc.simplerpc.core.filter.server.ServerPostFilterChain;
import org.syh.prj.rpc.simplerpc.core.filter.server.ServerPreFilterChain;
import org.syh.prj.rpc.simplerpc.core.registry.URL;
import org.syh.prj.rpc.simplerpc.core.serialize.SerializeFactory;
import org.syh.prj.rpc.simplerpc.core.server.ServerServiceSemaphoreWrapper;
import org.syh.prj.rpc.simplerpc.core.server.ServiceWrapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CommonServerCache {
    // {key: service interface name, value: service object}
    public static final Map<String, Object> PROVIDER_CLASS_MAP = new HashMap<>();

    // List of service url related info
    public static final Set<URL> PROVIDER_URL_SET = new HashSet<>();
    public static SerializeFactory SERVER_SERIALIZE_FACTORY;

    // {key: service interface name, value: service wrapper object}
    public static final Map<String, ServiceWrapper> PROVIDER_SERVICE_WRAPPER_MAP = new ConcurrentHashMap<>();
    public static ServerPreFilterChain SERVER_PRE_FILTER_CHAIN;
    public static ServerPostFilterChain SERVER_POST_FILTER_CHAIN;
    public static ServerConfig SERVER_CONFIG;
    public static ServerChannelDispatcher SERVER_CHANNEL_DISPATCHER = new ServerChannelDispatcher();
    public static final Map<String, ServerServiceSemaphoreWrapper> SERVER_SERVICE_SEMAPHORE_MAP = new ConcurrentHashMap<>(64);
}
