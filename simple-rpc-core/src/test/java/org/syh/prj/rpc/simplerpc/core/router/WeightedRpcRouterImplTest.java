package org.syh.prj.rpc.simplerpc.core.router;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.syh.prj.rpc.simplerpc.core.common.utils.ChannelFutureWrapper;
import org.syh.prj.rpc.simplerpc.core.router.impl.WeightedRpcRouterImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WeightedRpcRouterImplTest {
    @Test
    public void testCreateRandomIndex() {
        int len = 10;
        int[] randomizedIndex = WeightedRpcRouterImpl.createRandomIndex(len);

        boolean[] checked = new boolean[len];
        for (int e: randomizedIndex) {
            if (e >= len) {
                Assertions.fail();
            }
            checked[e] = true;
        }

        for (boolean flag: checked) {
            Assertions.assertTrue(flag);
        }
    }

    @Test
    public void testCreateWeightArr() {
        int[] weight = {100, 200, 300, 600};

        List<ChannelFutureWrapper> channelFutureWrappers = new ArrayList<>();
        for (int i = 0; i < weight.length; i++) {
            channelFutureWrappers.add(new ChannelFutureWrapper(null, null, weight[i]));
        }

        List<Integer> weightArr = WeightedRpcRouterImpl.createWeightArr(channelFutureWrappers);

        List<Integer> expectedWeightArr = Arrays.asList(0, 1, 1, 2, 2, 2, 3, 3, 3, 3, 3, 3);
        Assertions.assertEquals(expectedWeightArr.size(), weightArr.size());
        for (int i = 0; i < weightArr.size(); i++) {
            Assertions.assertEquals(expectedWeightArr.get(i), weightArr.get(i));
        }
    }
}
