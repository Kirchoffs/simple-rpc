package org.syh.prj.rpc.simplerpc.core.common.utils;

import org.syh.prj.rpc.simplerpc.core.router.Selector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class ChannelFuturePollingRef {
    private Map<String, AtomicLong> referenceTimesMap = new HashMap<>();

    public ChannelFutureWrapper getChannelFutureWrapper(Selector selector){
        String serviceName = selector.getProviderServiceName();
        List<ChannelFutureWrapper> arr = selector.getChannelFutureWrappers();
        if (!referenceTimesMap.containsKey(serviceName)) {
            referenceTimesMap.put(serviceName, new AtomicLong(0));
        }
        long choice = referenceTimesMap.get(serviceName).getAndIncrement();
        int index = (int) (choice % arr.size());
        return arr.get(index);
    }
}
