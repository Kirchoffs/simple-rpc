package org.syh.prj.rpc.simplerpc.core.router.impl;

import org.syh.prj.rpc.simplerpc.core.common.utils.ChannelFutureWrapper;
import org.syh.prj.rpc.simplerpc.core.router.Selector;
import org.syh.prj.rpc.simplerpc.core.router.SimpleRpcRouter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.CHANNEL_FUTURE_POLLING_REF;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.CONNECT_MAP;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.SERVICE_ROUTER_MAP;

public class WeightedRpcRouterImpl implements SimpleRpcRouter {
    @Override
    public void refreshRouterArr(Selector selector) {
        List<ChannelFutureWrapper> channelFutureWrappers = CONNECT_MAP.get(selector.getProviderServiceName());
        List<Integer> weightArr = createWeightArr(channelFutureWrappers);
        List<Integer> finalIndexArr = randomizedList(weightArr);
        ChannelFutureWrapper[] finalChannelFutureWrappers = new ChannelFutureWrapper[finalIndexArr.size()];
        for (int j = 0; j < finalIndexArr.size(); j++) {
            finalChannelFutureWrappers[j] = channelFutureWrappers.get(finalIndexArr.get(j));
        }
        SERVICE_ROUTER_MAP.put(selector.getProviderServiceName(), finalChannelFutureWrappers);
    }

    @Override
    public ChannelFutureWrapper select(Selector selector) {
        return CHANNEL_FUTURE_POLLING_REF.getChannelFutureWrapper(selector.getProviderServiceName());
    }

    public static List<Integer> createWeightArr(List<ChannelFutureWrapper> channelFutureWrappers) {
        List<Integer> weightArr = new ArrayList<>();
        for (int k = 0; k < channelFutureWrappers.size(); k++) {
            Integer weight = channelFutureWrappers.get(k).getWeight();
            int c = weight / 100;
            for (int i = 0; i < c; i++) {
                weightArr.add(k);
            }
        }
        return weightArr;
    }

    public static List<Integer> randomizedList(List<Integer> list) {
        int[] idx = createRandomIndex(list.size());
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            res.add(list.get(idx[i]));
        }
        return res;
    }

    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    public static int[] createRandomIndex(int len) {
        int[] arr = new int[len];
        for (int i = 0; i < len; i++) {
            arr[i] = i;
        }

        Random random = new Random();
        for (int i = 0; i < arr.length; i++) {
            int last = random.nextInt(arr.length - i);
            swap(arr, last, arr.length - 1 - i);
        }

        return arr;
    }
}
