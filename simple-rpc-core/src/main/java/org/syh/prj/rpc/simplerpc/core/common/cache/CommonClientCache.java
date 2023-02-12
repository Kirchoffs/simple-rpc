package org.syh.prj.rpc.simplerpc.core.common.cache;

import org.syh.prj.rpc.simplerpc.core.common.config.ClientConfig;
import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcInvocation;
import org.syh.prj.rpc.simplerpc.core.common.utils.ChannelFuturePollingRef;
import org.syh.prj.rpc.simplerpc.core.common.utils.ChannelFutureWrapper;
import org.syh.prj.rpc.simplerpc.core.filter.client.ClientFilterChain;
import org.syh.prj.rpc.simplerpc.core.registry.URL;
import org.syh.prj.rpc.simplerpc.core.router.SimpleRpcRouter;
import org.syh.prj.rpc.simplerpc.core.serialize.SerializeFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class CommonClientCache {
    public static BlockingQueue<RpcInvocation> SEND_QUEUE = new ArrayBlockingQueue(100);
    public static Map<String,Object> RESP_MAP = new ConcurrentHashMap<>();

    // When the client subscribes a new service, the service info will go in SUBSCRIBE_SERVICE_LIST
    public static List<URL> SUBSCRIBE_SERVICE_LIST = new ArrayList<>();

    // All service's addresses
    public static Set<String> SERVER_ADDRESS = new HashSet<>();

    // {key: service name, value: list of ChannelFuture wrappers}
    public static Map<String, List<ChannelFutureWrapper>> CONNECT_MAP = new ConcurrentHashMap<>();

    // A list of ChannelFuture wrappers, utilized with CHANNEL_FUTURE_POLLING_REF to get the next FutureWrapper
    public static Map<String, ChannelFutureWrapper[]> SERVICE_ROUTER_MAP = new ConcurrentHashMap<>();

    // An object which is used to determine the next FutureWrapper with SERVICE_ROUTER_MAP
    public static ChannelFuturePollingRef CHANNEL_FUTURE_POLLING_REF = new ChannelFuturePollingRef();

    // When the client subscribes a new service, all providers for the service will go in URL_MAP
    // {key: service name, value: {key: id and port address, value: provider info}}
    public static Map<String, Map<String, String>> URL_MAP = new ConcurrentHashMap<>();

    public static SimpleRpcRouter RPC_ROUTER;
    public static SerializeFactory CLIENT_SERIALIZE_FACTORY;
    public static ClientConfig CLIENT_CONFIG;
    public static ClientFilterChain CLIENT_FILTER_CHAIN;
}
