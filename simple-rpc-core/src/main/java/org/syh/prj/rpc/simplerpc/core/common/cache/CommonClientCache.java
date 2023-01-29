package org.syh.prj.rpc.simplerpc.core.common.cache;

import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcInvocation;
import org.syh.prj.rpc.simplerpc.core.common.utils.ChannelFuturePollingRef;
import org.syh.prj.rpc.simplerpc.core.common.utils.ChannelFutureWrapper;
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

    // Client's subscribe list in local
    public static List<URL> SUBSCRIBE_SERVICE_LIST = new ArrayList<>();

    public static Set<String> SERVER_ADDRESS = new HashSet<>();

    public static Map<String, List<ChannelFutureWrapper>> CONNECT_MAP = new ConcurrentHashMap<>();

    public static Map<String, ChannelFutureWrapper[]> SERVICE_ROUTER_MAP = new ConcurrentHashMap<>();

    public static ChannelFuturePollingRef CHANNEL_FUTURE_POLLING_REF = new ChannelFuturePollingRef();

    public static Map<String, Map<String,String>> URL_MAP = new ConcurrentHashMap<>();

    public static SimpleRpcRouter RPC_ROUTER;

    public static SerializeFactory CLIENT_SERIALIZE_FACTORY;
}
