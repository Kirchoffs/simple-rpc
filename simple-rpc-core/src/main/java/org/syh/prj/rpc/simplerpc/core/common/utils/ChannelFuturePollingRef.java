package org.syh.prj.rpc.simplerpc.core.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.SERVICE_ROUTER_MAP;

public class ChannelFuturePollingRef {
    private Map<String, AtomicLong> referenceTimesMap = new HashMap<>();

    public ChannelFutureWrapper getChannelFutureWrapper(String serviceName){
        ChannelFutureWrapper[] arr = SERVICE_ROUTER_MAP.get(serviceName);
        if (!referenceTimesMap.containsKey(serviceName)) {
            referenceTimesMap.put(serviceName, new AtomicLong(0));
        }
        long choice = referenceTimesMap.get(serviceName).getAndIncrement();
        int index = (int) (choice % arr.length);
        return arr[index];
    }
}
