package org.syh.prj.rpc.simplerpc.core.registry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.syh.prj.rpc.simplerpc.core.registry.zookeeper.ProviderNodeInfo;

public class URLTest {
    @Test
    public void testBuildURLFromUrlStr() {
        ProviderNodeInfo providerNodeInfo = URL.buildURLFromUrlStr(
            "/simple-rpc/org.syh.prj.rpc.simplerpc.interfaces.DataService/provider/192.168.43.227:9092"
        );
        System.out.println(providerNodeInfo);
        Assertions.assertEquals("org.syh.prj.rpc.simplerpc.interfaces.DataService", providerNodeInfo.getServiceName());
        Assertions.assertEquals("192.168.43.227:9092", providerNodeInfo.getAddress());
    }
}
